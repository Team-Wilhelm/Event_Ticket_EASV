package ticketSystemEASV.gui.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.be.User;
import ticketSystemEASV.bll.UserManager;
import ticketSystemEASV.gui.model.UserModel;

public class SaveUserTask extends Task<SaveUserTask.TaskState> {
    private final User user;
    private final boolean isEditing;
    private final UserModel userModel;
    public enum TaskState {USER_SAVED, USERNAME_ALREADY_EXISTS, USER_NOT_SAVED}

    public SaveUserTask(User user, boolean isEditing, UserModel userModel) {
        this.user = user;
        this.isEditing = isEditing;
        this.userModel = userModel;
    }

    @Override
    protected TaskState call() {
        if (isCancelled()) {
            updateMessage("User was not saved");
            return TaskState.USER_NOT_SAVED;
        }
        else {
            String message;
            if (isEditing)
                message = userModel.updateUser(user);
            else
                message = userModel.signUp(user);

            if (message.isEmpty())
                return TaskState.USER_SAVED;
            else if (message.contains("Violation of UNIQUE KEY constraint"))
                return TaskState.USERNAME_ALREADY_EXISTS;
            else
                return TaskState.USER_NOT_SAVED;
        }
    }
}
