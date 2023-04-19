package tests;

import org.junit.Test;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.dal.EventDAO;

import java.sql.Time;
import java.sql.Date;

import static org.junit.Assert.assertTrue;

public class EventTests {
    private EventDAO eventDAO = new EventDAO();
    @Test
    public void addEvent() {
        var event = new Event("Test",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                10,
                "location",
                "description",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                "Well");
        eventDAO.addEvent(event);
        assertTrue(event.getId() > 0);
    }
    @Test
    public void updateEvent() {
        var event = new Event("Test",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                10,
                "location",
                "description",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                "Well");
        eventDAO.addEvent(event);
        event.setEventName("Test2");
        eventDAO.updateEvent(event);
        var event2 = eventDAO.getEvent(event.getId());
        assertTrue(event2.getEventName().equals("Test2"));
    }
    @Test
    public void deleteEvent() {
        var event = new Event("Test",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                10,
                "location",
                "description",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                "Well");
        eventDAO.addEvent(event);
        eventDAO.deleteEvent(event);
        assertTrue(event.getId() == 0);
    }

    @Test
    public void getEvent() {
        var event = new Event("Test",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                10,
                "location",
                "description",
                new Date(2023, 12, 12),
                new Time(12,20,20),
                "Well");
        eventDAO.addEvent(event);
        var event2 = eventDAO.getEvent(event.getId());
        assertTrue(event2.getId() == event.getId());
    }
}
