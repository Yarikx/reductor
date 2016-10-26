package com.yheriatovych.reductor.processor.model;

import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;
import com.yheriatovych.reductor.processor.ValidationUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class ReduceAction {

    public final String action;
    public final List<VariableElement> args;
    public final ExecutableElement executableElement;
    public final boolean generateActionCreator;

    private ReduceAction(String action, List<VariableElement> args, ExecutableElement executableElement, boolean generateActionCreator) {
        this.executableElement = executableElement;
        this.args = args;
        this.action = action;
        this.generateActionCreator = generateActionCreator;
    }

    public static ReduceAction parseReduceAction(Env env, TypeMirror stateType, ExecutableElement element) throws ValidationException {
        AutoReducer.Action action = element.getAnnotation(AutoReducer.Action.class);
        if (action == null) return null;

        String actionNameConstant = action.value();
        boolean generateActionCreator = action.generateActionCreator();

        ValidationUtils.validateReturnsState(env, stateType, element);
        ValidationUtils.validateIsNotPrivate(element);

        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters.size() == 0) {
            throw new ValidationException(element, "Method %s should have at least 1 arguments: state of type %s", element, stateType);
        }

        List<? extends VariableElement> argumentVariables = parameters.subList(1, parameters.size());
        ArrayList<VariableElement> args = new ArrayList<>();
        for (VariableElement argumentVariable : argumentVariables) {
            args.add(argumentVariable);
        }
        VariableElement firstParam = parameters.get(0);
        if (!env.getTypes().isAssignable(stateType, firstParam.asType())) {
            throw new ValidationException(firstParam, "First parameter %s of method %s should have the same type as state (%s)", firstParam, element, stateType);
        }

        return new ReduceAction(actionNameConstant, args, element, generateActionCreator);
    }

    public String getMethodName() {
        return executableElement.getSimpleName().toString();
    }

}
