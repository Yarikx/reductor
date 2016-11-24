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
        Middleware.Dispatcher dispatcher = mock(Middleware.Dispatcher.class);
        Middleware<TestState> middleware = mock(Middleware.class);
        when(middleware.create(any(), any())).thenReturn(dispatcher);
        Store store = Store.create(reducer, initialState, middleware);
        store.dispatch(action);
        verify(dispatcher).call(action);
    }

    @Test
    public void testDoNotChangeStateWhenMiddlewareDoNothing() {
        Store<TestState> store = Store.create(reducer, initialState, (Middleware<TestState>) (store1, nextDispatcher) -> action -> {
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
                (Middleware<TestState>) (store1, nextDispatcher) -> nextDispatcher::call);
        store.subscribe(listener);
        Action action = new Action("test");

        store.dispatch(action);

        verify(listener).onStateChanged(nextState);
    }

    @Test
    public void testDispatchActionsInOrder() {

        Runnable action1 = mock(Runnable.class);
        Runnable action2 = mock(Runnable.class);
        Runnable action3 = mock(Runnable.class);

        Middleware<TestState> m1 = create(action1);
        Middleware<TestState> m2 = create(action2);
        Middleware<TestState> m3 = create(action3);

        Store<TestState> store = Store.create(reducer, initialState,
                m1, m2, m3);
        store.subscribe(listener);
        Action action = new Action("test");
        InOrder inOrder = inOrder(action1, action2, action3);

        store.dispatch(action);

        inOrder.verify(action1).run();
        inOrder.verify(action2).run();
        inOrder.verify(action3).run();
    }

    private Middleware<TestState> create(Runnable runnable) {
        return (store, nextDispatcher) -> action -> {
            runnable.run();
            nextDispatcher.call(action);
        };
    }

}