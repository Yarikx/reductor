package com.yheriatovych.reductor.processor;

import javax.lang.model.element.Element;

public class ValidationException extends Exception {
    private final Element element;
    private final String message;

    public ValidationException(Element element, String message, Object... args) {
        this.element = element;
        this.message = String.format(message, args);
    }

    public Element getElement() {
        return element;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
