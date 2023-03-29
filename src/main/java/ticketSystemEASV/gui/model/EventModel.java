package ticketSystemEASV.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.bll.EventManager;

import java.util.List;

public class EventModel {
    private final EventManager eventManager = new EventManager();
    private ObservableList<Event> allEvents = FXCollections.observableArrayList();

    public EventModel() {
        allEvents.addAll(eventManager.getAllEvents());
    }

    public void saveEvent(Event eventToSave) {
        eventManager.saveEvent(eventToSave);
    }

    public void updateEvent(Event eventToUpdate) {
        eventManager.updateEvent(eventToUpdate);
    }

    public void deleteEvent(Event eventToDelete) {
        eventManager.deleteEvent(eventToDelete);
    }

    public ObservableList<Event> getAllEvents() {
        allEvents.setAll(eventManager.getAllEvents());
        return allEvents;
    }

    public List<Event> searchEvents(String query) {
        return eventManager.searchEvents(query);
    }
}
