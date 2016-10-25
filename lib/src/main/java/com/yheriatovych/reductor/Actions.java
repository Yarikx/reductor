package com.yheriatovych.reductor;

import com.yheriatovych.reductor.annotations.ActionCreator;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Actions {
    private static ConcurrentHashMap<Class<?>, Object> classCache = new ConcurrentHashMap<>();

    public static <T> T creator(Class<T> actionCreator) {
        Object creator = classCache.get(actionCreator);
        if (creator == null) {
            creator = createCreator(actionCreator);
            classCache.put(actionCreator, creator);
        }
        return (T) creator;
    }

    private static Object createCreator(Class<?> actionCreator) {
        String className = actionCreator.getName();
        String generatedActionCreator = className + "_AutoImpl";

        try {
            Class<?> generatedClass = Class.forName(generatedActionCreator);
            return generatedClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            //should not happen with auto generated classes
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            //if there are not generated action creators just fallback to dynamic proxy
            return createDynamicProxy(actionCreator);
        }
    }

    private static Object createDynamicProxy(Class<?> actionCreator) {
        //TODO validate

        Method[] methods = actionCreator.getMethods();
        final HashMap<Method, String> actionsMap = new HashMap<>(methods.length);
        for (Method method : methods) {
            ActionCreator.Action annotation = method.getAnnotation(ActionCreator.Action.class);
            if (annotation == null) {
                throw new IllegalStateException(
                        String.format("Method %s should be annotated with @%s",
                                method, ActionCreator.Action.class.getCanonicalName()));
            }
            actionsMap.put(method, annotation.value());
        }
        return Proxy.newProxyInstance(actionCreator.getClassLoader(), new Class<?>[]{actionCreator},
                (instance, method, args) -> {
                    String action = actionsMap.get(method);
                    if(args == null){
                        args = new Object[0];
                    }
                    return new Action(action, args);
                });
    }

}
