package ticketSystemEASV.gui.controller.addController;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.be.User;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.bll.CropImageToCircle;
import ticketSystemEASV.gui.controller.viewControllers.MotherController;
import ticketSystemEASV.gui.tasks.DeleteTask;
import ticketSystemEASV.gui.tasks.SaveTask;
import ticketSystemEASV.gui.controller.viewControllers.ManageCoordinatorsController;
import javafx.fxml.FXML;
import ticketSystemEASV.gui.model.UserModel;
import ticketSystemEASV.gui.tasks.TaskState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.passay.DigestDictionaryRule.ERROR_CODE;

public class AddCoordinatorController extends AddObjectController implements Initializable {
    @FXML
    private MFXTextField txtCoordinatorName, txtPassword, txtUsername;
    @FXML
    private GridPane gridPane;
    @FXML
    private MFXTableView<Event> tblViewEvents;
    @FXML
    private MFXTableColumn<Event> tblColEventID, tblColEventName, tblColLocation;
    private Label progressLabel;
    private MFXProgressSpinner progressSpinner;
    private UserModel userModel;
    private ManageCoordinatorsController manageCoordinatorsController;
    private boolean isEditing, isManagingOwnAccount;
    private int IMAGE_SIZE;
    private User coordinatorToEdit;
    private ImageView imgViewProfilePicture;
    private Task<TaskState> task;
    private String coordinatorName, username, password;
    private final AlertManager alertManager = AlertManager.getInstance();
    private Scene scene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isEditing = false;
        isManagingOwnAccount = false;
        setUpProfilePicture();
        setUpTableView();
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
        coordinatorName = txtCoordinatorName.getText();
        username = txtUsername.getText();
        password = txtPassword.getText();

        if (checkInput(actionEvent)) {
            if (!isManagingOwnAccount)
                ((Node) actionEvent.getSource()).getScene().getWindow().hide();

            final String[] passwordFinal = {password};
            Role role = coordinatorToEdit.getRole() != null ? coordinatorToEdit.getRole() : userModel.getAllRoles().get("EventCoordinator");
            byte[] finalProfilePicture = getProfilePictureAsBytes();

            if (passwordFinal[0].isEmpty())
                passwordFinal[0] = coordinatorToEdit.getPassword();

            User user = new User(coordinatorName, username, passwordFinal[0], role, finalProfilePicture);
            if (isEditing) user.setId(coordinatorToEdit.getId());

            if (isManagingOwnAccount) {
                task = new SaveTask(user, isEditing, userModel);
                setUpTaskWhileManagingOwnAccount(task, actionEvent);
            }
            else {
                task = new SaveTask(user, isEditing, userModel);
                setUpSaveTask(task, actionEvent, manageCoordinatorsController);
            }
            executeTask(task);
        }
    }

    public void deleteCoordinatorAction(ActionEvent actionEvent) {
        if (isEditing){
            Alert alert = AlertManager.getInstance().getAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this coordinator?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                task = new DeleteTask(coordinatorToEdit, userModel);
                setUpDeleteTask(task, actionEvent, manageCoordinatorsController);
                executeTask(task);
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
        txtPassword.setPromptText("Leave blank to keep current password");

        Bindings.bindContentBidirectional(tblViewEvents.getItems(), FXCollections.observableArrayList(coordinatorToEdit.getAssignedEvents().values()));
    }

    public void setModel(UserModel userModel) {
        this.userModel = userModel;
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

    private boolean checkInput(ActionEvent actionEvent) {
        //TODO disable save button while input is invalid
        if (userModel.getAllUsers().values().stream().anyMatch(user -> user.getUsername().equals(username)
        && !username.equals(coordinatorToEdit.getUsername()))) {
            alertManager.getAlert(Alert.AlertType.ERROR, "Username already exists!", actionEvent).showAndWait();
            return false;
        }
        if (coordinatorName.isEmpty() || username.isEmpty() || (password.isEmpty() && !isEditing)) {
            alertManager.getAlert(Alert.AlertType.ERROR, "Please, fill out all required fields!", actionEvent).showAndWait();
            return false;
        }
        return true;
    }

    public void setIsManagingOwnAccount(boolean isManagingOwnAccount) {
        this.isManagingOwnAccount = isManagingOwnAccount;

        progressLabel = new Label("");
        progressLabel.setId("progressLabel");
        progressSpinner = new MFXProgressSpinner();
        progressSpinner.setId("progressSpinner");
        HBox hbox = new HBox(5, progressSpinner, progressLabel);
        hbox.setAlignment(Pos.CENTER);

        MFXButton btnLogOut = new MFXButton("Log out");
        btnLogOut.setId("btnLogOut");

        btnLogOut.setOnAction(event -> {
            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            try {
               stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"))));
               stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        if (isManagingOwnAccount) {
            gridPane.add(hbox, 1, 5);
            gridPane.add(btnLogOut, 0, 0);
            GridPane.setHalignment(btnLogOut, HPos.LEFT);

            progressSpinner.setVisible(false);
            progressLabel.setVisible(false);
            txtPassword.setPromptText("Leave blank to keep current password");
        }
    }

    private void setUpTaskWhileManagingOwnAccount(Task<TaskState> task, ActionEvent actionEvent) {
        task.setOnRunning(event -> {
            progressSpinner.progressProperty().bind(task.progressProperty());
            progressSpinner.setVisible(true);
            progressLabel.setVisible(true);
            progressLabel.textProperty().bind(task.messageProperty());
        });

        task.setOnFailed(event -> {
            progressSpinner.setVisible(false);
            progressLabel.setVisible(false);
            progressSpinner.progressProperty().unbind();
            progressLabel.textProperty().unbind();
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Something went wrong!", actionEvent).showAndWait();
        });

        task.setOnSucceeded(event -> {
            // unbind the progress label from the task and set it to full
            progressSpinner.progressProperty().unbind();
            progressSpinner.setProgress(100);

            // after 3 seconds, the progress bar will be hidden
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            progressSpinner.setVisible(false);
                            progressLabel.setVisible(false);
                            progressLabel.textProperty().unbind();
                        }
                    },
                    3000
            );

            if (task.getValue() == TaskState.CHOSEN_NAME_ALREADY_EXISTS) {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Username already exists!", actionEvent).showAndWait();
            } else if (task.getValue() == TaskState.NOT_SUCCESSFUL) {
                AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Something went wrong!", actionEvent).showAndWait();
            }
        });
    }

    private void setUpTableView() {
        tblColEventID = new MFXTableColumn<>("Event ID", true);
        tblColEventName = new MFXTableColumn<>("Event name", true);
        tblColLocation = new MFXTableColumn<>("Location", true);

        tblColEventID.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getId));
        tblColEventName.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getEventName));
        tblColLocation.setRowCellFactory(event -> new MFXTableRowCell<>(Event::getLocation));

        tblViewEvents.getTableColumns().addAll(tblColEventID, tblColEventName, tblColLocation);
    }
}
