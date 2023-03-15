package dal;

import be.Customer;
import be.Event;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerDAO {
    private final DBConnection dbConnection = new DBConnection();

    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO Customer (customerName, email) VALUES (?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(Customer customer) {
        String sql = "DELETE FROM Customer WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, customer.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
