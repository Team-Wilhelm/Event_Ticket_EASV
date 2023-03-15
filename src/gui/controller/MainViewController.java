package gui.controller;

import be.Event;
import com.sun.tools.javac.Main;
import gui.model.Model;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private MFXTableView<Event> eventTableView;
    private final Model model = new Model();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            setUpTableView();
        });
        eventTableView.getItems().add(new Event("Test", Date.valueOf("2023-04-01"), new Time(14, 0, 0), "EASV", "No notes"));
    }

    public void addEventAction(ActionEvent actionEvent) throws IOException {
        ((AddEventController) openNewWindow("/views/AddEventView.fxml").getController()).setModel(model);
    }

    public void editEventAction(ActionEvent actionEvent) throws IOException {
        if (eventTableView.getSelectionModel().getSelectedValues().size() == 0)
            new Alert(Alert.AlertType.ERROR, "No event selected!").showAndWait();
        else {
            FXMLLoader fxmlLoader = openNewWindow("/views/AddEventView.fxml");
            AddEventController addEventController = fxmlLoader.getController();
            addEventController.setModel(model);
            addEventController.setIsEditing(eventTableView.getSelectionModel().getSelectedValues().get(0));
        }
    }

    public void deleteEventAction(ActionEvent actionEvent) {
        if (eventTableView.getSelectionModel().getSelectedValues().size() == 0)
            new Alert(Alert.AlertType.ERROR, "No event selected!").showAndWait();
        else {
            Event event = eventTableView.getSelectionModel().getSelectedValues().get(0);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to delete this event?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                model.deleteEvent(event);
                eventTableView.getItems().remove(event);
            }
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

    private void setUpTableView() {
        MFXTableColumn<Event> eventName = new MFXTableColumn<>("Event name", true);
        MFXTableColumn<Event> location = new MFXTableColumn<>("Location", true);
        MFXTableColumn<Event> startingDate = new MFXTableColumn<>("Starting date", true, Comparator.comparing(Event::getStartingDate));
        MFXTableColumn<Event> startingTime = new MFXTableColumn<>("Starting time", true);

        eventName.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getEventName));
        location.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getLocation));
        startingDate.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getStartingDate));
        startingTime.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getStartingTime));

        eventTableView.getTableColumns().addAll(eventName, location, startingDate, startingTime);
        eventTableView.autosizeColumns();
    }
}
