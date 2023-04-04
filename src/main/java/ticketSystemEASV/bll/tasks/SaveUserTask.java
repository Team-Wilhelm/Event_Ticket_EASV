package ticketSystemEASV.bll.tasks;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import ticketSystemEASV.be.User;
import ticketSystemEASV.bll.UserManager;

import java.util.List;

public class SaveUserTask extends Task<User> {
    private final User user;
    private final boolean isEditing;
    private final UserManager userManager = new UserManager();
    private final Scene stageToUpdate;

    public SaveUserTask(User user, boolean isEditing, Scene stageToUpdate) {
        this.user = user;
        this.isEditing = isEditing;
        this.stageToUpdate = stageToUpdate;
    }

    @Override
    protected User call() throws Exception {
        if (isCancelled()) {
            updateMessage("User was not saved");
            return null;
        }
        else {
            if (isEditing)
                userManager.updateUser(user);
            else
                userManager.signUp(user);
            return userManager.getUser(user.getId());
        }
    }
}
