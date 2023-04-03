package ticketSystemEASV.bll;

import com.google.zxing.WriterException;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.be.Voucher;
import ticketSystemEASV.be.views.TicketGenerator;
import ticketSystemEASV.dal.CustomerDAO;
import ticketSystemEASV.dal.TicketDAO;
import ticketSystemEASV.dal.VoucherDAO;

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

    public static void main(String[] args) {
        TicketGenerator ticketGenerator = new TicketGenerator();
        TicketManager ticketManager = new TicketManager();
        ticketGenerator.generateTicket(ticketManager.getTicket(UUID.fromString("A34AA799-B8C6-4AC8-9A2F-5983351BBC54")));
        ticketGenerator.generateTicket(ticketManager.getTicket(UUID.fromString("B18D9552-2C27-4860-9C9B-5EDCC450F31C")));
    }
}
