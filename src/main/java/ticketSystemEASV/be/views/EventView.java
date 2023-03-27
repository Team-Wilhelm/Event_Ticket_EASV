package ticketSystemEASV.be.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ticketSystemEASV.be.Event;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class EventView extends VBox {
    private Event event;
    private final Image mapPin = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/map-pin.png")));
    private final Image calendar = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/calendar.png")));
    private final Image clock = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/clock.png")));


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
        ImageView nameIV = new ImageView();
        Label nameLabel = new Label(event.getEventName());
        nameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        nameLabel.maxWidthProperty().bind(top.prefWidthProperty());

        nameIV.setPreserveRatio(true);
        nameIV.setSmooth(true);
        nameIV.fitHeightProperty().bind(nameLabel.heightProperty());

        HBox nameBox = new HBox(5, nameIV, nameLabel);
        nameBox.alignmentProperty().set(Pos.CENTER_LEFT);

        // Date of the event
        ImageView calendarIV = new ImageView(calendar);
        Label dateLabel = new Label(new SimpleDateFormat("dd.MM.yyyy").format(event.getStartDate()));
        calendarIV.setPreserveRatio(true);
        calendarIV.setSmooth(true);
        calendarIV.fitHeightProperty().bind(dateLabel.heightProperty());

        HBox dateBox = new HBox(5, calendarIV, dateLabel);
        dateBox.alignmentProperty().set(Pos.CENTER_LEFT);

        // Time of the event
        ImageView timeIV = new ImageView(clock);
        Label timeLabel = new Label(new SimpleDateFormat("HH:mm").format(event.getStartTime()));
        timeIV.setPreserveRatio(true);
        timeIV.setSmooth(true);
        timeIV.fitHeightProperty().bind(timeLabel.heightProperty());

        HBox timeBox = new HBox(5, timeIV, timeLabel);
        timeBox.alignmentProperty().set(Pos.CENTER_LEFT);

        // Location of the event
        ImageView locationIV = new ImageView(mapPin);
        Label locationLabel = new Label(event.getLocation());

        locationLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        locationLabel.maxWidthProperty().bind(top.prefWidthProperty());

        locationIV.setPreserveRatio(true);
        locationIV.setSmooth(true);
        locationIV.fitHeightProperty().bind(locationLabel.heightProperty());

        HBox locationBox = new HBox(5, locationIV, locationLabel);
        locationBox.alignmentProperty().set(Pos.CENTER_LEFT);

        // All the information about the event
        VBox vBox = new VBox(10);
        vBox.getStyleClass().add("event-view-bottom");
        vBox.getChildren().add(nameBox);
        vBox.getChildren().add(dateBox);
        vBox.getChildren().add(timeBox);
        vBox.getChildren().add(locationBox);

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
