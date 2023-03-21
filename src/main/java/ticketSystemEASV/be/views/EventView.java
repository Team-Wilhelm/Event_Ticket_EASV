package ticketSystemEASV.be.views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ticketSystemEASV.be.Event;

public class EventView extends VBox {
    private Event event;

    public EventView(Event event) {
        super();
        this.event = event;

        this.getStyleClass().add("event-view");
        //
        HBox top = new HBox();
        top.getStyleClass().add("event-view-top");
        top.setPrefWidth(200);
        top.setPrefHeight(100);

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
        this.getChildren().addAll(top,vBox);

        this.setOnMouseClicked(e -> {
            if (!this.isFocused())
                this.requestFocus();
        });
    }

    public Event getEvent() {
        return event;
    }
}
