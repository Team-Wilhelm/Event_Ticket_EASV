package ticketSystemEASV.bll;

import com.google.zxing.WriterException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import ticketSystemEASV.be.*;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.dal.TicketDAO;
import ticketSystemEASV.dal.UserDAO;
import ticketSystemEASV.dal.VoucherDAO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class TicketManager {
    private final TicketDAO ticketDAO;
    private final VoucherDAO voucherDAO;
    private final TicketGenerator ticketGenerator;

    public TicketManager() {
        Callable<TicketDAO> ticketDAOCallable = TicketDAO::new;
        Callable<VoucherDAO> voucherDAOCallable = VoucherDAO::new;
        Callable<TicketGenerator> ticketGeneratorCallable = TicketGenerator::new;
        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
            Future<TicketDAO> ticketDAOFuture = executorService.submit(ticketDAOCallable);
            Future<VoucherDAO> voucherDAOFuture = executorService.submit(voucherDAOCallable);
            Future<TicketGenerator> ticketGeneratorFuture = executorService.submit(ticketGeneratorCallable);
            ticketDAO = ticketDAOFuture.get();
            voucherDAO = voucherDAOFuture.get();
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

    public void addVoucher(Voucher voucherToAdd) {
        voucherDAO.addVoucher(voucherToAdd);
    }

    public void deleteVoucher(Voucher voucherToDelete) {
        voucherDAO.deleteVoucher(voucherToDelete);
    }

    public void addMultipleVouchers(List<Voucher> vouchers) {
        voucherDAO.addMultipleVouchers(vouchers);
    }

    public void openTicket(Ticket ticket) {
        if (Desktop.isDesktopSupported()) {
            try {
                File ticketPDF = new File("src/main/resources/tickets/" + ticket.getId() + ".pdf");
                if (!ticketPDF.exists())
                    ticketGenerator.generateTicket(ticket);
                Desktop.getDesktop().open(ticketPDF);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }

    public String update(Ticket ticket) {
        return ticketDAO.update(ticket);
    }
}
