package com.yheriatovych.reductor.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class Env {
    private final Types types;
    private final Elements elements;
    private final Messager messager;
    private final Filer filer;

    public Env(Types types, Elements elements, Messager messager, Filer filer) {
        this.types = types;
        this.elements = elements;
        this.messager = messager;
        this.filer = filer;
    }

    public void printError(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void printMessage(Diagnostic.Kind level, Element element, String message, Object args) {
        messager.printMessage(level, String.format(message, args), element);
    }

    public Types getTypes() {
        return types;
    }

    public Filer getFiler() {
        return filer;
    }

    public String getPackageName(Element element) {
        return elements.getPackageOf(element).getQualifiedName().toString();
    }

}
