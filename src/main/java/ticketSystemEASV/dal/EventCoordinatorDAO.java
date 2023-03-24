package ticketSystemEASV.dal;

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
        String sql = "SELECT [User].Id, [User].Name, [User].Username, [User].Password FROM [User] " +
                "JOIN UserRole ON [User].ID = UserRole.UserID, " +
                "(SELECT Id FROM Role WHERE NAME LIKE 'EventCoordinator') Role " +
                "WHERE [User].deleted=0 AND UserRole.RoleID = Role.ID;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID id = (UUID.fromString(resultSet.getString("id")));
                String name = resultSet.getString("Name");
                String username = resultSet.getString("UserName");
                String password = resultSet.getString("Password");
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
        String sql = "DECLARE @UserID uniqueidentifier;" +
                "INSERT INTO [User] (name, userName, Password)" +
                "VALUES (?, ?, ?)" +
                "SET @UserID = (SELECT ID FROM [User] WHERE UserName = ?)" +
                "INSERT INTO UserRole (UserID, RoleID)" +
                "VALUES (@UserID,(SELECT Id FROM Role WHERE NAME LIKE 'EventCoordinator'));";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, eventCoordinator.getName());
            statement.setString(2, eventCoordinator.getUsername());
            statement.setString(3, eventCoordinator.getPassword());
            statement.setString(4, eventCoordinator.getUsername());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEventCoordinator(EventCoordinator eventCoordinator){
        String sql = "UPDATE [User] SET name=?, userName=?, password=? WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, eventCoordinator.getName());
            statement.setString(2, eventCoordinator.getUsername());
            statement.setString(3, eventCoordinator.getPassword());
            statement.setString(4, eventCoordinator.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEventCoordinator(EventCoordinator eventCoordinator){
        String sql = "UPDATE [User] SET deleted=1 WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, eventCoordinator.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


