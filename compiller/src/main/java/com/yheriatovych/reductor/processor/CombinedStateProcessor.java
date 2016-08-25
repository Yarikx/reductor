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

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(CombinedState.class);
        for (Element element : elements) {
            TypeElement combinedStateTypeElement = (TypeElement) element;

            try {
                CombinedStateElement combinedStateElement = CombinedStateElement.parseCombindedElement(combinedStateTypeElement);
                ClassName stateClassName = emmitCombinedStateImplementation(combinedStateElement);
                emmitCombinedReducer(combinedStateElement, stateClassName);
            } catch (ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                env.printError(element, "Internal processor error:\n"+e.getMessage());
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
        for (StateProperty property : combinedStateElement.mProperties) {
            TypeName returnTypeName = TypeName.get(property.mStateType);
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


        TypeSpec typeSpec = TypeSpec.classBuilder(combinedStateElement.mStateTypeElement.getSimpleName().toString() + "Impl")
                .addSuperinterface(TypeName.get(combinedStateElement.mStateTypeElement.asType()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(constructorBuilder.build())
                .addMethods(methodSpecs)
                .addFields(fieldSpecs)
                .build();

        JavaFile javaFile = JavaFile.builder(env.getPackageName(combinedStateElement.mStateTypeElement), typeSpec)
                .build();
        javaFile.writeTo(mFiler);
        return ClassName.get(javaFile.packageName, typeSpec.name);
    }

    private void emmitCombinedReducer(CombinedStateElement combinedStateElement, ClassName stateClassName) throws IOException {
        String stateParam = "state";
        String actionParam = "action";
        String isTheSame = "areValuesTheSame";

        TypeName reducerActionType = combinedStateElement.getCombinedReducerActionType();

        String packageName = env.getPackageName(combinedStateElement.mStateTypeElement);
        String combinedStateClassName = combinedStateElement.mStateTypeElement.getSimpleName().toString();
        ClassName combinedReducerClassName = ClassName.get(packageName, combinedStateClassName + "Reducer");

        TypeName combinedReducerReturnTypeName = TypeName.get(combinedStateElement.mStateTypeElement.asType());
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
        StringBuilder foobar = new StringBuilder();

        for (StateProperty property : combinedStateElement.mProperties) {
            String reducerFieldName = property.name + "Reducer";
            TypeName supReducerType = property.getReducerInterfaceTypeName();
            FieldSpec subReducerField = FieldSpec.builder(supReducerType, reducerFieldName, Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            if (foobar.length() != 0)
                foobar.append(", ");
            foobar.append(property.name);
            reducerFields.add(subReducerField);

            constructorBuilder.addParameter(supReducerType, reducerFieldName)
                    .addStatement("this.$N = $N", reducerFieldName, reducerFieldName);

            destructuringBlockBuilder
                    .addStatement("$T $N = $N.$N()", property.mStateType, property.name, stateParam, property.name);

            dispatchingBlockBuilder
                    .add("\n")
                    .addStatement("$T $NNext = $N.reduce($N, action)", property.mStateType, property.name, reducerFieldName, property.name)
                    .addStatement(isTheSame + " &= $N == $NNext", property.name, property.name)
                    .addStatement("$N = $NNext", property.name, property.name);
        }

        MethodSpec reduceMethdoSpec = MethodSpec.methodBuilder("reduce")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(combinedReducerReturnTypeName)
                .addParameter(combinedReducerReturnTypeName, stateParam)
                .addParameter(reducerActionType, actionParam)
                .addStatement("boolean " + isTheSame + " = true")
                .addCode(destructuringBlockBuilder.build())
                .addCode(dispatchingBlockBuilder.build())
                .addStatement("return $N ? $N : new $T($N)", isTheSame, stateParam, stateClassName, foobar.toString())
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
                .addMethod(reduceMethdoSpec)
                .addMethod(builderFactoryMethod)
                .addType(reducerBuilderTypeSpec);

        TypeSpec typeSpec = typeSpecBuilder.build();

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();
        javaFile.writeTo(mFiler);
    }

    private TypeSpec createReducerBuilder(CombinedStateElement combinedStateElement, ClassName combinedReducerClassName, ClassName builderClassName) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(builderClassName).addModifiers(Modifier.STATIC, Modifier.PUBLIC);

        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build());

        for (StateProperty property : combinedStateElement.mProperties) {
            String name = property.name + "Reducer";
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
        for (StateProperty property : combinedStateElement.mProperties) {
            String name = property.name + "Reducer";
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
