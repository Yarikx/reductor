package com.yheriatovych.reductor.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.*;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.model.AutoReducerConstructor;
import com.yheriatovych.reductor.processor.model.ReduceAction;
import com.yheriatovych.reductor.processor.model.StringReducerElement;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class AutoReducerProcessingStep implements BasicAnnotationProcessor.ProcessingStep{

    private final Env env;

    AutoReducerProcessingStep(Env env) {
        this.env = env;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Collections.singleton(AutoReducer.class);
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Element stringReducer : elementsByAnnotation.values()) {
            try {
                StringReducerElement reducerElement = StringReducerElement.parseStringReducerElement(stringReducer, env);
                emitGeneratedClass(reducerElement, reducerElement.getPackageName(env), reducerElement.originalElement);
            } catch (com.yheriatovych.reductor.processor.ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                env.printError(stringReducer, "Internal processor error:\n %s", e.getMessage());
            }
        }
        return Collections.emptySet();
    }

    private void emitGeneratedClass(StringReducerElement reducerElement, String packageName, TypeElement originalTypeElement) throws IOException {
        String name = reducerElement.getSimpleName() + "Impl";
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .superclass(TypeName.get(originalTypeElement.asType()));

        TypeName stateTypeName = TypeName.get(reducerElement.stateType);
        MethodSpec.Builder reduceMethodBuilder = MethodSpec.methodBuilder("reduce")
                .addModifiers(Modifier.PUBLIC)
                .returns(stateTypeName)
                .addAnnotation(Override.class)
                .addParameter(stateTypeName, "state")
                .addParameter(Action.class, "action");

        if (reducerElement.initMethod != null) {
            reduceMethodBuilder.beginControlFlow("if (state == null)")
                    .addStatement("state = $N()", reducerElement.initMethod.getName())
                    .endControlFlow()
                    .addCode("\n");
        }

        CodeBlock.Builder reduceBodyBuilder = CodeBlock.builder()
                .beginControlFlow("switch (action.type)");

        for (ReduceAction action : reducerElement.actions) {
            final List<VariableElement> args = action.args;
            reduceBodyBuilder
                    .add("case $S:", action.action)
                    .indent()
                    .add("\n");
            if (args.size() == 0) {
                if (args.size() == 0) {
                    reduceBodyBuilder.addStatement("return $N(state)", action.getMethodName());
                }
            } else {
                reduceBodyBuilder
                        .add("return $N(state", action.getMethodName());

                for (int i = 0; i < args.size(); i++) {
                    final VariableElement arg = args.get(i);
                    reduceBodyBuilder.add(", ($T) action.getValue($L)", arg.asType(), i);
                }
                reduceBodyBuilder.add(");\n");
            }
            reduceBodyBuilder.unindent();

            MethodSpec.Builder actionCreatorMethodBuilder = MethodSpec.methodBuilder(action.getMethodName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Action.class);

            if (args.size() == 0) {
                actionCreatorMethodBuilder
                        .addStatement("return $T.create($S)", Action.class, action.action);
            } else {
                actionCreatorMethodBuilder
                        .addCode("return $T.create($S", Action.class, action.action);
                for (VariableElement arg : args) {
                    actionCreatorMethodBuilder.addParameter(TypeName.get(arg.asType()), arg.getSimpleName().toString());
                    actionCreatorMethodBuilder.addCode(", $N", arg.getSimpleName().toString());
                }
                actionCreatorMethodBuilder.addCode(");\n");
            }
        }

        typeSpecBuilder
                .addMethods(emitConstructorAsSuper(reducerElement.constructors))
                .addMethod(reduceMethodBuilder
                        .addCode(reduceBodyBuilder
                                .add("default:\n")
                                .indent()
                                .addStatement("return state")
                                .unindent()
                                .endControlFlow()
                                .build())
                        .build());

        TypeSpec actionCreator = emitActionCreator(reducerElement);
        if (actionCreator != null) {
            typeSpecBuilder.addType(actionCreator);
        }

        JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build())
                .build();
        javaFile.writeTo(env.getFiler());
    }

    private List<MethodSpec> emitConstructorAsSuper(List<AutoReducerConstructor> constructors) {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (AutoReducerConstructor constructor : constructors) {

            MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);
            StringBuilder argsBuilder = new StringBuilder();
            List<? extends VariableElement> parameters = constructor.args;

            for (VariableElement variableElement : parameters) {
                String name = variableElement.getSimpleName().toString();
                builder.addParameter(TypeName.get(variableElement.asType()), name);
                if (argsBuilder.length() != 0) argsBuilder.append(", ");
                argsBuilder.append(name);
            }

            builder.addStatement("super(" + argsBuilder.toString() + ")");
            methodSpecs.add(builder.build());
        }

        return methodSpecs;
    }

    private TypeSpec emitActionCreator(StringReducerElement reducerElement) {
        TypeSpec.Builder actionCreatorBuilder = TypeSpec.classBuilder("ActionCreator")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        boolean hasActions = false;
        for (ReduceAction action : reducerElement.actions) {
            if (!action.generateActionCreator) continue;
            hasActions = true;
            final List<VariableElement> args = action.args;

            MethodSpec.Builder actionCreatorMethodBuilder = MethodSpec.methodBuilder(action.getMethodName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Action.class);

            if (args.size() == 0) {
                actionCreatorMethodBuilder
                        .addStatement("return $T.create($S)", Action.class, action.action);
            } else {
                actionCreatorMethodBuilder
                        .addCode("return $T.create($S", Action.class, action.action);
                for (VariableElement arg : args) {
                    actionCreatorMethodBuilder.addParameter(TypeName.get(arg.asType()), arg.getSimpleName().toString());
                    actionCreatorMethodBuilder.addCode(", $N", arg.getSimpleName());
                }
                actionCreatorMethodBuilder.addCode(");\n");
            }
            actionCreatorBuilder.addMethod(actionCreatorMethodBuilder.build());

        }
        return hasActions
                ? actionCreatorBuilder.build()
                : null;
    }
}
