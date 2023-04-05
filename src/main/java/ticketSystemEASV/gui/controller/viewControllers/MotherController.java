package ticketSystemEASV.gui.controller.viewControllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticketSystemEASV.Main;
import ticketSystemEASV.gui.model.Model;
import ticketSystemEASV.gui.tasks.SaveTask;

import java.io.IOException;
import java.util.Objects;

public abstract class MotherController {
    public abstract void refreshItems();

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
        return fxmlLoader;
    }

    public abstract void setProgressSpinnerVisibility(boolean isVisible);

    public abstract void bindSpinnerToTask(SaveTask constructCoordinatorCardTask);

    public abstract void unbindSpinnerFromTask();

    public abstract void refreshLastFocusedCard();
}
