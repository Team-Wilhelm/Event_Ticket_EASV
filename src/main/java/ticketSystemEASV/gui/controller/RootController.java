package ticketSystemEASV.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import ticketSystemEASV.gui.controller.addController.AddCoordinatorController;
import ticketSystemEASV.gui.controller.viewControllers.EventViewController;
import ticketSystemEASV.gui.controller.viewControllers.ManageCoordinatorsController;
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.TicketModel;
import ticketSystemEASV.gui.model.UserModel;
import ticketSystemEASV.gui.model.VoucherModel;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RootController implements Initializable {
    @FXML
    private GridPane gridPane;
    @FXML
    private Button btnManageCoordinators, btnEvents, btnMyProfile;
    @FXML
    private ImageView imgManageCoordinators, imgEvents, imgLogo, imgMyProfile;
    private final TicketModel ticketModel;
    private UserModel userModel;
    private final EventModel eventModel;
    private VoucherModel voucherModel;
    private Node eventsScene, coordinatorsScene, myProfileScene, currentScene;
    private EventViewController eventViewController;
    private ManageCoordinatorsController manageCoordinatorsController;
    private AddCoordinatorController addCoordinatorController;

    public RootController() {
        Callable<TicketModel> ticketModelCallable = TicketModel::new;
        Callable<EventModel> eventModelCallable = EventModel::new;
        Callable<VoucherModel> voucherModelCallable = VoucherModel::new;

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            ticketModel = executorService.submit(ticketModelCallable).get();
            eventModel = executorService.submit(eventModelCallable).get();
            voucherModel = executorService.submit(voucherModelCallable).get();
            eventModel.setTicketModel(ticketModel, voucherModel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize models");
        }

        // Try to shut down the executor service, if it fails, throw a runtime exception and force shutdown
        try {
            executorService.shutdown();
        } finally {
            if (!executorService.isShutdown())
                executorService.shutdownNow();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader eventsLoader = new FXMLLoader(getClass().getResource("/views/EventView.fxml"));
            eventsScene = eventsLoader.load();
            eventViewController = eventsLoader.getController();

            FXMLLoader coordinatorsLoader = new FXMLLoader(getClass().getResource("/views/ManageCoordinatorsView.fxml"));
            coordinatorsScene = coordinatorsLoader.load();
            manageCoordinatorsController = coordinatorsLoader.getController();

            FXMLLoader myProfileLoader = new FXMLLoader(getClass().getResource("/views/add...views/AddCoordinatorView.fxml"));
            myProfileScene = myProfileLoader.load();
            addCoordinatorController = myProfileLoader.getController();
            addCoordinatorController.setIsManagingOwnAccount(true);

            myProfileScene.lookup("#btnGoBack").setVisible(false);
            myProfileScene.lookup("#btnGoBack").setManaged(false);
            myProfileScene.lookup("#btnDelete").setVisible(false);
            myProfileScene.lookup("#btnDelete").setManaged(false);
            myProfileScene.lookup("#btnCancel").setVisible(false);

            currentScene = eventsScene;
            gridPane.add(currentScene, 1, 0, 1,gridPane.getRowCount());
            setUpMenuButtons();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpMenuButtons() {
        btnManageCoordinators.setText("");
        btnEvents.setText("");
        btnMyProfile.setText("");

        gridPane.getRowConstraints().get(2).prefHeightProperty().bind(btnEvents.heightProperty().add(10));
        gridPane.getRowConstraints().get(3).prefHeightProperty().bind(btnManageCoordinators.heightProperty().add(10));
        gridPane.getRowConstraints().get(5).prefHeightProperty().bind(btnMyProfile.heightProperty().add(10));


        //imgLogo.fitWidthProperty().bind(btnEvents.widthProperty().divide(2));
        //TODO size bindings
    }

    public void manageCoordinatorsAction(ActionEvent actionEvent) throws IOException {
        switchView(coordinatorsScene);
    }

    public void myEventsAction(ActionEvent actionEvent) {
        switchView(eventsScene);
    }

    private void switchView(Node scene) {
        gridPane.getChildren().remove(currentScene);
        if (scene == eventsScene && currentScene != eventsScene) {
            currentScene = eventsScene;
        } else if (scene == coordinatorsScene && currentScene != coordinatorsScene) {
            currentScene = coordinatorsScene;
        } else if (scene == myProfileScene && currentScene != myProfileScene) {
           currentScene = myProfileScene;
        }
        gridPane.add(currentScene, 1, 0, 1, gridPane.getRowCount());
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
        eventViewController.setModels(ticketModel, eventModel, this.userModel, voucherModel);

        if (!userModel.isAdmin()) {
            btnManageCoordinators.setVisible(false);
        }

        manageCoordinatorsController.setModels(userModel);
        eventViewController.refreshItems(List.copyOf(UserModel.getLoggedInUser().getAssignedEvents().values()));

        addCoordinatorController.setIsEditing(UserModel.getLoggedInUser());
        addCoordinatorController.setModel(userModel);
    }

    public void myProfileAction(ActionEvent actionEvent) {
        switchView(myProfileScene);
    }
}
