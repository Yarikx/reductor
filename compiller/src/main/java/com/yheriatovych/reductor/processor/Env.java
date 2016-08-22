package com.yheriatovych.reductor.processor;

import com.google.auto.common.MoreTypes;
import com.yheriatovych.reductor.Reducer;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.List;

public class Env {
    private final Types types;
    private final Elements elements;
    private final Messager messager;

    public Env(Types types, Elements elements, Messager messager) {
        this.types = types;
        this.elements = elements;
        this.messager = messager;
    }

    public static DeclaredType getReducerSuperInterface(DeclaredType reducerType) {
        List<? extends TypeMirror> supertypes = MoreTypes.asTypeElement(reducerType).getInterfaces();

        for (TypeMirror supertype : supertypes) {
            boolean isReducer = MoreTypes.isTypeOf(Reducer.class, supertype);
            if (isReducer) {
                return MoreTypes.asDeclared(supertype);
            }
        }
        throw new IllegalStateException("type " + reducerType + " is not subclass of " + Reducer.class);
    }

    public void printError(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    public void printWarning(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.WARNING, element, message, args);
    }

    private void printMessage(Diagnostic.Kind level, Element element, String message, Object args) {
        messager.printMessage(level, String.format(message, args), element);
    }

    public Types getTypes() {
        return types;
    }

    public String getPackageName(Element element) {
        return elements.getPackageOf(element).getQualifiedName().toString();
    }
}
