package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.views.EventCard;
import ticketSystemEASV.bll.EventManager;
import ticketSystemEASV.gui.tasks.ConstructEventCardTask;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class EventModel {
    private final EventManager eventManager = new EventManager();
    private HashMap<Integer, Event> allEvents = new HashMap<>();
    private HashMap<Event, EventCard> loadedEventCards = new HashMap<>();

    public EventModel() {
        getAllEventsFromManager();

        // Create a new task and bind it to the eventCards list
        ConstructEventCardTask task = new ConstructEventCardTask(List.copyOf(allEvents.values()), this);

        // Start the task
        try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
            executorService.execute(task);
            executorService.shutdown();
        }
    }

    public void saveEvent(Event eventToSave) {
        eventManager.saveEvent(eventToSave);
    }

    public void updateEvent(Event eventToUpdate) {
        eventManager.updateEvent(eventToUpdate);
    }

    public void deleteEvent(Event eventToDelete) {
        eventManager.deleteEvent(eventToDelete);
    }

    public HashMap<Integer, Event> getAllEvents() {
        getAllEventsFromManager();
        return allEvents;
    }

    public List<Event> searchEvents(String query) {
        return eventManager.searchEvents(query);
    }

    public HashMap<Event, EventCard> getLoadedEventCards() {
        return loadedEventCards;
    }

    public void getAllEventsFromManager() {
        Callable<HashMap<Integer, Event>> setAllEventsRunnable = ()
                -> (HashMap<Integer, Event>) eventManager.getAllEvents();

        try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
            Future<HashMap<Integer, Event>> future = executorService.submit(setAllEventsRunnable);
            allEvents = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
