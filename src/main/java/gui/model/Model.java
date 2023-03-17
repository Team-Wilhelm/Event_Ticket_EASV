package gui.model;

import be.Event;
import be.EventCoordinator;
import bll.LogicManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final LogicManager bll = new LogicManager();
    private final List<EventCoordinator> allEventCoordinators = new ArrayList<>();
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
    }

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

    public List<EventCoordinator> getAllEventCoordinators() {
        return allEventCoordinators;
    }
}
