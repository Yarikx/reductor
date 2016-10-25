package com.yheriatovych.reductor.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import java.util.Arrays;

@AutoService(Processor.class)
public class ReductorAnnotationProcessor extends BasicAnnotationProcessor {
    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        Env env = new Env(processingEnv.getTypeUtils(),
                processingEnv.getElementUtils(),
                processingEnv.getMessager(),
                processingEnv.getFiler());
        return Arrays.asList(
                new ActionCreatorProcessingStep(env),
                new AutoReducerProcessingStep(env),
                new CombinedStateProcessingStep(env)
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
