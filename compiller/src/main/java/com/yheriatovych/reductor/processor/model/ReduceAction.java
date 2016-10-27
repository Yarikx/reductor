package com.yheriatovych.reductor.processor.model;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.yheriatovych.reductor.annotations.ActionCreator;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static ReduceAction parseReduceAction(Env env, TypeMirror stateType, ExecutableElement element, Map<String, ActionCreatorElement> knownActionCreators) throws ValidationException {
        AutoReducer.Action action = element.getAnnotation(AutoReducer.Action.class);
        if (action == null) return null;

        String actionNameConstant = action.value();
        boolean generateActionCreator = action.generateActionCreator();
        TypeMirror actionCreatorType = getCreator(action, env.getElements(), env, element);

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

        if (actionCreatorType != null) {
            validateActionCreator(element, actionNameConstant, actionCreatorType, args, knownActionCreators, env);
        }

        return new ReduceAction(actionNameConstant, args, element, generateActionCreator);
    }

    private static TypeMirror getCreator(AutoReducer.Action action, Elements elements, Env env, ExecutableElement element) {
        TypeMirror typeMirror;
        try {
            Class<?> fromClass = action.from();
            String className = fromClass.getCanonicalName();
            typeMirror = elements.getTypeElement(className).asType();
        } catch (MirroredTypeException mte) {
            typeMirror = mte.getTypeMirror();
        }

        //Void is used by default meaning it's not linked to any action creator
        return env.getTypes().isSameType(typeMirror, env.asType(Void.class))
                ? null
                : typeMirror;
    }

    private static void validateActionCreator(ExecutableElement element,
                                              String actionName,
                                              TypeMirror actionCreator,
                                              ArrayList<VariableElement> args,
                                              Map<String, ActionCreatorElement> knownActionCreators,
                                              Env env) throws ValidationException {
        Element actionCreatorElement = MoreTypes.asElement(actionCreator);
        if (!MoreElements.isAnnotationPresent(actionCreatorElement, ActionCreator.class)) {
            throw new ValidationException(element, "Action creator %s should be annotated with %s", actionCreator, ActionCreator.class);
        }

        ActionCreatorElement creatorElement = knownActionCreators.get(env.getElements().getBinaryName((TypeElement) actionCreatorElement).toString());
        if(creatorElement == null) {
            throw new ElementNotReadyException();
        }
        if(!creatorElement.hasAction(actionName, args, env)){
            throw new ValidationException(element, "cannot find action creator for action \"%s\" and args %s in interface %s", actionName, toString(args), creatorElement.getName(env));
        }
    }

    private static String toString(List<VariableElement> arguments) {
        return "[" + Utils.join(", ", Utils.map(arguments, new Utils.Func1<VariableElement, String>() {
            @Override
            public String call(VariableElement arg) {
                return arg.asType().toString();
            }
        })) + "]";
    }

    public String getMethodName() {
        return executableElement.getSimpleName().toString();
    }

}
