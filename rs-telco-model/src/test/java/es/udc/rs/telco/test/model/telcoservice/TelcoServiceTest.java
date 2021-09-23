package es.udc.rs.telco.test.model.telcoservice;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.rs.telco.model.telcoservice.TelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TelcoServiceTest {

    private static TelcoService telcoService = null;

    @BeforeAll
    public static void init() {
        telcoService = TelcoServiceFactory.getService();

    }


    //Isma
    @Test
    public void testAddCustomerAndFindCustomerById() throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException {
        Customer customer1 = telcoService.addCustomer("Finn", "11111111A","13 Rue del Percebe","111222333");
        Customer customer2 = telcoService.addCustomer("Jake", "11111111B","Calle falsa 123","222222222");
        //El cliente 1 tiene el ID 1 y el 2, el ID 2
        assertEquals(customer1.getCustomerId(), 1);
        assertEquals(customer2.getCustomerId(), 2);
        //Buscamos los ID de los clientes 1 y 2 y comparamos que se encuentra a los clientes correctamente
        Customer c1 = telcoService.findCustomerById(Long.valueOf("1"));
        Customer c2 = telcoService.findCustomerById(Long.valueOf("2"));
        assertEquals(customer1, c1);
        assertEquals(customer2, c2);
        //Borramos lo creado en la prueba
        telcoService.removeCustomer(customer1.getCustomerId());
        telcoService.removeCustomer(customer2.getCustomerId());
    }

    @Test
    public void testCustomerNotFound() {
        assertThrows(InstanceNotFoundException.class, () -> telcoService.findCustomerById(Long.valueOf("9000")));
    }

    @Test
    public void testRemoveCustomer()  {
        assertTrue(telcoService != null);
    }

    @Test
    public void testRemoveCustomerWithCalls()  {
        assertTrue(telcoService != null);
    }

    @Test
    public void testModifyCustomer()  {
        assertTrue(telcoService != null);
    }



    @Test
    public void testFindCustomerByDNI (){ assertTrue(telcoService != null); }

    @Test
    public void testGetCustomerByName (){ assertTrue(telcoService != null); }

    @Test
    public void testAddCall (){ assertTrue(telcoService != null); }
}
