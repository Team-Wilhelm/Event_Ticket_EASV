package dal.Interfaces;

import be.User;

public interface IUserDAO {
    boolean isInRole(int userID, String role);

    void signUp(User user, String role);

    boolean logIn(String name, String password);

    User getUser(int userID);
}
