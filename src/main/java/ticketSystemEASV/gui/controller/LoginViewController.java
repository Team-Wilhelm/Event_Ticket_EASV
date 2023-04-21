package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ticketSystemEASV.Main;
import ticketSystemEASV.be.LoadingScreen;
import ticketSystemEASV.bll.util.AlertManager;
import ticketSystemEASV.gui.model.UserModel;
import javafx.fxml.Initializable;
import ticketSystemEASV.gui.tasks.LogInTask;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static javafx.scene.input.KeyCode.ENTER;

public class LoginViewController implements Initializable {
    private final UserModel userModel = new UserModel();
    @FXML private MFXTextField emailInput;
    @FXML private MFXPasswordField passwordInput;
    @FXML private Label progressLabel;
    private Parent root;
    private Stage stage;
    private FXMLLoader fxmlLoader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::setEnterKeyAction);
        progressLabel.setVisible(false);

        try {
            fxmlLoader = new FXMLLoader(Main.class.getResource("/views/Root.fxml"));
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loginUser(Event event) throws IOException {
        stage = (Stage) emailInput.getScene().getWindow();

        boolean login = false;
        Future<Boolean> future = null;
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            Task<Boolean> logInTask = new LogInTask(emailInput.getText(), passwordInput.getText(), userModel);
            setUpTask(logInTask);
            executorService.submit(logInTask);
            executorService.shutdown();
        } catch (Exception e) {
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Could not log in.", event).show();
        }
    }

    private void setEnterKeyAction() {
        Scene scene = emailInput.getScene();
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == ENTER) {
                try {
                    loginUser(new Event(passwordInput, passwordInput, Event.ANY));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setUpTask(Task<Boolean> task) {
        task.setOnRunning(event -> {
            progressLabel.setVisible(true);
            progressLabel.textProperty().bind(task.messageProperty());
        });

        task.setOnSucceeded(event -> {
            if (task.getValue().equals(true)) {
                ((RootController) fxmlLoader.getController()).setUserModel(userModel);
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.show();
            }
            else {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Invalid username or password.", event).show();
            }
            progressLabel.setVisible(false);
            progressLabel.textProperty().unbind();
        });
        task.setOnFailed(event -> {
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Could not log in.", event).show();
            progressLabel.setVisible(false);
            progressLabel.textProperty().unbind();
        });
    }
}
