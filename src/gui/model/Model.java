package gui.model;

import be.Event;
import bll.LogicManager;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Model {
    private final LogicManager bll = new LogicManager();
    private List<Event> allEvents = new ArrayList<>();

    public Model() {
        allEvents.add(new Event(1,"Test", Date.valueOf("2023-04-01"), new Time(14, 0, 0), "EASV", "No notes", null, null, null));
    }

    public void saveEvent(Event eventToSave) {
        //In order to get the proper id (temporary)
        allEvents.sort(Comparator.comparingInt(Event::getId));
        eventToSave.setId(allEvents.get(allEvents.size() - 1).getId() + 1);
        allEvents.add(eventToSave);

        //bll.saveEvent(eventToSave);
    }

    public void updateEvent(Event eventToUpdate) {
        //Temporary to reflect changes in the tableview
        Event event = allEvents.stream().filter(e -> e.getId() == eventToUpdate.getId()).findFirst().orElse(null);
        event.setEventName(eventToUpdate.getEventName());
        event.setStartDate(eventToUpdate.getStartDate());
        event.setStartTime(eventToUpdate.getStartTime());
        event.setLocation(eventToUpdate.getLocation());
        event.setNotes(eventToUpdate.getNotes());
        event.setEndDate(eventToUpdate.getEndDate());
        event.setEndTime(eventToUpdate.getEndTime());
        event.setLocationGuidance(eventToUpdate.getLocationGuidance());

        //bll.updateEvent(eventToUpdate);
    }

    public void deleteEvent(Event eventToDelete) {
        allEvents.remove(eventToDelete);
        //bll.deleteEvent(eventToDelete);
    }

    public List<Event> getAllEvents() {
        return allEvents;
    }
}
