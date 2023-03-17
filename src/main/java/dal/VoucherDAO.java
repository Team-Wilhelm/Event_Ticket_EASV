package dal;

import be.Voucher;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class VoucherDAO {
    private final DBConnection dbConnection = new DBConnection();

    public void addVoucher(Voucher voucher){
        String sql = "INSERT INTO Voucher (VoucherID, Restrictions, VoucherType, Redeemed) VALUES (?,?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, voucher.getId());
            statement.setString(2, voucher.getRestrictions());
            statement.setString(3, voucher.getVoucherType());
            statement.setBoolean(4, voucher.isRedeemed());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMultipleVouchers(List<Voucher> vouchers){
        int id = vouchers.get(0).getId();
        String restrictions = vouchers.get(0).getRestrictions();
        String voucherType = vouchers.get(0).getVoucherType();
        boolean redeemed = vouchers.get(0).isRedeemed();

        String sql = "INSERT INTO Voucher (voucherID, restrictions, redeemed) VALUES (?,?,?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, restrictions);
            statement.setString(3, voucherType);
            statement.setBoolean(4, redeemed);
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVoucher(Voucher voucher){
        String sql = "DELETE FROM Voucher WHERE id=?;";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, voucher.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
