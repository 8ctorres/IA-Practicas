package es.udc.rs.telco.client.service.rest;

import es.udc.rs.telco.client.service.rest.exceptions.CustomerHasCallsClientException;
import es.udc.rs.telco.client.service.rest.exceptions.MonthNotClosedClientException;
import es.udc.rs.telco.client.service.rest.exceptions.UnexpectedCallStatusClientException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import es.udc.rs.telco.client.service.ClientTelcoService;
import es.udc.ws.util.configuration.ConfigurationParametersManager;

import java.time.LocalDateTime;
import java.util.List;

public abstract class RestClientTelcoService implements ClientTelcoService {

	private static jakarta.ws.rs.client.Client client = null;

	private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientTelcoService.endpointAddress";
	private WebTarget endPointWebTarget = null;

	/*
	 * Client instances are expensive resources. It is recommended a configured
	 * instance is reused for the creation of Web resources. The creation of Web
	 * resources, the building of requests and receiving of responses are
	 * guaranteed to be thread safe. Thus a Client instance and WebTarget
	 * instances may be shared between multiple threads.
	 */
	private static Client getClient() {
		if (client == null) {
			client = ClientBuilder.newClient();
		}
		return client;
	}

	private WebTarget getEndpointWebTarget() {
		if (endPointWebTarget == null) {
			endPointWebTarget = getClient()
					.target(ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER));
		}
		return endPointWebTarget;
	}

	protected abstract MediaType getMediaType();

	//Isma
	@Override
	public CustomerDto addCustomer(CustomerDto newCustomer) throws InputValidationException {
		return null;
	}

	//Pablo
	@Override
	public PhoneCallDto addCall(PhoneCallDto newCall) throws InputValidationException, InstanceNotFoundException {
		return null;
	}

	//Isma
	@Override
	public void removeCustomer(CustomerDto cust) throws InputValidationException, InstanceNotFoundException, CustomerHasCallsClientException {

	}

	//Carlos
	@Override
	public List<PhoneCallDto> getCalls(Long customerId, LocalDateTime from, LocalDateTime to) throws InputValidationException, InstanceNotFoundException, MonthNotClosedClientException {
		return null;
	}

	//Carlos
	@Override
	public void changeCallStatus(Long customerId, Integer month, Integer year) throws InputValidationException, InstanceNotFoundException, MonthNotClosedClientException, UnexpectedCallStatusClientException {

	}

}
