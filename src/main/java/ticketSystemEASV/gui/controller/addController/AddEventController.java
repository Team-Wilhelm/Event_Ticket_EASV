package ticketSystemEASV.gui.controller.addController;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticketSystemEASV.Main;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.EventViewController;
import ticketSystemEASV.gui.controller.TicketController;
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.TicketModel;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddEventController implements Initializable {
    private TicketModel ticketModel;
    private EventModel eventModel;
    private EventViewController eventViewController;
    private boolean isEditing = false;
    private Event eventToEdit;
    private final String TIME_FORMAT = "HH:mm";
    @FXML
    private MFXDatePicker dateStartDate, dateEndDate;
    @FXML
    private MFXTextField txtEventName, txtLocation, txtLocationGuidance, txtNotes;
    //TODO Notes as TextArea-ish
    @FXML
    private VBox leftVBox;
    @FXML
    private MFXComboBox<String> comboStartTime, comboEndTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isEditing = false;

        //Populating the time ComboBoxes
        comboStartTime.setValue(formatTime(Time.valueOf(LocalTime.now())));
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += 30) {
                comboStartTime.getItems().add(formatTime(i + ":" + j + ":00"));
                comboEndTime.getItems().add(formatTime(i + ":" + j + ":00"));
            }
        }
    }

    public void setIsEditing(Event event) {
        isEditing = true;
        eventToEdit = event;
        txtEventName.setText(event.getEventName());
        txtLocation.setText(event.getLocation());
        comboStartTime.setValue(formatTime(event.getStartTime()));
        txtNotes.setText(event.getNotes());
        dateStartDate.setValue(event.getStartDate().toLocalDate());

        if (event.getEndDate() != null)
            dateEndDate.setValue(event.getEndDate().toLocalDate());
        if (event.getEndTime() != null)
            comboEndTime.setValue(formatTime(event.getEndTime()));
        txtLocationGuidance.setText(event.getLocationGuidance());
    }

    public void cancelAction(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void saveAction(ActionEvent actionEvent) {
        String eventName = txtEventName.getText();
        String location = txtLocation.getText();
        String notes = txtNotes.getText();
        String locationGuidance = txtLocationGuidance.getText();

        //Check if the field has a value, if not, set it to null, otherwise, an exception will ticketSystemEASV.be thrown
        Time startingTime = !(comboStartTime.getValue() == null) ? Time.valueOf(comboStartTime.getValue()+":00") : null;
        Date startingDate = dateStartDate.getValue() != null ? Date.valueOf(dateStartDate.getValue()) : null;
        Date endDate = dateEndDate.getValue() != null ? Date.valueOf(dateEndDate.getValue()) : null;
        Time endTime = !(comboEndTime.getValue() == null) ? Time.valueOf(comboEndTime.getValue()+":00") : null;

        if (eventName.isEmpty() || location.isEmpty() || comboStartTime.getValue().isEmpty()
                || dateStartDate.getValue() == null) {
            txtEventName.setPromptText("Field cannot be empty, please enter a name");
            txtLocation.setPromptText("Field cannot be empty, please enter a location");
            comboStartTime.setPromptText("Field cannot be empty, please enter a starting time");
            dateStartDate.setPromptText("Field cannot be empty, please choose a starting date");
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Please, fill in all required fields!", actionEvent).showAndWait();
        }
        else {
            if(!isEditing) {
                eventModel.saveEvent(new Event(eventName, startingDate, startingTime, location, notes, endDate, endTime, locationGuidance));
            }
            else {
                eventModel.updateEvent(new Event(eventToEdit.getId(), eventName, startingDate, startingTime, location, notes, endDate, endTime, locationGuidance));
            }
            eventViewController.refreshItems();
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        }
    }

    public void deleteEventAction(ActionEvent actionEvent) {
        if (isEditing){
            Alert alert = AlertManager.getInstance().getAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this event?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                eventModel.deleteEvent(eventToEdit);
                eventViewController.refreshItems();
            }
        }
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void setModels(TicketModel ticketModel, EventModel eventModel) {
        this.ticketModel = ticketModel;
        this.eventModel = eventModel;
    }

    public void setMainViewController(EventViewController eventViewController) {
        this.eventViewController = eventViewController;
    }

    private String formatTime(String time) {
        return new SimpleDateFormat(TIME_FORMAT).format(Time.valueOf(time));
    }

    private String formatTime(Time time) {
        return new SimpleDateFormat(TIME_FORMAT).format(time);
    }

    public void ticketsAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Tickets.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        TicketController ticketController = fxmlLoader.getController();

        ticketController.setTicketModel(ticketModel);
        ticketController.setEvent(eventToEdit);

        stage.setScene(scene);
        stage.setTitle("EASV Ticket System");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/icons/chicken.jpg"))));
        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
