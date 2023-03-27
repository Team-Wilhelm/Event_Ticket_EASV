package ticketSystemEASV.dal.Interfaces;

import ticketSystemEASV.be.User;

public interface IUserDAO {
    boolean isInRole(int userID, String role);

    void signUp(User user, String role);

    boolean logIn(String name, String password);

    User getUser(int userID);

    User getUserByEmail(String email);
}
