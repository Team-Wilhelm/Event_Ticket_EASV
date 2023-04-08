package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.be.views.CoordinatorCard;
import ticketSystemEASV.bll.UserManager;
import ticketSystemEASV.gui.tasks.ConstructCoordinatorCardTask;

import java.util.*;
import java.util.concurrent.*;

public class UserModel implements Model {
    private final UserManager userManager = new UserManager();
    private static User loggedInUser;
    private HashMap<UUID, User> allUsers = new HashMap<>();
    private HashMap<String, Role> allRoles;
    private final HashMap<User, CoordinatorCard> loadedCoordinatorCards = new HashMap<>();

    public UserModel() {
        allRoles = (HashMap<String , Role>) userManager.getAllRoles();
        setAllEventCoordinatorsFromManager(new CountDownLatch(0));

        // Create a new task and construct the CoordinatorCards
        ConstructCoordinatorCardTask task = new ConstructCoordinatorCardTask(List.copyOf(allUsers.values()), this);

        // Start the task
        try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
            executorService.execute(task);
            executorService.shutdown();
        }
    }

    public boolean logIn(String name, String password) {
        return userManager.logIn(name, password);
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        UserModel.loggedInUser = loggedInUser;
    }

    public User getUserByEmail(String email) {
        return allUsers.values().stream().filter(user -> user.getUsername().equals(email)).findFirst().orElse(null);
    }

    public List<User> searchUsers(String query) {
        return userManager.searchUsers(query);
    }

    @Override
    public String add(Object objectToAdd, CountDownLatch latch) {
        User user = (User) objectToAdd;
        String message = userManager.signUp(user);
        setAllEventCoordinatorsFromManager(latch);
        return message;
    }

    @Override
    public String update(Object objectToUpdate, CountDownLatch latch) {
        User user = (User) objectToUpdate;
        String message = userManager.updateUser(user);
        setAllEventCoordinatorsFromManager(latch);
        return message;
    }

    @Override
    public String delete(Object objectToDelete) {
        User user = (User) objectToDelete;
        String message = userManager.deleteUser(user);
        setAllEventCoordinatorsFromManager(new CountDownLatch(0));
        return message;
    }

    public Map<UUID, User> getAllUsers() {
        return allUsers;
    }

    public HashMap<String, Role> getAllRoles() {
        return allRoles;
    }

    public HashMap<User, CoordinatorCard> getLoadedCoordinatorCards() {
        return loadedCoordinatorCards;
    }

    public void setAllEventCoordinatorsFromManager(CountDownLatch latch) {
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
            allUsers = future.get();
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
