package gui.model;

import be.User;
import bll.UserManager;

public class UserModel {
    private final UserManager userManager = new UserManager();
    public UserModel() {
    }
    public boolean logIn(String name, String password) {
        return userManager.logIn(name, password);
    }
    public void signUp(String name, String userName, String password, String role) {
        userManager.signUp(new User(name, userName, password), role);
    }
}
