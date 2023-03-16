package bll;

import be.Event;
import dal.CustomerDAO;
import dal.EventDAO;
import dal.TicketDAO;

public class LogicManager {
    private final EventDAO eventDAO = new EventDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public void saveEvent(Event eventToSave) {
        eventDAO.addEvent(eventToSave);
    }

    public void updateEvent(Event eventToUpdate) {
        eventDAO.updateEvent(eventToUpdate);
    }

    public void deleteEvent(Event eventToDelete) {
        eventDAO.deleteEvent(eventToDelete);
    }
}
