package es.udc.rs.telco.client.ui;

import es.udc.rs.telco.client.service.ClientTelcoService;
import es.udc.rs.telco.client.service.ClientTelcoServiceFactory;
import es.udc.rs.telco.client.service.rest.CustomerDto;
import es.udc.rs.telco.model.customer.Customer;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.util.List;

public class TelcoServiceClient {

	public static void main(String[] args) {

		if (args.length == 0) {
			printUsageAndExit();
		}
		ClientTelcoService clientTelcoService = ClientTelcoServiceFactory.getService();
		if ("-addCustomer".equalsIgnoreCase(args[0])) {
			validateArgs(args, 5, new int[] {3});

			// [-addCustomer] TelcoServiceClient -addClient <name> <DNI> <address> <phone>

			try {
				CustomerDto c = new CustomerDto(null, args[1],args[2],args[3],args[4]);
				clientTelcoService.addCustomer(c); // Invoke method from the clientTelcoService
				System.out.println("Client " + c.getCustomerId() + " " + "created successfully");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Invalid arguments: " + ex.getLocalizedMessage());
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}

		} else if ("-findClient".equalsIgnoreCase(args[0])) {
			validateArgs(args, 2, new int[] {1});

			// [-findClient] ClientTelcoServiceClient -findClient <clientId>

			try {
				// ...
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}

		} else if ("-removeClient".equalsIgnoreCase(args[0])) {
			validateArgs(args, 2, new int[] { 1 });

			// [-findClient] ClientTelcoServiceClient -findClient <clientId>

			try {
				clientTelcoService.removeCustomer(Long.parseLong(args[1]));
				System.out.println("Customer " + args[1] + " deleted successfully '");
			} catch (InputValidationException ex) {
				printErrorMsgAndExit("Invalid arguments: " + ex.getLocalizedMessage());
			} catch (InstanceNotFoundException ex) {
				printErrorMsgAndExit("Instance not found: " + ex.getLocalizedMessage());
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}

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
				"Usage:\n" + "    [-addClient]    TelcoServiceClient -addClient <name> ...\n" +
		                     "    [-findClient]   TelcoServiceClient -findClient <clientId>\n" +
						     "    [-removeClient]   TelcoServiceClient -removeClient <clientId>\n" +
						     "    ...\n");
	}

}
