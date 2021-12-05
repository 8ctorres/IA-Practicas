package es.udc.rs.telco.jaxrs.exceptions;

import es.udc.rs.telco.jaxrs.dto.ApplicationExceptionDtoJaxb;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import es.udc.rs.telco.model.exceptions.UnexpectedCallStatusException;

@Provider
public class UnexpectedCallStatusExceptionMapper implements ExceptionMapper<UnexpectedCallStatusException> {

    @Override
    public Response toResponse(UnexpectedCallStatusException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ApplicationExceptionDtoJaxb("UnexpectedCallStatus", ex.getMessage()))
                .build();
    }
}
