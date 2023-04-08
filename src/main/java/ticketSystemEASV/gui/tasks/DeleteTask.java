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
            updateMessage("User was not deleted");
            return TaskState.NOT_SUCCESSFUL;
        }
        else {
            String message = IModel.delete(objectToDelete);

            if (message.isEmpty())
                return TaskState.SUCCESSFUL;
            else
                return TaskState.NOT_SUCCESSFUL;
        }
    }
}
