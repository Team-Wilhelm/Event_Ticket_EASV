package ticketSystemEASV.be;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticketSystemEASV.Main;

import java.io.IOException;
import java.util.Objects;

public class LoadingScreen {
    private static LoadingScreen instance;
    private Stage loadingStage;

    private LoadingScreen() throws IOException {
        loadingStage = new Stage();
        loadingStage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/views/LoadingScreen.fxml")))));
        loadingStage.setResizable(false);
        loadingStage.centerOnScreen();
        loadingStage.setTitle("Loading");
        loadingStage.initModality(Modality.APPLICATION_MODAL);
    }

    public static LoadingScreen getInstance() {
        if (instance == null) {
            try {
                instance = new LoadingScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Stage getLoadingStage() {
        return loadingStage;
    }

    public void showLoadingScreen() {
        loadingStage.show();
    }

    public void hideLoadingScreen() {
        loadingStage.hide();
    }
}
