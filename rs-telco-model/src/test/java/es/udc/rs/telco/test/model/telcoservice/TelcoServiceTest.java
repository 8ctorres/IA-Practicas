package es.udc.rs.telco.test.model.telcoservice;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.exceptions.UnexpectedCallStatusException;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.rs.telco.model.exceptions.MonthNotClosedException;
import es.udc.rs.telco.model.phonecall.PhoneCall;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;
import es.udc.rs.telco.model.phonecall.PhoneCallType;
import es.udc.rs.telco.model.telcoservice.MockTelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class TelcoServiceTest {

    private static TelcoService telcoService = null;

    private void clearCalls(){
        MockTelcoService mock = (MockTelcoService) telcoService;
        mock.clearCalls();
    }

    private void removeCall(Long callId){
        MockTelcoService mock = (MockTelcoService) telcoService;
        mock.removeCall(callId);
    }

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

    // Isma
    @Test
    public void testCustomerNotFound() {
        assertThrows(InstanceNotFoundException.class, () -> telcoService.findCustomerById(Long.valueOf("9000")));
    }

    // Isma
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

    // Isma
    @Test
    public void testRemoveCustomerWithCalls() throws InputValidationException, InstanceNotFoundException {
        //Se crea 1 cliente para la prueba
        Customer customer1 = telcoService.addCustomer("Jose", "11111111E","Calle se por favor","444555666");
        //Se le añade una llamada al cliente
        telcoService.addCall(customer1.getCustomerId(), LocalDateTime.of(2021, Month.OCTOBER, 25, 18, 30),Long.valueOf("30"), PhoneCallType.LOCAL,"123456789");
        //Se intenta eliminar el cliente 1
        assertThrows(CustomerHasCallsException.class, () -> telcoService.removeCustomer(customer1.getCustomerId()));
    }

    // Isma
    @Test
    public void testModifyCustomer() throws InputValidationException, CustomerHasCallsException, InstanceNotFoundException {
        //Se crea 1 cliente para la prueba
        Customer customer1 = telcoService.addCustomer("Paco", "11111111F","Rua Damiel Casteliao","987654321");
        //Se modifica el nombre, el DNI y la calle del cliente 1, que se había equivocado al registrarse
        telcoService.updateCustomer(customer1.getCustomerId(),"Francisco","11111112F","Rua Daniel Castelao");
        customer1 = telcoService.findCustomerById(customer1.getCustomerId());
        //Se comparan los nuevos atributos del cliente 1 con los esperados
        assertEquals("Francisco", customer1.getName());
        assertEquals("11111112F", customer1.getDni());
        assertEquals("Rua Daniel Castelao", customer1.getAddress());
        //Se borra lo creado en la prueba
        telcoService.removeCustomer(customer1.getCustomerId());
    }

    //Pablo
    @Test
    public void testFindCustomerByDNI () throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException{
        //creamos tres clientes
        Customer pablo = telcoService.addCustomer("Pablo", "21436587T", "los cantones, 123", "666555444");
        Customer carlos = telcoService.addCustomer("Carlos", "43658721Y", "Calle Real, 34", "666777888");
        Customer isma = telcoService.addCustomer("Ismael", "65872143N", "Ronda de nelle, 76", "666111222");
        //Buscamos a los clientes por su dni
        Customer c1 = telcoService.findCustomerByDNI("21436587T");
        Customer c2 = telcoService.findCustomerByDNI("43658721Y");
        Customer c3 = telcoService.findCustomerByDNI("65872143N");
        //Comparamos los resultados
        assertEquals(pablo.getDni(),c1.getDni());
        assertEquals(carlos.getDni(), c2.getDni());
        assertEquals(isma.getDni(), c3.getDni());
        //Eliminamos los customers creados
        telcoService.removeCustomer(pablo.getCustomerId());
        telcoService.removeCustomer(carlos.getCustomerId());
        telcoService.removeCustomer(isma.getCustomerId());
    }

    //Pablo
    @Test
    public void testGetCustomerByName () throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException{
        //creamos customers
        Customer pablo1 = telcoService.addCustomer("Pablo", "21436587T", "los cantones, 123", "666555444");
        Customer carlos = telcoService.addCustomer("Carlos", "43658721Y", "Calle Real, 34", "666777888");
        Customer isma = telcoService.addCustomer("Ismael", "65872143N", "Ronda de nelle, 76", "666111222");
        Customer pablo2 = telcoService.addCustomer("Pablo", "11436587T", "los cantones, 124", "666555442");
        //Añadimos al los clientes
        Collection<Customer> mycustomer = new ArrayList<>();
        mycustomer.add(pablo1);
        mycustomer.add(pablo2);
        //Buscamos el cliente por el nombre sin tener en cuenta las mayusculas
        Collection<Customer> customers = telcoService.findCustomersbyName("PABLO", null, null);
        //Comparamos
        assertEquals(mycustomer, customers);

        telcoService.removeCustomer(pablo1.getCustomerId());
        telcoService.removeCustomer(carlos.getCustomerId());
        telcoService.removeCustomer(isma.getCustomerId());
        telcoService.removeCustomer(pablo2.getCustomerId());
    }

    //Pablo
    @Test
    public void testAddCall () throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException {
        Customer pablo = telcoService.addCustomer("Pablo", "21436587T", "los cantones, 123", "666555444");
        PhoneCall phoneCall = telcoService.addCall(pablo.getCustomerId(), LocalDateTime.now().minusHours(1), (long) 85, PhoneCallType.LOCAL, "666777888");
        long c = phoneCall.getPhoneCallId();

        assertEquals(phoneCall.getPhoneCallId(), c);

        this.removeCall(phoneCall.getPhoneCallId());
        telcoService.removeCustomer(pablo.getCustomerId());
    }

    // Carlos
    @Test
    public void testGetCallsbyId () throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException {
        // Creamos un cliente
        Customer perico = telcoService.addCustomer("Perico de los palotes", "12345678P", "Ronda de Outeiro, 3000", "981167000");
        // Añadimos varias llamadas al nombre del nuevo cliente
        Collection<PhoneCall> addedcalls = new ArrayList<>(4);
        PhoneCall c;
        c = telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(1), (long) 180, PhoneCallType.LOCAL, "600300100");
        addedcalls.add(c);
        c = telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusDays(1), (long) 600, PhoneCallType.INTERNATIONAL, "0018005437689");
        addedcalls.add(c);
        c = telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusDays(3), (long) 240, PhoneCallType.NATIONAL, "900200300");
        addedcalls.add(c);
        c = telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(8), (long) 400, PhoneCallType.LOCAL, "981856382");
        addedcalls.add(c);

        // Ahora sacamos las llamadas registradas de ese cliente
        Collection<PhoneCall> retrievedcalls = telcoService.getCallsbyId(perico.getCustomerId(), null, null, null);

        // Comparamos
        assertEquals(addedcalls, retrievedcalls);

        //Ahora borramos las chamadas
        for (PhoneCall call : addedcalls) {
            this.removeCall(call.getPhoneCallId());
        }

        //Borramos el cliente
        telcoService.removeCustomer(perico.getCustomerId());
    }

    //Carlos
    @Test
    public void testGetCallsByMonth() throws InputValidationException, UnexpectedCallStatusException, MonthNotClosedException, InstanceNotFoundException, CustomerHasCallsException {
        // Primero creamos un nuevo cliente
        Customer perico = telcoService.addCustomer("Perico de los palotes", "12345678P", "Ronda de Outeiro, 3000", "981167000");

        // Añadimos varias llamadas al nombre del nuevo cliente
        Collection<PhoneCall> augustcalls = new ArrayList<>(4);
        PhoneCall c;
        c = telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 8, 13, 45), (long) 180, PhoneCallType.LOCAL, "600300100");
        augustcalls.add(c);
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.FEBRUARY, 24, 6,53), (long) 600, PhoneCallType.INTERNATIONAL, "0018005437689");
        c = telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 20, 12, 32), (long) 240, PhoneCallType.NATIONAL, "900200300");
        augustcalls.add(c);
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.APRIL, 14, 22, 17), (long) 400, PhoneCallType.LOCAL, "981856382");

        // Ahora sacamos las llamadas del cliente en agosto del 2021.
        Collection<PhoneCall> retrievedcalls = telcoService.getCallsbyMonth(perico.getCustomerId(), Month.AUGUST.getValue(), 2021);

        // Comprobamos que sean las dos (solo esas dos)
        assertEquals(augustcalls, retrievedcalls);

        //Borramos todas las llamadas
        this.clearCalls();

        //Borramos el cliente
        telcoService.removeCustomer(perico.getCustomerId());
    }

    // Carlos
    @Test
    public void testCallAlreadyBilled() throws InputValidationException, MonthNotClosedException, UnexpectedCallStatusException, InstanceNotFoundException, CustomerHasCallsException {
        //Primero creamos un cliente
        Customer perico = telcoService.addCustomer("Perico de los palotes", "12345678P", "Ronda de Outeiro, 3000", "981167000");

        //Añdimos varias llamadas a su nombre
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 8, 13, 45), (long) 180, PhoneCallType.LOCAL, "600300100");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 24, 6,53), (long) 600, PhoneCallType.INTERNATIONAL, "0018005437689");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 20, 12, 32), (long) 240, PhoneCallType.NATIONAL, "900200300");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 14, 22, 17), (long) 400, PhoneCallType.LOCAL, "981856382");

        //Pasamos las llamadas a estado BILLED
        telcoService.changeCallsStatus(perico.getCustomerId(), Month.AUGUST.getValue(), 2021, PhoneCallStatus.BILLED);

        //Volvemos a intentar pasar las llamadas a estado BILLED de nuevo
        //Debe saltar la excepción UnexpectedPhoneCallStatusException
        assertThrows(UnexpectedCallStatusException.class, () ->
                telcoService.changeCallsStatus(perico.getCustomerId(), Month.AUGUST.getValue(), 2021, PhoneCallStatus.BILLED));

        //Borramos las llamadas
        this.clearCalls();

        //Borramos el cliente
        telcoService.removeCustomer(perico.getCustomerId());
    }

    // Carlos
    @Test
    public void testMonthNotClosed() throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException {
        //Primero creamos un cliente
        Customer perico = telcoService.addCustomer("Perico de los palotes", "12345678P", "Ronda de Outeiro, 3000", "981167000");

        //Añadimos varias llamadas a su nombre, que pertenecen al mes actual (por tanto aún no acabó)
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(1), (long) 180, PhoneCallType.LOCAL, "600300100");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(2), (long) 600, PhoneCallType.INTERNATIONAL, "0018005437689");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(3), (long) 240, PhoneCallType.NATIONAL, "900200300");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(4), (long) 400, PhoneCallType.LOCAL, "981856382");

        //Ahora al intentar sacar las llamadas del mes actual, saltará la excepción MonthNotClosedException
        assertThrows(MonthNotClosedException.class, () -> telcoService.getCallsbyMonth(perico.getCustomerId(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getYear()));

        //Borramos las llamadas y el cliente
        this.clearCalls();
        telcoService.removeCustomer(perico.getCustomerId());
    }

}
