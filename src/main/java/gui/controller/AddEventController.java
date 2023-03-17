package gui.controller;

import be.Event;
import gui.model.Model;
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
import java.util.ResourceBundle;
import java.util.UUID;

public class AddEventController implements Initializable {
    private Model model;
    private boolean isEditing = false;
    private Event eventToEdit;
    @FXML
    private MFXDatePicker dateStartDate, dateEndDate;
    @FXML
    private TextArea txtAreaNotes;
    @FXML
    private MFXTextField txtEventName, txtLocation, txtStartTime, txtEndTime, txtLocationGuidance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isEditing = false;
    }

    public void setIsEditing(Event event) {
        isEditing = true;
        eventToEdit = event;
        txtEventName.setText(event.getEventName());
        txtLocation.setText(event.getLocation());
        txtStartTime.setText(event.getStartTime().toString());
        txtAreaNotes.setText(event.getNotes());
        dateStartDate.setValue(event.getStartDate().toLocalDate());
        dateEndDate.setValue(event.getEndDate().toLocalDate());
        txtEndTime.setText(event.getEndTime().toString());
        txtLocationGuidance.setText(event.getLocationGuidance());
    }

    public void cancelAction(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void saveAction(ActionEvent actionEvent) {
        String eventName = txtEventName.getText();
        String location = txtLocation.getText();
        String notes = txtAreaNotes.getText();
        String locationGuidance = txtLocationGuidance.getText();

        //Check if the field has a value, if not, set it to null, otherwise, an exception will be thrown
        Time startingTime = !txtStartTime.getText().isEmpty() ? Time.valueOf(txtStartTime.getText()) : null;
        Date startingDate = dateStartDate.getValue() != null ? Date.valueOf(dateStartDate.getValue()) : null;
        Date endDate = dateEndDate.getValue() != null ? Date.valueOf(dateEndDate.getValue()) : null;
        Time endTime = !txtEndTime.getText().isEmpty() ? Time.valueOf(txtEndTime.getText()) : null;

        if (eventName.isEmpty() || location.isEmpty() || txtStartTime.getText().isEmpty()
                || dateStartDate.getValue() == null) {
            txtEventName.setPromptText("Field cannot be empty, please enter a name");
            txtLocation.setPromptText("Field cannot be empty, please enter a location");
            txtStartTime.setPromptText("Field cannot be empty, please enter a starting time");
            dateStartDate.setPromptText("Field cannot be empty, please choose a starting date");
        }
        else {
            //TODO coordinatorID should be set to the logged in user (or admin can assign a coordinator)
            if(!isEditing) {
                model.saveEvent(new Event(model.getAllEventCoordinators().get(0).getId(),eventName, startingDate, startingTime, location, notes, endDate, endTime, locationGuidance));
                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }
            else {
                model.updateEvent(new Event(eventToEdit.getId(), eventToEdit.getCoordinatorId(), eventName, startingDate, startingTime, location, notes, endDate, endTime, locationGuidance));
                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
