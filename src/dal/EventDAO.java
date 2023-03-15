package dal;

import be.Event;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        statement.setDate(2, event.getStartingDate());
        statement.setTime(3, event.getStartingTime());
        statement.setString(4, event.getLocation());
        statement.setString(5, event.getNotes());
        statement.setDate(6, event.getEndingDate());
        statement.setTime(7, event.getEndingTime());
        statement.setString(8, event.getLocationGuidance());
    }
}
