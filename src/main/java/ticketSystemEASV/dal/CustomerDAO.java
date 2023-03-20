package ticketSystemEASV.dal;

import ticketSystemEASV.be.Customer;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerDAO {
    //TODO maybe this class should ticketSystemEASV.be merged with TicketDAO?
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

    public Customer getCustomer(int customerID) {
        String sql = "SELECT * FROM Customer WHERE Id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, customerID);
            statement.execute();
            while (statement.getResultSet().next()) {
                int id = statement.getResultSet().getInt("id");
                String name = statement.getResultSet().getString("customerName");
                String email = statement.getResultSet().getString("email");
                return new Customer(id, name, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
