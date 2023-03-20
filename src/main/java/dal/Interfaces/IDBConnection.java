package dal.Interfaces;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

public interface IDBConnection {
    Connection getConnection() throws SQLServerException;
}
