package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Voucher;
import ticketSystemEASV.bll.managers.VoucherManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class VoucherModel extends Model {
    private VoucherManager bll;

    public VoucherModel() {
        bll = new VoucherManager();
    }

    @Override
    public CompletableFuture<String> add(Object objectToAdd) {
        return null;
    }

    @Override
    public String update(Object objectToUpdate, CountDownLatch latch) {
        return null;
    }

    @Override
    public String delete(Object objectToDelete) {
        return null;
    }

    public String addMultiple(List<Voucher> vouchers) {
        String message =  bll.addMultipleVouchers(vouchers);
        getAllVouchersForEvent(vouchers.get(0).getEvent());
        return message;
    }

    public List<Voucher> getAllVouchersForEvent(Event event) {
        return bll.getAllVouchersForEvent(event);
    }
}
