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
        telcoService.addCall(customer1.getCustomerId(), LocalDateTime.of(2021, Month.OCTOBER, 25, 18, 30),Long.valueOf("30"), PhoneCallType.LOCAL,"123456789");
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

    @Test
    public void testGetCustomerByName () throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException{
        //creamos customers
        Customer pablo1 = telcoService.addCustomer("Pablo", "21436587T", "los cantones, 123", "666555444");
        Customer carlos = telcoService.addCustomer("Carlos", "43658721Y", "Calle Real, 34", "666777888");
        Customer isma = telcoService.addCustomer("Ismael", "65872143N", "Ronda de nelle, 76", "666111222");
        Customer pablo2 = telcoService.addCustomer("Pablo", "11436587T", "los cantones, 124", "666555442");
        //Añadimos al los clientes
        Collection<Customer> mycustomer = new ArrayList<>();
        Customer c;
        c = telcoService.addCustomer(pablo1.getName(), pablo1.getDni(), pablo1.getAddress(), pablo1.getPhoneNumber());
        c = telcoService.addCustomer(pablo2.getName(), pablo2.getDni(), pablo2.getAddress(), pablo2.getPhoneNumber());
        //Buscamos el cliente por el nombre sin tener en cuenta las mayusculas
        Collection<Customer> customers = telcoService.getCustomersbyName("PABLO", null, null);
        //Comparamos
        assertEquals(mycustomer, customers);

        telcoService.removeCustomer(pablo1.getCustomerId());
        telcoService.removeCustomer(carlos.getCustomerId());
        telcoService.removeCustomer(isma.getCustomerId());
        telcoService.removeCustomer(pablo2.getCustomerId());

        for (Customer cust : mycustomer) {
            telcoService.removeCustomer(cust.getCustomerId());
        }
    }

    @Test
    public void testAddCall () throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException {
        Customer pablo = telcoService.addCustomer("Pablo", "21436587T", "los cantones, 123", "666555444");
        PhoneCall phoneCall = telcoService.addCall(pablo.getCustomerId(), LocalDateTime.now().minusHours(1), (long) 85, PhoneCallType.LOCAL, "666777888");
        long c = phoneCall.getPhoneCallId();

        assertEquals(phoneCall.getPhoneCallId(), c);

        //telcoService.removeCall(phoneCall.getPhoneCallId());
        telcoService.removeCustomer(pablo.getCustomerId());
    }


    // Carlos
    @Test
    public void testGetCallsbyId () throws InputValidationException, InstanceNotFoundException, CustomerHasCallsException {
        // Creamos un cliente
        Customer perico = telcoService.addCustomer("Perico de los palotes", "12345678P", "Ronda de Outeiro, 3000", "981167000");
        // Añadimos varias chamadas a nome do novo cliente
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

        // Agora sacamos as chamadas rexistradas dese cliente
        Collection<PhoneCall> retrievedcalls = telcoService.getCallsbyId(perico.getCustomerId(), null, null, null, null, null);

        // E comparamos
        assertEquals(addedcalls, retrievedcalls);

        //Ahora borramos as chamadas
        // TODO: ver cómo facer para borrar as chamadas sen meter o método na interface
        for (PhoneCall call : addedcalls) {
            this.removeCall(call.getPhoneCallId());
        }

        //E borramos o cliente
        telcoService.removeCustomer(perico.getCustomerId());
    }

    //Carlos
    @Test
    public void testGetCallsByMonth() throws InputValidationException, UnexpectedCallStatusException, MonthNotClosedException, InstanceNotFoundException, CustomerHasCallsException {
        // Primeiro creamos un novo cliente
        Customer perico = telcoService.addCustomer("Perico de los palotes", "12345678P", "Ronda de Outeiro, 3000", "981167000");

        // Añadimos varias chamadas a nome do novo cliente
        Collection<PhoneCall> augustcalls = new ArrayList<>(4);
        PhoneCall c;
        c = telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 8, 13, 45), (long) 180, PhoneCallType.LOCAL, "600300100");
        augustcalls.add(c);
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.FEBRUARY, 24, 6,53), (long) 600, PhoneCallType.INTERNATIONAL, "0018005437689");
        c = telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 20, 12, 32), (long) 240, PhoneCallType.NATIONAL, "900200300");
        augustcalls.add(c);
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.APRIL, 14, 22, 17), (long) 400, PhoneCallType.LOCAL, "981856382");

        // Agora sacamos as chamadas do cliente en agosto do 2021.
        Collection<PhoneCall> retrievedcalls = telcoService.getCallsbyMonth(perico.getCustomerId(), Month.AUGUST.getValue(), 2021);

        // E comprobamos que sexan as dúas (e só esas dúas)
        assertEquals(augustcalls, retrievedcalls);

        // Agora probamos a cambiar o estado das chamadas a "BILLED"
        telcoService.changeCallsStatus(perico.getCustomerId(), Month.AUGUST.getValue(), 2021, PhoneCallStatus.BILLED);

        // Temos que volver a sacalas do servicio
        retrievedcalls = telcoService.getCallsbyMonth(perico.getCustomerId(), Month.AUGUST.getValue(), 2021);

        // E comprobar que están "BILLED"
        for (PhoneCall call: retrievedcalls) {
            assertEquals(call.getPhoneCallStatus(), PhoneCallStatus.BILLED);
        }

        //Borramos todas as chamadas
        this.clearCalls();

        //Borramos o cliente
        telcoService.removeCustomer(perico.getCustomerId());
    }

    // Carlos
    @Test
    public void testCallAlreadyBilled(){
        //Primeiro creamos un cliente
        Customer perico = telcoService.addCustomer("Perico de los palotes", "12345678P", "Ronda de Outeiro, 3000", "981167000");

        //Engadimos varias chamadas ó seu nome
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 8, 13, 45), (long) 180, PhoneCallType.LOCAL, "600300100");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 24, 6,53), (long) 600, PhoneCallType.INTERNATIONAL, "0018005437689");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 20, 12, 32), (long) 240, PhoneCallType.NATIONAL, "900200300");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.of(2021, Month.AUGUST, 14, 22, 17), (long) 400, PhoneCallType.LOCAL, "981856382");

        //Pasamos as chamadas a estado BILLED
        telcoService.changeCallsStatus(perico.getCustomerId(), Month.AUGUST.getValue(), 2021, PhoneCallStatus.BILLED);

        //Volvemos a intentar pasar as chamadas a estado BILLED de novo
        //Debe saltar a excepción UnexpectedPhoneCallStatusException
        assertException(UnexpectedPhoneCallStatusException.class, () => {
            telcoService.changeCallsStatus(perico.getCustomerId(), Month.AUGUST.getValue(), 2021, PhoneCallStatus.BILLED);
        });

        //Borramos as chamadas
        telcoService.clearCalls();

        //Borramos o cliente
        telcoService.removeCustomer(perico.getCustomerId());
    }

    // Carlos
    @Test
    public void testMonthNotClosed(){
        //Primeiro creamos un cliente
        Customer perico = telcoService.addCustomer("Perico de los palotes", "12345678P", "Ronda de Outeiro, 3000", "981167000");

        //Engadimos varias chamadas ó seu nome, que pertencen ó mes actual (por tanto que aínda non rematou)
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(1), (long) 180, PhoneCallType.LOCAL, "600300100");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(2), (long) 600, PhoneCallType.INTERNATIONAL, "0018005437689");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(3), (long) 240, PhoneCallType.NATIONAL, "900200300");
        telcoService.addCall(perico.getCustomerId(), LocalDateTime.now().minusHours(4), (long) 400, PhoneCallType.LOCAL, "981856382");

        //Agora ó intentar sacar as chamadas do mes actual, saltará a excepción MonthNotClosedException
        assertThrows(MonthNotClosedException.class, () => {
            telcoService.getCallsbyMonth(perico.getCustomerId(), LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
        });
    }

}
