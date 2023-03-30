package ticketSystemEASV.dal;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.User;
import ticketSystemEASV.gui.model.UserModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EventDAO {
    private final DBConnection dbConnection = new DBConnection();

    public void addEvent(Event event) {
        UserDAO userDAO = new UserDAO();
        User loggedInUser = userDAO.getUserByEmail("admin");
        //TODO unique Event names?
        //TODO be able to add multiple coordinators for one event (fucking pain in the ass, i think, respectfully)
        String sql = "DECLARE @EventID int;" +
                "INSERT INTO Event (startDate, startTime, eventName, eventLocation, notes, endDate, endTime, locationGuidance) " +
                "VALUES (?,?,?,?,?,?,?,?) " +
                "SET @EventID = (SELECT ID FROM Event WHERE EventName = ?)" +
                "INSERT INTO User_Event_Link (UserID, EventId)" +
                "VALUES (?, @EventID);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(event, statement);
            statement.setString(9, event.getEventName());
            statement.setString(10, loggedInUser.getId().toString()); //TODO logged in users
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {
        String sql = "UPDATE Event SET startDate=?, startTime=?, eventName=?, eventLocation=?, notes=?, endDate=?, endTime=?, locationGuidance=? WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(event, statement);
            statement.setInt(9, event.getId());
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
        statement.setDate(1, event.getStartDate());
        statement.setTime(2, event.getStartTime());
        statement.setString(3, event.getEventName());
        statement.setString(4, event.getLocation());
        statement.setString(5, event.getNotes());
        statement.setDate(6, event.getEndDate());
        statement.setTime(7, event.getEndTime());
        statement.setString(8, event.getLocationGuidance());
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

    public Collection<User> getCoordinatorsAssignedToEvent(int eventID) {
        UserDAO userDAO = new UserDAO();
        List<User> eventCoordinators = new ArrayList<>();
        String sql = "SELECT * FROM User_Event_Link WHERE EventID=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, eventID);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                eventCoordinators.add(userDAO.getUser(UUID.fromString(resultSet.getString("UserID"))));
            }
            return eventCoordinators;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Event constructEvent(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String eventName = resultSet.getString("eventName");
        Date startDate = resultSet.getDate("startDate");
        Time startTime = resultSet.getTime("startTime");
        String location = resultSet.getString("eventLocation");
        String notes = resultSet.getString("notes");
        Date endDate = resultSet.getDate("endDate");
        Time endTime = resultSet.getTime("endTime");
        String locationGuidance = resultSet.getString("locationGuidance");
        return new Event(id, eventName, startDate, startTime, location, notes, endDate, endTime, locationGuidance);
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
