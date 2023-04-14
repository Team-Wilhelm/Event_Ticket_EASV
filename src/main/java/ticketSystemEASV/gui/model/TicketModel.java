package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.bll.TicketManager;

import java.util.List;
import java.util.concurrent.*;

public class TicketModel extends Model {
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
        } finally {
            shutdownExecutorService(executorService);
        }
        getTicketsFromManager(new CountDownLatch(0));
    }


    @Override
    public String add(Object objectToAdd, CountDownLatch latch) {
        Ticket ticket = (Ticket) objectToAdd;
        String message = bll.addTicket(ticket);
        getTicketsFromManager(latch);
        return message;
    }

    @Override
    public String update(Object objectToUpdate, CountDownLatch latch) {
        //TODO Perhaps to do
        Ticket ticket = (Ticket) objectToUpdate;
        String message = bll.update(ticket);
        getTicketsFromManager(latch);
        return message;
    }

    @Override
    public String delete(Object objectToDelete) {
        Ticket ticket = (Ticket) objectToDelete;
        String message = bll.deleteTicket(ticket);
        getTicketsFromManager(new CountDownLatch(0));
        return message;
    }

    public List<Ticket> getAllTicketsForEvent(Event event) {
        return allTickets.stream().filter(ticket -> ticket.getEvent().getId() == event.getId()).toList();
    }

    public void openTicket(Ticket ticket) {
        bll.openTicket(ticket);
    }

    public void getTicketsFromManager(CountDownLatch latch) {
        Callable<List<Ticket>> setAllTicketsRunnable = bll::getAllTickets;

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<List<Ticket>> future = executorService.submit(() -> {
            List<Ticket> result = setAllTicketsRunnable.call();
            latch.countDown();
            return result;
        });

        try {
            latch.await();
            allTickets = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorService(executorService);
        }
    }
}
