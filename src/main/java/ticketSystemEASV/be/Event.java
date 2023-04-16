package ticketSystemEASV.be;

import java.sql.Date;
import java.sql.Time;
import java.util.*;

public class Event {
    //Required information
    private int id;
    private Date startDate;
    private Time startTime;
    private String eventName, location, notes;


    //Optional information
    private Date endDate;
    private Time endTime;
    private String locationGuidance;

    //Tickets
    private HashMap<UUID, Ticket> tickets;
    private List<Voucher> vouchers;

    public Event(String eventName, Date startDate, Time startTime, String location, String notes, Date endDate, Time endTime, String locationGuidance) {
        this.eventName = eventName;
        this.startDate = startDate;
        this.startTime = startTime;
        this.location = location;
        this.notes = notes;
        this.endDate = endDate;
        this.endTime = endTime;
        this.locationGuidance = locationGuidance;
        this.tickets = new HashMap<>();
    }

    public Event(int id, String eventName, Date startDate, Time startTime, String location, String notes, Date endDate, Time endTime, String locationGuidance){
        this(eventName, startDate, startTime, location, notes, endDate, endTime, locationGuidance);
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startingTime) {
        this.startTime = startingTime;
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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
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

    public void setId(int id) {
        this.id = id;
    }

    public void addTicket(Ticket ticket){
        tickets.put(ticket.getId(), ticket);
    }

    public void removeTicket(Ticket ticket){
        tickets.remove(ticket.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setTickets(HashMap<UUID, Ticket> tickets) {
        this.tickets = tickets;
        tickets.forEach((uuid, ticket) -> ticket.setEvent(this));
    }

    public HashMap<UUID, Ticket> getTickets() {
        return tickets;
    }

    public void setVouchers(List<Voucher> vouchers) {
        this.vouchers = vouchers;
    }

    public List<Voucher> getVouchers() {
        return vouchers;
    }
}
