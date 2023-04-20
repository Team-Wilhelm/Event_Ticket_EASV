package tests;

//import org.junit.Test;
import ticketSystemEASV.be.Customer;
import ticketSystemEASV.dal.CustomerDAO;
//import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CustomerTests {
    private final CustomerDAO customerDAO = new CustomerDAO();
    @Test
    public void createCustomer() {
        var customer = new Customer("Test", "test@test.dk");
        customerDAO.addCustomer(customer);
        assertTrue(customer.getId() > 0);
        customerDAO.deleteCustomer(customer);
    }
    @Test
    public void deleteCustomer() {
        var customer = new Customer("Test", "test@test.dk");
        customerDAO.addCustomer(customer);
        customerDAO.deleteCustomer(customer);
        assertTrue(customer.getId() == 0);
    }
    @Test
    public void getCustomer() {
        var customer = new Customer("Test", "test@test.dk");
        customerDAO.addCustomer(customer);
        var customer2 = customerDAO.getCustomer(customer.getId());
        assertTrue(customer2.getId() == customer.getId());
        customerDAO.deleteCustomer(customer);
    }
}
