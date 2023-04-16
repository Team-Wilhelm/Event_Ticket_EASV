package ticketSystemEASV.dal;

import ticketSystemEASV.be.Voucher;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class VoucherDAO {
    private final DBConnection dbConnection = DBConnection.getInstance();

    public void addVoucher(Voucher voucher){
        String sql = "INSERT INTO Voucher (VoucherType, VoucherQR, EventID) VALUES (?,?, ?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, voucher.getVoucherType());
            statement.setString(2, voucher.getVoucherQR());
            statement.setInt(3, voucher.getEvent().getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMultipleVouchers(List<Voucher> vouchers){
        UUID id = vouchers.get(0).getId();
        String voucherType = vouchers.get(0).getVoucherType();
        String voucherQR = vouchers.get(0).getVoucherQR();
        boolean redeemed = vouchers.get(0).isRedeemed();

        /*String sql = "INSERT INTO Voucher (VoucherID, Restrictions, VoucherQR, Redeemed) VALUES (?,?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, restrictions);
            statement.setString(3, voucherType);
            statement.setString(4, voucherQR);
            statement.setBoolean(5, redeemed);
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    public void deleteVoucher(Voucher voucher){
        String sql = "DELETE FROM Voucher WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, voucher.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
