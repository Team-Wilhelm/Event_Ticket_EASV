package tests;

//import org.junit.Test;
import ticketSystemEASV.dal.RoleDAO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class RoleTests {
    private RoleDAO roleDAO = new RoleDAO();
    @Test
    public void testGetAllRoles() {
        var roles = new ArrayList<String>();
        roles.add("EventCoordinator");
        roles.add("Admin");

        var rolesFromDB = roleDAO.getAllRoles();
        for (var role : rolesFromDB.keySet()) {
            assert roles.contains(role);
        }
    }
}
