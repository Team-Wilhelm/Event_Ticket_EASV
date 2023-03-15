package gui.controller;

import be.Event;
import gui.model.Model;
import io.github.palexdev.materialfx.controls.MFXButton;
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

public class AddEventController implements Initializable {
    private Model model;
    private boolean isEditing = false;
    private int eventToEditId;
    @FXML
    private MFXDatePicker dateStartDate;
    @FXML
    private TextArea txtAreaNotes;
    @FXML
    private MFXTextField txtEventName, txtLocation, txtStartTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isEditing = false;
    }

    public void setIsEditing(Event event) {
        isEditing = true;
        eventToEditId = event.getId();
        txtEventName.setText(event.getEventName());
        txtLocation.setText(event.getLocation());
        txtStartTime.setText(event.getStartingTime().toString());
        txtAreaNotes.setText(event.getNotes());
        dateStartDate.setValue(event.getStartingDate().toLocalDate());
    }

    public void cancelAction(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void saveAction(ActionEvent actionEvent) {
        String eventName = txtEventName.getText();
        String location = txtLocation.getText();
        Time startingTime = Time.valueOf(txtStartTime.getText());
        String notes = txtAreaNotes.getText();
        Date startingDate = Date.valueOf(dateStartDate.getValue());
        if (eventName.isEmpty() || location.isEmpty() || txtStartTime.getText().isEmpty()
                || dateStartDate.getValue() == null) {
            txtEventName.setPromptText("Please enter a name");
            txtLocation.setPromptText("Please enter a location");
            txtStartTime.setPromptText("Please enter a starting time");
            dateStartDate.setPromptText("Please choose a starting date");
        }
        else {
            if(!isEditing) {
                model.saveEvent(new Event(eventName, startingDate, startingTime, location, notes));
                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }
            else {
                model.updateEvent(new Event(eventToEditId, eventName, startingDate, startingTime, location, notes));
                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
