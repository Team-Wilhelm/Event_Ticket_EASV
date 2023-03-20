package ticketSystemEASV.dal;

import ticketSystemEASV.be.User;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.User;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import ticketSystemEASV.dal.Interfaces.IUserDAO;
import ticketSystemEASV.dal.DBConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO implements IUserDAO {
    private DBConnection dbConnection = new DBConnection();

    public User getUser(int userID) {
        String sql = "SELECT * FROM [User] WHERE Id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, userID);
            statement.execute();
            return new User(statement.getResultSet().getString("Id"),
                    statement.getResultSet().getString("Name"),
                    statement.getResultSet().getString("UserName"),
                    statement.getResultSet().getString("Password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isInRole(int userID, String role) {
        String sql = "SELECT * FROM UserRole\n" +
                "JOIN Role ON UserRole.RoleId = Role.Id\n" +
                "WHERE UserId=? AND Role.Name=?";

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
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)){
            statement.setString(1, name);
            statement.setString(2, password);
            statement.execute();
            return statement.getResultSet().next();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void signUp(User user,String role){
        String sql = "INSERT INTO [User] (Name, UserName, Password) VALUES (?,?,?);";
        var userId = 0;
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)){
            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.execute();
            userId = statement.getGeneratedKeys().getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        sql = "INSERT INTO UserRole (UserId, RoleId) VALUES (?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)){
            statement.setInt(1, userId);
            statement.setString(2, getRoleId(role));
            statement.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getRoleId(String role){
        String sql = "SELECT Id FROM Role WHERE Name=?";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)){
            statement.setString(1, role);
            statement.execute();
            return statement.getResultSet().getString("Id");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
