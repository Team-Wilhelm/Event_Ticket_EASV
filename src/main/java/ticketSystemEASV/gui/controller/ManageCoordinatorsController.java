package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import ticketSystemEASV.be.User;
import ticketSystemEASV.be.views.CoordinatorCard;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.tasks.ConstructCoordinatorCardTask;
import ticketSystemEASV.gui.controller.addController.AddCoordinatorController;
import ticketSystemEASV.gui.model.TicketModel;
import ticketSystemEASV.gui.model.UserModel;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class ManageCoordinatorsController extends MotherController implements Initializable {
    @FXML
    private ScrollPane coordinatorScrollPane;
    @FXML
    private FlowPane flowPane;
    @FXML
    private MFXTextField searchBar;
    @FXML
    private MFXProgressSpinner progressSpinner;
    private final ObservableList<CoordinatorCard> coordinatorCards = FXCollections.observableArrayList();
    private final AlertManager alertManager = AlertManager.getInstance();
    private UserModel userModel;
    private CoordinatorCard lastFocusedCoordinator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bindings.bindContent(flowPane.getChildren(), coordinatorCards);

        searchBar.textProperty().addListener((observable, oldValue, newValue) ->
                setFilteredUsers(userModel.searchUsers(searchBar.getText().trim().toLowerCase())));

        flowPane.prefHeightProperty().bind(coordinatorScrollPane.heightProperty());
        flowPane.prefWidthProperty().bind(coordinatorScrollPane.widthProperty());

        progressSpinner.setVisible(false);
    }

    private void setFilteredUsers(List<User> searchUsers) {
        coordinatorCards.clear();
        coordinatorCards.addAll(searchUsers.stream().map(CoordinatorCard::new).toList());
    }

    public void addCoordinatorAction(ActionEvent actionEvent) throws IOException {
        AddCoordinatorController addCoordinatorController = openNewWindow("/views/add...views/AddCoordinatorView.fxml", Modality.WINDOW_MODAL).getController();
        addCoordinatorController.setModels(userModel);
        addCoordinatorController.setManageCoordinatorsController(this);
    }

    public void editCoordinatorAction(ActionEvent actionEvent) throws IOException {
        if (lastFocusedCoordinator == null)
            alertManager.getAlert(Alert.AlertType.ERROR, "No coordinator selected!", actionEvent).showAndWait();
        else {
            FXMLLoader fxmlLoader = openNewWindow("/views/add...views/AddCoordinatorView.fxml", Modality.WINDOW_MODAL);
            AddCoordinatorController addCoordinatorController = fxmlLoader.getController();
            addCoordinatorController.setModels(userModel);
            addCoordinatorController.setIsEditing(lastFocusedCoordinator.getCoordinator());
            addCoordinatorController.setManageCoordinatorsController(this);
        }
    }

    public void setModels(UserModel userModel) {
        this.userModel = userModel;
        refreshItems();
    }

    @Override
    public void refreshItems() {
        coordinatorCards.clear();
        HashMap<User, CoordinatorCard> loadedCards = userModel.getLoadedCoordinatorCards();
        HashMap<UUID, User> coordinators = (HashMap<UUID, User>) userModel.getAllEventCoordinators();

        for (User user : coordinators.values()) {

            CoordinatorCard coordinatorCard = loadedCards.get(user);
            if (loadedCards.get(user) == null || lastFocusedCoordinator == coordinatorCard) {
                coordinatorCard = new CoordinatorCard(user);
                userModel.getLoadedCoordinatorCards().put(user, coordinatorCard);
                loadedCards.put(user, coordinatorCard);
            }

            final CoordinatorCard finalCoordinatorCard = coordinatorCard;
            coordinatorCard.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) lastFocusedCoordinator = finalCoordinatorCard;
            });

            coordinatorCard.setOnMouseClicked(event -> {
                if (!finalCoordinatorCard.isFocused())
                    finalCoordinatorCard.requestFocus();

                if (event.getClickCount() == 2) {
                    try {
                        lastFocusedCoordinator = finalCoordinatorCard;
                        editCoordinatorAction(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            coordinatorCards.add(coordinatorCard);
        }
        System.out.println(coordinatorCards.size());
    }

    public void refreshLastFocusedCard() {
        if (lastFocusedCoordinator != null) {
            lastFocusedCoordinator.refresh(userModel.getUser(lastFocusedCoordinator.getCoordinator().getId()));
        }
    }

    public void deleteCard(User coordinatorToDelete) {
        coordinatorCards.removeIf(coordinatorCard -> coordinatorCard.getCoordinator().getId() == coordinatorToDelete.getId());
    }

    public MFXProgressSpinner getProgressSpinner() {
        return progressSpinner;
    }
}
