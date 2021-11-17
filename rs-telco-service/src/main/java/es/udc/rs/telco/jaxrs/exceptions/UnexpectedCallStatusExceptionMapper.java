package es.udc.rs.telco.jaxrs.exceptions;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import es.udc.rs.telco.model.exceptions.UnexpectedCallStatusException;
import es.udc.rs.telco.jaxrs.dto.UnexpectedCallStatusExceptionDtoJaxb;

@Provider
public class UnexpectedCallStatusExceptionMapper implements ExceptionMapper<UnexpectedCallStatusException> {

    @Override
    public Response toResponse(UnexpectedCallStatusException ex) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new UnexpectedCallStatusExceptionDtoJaxb(ex.getMessage()))
                .build();
    }
}
