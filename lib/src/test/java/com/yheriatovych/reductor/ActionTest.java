package com.yheriatovych.reductor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionTest {

    @Test
    public void testActionToStringObject() {
        Action action = Action.create("TEST", 5);
        assertEquals("Action{type='TEST', values=[5]}", action.toString());
    }

    @Test
    public void testActionToStringObjectArray() {
        Action action = Action.create("TEST", "foo", "bar");
        assertEquals("Action{type='TEST', values=[foo, bar]}", action.toString());
    }

    @Test
    public void testActionToStringObjectNull() {
        Action action = Action.create("TEST");
        assertEquals("Action{type='TEST', values=[]}", action.toString());
    }

}