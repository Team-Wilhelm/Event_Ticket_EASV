package ticketSystemEASV.dal.Interfaces;

import ticketSystemEASV.be.User;

import java.util.List;
import java.util.UUID;

public interface IUserDAO {
    boolean isInRole(UUID userID, String role);

    String signUp(User user);

    boolean logIn(String name, String password);

    User getUser(UUID userID);

    User getUserByEmail(String email);

    List<User> searchUsers(String query);
}
