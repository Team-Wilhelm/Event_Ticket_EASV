package ticketSystemEASV.gui;

import javafx.fxml.FXMLLoader;
import ticketSystemEASV.gui.controller.RootController;

import java.io.IOException;
import java.util.Objects;

public class WindowNavigator {
    /*public static final String ROOT = "/views/Root.fxml";
    public static final String LOGIN = "/views/LoginView.fxml";
    public static final String MAIN_VIEW = "/views/MainView.fxml";
    public static final String ADD_EVENT = "/views/AddEventView.fxml";
    public static final String MANAGE_COORDINATORS= "/views/ManageCoordinatorsView.fxml";
    public static final String ADD_COORDINATOR = "/views/AddCoordinatorView.fxml";

    private static RootController rootController;

    public static void setRootController(RootController rootController) {
        WindowNavigator.rootController = rootController;
    }

    public static void loadWindow(String fxml) {
        try {
            rootController.setWindow(FXMLLoader.load(Objects.requireNonNull(WindowNavigator.class.getResource(fxml))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/
}
