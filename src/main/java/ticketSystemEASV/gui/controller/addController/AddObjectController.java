package ticketSystemEASV.gui.controller.addController;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.viewControllers.MotherController;
import ticketSystemEASV.gui.tasks.SaveTask;
import ticketSystemEASV.gui.tasks.TaskState;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AddObjectController {
    protected void setUpSaveTask(Task<TaskState> saveTask, ActionEvent actionEvent, MotherController controller){
        setUpTask(saveTask, actionEvent, controller);

        saveTask.setOnSucceeded(event -> {
            // unbind the progress label from the task and set it to full
            controller.unbindSpinnerFromTask();

            // after 3 seconds, the progress bar will be hidden
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            controller.setProgressSpinnerVisibility(false);
                            controller.unbindLabelFromTask();
                        }
                    },
                    3000
            );

            if (saveTask.getValue() == TaskState.CHOSEN_NAME_ALREADY_EXISTS) {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Username already exists!", actionEvent).showAndWait();
            } else if (saveTask.getValue() == TaskState.SUCCESSFUL && ((SaveTask) saveTask).isEditing()) {
                controller.refreshLastFocusedCard();
            } else if (saveTask.getValue() == TaskState.SUCCESSFUL) {
                controller.refreshItems();
            } else {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Something went wrong!", actionEvent).showAndWait();
            }
        });
    }

    protected void setUpDeleteTask(Task<TaskState> deleteTask, ActionEvent actionEvent, MotherController controller){
        setUpTask(deleteTask, actionEvent, controller);

        deleteTask.setOnSucceeded(event -> {
            // unbind the progress label from the task and set it to full
            controller.unbindSpinnerFromTask();

            // after 3 seconds, the progress bar will be hidden
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            controller.setProgressSpinnerVisibility(false);
                            controller.unbindLabelFromTask();
                        }
                    },
                    3000
            );

            controller.setProgressSpinnerVisibility(false);
            controller.unbindSpinnerFromTask();
            if (deleteTask.getValue() == TaskState.SUCCESSFUL) {
                controller.refreshItems();
            } else {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Something went wrong!", actionEvent).showAndWait();
            }
        });
    }

    protected abstract void setIsEditing(Object objectToEdit);

    /**
     * Try to softly shut down the executor service, if it fails, force shutdown
     */
    private void shutdownExecutorService(ExecutorService executorService){
        try {
            executorService.shutdown();
        } finally {
            if (!executorService.isShutdown()) {
                executorService.shutdownNow();
            }
        }
    }

    protected void executeTask(Task<TaskState> task){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        shutdownExecutorService(executorService);
        shutdownExecutorService(executorService);
    }

    private void setUpTask(Task<TaskState> task, ActionEvent actionEvent, MotherController controller) {
        task.setOnRunning(event -> {
            controller.bindSpinnerToTask(task);
            controller.setProgressSpinnerVisibility(true);
        });

        task.setOnFailed(event -> {
            controller.setProgressSpinnerVisibility(false);
            controller.unbindSpinnerFromTask();
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Something went wrong!", actionEvent).showAndWait();
        });

    }
}
