package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
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

public class LoginViewController implements Initializable {
    private final UserModel userModel = new UserModel();
    public MFXTextField emailInput;
    public MFXPasswordField passwordInput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void loginUser(ActionEvent actionEvent) throws IOException {
        if(userModel.logIn(emailInput.getText(), passwordInput.getText())) {
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/views/MainView.fxml")));
            Stage stage = (Stage) emailInput.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
        else
            System.out.println("Failed to log in");
    }
}
