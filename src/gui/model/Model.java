package gui.model;

import be.Event;
import bll.LogicManager;

public class Model {
    private final LogicManager bll = new LogicManager();

    public void saveEvent(Event eventToSave) {
        bll.saveEvent(eventToSave);
    }

    public void updateEvent(Event eventToUpdate) {
        bll.updateEvent(eventToUpdate);
    }

    public void deleteEvent(Event eventToDelete) {
        bll.deleteEvent(eventToDelete);
    }
}
