package com.yheriatovych.reductor.processor.autoreducer;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.yheriatovych.reductor.Reducer;
import com.yheriatovych.reductor.annotations.AutoReducer;
import com.yheriatovych.reductor.processor.Env;
import com.yheriatovych.reductor.processor.Utils;
import com.yheriatovych.reductor.processor.ValidationException;
import com.yheriatovych.reductor.processor.actioncreator.ActionCreatorElement;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StringReducerElement {
    public final DeclaredType stateType;
    public final List<ReduceAction> actions;
    public final AutoReducerInit initMethod;
    public final TypeElement originalElement;
    public final List<AutoReducerConstructor> constructors;

    public StringReducerElement(DeclaredType stateType, List<ReduceAction> actions, AutoReducerInit initMethod, TypeElement originalElement, List<AutoReducerConstructor> constructors) {
        this.stateType = stateType;
        this.actions = actions;
        this.initMethod = initMethod;
        this.originalElement = originalElement;
        this.constructors = constructors;
    }

    public String getSimpleName() {
        return originalElement.getSimpleName().toString();
    }

    public String getPackageName(Env env) {
        return env.getPackageName(originalElement);
    }

    public static StringReducerElement parseStringReducerElement(Element element, Map<String, ActionCreatorElement> knownActionCreators, Env env) throws ValidationException {
        if (element.getKind() != ElementKind.CLASS) {
            throw new ValidationException(element, "You can apply %s only to classes", AutoReducer.class.getSimpleName());
        }


        TypeElement typeElement = (TypeElement) element;
        if (MoreTypes.asDeclared(typeElement.asType()).getEnclosingType().getKind() != TypeKind.NONE) {
            throw new ValidationException(element, "%s annotated reducers should not be inner classes. Probably 'static' modifier missing", AutoReducer.class.getSimpleName());
        }

        DeclaredType declaredType = (DeclaredType) typeElement.asType();
        DeclaredType reducerSuperInterface = Utils.getReducerSuperInterface(declaredType);
        if (reducerSuperInterface == null) {
            throw new ValidationException(typeElement, "%s should implement %s interface", typeElement, Reducer.class.getSimpleName());
        }

        TypeMirror stateType = reducerSuperInterface.getTypeArguments().get(0);

        List<ReduceAction> actions = new ArrayList<>();
        AutoReducerInit initMethod = null;
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if(enclosedElement.getKind() != ElementKind.METHOD) continue;
            ExecutableElement executableElement = MoreElements.asExecutable(enclosedElement);

            if (enclosedElement.getAnnotation(AutoReducer.InitialState.class) != null) {
                if(initMethod != null) {
                    throw new ValidationException(enclosedElement, "Methods %s and %s are both annotated with @AutoReducer.InitialState. Only one @AutoReducer.InitialState method is allowed", initMethod.executableElement, executableElement);
                }
                initMethod = AutoReducerInit.parse(env, executableElement, stateType);
            } else {
                ReduceAction reduceAction = ReduceAction.parseReduceAction(env, stateType, executableElement, knownActionCreators);
                if (reduceAction != null) {
                    actions.add(reduceAction);
                }
            }
        }

        List<AutoReducerConstructor> constructors = parseConstructors(typeElement);

        final DeclaredType stateDeclaredType = (DeclaredType) stateType;
        return new StringReducerElement(stateDeclaredType, actions, initMethod, typeElement, constructors);
    }

    private static List<AutoReducerConstructor> parseConstructors(TypeElement typeElement) throws ValidationException {
        List<AutoReducerConstructor> constructors = new ArrayList<>();
        boolean hasNonDefaultConstructors = false;
        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement executableElement = MoreElements.asExecutable(element);

                if (executableElement.getModifiers().contains(Modifier.PRIVATE)) continue;

                List<? extends VariableElement> parameters = executableElement.getParameters();

                hasNonDefaultConstructors |= parameters.size() != 0;
                constructors.add(new AutoReducerConstructor(executableElement));
            }
        }

        //if we only have one default constructor, it can be omitted
        if (!hasNonDefaultConstructors && constructors.size() == 1) return Collections.emptyList();

        //this is the case when all constructors are private
        if (constructors.size() == 0)
            throw new ValidationException(typeElement, "No accessible constructors available for class %s", typeElement);

        return constructors;
    }

}
