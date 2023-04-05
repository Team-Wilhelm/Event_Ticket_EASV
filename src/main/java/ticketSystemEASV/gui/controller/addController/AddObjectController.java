package ticketSystemEASV.gui.controller.addController;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.viewControllers.MotherController;
import ticketSystemEASV.gui.tasks.SaveTask;

public abstract class AddObjectController {
    protected void setUpTask(SaveTask saveTask, ActionEvent actionEvent, MotherController controller){
        saveTask.setOnRunning(event -> {
            controller.bindSpinnerToTask(saveTask);
            controller.setProgressSpinnerVisibility(true);
        });

        saveTask.setOnFailed(event -> {
            controller.setProgressSpinnerVisibility(false);
            controller.unbindSpinnerFromTask();
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Something went wrong!", actionEvent).showAndWait();
        });

        saveTask.setOnSucceeded(event -> {
            controller.setProgressSpinnerVisibility(false);
            controller.unbindSpinnerFromTask();
            if (saveTask.getValue() == SaveTask.TaskState.CHOSEN_NAME_ALREADY_EXISTS) {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Username already exists!", actionEvent).showAndWait();
            } else if (saveTask.getValue() == SaveTask.TaskState.SAVED) {
                controller.refreshLastFocusedCard();
            } else {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Something went wrong!", actionEvent).showAndWait();
            }
        });
    }

    protected abstract void setIsEditing(Object objectToEdit);
}
