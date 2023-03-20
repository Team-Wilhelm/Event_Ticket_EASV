package bll;

import be.User;
import dal.Interfaces.IUserDAO;
import dal.UserDAO;

public class UserManager {
    private final IUserDAO userDAO = new UserDAO();
    public boolean logIn(String name, String password) {
        return userDAO.logIn(name, password);
    }
    public void signUp(User user, String role) {
        userDAO.signUp(user, role);
    }
    public boolean isInRole(int userID, String role) {
        return userDAO.isInRole(userID, role);
    }
    public User getUser(int userID) {
        return userDAO.getUser(userID);
    }
}
