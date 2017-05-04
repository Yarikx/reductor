package com.yheriatovych.reductor.processor;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MethodTypeInfo {

    private final Method md;
    private MethodTypeInfo(Method md) {
        this.md = md;
    }

    public boolean isReturnTypeGeneric() {
        return md != null && isGenericType(md.getGenericReturnType());
    }

    public Type getGenericReturnType() { return md.getGenericReturnType(); }
    public Type getReturnType() { return md.getReturnType(); }

    public Type[] getGenericsInReturnType() {
        if (!isReturnTypeGeneric()) return null;

        return ((ParameterizedType)md.getGenericReturnType()).getActualTypeArguments();
    }

    public static boolean isGenericType(Type type) {
        return type != null && ((ParameterizedType)type).getActualTypeArguments().length > 0;
    }

    public static MethodTypeInfo of(Method md) {
        return new MethodTypeInfo(md);
    }

}
