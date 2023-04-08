package ticketSystemEASV.gui.controller.viewControllers;

import io.github.palexdev.materialfx.controls.*;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.views.EventCard;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.tasks.ConstructEventCardTask;
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
import java.util.HashMap;
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
    @FXML
    private MFXProgressSpinner progressSpinner;
    @FXML
    private Label progressLabel;
    private final ObservableList<EventCard> eventCards = FXCollections.observableArrayList();
    private final AlertManager alertManager = AlertManager.getInstance();
    private TicketModel ticketModel;
    private EventModel eventModel;
    private EventCard lastFocusedEvent;
    private ConstructEventCardTask task;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO add calendar to gridpane(1,2)
        Bindings.bindContent(eventFlowPane.getChildren(), eventCards);

        searchBar.textProperty().addListener((observable, oldValue, newValue) ->
                setFilteredEvents(eventModel.searchEvents(searchBar.getText().trim().toLowerCase())));

        eventFlowPane.prefHeightProperty().bind(eventScrollPane.heightProperty());
        eventFlowPane.prefWidthProperty().bind(eventScrollPane.widthProperty());

        progressSpinner.setVisible(false);
        progressLabel.setVisible(false);

       /* Platform.runLater(() -> {
            MFXDatePicker datePicker = new MFXDatePicker();
            gridPane.add(datePicker, 1, 2);

            Node calendar = datePicker.getClip();
            System.out.println(calendar);

            //gridPane.getChildren().remove(datePicker);
            //gridPane.add(calendar, 1, 2);
        });*/
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

        HashMap<Event, EventCard> loadedCards = eventModel.getLoadedEventCards();
        for (Event event : eventModel.getAllEvents().values()) {

            EventCard eventCard = loadedCards.get(event);
            if (loadedCards.get(event) == null) {
                eventCard = new EventCard(event);
                eventModel.getLoadedEventCards().put(event, eventCard);
                loadedCards.put(event, eventCard);
            }

            final EventCard finalEventCard = eventCard;
            eventCard.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) lastFocusedEvent = finalEventCard;
            });

            eventCard.setOnMouseClicked(e -> {
                if (!finalEventCard.isFocused())
                    finalEventCard.requestFocus();

                if (e.getClickCount() == 2) {
                    try {
                        lastFocusedEvent = finalEventCard;
                        editEventAction(null);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            });
            eventCards.add(eventCard);
        }
    }

    @Override
    public void setProgressSpinnerVisibility(boolean isVisible) {
        progressSpinner.setVisible(isVisible);
        progressLabel.setVisible(isVisible);
    }

    @Override
    public void bindSpinnerToTask(Task task) {
        progressSpinner.progressProperty().bind(task.progressProperty());
        progressLabel.textProperty().bind(task.messageProperty());
    }

    @Override
    public void unbindSpinnerFromTask() {
        progressSpinner.progressProperty().unbind();
        progressSpinner.progressProperty().set(100);
    }

    public void unbindLabelFromTask() {
        progressLabel.textProperty().unbind();
    }

    @Override
    public void refreshLastFocusedCard() {
        if (lastFocusedEvent != null)
            lastFocusedEvent.refresh(eventModel.getEvent(lastFocusedEvent.getEvent().getId()));
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
