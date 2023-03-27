package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ticketSystemEASV.Main;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.model.UserModel;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.ENTER;

public class LoginViewController implements Initializable {
    private final UserModel userModel = new UserModel();
    @FXML private MFXTextField emailInput;
    @FXML private MFXPasswordField passwordInput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::setEnterKeyAction);
    }

    public void loginUser(Event event) throws IOException {
        if(userModel.logIn(emailInput.getText(), passwordInput.getText())) {
            userModel.setLoggedInUser(userModel.getUserByEmail(emailInput.getText()));
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/views/Root.fxml")));
            Stage stage = (Stage) emailInput.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
        else
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Invalid username or password.", event);
    }

    private void setEnterKeyAction() {
        Scene scene = emailInput.getScene();
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == ENTER) {
                try {
                    loginUser(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
