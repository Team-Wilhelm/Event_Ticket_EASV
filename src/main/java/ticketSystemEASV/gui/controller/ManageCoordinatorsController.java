package ticketSystemEASV.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import ticketSystemEASV.be.views.CoordinatorView;
import ticketSystemEASV.gui.model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class ManageCoordinatorsController implements Initializable {
    @FXML
    private ScrollPane coordinatorScrollPane;
    private FlowPane flowPane = new FlowPane();
    private ObservableList<CoordinatorView> coordinatorViews = FXCollections.observableArrayList();
    private Model model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coordinatorScrollPane.setContent(flowPane);
        coordinatorScrollPane.setFitToHeight(true);
        coordinatorScrollPane.setFitToWidth(true);

        flowPane.getChildren().addAll(coordinatorViews);
        flowPane.prefHeightProperty().bind(coordinatorScrollPane.heightProperty());
        flowPane.prefWidthProperty().bind(coordinatorScrollPane.widthProperty());
        flowPane.setHgap(10);
        flowPane.setVgap(20);
    }

    public void deleteCoordinatorAction(ActionEvent actionEvent) {
    }

    public void editCoordinatorAction(ActionEvent actionEvent) {
    }

    public void addCoordinatorAction(ActionEvent actionEvent) {
    }

    public void setModel(Model model) {
        this.model = model;
        coordinatorViews.addAll(model.getAllEventCoordinators().stream().map(CoordinatorView::new).toList());
        flowPane.getChildren().addAll(coordinatorViews);
    }
}
