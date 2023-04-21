package ticketSystemEASV.gui.model;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public interface IModel {
    CompletableFuture<String> add(Object objectToAdd);
    String update(Object objectToUpdate, CountDownLatch latch);
    String delete(Object objectToDelete);
}
