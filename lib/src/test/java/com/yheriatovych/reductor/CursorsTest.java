package com.yheriatovych.reductor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class CursorsTest {

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
        when(reducer.reduce(any(), eq(new Action(Store.INIT_ACTION)))).thenReturn(initialState);
        store = Store.create(reducer, initialState);
        reset(reducer);
    }
    
    @Test
    public void testForEachPropagateInitialValue() throws Exception {
        Action action = new Action("TEST");
        TestState newState1 = new TestState();
        TestState newState2 = new TestState();
        when(reducer.reduce(any(), eq(action)))
                .thenReturn(newState1)
                .thenReturn(newState2);

        StateChangeListener<TestState> listener = Mockito.mock(StateChangeListener.class);
        Cursors.forEach(store, listener);

        store.dispatch(action);
        store.dispatch(action);

        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).onStateChanged(initialState);
        inOrder.verify(listener).onStateChanged(newState1);
        inOrder.verify(listener).onStateChanged(newState2);
        inOrder.verifyNoMoreInteractions();
    }


    class ComplexState {
        int foo;
        int bar;
    }

    private Action update(int foo, int bar) {
        return Action.create("UPDATE", foo, bar);
    }

    @Test
    public void testFilterUniqueValuesOnMap() {
        Reducer<ComplexState> reducer = (state, action) -> {
            if(state == null) {
                state = new ComplexState();
            }
            if(action.type.equals("UPDATE")) {
                ComplexState nextState = new ComplexState();
                nextState.foo = (int) action.getValue(0);
                nextState.bar = (int) action.getValue(1);
                return nextState;
            }
            return state;
        };

        Store<ComplexState> store = Store.create(reducer);

        Cursor<Integer> map = Cursors.map(store, state -> state.foo + state.bar);
        StateChangeListener<Integer> listener = mock(StateChangeListener.class);
        InOrder inOrder = inOrder(listener);

        map.subscribe(listener);

        store.dispatch(update(1,0));
        inOrder.verify(listener).onStateChanged(1);

        store.dispatch(update(1,1));
        inOrder.verify(listener).onStateChanged(2);

        store.dispatch(update(2,0));
        store.dispatch(update(0,2));
        store.dispatch(update(2,2));

        inOrder.verify(listener).onStateChanged(4);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testMappedCursorReturnMappedValue() {
        Cursor<Integer> cursor = Cursors.map(store, state -> 42);
        Assert.assertEquals(42, cursor.getState().intValue());
    }
}
