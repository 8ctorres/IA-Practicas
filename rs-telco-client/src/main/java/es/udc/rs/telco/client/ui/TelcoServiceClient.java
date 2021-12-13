package es.udc.rs.telco.client.ui;

import es.udc.rs.telco.client.service.ClientTelcoService;
import es.udc.rs.telco.client.service.ClientTelcoServiceFactory;
import es.udc.rs.telco.client.service.dto.CustomerDto;
import es.udc.rs.telco.client.service.dto.PhoneCallDto;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallStatus;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallType;
import es.udc.rs.telco.client.service.rest.exceptions.CustomerHasCallsClientException;
import es.udc.rs.telco.client.service.rest.exceptions.MonthNotClosedClientException;
import es.udc.rs.telco.client.service.rest.exceptions.UnexpectedCallStatusClientException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public class TelcoServiceClient {

	public static void main(String[] args) {

		if (args.length == 0) {
			printUsageAndExit();
		}
		ClientTelcoService clientTelcoService = ClientTelcoServiceFactory.getService();

		//Isma
		if ("-addCustomer".equalsIgnoreCase(args[0])) {
			validateArgs(args, 5, new int[]{});

			// -addCustomer <name> <DNI> <address> <phone>

			try {
				CustomerDto c = new CustomerDto(null, args[1], args[2], args[3], args[4]);
				Long newId = clientTelcoService.addCustomer(c); // Invoke method from the clientTelcoService
				System.out.println("Cliente con ID " + newId.toString() + " creado con éxito");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Argumentos inválidos: " + ex.getLocalizedMessage());
			} catch (Exception ex) {
				printErrorMsgAndExit("Error: " + ex.getLocalizedMessage());
			}

			//Isma
		} else if ("-removeCustomer".equalsIgnoreCase(args[0])) {
			validateArgs(args, 2, new int[]{1});

			// -removeCustomer <customerId>

			try {
				clientTelcoService.removeCustomer(Long.parseLong(args[1]));
				System.out.println("Cliente " + args[1] + " borrado con éxito");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Argumentos inválidos: " + ex.getLocalizedMessage());
			} catch (InstanceNotFoundException ex) {
				printErrorMsgAndExit("Error: El elemento " + ex.getInstanceType() + " con ID " + ex.getInstanceId().toString() + " no existe");
			} catch (CustomerHasCallsClientException ex) {
				printErrorMsgAndExit("Error: " + ex.getLocalizedMessage());
			} catch (Exception ex){
				printErrorMsgAndExit("Error inesperado: " + ex.getLocalizedMessage());
			}

			//Carlos
		} else if ("-getCalls".equalsIgnoreCase(args[0])) {
			//Como el último parámetro es opcional, la lógica de validar argumentos se complica un poco
			PhoneCallType type = null;
			if (args.length == 5) {
				type = phoneCallTypeFromString(args[4]);
				validateArgs(args, 5, new int[]{1});

			}else if (args.length == 4){
				validateArgs(args, 4, new int[]{1});

			}else{
				printUsageAndExit();
			}

			// -getCalls <customerId> <time_from> <time_to> <phoneCallType>
			try {
				List<PhoneCallDto> foundCalls = clientTelcoService.getCalls(Long.parseLong(args[1]), LocalDateTime.parse(args[2]),
						LocalDateTime.parse(args[3]), type);

				System.out.println("Llamadas encontradas:");
				for (PhoneCallDto call : foundCalls){
					System.out.println(call.toString());
				}
			} catch (InstanceNotFoundException ex) {
				printErrorMsgAndExit("Error: El elemento " + ex.getInstanceType() + " con ID " + ex.getInstanceId().toString() + " no existe");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Argumentos inválidos: " + ex.getLocalizedMessage());
			} catch (Exception ex) {
				printErrorMsgAndExit("Error inesperado: " + ex.getLocalizedMessage());
			}

			//Carlos
		} else if ("-changeCallStatus".equalsIgnoreCase(args[0])) {
			validateArgs(args, 5, new int[]{1,2,3});

			// -changeCallStatus <customerId> <month> <year> <newStatus>

			try {
				clientTelcoService.changeCallStatus(Long.parseLong(args[1]), Integer.parseInt(args[2]),
						Integer.parseInt(args[3]), phoneCallStatusFromString(args[4]));

				System.out.println("Estado de las llamadas cambiado correctamente");
			} catch (InstanceNotFoundException ex) {
				printErrorMsgAndExit("El elemento " + ex.getInstanceType() + " con ID " + ex.getInstanceId().toString() + " no existe");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Argumentos inválidos: " + ex.getLocalizedMessage());
			} catch (UnexpectedCallStatusClientException | MonthNotClosedClientException ex) {
				printErrorMsgAndExit("Error: " + ex.getLocalizedMessage());
			} catch (Exception ex) {
				printErrorMsgAndExit("Error inesperado: " + ex.getLocalizedMessage());
			}

			//Pablo
		} else if ("-addCall".equalsIgnoreCase(args[0])) {
			validateArgs(args, 6, new int[]{1, 3});

			// -addCall <customerId> <startTime> <duration> <destinationNumber> <phoneCallType>

			try {
				PhoneCallDto call = new PhoneCallDto(null, Long.parseLong(args[1]), LocalDateTime.parse(args[2]),
						Long.parseLong(args[3]), args[4], phoneCallTypeFromString(args[5]), PhoneCallStatus.PENDING);
				Long newId = clientTelcoService.addCall(call);

				System.out.println("Llamada con ID " + newId.toString() + " creada correctamente");
			} catch (InstanceNotFoundException ex) {
				printErrorMsgAndExit("Error: El elemento " + ex.getInstanceType() + " con ID " + ex.getInstanceId().toString() + " no existe");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Argumentos inválidos: " + ex.getLocalizedMessage());
			} catch (Exception ex) {
				printErrorMsgAndExit("Error inesperado: " + ex.getLocalizedMessage());
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
		System.err.println(err);
		System.exit(-1);
	}

	public static void printUsage() {
		System.err.println(
				"Uso:\n" +
						"    [-addCustomer]    TelcoServiceClient -addCustomer <name> <DNI> <address> <phoneNumber>\n" +
						"    [-removeCustomer]    TelcoServiceClient -removeCustomer <customerId>\n" +
						"    [-getCalls]    TelcoServiceClient -getCalls <customerId> <startTime> <endTime> [callType]\n" +
						"    [-changeCallStatus]    TelcoServiceClient -changeCallStatus <customerId> <month> <year> <newStatus>\n" +
						"    [-addCall]    TelcoServiceClient -addCall <customerId> <startDate> <duration> <destNumber> <type>\n");
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
