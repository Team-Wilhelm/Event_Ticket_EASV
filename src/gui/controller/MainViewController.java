package gui.controller;

import be.Event;
import com.sun.tools.javac.Main;
import gui.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private ListView<Event> eventListView;
    private Model model = new Model();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventListView.getItems().add(new Event("Test", null, null, "Test", "Test"));
    }

    public void addEventAction(ActionEvent actionEvent) throws IOException {
        openNewWindow("/views/AddEventView.fxml");
    }

    public void editEventAction(ActionEvent actionEvent) throws IOException {
        openNewWindow("/views/AddEventView.fxml");
    }

    public void deleteEventAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this event?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            eventListView.getItems().remove(eventListView.getSelectionModel().getSelectedItem());
            model.deleteEvent(eventListView.getSelectionModel().getSelectedItem());
        }

    }

    private FXMLLoader openNewWindow(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("EASV Ticket System");
        stage.centerOnScreen();
        stage.show();
        return fxmlLoader;
    }
}
