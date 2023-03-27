package ticketSystemEASV.be.views;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.EventCoordinator;
import ticketSystemEASV.bll.CropImageToCircle;

public class CoordinatorView extends VBox {
    private final int IMAGE_SIZE = 150;
    private final Image userIcon = new Image("images/userProfilePictures/userIcon.png", IMAGE_SIZE, IMAGE_SIZE, true, true);
    private EventCoordinator coordinator;

    public CoordinatorView(EventCoordinator coordinator) {
        super();
        this.coordinator = coordinator;

        this.setPrefWidth(370);
        this.setPrefHeight(210);
        this.getStyleClass().add("coordinator-view");

        // Picture of the event coordinator
        ImageView imageView = new ImageView(CropImageToCircle.getRoundedImage(userIcon, IMAGE_SIZE/2));
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);

        // Coordinator information
        Label nameLabel = new Label(coordinator.getName());
        nameLabel.getStyleClass().add("info-label");

        Label usernameLabel = new Label(coordinator.getUsername());
        usernameLabel.getStyleClass().add("info-label");

        String mostRecent = coordinator.getAssignedEvents().size() > 0 ? ((Event) coordinator.getAssignedEvents().toArray()[0]).getEventName() : "No events assigned";
        Label mostRecentEvent = new Label(mostRecent);
        mostRecentEvent.getStyleClass().add("info-label");

        // Store all the information in a VBox
        VBox information = new VBox(10);
        information.getStyleClass().add("coordinator-information");
        information.getChildren().addAll(
                new VBox(new Label("Name"), nameLabel),
                new VBox(new Label("Username"), usernameLabel),
                new VBox(new Label("Most recent event"), mostRecentEvent));
        information.setAlignment(Pos.CENTER_LEFT);

        // Events assigned to this coordinator
        VBox events = new VBox(10, new Label("Assigned events:"));
        for (var event : coordinator.getAssignedEvents()) {
            Label eventLabel = new Label("-" + event.getEventName());
            events.getChildren().add(eventLabel);
        }

        // Add all the elements to the VBox
        this.setPadding(new Insets(15,15,15,15));

        HBox hBox = new HBox(20, imageView, information);
        VBox filler = new VBox();
        VBox.setVgrow(filler, Priority.ALWAYS);
        this.getChildren().addAll(hBox, filler, new Label("ID: " + coordinator.getId()));

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
