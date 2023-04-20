package tests;

//import org.junit.Test;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.dal.EventDAO;
import ticketSystemEASV.dal.TicketDAO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;

//import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicketTests {
    private TicketDAO ticketDAO = new TicketDAO();
    @Test
    public void addTicket() {
        var event = new Event("Test",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                10,
                "location",
                "description",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                "Well");
        new EventDAO().addEvent(event);
        var customer = new Customer("Test",
                "test@test.dk");
        var ticket = new Ticket(event, customer, null);
        ticketDAO.addTicket(ticket);
        assertTrue(ticket.getId() != null);
    }
    @Test
    public void deleteTicket() {
        var event = new Event("Test",
                new Date(2023, 12, 12),
                new Time(12, 20, 20),
                10,
                "location",
                "description",
                new Date(2023, 12, 12),
                new Time(12, 20, 20),
                "Well");
        new EventDAO().addEvent(event);
        var customer = new Customer("Test",
                "test@test.dk");
        var ticket = new Ticket(event, customer, null);
        ticketDAO.addTicket(ticket);
        ticketDAO.deleteTicket(ticket);
        assertTrue(ticket.getId() == null);
    }
    @Test
    public void getTicket(){
        var event = new Event("Test",
                new Date(2023, 12, 12),
                new Time(12, 20, 20),
                10,
                "location",
                "description",
                new Date(2023, 12, 12),
                new Time(12, 20, 20),
                "Well");
        new EventDAO().addEvent(event);
        var customer = new Customer("Test",
                "test@test.dk");
        var ticket = new Ticket(event, customer, null);
        ticketDAO.addTicket(ticket);
        var ticket2 = ticketDAO.getTicket(ticket.getId());
        assertTrue(ticket2.getId() == ticket.getId());
    }
}
