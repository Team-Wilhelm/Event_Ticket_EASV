package ticketSystemEASV.gui.controller.addController;

import io.github.palexdev.materialfx.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import ticketSystemEASV.Main;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.User;
import ticketSystemEASV.bll.util.AlertManager;
import ticketSystemEASV.gui.controller.viewControllers.EventViewController;
import ticketSystemEASV.gui.controller.TicketController;
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.TicketModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import ticketSystemEASV.gui.model.UserModel;
import ticketSystemEASV.gui.model.VoucherModel;
import ticketSystemEASV.gui.tasks.DeleteTask;
import ticketSystemEASV.gui.tasks.SaveTask;
import ticketSystemEASV.gui.tasks.TaskState;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class AddEventController extends AddObjectController implements Initializable {
    private TicketModel ticketModel;
    private EventModel eventModel;
    private UserModel userModel;
    private VoucherModel voucherModel;
    private EventViewController eventViewController;
    private boolean isEditing = false;
    private Event eventToEdit;
    private final String TIME_FORMAT = "HH:mm";
    @FXML
    private MFXDatePicker dateStartDate, dateEndDate;
    @FXML
    private MFXTextField txtEventName, numOfTickets, txtLocation, txtLocationGuidance, txtNotes;
    @FXML
    private VBox leftVBox;
    @FXML
    private MFXComboBox<String> comboStartTime, comboEndTime;
    @FXML
    MFXButton btnTickets;
    private Task<TaskState> task;
    private final AlertManager alertManager = AlertManager.getInstance();
    private String eventName, location, locationGuidance, notes;
    private int numberOfTickets;
    private Date startingDate, endDate;
    private Time startingTime, endTime;
    @FXML
    private MFXFilterComboBox<User> comboAssignCoordinator;
    private ObservableList<User> allCoordinators = FXCollections.observableArrayList();

    //TODO update event does not update tickets in the database
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isEditing = false;
        comboAssignCoordinator.setDisable(true);
        btnTickets.setDisable(true);

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };

        numOfTickets.delegateSetTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), null, integerFilter));

        //Populating the time ComboBoxes
        comboStartTime.setValue(formatTime(Time.valueOf(LocalTime.now())));
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += 30) {
                comboStartTime.getItems().add(formatTime(i + ":" + j + ":00"));
                comboEndTime.getItems().add(formatTime(i + ":" + j + ":00"));
            }
        }

        comboAssignCoordinator.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                assignCoordinator(newValue);
                comboAssignCoordinator.getSelectionModel().clearSelection();

                populateAssignCoordinatorComboBox();
            }
        });

        comboAssignCoordinator.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User object) {
                if (!(object == null))
                return object.getAssignation(object.getAssignedEvents().get(eventToEdit.getId())) + " " + object.getName() + " (" + object.getUsername() + ")";
                else return "";
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
    }

    @Override
    public void setIsEditing(Object object) {
        isEditing = true;
        comboAssignCoordinator.setDisable(false);
        btnTickets.setDisable(false);

        Event event = (Event) object;
        eventToEdit = event;
        txtEventName.setText(event.getEventName());
        txtLocation.setText(event.getLocation());
        comboStartTime.setValue(formatTime(event.getStartTime()));
        txtNotes.setText(event.getNotes());
        dateStartDate.setValue(event.getStartDate().toLocalDate());
        numOfTickets.setText(String.valueOf(event.getNumberOfTickets()));

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
        eventName = txtEventName.getText();
        location = txtLocation.getText();
        try {
            numberOfTickets = Integer.parseInt(numOfTickets.getText());
        }
        catch (Exception e) {
            numberOfTickets = 0;
        }
        notes = txtNotes.getText();
        locationGuidance = txtLocationGuidance.getText();

        if (checkInput(actionEvent)) {
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();

            Event event = new Event(eventName, startingDate, startingTime, numberOfTickets, location, notes, endDate, endTime, locationGuidance);
            if (isEditing) event.setId(eventToEdit.getId());

            task = new SaveTask(event, isEditing, eventModel);
            setUpSaveTask(task, actionEvent, eventViewController);
            executeTask(task);
        }
    }

    public void deleteEventAction(ActionEvent actionEvent) {
        if (isEditing){
            Alert alert = alertManager.getAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this event?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                task = new DeleteTask(eventToEdit, eventModel);
                setUpDeleteTask(task, actionEvent, eventViewController);
                executeTask(task);
            }
        }
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void setModels(TicketModel ticketModel, EventModel eventModel, UserModel userModel, VoucherModel voucherModel) {
        this.ticketModel = ticketModel;
        this.eventModel = eventModel;
        this.userModel = userModel;
        this.voucherModel = voucherModel;
        populateAssignCoordinatorComboBox();
    }

    private void populateAssignCoordinatorComboBox(){
        allCoordinators.clear();
        for(User user : userModel.getAllUsers().values()) {
            if (!(user.getRole().getName().equals("Admin") || user.equals(UserModel.getLoggedInUser()))) {
                allCoordinators.add(user);

            }
        }
        comboAssignCoordinator.setItems(allCoordinators);
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

        ticketController.setTicketModel(ticketModel, voucherModel, eventModel);
        ticketController.setEvent(eventToEdit);

        stage.setScene(scene);
        stage.setTitle("EASV Ticket System");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/icons/chicken.jpg"))));
        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private boolean checkInput(ActionEvent actionEvent) {
        //Check if the field has a value, if not, set it to null, otherwise, an exception will be thrown
        startingTime = !(comboStartTime.getValue() == null) ? Time.valueOf(comboStartTime.getValue()+":00") : null;
        startingDate = dateStartDate.getValue() != null ? Date.valueOf(dateStartDate.getValue()) : null;
        endDate = dateEndDate.getValue() != null ? Date.valueOf(dateEndDate.getValue()) : null;
        endTime = !(comboEndTime.getValue() == null) ? Time.valueOf(comboEndTime.getValue()+":00") : null;

        if (eventModel.getAllEvents().values().stream().anyMatch(event -> event.getEventName().equals(eventName) && !eventName.equals(eventToEdit.getEventName()))) {
            txtEventName.setPromptText("Event name already exists, please choose another");
            alertManager.getAlert(Alert.AlertType.ERROR, "Event with this name already exists, \nplease choose a different name", actionEvent).showAndWait();
            return false;
        }

        if (endDate != null && startingDate != null && endDate.before(startingDate)) {
            dateEndDate.setPromptText("End date cannot be before start date");
            alertManager.getAlert(Alert.AlertType.ERROR, "End date cannot be before start date", actionEvent).showAndWait();
            return false;
        }

        if (endTime != null && startingTime != null && endTime.before(startingTime) && endDate.equals(startingDate)) {
            comboEndTime.setPromptText("End time cannot be before start time");
            alertManager.getAlert(Alert.AlertType.ERROR, "End time cannot be before start time", actionEvent).showAndWait();
            return false;
        }

        if (eventName.isEmpty() || location.isEmpty() || comboStartTime.getValue().isEmpty()
                || dateStartDate.getValue() == null || numOfTickets.getText().isEmpty()) {
            txtEventName.setPromptText("Field cannot be empty, please enter a name");
            txtLocation.setPromptText("Field cannot be empty, please enter a location");
            comboStartTime.setPromptText("Field cannot be empty, please enter a starting time");
            dateStartDate.setPromptText("Field cannot be empty, please choose a starting date");
            numOfTickets.setPromptText("Field cannot be empty, please enter a number of tickets");
            alertManager.getAlert(Alert.AlertType.ERROR, "Please, fill in all required fields!", actionEvent).showAndWait();
            return false;
        }
        return true;
    }

    private void assignCoordinator(User user) {
        Event event = eventToEdit;

        if(!user.getAssignedEvents().containsKey(event.getId())){
            eventModel.assignCoordinatorToEvent(user, event);
        }
        else eventModel.unassignCoordinatorFromEvent(user, event);
    }
}
