package com.yheriatovych.reductor.processor.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class StateProperty {
    public final String name;
    public final TypeMirror mStateType;
    public final ExecutableElement mExecutableElement;

    private StateProperty(String name, TypeMirror stateType, ExecutableElement executableElement) {
        this.name = name;
        mStateType = stateType;
        mExecutableElement = executableElement;
    }

    static StateProperty parseStateProperty(Env env, Element element) throws ValidationException {
        StateProperty stateProperty;
        if (element.getKind() != ElementKind.METHOD) return null;

        ExecutableElement executableElement = (ExecutableElement) element;
        String propertyName = executableElement.getSimpleName().toString();
        TypeMirror stateType = executableElement.getReturnType();

        if (!executableElement.getParameters().isEmpty())
            throw new ValidationException(executableElement, "state property should not have any parameters");

        stateProperty = new StateProperty(propertyName, stateType, executableElement);
        return stateProperty;
    }

    public TypeName getReducerInterfaceTypeName() {
        TypeName stateType = TypeName.get(mStateType);
        if (stateType.isPrimitive()) {
            stateType = stateType.box();
        }
        return ParameterizedTypeName.get(ClassName.get(Reducer.class), stateType);
    }
}
