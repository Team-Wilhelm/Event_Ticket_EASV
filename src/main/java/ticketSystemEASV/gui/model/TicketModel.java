package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.bll.TicketManager;
import ticketSystemEASV.dal.EventDAO;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class TicketModel {
    private final TicketManager bll;;
    private List<Ticket> allTickets;

    public TicketModel() {
        Callable<TicketManager> ticketManagerCallable = TicketManager::new;
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        // Try to execute the callable, if it fails, throw a runtime exception
        try {
            Future<TicketManager> future = executorService.submit(ticketManagerCallable);
            bll = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create TicketManager");
        }

        // Try to shut down the executor service, if it fails, throw a runtime exception and force shutdown
        try {
            executorService.shutdown();
            executorService.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (!executorService.isShutdown())
                executorService.shutdownNow();
        }
        getTicketsFromManager();
    }

    public void generateTicket(Event event, Customer customer) {
        bll.addTicket(new Ticket(event, customer));
        getTicketsFromManager();
    }

    public List<Ticket> getAllTicketsForEvent(Event event) {
        return allTickets;
    }

    public void openTicket(Ticket ticket) {
        bll.openTicket(ticket);
    }

    public void getTicketsFromManager() {
        Callable<List<Ticket>> setAllTicketsRunnable = bll::getAllTickets;
        try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
            Future<List<Ticket>> future = executorService.submit(setAllTicketsRunnable);
            allTickets = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
