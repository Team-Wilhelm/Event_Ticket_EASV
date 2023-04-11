package ticketSystemEASV.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import ticketSystemEASV.dal.Interfaces.IDBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DBConnection implements IDBConnection {
    private final SQLServerDataSource ds = new SQLServerDataSource();
    private static DBConnection instance;
    private Deque<Connection> connectionPool = new ArrayDeque<>();
    private List<Connection> usedConnections = new ArrayList<>();

    private DBConnection() {
        ds.setServerName("10.176.111.34");
        ds.setDatabaseName("CSe22B_Wilhelm_EventTicket");
        ds.setPortNumber(1433);
        ds.setUser("CSe2022B_e_13");
        ds.setPassword("CSe2022BE13#");
        ds.setTrustServerCertificate(true);
    }

    public static DBConnection getInstance() {
        if (instance == null)
            instance = new DBConnection();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection connection;

        if (connectionPool.isEmpty()) {
            connection = ds.getConnection();
        } else {
            connection = connectionPool.poll();
            if (!connection.isValid(50)) {
                connection = ds.getConnection();
            }
        }
        return connection;
    }

    public void releaseConnection(Connection connection){
        if (usedConnections.contains(connection)){
            usedConnections.remove(connection);
            connectionPool.add(connection);
        }
    }
}
