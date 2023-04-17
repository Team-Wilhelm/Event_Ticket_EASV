package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.be.User;
import ticketSystemEASV.be.views.EventCard;
import ticketSystemEASV.bll.EventManager;
import ticketSystemEASV.gui.tasks.ConstructEventCardTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class EventModel extends Model {
    private final EventManager eventManager = new EventManager();
    private HashMap<Integer, Event> allEvents = new HashMap<>();
    private final HashMap<Event, EventCard> loadedEventCards = new HashMap<>();
    private TicketModel ticketModel;

    public EventModel() {
        //getAllEventsFromManager();

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
        ticketModel.updateTickets(eventToUpdate.getTickets().values());
        return eventManager.updateEvent(eventToUpdate);
    }

    @Override
    public String delete(Object objectToDelete) {
        Event eventToDelete = (Event) objectToDelete;
        String message = eventManager.deleteEvent(eventToDelete);
        eventManager.deleteEvent(eventToDelete);
        return message;
    }

    public HashMap<Integer, Event> getAllEvents() {
        getAllEventsFromManager();
        return allEvents;
    }

    public List<Event> searchEvents(String query) {
        List<Event> filteredEvents = new ArrayList<>();
        allEvents.values().stream().filter(event -> event.getEventName().toLowerCase().contains(query.toLowerCase())
        || event.getLocation().toLowerCase().contains(query.toLowerCase())
        || event.getStartDate().toString().contains(query.toLowerCase()))
                .forEach(filteredEvents::add);
        return filteredEvents;
    }

    public HashMap<Event, EventCard> getLoadedEventCards() {
        return loadedEventCards;
    }

    public void getAllEventsFromManager() {
        getEventsAssignedToEventCoordinator();
        allEvents = UserModel.getLoggedInUser().getAssignedEvents();

        /*Callable<HashMap<Integer, Event>> setAllEventsRunnable = ()
                -> (HashMap<Integer, Event>) eventManager.getAllEvents();

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            Future<HashMap<Integer, Event>> future = executorService.submit(setAllEventsRunnable);
            allEvents = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        shutdownExecutorService(executorService);*/
    }
    public void getEventsAssignedToEventCoordinator(){
        eventManager.getEventsAssignedToEventCoordinator();
    }

    public void assignCoordinatorToEvent(User user, Event event){
        eventManager.assignCoordinatorToEvent(user, event);
        eventManager.getEventsAssignedToEventCoordinator(user);
    }

    public Event getEvent(int id) {
        return eventManager.getEvent(id);
    }

    public void unassignCoordinatorFromEvent(User user, Event event) {
        eventManager.unassignCoordinatorFromEvent(user, event);
        eventManager.getEventsAssignedToEventCoordinator(user);
    }

    public void setTicketModel(TicketModel ticketModel) {
        this.ticketModel = ticketModel;
    }
}
