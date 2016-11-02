package com.yheriatovych.reductor;

import com.yheriatovych.reductor.annotations.ActionCreator;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActionsTest {

    @ActionCreator
    interface Creator {
        @ActionCreator.Action("TEST1")
        Action test1();

        @ActionCreator.Action("TEST2")
        Action test2(int foo);

        @ActionCreator.Action("TEST3")
        Action test3(int foo, String bar);
    }

    @Test
    public void testCreateDynamicProxy() {
        Creator creator = Actions.from(Creator.class);
        Action action = creator.test1();

        assertEquals(Action.create("TEST1"), action);
    }

    @Test
    public void testCreateDynamicProxy2() {
        Creator creator = Actions.from(Creator.class);
        Action action = creator.test2(5);

        assertEquals(Action.create("TEST2", 5), action);
    }

    @Test
    public void testCreateDynamicProxy3() {
        Creator creator = Actions.from(Creator.class);
        Action action = creator.test3(42, "foobar");

        assertEquals(Action.create("TEST3", 42, "foobar"), action);
    }

    interface Creator2 {
        @ActionCreator.Action("TEST1")
        Action test1();
    }

    @Test(expected = IllegalStateException.class)
    public void testFailsWhenDoNotHaveAnnotation() {
        Creator2 creator = Actions.from(Creator2.class);
    }

    @ActionCreator
    interface Creator3 {
        Action test1();
    }

    @Test(expected = IllegalStateException.class)
    public void testFailsWhenDoNotHaveMethodAnnotation() {
        Creator3 creator = Actions.from(Creator3.class);
    }

    @ActionCreator
    interface Creator4 {
        @ActionCreator.Action("TEST")
        Action foobar();
    }

    static class Creator4_AutoImpl implements Creator4 {
        @Override
        public Action foobar() {
            return new Action("TEST");
        }
    }

    @Test
    public void testInstatntiateAutoClass() {
        Creator4 creator = Actions.from(Creator4.class);
        assertEquals(Creator4_AutoImpl.class, creator.getClass());
    }
}