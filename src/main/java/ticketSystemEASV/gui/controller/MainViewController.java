package ticketSystemEASV.gui.controller;

import javafx.beans.binding.Bindings;
import ticketSystemEASV.be.views.EventView;
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
import java.util.ResourceBundle;

public class MainViewController extends MotherController implements Initializable {
    @FXML
    private ScrollPane eventScrollPane;
    private final FlowPane eventFlowPane = new FlowPane();
    private final ObservableList<EventView> eventViews = FXCollections.observableArrayList();
    private final Model model = new Model();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventViews.addAll(model.getAllEvents().stream().map(EventView::new).toList());
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
        ((AddEventController) openNewWindow("/views/add...views/AddEventView.fxml", Modality.WINDOW_MODAL).getController()).setModel(model);
    }

    public void editEventAction(ActionEvent actionEvent) throws IOException {
    }

    public void deleteEventAction(ActionEvent actionEvent) {
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
    }
}
