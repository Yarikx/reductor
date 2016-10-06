package com.yheriatovych.reductor.processor;

import com.google.auto.common.MoreTypes;
import com.yheriatovych.reductor.Reducer;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    interface Func1<T, R> {
        R call(T value);
    }

    interface Func2<T1, T2, R> {
        R call(T1 value1, T2 value2);
    }

    public static DeclaredType getReducerSuperInterface(DeclaredType reducerType) {
        List<? extends TypeMirror> supertypes = MoreTypes.asTypeElement(reducerType).getInterfaces();

        for (TypeMirror supertype : supertypes) {
            boolean isReducer = MoreTypes.isTypeOf(Reducer.class, supertype);
            if (isReducer) {
                return MoreTypes.asDeclared(supertype);
            }
        }
        return null;
    }

    public static String join(String delimiter, List<String> strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            String part = strings.get(i);
            if (i != 0) stringBuilder.append(delimiter);
            stringBuilder.append(part);
        }
        return stringBuilder.toString();
    }

    public static <T, R> List<R> map(List<T> list, Func1<T, R> function) {
        List<R> result = new ArrayList<R>(list.size());
        for (T t : list) {
            result.add(function.call(t));
        }
        return result;
    }

    public static <T, R> R reduce(List<T> list, R initialValue, Func2<R, T, R> func) {
        R result = initialValue;
        for (T value : list) {
            result = func.call(result, value);
        }
        return result;
    }

    static String getDefaultValue(TypeKind kind) {
        switch (kind) {
            case BOOLEAN:
                return "false";
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return "0";
            case CHAR:
                return "'\\u0000'";
            default:
                return "null";
        }
    }

}
