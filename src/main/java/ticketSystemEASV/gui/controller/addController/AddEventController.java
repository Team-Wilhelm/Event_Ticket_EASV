package ticketSystemEASV.gui.controller.addController;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.MainViewController;
import ticketSystemEASV.gui.model.Model;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AddEventController implements Initializable {
    private Model model;
    private MainViewController mainViewController;
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
                model.saveEvent(new Event(eventName, startingDate, startingTime, location, notes, endDate, endTime, locationGuidance));
            }
            else {
                model.updateEvent(new Event(eventToEdit.getId(), eventName, startingDate, startingTime, location, notes, endDate, endTime, locationGuidance));
            }
            mainViewController.refreshItems();
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    private String formatTime(String time) {
        return new SimpleDateFormat(TIME_FORMAT).format(Time.valueOf(time));
    }

    private String formatTime(Time time) {
        return new SimpleDateFormat(TIME_FORMAT).format(time);
    }
}
