package dal;

import be.Customer;
import be.Event;
import be.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Ticket getTicket(UUID ticketId){
        String sql = "SELECT * FROM Ticket WHERE Id = ?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, ticketId.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                  return new Ticket(
                        UUID.fromString(rs.getString("id")),
                        new EventDAO().getEvent(rs.getInt("eventID")),
                        new CustomerDAO().getCustomer(rs.getInt("customerID")),
                        rs.getString("ticketType"),
                        rs.getString("ticketQR")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Ticket> getAllTicketsForEvent(Event event){
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE EventId = ?;";
        int id = event.getId();
        getTickets(tickets, sql, id);
        return tickets;
    }

    public List<Ticket> getAllTicketsForCustomer(Customer customer){
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE CustomerId = ?;";
        int id = customer.getId();
        getTickets(tickets, sql, id);
        return tickets;
    }

    public void getTickets(List<Ticket> tickets, String sql, int id){
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                tickets.add(new Ticket(
                        UUID.fromString(rs.getString("id")),
                        new EventDAO().getEvent(rs.getInt("eventID")),
                        new CustomerDAO().getCustomer(rs.getInt("customerID")),
                        rs.getString("ticketType"),
                        rs.getString("ticketQR")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
