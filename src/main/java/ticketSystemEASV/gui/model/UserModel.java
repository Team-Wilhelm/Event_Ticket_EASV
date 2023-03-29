package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.bll.UserManager;

public class UserModel {
    private final UserManager userManager = new UserManager();
    private User loggedInUser;

    public UserModel() {
        this.loggedInUser = userManager.getUserByEmail("admin");
    }

    public boolean logIn(String name, String password) {
        return userManager.logIn(name, password);
    }

    public void signUp(String name, String userName, String password, Role role) {
        userManager.signUp(new User(name, userName, password, role));
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
}
