package ticketSystemEASV.gui.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.gui.model.Model;

import java.util.concurrent.CountDownLatch;

public class DeleteTask extends Task<TaskState> {
    protected final Object objectToDelete;
    protected final Model model;

    public DeleteTask(Object objectToDelete, Model model) {
        this.objectToDelete = objectToDelete;
        this.model = model;
    }

    @Override
    protected TaskState call() {
        CountDownLatch latch = new CountDownLatch(1);
        if (isCancelled()) {
            updateMessage("User was not deleted");
            return TaskState.NOT_SUCCESSFUL;
        }
        else {
            String message = model.delete(objectToDelete);

            if (message.isEmpty())
                return TaskState.SUCCESSFUL;
            else
                return TaskState.NOT_SUCCESSFUL;
        }
    }
}
