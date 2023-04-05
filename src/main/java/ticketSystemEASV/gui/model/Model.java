package ticketSystemEASV.gui.model;

import java.util.concurrent.CountDownLatch;

public interface Model {
    String add(Object objectToAdd, CountDownLatch latch);
    String update(Object objectToUpdate, CountDownLatch latch);
    void delete(Object objectToDelete);
}
