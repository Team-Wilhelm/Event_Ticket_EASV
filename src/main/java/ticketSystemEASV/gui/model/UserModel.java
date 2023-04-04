package ticketSystemEASV.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.be.views.CoordinatorCard;
import ticketSystemEASV.bll.UserManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserModel {
    private final UserManager userManager = new UserManager();
    private User loggedInUser;
    private final ObservableList<User> allEventCoordinators = FXCollections.observableArrayList();
    private final ObservableList<Role> allRoles = FXCollections.observableArrayList();
    private final HashMap<User, CoordinatorCard> loadedCoordinatorCards = new HashMap<>();

    public UserModel() {
        this.loggedInUser = userManager.getUserByEmail("admin");
        allRoles.setAll(userManager.getAllRoles());
    }

    public boolean logIn(String name, String password) {
        return userManager.logIn(name, password);
    }

    public void signUp(String name, String userName, String password, Role role, byte[] profilePicture) {
        userManager.signUp(new User(name, userName, password, role, profilePicture));
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public User getUserByEmail(String email) {
        return userManager.getUserByEmail(email);
    }

    public List<User> searchUsers(String query) {
        return userManager.searchUsers(query);
    }

    public void updateUser(User user) {
        userManager.updateUser(user);
    }

    public void deleteUser(User user) {
        userManager.deleteUser(user);
    }

    public List<User> getAllEventCoordinators() {
        allEventCoordinators.setAll(userManager.getAllEventCoordinators());
        return allEventCoordinators;
    }

    public List<Role> getAllRoles() {
        return allRoles;
    }

    public HashMap<User, CoordinatorCard> getLoadedCoordinatorCards() {
        return loadedCoordinatorCards;
    }
}
