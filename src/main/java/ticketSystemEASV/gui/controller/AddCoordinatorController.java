package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.EventCoordinator;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.model.Model;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

import static org.passay.DigestDictionaryRule.ERROR_CODE;

public class AddCoordinatorController {
    private Model model;
    private boolean isEditing = false;
    private EventCoordinator coordinatorToEdit;

    @FXML
    private MFXTextField txtCoordinatorName, txtPassword, txtUsername;

    public void cancelAction(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void saveAction(ActionEvent actionEvent) {
        String coordinatorName = txtCoordinatorName.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (coordinatorName.isEmpty() || username.isEmpty()) {
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Please, fill out all required fields!", actionEvent).showAndWait();
        } else {
            if (!isEditing){
                model.addCoordinator(new EventCoordinator(coordinatorName, username, password));
                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }
            else {
                if (password.isEmpty())
                    password = coordinatorToEdit.getPassword();
                model.updateCoordinator(new EventCoordinator(coordinatorToEdit.getId(), coordinatorName, username, password));
                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }
        }
    }

    public void setIsEditing(EventCoordinator coordinator) {
        isEditing = true;
        coordinatorToEdit = coordinator;
        txtCoordinatorName.setText(coordinator.getName());
        txtUsername.setText(coordinator.getUsername());
    }

    public void setModel(Model model) {
        this.model = model;
    }

    private String createRandomPassword(){
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(10, splCharRule, lowerCaseRule, upperCaseRule, digitRule);
    }

    public void generatePassword(ActionEvent actionEvent) {
        txtPassword.setText(createRandomPassword());
    }
}
