package com.yheriatovych.reductor.processor.combinedstate;

import com.google.auto.value.AutoValue;
import com.squareup.javapoet.TypeName;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.CombinedState;
import com.yheriatovych.reductor.processor.ValidationException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CombinedStateElement {
    public final TypeElement stateTypeElement;
    public final List<StateProperty> properties;


    public CombinedStateElement(TypeElement stateTypeElement, List<StateProperty> getters) {
        this.stateTypeElement = stateTypeElement;
        properties = getters;
    }

    public static CombinedStateElement parseCombinedElement(TypeElement typeElement) throws ValidationException {

        if (!typeElement.getKind().isInterface()) {
            //We allow to implement @CombinedState either with interface or with @AutoValue class
            if (typeElement.getAnnotation(AutoValue.class) != null) {
                //Do nothing here. CombinedStateAutoValueExtension will handle this
                return null;
            } else {
                throw new ValidationException(typeElement, "Only interfaces and @AutoValue classes are supported as @%s", CombinedState.class.getSimpleName());
            }
        }

        List<StateProperty> properties = new ArrayList<>();
        for (Element element : typeElement.getEnclosedElements()) {
            StateProperty stateProperty = StateProperty.parseStateProperty(element);
            if (stateProperty != null) {
                properties.add(stateProperty);
            }
        }

        return new CombinedStateElement(typeElement, properties);
    }

    public TypeName getCombinedReducerActionType() {
        return TypeName.get(Action.class);
    }

    public static CombinedStateElement parseAutoValueCombinedElement(TypeElement typeElement, Map<String, ExecutableElement> autoValueProperties) throws ValidationException {
        List<StateProperty> properties = new ArrayList<>();

        for (String propertyName : autoValueProperties.keySet()) {
            ExecutableElement propertyElement = autoValueProperties.get(propertyName);

            StateProperty stateProperty = StateProperty.parseStateProperty(propertyElement);
            if (stateProperty != null) {
                properties.add(stateProperty);
            }
        }

        return new CombinedStateElement(typeElement, properties);
    }
}
