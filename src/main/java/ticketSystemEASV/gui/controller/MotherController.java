package ticketSystemEASV.gui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticketSystemEASV.Main;

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
}
