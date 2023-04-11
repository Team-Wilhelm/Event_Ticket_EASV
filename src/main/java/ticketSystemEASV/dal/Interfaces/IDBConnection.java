package ticketSystemEASV.dal.Interfaces;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDBConnection {
    Connection getConnection() throws SQLException;
}
