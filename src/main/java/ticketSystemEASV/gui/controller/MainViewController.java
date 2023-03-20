package ticketSystemEASV.gui.controller;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.EventView;
import ticketSystemEASV.gui.model.Model;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.enums.SortState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private MFXTableView<Event> eventTableView;
    @FXML
    private ScrollPane eventScrollPane;
    private FlowPane eventFlowPane = new FlowPane();
    private ObservableList<EventView> eventViews = FXCollections.observableArrayList();
    private final Model model = new Model();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpTableView();
        eventTableView.setItems(FXCollections.observableArrayList(model.getAllEvents()));
        eventViews.addAll(model.getAllEvents().stream().map(EventView::new).toList());

        eventScrollPane.setContent(eventFlowPane);
        eventScrollPane.setFitToHeight(true);
        eventScrollPane.setFitToWidth(true);

        eventFlowPane.getChildren().addAll(eventViews);
        eventFlowPane.prefHeightProperty().bind(eventScrollPane.heightProperty());
        eventFlowPane.prefWidthProperty().bind(eventScrollPane.widthProperty());
        eventFlowPane.setHgap(10);
        eventFlowPane.setVgap(20);
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
                refreshTableView();
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
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.setOnHiding(event -> refreshTableView());
        return fxmlLoader;
    }

    private void setUpTableView() {
        eventTableView.autosizeColumns();

        //Required information
        MFXTableColumn<Event> eventName = new MFXTableColumn<>("Event name", true, Comparator.comparing(Event::getEventName));
        MFXTableColumn<Event> location = new MFXTableColumn<>("Location", true, Comparator.comparing(Event::getLocation));
        MFXTableColumn<Event> startDate = new MFXTableColumn<>("Start date", true, Comparator.comparing(Event::getStartDate));
        MFXTableColumn<Event> startTime = new MFXTableColumn<>("Start time", true, Comparator.comparing(Event::getStartTime));
        MFXTableColumn<Event> notes = new MFXTableColumn<>("Notes", true, Comparator.comparing(Event::getNotes));

        eventName.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getEventName));
        location.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getLocation));
        startDate.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getStartDate));
        startTime.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getStartTime));
        notes.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getNotes));

        //Optional information
        MFXTableColumn<Event> endDate = new MFXTableColumn<>("End date", true, Comparator.comparing(Event::getEndDate));
        MFXTableColumn<Event> endTime = new MFXTableColumn<>("End time", true, Comparator.comparing(Event::getEndTime));
        MFXTableColumn<Event> locationGuidance = new MFXTableColumn<>("Location guidance", true, Comparator.comparing(Event::getLocationGuidance));

        endDate.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getEndDate));
        endTime.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getEndTime));
        locationGuidance.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getLocationGuidance));

        eventTableView.getTableColumns().addAll(eventName, location, startDate, startTime, endDate, endTime, locationGuidance, notes);
        eventTableView.footerVisibleProperty().set(false);

        //Sort by start date, to show the next event first
        startDate.setSortState(SortState.ASCENDING);
    }

    private void refreshTableView() {
        eventTableView.getItems().clear();
        eventTableView.getItems().addAll(model.getAllEvents());

    }
}
