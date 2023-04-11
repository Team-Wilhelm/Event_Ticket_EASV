package ticketSystemEASV.dal;

import ticketSystemEASV.be.Role;
import ticketSystemEASV.dal.Interfaces.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RoleDAO extends DAO<Role> {
    private DBConnection dbConnection = DBConnection.getInstance();

    public Map<String , Role> getAllRoles() {
        Map<String , Role> roles = new HashMap<>();
        String sql = "SELECT * FROM Role";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                UUID id = UUID.fromString(resultSet.getString("id"));
                String name = resultSet.getString("RoleName");
                roles.put(name, new Role(id, name));
            }
            return roles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            releaseConnection(connection);
        }
    }
}
