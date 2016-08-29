package com.yheriatovych.reductor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class MiddlewareTest {

    private static class TestState {

    }

    TestState initialState;
    TestState nextState;
    @Mock
    Reducer<TestState> reducer;
    @Mock
    Store.StateChangeListener<TestState> listener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initialState = new TestState();
        nextState = new TestState();
        when(reducer.reduce(any(), any())).thenReturn(nextState);
    }

    @Test
    public void testPropagateActionToMiddleware() {
        Action action = new Action("test");
        Middleware<TestState> middleware = mock(Middleware.class);
        Store store = Store.create(reducer, initialState, middleware);
        store.dispatch(action);
        verify(middleware).dispatch(eq(store), eq(action), any());
    }

    @Test
    public void testDoNotChangeStateWhenMiddlewareDoNothing() {
        Store<TestState> store = Store.create(reducer, initialState, (Middleware<TestState>) (store1, action, nextDispatcher) -> {
            //NOOP
        });
        store.subscribe(listener);
        Action action = new Action("test");

        store.dispatch(action);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testChangeStateWhenMiddlewareCallNext() {
        Store<TestState> store = Store.create(reducer, initialState,
                (Middleware<TestState>) (store1, action, nextDispatcher) ->
                        nextDispatcher.call(action));
        store.subscribe(listener);
        Action action = new Action("test");

        store.dispatch(action);

        verify(listener).onStateChanged(nextState);
    }

    @Test
    public void testCallMiddlewaresInOrder() {
        Middleware<TestState> original = new Middleware<TestState>() {
            @Override
            public void dispatch(Store<TestState> store1, Object action, NextDispatcher nextDispatcher) {
                nextDispatcher.call(action);
            }
        };

        Middleware<TestState> m1 = spy(original);
        Middleware<TestState> m2 = spy(original);
        Middleware<TestState> m3 = spy(original);

        Store<TestState> store = Store.create(reducer, initialState,
                m1, m2, m3);
        store.subscribe(listener);
        Action action = new Action("test");
        InOrder inOrder = inOrder(m1, m2, m3);

        store.dispatch(action);

        inOrder.verify(m1).dispatch(eq(store), eq(action), any());
        inOrder.verify(m2).dispatch(eq(store), eq(action), any());
        inOrder.verify(m3).dispatch(eq(store), eq(action), any());
    }

}