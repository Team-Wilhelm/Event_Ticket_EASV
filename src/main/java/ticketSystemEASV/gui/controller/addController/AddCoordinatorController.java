package ticketSystemEASV.gui.controller.addController;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import ticketSystemEASV.be.EventCoordinator;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.bll.CropImageToCircle;
import ticketSystemEASV.gui.controller.ManageCoordinatorsController;
import ticketSystemEASV.gui.model.Model;
import javafx.fxml.FXML;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.passay.DigestDictionaryRule.ERROR_CODE;

public class AddCoordinatorController implements Initializable{
    private Model model;
    private ManageCoordinatorsController manageCoordinatorsController;
    private boolean isEditing = false;
    private EventCoordinator coordinatorToEdit;
    @FXML
    private MFXTextField txtCoordinatorName, txtPassword, txtUsername;
    @FXML
    private GridPane gridPane;
    private ImageView imgViewProfilePicture;
    private String profilePicturePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpProfilePicture();
    }

    private void setUpProfilePicture() {
        int IMAGE_SIZE = 500;
        Image userIcon = new Image("images/userProfilePictures/userIcon.png", IMAGE_SIZE, IMAGE_SIZE, false, true);
        imgViewProfilePicture = new ImageView(CropImageToCircle.getRoundedImage(userIcon,IMAGE_SIZE/2));
        imgViewProfilePicture.fitWidthProperty().bind(txtCoordinatorName.widthProperty().subtract(txtCoordinatorName.widthProperty().divide(2)));
        imgViewProfilePicture.fitHeightProperty().bind(imgViewProfilePicture.fitWidthProperty());
        imgViewProfilePicture.preserveRatioProperty().setValue(false);
        gridPane.add(imgViewProfilePicture, 0,1);

        imgViewProfilePicture.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose profile picture");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                        new FileChooser.ExtensionFilter("All Files", "*.*"));
                File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
                if (selectedFile != null) {
                    Image image = new Image(selectedFile.toURI().toString(), IMAGE_SIZE, IMAGE_SIZE, false, true);
                    imgViewProfilePicture.setImage(CropImageToCircle.getRoundedImage(image, IMAGE_SIZE / 2));
                }
            }
        });
    }

    public void cancelAction(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void saveAction(ActionEvent actionEvent) {
        String coordinatorName = txtCoordinatorName.getText();
        String username = txtUsername.getText();
        final String[] password = {txtPassword.getText()};
        //System.out.println(profilePicturePath);

        if (coordinatorName.isEmpty() || username.isEmpty()) {
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Please, fill out all required fields!", actionEvent).showAndWait();
        } else {
            new Thread(() -> {
                if (!isEditing){
                    model.addCoordinator(new EventCoordinator(coordinatorName, username, password[0]));
                }
            else {
                    if (password[0].isEmpty())
                        password[0] = coordinatorToEdit.getPassword();
                    model.updateCoordinator(new EventCoordinator(coordinatorToEdit.getId(), coordinatorName, username, password[0]));
                }
            }).start();

            Platform.runLater(() -> manageCoordinatorsController.refreshItems());
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
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
        //TODO maybe change this
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

    public void setManageCoordinatorsController(ManageCoordinatorsController manageCoordinatorsController) {
        this.manageCoordinatorsController = manageCoordinatorsController;
    }
}
