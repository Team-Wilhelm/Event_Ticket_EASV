package ticketSystemEASV.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.be.views.CoordinatorCard;
import ticketSystemEASV.bll.UserManager;
import ticketSystemEASV.gui.tasks.ConstructCoordinatorCardTask;

import java.util.*;
import java.util.concurrent.*;

public class UserModel implements Model {
    private final UserManager userManager = new UserManager();
    private User loggedInUser;
    private HashMap<UUID, User> allEventCoordinators = new HashMap<>();
    private HashMap<UUID, Role> allRoles;
    private final HashMap<User, CoordinatorCard> loadedCoordinatorCards = new HashMap<>();

    public UserModel() {
        allRoles = (HashMap<UUID, Role>) userManager.getAllRoles();
        getAllEventCoordinatorsFromManager(new CountDownLatch(0));

        // Create a new task and construct the CoordinatorCards
        ConstructCoordinatorCardTask task = new ConstructCoordinatorCardTask(List.copyOf(allEventCoordinators.values()), this);

        // Start the task
        try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
            executorService.execute(task);
            executorService.shutdown();
        }
    }

    public boolean logIn(String name, String password) {
        return userManager.logIn(name, password);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public User getUserByEmail(String email) {
        return userManager.getUserByEmail(email);
    }

    public List<User> searchUsers(String query) {
        return userManager.searchUsers(query);
    }

    @Override
    public String add(Object objectToAdd, CountDownLatch latch) {
        User user = (User) objectToAdd;
        String message = userManager.signUp(user);
        getAllEventCoordinatorsFromManager(latch);
        return message;
    }

    @Override
    public String update(Object objectToUpdate, CountDownLatch latch) {
        User user = (User) objectToUpdate;
        String message = userManager.updateUser(user);
        getAllEventCoordinatorsFromManager(latch);
        return message;
    }

    @Override
    public String delete(Object objectToDelete) {
        User user = (User) objectToDelete;
        String message = userManager.deleteUser(user);
        getAllEventCoordinatorsFromManager(new CountDownLatch(0));
        return message;
    }

    public Map<UUID, User> getAllEventCoordinators() {
        return allEventCoordinators;
    }

    public HashMap<UUID, Role> getAllRoles() {
        return allRoles;
    }

    public HashMap<User, CoordinatorCard> getLoadedCoordinatorCards() {
        return loadedCoordinatorCards;
    }

    public void getAllEventCoordinatorsFromManager(CountDownLatch latch) {
        Callable<HashMap<UUID, User>> setAllEventCoordinatorsRunnable = ()
                -> (HashMap<UUID, User>) userManager.getAllEventCoordinators();

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<HashMap<UUID, User>> future = executorService.submit(() -> {
            HashMap<UUID, User> result = setAllEventCoordinatorsRunnable.call();
            latch.countDown();
            return result;
        });

        try {
            latch.await();
            allEventCoordinators = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Try to shut down the executor service, if it fails, throw a runtime exception and force shutdown
        try {
            executorService.shutdown();
        } finally {
            if (!executorService.isShutdown())
                executorService.shutdownNow();
        }
    }

    public User getUser(UUID userID) {
        return userManager.getUser(userID);
    }

    public boolean isAdmin() {
        return loggedInUser.getRole().getName().equals("Admin");
    }
}
