package com.yheriatovych.reductor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class StoreTest {

    private static class TestState {

    }

    TestState initialState;
    @Mock
    Reducer<TestState> reducer;
    Store<TestState> store;

    @Before
    public void setUp() {
        initialState = new TestState();
        MockitoAnnotations.initMocks(this);
        store = Store.create(reducer, initialState);
    }

    @Test
    public void testDispatchActionToReducer() {
        Action action = new Action("TEST");
        store.dispatch(action);

        verify(reducer).reduce(initialState, action);
    }

    @Test
    public void testStateIsSavedToStore() {
        Action action = new Action("TEST");
        TestState newState = new TestState();
        when(reducer.reduce(any(), eq(action))).thenReturn(newState);

        store.dispatch(action);

        assertEquals(newState, store.getState());
    }

    @Test
    public void testStatePropagatedToListener() {
        Action action = new Action("TEST");
        TestState newState1 = new TestState();
        TestState newState2 = new TestState();
        when(reducer.reduce(any(), eq(action)))
                .thenReturn(newState1)
                .thenReturn(newState2);

        Store.StateChangeListener<TestState> listener = Mockito.mock(Store.StateChangeListener.class);
        store.subscribe(listener);

        store.dispatch(action);
        store.dispatch(action);

        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).onStateChanged(newState1);
        inOrder.verify(listener).onStateChanged(newState2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDoNotPropagateResultToListenerAfterCancel() {
        Action action = new Action("TEST");
        TestState newState1 = new TestState();
        TestState newState2 = new TestState();
        when(reducer.reduce(any(), eq(action)))
                .thenReturn(newState1)
                .thenReturn(newState2);

        Store.StateChangeListener<TestState> listener = Mockito.mock(Store.StateChangeListener.class);
        Cancelable cancelable = store.subscribe(listener);

        store.dispatch(action);
        cancelable.cancel();
        store.dispatch(action);

        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).onStateChanged(newState1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionIfActionIsNotSupported() {
        store.dispatch("action");
    }

}