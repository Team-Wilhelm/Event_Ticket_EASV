package ticketSystemEASV.gui.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.gui.model.UserModel;

public class LogInTask extends Task<Boolean> {
    private String email;
    private String password;
    private UserModel userModel;

    public LogInTask(String email, String password, UserModel userModel) {
        this.email = email;
        this.password = password;
        this.userModel = userModel;
    }

    @Override
    protected Boolean call() throws Exception {
        updateMessage("Logging in...");
        if (userModel.logIn(email, password)) {
            userModel.setLoggedInUser(userModel.getUserByEmail(email));
            return true;
        } else
            return false;
    }
}
