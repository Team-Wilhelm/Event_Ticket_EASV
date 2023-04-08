package ticketSystemEASV.gui.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.gui.model.IModel;

public class DeleteTask extends Task<TaskState> {
    protected final Object objectToDelete;
    protected final IModel IModel;

    public DeleteTask(Object objectToDelete, IModel IModel) {
        this.objectToDelete = objectToDelete;
        this.IModel = IModel;
    }

    @Override
    protected TaskState call() {
        if (isCancelled()) {
            updateMessage("Deleting cancelled");
            return TaskState.NOT_SUCCESSFUL;
        }
        else {
            updateMessage("Deleting...");
            String message = IModel.delete(objectToDelete);

            if (message.isEmpty()) {
                updateMessage("Deleted successfully");
                return TaskState.SUCCESSFUL;
            }
            else {
                updateMessage("Deleting was not successful");
                return TaskState.NOT_SUCCESSFUL;
            }
        }
    }
}
