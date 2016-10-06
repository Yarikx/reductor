package com.yheriatovych.reductor.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

public class ValidationUtils {
    public static void validateReturnsState(Env env, TypeMirror stateType, ExecutableElement element) throws ValidationException {
        if (!env.getTypes().isAssignable(element.getReturnType(), stateType)) {
            throw new ValidationException(element, "Method %s should return type assignable to state type %s", element, stateType);
        }
    }

    public static void validateIsNotPrivate(ExecutableElement element) throws ValidationException {
        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            throw new ValidationException(element, "%s has 'private' modifier and is not accessible from child classes", element);
        }
    }
}
