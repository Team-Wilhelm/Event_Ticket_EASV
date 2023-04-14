package ticketSystemEASV.gui.controller.viewControllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticketSystemEASV.Main;
import ticketSystemEASV.gui.controller.addController.AddCoordinatorController;
import ticketSystemEASV.gui.tasks.TaskState;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public abstract class MotherController {
    public abstract void refreshItems();
    public abstract void refreshItems(List<?> obejectList);

    protected FXMLLoader openNewWindow(String resource, Modality modalityType) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("EASV Ticket System");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/icons/chicken.jpg"))));
        stage.centerOnScreen();
        stage.initModality(modalityType);
        stage.show();

        if (resource.equals("/views/add...views/AddCoordinatorView.fxml")) {
            AddCoordinatorController addCoordinatorController = fxmlLoader.getController();
            Platform.runLater(() -> {
                ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
                        addCoordinatorController.autosizeTableColumns();
                stage.widthProperty().addListener(stageSizeListener);
                stage.heightProperty().addListener(stageSizeListener);
            });
        }
        return fxmlLoader;
    }

    public abstract void setProgressSpinnerVisibility(boolean isVisible);

    public abstract void bindSpinnerToTask(Task<TaskState> task);

    public abstract void unbindSpinnerFromTask();

    public abstract void refreshLastFocusedCard();
    public abstract void unbindLabelFromTask();
}
