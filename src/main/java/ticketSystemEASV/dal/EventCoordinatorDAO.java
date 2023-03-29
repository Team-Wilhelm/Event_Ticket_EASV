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
    private EventDAO eventDAO = new EventDAO();

    public Collection<EventCoordinator> getAllEventCoordinators() {
        List<EventCoordinator> eventCoordinators = new ArrayList<>();
        String sql = "SELECT [User].Id, [User].Name, [User].Username, [User].Password FROM [User] " +
                "JOIN UserRole ON [User].ID = UserRole.UserID, " +
                "(SELECT Id FROM Role WHERE RoleName LIKE 'EventCoordinator') Role " +
                "WHERE [User].deleted=0 AND UserRole.RoleID = Role.ID;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                eventCoordinators.add(constructEventCoordinator(resultSet));
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
                "VALUES (@UserID,(SELECT Id FROM Role WHERE RoleName LIKE 'EventCoordinator'));";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(statement, eventCoordinator);
            statement.setString(4, eventCoordinator.getUsername());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEventCoordinator(EventCoordinator eventCoordinator){
        String sql = "UPDATE [User] SET name=?, userName=?, password=? WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(statement, eventCoordinator);
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

    public void getEventsAssignedToEventCoordinator(EventCoordinator eventCoordinator){
        String sql = "SELECT eventID FROM User_Event_Link WHERE userID=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, eventCoordinator.getId().toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                int eventID = resultSet.getInt("eventID");
                Event event = eventDAO.getEvent(eventID);
                eventCoordinator.getAssignedEvents().add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public EventCoordinator getEventCoordinator(UUID id){
        String sql = "SELECT [User].Id, [User].Name, [User].Username, [User].Password FROM [User] " +
                "JOIN UserRole ON [User].ID = UserRole.UserID, " +
                "(SELECT Id FROM Role WHERE RoleName LIKE 'EventCoordinator') Role " +
                "WHERE [User].deleted=0 AND UserRole.RoleID = Role.ID AND [User].id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return constructEventCoordinator(resultSet);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<EventCoordinator> searchCoordinators(String query) {
        List<EventCoordinator> coordinators = new ArrayList<>();
        String sql = "SELECT * FROM [User] WHERE [User].Name LIKE ? OR [User].UserName LIKE ? OR Id LIKE ?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            statement.setString(3, "%" + query + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                coordinators.add(constructEventCoordinator(resultSet));
            }
            return coordinators;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private EventCoordinator constructEventCoordinator(ResultSet resultSet) throws SQLException {
        UUID id = (UUID.fromString(resultSet.getString("id")));
        String name = resultSet.getString("Name");
        String username = resultSet.getString("UserName");
        String password = resultSet.getString("Password");
        EventCoordinator coordinator = new EventCoordinator(id, name, username, password);
        getEventsAssignedToEventCoordinator(coordinator);
        return coordinator;
    }

    private void fillPreparedStatement(PreparedStatement statement, EventCoordinator eventCoordinator) throws SQLException {
        statement.setString(1, eventCoordinator.getName());
        statement.setString(2, eventCoordinator.getUsername());
        statement.setString(3, eventCoordinator.getPassword());
    }
}


