package ticketSystemEASV.dal;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Ticket;
import ticketSystemEASV.be.Voucher;
import ticketSystemEASV.dal.Interfaces.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoucherDAO extends DAO<Voucher> {
    private final DBConnection dbConnection = DBConnection.getInstance();

    public List<Voucher> getAllVouchersForEvent(Event event) {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM Voucher WHERE EventID=?;";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, event.getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                vouchers.add(new Voucher(event,
                        rs.getString("VoucherType"),
                        UUID.fromString(rs.getString("ID")),
                        rs.getBytes("VoucherQR"),
                        rs.getBoolean("Redeemed")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        return vouchers;
    }

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

    public String addMultipleVouchers(List<Voucher> vouchers){
        String sql = "INSERT INTO Voucher (VoucherType, VoucherQR, eventID) VALUES (?,?,?);";
        String message = "";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Voucher voucher : vouchers) {
                preparedStatement.setString(1, voucher.getVoucherType());
                preparedStatement.setBytes(2, voucher.getVoucherQR());
                preparedStatement.setInt(3, voucher.getEvent().getId());
                preparedStatement.addBatch();
                voucher.getEvent().getVouchers().add(voucher);
            }
            preparedStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            message = "Could not add vouchers";
        } finally {
            releaseConnection(connection);
        }
        return message;
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
