package ticketSystemEASV.bll.managers;

import com.google.zxing.WriterException;
import ticketSystemEASV.be.*;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.dal.TicketDAO;
import ticketSystemEASV.dal.VoucherDAO;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class TicketManager {
    private final TicketDAO ticketDAO;
    private final TicketGenerator ticketGenerator;

    public TicketManager() {
        Callable<TicketDAO> ticketDAOCallable = TicketDAO::new;
        Callable<TicketGenerator> ticketGeneratorCallable = TicketGenerator::new;
        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
            Future<TicketDAO> ticketDAOFuture = executorService.submit(ticketDAOCallable);
            Future<TicketGenerator> ticketGeneratorFuture = executorService.submit(ticketGeneratorCallable);
            ticketDAO = ticketDAOFuture.get();
            ticketGenerator = ticketGeneratorFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create TicketManager");
        }
    }

    public String addTicket(Ticket ticketToAdd) {
        try {
            ticketToAdd.setTicketQR(ticketGenerator.generateQRCode(ticketToAdd, 150));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return ticketDAO.addTicket(ticketToAdd);
    }

    public void addMultipleTickets(List<Ticket> tickets, Customer customer) {
        ticketDAO.addMultipleTickets(tickets, customer);
    }

    public Ticket getTicket(UUID id) {
        return ticketDAO.getTicket(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketDAO.getAllTickets();
    }

    public List<Ticket> getAllTicketsForEvent(Event event) {
        return ticketDAO.getAllTicketsForEvent(event);
    }

    public List<Ticket> getAllTicketsForCustomer(Customer customer) {
        return ticketDAO.getAllTicketsForCustomer(customer);
    }

    public String deleteTicket(Ticket ticketToDelete) {
        return ticketDAO.deleteTicket(ticketToDelete);
    }

    public void openTicket(Ticket ticket) {
        if (Desktop.isDesktopSupported()) {
            try {
                ticketGenerator.generateTicket(ticket);
                File ticketPDF = new File("src/main/resources/tickets/" + ticket.getId() + ".pdf");
                Desktop.getDesktop().open(ticketPDF);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }

    public String update(Ticket ticket) {
        return ticketDAO.update(ticket);
    }

    public void updateTickets(List<Ticket> tickets) {
        try {
            for (Ticket ticket : tickets) {
                ticket.setTicketQR(ticketGenerator.generateQRCode(ticket, 150));
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        ticketDAO.updateTickets(tickets);
    }
    public void redeemTicket(Ticket ticket) { ticketDAO.redeemTicket(ticket); }
}
