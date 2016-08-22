package com.yheriatovych.reductor.processor.model;

import com.squareup.javapoet.TypeName;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.CombinedState;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

public class CombinedStateElement {
    public final TypeElement mStateTypeElement;
    public final List<StateProperty> mProperties;


    public CombinedStateElement(TypeElement stateTypeElement, List<StateProperty> getters) {
        mStateTypeElement = stateTypeElement;
        mProperties = getters;
    }

    public static CombinedStateElement parseCombindedElement(TypeElement typeElement, Env env) throws ValidationException {

        StateProperty stateProperty;
        if (!typeElement.getKind().isInterface())
            throw new ValidationException(typeElement, "only interfaces supported as %s", CombinedState.class);

        List<StateProperty> getters = new ArrayList<>();

        for (Element element : typeElement.getEnclosedElements()) {
            stateProperty = StateProperty.parseStateProperty(env, element);
            if (stateProperty != null) {
                getters.add(stateProperty);
            }
        }

        return new CombinedStateElement(typeElement, getters);
    }

    public TypeName getCombinedReducerActionType() {
        return TypeName.get(Action.class);
    }
}
