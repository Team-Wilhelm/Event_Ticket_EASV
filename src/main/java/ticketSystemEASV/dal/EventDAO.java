package ticketSystemEASV.dal;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.be.TicketType;
import ticketSystemEASV.be.User;
import ticketSystemEASV.dal.Interfaces.DAO;
import ticketSystemEASV.gui.model.UserModel;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class EventDAO extends DAO<Event> {
    private final DBConnection dbConnection = DBConnection.getInstance();

    public String addEvent(Event event) {
        User loggedInUser = UserModel.getLoggedInUser();
        String message="";
        String sql = "DECLARE @EventID int;" +
                "INSERT INTO Event (startDate, startTime, eventName, eventLocation, notes, endDate, endTime, locationGuidance) " +
                "VALUES (?,?,?,?,?,?,?,?) " +
                "SET @EventID = (SELECT ID FROM Event WHERE EventName = ?)" +
                "INSERT INTO User_Event_Link (UserID, EventId)" +
                "VALUES (?, @EventID);";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            fillPreparedStatement(event, statement);
            statement.setString(9, event.getEventName());
            statement.setString(10, loggedInUser.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            message = e.getMessage();
            e.printStackTrace();
        }
        return message;
    }

    public String updateEvent(Event event) {
        String message = "";
        String sql = "UPDATE Event SET startDate=?, startTime=?, eventName=?, eventLocation=?, notes=?, endDate=?, endTime=?, locationGuidance=? WHERE id=?;";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            fillPreparedStatement(event, statement);
            statement.setInt(9, event.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
    }

    public String deleteEvent(Event eventToDelete) {
        String sql = "UPDATE Event SET deleted=1 WHERE id=?;";
        String message = "";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, eventToDelete.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
    }

    private void fillPreparedStatement(Event event, PreparedStatement statement) throws SQLException {
        statement.setDate(1, event.getStartDate());
        statement.setTime(2, event.getStartTime());
        statement.setString(3, event.getEventName());
        statement.setString(4, event.getLocation());
        statement.setString(5, event.getNotes());
        statement.setDate(6, event.getEndDate());
        statement.setTime(7, event.getEndTime());
        statement.setString(8, event.getLocationGuidance());
    }

    public Map<Integer, Event> getAllEvents() {
        HashMap<Integer, Event> events = new HashMap<>();
        String sql = "SELECT * FROM Event WHERE deleted=0;";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Event e = constructEvent(resultSet);
                events.put(e.getId(), e);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Event getEvent(int eventID) {
        String sql = "SELECT * FROM Event WHERE id=?;";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, eventID);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                return constructEvent(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getEventsAssignedToEventCoordinator(User eventCoordinator){
        if (Objects.equals(eventCoordinator.getRole().getName(), "Admin")) {
            eventCoordinator.setAssignedEvents((HashMap<Integer, Event>) getAllEvents());
            return;
        }

        String sql = "SELECT eventID FROM User_Event_Link WHERE userID=?;";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, eventCoordinator.getId().toString());
            ResultSet resultSet = statement.executeQuery();
            List<Integer> eventIds = new ArrayList<>();
            while (resultSet.next()) {
                int eventID = resultSet.getInt("eventID");
                eventIds.add(eventID);
            }
            if (!eventIds.isEmpty()) {
                // Batch get events by IDs
                HashMap<Integer, Event> events = getEventsByIds(eventIds);
                eventCoordinator.setAssignedEvents(events);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
    }

    public void assignCoordinatorToEvent(User user, Event event){
        String sql = "INSERT INTO User_Event_Link (UserID, EventID) VALUES (?, ?);";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getId().toString());
            statement.setInt(2, event.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unassignCoordinatorFromEvent(User user, Event event) {
        String sql = "DELETE FROM User_Event_Link WHERE UserID = ? AND EventID =?;";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getId().toString());
            statement.setInt(2, event.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Event constructEvent(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String eventName = resultSet.getString("eventName");
        Date startDate = resultSet.getDate("startDate");
        Time startTime = resultSet.getTime("startTime");
        String location = resultSet.getString("eventLocation");
        String notes = resultSet.getString("notes");
        Date endDate = resultSet.getDate("endDate");
        Time endTime = resultSet.getTime("endTime");
        String locationGuidance = resultSet.getString("locationGuidance");
        Event event = new Event(id, eventName, startDate, startTime, location, notes, endDate, endTime, locationGuidance);
        getTicketsAssignedToEvent(event);
        return event;
    }

    public HashMap<Integer, Event> getEventsByIds(List<Integer> eventIDs) {
        HashMap<Integer, Event> events = new HashMap<>();
        if (eventIDs == null || eventIDs.isEmpty()) {
            return events;
        }

        // create comma-separated string of event IDs
        String ids = eventIDs.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        String sql = "SELECT * FROM Event WHERE deleted=0 AND id IN (" + ids + ")";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Event event = constructEvent(resultSet);
                events.put(event.getId(), event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        return events;
    }

    public synchronized void getTicketsAssignedToEvent(Event event) {
        String sql = "SELECT * FROM Ticket WHERE eventID=?;";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, event.getId());
            ResultSet resultSet = statement.executeQuery();

            List<UUID> ticketIDs = new ArrayList<>();
            while (resultSet.next()) {
                UUID ticketID = UUID.fromString(resultSet.getString("ID"));
                ticketIDs.add(ticketID);
            }

            if (!ticketIDs.isEmpty()) {
                TicketDAO ticketDAO = new TicketDAO();
                // Batch get events by IDs
                HashMap<UUID, Ticket> tickets = ticketDAO.getTicketsByIDs(ticketIDs);
                event.setTickets(tickets);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
    }

}
