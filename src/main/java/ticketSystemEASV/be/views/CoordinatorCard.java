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
import ticketSystemEASV.be.User;
import ticketSystemEASV.bll.CropImageToCircle;

import java.io.ByteArrayInputStream;

public class CoordinatorCard extends VBox {
    private final int IMAGE_SIZE = 500;
    private User coordinator;

    // Child nodes
    private ImageView imageView;
    private Image userIcon;
    private Label nameLabel, usernameLabel, mostRecentEvent;
    private String mostRecent;

    public CoordinatorCard(User coordinator) {
        super();
        this.coordinator = coordinator;

        this.setPrefWidth(370);
        this.setPrefHeight(210);
        this.getStyleClass().add("coordinator-view");

        // Picture of the event coordinator
        userIcon = new Image(new ByteArrayInputStream(coordinator.getProfilePicture()), IMAGE_SIZE, IMAGE_SIZE, true, true);
        imageView = new ImageView(CropImageToCircle.getRoundedImage(userIcon, IMAGE_SIZE/2));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

        // Coordinator information
        nameLabel = new Label(coordinator.getName());
        nameLabel.getStyleClass().add("info-label");

        usernameLabel = new Label(coordinator.getUsername());
        usernameLabel.getStyleClass().add("info-label");

        mostRecent = coordinator.getAssignedEvents().size() > 0 ? ((Event) coordinator.getAssignedEvents().values().toArray()[coordinator.getAssignedEvents().size()-1]).getEventName() : "No events assigned";
        mostRecentEvent = new Label(mostRecent);
        mostRecentEvent.getStyleClass().add("info-label");

        // Store all the information in a VBox
        VBox information = new VBox(10);
        information.getStyleClass().add("coordinator-information");
        information.getChildren().addAll(
                new VBox(new Label("Name"), nameLabel),
                new VBox(new Label("Username"), usernameLabel),
                new VBox(new Label("Most recent event"), mostRecentEvent));
        information.setAlignment(Pos.CENTER_LEFT);

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

    public User getCoordinator() {
        return coordinator;
    }

    public void refresh(User coordinator) {
        this.coordinator = coordinator;

        userIcon = new Image(new ByteArrayInputStream(coordinator.getProfilePicture()), IMAGE_SIZE, IMAGE_SIZE, true, true);
        imageView.setImage(CropImageToCircle.getRoundedImage(userIcon, IMAGE_SIZE/2));

        nameLabel.setText(coordinator.getName());
        usernameLabel.setText(coordinator.getUsername());
        mostRecent = coordinator.getAssignedEvents().size() > 0 ? ((Event) coordinator.getAssignedEvents().values().toArray()[coordinator.getAssignedEvents().size()-1]).getEventName() : "No events assigned";
        mostRecentEvent.setText(mostRecent);
    }
}
