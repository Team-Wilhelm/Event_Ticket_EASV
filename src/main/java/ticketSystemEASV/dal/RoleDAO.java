package ticketSystemEASV.dal;

import ticketSystemEASV.be.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RoleDAO {
    private DBConnection dbConnection = new DBConnection();

    public Map<String , Role> getAllRoles() {
        Map<String , Role> roles = new HashMap<>();
        String sql = "SELECT * FROM Role";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
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
        }
    }
}
