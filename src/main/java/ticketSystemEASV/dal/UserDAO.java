package ticketSystemEASV.dal;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.dal.Interfaces.IUserDAO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserDAO implements IUserDAO {
    private DBConnection dbConnection = new DBConnection();
    private EventDAO eventDAO = new EventDAO();

    public User getUser(UUID userID) {
        String sql = "SELECT * FROM [User] WHERE Id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, userID.toString());
            statement.execute();
            User user = new User(UUID.fromString(statement.getResultSet().getString("Id")),
                    statement.getResultSet().getString("Name"),
                    statement.getResultSet().getString("UserName"),
                    statement.getResultSet().getString("Password"),
                    null,
                    statement.getResultSet().getBytes("profilePicture"));
            assignRoleToUser(user);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isInRole(UUID userID, String role) {
        String sql = "SELECT * FROM UserRole\n" +
                "JOIN Role ON UserRole.RoleId = Role.Id\n" +
                "WHERE UserId=? AND Role.RoleName=?";

        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, userID.toString());
            statement.setString(2, role);
            statement.execute();
            return statement.getResultSet().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean logIn(String name, String password) {
        String sql = "SELECT * FROM [User] WHERE UserName=? AND Password=?";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, password);
            statement.execute();
            return statement.getResultSet().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void signUp(User user) {
        String sql = "DECLARE @UserID uniqueidentifier;" +
                "INSERT INTO [User] (name, userName, Password, profilePicture)" +
                "VALUES (?, ?, ?, ?)" +
                "SET @UserID = (SELECT ID FROM [User] WHERE UserName = ?)" +
                "INSERT INTO UserRole (UserID, RoleID)" +
                "VALUES (@UserID,(SELECT Id FROM Role WHERE RoleName LIKE 'EventCoordinator'));";

        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setBytes(4, user.getProfilePicture());
            statement.setString(5, user.getUsername());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        String sql = "UPDATE [User] SET name=?, userName=?, password=?, profilePicture=? WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(statement, user);
            statement.setString(5, user.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User user) {
        String sql = "UPDATE [User] SET deleted=1 WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM [User] RIGHT JOIN UserRole ON [User].Id = UserRole.UserId WHERE UserName=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, email);
            statement.execute();
            while (statement.getResultSet().next()) {
                User user = new User(UUID.fromString(statement.getResultSet().getString("Id")),
                        statement.getResultSet().getString("Name"),
                        statement.getResultSet().getString("UserName"),
                        statement.getResultSet().getString("Password"),
                        null,
                        statement.getResultSet().getBytes("profilePicture"));
                assignRoleToUser(user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper methods
    private void assignRoleToUser(User user){
        String sql = "SELECT RoleName, Id FROM UserRole JOIN Role ON UserRole.RoleId = Role.Id WHERE UserID=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)){
            statement.setString(1, user.getId().toString());
            statement.execute();
            while (statement.getResultSet().next()){
                user.setRole(new Role(UUID.fromString(statement.getResultSet().getString("Id")),
                        statement.getResultSet().getString("RoleName")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillPreparedStatement(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getName());
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPassword());
        statement.setBytes(4, user.getProfilePicture());
    }

    public String getRoleId(String role) {
        String sql = "SELECT Id FROM Role WHERE RoleName=?";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, role);
            statement.execute();
            if (statement.getResultSet().next())
                return statement.getResultSet().getString("Id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> searchUsers(String query) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM [User] WHERE [User].Name LIKE ? OR [User].UserName LIKE ? OR Id LIKE ?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            statement.setString(3, "%" + query + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                User user = new User(UUID.fromString(statement.getResultSet().getString("Id")),
                        statement.getResultSet().getString("Name"),
                        statement.getResultSet().getString("UserName"),
                        statement.getResultSet().getString("Password"),
                        null,
                        statement.getResultSet().getBytes("profilePicture"));
                assignRoleToUser(user);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Event coordinators
    public Collection<User> getAllEventCoordinators() {
        List<User> eventCoordinators = new ArrayList<>();
        String sql = "SELECT * FROM [User] " +
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

    public void getEventsAssignedToEventCoordinator(User eventCoordinator){
        String sql = "SELECT eventID FROM User_Event_Link WHERE userID=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, eventCoordinator.getId().toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                int eventID = resultSet.getInt("eventID"); //todo batch?
                Event event = eventDAO.getEvent(eventID);
                eventCoordinator.getAssignedEvents().add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User constructEventCoordinator(ResultSet resultSet) throws SQLException {
        UUID id = (UUID.fromString(resultSet.getString("id")));
        String name = resultSet.getString("Name");
        String username = resultSet.getString("UserName");
        String password = resultSet.getString("Password");
        byte[] profilePicture = resultSet.getBytes("profilePicture");
        User coordinator = new User(id, name, username, password, null, profilePicture);
        coordinator.setRole(new Role(UUID.fromString(getRoleId("EventCoordinator")), "EventCoordinator"));
        getEventsAssignedToEventCoordinator(coordinator);
        return coordinator;
    }

    public List<Role> getAllRoles() {
        String sql = "SELECT * FROM Role;";
        List<Role> roles = new ArrayList<>();
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                roles.add(new Role(UUID.fromString(resultSet.getString("Id")), resultSet.getString("RoleName")));
            }
            return roles;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
