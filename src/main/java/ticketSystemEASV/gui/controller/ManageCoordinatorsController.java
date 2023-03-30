package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.User;
import ticketSystemEASV.be.views.CoordinatorView;
import ticketSystemEASV.be.views.EventView;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.addController.AddCoordinatorController;
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.Model;
import ticketSystemEASV.gui.model.UserModel;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageCoordinatorsController extends MotherController implements Initializable {
    @FXML
    private ScrollPane coordinatorScrollPane;
    @FXML
    private FlowPane flowPane;
    @FXML
    private MFXTextField searchBar;
    private final ObservableList<CoordinatorView> coordinatorViews = FXCollections.observableArrayList();
    private final AlertManager alertManager = AlertManager.getInstance();
    private Model model;
    private EventModel eventModel;
    private UserModel userModel;
    private CoordinatorView lastFocusedCoordinator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bindings.bindContent(flowPane.getChildren(), coordinatorViews);

        searchBar.textProperty().addListener((observable, oldValue, newValue) ->
                setFilteredUsers(userModel.searchUsers(searchBar.getText().trim().toLowerCase())));

        flowPane.prefHeightProperty().bind(coordinatorScrollPane.heightProperty());
        flowPane.prefWidthProperty().bind(coordinatorScrollPane.widthProperty());
    }

    private void setFilteredUsers(List<User> searchUsers) {
        coordinatorViews.clear();
        coordinatorViews.addAll(searchUsers.stream().map(CoordinatorView::new).toList());
    }

    public void addCoordinatorAction(ActionEvent actionEvent) throws IOException {
        AddCoordinatorController addCoordinatorController = openNewWindow("/views/add...views/AddCoordinatorView.fxml", Modality.WINDOW_MODAL).getController();
        addCoordinatorController.setModels(model, userModel);
        addCoordinatorController.setManageCoordinatorsController(this);
    }

    public void editCoordinatorAction(ActionEvent actionEvent) throws IOException {
        if (lastFocusedCoordinator == null)
            alertManager.getAlert(Alert.AlertType.ERROR, "No coordinator selected!", actionEvent).showAndWait();
        else {
            FXMLLoader fxmlLoader = openNewWindow("/views/add...views/AddCoordinatorView.fxml", Modality.WINDOW_MODAL);
            AddCoordinatorController addCoordinatorController = fxmlLoader.getController();
            addCoordinatorController.setModels(model, userModel);
            addCoordinatorController.setIsEditing(lastFocusedCoordinator.getCoordinator());
            addCoordinatorController.setManageCoordinatorsController(this);
        }
    }

    public void setModels(Model model, EventModel eventModel, UserModel userModel) {
        this.model = model;
        this.eventModel = eventModel;
        this.userModel = userModel;
        refreshItems();
    }

    @Override
    public void refreshItems() {
        //TODO optimize
        coordinatorViews.clear();
        coordinatorViews.addAll(userModel.getAllEventCoordinators().stream().map(CoordinatorView::new).toList());

        for (var coordinatorView : coordinatorViews) {
            coordinatorView.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) lastFocusedCoordinator = coordinatorView;
            });

            coordinatorView.setOnMouseClicked(event -> {
                if (!coordinatorView.isFocused())
                    coordinatorView.requestFocus();

                if (event.getClickCount() == 2) {
                    try {
                        coordinatorView.requestFocus();
                        lastFocusedCoordinator = coordinatorView;
                        editCoordinatorAction(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
