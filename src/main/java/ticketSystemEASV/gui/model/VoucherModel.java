package ticketSystemEASV.gui.model;

import ticketSystemEASV.be.Voucher;
import ticketSystemEASV.bll.managers.VoucherManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class VoucherModel extends Model {
    private VoucherManager bll;

    public VoucherModel() {
        bll = new VoucherManager();
    }

    @Override
    public String add(Object objectToAdd, CountDownLatch latch) {
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

    public void addMultiple(List<Voucher> vouchers) {
        bll.addMultipleVouchers(vouchers);
    }
}
