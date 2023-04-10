package ticketSystemEASV.bll;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.User;
import ticketSystemEASV.dal.EventDAO;
import ticketSystemEASV.gui.model.UserModel;

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

    public String deleteEvent(Event eventToDelete) {
        return eventDAO.deleteEvent(eventToDelete);
    }

    public Map<Integer, Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }

    public void getEventsAssignedToEventCoordinator() {
        eventDAO.getEventsAssignedToEventCoordinator(UserModel.getLoggedInUser());
    }

    public void assignCoordinatorToEvent(User user, Event event){
        eventDAO.assignCoordinatorToEvent(user, event);
    }

    public List<Event> searchEvents(String query) {
        return eventDAO.searchEvents(query);
    }

    public Event getEvent(int id) {
        return eventDAO.getEvent(id);
    }

    public void unassignCoordinatorFromEvent(User user, Event event) {
        eventDAO.unassignCoordinatorFromEvent(user, event);
    }
}
