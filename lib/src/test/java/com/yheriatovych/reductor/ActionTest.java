package com.yheriatovych.reductor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionTest {

    @Test
    public void testActionToStringObject() {
        Action action = new Action("TEST", 5);
        assertEquals("Action{type='TEST', value=5}", action.toString());
    }

    @Test
    public void testActionToStringObjectArray() {
        Action action = new Action("TEST", new String[]{"foo", "bar"});
        assertEquals("Action{type='TEST', value=[foo, bar]}", action.toString());
    }

    @Test
    public void testActionToStringObjectNull() {
        Action action = new Action("TEST", null);
        assertEquals("Action{type='TEST', value=null}", action.toString());
    }

}