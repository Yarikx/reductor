package com.yheriatovych.reductor.processor;

import com.squareup.javapoet.TypeName;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhengyuanzhou on 2017/5/3.
 */

public class MethodTypeInfo {

    public final Method md;
    public MethodTypeInfo(Method md) {
        this.md = md;
    }

    public boolean isReturnTypeGeneric() {
        if (md == null) return false;
        return isGenericType(md.getGenericReturnType());
    }

    public Type getGenericReturnType() { return md.getGenericReturnType(); }
    public Type getReturnType() { return md.getReturnType(); }
    public TypeName getReturnTypeName() { return TypeName.get(md.getGenericReturnType()); }

    public Type[] getGenericsInReturnType() {
        if (!isReturnTypeGeneric()) return null;

        return ((ParameterizedType)md.getGenericReturnType()).getActualTypeArguments();
    }

    public static String extractTypeName(String fullClzName) {
        if (!fullClzName.contains(".")) return fullClzName;

        return fullClzName.substring(fullClzName.lastIndexOf(".") + 1);
    }

    public static boolean isGenericType(Type type) {
        return type != null && ((ParameterizedType)type).getActualTypeArguments().length > 0;
    }

    public static MethodTypeInfo of(Method md) {
        return new MethodTypeInfo(md);
    }

}
