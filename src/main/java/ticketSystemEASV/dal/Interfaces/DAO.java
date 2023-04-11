package ticketSystemEASV.dal.Interfaces;

import ticketSystemEASV.dal.DBConnection;

import java.sql.Connection;
import java.util.Collection;

public abstract class DAO<T> {
    protected final DBConnection dbConnection = DBConnection.getInstance();

    /*public abstract Collection<T> getAll();
    public abstract Class<T> get();
    public abstract void add(T object);
    public abstract void update(T object);
    public abstract void delete(T object);*/

    protected void releaseConnection(Connection connection){
        if (connection != null)
            dbConnection.releaseConnection(connection);
    }
}
