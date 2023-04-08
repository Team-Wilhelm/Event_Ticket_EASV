package ticketSystemEASV.bll;

import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.dal.RoleDAO;
import ticketSystemEASV.dal.UserDAO;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public List<User> searchUsers(String query) {
        return userDAO.searchUsers(query);
    }

    public String updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public String deleteUser(User user) {
        return userDAO.deleteUser(user);
    }

    public Map<UUID, User> getAllEventCoordinators(){
        return userDAO.getAllEventCoordinators();
    }

    public Map<String , Role> getAllRoles() {
        return userDAO.getAllRoles();
    }
}
