package ticketSystemEASV.dal;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.EventCoordinator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EventCoordinatorDAO {
    private final DBConnection dbConnection = new DBConnection();
    EventDAO eventDAO = new EventDAO();

    public Collection<EventCoordinator> getAllEventCoordinators() {
        List<EventCoordinator> eventCoordinators = new ArrayList<>();
        String sql = "SELECT * FROM Event_Coordinator;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID id = (UUID.fromString(resultSet.getString("id")));
                String name = resultSet.getString("coordinatorName");
                String username = resultSet.getString("userName");
                String password = resultSet.getString("coordinatorPassword");
                EventCoordinator coordinator = new EventCoordinator(id, name, username, password);
                eventDAO.getEventsAssignedToEventCoordinator(coordinator);
                eventCoordinators.add(coordinator);
            }
            return eventCoordinators;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addEventCoordinator(EventCoordinator eventCoordinator) {
        String sql = "INSERT INTO Event_Coordinator (coordinatorName, userName, coordinatorPassword) " +
                "VALUES (?,?,?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, eventCoordinator.getName());
            statement.setString(2, eventCoordinator.getUsername());
            statement.setString(3, eventCoordinator.getPassword());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEventCoordinator(EventCoordinator eventCoordinator){
        String sql = "UPDATE Event_Coordinator SET deleted=1 WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, eventCoordinator.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


