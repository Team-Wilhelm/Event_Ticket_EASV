package ticketSystemEASV.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import ticketSystemEASV.gui.controller.viewControllers.EventViewController;
import ticketSystemEASV.gui.controller.viewControllers.ManageCoordinatorsController;
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.TicketModel;
import ticketSystemEASV.gui.model.UserModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RootController implements Initializable {
    @FXML
    private GridPane gridPane;
    @FXML
    private Button btnManageCoordinators;
    private final TicketModel ticketModel;
    private UserModel userModel;
    private final EventModel eventModel;
    private Node eventsScene, coordinatorsScene;
    private EventViewController eventViewController;
    private ManageCoordinatorsController manageCoordinatorsController;

    public RootController() {
        Callable<TicketModel> ticketModelCallable = TicketModel::new;
        Callable<EventModel> eventModelCallable = EventModel::new;

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            ticketModel = executorService.submit(ticketModelCallable).get();
            eventModel = executorService.submit(eventModelCallable).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize models");
        }

        // Try to shut down the executor service, if it fails, throw a runtime exception and force shutdown
        try {
            executorService.shutdown();
            executorService.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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

            gridPane.add(eventsScene, 1, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void manageCoordinatorsAction(ActionEvent actionEvent) throws IOException {
        switchView(coordinatorsScene);
    }

    public void myEventsAction(ActionEvent actionEvent) {
        switchView(eventsScene);
    }

    private void switchView(Node scene) {
        if (scene == eventsScene && !gridPane.getChildren().contains(eventsScene)){
            gridPane.getChildren().remove(coordinatorsScene);
            gridPane.add(eventsScene, 1, 0);
        }
        else if (scene == coordinatorsScene && !gridPane.getChildren().contains(coordinatorsScene)){
            gridPane.getChildren().remove(eventsScene);
            gridPane.add(coordinatorsScene, 1, 0);
        }
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
        eventViewController.setModels(ticketModel, eventModel);
        if (userModel.isAdmin())
            manageCoordinatorsController.setModels(userModel);
        else btnManageCoordinators.setVisible(false);
    }
}
