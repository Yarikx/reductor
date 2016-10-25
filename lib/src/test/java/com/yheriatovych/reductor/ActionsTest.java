package com.yheriatovych.reductor;

import com.yheriatovych.reductor.annotations.ActionCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActionsTest {

    interface Creator {
        @ActionCreator.Action("TEST1")
        Action test1();

        @ActionCreator.Action("TEST2")
        Action test2(int foo);

        @ActionCreator.Action("TEST3")
        Action test3(int foo, String bar);
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testCreateDynamicProxy() {
        Creator creator = Actions.creator(Creator.class);
        Action action = creator.test1();

        assertEquals(Action.create("TEST1"), action);
    }

    @Test
    public void testCreateDynamicProxy2() {
        Creator creator = Actions.creator(Creator.class);
        Action action = creator.test2(5);

        assertEquals(Action.create("TEST2", 5), action);
    }

    @Test
    public void testCreateDynamicProxy3() {
        Creator creator = Actions.creator(Creator.class);
        Action action = creator.test3(42, "foobar");

        assertEquals(Action.create("TEST3", 42, "foobar"), action);
    }
}