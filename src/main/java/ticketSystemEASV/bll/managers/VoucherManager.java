package ticketSystemEASV.bll.managers;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Voucher;
import ticketSystemEASV.dal.VoucherDAO;

import java.util.List;
import java.util.concurrent.*;

public class VoucherManager {
    private final VoucherDAO voucherDAO;

    public VoucherManager() {
        Callable<VoucherDAO> voucherDAOCallable = VoucherDAO::new;
        try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
            Future<VoucherDAO> voucherDAOFuture = executorService.submit(voucherDAOCallable);
            voucherDAO = voucherDAOFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create VoucherManager");
        }
    }

    public void addVoucher(Voucher voucherToAdd) {
        voucherDAO.addVoucher(voucherToAdd);
    }

    public void deleteVoucher(Voucher voucherToDelete) {
        voucherDAO.deleteVoucher(voucherToDelete);
    }

    public String addMultipleVouchers(List<Voucher> vouchers) {
        return voucherDAO.addMultipleVouchers(vouchers);
    }

    public List<Voucher> getAllVouchersForEvent(Event event) {
        return voucherDAO.getAllVouchersForEvent(event);
    }
}
