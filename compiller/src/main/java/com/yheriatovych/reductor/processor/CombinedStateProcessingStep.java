package com.yheriatovych.reductor.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.*;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.CombinedState;
import com.yheriatovych.reductor.processor.model.CombinedStateElement;
import com.yheriatovych.reductor.processor.model.StateProperty;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.yheriatovych.reductor.processor.Utils.*;

class CombinedStateProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private static final String REDUCER_SUFFIX = "Reducer";
    private final Env env;

    CombinedStateProcessingStep(Env env) {
        this.env = env;
    }


    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Collections.singleton(CombinedState.class);
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Element element : elementsByAnnotation.values()) {
            TypeElement combinedStateTypeElement = (TypeElement) element;

            try {
                CombinedStateElement combinedStateElement = CombinedStateElement.parseCombinedElement(combinedStateTypeElement);
                if (combinedStateElement == null) continue;

                ClassName stateClassName = emmitCombinedStateImplementation(combinedStateElement);
                emmitCombinedReducer(env, combinedStateElement, stateClassName);
            } catch (ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                env.printError(element, "Internal processor error:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
        return Collections.emptySet();
    }

    private ClassName emmitCombinedStateImplementation(CombinedStateElement combinedStateElement) throws IOException {
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        List<MethodSpec> methodSpecs = new ArrayList<>();
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        for (StateProperty property : combinedStateElement.properties) {
            TypeName returnTypeName = TypeName.get(property.stateType);
            FieldSpec fieldSpec = FieldSpec.builder(returnTypeName, property.name, Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            MethodSpec spec = MethodSpec
                    .methodBuilder(property.name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnTypeName)
                    .addAnnotation(Override.class)
                    .addStatement("return $N", property.name)
                    .build();
            methodSpecs.add(spec);
            fieldSpecs.add(fieldSpec);

            ParameterSpec parameterSpec = ParameterSpec.builder(returnTypeName, property.name).build();
            constructorBuilder.addParameter(parameterSpec);
            constructorBuilder.addStatement("this.$N = $N", fieldSpec, parameterSpec);
        }


        TypeSpec typeSpec = TypeSpec.classBuilder(combinedStateElement.stateTypeElement.getSimpleName().toString() + "Impl")
                .addSuperinterface(TypeName.get(combinedStateElement.stateTypeElement.asType()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(constructorBuilder.build())
                .addMethods(methodSpecs)
                .addFields(fieldSpecs)
                .build();

        JavaFile javaFile = JavaFile.builder(env.getPackageName(combinedStateElement.stateTypeElement), typeSpec)
                .build();
        javaFile.writeTo(env.getFiler());
        return ClassName.get(javaFile.packageName, typeSpec.name);
    }

    public static void emmitCombinedReducer(final Env env, CombinedStateElement combinedStateElement, ClassName stateClassName) throws IOException {
        String stateParam = "state";
        String actionParam = "action";

        TypeName reducerActionType = combinedStateElement.getCombinedReducerActionType();

        String packageName = env.getPackageName(combinedStateElement.stateTypeElement);
        String combinedStateClassName = combinedStateElement.stateTypeElement.getSimpleName().toString();
        ClassName combinedReducerClassName = ClassName.get(packageName, combinedStateClassName + REDUCER_SUFFIX);

        TypeName combinedReducerReturnTypeName = TypeName.get(combinedStateElement.stateTypeElement.asType());
        List<StateProperty> properties = combinedStateElement.properties;

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(combinedReducerClassName)
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(Reducer.class),
                        combinedReducerReturnTypeName))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        List<FieldSpec> reducerFields = new ArrayList<>();
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);

        for (StateProperty property : properties) {
            String reducerFieldName = property.name + REDUCER_SUFFIX;
            TypeName subReducerType = property.getReducerInterfaceTypeName();
            FieldSpec subReducerField = FieldSpec.builder(subReducerType, reducerFieldName, Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            reducerFields.add(subReducerField);

            constructorBuilder.addParameter(subReducerType, reducerFieldName)
                    .addStatement("this.$N = $N", reducerFieldName, reducerFieldName);
        }

        CodeBlock dispatchingBlockBuilder = reduce(properties, CodeBlock.builder(), new Utils.Func2<CodeBlock.Builder, StateProperty, CodeBlock.Builder>() {
            @Override
            public CodeBlock.Builder call(CodeBlock.Builder builder, StateProperty property) {
                String reducerFieldName = property.name + REDUCER_SUFFIX;
                return builder.addStatement("$T $NNext = $N.reduce($N, action)", property.boxedStateType(env), property.name, reducerFieldName, property.name);
            }
        }).build();

        MethodSpec reduceMethodSpec = MethodSpec.methodBuilder("reduce")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(combinedReducerReturnTypeName)
                .addParameter(combinedReducerReturnTypeName, stateParam)
                .addParameter(reducerActionType, actionParam)
                .addCode(emitDestructuringBlock(properties, env)).addCode("\n")
                .addCode(dispatchingBlockBuilder).addCode("\n")
                .addCode(CombinedStateProcessingStep.emitReturnBlock(stateClassName, properties))
                .build();

        ClassName builderClassName = ClassName.get(combinedReducerClassName.packageName(), combinedReducerClassName.simpleName(), "Builder");
        TypeSpec reducerBuilderTypeSpec = CombinedStateProcessingStep.createReducerBuilder(combinedStateElement, combinedReducerClassName, builderClassName);


        MethodSpec builderFactoryMethod = MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(builderClassName)
                .addStatement("return new $T()", builderClassName)
                .build();

        typeSpecBuilder
                .addMethod(constructorBuilder.build())
                .addFields(reducerFields)
                .addMethod(reduceMethodSpec)
                .addMethod(builderFactoryMethod)
                .addType(reducerBuilderTypeSpec);

        TypeSpec typeSpec = typeSpecBuilder.build();

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();
        javaFile.writeTo(env.getFiler());
    }

    private static CodeBlock emitDestructuringBlock(List<StateProperty> properties, Env env) {
        CodeBlock.Builder destructuringBlock = CodeBlock.builder();
        for (StateProperty property : properties) {
            destructuringBlock.addStatement("$T $N = null", property.boxedStateType(env), property.name);
        }

        destructuringBlock.add("\n");

        destructuringBlock.beginControlFlow("if (state != null)");
        for (StateProperty property : properties) {
            destructuringBlock.addStatement("$N = state.$N()", property.name, property.name);
        }
        destructuringBlock.endControlFlow();
        return destructuringBlock.build();
    }

    private static CodeBlock emitReturnBlock(ClassName stateClassName, List<StateProperty> properties) {
        StringBuilder equalsCondition = new StringBuilder();
        for (StateProperty property : properties) {
            equalsCondition.append(String.format("\n && %s == %sNext", property.name, property.name));
        }
        String args = join(", ", map(properties, new Utils.Func1<StateProperty, String>() {
            @Override
            public String call(StateProperty property) {
                return property.name + "Next";
            }
        }));
        return CodeBlock.builder()
                .add("//If all values are the same there is no need to create an object\n")
                .beginControlFlow("if (state != null" + equalsCondition + ")")
                .addStatement("return state")
                .nextControlFlow("else")
                .addStatement("return new $T(" + args + ")", stateClassName)
                .endControlFlow()
                .build();
    }

    private static TypeSpec createReducerBuilder(CombinedStateElement combinedStateElement, ClassName combinedReducerClassName, ClassName builderClassName) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(builderClassName).addModifiers(Modifier.STATIC, Modifier.PUBLIC);

        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build());

        for (StateProperty property : combinedStateElement.properties) {
            String name = property.name + REDUCER_SUFFIX;
            FieldSpec field = FieldSpec.builder(property.getReducerInterfaceTypeName(), name, Modifier.PRIVATE)
                    .build();

            MethodSpec setter = MethodSpec.methodBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(builderClassName)
                    .addParameter(property.getReducerInterfaceTypeName(), name)
                    .addStatement("this.$N = $N", name, name)
                    .addStatement("return this")
                    .build();

            builder.addField(field);
            builder.addMethod(setter);
        }

        MethodSpec.Builder buildMethodBuilder = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(combinedReducerClassName);

        ClassName exception = ClassName.get(IllegalStateException.class);
        StringBuilder constructorArgs = new StringBuilder();
        for (StateProperty property : combinedStateElement.properties) {
            String name = property.name + REDUCER_SUFFIX;
            buildMethodBuilder.beginControlFlow("if ($N == null)", name);
            buildMethodBuilder.addStatement("throw new $T($S)", exception, name + " should not be null");
            buildMethodBuilder.endControlFlow();

            if (constructorArgs.length() != 0) constructorArgs.append(", ");
            constructorArgs.append(name);
        }
        buildMethodBuilder.addStatement("return new $T(" + constructorArgs.toString() + ")", combinedReducerClassName);

        builder.addMethod(buildMethodBuilder.build());

        return builder.build();
    }
}
