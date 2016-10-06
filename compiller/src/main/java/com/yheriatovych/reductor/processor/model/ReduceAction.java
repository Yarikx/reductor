package com.yheriatovych.reductor.processor.model;

import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class ReduceAction {

    public final String action;
    public final List<ActionHandlerArg> args;
    public final ExecutableElement executableElement;

    private ReduceAction(String action, List<ActionHandlerArg> args, ExecutableElement executableElement) {
        this.executableElement = executableElement;
        this.args = args;
        this.action = action;
    }

    public static ReduceAction parseReduceAction(Env env, TypeMirror stateType, ExecutableElement element) throws ValidationException {
        AutoReducer.Action action = element.getAnnotation(AutoReducer.Action.class);
        if (action == null) return null;

        String actionNameConstant = action.value();

        if (!env.getTypes().isSameType(stateType, element.getReturnType())) {
            throw new ValidationException(element, "Method %s should return the same type as state (%s)", element, stateType);
        }

        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters.size() == 0) {
            throw new ValidationException(element, "method %s should have at least 1 arguments: state of type %s", element, stateType);
        }

        List<? extends VariableElement> argumentVariables = parameters.subList(1, parameters.size());
        ArrayList<ActionHandlerArg> args = new ArrayList<>();
        for (VariableElement argumentVariable : argumentVariables) {
            args.add(ActionHandlerArg.parse(argumentVariable));
        }
        VariableElement firstParam = parameters.get(0);
        if (!env.getTypes().isSameType(stateType, firstParam.asType())) {
            throw new ValidationException(firstParam, "First parameter %s of method %s should have the same type as state (%s)", firstParam, element, stateType);
        }

        return new ReduceAction(actionNameConstant, args, element);
    }

    public String getMethodName() {
        return executableElement.getSimpleName().toString();
    }

}
