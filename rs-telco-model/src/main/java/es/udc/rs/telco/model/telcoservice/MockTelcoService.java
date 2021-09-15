package es.udc.rs.telco.model.telcoservice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.phonecall.PhoneCall;

public class MockTelcoService implements TelcoService {

	private static Map<Long, Customer> clientsMap = new LinkedHashMap<Long, Customer>();
	private static Map<Long,PhoneCall> phoneCallsMap = new LinkedHashMap<Long,PhoneCall>();
	private static Map<Long,List<PhoneCall>> phoneCallsByUserMap = new LinkedHashMap<Long,List<PhoneCall>>();

	private static long lastClientId = 0;
	private static long lastPhoneCallId = 0;
	

	private static synchronized long getNextClientId() {
		return ++lastClientId;
	}
	
	private static synchronized long getNextPhoneCallId() {
		return ++lastPhoneCallId;
	}
	
}
