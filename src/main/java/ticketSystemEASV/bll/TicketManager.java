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

public class TicketManager {
    private final TicketDAO ticketDAO = new TicketDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final TicketGenerator ticketGenerator = new TicketGenerator();

    public void addTicket(Ticket ticketToAdd) {
        try {
            ticketToAdd.setTicketQR(ticketGenerator.generateQRCode(ticketToAdd, 150));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        ticketDAO.addTicket(ticketToAdd);
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

    public void deleteTicket(Ticket ticketToDelete) {
        ticketDAO.deleteTicket(ticketToDelete);
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

    public static void main(String[] args) throws IOException {
        TicketGenerator ticketGenerator = new TicketGenerator();
        TicketManager ticketManager = new TicketManager();
        UserDAO userDAO = new UserDAO();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/images/userProfilePictures/userIcon.png"));
        ImageIO.write(bufferedImage, "png", baos);
        byte[] profile = baos.toByteArray();
        baos.close();
        /*userDAO.signUp(new User("test", "test", "test",
                new Role(UUID.fromString("DEF30627-B332-4C4A-AB14-801ED28E80BD"), "EventCoordinator"),
                profile));*/
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
}
