package dal;

import be.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventDAO {
    private final DBConnection dbConnection = new DBConnection();

    public void addEvent(Event event) {
        String sql = "INSERT INTO Event (eventName, startDate, startTime, location, notes, endDate, endTime, locationGuidance) " +
                "VALUES (?,?,?,?,?,?,?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(event, statement);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {
        String sql = "UPDATE Event SET eventName=?, startDate=?, startTime=?, location=?, notes=?, endDate=?, endTime=?, locationGuidance=? WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(event, statement);
            statement.setInt(9, event.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(Event eventToDelete) {
        //TODO constraints with other tables
        String sql = "DELETE FROM Event WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, eventToDelete.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillPreparedStatement(Event event, PreparedStatement statement) throws SQLException {
        statement.setString(1, event.getEventName());
        statement.setDate(2, event.getStartDate());
        statement.setTime(3, event.getStartTime());
        statement.setString(4, event.getLocation());
        statement.setString(5, event.getNotes());
        statement.setDate(6, event.getEndDate());
        statement.setTime(7, event.getEndTime());
        statement.setString(8, event.getLocationGuidance());
    }

    public Collection<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String eventName = resultSet.getString("eventName");
                Date startDate = resultSet.getDate("startDate");
                Time startTime = resultSet.getTime("startTime");
                String location = resultSet.getString("location");
                String notes = resultSet.getString("notes");
                Date endDate = resultSet.getDate("endDate");
                Time endTime = resultSet.getTime("endTime");
                String locationGuidance = resultSet.getString("locationGuidance");
                events.add(new Event(id, eventName, startDate, startTime, location, notes, endDate, endTime, locationGuidance));
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
