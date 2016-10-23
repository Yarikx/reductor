package com.yheriatovych.reductor;

import java.util.Arrays;

/**
 * Minimal representation of change to be performed on state
 */
public class Action {
    public final String type;
    public final Object[] values;

    /**
     * Create Action object with specified type and value
     *
     * @param type  String type of action, will be used by {@link Reducer} for dispatch
     * @param value any payload to be included with this value
     */
    public Action(String type, Object[] values) {
        this.type = type;
        this.values = values;
    }

    /**
     * Create Action with defined type and null value
     *
     * @param type String type of action, will be used by {@link Reducer} for dispatch
     */
    public Action(String type) {
        this(type, new Object[0]);
    }

    public static Action create(String type, Object... values) {
        return new Action(type, values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (type != null ? !type.equals(action.type) : action.type != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(values, action.values);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public String toString() {
        return "Action{" +
                "type='" + type + '\'' +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
