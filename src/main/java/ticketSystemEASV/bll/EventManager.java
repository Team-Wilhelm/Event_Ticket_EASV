package ticketSystemEASV.bll;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.dal.EventDAO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final EventDAO eventDAO = new EventDAO();

    public String saveEvent(Event eventToSave) {
        return eventDAO.addEvent(eventToSave);
    }

    public String updateEvent(Event eventToUpdate) {
        return eventDAO.updateEvent(eventToUpdate);
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

    public Event getEvent(int id) {
        return eventDAO.getEvent(id);
    }
}
