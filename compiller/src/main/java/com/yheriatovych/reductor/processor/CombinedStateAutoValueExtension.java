package com.yheriatovych.reductor.processor;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.squareup.javapoet.*;
import com.yheriatovych.reductor.annotations.CombinedState;
import com.yheriatovych.reductor.processor.model.CombinedStateElement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Map;

@AutoService(AutoValueExtension.class)
public class CombinedStateAutoValueExtension extends AutoValueExtension {
    @Override
    public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
        return null;
    }

    @Override
    public boolean applicable(Context context) {
        TypeElement typeElement = context.autoValueClass();
        boolean isApplicable = typeElement.getAnnotation(CombinedState.class) != null;
        if(isApplicable) {
            try {
                CombinedStateElement combinedStateElement = CombinedStateElement.parseAutoValueCombinedElement(typeElement, context.properties());
                ProcessingEnvironment processingEnvironment = context.processingEnvironment();
                Env env = new Env(processingEnvironment.getTypeUtils(), processingEnvironment.getElementUtils(), processingEnvironment.getMessager(), processingEnvironment.getFiler());
                CombinedStateProcessor.emmitCombinedReducer(env, combinedStateElement, ClassName.get(context.packageName(), "AutoValue_"+context.autoValueClass().getSimpleName().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


}
