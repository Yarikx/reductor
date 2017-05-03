package com.yheriatovych.reductor.processor;

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

    public String getReturnTypeName() {
        return md.getReturnType().getSimpleName();
    }

    public List<String> getGenericsInReturnType() {
        if (!isReturnTypeGeneric()) return null;

        List<Type> gTypes = Arrays.asList(
                ((ParameterizedType)md.getGenericReturnType()).getActualTypeArguments()
        );

        return Utils.map(gTypes, new Utils.Func1<Type, String>() {
            @Override
            public String call(Type value) {
                return extractTypeName(value.toString());
            }
        });
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
