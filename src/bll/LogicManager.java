package bll;

import be.Event;
import dal.EventDAO;

public class LogicManager {
    private final EventDAO eventDAO = new EventDAO();
    public void deleteEvent(Event eventToDelete) {
        eventDAO.deleteEvent(eventToDelete);
    }
}
