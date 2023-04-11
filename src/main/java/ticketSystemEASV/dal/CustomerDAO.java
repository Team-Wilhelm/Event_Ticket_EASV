package ticketSystemEASV.dal;

import ticketSystemEASV.be.Customer;
import ticketSystemEASV.dal.Interfaces.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerDAO extends DAO<Customer> {
    private final DBConnection dbConnection = DBConnection.getInstance();

    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO Customer (customerName, email) VALUES (?,?);";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.execute();
            dbConnection.releaseConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
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

    public Customer getCustomer(int customerID) {
        String sql = "SELECT * FROM Customer WHERE Id=?;";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, customerID);
            statement.execute();
            if (statement.getResultSet().next()) {
                int id = statement.getResultSet().getInt("id");
                String name = statement.getResultSet().getString("customerName");
                String email = statement.getResultSet().getString("email");
                return new Customer(id, name, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        return null;
    }
}
