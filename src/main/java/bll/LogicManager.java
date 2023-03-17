package bll;

import be.Event;
import be.EventCoordinator;
import dal.CustomerDAO;
import dal.EventCoordinatorDAO;
import dal.EventDAO;
import dal.TicketDAO;

import java.util.Collection;

public class LogicManager {
    private final EventDAO eventDAO = new EventDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final EventCoordinatorDAO eventCoordinatorDAO = new EventCoordinatorDAO();

    public void saveEvent(Event eventToSave) {
        eventDAO.addEvent(eventToSave);
    }

    public void updateEvent(Event eventToUpdate) {
        eventDAO.updateEvent(eventToUpdate);
    }

    public void deleteEvent(Event eventToDelete) {
        eventDAO.deleteEvent(eventToDelete);
    }

    public Collection<Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }

    public Collection<EventCoordinator> getAllEventCoordinators(){
        return eventCoordinatorDAO.getAllEventCoordinators();
    }
}
