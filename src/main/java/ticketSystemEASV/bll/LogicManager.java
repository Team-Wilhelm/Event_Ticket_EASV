package ticketSystemEASV.bll;

import ticketSystemEASV.be.*;
import ticketSystemEASV.dal.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class LogicManager {
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

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
