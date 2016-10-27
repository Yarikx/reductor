package com.yheriatovych.reductor.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.yheriatovych.reductor.processor.autoreducer.AutoReducerProcessingStep;
import com.yheriatovych.reductor.processor.combinedstate.CombinedStateProcessingStep;
import com.yheriatovych.reductor.processor.model.ActionCreatorElement;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@AutoService(Processor.class)
public class ReductorAnnotationProcessor extends BasicAnnotationProcessor {
    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        Env env = new Env(processingEnv.getTypeUtils(),
                processingEnv.getElementUtils(),
                processingEnv.getMessager(),
                processingEnv.getFiler());

        Map<String, ActionCreatorElement> knownActionCreators = new HashMap<>();

        return Arrays.asList(
                new CombinedStateProcessingStep(env),
                new AutoReducerProcessingStep(env, knownActionCreators),
                new ActionCreatorProcessingStep(env, knownActionCreators)
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
