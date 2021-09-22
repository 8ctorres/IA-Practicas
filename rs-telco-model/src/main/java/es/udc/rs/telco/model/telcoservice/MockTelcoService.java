package es.udc.rs.telco.model.telcoservice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.rs.telco.model.exceptions.CallNotPendingException;
import es.udc.rs.telco.model.exceptions.MonthNotClosedException;
import es.udc.rs.telco.model.phonecall.PhoneCall;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;

import es.udc.rs.telco.model.phonecall.PhoneCallType;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class MockTelcoService implements TelcoService {

	private static Map<Long, Customer> clientsMap = new LinkedHashMap<Long, Customer>();
	private static Map<Long, PhoneCall> phoneCallsMap = new LinkedHashMap<Long,PhoneCall>();
	//private static Map<Long, List<PhoneCall>> phoneCallsByUserMap = new LinkedHashMap<Long,List<PhoneCall>>();

	private static long lastClientId = 0;
	private static long lastPhoneCallId = 0;
	

	private static synchronized long getNextClientId() {
		return ++lastClientId;
	}
	
	private static synchronized long getNextPhoneCallId() {
		return ++lastPhoneCallId;
	}

	//Isma
	public Customer addCustomer(String name, String DNI, String address, String phone) throws InputValidationException {
		//Creamos un cliente con los datos proporcionados
		Customer c = new Customer(name, DNI, address, phone);
		//Le asignamos el siguiente ID disponible
		Long id = getNextClientId();
		c.setCustomerId(id);
		//Le asignamos la fecha actual como fecha de creación
		c.setCreationDate(LocalDateTime.now());
		//Lo metemos en clientsMap con su ID
		clientsMap.put(c.getCustomerId(), c);
		return c;
	}

	//Isma
	public void updateCustomer(Long id, String name, String DNI, String address) throws InstanceNotFoundException, InputValidationException {
		//Si el cliente no existe devuelve la excepción InstanceNotFoundException
		Customer c = clientsMap.get(id);
		if (c == null) {
			throw new InstanceNotFoundException(id, "Cliente no encontrado");
		}
		//Actualiza los parámetros modificables
		c.setName(name);
		c.setDni(DNI);
		c.setAddress(address);
	}

	//Isma
	public void removeCustomer(Long id) throws InstanceNotFoundException, CustomerHasCallsException {
		if (!(getCallsbyId(id, null, null, null, null, null).isEmpty())){
			throw new CustomerHasCallsException("El cliente tiene llamadas registradas");
		}
		//Quitamos al cliente de la lista de clientes
		Customer c = clientsMap.remove(id);
		//Si es nulo devuelve la excepción InstanceNotFoundException
		if (c == null) {
			throw new InstanceNotFoundException(id, "Cliente no encontrado");
		}
	}

	//Isma
	public Customer findCustomerById(Long id) throws InstanceNotFoundException {
		//Buscamos al cliente en la lista por su id
		Customer c = clientsMap.get(id);
		//Si es nulo devolvemos la excepción InstanceNotFoundException
		if (c == null) {
			throw new InstanceNotFoundException(id, "Cliente no encontrado");
		}
		return c;
	}

	//Pablo
	public static Customer findCustomerByDNI(String dni) throws InstanceNotFoundException {
		//Buscamos al cliente en la lista por su DNI
		Customer c = null;
		for (Customer customer: clientsMap.values()){
			if ((customer.getName().equals(dni))) {
				c = customer;
				// Break porque suponemos que los DNIs no se repiten, entonces al encontrar
				// un cliente que tiene el DNI que buscamos, ya paramos de buscar
				break;
			}
		}
		if (c == null){
			throw new InstanceNotFoundException(dni, "Cliente no encontrado");
		}
		return c;
	}

	//Pablo
	public static List<Customer> getCustomersbyName(String name, Integer start_position, Integer amount){
		List<Customer> mycustomer = new ArrayList<>();

		for (Customer customer: clientsMap.values()){
			if ((customer.getName().toLowerCase().contains(name.toLowerCase()))) {
				mycustomer.add(customer);
			}
		}

		start_position = (start_position == null ? 0 : start_position);
		amount = (amount == null ? mycustomer.size() : amount);

		return mycustomer.subList(start_position, amount);
	}

	//Pablo
	public static PhoneCall AddCall(Long customerId, LocalDateTime startDate, Long duration,
									PhoneCallType tipo, String destinationNumber) {
		//Se crea llamada donde nos proporcionan customerId, fecha y hora, duracion, tipo y destino
		PhoneCall p = new PhoneCall(customerId, startDate, duration, destinationNumber, tipo);
		//creamos el id
		Long id = getNextPhoneCallId();
		p.setPhoneCallId(id);
		//asignamos el estado como pendiente
		p.setPhoneCallStatus(PhoneCallStatus.PENDING);

		//añadimos la llamada del cliente
		phoneCallsMap.put(p.getPhoneCallId(), p);
		return p;
	}

	//Carlos
	public static List<PhoneCall> getCallsbyId(Long customerId, LocalDateTime start_time, LocalDateTime end,
											   PhoneCallType tipo, Integer start_position, Integer amount){
		List<PhoneCall> mycalls = new ArrayList<>();

		for (PhoneCall call: phoneCallsMap.values()) {
			// Recorremos toda a colección de chamadas
			if ((call.getCustomerId() == customerId)
					&& ((start_time == null) || (call.getStartDate().isAfter(start_time)))
					&& ((end == null) || (call.getStartDate().isBefore(end)))
					&& ((tipo == null) || (call.getPhoneCallType().equals(tipo)))) {
				mycalls.add(call);
			}
		}

		start_position = (start_position == null ? 0 : start_position);
		amount = (amount == null ? mycalls.size() : amount);

		return mycalls.subList(start_position, amount);
	}

	//Carlos
	public static Collection<PhoneCall> getCallsbyMonth(Long customerId, int month, int year) throws MonthNotClosedException, CallNotPendingException {
		// Primeiro comprobamos que o mes que nos piden xa pasou
		if (LocalDateTime.now().isBefore(LocalDateTime.of(year, month, 1, 0, 0).plusMonths(1))){
			throw new MonthNotClosedException("O mes aínda non rematou");
		}

		// Esta operación sería máis sinxela se tivéramos o mapa "phoneCallsByUser", a costa de complicar a operación
		// de engadir chamadas ó sistema, como explicamos no LEEME.md
		Collection<PhoneCall> calls = new ArrayList<>();
		for (PhoneCall call: phoneCallsMap.values()) {
			// Recorremos toda a colección de chamadas
			if (call.getCustomerId() == customerId){
				// Si é do cliente que esperamos, comprobamos o mes e o ano
				LocalDateTime date = call.getStartDate();
				if ((date.getMonthValue() == month) && (date.getYear() == year)){
					// Si coincide, gardamos o obxeto PhoneCall nunha nova lista
					calls.add(call);
				}
			}
		}

		// Agora percorremos toda a lista de chamadas e comprobamos que todas están en estado "PENDING"
		for (PhoneCall call: calls) {
			if (!(call.getPhoneCallStatus().equals(PhoneCallStatus.PENDING))){
				// Si atopamos algunha que non estea PENDING, sacamos un erro
				throw new CallNotPendingException("Hai unha chamada que non está en estado PENDING");
			}
		}

		return calls;
	}

	//Carlos
	public static void changeCallsStatus(Long customerId, int month, int year, PhoneCallStatus newstatus) throws CallNotPendingException, MonthNotClosedException {
		// Recuperamos as chamadas do mes indicado
		Collection<PhoneCall> calls = getCallsbyMonth(customerId, month, year);
		// Agora volvemos a percorrer a lista de chamadas e poñémolas todas ó estado correspondiente
		for (PhoneCall call: calls) {
			call.setPhoneCallStatus(newstatus);
		}
	}

}
