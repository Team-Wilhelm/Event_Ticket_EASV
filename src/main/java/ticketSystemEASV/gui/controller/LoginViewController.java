package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ticketSystemEASV.Main;
import ticketSystemEASV.gui.model.UserModel;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.ENTER;

public class LoginViewController implements Initializable {
    private final UserModel userModel = new UserModel();
    public MFXTextField emailInput;
    public MFXPasswordField passwordInput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::setEnterKeyAction);
    }

    public void loginUser(Event event) throws IOException {
        if(userModel.logIn(emailInput.getText(), passwordInput.getText())) {
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/views/Root.fxml")));
            Stage stage = (Stage) emailInput.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
        else
            System.out.println("Failed to log in");
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
