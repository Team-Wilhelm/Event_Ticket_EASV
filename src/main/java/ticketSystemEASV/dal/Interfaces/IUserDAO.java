package ticketSystemEASV.dal.Interfaces;

import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;

public interface IUserDAO {
    boolean isInRole(int userID, String role);

    void signUp(User user);

    boolean logIn(String name, String password);

    User getUser(int userID);

    User getUserByEmail(String email);
}
