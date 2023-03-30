package ticketSystemEASV.bll;

import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.dal.TicketDAO;

import java.util.List;
import java.util.UUID;

public class TicketManager {
    private final TicketDAO ticketDAO = new TicketDAO();

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
}
