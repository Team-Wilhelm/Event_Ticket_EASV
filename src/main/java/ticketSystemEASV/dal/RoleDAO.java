package ticketSystemEASV.dal;

import ticketSystemEASV.be.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class RoleDAO {
    private DBConnection dbConnection = new DBConnection();

    public Collection<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM Role";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                UUID id = UUID.fromString(resultSet.getString("id"));
                String name = resultSet.getString("RoleName");
                roles.add(new Role(id, name));
            }
            return roles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAdmin(UUID userId){
        String sql = "SELECT * FROM UserRole WHERE UserId = ? INNER JOIN Role ON Role.id = UserRole.RoleId WHERE Role.RoleName = 'Admin'";
        try (PreparedStatement statement = dbConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, userId.toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
