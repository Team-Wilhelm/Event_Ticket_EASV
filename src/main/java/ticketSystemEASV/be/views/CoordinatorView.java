package ticketSystemEASV.be.views;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import ticketSystemEASV.be.EventCoordinator;
import ticketSystemEASV.bll.CropImageToCircle;

public class CoordinatorView extends HBox {
    private Image userIcon = new Image("images/userIcon.png", 75.0, 75.0, true, true);
    private EventCoordinator coordinator;
    public CoordinatorView(EventCoordinator coordinator) {
        super();
        this.coordinator = coordinator;

        this.getStyleClass().add("coordinator-view");

        // Picture of the event coordinator
        ImageView imageView = new ImageView(CropImageToCircle.getRoundedImage(userIcon, 75/2));
        imageView.setFitWidth(75);
        imageView.setFitHeight(75);

        // Coordinator information
        Label nameLabel = new Label(coordinator.getName());
        Label usernameLabel = new Label("Username: " + coordinator.getUsername());
        Label idLabel = new Label("ID: " + coordinator.getId());

        // Store all the information in a VBox
        VBox information = new VBox(10);
        information.getChildren().addAll(imageView, nameLabel, usernameLabel, idLabel);

        // Events assigned to this coordinator
        VBox events = new VBox(10, new Label("Assigned events:"));
        for (var event : coordinator.getAssignedEvents()) {
            Label eventLabel = new Label("-" + event.getEventName());
            events.getChildren().add(eventLabel);
        }

        // Add all the elements to the VBox
        this.setPadding(new Insets(15,15,15,15));
        this.getChildren().addAll(information, events);

        // When vbox is clicked focus on it
        this.setOnMouseClicked(event -> {
            if (!this.isFocused())
                this.requestFocus();
        });

        // Use different backgrounds for focused and unfocused states
        this.backgroundProperty().bind(Bindings
                .when(this.focusedProperty())
                .then(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)))
                .otherwise(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))));
    }

    public EventCoordinator getCoordinator() {
        return coordinator;
    }
}
