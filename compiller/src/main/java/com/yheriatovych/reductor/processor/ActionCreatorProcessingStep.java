package com.yheriatovych.reductor.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.ActionCreator;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

class ActionCreatorProcessingStep implements BasicAnnotationProcessor.ProcessingStep{

    private final Env env;

    ActionCreatorProcessingStep(Env env) {
        this.env = env;
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Element element : elementsByAnnotation.values()) {
            try {
                foobar(element, env);
            } catch (ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                env.printError(element, "Internal processor error:\n %s", e.getMessage());
            }
        }
        return Collections.emptySet();
    }

    private void foobar(Element element, Env env) throws ValidationException {
        TypeElement typeElement = MoreElements.asType(element);
        TypeMirror typeMirror = typeElement.asType();
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "_AutoImpl")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(TypeName.get(typeMirror));

        for (Element method : element.getEnclosedElements()) {
            ActionCreator.Action annotation = method.getAnnotation(ActionCreator.Action.class);
            if (annotation != null) {
                String actionType = annotation.value();

                ExecutableElement executableElement = MoreElements.asExecutable(method);
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                        .returns(Action.class)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC);

                StringBuilder args = new StringBuilder();
                for (VariableElement variableElement : executableElement.getParameters()) {
                    String name = variableElement.getSimpleName().toString();
                    methodBuilder.addParameter(TypeName.get(variableElement.asType()), name);
                    args.append(", ").append(name);
                }

                typeBuilder.addMethod(methodBuilder
                        .addStatement("return $T.create(\"$L\"$N)", Action.class, actionType, args.toString())
                        .build());
            }
        }

        try {
            JavaFile.builder(env.getPackageName(element), typeBuilder.build())
                    .build()
                    .writeTo(env.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Collections.singleton(ActionCreator.class);
    }
}
