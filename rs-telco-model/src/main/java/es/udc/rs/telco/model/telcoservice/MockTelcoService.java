package es.udc.rs.telco.model.telcoservice;

import java.time.LocalDateTime;
import java.util.*;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.rs.telco.model.exceptions.UnexpectedCallStatusException;
import es.udc.rs.telco.model.exceptions.MonthNotClosedException;
import es.udc.rs.telco.model.phonecall.PhoneCall;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;

import es.udc.rs.telco.model.phonecall.PhoneCallType;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class MockTelcoService implements TelcoService {

	private final Map<Long, Customer> clientsMap = new LinkedHashMap<>();
	private final Map<Long, PhoneCall> phoneCallsMap = new LinkedHashMap<>();
	//private static Map<Long, List<PhoneCall>> phoneCallsByUserMap = new LinkedHashMap<Long,List<PhoneCall>>();

	private long lastClientId = 0;
	private long lastPhoneCallId = 0;


	private synchronized long getNextClientId() {
		return ++lastClientId;
	}
	
	private synchronized long getNextPhoneCallId() {
		return ++lastPhoneCallId;
	}

	//Isma
	public Customer addCustomer(String name, String DNI, String address, String phone) throws InputValidationException {
		validateDNI(DNI);
		//Validamos para ser consistentes con el comportamiento de la función findbyDNI que asume que los DNI son únicos
		for (Customer cus:clientsMap.values()) {
			if (DNI.equals(cus.getDni())) {
				throw new InputValidationException("Ya existe un cliente con ese DNI");
			}
		}
		try{
			//Intentamos crear un cliente con los datos proporcionados
			Customer c = new Customer(name, DNI, address, phone);
			//Le asignamos el siguiente ID disponible
			c.setCustomerId(getNextClientId());
			//Le asignamos la fecha actual como fecha de creación
			c.setCreationDate(LocalDateTime.now());
			//Lo metemos en clientsMap con su ID
			clientsMap.put(c.getCustomerId(), c);
			//Devolvemos una copia del objeto cliente
			return new Customer(c);
		} catch (NullPointerException | ClassCastException e){
			throw new InputValidationException("Los datos son inválidos");
		}
	}

	//Isma
	public void updateCustomer(Long id, String name, String DNI, String address) throws InstanceNotFoundException, InputValidationException {
		validateDNI(DNI);
		//Si el cliente no existe devuelve la excepción InstanceNotFoundException
		Customer c = clientsMap.get(id);
		if (c == null) {
			throw new InstanceNotFoundException(id, "Customer");
		}
		//Intenta actualizar los parámetros modificables
		try{
			c.setName(name);
			c.setDni(DNI);
			c.setAddress(address);
		} catch (NullPointerException | ClassCastException e){
			throw new InputValidationException("Los datos son inválidos");
		}
	}

	//Isma
	public void removeCustomer(Long id) throws InstanceNotFoundException, CustomerHasCallsException, InputValidationException {
		if (!(getCallsbyId(id, null, null, null, null, null).isEmpty())){
			throw new CustomerHasCallsException("El cliente especificado tiene llamadas asociadas y no se puede eliminar");
		}
		//La comprobación de si tiene llamadas también comprueba indirectamente si el cliente existe o no,
		//por lo que no hace falta comprobarlo de nuevo
		//Quitamos al cliente de la lista de clientes
		Customer c = clientsMap.remove(id);
	}

	//Isma
	public Customer findCustomerById(Long id) throws InstanceNotFoundException {
		//Buscamos al cliente en la lista por su id
		Customer c = clientsMap.get(id);
		//Si es nulo devolvemos la excepción InstanceNotFoundException
		if (c == null) {
			throw new InstanceNotFoundException(id, "Customer");
		}
		//Devolvemos una copia del objeto cliente
		return new Customer(c);
	}

	//Pablo
	public Customer findCustomerByDNI(String dni) throws InstanceNotFoundException, InputValidationException {
		validateDNI(dni);
		//Buscamos al cliente en la lista por su DNI
		Customer c = null;
		for (Customer customer: clientsMap.values()){
			if ((customer.getDni().equals(dni))) {
				c = customer;
				// Break porque suponemos que los DNI no se repiten, entonces al encontrar
				// un cliente que tiene el DNI que buscamos, ya paramos de buscar
				break;
			}
		}
		if (c == null){
			throw new InstanceNotFoundException(dni, "Customer");
		}
		//Devolvemos una copia del objeto cliente
		return new Customer(c);
	}

	//Pablo
	public List<Customer> findCustomersbyName(String name, Integer start_position, Integer amount){
		List<Customer> mycustomer = new ArrayList<>();

		for (Customer customer: clientsMap.values()){
			if ((customer.getName().toLowerCase().contains(name.toLowerCase()))) {
				//Devolvemos una copia del objeto cliente para cada elemento de la lista
				mycustomer.add(new Customer(customer));
			}
		}

		start_position = (start_position == null ? 0 : start_position);
		amount = (amount == null ? mycustomer.size() : amount);

		return mycustomer.subList(start_position, amount);
	}

	//Pablo
	public PhoneCall addCall(Long customerId, LocalDateTime startDate, Long duration,
									PhoneCallType tipo, String destinationNumber) throws InputValidationException, InstanceNotFoundException {
		//Comprobamos que customerId es valido
		try{
			if (clientsMap.get(customerId) == null) {
				throw new InstanceNotFoundException(customerId, "Customer");
			}
		} catch (NullPointerException | ClassCastException e){
			throw new InputValidationException("CustomerId inválido");
		}

		//Se crea llamada donde nos proporcionan customerId, fecha y hora, duracion, tipo y destino
		PhoneCall p = new PhoneCall(customerId, startDate, duration, destinationNumber, tipo);
		//creamos el id
		Long id = getNextPhoneCallId();
		p.setPhoneCallId(id);
		//asignamos el estado como pendiente
		p.setPhoneCallStatus(PhoneCallStatus.PENDING);

		//añadimos la llamada del cliente
		phoneCallsMap.put(p.getPhoneCallId(), p);

		//Devolvemos una copia del objeto llamada original
		return new PhoneCall(p);
	}

	//Carlos
	public List<PhoneCall> getCallsbyId(Long customerId, LocalDateTime start_time, LocalDateTime end,
											   PhoneCallType tipo, Integer start_position, Integer amount) throws InstanceNotFoundException, InputValidationException {
		List<PhoneCall> mycalls = new ArrayList<>();
		//Validamos que el cliente existe
		try {
			if (clientsMap.get(customerId) == null) {
				throw new InstanceNotFoundException(customerId, "Customer");
			}
		}catch (NullPointerException | ClassCastException e){
			throw new InputValidationException("CustomerId inválido");
		}

		for (PhoneCall call: phoneCallsMap.values()) {
			// Recorremos toda la colección de llamadas buscando las que coinciden con customerId
			// Añadimos a la lista de salida copias de los objetos originales, para non dar acceso desde fuera
			// a los contenidos de los mapas
			if ((call.getCustomerId().equals(customerId))
					&& ((start_time == null) || (call.getStartDate().isAfter(start_time)))
					&& ((end == null) || (call.getStartDate().isBefore(end)))
					&& ((tipo == null) || (call.getPhoneCallType().equals(tipo)))) {
				mycalls.add(new PhoneCall(call));
			}
		}

		start_position = (start_position == null ? 0 : start_position);
		amount = (amount == null ? mycalls.size() : amount);

		return mycalls.subList(start_position, amount);
	}

	//Carlos
	public Collection<PhoneCall> getCallsbyMonth(Long customerId, int month, int year) throws MonthNotClosedException, UnexpectedCallStatusException {
		// Primero comprobamos que el mes que nos piden ya pasó
		if (LocalDateTime.now().isBefore(LocalDateTime.of(year, month, 1, 0, 0).plusMonths(1))){
			throw new MonthNotClosedException("No se puede realizar la operación porque el mes especificado todavía no terminó");
		}

		// Esta operación sería más sencilla si tuviésemos el mapa "phoneCallsByUser", a costa de complicar la operación
		// de añadir llamadas al sistema, como explicamos en el LEEME.md
		Collection<PhoneCall> calls = new ArrayList<>();
		for (PhoneCall call: phoneCallsMap.values()) {
			// Recorremos toda la colección de llamadas
			if (call.getCustomerId().equals(customerId)) {
				// Si es del cliente que esperamos, comprobamos el mes y año
				LocalDateTime date = call.getStartDate();
				if ((date.getMonthValue() == month) && (date.getYear() == year)) {
					// Si coincide, guardamos el objeto PhoneCall en una nueva lista
					// Usamos copias de los objetos originales para evitar sacar a fuera punteros al interior del mapa
					calls.add(new PhoneCall(call));
				}
			}
		}

		return calls;
	}

	//Carlos
	public void changeCallsStatus(Long customerId, int month, int year, PhoneCallStatus newstatus) throws UnexpectedCallStatusException, MonthNotClosedException {
		// Recuperamos las llamadas del mes indicado
		Collection<PhoneCall> calls = getCallsbyMonth(customerId, month, year);

		// Ahora recorremos toda la lista de llamadas y comprobamos que todas están en el estado adecuado
		PhoneCallStatus expectedStatus = (newstatus == PhoneCallStatus.BILLED ? PhoneCallStatus.PENDING : PhoneCallStatus.BILLED);

		for (PhoneCall call: calls) {
			if (!(call.getPhoneCallStatus().equals(expectedStatus))){
				// Si encontramos alguna que no está en el estado correcto, sacamos un error
				throw new UnexpectedCallStatusException("La llamada" + call.getPhoneCallId().toString() + " non está en el estado esperado");
			}
		}

		// Ahora volvemos a recorrer la lista de llamadas y las ponemos todas al estado correspondiente
		for (PhoneCall call: calls) {
			this.changeStoredCallStatus(call.getPhoneCallId(), newstatus);
		}
	}

	public void clearCalls() {
		// Este método borra todas las llamadas del sistema. Se usa solo para los tests. No se expone en la interfaz.
		phoneCallsMap.clear();
	}

	public void removeCall(Long callId){
		// Este método borra una llamada del sistema. Se usa solo para los tests. No se expone en la interfaz.
		phoneCallsMap.remove(callId);
	}

	private void validateDNI(String DNI) throws InputValidationException{
		if(DNI.length() != 9){
			throw new InputValidationException("DNI inválido: "+DNI);
		}
		try{
			Integer.parseInt(DNI.substring(0,7));
		} catch (NumberFormatException e) {
			throw new InputValidationException("DNI inválido: " + DNI);
		}
	}

	// Función privada de utilidad, para poder cambiar los status de las llamadas, ya que las funciones de búsqueda
	// devuelven copias de los objetos originales.
	private void changeStoredCallStatus(Long callId, PhoneCallStatus newstatus){
		this.phoneCallsMap.get(callId).setPhoneCallStatus(newstatus);
	}

}
