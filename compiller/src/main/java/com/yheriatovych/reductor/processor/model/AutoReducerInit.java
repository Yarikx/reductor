package com.yheriatovych.reductor.processor.model;

import javax.lang.model.element.ExecutableElement;

public class AutoReducerInit {
    public final ExecutableElement executableElement;

    private AutoReducerInit(ExecutableElement executableElement) {
        this.executableElement = executableElement;
    }

    public String getName() {
        return executableElement.getSimpleName().toString();
    }

    public static AutoReducerInit parse(ExecutableElement executableElement) {
        //TODO validate
        return new AutoReducerInit(executableElement);
    }
}
