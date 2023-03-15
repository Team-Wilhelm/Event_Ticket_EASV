package gui.model;

import be.Event;
import bll.LogicManager;

public class Model {
    private final LogicManager bll = new LogicManager();
    public void deleteEvent(Event eventToDelete) {
        bll.deleteEvent(eventToDelete);
    }
}
