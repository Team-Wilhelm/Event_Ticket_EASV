package ticketSystemEASV.gui.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.gui.model.Model;

public class SaveTask extends Task<SaveTask.TaskState> {
    public enum TaskState {SAVED, CHOSEN_NAME_ALREADY_EXISTS, NOT_SAVED}
    protected final Object objectToSave;
    protected final boolean isEditing;
    protected final Model model;

    public SaveTask(Object objectToSave, boolean isEditing, Model model) {
        this.objectToSave = objectToSave;
        this.isEditing = isEditing;
        this.model = model;
    }

    @Override
    protected SaveTask.TaskState call() {
        System.out.println(Thread.currentThread().getName());
        if (isCancelled()) {
            updateMessage("User was not saved");
            return TaskState.NOT_SAVED;
        }
        else {
            String message;
            if (isEditing)
                message = model.update(objectToSave);
            else
                message = model.add(objectToSave);

            if (message.isEmpty())
                return TaskState.SAVED;
            else if (message.contains("Violation of UNIQUE KEY constraint"))
                return TaskState.CHOSEN_NAME_ALREADY_EXISTS;
            else
                return TaskState.NOT_SAVED;
        }
    }
}
