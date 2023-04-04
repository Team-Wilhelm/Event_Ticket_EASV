package ticketSystemEASV.bll.tasks;

import javafx.concurrent.Task;
import ticketSystemEASV.be.User;
import ticketSystemEASV.be.views.CoordinatorCard;
import ticketSystemEASV.gui.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConstructCoordinatorCardTask extends Task<List<CoordinatorCard>> {
    private final List<User> coordinators;
    private final UserModel userModel;
    private final List<CoordinatorCard> constructedCoordinatorCards;
    private final HashMap<User, CoordinatorCard> loadedCoordinatorCards;

    public ConstructCoordinatorCardTask(List<User> coordinators, UserModel userModel) {
        this.coordinators = coordinators;
        this.userModel = userModel;
        this.constructedCoordinatorCards = new ArrayList<>();
        this.loadedCoordinatorCards = userModel.getLoadedCoordinatorCards();
    }

    @Override
    protected List<CoordinatorCard> call() throws Exception {
        for (User coordinator : coordinators) {
            if (isCancelled()) {
                updateMessage("Cancelled");
                break;
            }
            else {
                updateProgress(constructedCoordinatorCards.size(), coordinators.size());

                CoordinatorCard coordinatorCard;
                if (loadedCoordinatorCards.get(coordinator) == null) {
                    coordinatorCard = new CoordinatorCard(coordinator);
                    userModel.getLoadedCoordinatorCards().put(coordinator, coordinatorCard);
                    loadedCoordinatorCards.put(coordinator, coordinatorCard);
                } else {
                    coordinatorCard = loadedCoordinatorCards.get(coordinator);
                }
                constructedCoordinatorCards.add(coordinatorCard);
                updateValue(constructedCoordinatorCards);
            }
        }
        return constructedCoordinatorCards;
    }
}
