package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import ticketSystemEASV.gui.model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    @FXML
    private MainViewController mainViewController;
    @FXML
    private BorderPane borderPane;
    @FXML
    private MFXTextField searchBar;
    @FXML
    private MFXComboBox menuDropDown;
    private Model model = new Model();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> searchEvents(searchBar.getText().trim().toLowerCase()));
        mainViewController.setModel(model);
    }

    public void searchEvents(String query) {
        mainViewController.setFilteredEvents(model.searchEvents(query));
    }

    public void setWindow(Node node) {
        borderPane.setCenter(node);
    }
}
