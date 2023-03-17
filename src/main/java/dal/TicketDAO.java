package dal;

import be.Ticket;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketDAO {
    private final DBConnection dbConnection = new DBConnection();

    public void addTicket(Ticket ticket){
        String sql = "INSERT INTO Ticket (eventID, customerID, ticketType, ticketQR) VALUES (?,?,?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, ticket.getEvent().getId());
            statement.setInt(2, ticket.getCustomer().getId());
            statement.setString(3, ticket.getTicketType());
            statement.setString(4, ticket.getTicketQR());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTicket(Ticket ticket){
        String sql = "DELETE FROM Ticket WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, ticket.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
