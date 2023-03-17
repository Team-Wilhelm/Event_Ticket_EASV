package bll;

import be.Customer;
import be.Event;
import be.EventCoordinator;
import be.Ticket;
import dal.CustomerDAO;
import dal.EventCoordinatorDAO;
import dal.EventDAO;
import dal.TicketDAO;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

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


    public void addTicket(Ticket ticketToAdd) {
        ticketDAO.addTicket(ticketToAdd);
    }
    public void addMultipleTickets(List<Ticket> tickets, Customer customer) {
        ticketDAO.addMultipleTickets(tickets, customer);
    }
    public Ticket getTicket(UUID id) {
        return ticketDAO.getTicket(id);
    }
    public List<Ticket> getAllTicketsForEvent(Event event) {
        return ticketDAO.getAllTicketsForEvent(event);
    }
    public List<Ticket> getAllTicketsForCustomer(Customer customer) {
        return ticketDAO.getAllTicketsForCustomer(customer);
    }
    public void deleteTicket(Ticket ticketToDelete) {
        ticketDAO.deleteTicket(ticketToDelete);
    }


    public void createEventCoordinator(EventCoordinator eventCoordinatorToCreate){
        eventCoordinatorDAO.addEventCoordinator(eventCoordinatorToCreate);
    }

    public void deleteEventCoordinator(EventCoordinator eventCoordinatorToDelete){
        eventCoordinatorDAO.deleteEventCoordinator(eventCoordinatorToDelete);
    }

    public Collection<EventCoordinator> getAllEventCoordinators(){
        return eventCoordinatorDAO.getAllEventCoordinators();
    }
}
