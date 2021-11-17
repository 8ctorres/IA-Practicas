package es.udc.rs.telco.jaxrs.resources;

import es.udc.rs.telco.jaxrs.dto.CustomerDto;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.rs.telco.model.telcoservice.MockTelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("clientes")
public class CustomerResource {
    public CustomerResource(){}

    TelcoService telcoService = TelcoServiceFactory.getService();

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response addCustomer(CustomerDto newcust, @Context UriInfo ui) throws InputValidationException {

        CustomerDto createdCustomer =
                CustomerDto.from(telcoService.addCustomer(
                        newcust.getName(),
                        newcust.getDni(),
                        newcust.getAddress(),
                        newcust.getPhoneNumber()
        ));

        return Response.created(
                URI.create(
                        ui.getRequestUri().toString() + "/" + createdCustomer.getCustomerId().toString())
        ).build();
    }

    @PUT
    @Path("/{id : \\d+}")
    public void updateCustomer(CustomerDto newcust, @PathParam("id") String id) throws InstanceNotFoundException, InputValidationException {
        newcust.setCustomerId(Long.valueOf(id));
        telcoService.updateCustomer(
                newcust.getCustomerId(),
                newcust.getName(),
                newcust.getDni(),
                newcust.getAddress()
        );
    }

    @DELETE
    @Path("/{id : \\d+}")
    public void deleteCustomer(@PathParam("id") String id) throws InstanceNotFoundException, CustomerHasCallsException, InputValidationException {
        telcoService.removeCustomer(Long.valueOf(id));
    }

    @GET
    @Path("/{id : \\d+}")
    public CustomerDto findCustomerById(@PathParam("id") String id) throws InstanceNotFoundException {
        return CustomerDto.from(
                telcoService.findCustomerById(Long.valueOf(id))
        );
    }

    @GET
    public CustomerDto findCustomerByDNI(@DefaultValue("") @QueryParam("dni") String dni) throws InstanceNotFoundException, InputValidationException {
        return CustomerDto.from(
                telcoService.findCustomerByDNI(dni)
        );
    }
}
