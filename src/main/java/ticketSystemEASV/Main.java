package ticketSystemEASV;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root;
        if(true)
            root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/views/MainView.fxml")));
        else
            root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/views/LoginView.fxml")));
        primaryStage.setTitle("EASV Ticket System");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/EASVLogo.png"))));
        //perfectly squished logo
        primaryStage.show();
    }

    public static void main(String[] args) {
        //StartUp.configure();
        launch(args);
    }
}