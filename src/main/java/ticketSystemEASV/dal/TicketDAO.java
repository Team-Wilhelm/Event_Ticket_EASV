package ticketSystemEASV.dal;

import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.dal.Interfaces.DAO;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class TicketDAO extends DAO<Ticket> {
    private final DBConnection dbConnection = DBConnection.getInstance();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public String addTicket(Ticket ticket){
        String SQL = "SELECT id FROM Customer WHERE CustomerName = ? AND email = ?";
        String message = "";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement idStatement = connection.prepareStatement(SQL);
            idStatement.setString(1, ticket.getCustomer().getName());
            idStatement.setString(2, ticket.getCustomer().getEmail());
            ResultSet rs = idStatement.executeQuery();

            int customerID;
            if (rs.next()) {
                customerID = rs.getInt("id");
            } else {
                try (PreparedStatement ps = connection.prepareStatement("INSERT INTO Customer (CustomerName, Email) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS)){
                    ps.setString(1, ticket.getCustomer().getName());
                    ps.setString(2, ticket.getCustomer().getEmail());
                    ps.executeUpdate();
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        customerID = rs.getInt(1);
                    } else {
                        throw new SQLException("Creating customer failed, no ID obtained.");
                    }
                }
            }

            SQL = "INSERT INTO Ticket (eventID, customerID, ticketType, ticketQR) VALUES (?,?,?,?);";
            try (PreparedStatement ticketStatement = connection.prepareStatement(SQL)) {
                ticketStatement.setInt(1, ticket.getEvent().getId());
                ticketStatement.setInt(2, customerID);
                ticketStatement.setString(3, ticket.getTicketType());
                ticketStatement.setBytes(4, ticket.getTicketQR());
                ticketStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Could not add ticket to database";
        } finally {
            releaseConnection(connection);
        }
        return message;
    }

    public void addMultipleTickets(List<Ticket> tickets, Customer customer){
        int customerId = customer.getId();
        String sql = "INSERT INTO Ticket (eventID, customerID, ticketType, ticketQR) VALUES (?,?,?,?);";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            for (Ticket ticket : tickets) {
                statement.setInt(1, ticket.getEvent().getId());
                statement.setInt(2, customerId);
                statement.setString(3, ticket.getTicketType());
                statement.setBytes(4, ticket.getTicketQR());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
    }

    public String deleteTicket(Ticket ticket){
        String sql = "DELETE FROM Ticket WHERE id=?;";
        String message = "";
        Connection connection = null;

        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, ticket.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Could not delete ticket from database";
        } finally {
            releaseConnection(connection);
        }
        return message;
    }

    public Ticket getTicket(UUID ticketId){
        String sql = "SELECT * FROM Ticket WHERE Id = ?;";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, ticketId.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                  return constructTicket(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
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
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                tickets.add(constructTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket;";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                tickets.add(constructTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        return tickets;
    }

    public String update(Ticket ticket) {
        String sql = "UPDATE Ticket SET eventID=?, customerID=?, ticketType=?, ticketQR=? WHERE id=?;";
        String message = "";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, ticket.getEvent().getId());
            statement.setInt(2, ticket.getCustomer().getId());
            statement.setString(3, ticket.getTicketType());
            statement.setBytes(4, ticket.getTicketQR());
            statement.setString(5, ticket.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Could not update ticket in database";
        } finally {
            releaseConnection(connection);
        }
        return message;
    }

    public synchronized HashMap<UUID, Ticket> getTicketsByIDs(List<UUID> ticketIDs) {
        HashMap<UUID, Ticket> tickets = new HashMap<>();
        if (ticketIDs == null || ticketIDs.isEmpty()) {
            return tickets;
        }

        // create comma-separated string of event IDs
        System.out.println("TicketDAO: " + ticketIDs.size() + " tickets to find");
        String sql = "SELECT * FROM Ticket WHERE id IN ("
                + String.join(",", Collections.nCopies(ticketIDs.size(), "?"))
                + ")";
        System.out.println("TicketDAO: " + sql);

        Connection connection = null;
        try {
            System.out.println("TicketDAO: getting connection");
            connection = dbConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < ticketIDs.size(); i++) {
                statement.setString(i+1, ticketIDs.get(i).toString());
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = constructTicket(resultSet);
                tickets.put(ticket.getId(), ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        System.out.println("TicketDAO: " + tickets.size() + " tickets found");
        return tickets;
    }

    private Ticket constructTicket(ResultSet resultSet) throws SQLException {
        return new Ticket(
                UUID.fromString(resultSet.getString("id")),
                customerDAO.getCustomer(resultSet.getInt("customerID")),
                resultSet.getString("ticketType"),
                resultSet.getBytes("ticketQR")
        );
    }
}
