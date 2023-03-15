package be;

import java.sql.Date;
import java.sql.Time;

public class Event {
    //Required information
    int id;
    Date startingDate;
    Time startingTime;
    String eventName, location, notes;


    //Optional information
    Date endingDate;
    Time endingTime;
    String locationGuidance;

    public Event(int id, String eventName, Date startingDate, Time startingTime, String location, String notes) {
        this(eventName, startingDate, startingTime, location, notes);
        this.id = id;
    }

    public Event(String eventName, Date startingDate, Time startingTime, String location, String notes) {
        this.eventName = eventName;
        this.startingDate = startingDate;
        this.startingTime = startingTime;
        this.location = location;
        this.notes = notes;
    }

    public Event(String eventName, Date startingDate, Time startingTime, String location, String notes, Date endingDate, Time endingTime, String locationGuidance){
        this(eventName, startingDate, startingTime, location, notes);
        this.endingDate = endingDate;
        this.endingTime = endingTime;
        this.locationGuidance = locationGuidance;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Time getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Time startingTime) {
        this.startingTime = startingTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public Time getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(Time endingTime) {
        this.endingTime = endingTime;
    }

    public String getLocationGuidance() {
        return locationGuidance;
    }

    public void setLocationGuidance(String locationGuidance) {
        this.locationGuidance = locationGuidance;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getId() {
        return id;
    }
}
