package com.yheriatovych.reductor.example.reductor.notelist;

import com.yheriatovych.reductor.Action;
import com.yheriatovych.reductor.annotations.ActionCreator;

@ActionCreator
public interface NotesActions {
    String ADD_ACTION = "ADD_ITEM";
    String TOGGLE = "TOGGLE";
    String REMOVE_ITEM = "REMOVE_ITEM";

    @ActionCreator.Action(NotesActions.ADD_ACTION)
    Action add(int id, String content);

    @ActionCreator.Action(NotesActions.REMOVE_ITEM)
    Action remove(int id);

    @ActionCreator.Action(NotesActions.TOGGLE)
    Action toggle(int id);
}
