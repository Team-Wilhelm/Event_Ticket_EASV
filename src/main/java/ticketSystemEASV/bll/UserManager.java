package ticketSystemEASV.bll;

import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.dal.UserDAO;

import java.util.*;

public class UserManager {
    private final UserDAO userDAO = new UserDAO();

    public boolean logIn(String name, String password) {
        return userDAO.logIn(name, password);
    }

    public String signUp(User user) {
        return userDAO.signUp(user);
    }

    public boolean isInRole(UUID userID, String role) {
        return userDAO.isInRole(userID, role);
    }

    public User getUser(UUID userID) {
        return userDAO.getUser(userID);
    }

    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    public List<User> searchUsers(String query) {
        return userDAO.searchUsers(query);
    }

    public String updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    public Map<UUID, User> getAllEventCoordinators(){
        return userDAO.getAllEventCoordinators();
    }

    public List<Role> getAllRoles() {
        return userDAO.getAllRoles();
    }
}
