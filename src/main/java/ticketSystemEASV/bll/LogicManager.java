package ticketSystemEASV.bll;

import ticketSystemEASV.be.*;
import ticketSystemEASV.dal.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class LogicManager {
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final EventCoordinatorDAO eventCoordinatorDAO = new EventCoordinatorDAO();

    //region EventCoordinator CRUD
    public void addCoordinator(EventCoordinator coordinatorToSave) {
        eventCoordinatorDAO.addEventCoordinator(coordinatorToSave);
    }

    public void updateCoordinator(EventCoordinator coordinatorToUpdate) {
        eventCoordinatorDAO.updateEventCoordinator(coordinatorToUpdate);
    }

    public void deleteCoordinator(EventCoordinator coordinatorToDelete) {
        eventCoordinatorDAO.deleteEventCoordinator(coordinatorToDelete);
    }

    public Collection<EventCoordinator> getAllEventCoordinators(){
        return eventCoordinatorDAO.getAllEventCoordinators();
    }
    //endregion



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
