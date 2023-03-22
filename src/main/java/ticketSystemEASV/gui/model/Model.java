package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.*;
import ticketSystemEASV.be.views.TicketView;
import ticketSystemEASV.bll.LogicManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.UUID;

public class Model {
    private final LogicManager bll = new LogicManager();
    private final ObservableList<EventCoordinator> allEventCoordinators = FXCollections.observableArrayList();
    private ObservableList<Event> allEvents = FXCollections.observableArrayList();

    public Model() {
        allEventCoordinators.addAll(bll.getAllEventCoordinators());
        allEvents.addAll(bll.getAllEvents());

        /*saveEvent(new Event(allEventCoordinators.get(0).getId(), "UTTT Tournament!", Date.valueOf("2023-04-01"), new Time(7, 0, 0),
                "Innovatorium", "Attendees are all losers", Date.valueOf("2023-04-08"),
                new Time(24, 30, 0), "Use your god damn feet!"));

        saveEvent(new Event(allEventCoordinators.get(0).getId(),"RPS Tournament!", Date.valueOf("2023-12-24"), new Time(9, 0, 0),
                "EASV Bar", "No notes", Date.valueOf("2023-12-24"),
                new Time(17, 45, 0), "Private jet to the moon"));

        saveEvent(new Event(allEventCoordinators.get(0).getId(),"Ornithology 101", Date.valueOf("2012-12-20"), new Time(12, 12, 12),
                "Basement restroom", "Oh my god Beckeighhh", Date.valueOf("9999-08-01"),
                new Time(22, 22, 22), "Don't forget to bring your best ears :)"));*/

        Ticket ticket1 = new Ticket(UUID.randomUUID() , allEvents.get(0), new Customer("Beckeigh", "beckeigh@nielsen.dk"), "I'm a loser", "No QR");
        Ticket ticket2 = new Ticket(UUID.randomUUID(), allEvents.get(4), new Customer("Ashghhleigh", "real@mail.com"), "I'm a winner", "No QR");
        TicketView ticketView = new TicketView();
        //ticketView.generateTicket(ticket2);
        //ticketView.generateTicket(ticket1);
    }

    //region Event CRUD
    public void saveEvent(Event eventToSave) {
        /*
        //In order to get the proper id (temporary)
        allEvents.sort(Comparator.comparingInt(Event::getId));
        eventToSave.setId(allEvents.get(allEvents.size() - 1).getId() + 1);
        allEvents.add(eventToSave);
         */
        bll.saveEvent(eventToSave);
    }

    public void updateEvent(Event eventToUpdate) {
        bll.updateEvent(eventToUpdate);
    }

    public void deleteEvent(Event eventToDelete) {
        bll.deleteEvent(eventToDelete);
    }

    public ObservableList<Event> getAllEvents() {
        allEvents.setAll(bll.getAllEvents());
        return allEvents;
    }
    //endregion

    //region EventCoordinator CRUD
    public void addCoordinator(EventCoordinator coordinatorToSave) {
        bll.addCoordinator(coordinatorToSave);
    }

    public void updateCoordinator(EventCoordinator coordinatorToUpdate) {
        bll.updateCoordinator(coordinatorToUpdate);
    }
    public void deleteCoordinator(EventCoordinator coordinatorToDelete) {
        bll.deleteCoordinator(coordinatorToDelete);
    }

    public List<EventCoordinator> getAllEventCoordinators() {
        allEventCoordinators.setAll(bll.getAllEventCoordinators());
        return allEventCoordinators;
    }

    public List<Event> searchEvents(String query) {
        return bll.searchEvents(query);
    }
    //endregion
}
