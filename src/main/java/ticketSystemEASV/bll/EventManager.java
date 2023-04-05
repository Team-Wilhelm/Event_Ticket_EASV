package ticketSystemEASV.bll;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.dal.EventDAO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final EventDAO eventDAO = new EventDAO();

    public void saveEvent(Event eventToSave) {
        eventDAO.addEvent(eventToSave);
    }

    public void updateEvent(Event eventToUpdate) {
        eventDAO.updateEvent(eventToUpdate);
    }

    public void deleteEvent(Event eventToDelete) {
        eventDAO.deleteEvent(eventToDelete);
    }

    public Map<Integer, Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }

    public List<Event> searchEvents(String query) {
        return eventDAO.searchEvents(query);
    }
}
