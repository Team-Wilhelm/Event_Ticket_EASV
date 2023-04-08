package ticketSystemEASV.gui.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.gui.model.IModel;

import java.util.concurrent.CountDownLatch;

public class SaveTask extends Task<TaskState> {
    protected final Object objectToSave;
    protected final boolean isEditing;
    protected final IModel IModel;

    public SaveTask(Object objectToSave, boolean isEditing, IModel IModel) {
        this.objectToSave = objectToSave;
        this.isEditing = isEditing;
        this.IModel = IModel;
    }

    @Override
    protected TaskState call() {
        CountDownLatch latch = new CountDownLatch(1);
        if (isCancelled()) {
            updateMessage("Saving was not successful");
            return TaskState.NOT_SUCCESSFUL;
        }
        else {
            updateMessage("Saving...");
            String message;
            if (isEditing)
                message = IModel.update(objectToSave, latch);
            else
                message = IModel.add(objectToSave, latch);

            if (message.isEmpty()) {
                updateMessage("Saved successfully");
                return TaskState.SUCCESSFUL;
            }
            else if (message.contains("Violation of UNIQUE KEY constraint")) {
                updateMessage("Name already exists");
                return TaskState.CHOSEN_NAME_ALREADY_EXISTS;
            }
            else {
                updateMessage("Saving was not successful");
                return TaskState.NOT_SUCCESSFUL;
            }
        }
    }

    public boolean isEditing() {
        return isEditing;
    }
}
