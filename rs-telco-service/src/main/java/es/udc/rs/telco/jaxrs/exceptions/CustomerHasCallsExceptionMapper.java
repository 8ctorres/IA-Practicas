package es.udc.rs.telco.jaxrs.exceptions;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.rs.telco.jaxrs.dto.CustomerHasCallsExceptionDtoJaxb;

@Provider
public class CustomerHasCallsExceptionMapper implements ExceptionMapper<CustomerHasCallsException> {

    @Override
    public Response toResponse(CustomerHasCallsException ex) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new CustomerHasCallsExceptionDtoJaxb(ex.getMessage()))
                .build();
    }
}
