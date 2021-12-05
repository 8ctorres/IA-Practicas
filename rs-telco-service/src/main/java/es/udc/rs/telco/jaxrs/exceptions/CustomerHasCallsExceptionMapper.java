package es.udc.rs.telco.jaxrs.exceptions;

import es.udc.rs.telco.jaxrs.dto.ApplicationExceptionDtoJaxb;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;

@Provider
public class CustomerHasCallsExceptionMapper implements ExceptionMapper<CustomerHasCallsException> {

    @Override
    public Response toResponse(CustomerHasCallsException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ApplicationExceptionDtoJaxb("CustomerHasCalls", ex.getMessage()))
                .build();
    }
}
