package com.yheriatovych.reductor.example.model;

public class Note {
    public final int id;
    public final String note;
    public final boolean checked;

    public Note(int id, String note, boolean checked) {
        this.id = id;
        this.note = note;
        this.checked = checked;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", checked ? "+" : " ", note);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note1 = (Note) o;

        if (id != note1.id) return false;
        if (checked != note1.checked) return false;
        return note != null ? note.equals(note1.note) : note1.note == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        return result;
    }
}
