package com.yheriatovych.reductor.processor.autoreducer;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

public class AutoReducerConstructor {
    public final ExecutableElement executableElement;
    public final List<? extends VariableElement> args;

    public AutoReducerConstructor(ExecutableElement executableElement) {
        this.executableElement = executableElement;
        this.args = executableElement.getParameters();
    }
}
