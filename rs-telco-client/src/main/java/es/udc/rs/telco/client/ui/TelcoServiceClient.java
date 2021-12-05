package es.udc.rs.telco.client.ui;

import es.udc.rs.telco.client.service.ClientTelcoService;
import es.udc.rs.telco.client.service.ClientTelcoServiceFactory;
import es.udc.rs.telco.client.service.dto.CustomerDto;
import es.udc.rs.telco.client.service.dto.PhoneCallDto;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallStatus;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallType;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;

public class TelcoServiceClient {

	public static void main(String[] args) {

		if (args.length == 0) {
			printUsageAndExit();
		}
		ClientTelcoService clientTelcoService = ClientTelcoServiceFactory.getService();

		//Isma
		if ("-addCustomer".equalsIgnoreCase(args[0])) {
			validateArgs(args, 5, new int[]{});

			// -addClient <name> <DNI> <address> <phone>

			try {
				CustomerDto c = new CustomerDto(null, args[1], args[2], args[3], args[4]);
				Long newId = clientTelcoService.addCustomer(c); // Invoke method from the clientTelcoService
				System.out.println("Client with ID " + newId.toString() + " created successfully");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Invalid arguments: " + ex.getLocalizedMessage());
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}

			//Isma
		} else if ("-removeCustomer".equalsIgnoreCase(args[0])) {
			validateArgs(args, 2, new int[]{1});

			try {
				clientTelcoService.removeCustomer(Long.parseLong(args[1]));
				System.out.println("Customer " + args[1] + " deleted successfully");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Invalid arguments: " + ex.getLocalizedMessage());
			} catch (InstanceNotFoundException ex) {
				printErrorMsgAndExit("Instance not found: " + ex.getLocalizedMessage());
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}

			//Carlos
		} else if ("-getCalls".equalsIgnoreCase(args[0])) {
			validateArgs(args, 2, new int[]{1});

			try {
				clientTelcoService.getCalls(Long.parseLong(args[1]), LocalDateTime.parse(args[2]),
						LocalDateTime.parse(args[3]), phoneCallTypeFromString(args[4]));
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}

			//Carlos
		} else if ("-changeCallStatus".equalsIgnoreCase(args[0])) {
			validateArgs(args, 2, new int[]{1});

			try {
				clientTelcoService.changeCallStatus(Long.parseLong(args[1]), Integer.parseInt(args[2]),
						Integer.parseInt(args[3]), phoneCallStatusFromString(args[4]));
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}

			//Pablo
		} else if ("-addCall".equalsIgnoreCase(args[0])) {
			validateArgs(args, 6, new int[]{1, 3});

			try {
				PhoneCallDto call = new PhoneCallDto(null, Long.parseLong(args[1]), LocalDateTime.parse(args[2]),
						Long.parseLong(args[3]), args[4], phoneCallTypeFromString(args[5]), PhoneCallStatus.PENDING);
				clientTelcoService.addCall(call);
			}catch (InputValidationException a) {
				printErrorMsgAndExit("Invalid arguments: " + a.getLocalizedMessage());
			}catch (InstanceNotFoundException b){
				printErrorMsgAndExit("Instance not found: " + b.getLocalizedMessage());
			}catch (Exception ex) {
				printErrorMsgAndExit("Unknown Error: " + ex.getLocalizedMessage());
			}
		} else {
			printUsageAndExit();
		}
	}

	public static void validateArgs(String[] args, int expectedArgs, int[] numericArguments) {
		if (expectedArgs != args.length) {
			printUsageAndExit();
		}
		for (int i = 0; i < numericArguments.length; i++) {
			int position = numericArguments[i];
			try {
				Double.parseDouble(args[position]);
			} catch (NumberFormatException n) {
				printUsageAndExit();
			}
		}
	}

	public static void printUsageAndExit() {
		printUsage();
		System.exit(-1);
	}

	public static void printErrorMsgAndExit(String err){
		System.err.println("Error: " + err);
		System.exit(-1);
	}

	public static void printUsage() {
		System.err.println(
				"Usage:\n" +
						"    [-addCustomer]    TelcoServiceClient -addCustomer <name> <DNI> <address> <phoneNumber>\n" +
						"    [-removeCustomer]    TelcoServiceClient -removeCustomer <customerId>\n" +
						"    [-getCalls]    TelcoServiceClient -getCalls <customerId> <startTime> <endTime> [callType]\n" +
						"    [-changeCallStatus]    TelcoServiceClient -changeCallStatus <customerId> <month> <year> <newStatus>\n" +
						"    [-addCall]    TelcoServiceClient -addCall <customerId> <startDate> <duration> <destNumber> <type>\n" +
						"    ...\n");
	}

	// FUNCIONES DE UTILIDAD PARA TRADUCIR LOS ENUMERADOS A STRINGS Y VICEVERSA

	private static PhoneCallType phoneCallTypeFromString(String str){
		switch (str.toUpperCase()){
			case "LOCAL": return PhoneCallType.LOCAL;
			case "NATIONAL": return PhoneCallType.NATIONAL;
			case "INTERNATIONAL": return PhoneCallType.INTERNATIONAL;
		}
		return null;
	}

	private static String phoneCallStatustoString(PhoneCallStatus phoneCallStatus){
		switch (phoneCallStatus){
			case PENDING: return "PENDING";
			case BILLED: return "BILLED";
			case PAID: return "PAID";
		}
		return null;
	}

	private static PhoneCallStatus phoneCallStatusFromString(String str){
		switch (str.toUpperCase()){
			case "PENDING": return PhoneCallStatus.PENDING;
			case "BILLED": return PhoneCallStatus.BILLED;
			case "PAID": return PhoneCallStatus.PAID;
		}
		return null;
	}

	private static String phoneCallTypetoString(PhoneCallType phoneCallType){
		switch (phoneCallType){
			case LOCAL: return "LOCAL";
			case NATIONAL: return "NATIONAL";
			case INTERNATIONAL: return "INTERNATIONAL";
		}
		return null;
	}

}
