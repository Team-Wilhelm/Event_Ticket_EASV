package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.views.EventCard;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.addController.AddEventController;
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.TicketModel;
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
import java.util.ResourceBundle;

public class EventViewController extends MotherController implements Initializable {
    @FXML
    private ScrollPane eventScrollPane;
    @FXML
    private FlowPane eventFlowPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private MFXTextField searchBar;
    private final ObservableList<EventCard> eventCards = FXCollections.observableArrayList();
    private final AlertManager alertManager = AlertManager.getInstance();
    private TicketModel ticketModel;
    private EventModel eventModel;
    private EventCard lastFocusedEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO add calendar to gridpane(1,2)
        Bindings.bindContent(eventFlowPane.getChildren(), eventCards);

        searchBar.textProperty().addListener((observable, oldValue, newValue) ->
                setFilteredEvents(eventModel.searchEvents(searchBar.getText().trim().toLowerCase())));

        eventFlowPane.prefHeightProperty().bind(eventScrollPane.heightProperty());
        eventFlowPane.prefWidthProperty().bind(eventScrollPane.widthProperty());
    }

    public void addEventAction(ActionEvent actionEvent) throws IOException {
        AddEventController addEventController = openNewWindow("/views/add...views/AddEventView.fxml", Modality.WINDOW_MODAL).getController();
        addEventController.setModels(ticketModel, eventModel);
        addEventController.setMainViewController(this);
    }

    public void editEventAction(ActionEvent actionEvent) throws IOException {
        if (lastFocusedEvent == null)
            alertManager.getAlert(Alert.AlertType.ERROR, "No event selected!", actionEvent).showAndWait();
        else {
            FXMLLoader fxmlLoader = openNewWindow("/views/add...views/AddEventView.fxml", Modality.APPLICATION_MODAL);
            AddEventController addEventController = fxmlLoader.getController();
            addEventController.setModels(ticketModel, eventModel);
            addEventController.setIsEditing(lastFocusedEvent.getEvent());
            addEventController.setMainViewController(this);
        }
    }

    @Override
    public void refreshItems() {
        eventCards.clear();
        eventCards.addAll(eventModel.getAllEvents().stream().map(EventCard::new).toList());

        for (var eventView : eventCards) {
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

    public void setModels(TicketModel ticketModel, EventModel eventModel) {
        this.ticketModel = ticketModel;
        this.eventModel = eventModel;
        refreshItems();
    }

    public void setFilteredEvents(List<Event> searchEvents) {
        eventCards.clear();
        eventCards.addAll(searchEvents.stream().map(EventCard::new).toList());
    }
}
