package com.yheriatovych.reductor.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.model.ActionHandlerArg;
import com.yheriatovych.reductor.processor.model.AutoReducerConstructor;
import com.yheriatovych.reductor.processor.model.ReduceAction;
import com.yheriatovych.reductor.processor.model.StringReducerElement;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
public class AutoReducerProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(
                AutoReducer.class.getCanonicalName()
        ));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> stringReducers = roundEnv.getElementsAnnotatedWith(AutoReducer.class);
        for (Element stringReducer : stringReducers) {
            try {
                StringReducerElement reducerElement = StringReducerElement.parseStringReducerElement(stringReducer, env);
                emitGeneratedClass(reducerElement, reducerElement.packageName, reducerElement.originalElement);
            } catch (com.yheriatovych.reductor.processor.ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                env.printError(stringReducer, "Internal processor error:\n %s", e.getMessage());
            }
        }

        return true;
    }

    private void emitGeneratedClass(StringReducerElement reducerElement, String packageName, TypeElement originalTypeElement) throws IOException {
        String name = reducerElement.simpleName + "Impl";
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

        CodeBlock.Builder reduceBodyBuilder = CodeBlock.builder()
                .beginControlFlow("switch (action.type)");


        TypeSpec.Builder actionCreatorBuilder = TypeSpec.classBuilder("ActionCreator")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        for (ReduceAction action : reducerElement.actions) {
            final List<ActionHandlerArg> args = action.args;
            reduceBodyBuilder
                    .add("case $S:", action.action);
            if (args.size() < 2) {
                reduceBodyBuilder.indent();
                reduceBodyBuilder.add("\n");
                if (args.size() == 0) {
                    reduceBodyBuilder.addStatement("return $N(state)", action.getMethodName());
                } else if (args.size() == 1) {
                    reduceBodyBuilder.addStatement("return $N(state, ($T) action.value)", action.getMethodName(), args.get(0).argType);
                }
                reduceBodyBuilder.unindent();
            } else {
                reduceBodyBuilder.beginControlFlow("");
                reduceBodyBuilder
                        .addStatement("Object[] args = (Object[]) action.value")
                        .add("return $N(state", action.getMethodName());

                for (int i = 0; i < args.size(); i++) {
                    final ActionHandlerArg arg = args.get(i);
                    reduceBodyBuilder.add(", ($T) args[$L]", arg.argType, i);
                }
                reduceBodyBuilder.add(");\n");
                reduceBodyBuilder.endControlFlow();
            }

            MethodSpec.Builder actionCreatorMethodBuilder = MethodSpec.methodBuilder(action.getMethodName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Action.class);

            if (args.size() == 0) {
                actionCreatorMethodBuilder
                        .addStatement("return new $T($S, null)", Action.class, action.action);
            } else if (args.size() == 1) {
                ActionHandlerArg arg = args.get(0);
                actionCreatorMethodBuilder
                        .addParameter(TypeName.get(arg.argType), arg.argName)
                        .addStatement("return new $T($S, $N)", Action.class, action.action, arg.argName);
            } else {
                actionCreatorMethodBuilder.addCode("Object[] args = new Object[]{");
                for (int i = 0; i < args.size(); i++) {
                    ActionHandlerArg arg = args.get(i);
                    actionCreatorMethodBuilder.addParameter(TypeName.get(arg.argType), arg.argName);
                    if (i != 0) {
                        actionCreatorMethodBuilder.addCode(", ");
                    }
                    actionCreatorMethodBuilder.addCode("$N", arg.argName);
                }
                actionCreatorMethodBuilder.addCode("};\n");
                actionCreatorMethodBuilder
                        .addStatement("return new $T($S, args)", Action.class, action.action);
            }

            actionCreatorBuilder.addMethod(actionCreatorMethodBuilder.build());
        }

        typeSpecBuilder
                .addMethods(emitConstructorAsSuper(reducerElement.constructors))
                .addMethod(reduceMethodBuilder
                        .addCode(reduceBodyBuilder
                                .addStatement("default: return state")
                                .endControlFlow()
                                .build())
                        .build())
                .addType(actionCreatorBuilder.build());

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
}
