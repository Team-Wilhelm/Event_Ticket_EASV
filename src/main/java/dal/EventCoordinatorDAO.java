package dal;

import be.Event;
import be.EventCoordinator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EventCoordinatorDAO {
    private final DBConnection dbConnection = new DBConnection();

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
                eventCoordinators.add(new EventCoordinator(id, name, username, password));
            }
            return eventCoordinators;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}


