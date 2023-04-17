package ticketSystemEASV.dal;

import ticketSystemEASV.be.Voucher;
import ticketSystemEASV.dal.Interfaces.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class VoucherDAO extends DAO<Voucher> {
    private final DBConnection dbConnection = DBConnection.getInstance();

    public void addVoucher(Voucher voucher){
        String sql = "INSERT INTO Voucher (VoucherType, VoucherQR, EventID) VALUES (?,?, ?);";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, voucher.getVoucherType());
            statement.setBytes(2, voucher.getVoucherQR());
            statement.setInt(3, voucher.getEvent().getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMultipleVouchers(List<Voucher> vouchers){
        String sql = "INSERT INTO Voucher (VoucherType, VoucherQR, eventID) VALUES (?,?,?);";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Voucher voucher : vouchers) {
                preparedStatement.setString(1, voucher.getVoucherType());
                preparedStatement.setBytes(2, voucher.getVoucherQR());
                preparedStatement.setInt(3, voucher.getEvent().getId());
                preparedStatement.addBatch();
            }
            System.out.println("Executing batch");
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
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
