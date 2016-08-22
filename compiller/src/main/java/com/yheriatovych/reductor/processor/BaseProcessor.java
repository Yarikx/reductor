package com.yheriatovych.reductor.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Types;

public abstract class BaseProcessor extends AbstractProcessor {
    protected Filer mFiler;
    protected Types mTypeUtils;
    protected Env env;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mTypeUtils = processingEnv.getTypeUtils();
        env = new Env(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getMessager());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
