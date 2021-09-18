package es.udc.rs.telco.model.telcoservice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.phonecall.PhoneCall;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;

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
	public static Customer addCustomer(String name, String DNI, String address, String phone) throws InputValidationException {
		Customer c = new Customer(name, DNI, address, phone);
		Long id = getNextClientId();
		c.setCustomerId(id);
		c.setCreationDate(LocalDateTime.now());
		clientsMap.put(c.getCustomerId(), c);
		return c;
	}

	//Isma
	public static Customer findCustomerById(long id) throws InstanceNotFoundException {
		Customer c = clientsMap.get(id);
		if (c == null) {
			throw new InstanceNotFoundException(id, "No se encontró el cliente");
		}
		return c;
	}

	//Carlos
	public static void getCallsbyMonth(Long customerId, int month, int year){
		// Primeiro comprobamos que o mes que nos piden xa pasou
		if (LocalDateTime.now().isBefore(LocalDateTime.of(year, month, 1, 0, 0).plusMonths(1))){
			// TODO: Poñer unha excepción axeitada para este erro
			throw new RuntimeException("O mes aínda non rematou");
		}

		List<PhoneCall> calls = new ArrayList<>();
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
				// TODO: Poñer unha excepción axeitada para este erro
				throw new RuntimeException("Hai unha chamada que non está en estado PENDING");
			}
		}
		// Agora volvemos a percorrer a lista de chamadas e poñémolas todas a estado "BILLED"
		for (PhoneCall call: calls) {
			call.setPhoneCallStatus(PhoneCallStatus.BILLED);
		}
	}
	
}
