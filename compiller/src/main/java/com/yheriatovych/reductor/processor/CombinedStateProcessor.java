package com.yheriatovych.reductor.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.CombinedState;
import com.yheriatovych.reductor.processor.model.CombinedStateElement;
import com.yheriatovych.reductor.processor.model.StateProperty;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
public class CombinedStateProcessor extends BaseProcessor {

    public static final String REDUCER_SUFFIX = "Reducer";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(CombinedState.class);
        for (Element element : elements) {
            TypeElement combinedStateTypeElement = (TypeElement) element;

            try {
                CombinedStateElement combinedStateElement = CombinedStateElement.parseCombinedElement(combinedStateTypeElement);
                ClassName stateClassName = emmitCombinedStateImplementation(combinedStateElement);
                emmitCombinedReducer(combinedStateElement, stateClassName);
            } catch (ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                env.printError(element, "Internal processor error:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
        return true;
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

    private void emmitCombinedReducer(CombinedStateElement combinedStateElement, ClassName stateClassName) throws IOException {
        String stateParam = "state";
        String actionParam = "action";
        String isTheSame = "areValuesTheSame";

        TypeName reducerActionType = combinedStateElement.getCombinedReducerActionType();

        String packageName = env.getPackageName(combinedStateElement.stateTypeElement);
        String combinedStateClassName = combinedStateElement.stateTypeElement.getSimpleName().toString();
        ClassName combinedReducerClassName = ClassName.get(packageName, combinedStateClassName + REDUCER_SUFFIX);

        TypeName combinedReducerReturnTypeName = TypeName.get(combinedStateElement.stateTypeElement.asType());
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(combinedReducerClassName)
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(Reducer.class),
                        combinedReducerReturnTypeName))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        List<FieldSpec> reducerFields = new ArrayList<>();
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);

        CodeBlock.Builder destructuringBlockBuilder = CodeBlock.builder();
        CodeBlock.Builder dispatchingBlockBuilder = CodeBlock.builder();
        CodeBlock.Builder stateConstructionBlockBuilder = CodeBlock.builder()
                .add("return $N ? $N : new $T(", isTheSame, stateParam, stateClassName);

        List<StateProperty> properties = combinedStateElement.properties;
        for (int i = 0; i < properties.size(); i++) {
            StateProperty property = properties.get(i);
            boolean isFirst = i == 0;
            String reducerFieldName = property.name + REDUCER_SUFFIX;
            TypeName supReducerType = property.getReducerInterfaceTypeName();
            FieldSpec subReducerField = FieldSpec.builder(supReducerType, reducerFieldName, Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            if (!isFirst)
                stateConstructionBlockBuilder.add(", ");
            stateConstructionBlockBuilder.add(property.name);
            reducerFields.add(subReducerField);

            constructorBuilder.addParameter(supReducerType, reducerFieldName)
                    .addStatement("this.$N = $N", reducerFieldName, reducerFieldName);

            destructuringBlockBuilder
                    .addStatement("$T $N = $N.$N()", property.stateType, property.name, stateParam, property.name);

            dispatchingBlockBuilder
                    .add("\n")
                    .addStatement("$T $NNext = $N.reduce($N, action)", property.stateType, property.name, reducerFieldName, property.name)
                    .addStatement(isTheSame + " &= $N == $NNext", property.name, property.name)
                    .addStatement("$N = $NNext", property.name, property.name);
        }

        stateConstructionBlockBuilder.add(");\n");

        MethodSpec reduceMethodSpec = MethodSpec.methodBuilder("reduce")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(combinedReducerReturnTypeName)
                .addParameter(combinedReducerReturnTypeName, stateParam)
                .addParameter(reducerActionType, actionParam)
                .addStatement("boolean " + isTheSame + " = true")
                .addCode(destructuringBlockBuilder.build())
                .addCode(dispatchingBlockBuilder.build())
                .addCode(stateConstructionBlockBuilder.build())
                .build();

        ClassName builderClassName = ClassName.get(combinedReducerClassName.packageName(), combinedReducerClassName.simpleName(), "Builder");
        TypeSpec reducerBuilderTypeSpec = createReducerBuilder(combinedStateElement, combinedReducerClassName, builderClassName);


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

    private TypeSpec createReducerBuilder(CombinedStateElement combinedStateElement, ClassName combinedReducerClassName, ClassName builderClassName) {
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

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(
                CombinedState.class.getCanonicalName()
        ));
    }
}
