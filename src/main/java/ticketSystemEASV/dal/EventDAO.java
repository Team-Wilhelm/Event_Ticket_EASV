package ticketSystemEASV.dal;

import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.EventCoordinator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EventDAO {
    private final DBConnection dbConnection = new DBConnection();

    public void addEvent(Event event) {
        String sql = "INSERT INTO Event (coordinatorId, startDate, startTime, eventName, eventLocation, notes, endDate, endTime, locationGuidance) " +
                "VALUES (?,?,?,?,?,?,?,?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(event, statement);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {
        String sql = "UPDATE Event SET coordinatorId=?, startDate=?, startTime=?, eventName=?, eventLocation=?, notes=?, endDate=?, endTime=?, locationGuidance=? WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(event, statement);
            statement.setInt(10, event.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(Event eventToDelete) {
        String sql = "UPDATE Event SET deleted=1 WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, eventToDelete.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillPreparedStatement(Event event, PreparedStatement statement) throws SQLException {
        statement.setString(1, event.getCoordinatorId().toString());
        statement.setDate(2, event.getStartDate());
        statement.setTime(3, event.getStartTime());
        statement.setString(4, event.getEventName());
        statement.setString(5, event.getLocation());
        statement.setString(6, event.getNotes());
        statement.setDate(7, event.getEndDate());
        statement.setTime(8, event.getEndTime());
        statement.setString(9, event.getLocationGuidance());
    }

    public Collection<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE deleted=0;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                events.add(constructEvent(resultSet));
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Event getEvent(int eventID) {
        String sql = "SELECT * FROM Event WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, eventID);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                return constructEvent(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getEventsAssignedToEventCoordinator(EventCoordinator eventCoordinator){
        String sql = "SELECT eventID FROM Event_Coordinator_Link WHERE coordinatorID=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, eventCoordinator.getId().toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                int eventID = resultSet.getInt("eventID");
                Event event = getEvent(eventID);
                eventCoordinator.getAssignedEvents().add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Event constructEvent(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        UUID coordinatorId = (UUID.fromString(resultSet.getString("coordinatorId")));
        String eventName = resultSet.getString("eventName");
        Date startDate = resultSet.getDate("startDate");
        Time startTime = resultSet.getTime("startTime");
        String location = resultSet.getString("eventLocation");
        String notes = resultSet.getString("notes");
        Date endDate = resultSet.getDate("endDate");
        Time endTime = resultSet.getTime("endTime");
        String locationGuidance = resultSet.getString("locationGuidance");
        return new Event(id, coordinatorId, eventName, startDate, startTime, location, notes, endDate, endTime, locationGuidance);
    }

    public List<Event> searchEvents(String query) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE eventName LIKE ? OR eventLocation LIKE ?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                events.add(constructEvent(resultSet));
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
