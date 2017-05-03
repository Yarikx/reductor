package com.yheriatovych.reductor.processor.combinedstate;

import com.google.auto.common.MoreTypes;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.MethodTypeInfo;
import com.yheriatovych.reductor.processor.ValidationException;

import java.lang.reflect.Method;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class StateProperty {
    public final String name;
    public final TypeMirror stateType;
    public final ExecutableElement executableElement;

    private StateProperty(String name, TypeMirror stateType, ExecutableElement executableElement) {
        this.name = name;
        this.stateType = stateType;
        this.executableElement = executableElement;
    }

    public TypeMirror boxedStateType(Env env) {
        if(stateType.getKind().isPrimitive()) {
            return env.getTypes().boxedClass(MoreTypes.asPrimitiveType(stateType)).asType();
        }
        return stateType;
    }

    static StateProperty parseStateProperty(Element element) throws ValidationException {
        StateProperty stateProperty;
        if (element.getKind() != ElementKind.METHOD) return null;

        //We don't care about static methods, just ignore
        if (element.getModifiers().contains(Modifier.STATIC)) return null;

        //We also don't care about default methods.
        //If default method is there, it probably not meant to be override
        if (element.getModifiers().contains(Modifier.DEFAULT)) return null;

        ExecutableElement executableElement = (ExecutableElement) element;
        String propertyName = executableElement.getSimpleName().toString();
        TypeMirror stateType = executableElement.getReturnType();

        if (!executableElement.getParameters().isEmpty())
            throw new ValidationException(executableElement, "state property accessor %s should not have any parameters", executableElement);

        if (stateType.getKind() == TypeKind.VOID) {
            throw new ValidationException(executableElement, "void is not allowed as return type for property method %s", executableElement);
        }

        stateProperty = new StateProperty(propertyName, stateType, executableElement);
        return stateProperty;
    }

    public TypeName getReducerInterfaceTypeName() {
        TypeName stateType = TypeName.get(this.stateType);
        if (stateType.isPrimitive()) {
            stateType = stateType.box();
        }
        return ParameterizedTypeName.get(ClassName.get(Reducer.class), stateType);
    }

    static MethodTypeInfo getReducerInterfaceReturnTypeInfo() {
        Method[] ms = Reducer.class.getDeclaredMethods();
        if (ms.length <= 0) return null;

        return MethodTypeInfo.of(ms[0]);
    }
}
