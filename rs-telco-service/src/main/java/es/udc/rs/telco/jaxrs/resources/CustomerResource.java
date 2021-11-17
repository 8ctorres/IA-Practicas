package es.udc.rs.telco.jaxrs.resources;

import es.udc.rs.telco.jaxrs.dto.CustomerDtoJaxb;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
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

    private TelcoService telcoService = TelcoServiceFactory.getService();

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response addCustomer(CustomerDtoJaxb newcust, @Context UriInfo ui) throws InputValidationException {

        CustomerDtoJaxb createdCustomer =
                CustomerDtoJaxb.from(telcoService.addCustomer(
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
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id : \\d+}")
    public void updateCustomer(CustomerDtoJaxb newcust, @PathParam("id") String id) throws InstanceNotFoundException, InputValidationException {
        newcust.setCustomerId(Long.valueOf(id));
        telcoService.updateCustomer(
                newcust.getCustomerId(),
                newcust.getName(),
                newcust.getDni(),
                newcust.getAddress()
        );
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id : \\d+}")
    public void deleteCustomer(@PathParam("id") String id) throws InstanceNotFoundException, CustomerHasCallsException, InputValidationException {
        telcoService.removeCustomer(Long.valueOf(id));
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id : \\d+}")
    public CustomerDtoJaxb findCustomerById(@PathParam("id") String id) throws InstanceNotFoundException {
        return CustomerDtoJaxb.from(
                telcoService.findCustomerById(Long.valueOf(id))
        );
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public CustomerDtoJaxb findCustomerByDNI(@DefaultValue("") @QueryParam("dni") String dni) throws InstanceNotFoundException, InputValidationException {
        return CustomerDtoJaxb.from(
                telcoService.findCustomerByDNI(dni)
        );
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public List<CustomerDtoJaxb> findCustomerByName(@QueryParam("name") String name, @DefaultValue("null") String start_position, @DefaultValue("null") String amount) {

        Integer start_pos_Int = (start_position.equals("null") ? null : Integer.valueOf(start_position));
        Integer amountInt = (amount.equals("null") ? null : Integer.valueOf(amount));

        return CustomerDtoJaxb.from(
                telcoService.findCustomersbyName(name, start_pos_Int, amountInt)
        );
    }
}
