package com.yheriatovych.reductor.processor;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.ActionCreator;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.model.ActionHandlerArg;
import com.yheriatovych.reductor.processor.model.AutoReducerConstructor;
import com.yheriatovych.reductor.processor.model.ReduceAction;
import com.yheriatovych.reductor.processor.model.StringReducerElement;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
public class ActionCreatorProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(
                ActionCreator.class.getCanonicalName()
        ));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> creatorElements = roundEnv.getElementsAnnotatedWith(ActionCreator.class);
        for (Element element : creatorElements) {
            try {
                foobar(element, env);
            } catch (ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                env.printError(element, "Internal processor error:\n %s", e.getMessage());
            }
        }

        return true;
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
}
