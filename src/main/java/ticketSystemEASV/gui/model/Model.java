package ticketSystemEASV.gui.model;

import javafx.concurrent.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Model implements IModel {
    @Override
    public abstract String add(Object objectToAdd, CountDownLatch latch);
    @Override
    public abstract String update(Object objectToUpdate, CountDownLatch latch);
    @Override
    public abstract String delete(Object objectToDelete);

    protected void shutdownExecutorService(ExecutorService executorService){
        try {
            executorService.shutdown();
        } finally {
            if (!executorService.isShutdown()) {
                executorService.shutdownNow();
            }
        }
    }

    protected void executeTask(Task task){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        shutdownExecutorService(executorService);
        shutdownExecutorService(executorService);
    }
}
