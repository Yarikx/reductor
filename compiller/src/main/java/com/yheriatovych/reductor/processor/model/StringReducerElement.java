package com.yheriatovych.reductor.processor.model;

import com.google.auto.common.MoreTypes;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.ValidationException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class StringReducerElement {
    public final DeclaredType stateType;
    public final List<ReduceAction> actions;
    public final String packageName;
    public final String simpleName;
    public final TypeElement originalElement;


    public StringReducerElement(DeclaredType stateType, List<ReduceAction> actions, String packageName, TypeElement originalElement) {
        this.stateType = stateType;
        this.actions = actions;
        this.packageName = packageName;
        this.originalElement = originalElement;
        this.simpleName = originalElement.getSimpleName().toString();
    }


    public static StringReducerElement parseStringReducerElement(Element element, Env env) throws ValidationException {
        if (element.getKind() != ElementKind.CLASS) {
            throw new ValidationException(element, "You can apply %s only to classes", AutoReducer.class.getSimpleName());
        }


        TypeElement typeElement = (TypeElement) element;
        if (MoreTypes.asDeclared(typeElement.asType()).getEnclosingType().getKind() != TypeKind.NONE) {
            throw new ValidationException(element, "%s annotated reducers should not be inner classes. Probably 'static' modifier missing", AutoReducer.class.getSimpleName());
        }

        DeclaredType declaredType = (DeclaredType) typeElement.asType();
        DeclaredType reducerSuperInterface = Env.getReducerSuperInterface(declaredType);
        if (reducerSuperInterface == null) {
            throw new ValidationException(typeElement, "%s should implement %s interface", typeElement, Reducer.class.getSimpleName());
        }

        TypeMirror stateType = reducerSuperInterface.getTypeArguments().get(0);

        List<ReduceAction> actions = new ArrayList<>();
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            ReduceAction reduceAction = ReduceAction.parseReduceAction(env, stateType, enclosedElement);
            if (reduceAction != null) {
                actions.add(reduceAction);
            }
        }

        final DeclaredType stateDeclaredType = (DeclaredType) stateType;
        return new StringReducerElement(stateDeclaredType, actions, env.getPackageName(element), typeElement);
    }

}
