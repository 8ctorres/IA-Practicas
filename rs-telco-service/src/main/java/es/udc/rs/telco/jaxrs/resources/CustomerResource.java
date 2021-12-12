package es.udc.rs.telco.jaxrs.resources;

import es.udc.rs.telco.jaxrs.dto.CustomerDtoJaxb;
import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.rs.telco.model.telcoservice.TelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@Path("clientes")
@OpenAPIDefinition(
        info = @Info(
                title = "Documentacion OpenAPI de rs-telco",
                version = "3.0.1",
                description = "Documentacion acerca del servicio de telecomunicaciones Telco, donde se gestionan clientes y sus llamadas"),
        servers = {
                @Server(
                        description = "Servidor en Localhost",
                        url = "http://localhost:7070/rs-telco-service")
        })
public class CustomerResource {
    public CustomerResource(){}

    private TelcoService telcoService = TelcoServiceFactory.getService();

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Creación de un nuevo cliente", description = "Los parámetros del cliente deben indicarse en el cuerpo de la petición")
    @ApiResponse(responseCode = "201", description = "URI que apunta al cliente creado correctamente",
            content = @Content(schema = @Schema(implementation = CustomerDtoJaxb.class)))
    @ApiResponse(responseCode = "400", description = "Algún parámetro es incorrecto",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    public Response addCustomer(@RequestBody(description = "Objeto cliente creado", required = true,
            content = @Content(schema = @Schema(implementation = CustomerDtoJaxb.class))) CustomerDtoJaxb newcust, @Context UriInfo ui) throws InputValidationException {

        Long createdId =
                telcoService.addCustomer(
                        newcust.getName(),
                        newcust.getDni(),
                        newcust.getAddress(),
                        newcust.getPhoneNumber()
                ).getCustomerId();

        return Response.created(
                URI.create(
                        ui.getRequestUri().toString() + "/" + createdId.toString())
        ).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id : \\d+}")
    @Operation(summary = "Actualizar un cliente registrado",
            description = "Los datos del cliente actualizables deben especificarse en el cuerpo de la petición")
    @ApiResponse(responseCode  = "200", description="Cliente actualizado con éxito")
    @ApiResponse(responseCode = "400", description="InputValidationException")
    @ApiResponse(responseCode = "404", description="InstanceNotFoundException")
    public void updateCustomer(CustomerDtoJaxb newcust, @Parameter(description = "ID del cliente a actualizar", required = true)
            @PathParam("id") String id) throws InstanceNotFoundException, InputValidationException {
        newcust.setCustomerId(Long.valueOf(id));
        telcoService.updateCustomer(
                newcust.getCustomerId(),
                newcust.getName(),
                newcust.getDni(),
                newcust.getAddress()
        );
    }

    @DELETE
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id : \\d+}")
    @Operation(summary = "Eliminar un cliente registrado",
            description = "Se debe especificar el ID del cliente a eliminar, pero si este tiene llamadas registradas no se eliminará")
    @ApiResponse(responseCode  = "200", description="Cliente eliminado con éxito")
    @ApiResponse(responseCode = "400", description="InputValidationException o CustomerHasCallsException")
    @ApiResponse(responseCode = "404", description="InstanceNotFoundException")
    public void deleteCustomer(@Parameter(description = "ID del cliente a eliminar", required = true)
                                   @PathParam("id") String id) throws InstanceNotFoundException, CustomerHasCallsException, InputValidationException {
        telcoService.removeCustomer(Long.valueOf(id));
    }


    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id : \\d+}")
    @Operation(summary = "Buscar un cliente por su ID", description = "Muestra el cliente que contenga el ID especificado si lo encuentra")
    @ApiResponse(responseCode = "200", description="Cliente encontrado con éxito")
    @ApiResponse(responseCode = "404", description= "InstanceNotFoundException")
    public CustomerDtoJaxb findCustomerById(@Parameter(description = "ID del cliente a buscar", required = true) @PathParam("id") String id,
                                            @Context UriInfo ui) throws InstanceNotFoundException {
        return CustomerDtoJaxb.from(
                telcoService.findCustomerById(Long.valueOf(id)),
                ui.getBaseUri(), this.getClass(), MediaType.APPLICATION_XML.toString());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{dni}")
    @Operation(summary = "Buscar un cliente por su DNI", description = "Muestra el cliente que contenga el DNI especificado si lo encuentra")
    @ApiResponse(responseCode = "200", description="Cliente encontrado con éxito")
    @ApiResponse(responseCode = "400", description="InputValidationException")
    @ApiResponse(responseCode = "404", description= "InstanceNotFoundException")
    public CustomerDtoJaxb findCustomerByDNI(@Parameter(description = "DNI del cliente a buscar", required = true) @PathParam("dni") String dni,
                                             @Context UriInfo ui) throws InstanceNotFoundException, InputValidationException {
        return CustomerDtoJaxb.from(
                telcoService.findCustomerByDNI(dni),
                ui.getBaseUri(), this.getClass(), MediaType.APPLICATION_XML.toString()
        );
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Busca clientes cuyo nombre coincide con las palabras clave especificadas",
            description = "Devuelve los clientes cuyo nombre coincide con las palabras clave especificadas")
    @ApiResponse(responseCode = "200", description="Clientes cuyo nombre coincide con las palabras clave especificadas")
    public Response findCustomerByName(@Parameter(description = "Palabras clave a buscar", required = true) @QueryParam("name") @NotNull String name,
                                       @Parameter(description = "Cliente desde el que se empiezan a mostrar los resultados", required = false) @QueryParam("startPos") @DefaultValue("null") String start_position,
                                       @Parameter(description = "Cantidad de clientes a mostrar", required = false) @QueryParam("amount") @DefaultValue("null") String amount,
                                                    @Context UriInfo ui) {

        Integer start_pos_Int = (start_position.equals("null") ? null : Integer.valueOf(start_position));
        Integer amountInt = (amount.equals("null") ? null : Integer.valueOf(amount));

        List<CustomerDtoJaxb> found = CustomerDtoJaxb.from(
                telcoService.findCustomersbyName(name, start_pos_Int, amountInt),
                ui.getBaseUri(), this.getClass(), MediaType.APPLICATION_XML.toString()
        );

        GenericEntity<List<CustomerDtoJaxb>> entity = new GenericEntity<>(found){};

        return Response.ok(entity).
                link(ui.getRequestUri(), "self").build();
    }

}
