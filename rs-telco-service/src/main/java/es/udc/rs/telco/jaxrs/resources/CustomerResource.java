package es.udc.rs.telco.jaxrs.resources;

import es.udc.rs.telco.jaxrs.dto.CustomerDtoJaxb;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

import java.net.URI;
import java.util.List;

import static es.udc.rs.telco.jaxrs.resources.PhoneCallResource.getAcceptableMediaType;

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
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Operation(summary = "Creación de un nuevo cliente", description = "Los parámetros del cliente deben indicarse en el cuerpo de la petición")
    @ApiResponse(responseCode = "201", description = "Cliente creado correctamente")
    @ApiResponse(responseCode = "400", description = "Algún parámetro es incorrecto",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    public Response addCustomer(@RequestBody(description = "Objeto cliente para crear", required = true,
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
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{id : \\d+}")
    @Operation(summary = "Actualizar un cliente registrado",
            description = "Los datos del cliente actualizables deben especificarse en el cuerpo de la petición")
    @ApiResponse(responseCode  = "200", description="Cliente actualizado con éxito")
    @ApiResponse(responseCode = "400", description = "Algún parámetro es incorrecto",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    @ApiResponse(responseCode = "404", description = "El ID del cliente indicado no existe",
            content = @Content(schema = @Schema(implementation = InstanceNotFoundException.class)))
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
    @Path("/{id : \\d+}")
    @Operation(summary = "Eliminar un cliente registrado",
            description = "Se debe especificar el ID del cliente a eliminar, pero si este tiene llamadas registradas no se eliminará")
    @ApiResponse(responseCode  = "200", description="Cliente eliminado con éxito")
    @ApiResponse(responseCode = "400", description = "Algún parámetro es incorrecto",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    @ApiResponse(responseCode = "404", description = "El ID del cliente indicado no existe",
            content = @Content(schema = @Schema(implementation = InstanceNotFoundException.class)))
    @ApiResponse(responseCode = "409", description = "El cliente indicado tiene llamadas asociadas y por tanto no se puede eliminar",
            content = @Content(schema = @Schema(implementation = CustomerHasCallsException.class)))
    public void deleteCustomer(@Parameter(description = "ID del cliente a eliminar", required = true)
                                   @PathParam("id") String id) throws InstanceNotFoundException, CustomerHasCallsException, InputValidationException {
        telcoService.removeCustomer(Long.valueOf(id));
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{id : \\d+}")
    @Operation(summary = "Buscar un cliente por su ID", description = "Muestra el cliente que contenga el ID especificado si lo encuentra")
    @ApiResponse(responseCode = "200", description="Cliente encontrado con éxito")
    @ApiResponse(responseCode = "404", description = "El ID del cliente indicado no existe",
            content = @Content(schema = @Schema(implementation = InstanceNotFoundException.class)))
    public CustomerDtoJaxb findCustomerById(@Parameter(description = "ID del cliente a buscar", required = true) @PathParam("id") String id,
                                            @Context UriInfo ui, @Context HttpHeaders headers) throws InstanceNotFoundException {
        return CustomerDtoJaxb.from(
                telcoService.findCustomerById(Long.valueOf(id)),
                ui.getBaseUri(), this.getClass(), getAcceptableMediaType(headers), PhoneCallResource.class);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{dni}")
    @Operation(summary = "Buscar un cliente por su DNI", description = "Muestra el cliente que contenga el DNI especificado si lo encuentra")
    @ApiResponse(responseCode = "200", description="Cliente encontrado con éxito")
    @ApiResponse(responseCode = "400", description = "Algún parámetro es incorrecto",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    @ApiResponse(responseCode = "404", description = "El DNI del cliente indicado no existe",
            content = @Content(schema = @Schema(implementation = InstanceNotFoundException.class)))
    public CustomerDtoJaxb findCustomerByDNI(@Parameter(description = "DNI del cliente a buscar", required = true) @PathParam("dni") String dni,
                                             @Context UriInfo ui, @Context HttpHeaders headers) throws InstanceNotFoundException, InputValidationException {
        return CustomerDtoJaxb.from(
                telcoService.findCustomerByDNI(dni),
                ui.getBaseUri(), this.getClass(), getAcceptableMediaType(headers), PhoneCallResource.class
        );
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Operation(summary = "Busca clientes cuyo nombre coincide con las palabras clave especificadas",
            description = "Devuelve los clientes cuyo nombre coincide con las palabras clave especificadas")
    @ApiResponse(responseCode = "200", description="Clientes cuyo nombre coincide con las palabras clave especificadas")
    public Response findCustomerByName(@Parameter(description = "Palabras clave a buscar", required = true) @QueryParam("name") @NotNull String name,
                                       @Parameter(description = "Cliente desde el que se empiezan a mostrar los resultados", required = false) @QueryParam("startPos") @DefaultValue("null") String startPosStr,
                                       @Parameter(description = "Cantidad de clientes a mostrar", required = false) @QueryParam("amount") @DefaultValue("null") String amountStr,
                                                    @Context UriInfo ui, @Context HttpHeaders headers) {

        //Valor por defecto, startPos = 0
        int startPos = (startPosStr.equals("null") ? 0 : Integer.parseInt(startPosStr));
        //Valor por defecto, amount = 1
        int amount = (amountStr.equals("null") ? 1 : Integer.parseInt(amountStr));

        List<CustomerDtoJaxb> foundCustomers = CustomerDtoJaxb.from(
                telcoService.findCustomersbyName(name, startPos, amount+1), //Pedimos amount+1 para poder comprobar fácilmente si hay más
                ui.getBaseUri(), this.getClass(), getAcceptableMediaType(headers), PhoneCallResource.class
        );

        boolean hasNext = (foundCustomers.size() == amount+1); //Si se pidieron amount+1 items y no se recibieron todos, es porque ya se acabaron

        List<CustomerDtoJaxb> foundSubList;

        if (hasNext){
            foundSubList = foundCustomers.subList(0, amount);
        }else{
            foundSubList = foundCustomers;
        }

        GenericEntity<List<CustomerDtoJaxb>> entity = new GenericEntity<>(foundSubList){};

        Response.ResponseBuilder response = Response.ok(entity);

        response.link(ui.getRequestUri(), "self");

        if(startPos > 0) {
            int prevStartIndex = Math.max((startPos - amount), 0);

            URI previousUri = UriBuilder.fromUri(ui.getBaseUri()).path(UriBuilder.fromResource(this.getClass()).build().toString())
                    .queryParam("name", name).queryParam("startPos", prevStartIndex).queryParam("amount", amount).build();

            response.link(previousUri, "previous");
        }

        if(hasNext) {
            int nextStartIndex = startPos + amount;

            URI nextUri = UriBuilder.fromUri(ui.getBaseUri()).path(UriBuilder.fromResource(this.getClass()).build().toString())
                    .queryParam("name", name).queryParam("startPos", nextStartIndex).queryParam("amount", amount).build();

            response.link(nextUri, "next");
        }

        return response.build();
    }

}
