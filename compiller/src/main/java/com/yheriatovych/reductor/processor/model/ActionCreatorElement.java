package com.yheriatovych.reductor.processor.model;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.yheriatovych.reductor.annotations.ActionCreator;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionCreatorElement {
    public final List<ActionCreatorAction> actions;
    private final TypeElement typeElement;
    private Map<String, ActionCreatorAction> actionMap;

    private ActionCreatorElement(List<ActionCreatorAction> actions, TypeElement typeElement) {
        this.actions = actions;
        this.typeElement = typeElement;
        actionMap = new HashMap<>();
        for (ActionCreatorAction action : actions) {
            actionMap.put(action.actionType, action);
        }
    }

    public boolean hasAction(String actionType, List<? extends VariableElement> reducerArgs, Env env) {
        ActionCreatorAction actionCreator = actionMap.get(actionType);
        if(actionCreator == null || reducerArgs.size() != actionCreator.arguments.size()) return false;
        List<? extends VariableElement> arguments = actionCreator.arguments;
        for (int i = 0; i < arguments.size(); i++) {
            VariableElement creatorArg = arguments.get(i);
            VariableElement reducerArg = reducerArgs.get(i);
            if (!env.getTypes().isAssignable(creatorArg.asType(), reducerArg.asType())) {
                return false;
            }
        }
        return true;
    }

    public String getPackageName(Env env) {
        return env.getPackageName(typeElement);
    }

    public String getName(Env env) {
        return env.getElements().getBinaryName(typeElement).toString();
    }

    public TypeMirror getType() {
        return  typeElement.asType();
    }

    public static ActionCreatorElement parse(Element element, Env env) throws ValidationException {
        if (element.getKind() != ElementKind.INTERFACE) {
            throw new ValidationException(element, "%s annotated with %s should be interface", element, ActionCreator.class);
        }
        TypeElement typeElement = MoreElements.asType(element);

        List<ActionCreatorAction> actions = new ArrayList<>();
        for (Element methodElement : typeElement.getEnclosedElements()) {
            actions.add(ActionCreatorAction.parse(MoreElements.asExecutable(methodElement), env));
        }

        return new ActionCreatorElement(actions, typeElement);
    }
}
