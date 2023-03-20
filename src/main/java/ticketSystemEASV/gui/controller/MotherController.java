package ticketSystemEASV.gui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class MotherController {
    public abstract void refreshItems();

    protected FXMLLoader openNewWindow(String resource, Modality modalityType) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("EASV Ticket System");
        stage.centerOnScreen();
        stage.initModality(modalityType);
        stage.show();
        return fxmlLoader;
    }
}
