package ticketSystemEASV.gui.controller.addController;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.bll.CropImageToCircle;
import ticketSystemEASV.gui.tasks.SaveTask;
import ticketSystemEASV.gui.controller.viewControllers.ManageCoordinatorsController;
import javafx.fxml.FXML;
import ticketSystemEASV.gui.model.UserModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.passay.DigestDictionaryRule.ERROR_CODE;

public class AddCoordinatorController extends AddObjectController implements Initializable {
    @FXML
    private MFXTextField txtCoordinatorName, txtPassword, txtUsername;
    @FXML
    private GridPane gridPane;
    private UserModel userModel;
    private ManageCoordinatorsController manageCoordinatorsController;
    private boolean isEditing = false;
    private int IMAGE_SIZE;
    private User coordinatorToEdit;
    private ImageView imgViewProfilePicture;
    private SaveTask saveTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpProfilePicture();
    }

    private void setUpProfilePicture() {
        IMAGE_SIZE = 500;
        Image userIcon;
        if (isEditing)
            userIcon = new Image(new ByteArrayInputStream(coordinatorToEdit.getProfilePicture()), IMAGE_SIZE, IMAGE_SIZE, true, true);
        else
            userIcon = new Image("images/userProfilePictures/userIcon.png", IMAGE_SIZE, IMAGE_SIZE, true, true);

        imgViewProfilePicture = new ImageView(CropImageToCircle.getRoundedImage(userIcon,IMAGE_SIZE/2));
        imgViewProfilePicture.fitWidthProperty().bind(txtCoordinatorName.widthProperty().subtract(txtCoordinatorName.widthProperty().divide(2)));
        imgViewProfilePicture.fitHeightProperty().bind(imgViewProfilePicture.fitWidthProperty());
        imgViewProfilePicture.preserveRatioProperty().setValue(true);
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
        Scene source = ((Node) actionEvent.getSource()).getScene();

        String coordinatorName = txtCoordinatorName.getText();
        String username = txtUsername.getText();
        final String[] password = {txtPassword.getText()};

        if (coordinatorName.isEmpty() || username.isEmpty() || (password[0].isEmpty() && !isEditing)) {
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Please, fill out all required fields!", actionEvent).showAndWait();
        } else {
            source.getWindow().hide();

            Role role = userModel.getAllRoles().stream().filter(r -> r.getName().equals("EventCoordinator")).findFirst().get();
            byte[] finalProfilePicture = getProfilePictureAsBytes();

            if (password[0].isEmpty())
                password[0] = coordinatorToEdit.getPassword();

            User user = new User(coordinatorName, username, password[0], role, finalProfilePicture);
            if (isEditing) user.setId(coordinatorToEdit.getId());

            saveTask = new SaveTask(user, isEditing, userModel);
            setUpTask(saveTask, actionEvent, manageCoordinatorsController);
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(saveTask);

            // Try to shut down the executor service, if it fails, throw a runtime exception and force shutdown
            try {
                executorService.shutdown();
            } finally {
                if (!executorService.isTerminated()) {
                    executorService.shutdownNow();
                }
            }
        }
    }

    public void deleteCoordinatorAction(ActionEvent actionEvent) {
        if (isEditing){
            Alert alert = AlertManager.getInstance().getAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this coordinator?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                manageCoordinatorsController.deleteCard(coordinatorToEdit);
                userModel.delete(coordinatorToEdit);
            }
        }
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    @Override
    public void setIsEditing(Object object) {
        isEditing = true;
        User coordinator = (User) object;
        coordinatorToEdit = coordinator;
        txtCoordinatorName.setText(coordinator.getName());
        txtUsername.setText(coordinator.getUsername());
        imgViewProfilePicture.setImage(CropImageToCircle.getRoundedImage(
                new Image(new ByteArrayInputStream(coordinator.getProfilePicture()), IMAGE_SIZE, IMAGE_SIZE, true, true), IMAGE_SIZE/2));
    }

    public void setModel(UserModel userModel) {
        this.userModel = userModel;
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

    private byte[] getProfilePictureAsBytes() {
        byte[] profilePicture = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imgViewProfilePicture.getImage(), null);
            ImageIO.write(bufferedImage, "png", baos);
            profilePicture = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profilePicture;
    }
}
