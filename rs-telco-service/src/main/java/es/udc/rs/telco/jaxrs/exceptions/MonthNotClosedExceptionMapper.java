package es.udc.rs.telco.jaxrs.exceptions;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import es.udc.rs.telco.model.exceptions.MonthNotClosedException;
import es.udc.rs.telco.jaxrs.dto.MonthNotClosedExceptionDtoJaxb;

@Provider
public class MonthNotClosedExceptionMapper implements ExceptionMapper<MonthNotClosedException> {

    @Override
    public Response toResponse(MonthNotClosedException ex) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new MonthNotClosedExceptionDtoJaxb(ex.getMessage()))
                .build();
    }
}
