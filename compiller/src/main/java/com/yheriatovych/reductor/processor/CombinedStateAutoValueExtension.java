package com.yheriatovych.reductor.processor;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.squareup.javapoet.ClassName;
import com.yheriatovych.reductor.annotations.CombinedState;
import com.yheriatovych.reductor.processor.model.CombinedStateElement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

@AutoService(AutoValueExtension.class)
public class CombinedStateAutoValueExtension extends AutoValueExtension {
    @Override
    public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
        //So yeah, we are generating code in `applicable` method, not here
        //The reason for it: If we declare that some class is applicable for this extension, we need to contribute to
        //value class hierarchy. But we don't need it actually.
        //The only thing we need to do is to generate another class (Reducer)

        return null;
    }

    @Override
    public boolean applicable(Context context) {
        TypeElement typeElement = context.autoValueClass();
        boolean isApplicable = typeElement.getAnnotation(CombinedState.class) != null;
        if (isApplicable) {
            ProcessingEnvironment processingEnvironment = context.processingEnvironment();
            Env env = new Env(processingEnvironment.getTypeUtils(), processingEnvironment.getElementUtils(), processingEnvironment.getMessager(), processingEnvironment.getFiler());
            try {
                CombinedStateElement combinedStateElement = CombinedStateElement.parseAutoValueCombinedElement(typeElement, context.properties());
                CombinedStateProcessingStep.emmitCombinedReducer(env, combinedStateElement, ClassName.get(context.packageName(), "AutoValue_" + context.autoValueClass().getSimpleName()));
            } catch (ValidationException ve) {
                env.printError(ve.getElement(), ve.getMessage());
            } catch (Exception e) {
                env.printError(typeElement, "Internal processor error:\n" + e.getMessage());
                e.printStackTrace();
            }
        }

        return false;
    }


}
