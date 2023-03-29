package ticketSystemEASV.dal;

import ticketSystemEASV.be.EventCoordinator;
import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.dal.Interfaces.IUserDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDAO implements IUserDAO {
    private DBConnection dbConnection = new DBConnection();

    public User getUser(int userID) {
        String sql = "SELECT * FROM [User] WHERE Id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, userID);
            statement.execute();
            User user = new User(statement.getResultSet().getString("Id"),
                    statement.getResultSet().getString("Name"),
                    statement.getResultSet().getString("UserName"),
                    statement.getResultSet().getString("Password"));
            assignRoleToUser(user);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isInRole(int userID, String role) {
        String sql = "SELECT * FROM UserRole\n" +
                "JOIN Role ON UserRole.RoleId = Role.Id\n" +
                "WHERE UserId=? AND Role.RoleName=?";

        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, userID);
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
        String sql = "INSERT INTO [User] (Name, UserName, Password) VALUES (?,?,?);";
        var userId = 0;
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.execute();
            userId = statement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sql = "INSERT INTO UserRole (UserId, RoleId) VALUES (?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, user.getRole().getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user){
        String sql = "UPDATE [User] SET name=?, userName=?, password=? WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            fillPreparedStatement(statement, user);
            statement.setString(4, user.getId().toString());
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
                User user = new User(statement.getResultSet().getString("Id"),
                        statement.getResultSet().getString("Name"),
                        statement.getResultSet().getString("UserName"),
                        statement.getResultSet().getString("Password"));
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
    }

    public String getRoleId(String role) {
        String sql = "SELECT Id FROM Role WHERE RoleName=?";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, role);
            statement.execute();
            return statement.getResultSet().getString("Id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
