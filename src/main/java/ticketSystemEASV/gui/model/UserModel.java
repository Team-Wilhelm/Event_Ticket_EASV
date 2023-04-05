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

public class UserModel {
    private final UserManager userManager = new UserManager();
    private User loggedInUser;
    private HashMap<UUID, User> allEventCoordinators = new HashMap<>();
    private final ObservableList<Role> allRoles = FXCollections.observableArrayList();
    private final HashMap<User, CoordinatorCard> loadedCoordinatorCards = new HashMap<>();

    public UserModel() {
        allRoles.setAll(userManager.getAllRoles());
        getAllEventCoordinatorsFromManager();

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

    public String signUp(User user) {
        String message = userManager.signUp(user);
        getAllEventCoordinatorsFromManager();
        return message;
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

    public String updateUser(User user) {
        String message = userManager.updateUser(user);
        getAllEventCoordinatorsFromManager();
        return message;
    }

    public void deleteUser(User user) {
        userManager.deleteUser(user);
        getAllEventCoordinatorsFromManager();
    }

    public Map<UUID, User> getAllEventCoordinators() {
        return allEventCoordinators;
    }

    public List<Role> getAllRoles() {
        return allRoles;
    }

    public HashMap<User, CoordinatorCard> getLoadedCoordinatorCards() {
        return loadedCoordinatorCards;
    }

    public void getAllEventCoordinatorsFromManager() {
        Callable<HashMap<UUID, User>> setAllEventCoordinatorsRunnable = ()
                -> (HashMap<UUID, User>) userManager.getAllEventCoordinators();

        try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
            Future<HashMap<UUID, User>> future = executorService.submit(setAllEventCoordinatorsRunnable);
            allEventCoordinators = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public User getUser(UUID userID) {
        return userManager.getUser(userID);
    }
}
