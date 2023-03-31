package ticketSystemEASV.bll;

import ticketSystemEASV.be.Customer;
import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.be.Voucher;
import ticketSystemEASV.dal.CustomerDAO;
import ticketSystemEASV.dal.TicketDAO;
import ticketSystemEASV.dal.VoucherDAO;

import java.util.List;
import java.util.UUID;

public class TicketManager {
    private final TicketDAO ticketDAO = new TicketDAO();

    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public void addTicket(Ticket ticketToAdd) {
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
}
