package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.bll.TicketManager;
import ticketSystemEASV.dal.EventDAO;

import java.util.List;
import java.util.UUID;

public class TicketModel {
    private final TicketManager bll = new TicketManager();
    private static EventDAO eventDAO = new EventDAO();

    public TicketModel() {

        /*saveEvent(new Event(allEventCoordinators.get(0).getId(), "UTTT Tournament!", Date.valueOf("2023-04-01"), new Time(7, 0, 0),
                "Innovatorium", "Attendees are all losers", Date.valueOf("2023-04-08"),
                new Time(24, 30, 0), "Use your god damn feet!"));

        saveEvent(new Event(allEventCoordinators.get(0).getId(),"RPS Tournament!", Date.valueOf("2023-12-24"), new Time(9, 0, 0),
                "EASV Bar", "No notes", Date.valueOf("2023-12-24"),
                new Time(17, 45, 0), "Private jet to the moon"));

        saveEvent(new Event(allEventCoordinators.get(0).getId(),"Ornithology 101", Date.valueOf("2012-12-20"), new Time(12, 12, 12),
                "Basement restroom", "Oh my god Beckeighhh", Date.valueOf("9999-08-01"),
                new Time(22, 22, 22), "Don't forget to bring your best ears :)"));*/
    }

    public void generateTicket(Event event, Customer customer) {
        bll.addTicket(new Ticket(event, customer));
    }

    public static void main(String[] args){
        TicketModel ticketModel = new TicketModel();
        Ticket ticket1 = new Ticket(UUID.randomUUID(), List.copyOf(eventDAO.getAllEvents()).get(0), new Customer("Beckeigh", "beckeigh@nielsen.dk"), "I'm a loser", "No QR");
        //ticketModel.bll.addTicket(ticket1);
    }
}
