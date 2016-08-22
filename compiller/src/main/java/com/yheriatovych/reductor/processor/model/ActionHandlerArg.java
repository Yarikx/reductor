package com.yheriatovych.reductor.processor.model;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ActionHandlerArg {
    public final String argName;
    public final TypeMirror argType;

    public ActionHandlerArg(String argName, TypeMirror argType) {
        this.argName = argName;
        this.argType = argType;
    }

    public static ActionHandlerArg parse(VariableElement argumentVariable) {
        return new ActionHandlerArg(
                argumentVariable.getSimpleName().toString(),
                argumentVariable.asType()
        );
    }
}
