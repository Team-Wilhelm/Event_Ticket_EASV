package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.views.EventCard;
import ticketSystemEASV.bll.EventManager;
import ticketSystemEASV.gui.tasks.ConstructEventCardTask;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class EventModel implements Model {
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


    @Override
    public String add(Object objectToAdd, CountDownLatch latch) {
        Event eventToSave = (Event) objectToAdd;
        return eventManager.saveEvent(eventToSave);
    }

    @Override
    public String update(Object objectToUpdate, CountDownLatch latch) {
        Event eventToUpdate = (Event) objectToUpdate;
        return eventManager.updateEvent(eventToUpdate);
    }

    @Override
    public void delete(Object objectToDelete) {
        Event eventToDelete = (Event) objectToDelete;
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

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            Future<HashMap<Integer, Event>> future = executorService.submit(setAllEventsRunnable);
            allEvents = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            executorService.shutdown();
        }
        finally {
            if (!executorService.isShutdown())
                executorService.shutdown();
        }
    }

    public Event getEvent(int id) {
        return eventManager.getEvent(id);
    }
}
