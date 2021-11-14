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
				throw new InputValidationException("DNI already exists");
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
			throw new InstanceNotFoundException(id, "Cliente no encontrado");
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
			throw new InstanceNotFoundException(dni, "Cliente no encontrado");
		}
		//Devolvemos una copia del objeto cliente
		return new Customer(c);
	}

	//Pablo
	public List<Customer> getCustomersbyName(String name, Integer start_position, Integer amount){
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
				throw new InstanceNotFoundException(customerId, "Cliente no existe");
			}
		} catch (NullPointerException | ClassCastException e){
			throw new InputValidationException("CustomerId es inválido");
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
				throw new InstanceNotFoundException(customerId, "There is no Customer with that customerId");
			}
		}catch (NullPointerException | ClassCastException e){
			throw new InputValidationException("CustomerId is invalid");
		}

		for (PhoneCall call: phoneCallsMap.values()) {
			// Recorremos toda a colección de chamadas buscando as que coinciden co customerId
			// Engadimos á lista de saída copias dos obxectos orixinais, para non dar acceso dende fóra
			// ós contidos dos mapas
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
		// Primeiro comprobamos que o mes que nos piden xa pasou
		if (LocalDateTime.now().isBefore(LocalDateTime.of(year, month, 1, 0, 0).plusMonths(1))){
			throw new MonthNotClosedException("O mes aínda non rematou");
		}

		// Esta operación sería máis sinxela se tivéramos o mapa "phoneCallsByUser", a costa de complicar a operación
		// de engadir chamadas ó sistema, como explicamos no LEEME.md
		Collection<PhoneCall> calls = new ArrayList<>();
		for (PhoneCall call: phoneCallsMap.values()) {
			// Recorremos toda a colección de chamadas
			if (call.getCustomerId().equals(customerId)) {
				// Si é do cliente que esperamos, comprobamos o mes e o ano
				LocalDateTime date = call.getStartDate();
				if ((date.getMonthValue() == month) && (date.getYear() == year)) {
					// Si coincide, gardamos o obxeto PhoneCall nunha nova lista
					if (!(call.getPhoneCallStatus().equals(PhoneCallStatus.PENDING))) {
						throw new UnexpectedCallStatusException("Call was not PENDING when trying to retrieve it");
					}
					// Usamos copias dos obxectos orixinais para evitar sacaar a fora punteiros o interior do mapa
					calls.add(new PhoneCall(call));
				}
			}
		}

		return calls;
	}

	//Carlos
	public void changeCallsStatus(Long customerId, int month, int year, PhoneCallStatus newstatus) throws UnexpectedCallStatusException, MonthNotClosedException {
		// Recuperamos as chamadas do mes indicado
		Collection<PhoneCall> calls = getCallsbyMonth(customerId, month, year);

		// Agora percorremos toda a lista de chamadas e comprobamos que todas están en estado no estado adecuado
		PhoneCallStatus expectedStatus = (newstatus == PhoneCallStatus.BILLED ? PhoneCallStatus.PENDING : PhoneCallStatus.BILLED);

		for (PhoneCall call: calls) {
			if (!(call.getPhoneCallStatus().equals(expectedStatus))){
				// Si atopamos algunha que non estea no estado correcto, sacamos un erro
				throw new UnexpectedCallStatusException("A chamada" + call.getPhoneCallId().toString() + " non está no estado esperado");
			}
		}

		// Agora volvemos a percorrer a lista de chamadas e poñémolas todas ó estado correspondiente
		for (PhoneCall call: calls) {
			this.changeStoredCallStatus(call.getPhoneCallId(), newstatus);
		}
	}

	public void clearCalls() {
		// This method deletes all the calls in the system. Used for unit tests only. Not exposed in the interface
		phoneCallsMap.clear();
	}

	public void removeCall(Long callId){
		// This method deletes a call from the system. Used for unit tests only. Not exposed in the interface.
		phoneCallsMap.remove(callId);
	}

	private void validateDNI(String DNI) throws InputValidationException{
		if(DNI.length() != 9){
			throw new InputValidationException("Invalid DNI: "+DNI);
		}
		try{
			Integer.parseInt(DNI.substring(0,7));
		} catch (NumberFormatException e) {
			throw new InputValidationException("Invalid DNI: " + DNI);
		}
	}

	// Función privada de utilidad, para poder cambiar los status de las llamadas, ya que las funciones de búsqueda
	// devuelven copias de los objetos originales.
	private void changeStoredCallStatus(Long callId, PhoneCallStatus newstatus){
		this.phoneCallsMap.get(callId).setPhoneCallStatus(newstatus);
	}

}
