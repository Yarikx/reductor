package com.yheriatovych.reductor.processor.actioncreator;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.TypeName;
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

    public boolean hasAction(String actionType, List<? extends VariableElement> reducerArgs) {
        ActionCreatorAction actionCreator = actionMap.get(actionType);
        if (actionCreator == null || reducerArgs.size() != actionCreator.arguments.size()) return false;
        List<? extends VariableElement> arguments = actionCreator.arguments;
        for (int i = 0; i < arguments.size(); i++) {
            //we are doing black magic with checking if TypeName are equals
            //instead of checking if two TypeMirrors are equals because TypeMirrors can be different for the same type
            //happens when code is processed with several number of processing steps
            TypeName creatorArgType = actionCreator.argumentTypes.get(i);
            TypeName reducerArgType = TypeName.get(reducerArgs.get(i).asType());
            if (!creatorArgType.equals(reducerArgType)) {
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
        return typeElement.asType();
    }

    public static ActionCreatorElement parse(Element element, Env env) throws ValidationException {
        if (element.getKind() != ElementKind.INTERFACE) {
            throw new ValidationException(element, "%s annotated with @%s should be interface", element, ActionCreator.class.getSimpleName());
        }
        TypeElement typeElement = MoreElements.asType(element);

        List<ActionCreatorAction> actions = new ArrayList<>();
        for (Element methodElement : typeElement.getEnclosedElements()) {
            if (methodElement.getKind() != ElementKind.METHOD) continue;
            actions.add(ActionCreatorAction.parse(MoreElements.asExecutable(methodElement), env));
        }

        return new ActionCreatorElement(actions, typeElement);
    }
}
