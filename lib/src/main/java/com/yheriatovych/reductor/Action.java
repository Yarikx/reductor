package com.yheriatovych.reductor;

import java.util.Arrays;

/**
 * Minimal representation of change to be performed on state
 */
public class Action {
    public final String type;
    public final Object value;

    /**
     * Create Action object with specified type and value
     *
     * @param type  String type of action, will be used by {@link Reducer} for dispatch
     * @param value any payload to be included with this value
     */
    public Action(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Create Action with defined type and null value
     *
     * @param type String type of action, will be used by {@link Reducer} for dispatch
     */
    public Action(String type) {
        this(type, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (type != null ? !type.equals(action.type) : action.type != null) return false;
        return value != null ? value.equals(action.value) : action.value == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        Object valueStr = (this.value instanceof Object[])
                ? Arrays.toString((Object[]) this.value)
                : this.value;

        return "Action{" +
                "type='" + type + '\'' +
                ", value=" + valueStr +
                '}';
    }
}
