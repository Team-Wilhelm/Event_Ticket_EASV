package dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

public class DBConnection {
    private final SQLServerDataSource ds = new SQLServerDataSource();

    public DBConnection() {
        ds.setServerName("10.176.111.34");
        ds.setDatabaseName("CSe22B_Wilhelm_EventTicket");
        ds.setPortNumber(1433);
        ds.setUser(" CSe2022B_e_16");
        ds.setPassword("CSe2022BE16#");
        ds.setTrustServerCertificate(true);
    }

    public Connection getConnection() throws SQLServerException {
        return ds.getConnection();
    }

}
