package es.udc.rs.telco.client.service.rest;

import com.sun.istack.NotNull;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallDtoJaxb;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallStatus;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallType;
import es.udc.rs.telco.client.service.rest.exceptions.CustomerHasCallsClientException;
import es.udc.rs.telco.client.service.rest.exceptions.MonthNotClosedClientException;
import es.udc.rs.telco.client.service.rest.exceptions.UnexpectedCallStatusClientException;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import es.udc.rs.telco.client.service.ClientTelcoService;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import jakarta.ws.rs.core.Response;

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

		try {
			 Response response = getEndpointWebTarget().path("clientes").request().accept(this.getMediaType())
				.post(Entity.entity(newCustomer.toDtoJaxb(), this.getMediaType()));

			validateResponse(Response.Status.CREATED, response);
			return response.readEntity(CustomerDto.class);
		} catch (InputValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//Pablo
	@Override
	public PhoneCallDto addCall(PhoneCallDto newCall) throws InputValidationException, InstanceNotFoundException {
		return null;
	}

	//Isma
	@Override
	public void removeCustomer(Long idCust) throws InputValidationException, InstanceNotFoundException, CustomerHasCallsClientException {

		try {
			Response response = getEndpointWebTarget().path("clientes/{id}").resolveTemplate("id", idCust).request().accept(this.getMediaType()).delete();
			validateResponse(Response.Status.NO_CONTENT, response);
		} catch (InputValidationException|InstanceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//Carlos
	@Override
	public List<PhoneCallDto> getCalls(Long customerId, LocalDateTime startTime, LocalDateTime endTime, PhoneCallType type) throws InputValidationException, InstanceNotFoundException, MonthNotClosedClientException {
		//En previsión de que vamos a implementar la parte opcional de Hipermedia, no voy a añadir los paŕametros de paginacion
		try (Response response = getEndpointWebTarget().path("llamadas").
				queryParam("customerId", customerId).queryParam("startTime", startTime).
				queryParam("endTime", endTime).queryParam("type", type).
				request().accept(this.getMediaType()).get()){

			validateResponse(Response.Status.OK, response);
			return PhoneCallDto.from(response.readEntity(new GenericType<List<PhoneCallDtoJaxb>>(){}));
		}
	}

	//Carlos
	@Override
	public void changeCallStatus(Long customerId, Integer month, Integer year, PhoneCallStatus newstatus) throws InputValidationException, InstanceNotFoundException, MonthNotClosedClientException, UnexpectedCallStatusClientException {
		try (Response response = getEndpointWebTarget().path("llamadas").path("changestatus").
				queryParam("customerId", customerId).queryParam("month", month).queryParam("year", year).
				queryParam("newstatus", newstatus).request().accept(this.getMediaType()).post(null)) {

			validateResponse(Response.Status.NO_CONTENT, response);
		}
	}

	private void validateResponse(Response.Status expected, @NotNull Response received) throws InputValidationException, InstanceNotFoundException{

	}

}
