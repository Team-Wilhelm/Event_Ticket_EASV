package ticketSystemEASV.gui.model;

import java.util.concurrent.CountDownLatch;

public interface IModel {
    String add(Object objectToAdd, CountDownLatch latch);
    String update(Object objectToUpdate, CountDownLatch latch);
    String delete(Object objectToDelete);
}
