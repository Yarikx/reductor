package com.yheriatovych.reductor.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.*;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.ActionCreator;
import com.yheriatovych.reductor.processor.model.ActionCreatorAction;
import com.yheriatovych.reductor.processor.model.ActionCreatorElement;

import javax.lang.model.element.*;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

class ActionCreatorProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private final Env env;

    ActionCreatorProcessingStep(Env env) {
        this.env = env;
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Element element : elementsByAnnotation.values()) {
            try {
                ActionCreatorElement creatorElement = ActionCreatorElement.parse(element, env);
                emitActionCreator(creatorElement, env);
            } catch (ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                env.printError(element, "Internal processor error:\n " + e.getMessage());
            }
        }
        return Collections.emptySet();
    }

    private void emitActionCreator(ActionCreatorElement creatorElement, Env env) throws IOException {
        ClassName className = ClassName.bestGuess(creatorElement.getName(env) + "_AutoImpl");
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(TypeName.get(creatorElement.getType()));

        for (ActionCreatorAction action : creatorElement.actions) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(action.methodName)
                    .returns(Action.class)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);

            StringBuilder args = new StringBuilder();
            for (VariableElement variableElement : action.arguments) {
                String name = variableElement.getSimpleName().toString();
                methodBuilder.addParameter(TypeName.get(variableElement.asType()), name);
                args.append(", ").append(name);
            }

            typeBuilder.addMethod(methodBuilder
                    .addStatement("return $T.create(\"$L\"$N)", Action.class, action.actionType, args.toString())
                    .build());
        }

        JavaFile.builder(creatorElement.getPackageName(env), typeBuilder.build())
                .build()
                .writeTo(env.getFiler());
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Collections.singleton(ActionCreator.class);
    }
}
