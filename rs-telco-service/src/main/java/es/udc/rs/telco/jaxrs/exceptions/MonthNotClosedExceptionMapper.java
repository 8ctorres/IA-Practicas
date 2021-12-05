package es.udc.rs.telco.jaxrs.exceptions;

import es.udc.rs.telco.jaxrs.dto.ApplicationExceptionDtoJaxb;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import es.udc.rs.telco.model.exceptions.MonthNotClosedException;

@Provider
public class MonthNotClosedExceptionMapper implements ExceptionMapper<MonthNotClosedException> {

    @Override
    public Response toResponse(MonthNotClosedException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ApplicationExceptionDtoJaxb("MonthNotClosed", ex.getMessage()))
                .build();
    }
}
