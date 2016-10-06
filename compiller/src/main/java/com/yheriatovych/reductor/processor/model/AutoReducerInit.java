package com.yheriatovych.reductor.processor.model;

import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;
import com.yheriatovych.reductor.processor.ValidationUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class AutoReducerInit {
    public final ExecutableElement executableElement;

    private AutoReducerInit(ExecutableElement executableElement) {
        this.executableElement = executableElement;
    }

    public String getName() {
        return executableElement.getSimpleName().toString();
    }

    public static AutoReducerInit parse(Env env, ExecutableElement executableElement, TypeMirror stateType) throws ValidationException {
        if (executableElement.getAnnotation(AutoReducer.Action.class) != null) {
            throw new ValidationException(executableElement, "Method %s should be may be annotated" +
                    " with either @AutoReducer.InitialState or @AutoReducer.Action but not both", executableElement);
        }
        ValidationUtils.validateReturnsState(env, stateType, executableElement);
        ValidationUtils.validateIsNotPrivate(executableElement);

        int paramsCount = executableElement.getParameters().size();
        if (paramsCount != 0) {
            throw new ValidationException(executableElement, "Method %s should not have any parameters", executableElement);
        }
        return new AutoReducerInit(executableElement);
    }
}
