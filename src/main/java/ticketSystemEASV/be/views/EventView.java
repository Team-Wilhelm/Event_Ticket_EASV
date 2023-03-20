package ticketSystemEASV.be.views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.EventCoordinator;

public class EventView extends VBox {
    private Event event;
    public EventView(Event event) {
        super();
        this.event = event;

        this.getStyleClass().add("event-view");
        // Poster of the event
        ImageView imageView = new ImageView("data:image/jpeg;base64,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");
        imageView.setFitWidth(280);
        imageView.setFitHeight(140);

        // Name of the event
        Label nameLabel = new Label("Event: " + event.getEventName());

        // Date of the event
        Label dateLabel = new Label("Date: " + event.getStartDate().toString());

        // Time of the event
        Label timeLabel = new Label("Time: " + event.getStartTime().toString());

        // Location of the event
        Label locationLabel = new Label("Location: " + event.getLocation());

        // All the information about the event
        VBox vBox = new VBox(10);
        vBox.getChildren().add(nameLabel);
        vBox.getChildren().add(dateLabel);
        vBox.getChildren().add(timeLabel);
        vBox.getChildren().add(locationLabel);

        vBox.setPadding(new Insets(10,10,10,10));
        vBox.backgroundProperty().set(new Background(new BackgroundFill(javafx.scene.paint.Color.WHITE,null,null)));

        // Add all the elements to the VBox
        this.getChildren().addAll(imageView,vBox);

        this.setOnMouseClicked(e -> {
            if (!this.isFocused())
                this.requestFocus();
        });
    }

    public Event getEvent() {
        return event;
    }
}
