package ticketSystemEASV.dal;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.dal.Interfaces.IUserDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserDAO implements IUserDAO {
    private DBConnection dbConnection = new DBConnection();
    private EventDAO eventDAO = new EventDAO();
    private UserBuilder userBuilder = new UserBuilder();

    public User getUser(UUID userID) {
        String sql = "SELECT * FROM [User] " +
                "LEFT JOIN UserRole ON UserRole.UserID = [User].Id " +
                "LEFT JOIN Role ON Role.Id = UserRole.RoleId " +
                "WHERE [User].Id=?";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, userID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return constructEventCoordinator(resultSet);
            }
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

    public String signUp(User user) {
        String message = "";
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
            message = e.getMessage();
        }
        return message;
    }

    public String updateUser(User user) {
        String message = "";
        String sql = "UPDATE [User] SET name=?, userName=?, password=?, profilePicture=? WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(statement, user);
            statement.setString(5, user.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return message;
    }

    public String deleteUser(User user) {
        String sql = "UPDATE [User] SET deleted=1 WHERE id=?;";
        String message = "";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
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
    public HashMap<UUID, User> getAllEventCoordinators() {
        long startTime = System.currentTimeMillis();
        HashMap<UUID, User> eventCoordinators = new HashMap<>();
        String sql = "SELECT * FROM [User] " +
                "JOIN UserRole ON [User].ID = UserRole.UserID " +
                "JOIN [Role] ON UserRole.RoleID = Role.ID " +
                "WHERE [User].deleted=0 AND Role.RoleName='EventCoordinator';";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User u = constructEventCoordinator(resultSet);
                eventCoordinators.put(u.getId(), u);
            }
            System.out.println("Time to get all event coordinators: " + (System.currentTimeMillis() - startTime) + "ms");
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
            List<Integer> eventIds = new ArrayList<>();
            while (resultSet.next()) {
                int eventID = resultSet.getInt("eventID");
                eventIds.add(eventID);
            }
            if (!eventIds.isEmpty()) {
                // Batch get events by IDs
                List<Event> events = eventDAO.getEventsByIds(eventIds);
                eventCoordinator.setAssignedEvents(events);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private User constructEventCoordinator(ResultSet resultSet) throws SQLException {
        User user = userBuilder.setId(UUID.fromString(resultSet.getString("Id")))
                .setName(resultSet.getString("Name"))
                .setUsername( resultSet.getString("UserName"))
                .setPassword(resultSet.getString("Password"))
                .setProfilePicture(resultSet.getBytes("profilePicture"))
                .setRole(new Role(UUID.fromString(resultSet.getString("RoleID")), resultSet.getString("RoleName")))
                .build();
        getEventsAssignedToEventCoordinator(user);
        return user;
    }

    //TODO remove this and getAllRoles from RoleDAO instead?
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
