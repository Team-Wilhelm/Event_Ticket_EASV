package ticketSystemEASV.gui.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.gui.model.Model;

import java.util.concurrent.CountDownLatch;

public class SaveTask extends Task<TaskState> {
    protected final Object objectToSave;
    protected final boolean isEditing;
    protected final Model model;

    public SaveTask(Object objectToSave, boolean isEditing, Model model) {
        this.objectToSave = objectToSave;
        this.isEditing = isEditing;
        this.model = model;
    }

    @Override
    protected TaskState call() {
        CountDownLatch latch = new CountDownLatch(1);
        if (isCancelled()) {
            updateMessage("User was not saved");
            return TaskState.NOT_SUCCESSFUL;
        }
        else {
            String message;
            if (isEditing)
                message = model.update(objectToSave, latch);
            else
                message = model.add(objectToSave, latch);

            System.out.println(message);

            if (message.isEmpty())
                return TaskState.SUCCESSFUL;
            else if (message.contains("Violation of UNIQUE KEY constraint"))
                return TaskState.CHOSEN_NAME_ALREADY_EXISTS;
            else
                return TaskState.NOT_SUCCESSFUL;
        }
    }

    public boolean isEditing() {
        return isEditing;
    }
}
