package ticketSystemEASV.gui.controller;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.views.EventView;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.addController.AddEventController;
import ticketSystemEASV.gui.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController extends MotherController implements Initializable {
    @FXML
    private ScrollPane eventScrollPane;
    private final FlowPane eventFlowPane = new FlowPane();
    private final ObservableList<EventView> eventViews = FXCollections.observableArrayList();
    private final AlertManager alertManager = AlertManager.getInstance();
    private Model model;
    private EventView lastFocusedEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bindings.bindContent(eventFlowPane.getChildren(), eventViews);

        eventScrollPane.setContent(eventFlowPane);
        eventScrollPane.setFitToHeight(true);
        eventScrollPane.setFitToWidth(true);

        eventFlowPane.prefHeightProperty().bind(eventScrollPane.heightProperty());
        eventFlowPane.prefWidthProperty().bind(eventScrollPane.widthProperty());
        eventFlowPane.setHgap(10);
        eventFlowPane.setVgap(20);
    }

    public void addEventAction(ActionEvent actionEvent) throws IOException {
        AddEventController addEventController = openNewWindow("/views/add...views/AddEventView.fxml", Modality.WINDOW_MODAL).getController();
        addEventController.setModel(model);
        addEventController.setMainViewController(this);
    }

    public void editEventAction(ActionEvent actionEvent) throws IOException {
        if (lastFocusedEvent == null)
            alertManager.getAlert(Alert.AlertType.ERROR, "No event selected!", actionEvent).showAndWait();
        else {
            FXMLLoader fxmlLoader = openNewWindow("/views/add...views/AddEventView.fxml", Modality.APPLICATION_MODAL);
            AddEventController addEventController = fxmlLoader.getController();
            addEventController.setModel(model);
            addEventController.setIsEditing(lastFocusedEvent.getEvent());
            addEventController.setMainViewController(this);
        }
    }

    public void deleteEventAction(ActionEvent actionEvent) {
        if (lastFocusedEvent == null)
            alertManager.getAlert(Alert.AlertType.ERROR, "No event selected!", actionEvent).showAndWait();
        else {
            Alert alert = alertManager.getAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this event?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                model.deleteEvent(lastFocusedEvent.getEvent());
                refreshItems();
            }
        }
    }

    public void manageCoordinatorsAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = openNewWindow("/views/ManageCoordinatorsView.fxml", Modality.NONE);
        ManageCoordinatorsController manageCoordinatorsController = fxmlLoader.getController();
        manageCoordinatorsController.setModel(model);
    }

    @Override
    public void refreshItems() {
        eventViews.clear();
        eventViews.addAll(model.getAllEvents().stream().map(EventView::new).toList());

        for (var eventView : eventViews) {
            eventView.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) lastFocusedEvent = eventView;
            });

            eventView.setOnMouseClicked(event -> {
                if (!eventView.isFocused())
                    eventView.requestFocus();

                if (event.getClickCount() == 2) {
                    try {
                        lastFocusedEvent = eventView;
                        editEventAction(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setModel(Model model) {
        this.model = model;
        refreshItems();
    }

    public void setFilteredEvents(List<Event> searchEvents) {
        eventViews.clear();
        eventViews.addAll(searchEvents.stream().map(EventView::new).toList());
    }
}
