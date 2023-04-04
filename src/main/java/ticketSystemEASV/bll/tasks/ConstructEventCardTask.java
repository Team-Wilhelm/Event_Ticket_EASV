package ticketSystemEASV.bll.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.views.EventCard;
import ticketSystemEASV.gui.model.EventModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConstructEventCardTask extends Task<List<EventCard>> {
    private final List<Event> events;
    private final EventModel eventModel;
    private final List<EventCard> constructedEventCards;
    private final HashMap<Event, EventCard> loadedEventCards;

    public ConstructEventCardTask(List<Event> events, EventModel eventModel) {
        this.events = events;
        this.eventModel = eventModel;
        this.constructedEventCards = new ArrayList<>();
        this.loadedEventCards = eventModel.getLoadedEventCards();
    }

    @Override
    protected List<EventCard> call() throws Exception {
        for (Event event : events) {
            if (isCancelled()) {
                updateMessage("Cancelled");
                break;
            }
            else {
                updateProgress(constructedEventCards.size(), events.size());

                EventCard eventCard;
                if (loadedEventCards.get(event) == null) {
                    eventCard = new EventCard(event);
                    eventModel.getLoadedEventCards().put(event, eventCard);
                    loadedEventCards.put(event, eventCard);
                } else {
                    eventCard = loadedEventCards.get(event);
                }
                constructedEventCards.add(eventCard);
                updateValue(constructedEventCards);
            }
        }
        return constructedEventCards;
    }
}
