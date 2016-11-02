package com.yheriatovych.reductor.processor.actioncreator;

import com.squareup.javapoet.TypeName;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.ActionCreator;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class ActionCreatorAction {
    public final String actionType;
    public final String methodName;
    public final List<? extends VariableElement> arguments;
    public final List<TypeName> argumentTypes;

    private ActionCreatorAction(String actionType, String methodName, List<? extends VariableElement> arguments) {
        this.actionType = actionType;
        this.methodName = methodName;
        this.arguments = arguments;
        this.argumentTypes = new ArrayList<>(arguments.size());
        for (VariableElement argument : arguments) {
            argumentTypes.add(TypeName.get(argument.asType()));
        }
    }

    public static ActionCreatorAction parse(ExecutableElement element, Env env) throws ValidationException {
        ActionCreator.Action annotation = element.getAnnotation(ActionCreator.Action.class);
        if (annotation == null) {
            throw new ValidationException(element, "Action creator %s should be annotated with @%s", element, ActionCreator.Action.class.getCanonicalName());
        }

        if (!env.getTypes().isAssignable(element.getReturnType(), env.asType(Action.class))) {
            throw new ValidationException(element, "Action creator %s should return %s", element, Action.class.getSimpleName());
        }

        return new ActionCreatorAction(annotation.value(), element.getSimpleName().toString(), element.getParameters());
    }
}
