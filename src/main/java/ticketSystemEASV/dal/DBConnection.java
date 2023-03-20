package ticketSystemEASV.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dal.Interfaces.IDBConnection;

import java.sql.Connection;

public class DBConnection implements IDBConnection {
    private final SQLServerDataSource ds = new SQLServerDataSource();

    public DBConnection() {
        ds.setServerName("10.176.111.34");
        ds.setDatabaseName("CSe22B_Wilhelm_EventTicket");
        ds.setPortNumber(1433);
        ds.setUser("CSe2022B_e_13");
        ds.setPassword("CSe2022BE13#");
        ds.setTrustServerCertificate(true);
    }

    public Connection getConnection() throws SQLServerException {
        return ds.getConnection();
    }

}
