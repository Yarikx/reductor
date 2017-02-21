package com.yheriatovych.reductor.processor.combinedstate;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.TypeName;
import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.CombinedState;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
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

    public static CombinedStateElement parseCombinedElement(TypeElement typeElement, Env env) throws ValidationException {

        if (!typeElement.getKind().isInterface()) {
            //We allow to implement @CombinedState either with interface or with @AutoValue class

            if (hasAutoValueAnnotation(typeElement, env)) {
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

    private static boolean hasAutoValueAnnotation(TypeElement typeElement, Env env) {
        Name autoValueName = env.getElements().getName("com.google.auto.value.AutoValue");
        for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
            DeclaredType annotationType = annotationMirror.getAnnotationType();
            Name qualifiedName = MoreElements.asType(annotationType.asElement()).getQualifiedName();
            if (qualifiedName.equals(autoValueName)) {
                return true;
            }
        }
        return false;
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
