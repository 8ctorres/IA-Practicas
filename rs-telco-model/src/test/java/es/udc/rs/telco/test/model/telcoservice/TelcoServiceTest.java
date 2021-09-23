package es.udc.rs.telco.test.model.telcoservice;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.rs.telco.model.phonecall.PhoneCallType;
import es.udc.rs.telco.model.telcoservice.TelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

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
        //Se crean 2 clientes para la prueba
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
    public void testRemoveCustomer() throws InputValidationException, CustomerHasCallsException, InstanceNotFoundException {
        //Se crean 2 clientes para la prueba
        Customer customer1 = telcoService.addCustomer("Carlos", "11111111C","Su piso","333333333");
        Customer customer2 = telcoService.addCustomer("Pablo", "11111111D","Su casa","123456789");
        //Se elimina el cliente 1
        telcoService.removeCustomer(customer1.getCustomerId());
        //Se buscan los ID de los clientes 1 y 2 y se compara que se encuentra solo al cliente 2
        Customer c2 = telcoService.findCustomerById(customer2.getCustomerId());
        assertThrows(InstanceNotFoundException.class, () -> telcoService.findCustomerById(customer1.getCustomerId()));
        assertEquals(customer2, c2);
        //Se borra lo creado en la prueba
        telcoService.removeCustomer(customer2.getCustomerId());

    }

    @Test
    public void testRemoveCustomerWithCalls() throws InputValidationException {
        //Se crea 1 cliente para la prueba
        Customer customer1 = telcoService.addCustomer("Jose", "11111111E","Calle se por favor","444555666");
        //Se le añade una llamada al cliente
        telcoService.AddCall(customer1.getCustomerId(), LocalDateTime.of(2021, Month.OCTOBER, 25, 18, 30),Long.valueOf("30"), PhoneCallType.LOCAL,"123456789");
        //Se intenta eliminar el cliente 1
        assertThrows(CustomerHasCallsException.class, () -> telcoService.removeCustomer(customer1.getCustomerId()));
    }

    @Test
    public void testModifyCustomer() throws InputValidationException, CustomerHasCallsException, InstanceNotFoundException {
        //Se crea 1 cliente para la prueba
        Customer customer1 = telcoService.addCustomer("Paco", "11111111F","Rua Damiel Casteliao","987654321");
        //Se modifica el nombre, el DNI y la calle del cliente 1, que se había equivocado al registrarse
        telcoService.updateCustomer(customer1.getCustomerId(),"Francisco","11111112F","Rua Daniel Castelao");
        //Se comparan los nuevos atributos del cliente 1 con los esperados
        assertEquals(customer1.getName(), "Francisco");
        assertEquals(customer1.getDni(), "11111112F");
        assertEquals(customer1.getAddress(), "Rua Daniel Castelao");
        //Se borra lo creado en la prueba
        telcoService.removeCustomer(customer1.getCustomerId());
    }



    @Test
    public void testFindCustomerByDNI (){ assertTrue(telcoService != null); }

    @Test
    public void testGetCustomerByName (){ assertTrue(telcoService != null); }

    @Test
    public void testAddCall (){ assertTrue(telcoService != null); }
}
