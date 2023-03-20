package ticketSystemEASV.be.views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ticketSystemEASV.be.EventCoordinator;
import ticketSystemEASV.bll.CropImageToCircle;

public class CoordinatorView extends VBox{
    private Image userIcon = new Image("images/userIcon.png", 75.0, 75.0, true, true);
    private EventCoordinator coordinator;
    public CoordinatorView(EventCoordinator coordinator) {
        super();
        this.coordinator = coordinator;

        this.getStyleClass().add("coordinator-view");

        // Picture of the event coordinator
        ImageView imageView = new ImageView(CropImageToCircle.getRoundedImage(userIcon, 75/2));

        //imageView.getStyleClass().add("user-image-view");
        imageView.setFitWidth(75);
        imageView.setFitHeight(75);

        // Name of the coordinator
        Label nameLabel = new Label("Name: " + coordinator.getName());

        // All the information about the event
        VBox vBox = new VBox(10);
        vBox.getChildren().add(nameLabel);

        vBox.setPadding(new Insets(10,10,10,10));
        vBox.backgroundProperty().set(new Background(new BackgroundFill(Color.GREEN,null,null)));

        // Add all the elements to the VBox
        this.getChildren().addAll(imageView,vBox);
    }
}
