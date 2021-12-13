package es.udc.rs.telco.jaxrs.resources;


import es.udc.rs.telco.jaxrs.dto.PhoneCallDtoJaxb;
import es.udc.rs.telco.model.exceptions.MonthNotClosedException;
import es.udc.rs.telco.model.exceptions.UnexpectedCallStatusException;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;
import es.udc.rs.telco.model.phonecall.PhoneCallType;
import es.udc.rs.telco.model.telcoservice.TelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Path("llamadas")
public class PhoneCallResource {
    public PhoneCallResource(){}

    private TelcoService telcoService = TelcoServiceFactory.getService();

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Operation(summary = "Creación de una nueva llamada", description = "Los parámetros de la llamada deben indicarse en el cuerpo de la petición")
    @ApiResponse(responseCode = "201", description = "Llamada creada correctamente")
    @ApiResponse(responseCode = "400", description = "Los argumentos son inválidos o incorrectos",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    @ApiResponse(responseCode = "404", description = "El ID del cliente indicado no existe",
            content = @Content(schema = @Schema(implementation = InstanceNotFoundException.class)))
    public Response addPhoneCall(@RequestBody(description = "Objeto llamadas para crear", required = true,
            content = @Content(schema = @Schema(implementation = PhoneCallDtoJaxb.class))) PhoneCallDtoJaxb newCall, @Context UriInfo ui) throws InstanceNotFoundException, InputValidationException {

        Long newId =
                telcoService.addCall(
                        newCall.getCustomerId(),
                        newCall.getStartDate(),
                        newCall.getDuration(),
                        newCall.getPhoneCallType(),
                        newCall.getDestinationNumber()
                ).getPhoneCallId();

        return Response.created(
                URI.create(
                        ui.getRequestUri().toString() + "/" + newId.toString()
                )
        ).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Operation(summary = "Buscar llamadas según unos parámetros",
            description = "Si se indican cliente, mes y año se devuelven todas las llamadas del cliente en ese mes." +
                    "Si se indican cliente y unos constraints de tiempo, se devuelven las llamadas del cliente en ese período de tiempo, paginadas")
    @ApiResponse(responseCode  = "200", description="Búsqueda exitosa, devuelve las llamadas")
    @ApiResponse(responseCode = "400", description="Los argumentos son inválidos o incorrectos",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    @ApiResponse(responseCode = "404", description="El ID del cliente indicado no existe",
            content = @Content(schema = @Schema(implementation = InstanceNotFoundException.class)))
    @ApiResponse(responseCode = "409", description="Error de lógica de aplicación: El mes buscado todavía no terminó",
            content = @Content(schema = @Schema(implementation = MonthNotClosedException.class)))
    public Response getCallsBy(@Parameter(description = "ID del cliente", required = true) @NotNull @QueryParam("customerId") String customerIdStr,
                               @Parameter(description = "Inicio del rango de tiempo a buscar") @DefaultValue("null") @QueryParam("startTime") String startTimeStr,
                               @Parameter(description = "Final del rango de tiempo a buscar") @DefaultValue("null") @QueryParam("endTime") String endTimeStr,
                               @Parameter(description = "Tipo de llamadas a buscar") @DefaultValue("null") @QueryParam("type") String phoneCallTypeStr,
                               @Parameter(description = "Posición de la lista a partir de la cual queremos resultados. Usado para paginación")
                                   @DefaultValue("null") @QueryParam("startPos") String startPosStr,
                               @Parameter(description = "Cantidad de resultos que queremos. Usado para paginacion")
                                   @DefaultValue("null") @QueryParam("amount") String amountStr,
                               @Parameter(description = "Mes de las llamadas buscadas") @DefaultValue("null") @QueryParam("month") String monthStr,
                               @Parameter(description = "Año de las llamadas buscadas") @DefaultValue("null") @QueryParam("year") String yearStr,
                                             @Context UriInfo ui, @Context HttpHeaders headers
                                             ) throws InputValidationException, InstanceNotFoundException, MonthNotClosedException {

        // Si están los parámetros de MES y AÑO, tiene que ser getCallsByMonth
        if (!(monthStr.equals("null")) && !(yearStr.equals("null"))){
            // No puede haber ningun otro parametro pasado
            if (!(startTimeStr.equals("null"))
                || !(endTimeStr.equals("null"))
                || !(phoneCallTypeStr.equals("null"))
                || !(startPosStr.equals("null"))
                || !(amountStr.equals("null"))){
                throw new InputValidationException("Parámetros inválidos");
            }
            return getCallsbyMonth(customerIdStr, monthStr, yearStr, ui, headers);
        }
        // Si no están AMBOS parámetros mes y año, no puede estar ninguno de ellos
        else{
            if (!(monthStr.equals("null")) || !(yearStr.equals("null"))){
                throw new InputValidationException("Parámetros inválidos");
            }
            // Si no están mes ni año, es una llamada genérica de getCallsByCustomerId
            return getCallsByCustomerId(customerIdStr, startTimeStr, endTimeStr, phoneCallTypeStr,
                    startPosStr, amountStr, ui, headers);
        }
    }


    private Response getCallsByCustomerId(String customerIdStr, String startTimeStr, String endTimeStr,
                                                       String phoneCallTypeStr,String startPosStr, String amountStr, UriInfo ui, HttpHeaders headers)
            throws InstanceNotFoundException, InputValidationException {

        Long customerId;
        try {
            customerId = Long.valueOf(customerIdStr);
        }catch(NumberFormatException e){
            throw new InputValidationException(e.getMessage());
        }

        LocalDateTime startTime = (startTimeStr.equals("null") ? null : LocalDateTime.parse(startTimeStr));
        LocalDateTime endTime = (endTimeStr.equals("null") ? null : LocalDateTime.parse(endTimeStr));

        //Valor por defecto, startPos = 0
        int startPos = (startPosStr.equals("null") ? 0 : Integer.parseInt(startPosStr));
        //Valor por defecto, amount = 1
        int amount = (amountStr.equals("null") ? 1 : Integer.parseInt(amountStr));

        List<PhoneCallDtoJaxb> foundCalls = PhoneCallDtoJaxb.from(
                telcoService.getCallsbyId(
                        customerId,
                        startTime,
                        endTime,
                        phoneCallTypeFromString(phoneCallTypeStr),
                        startPos, amount +1 //Se pasa +1 para poder comprobar si existe el siguiente o no
                ),ui.getBaseUri(), this.getClass(), getAcceptableMediaType(headers));

        boolean hasNext = (foundCalls.size() == amount+1); //Si se pidieron amount+1 items y no se recibieron todos, es porque ya se acabaron

        List<PhoneCallDtoJaxb> foundSubList;

        if (hasNext){
            foundSubList = foundCalls.subList(0, amount);
        }else{
            foundSubList = foundCalls;
        }

        GenericEntity<List<PhoneCallDtoJaxb>> entity = new GenericEntity<>(foundSubList){};

        Response.ResponseBuilder response = Response.ok(entity);

        response.link(ui.getRequestUri(), "self");

        if(startPos > 0) {
            int prevStartIndex = Math.max((startPos - amount), 0);

            URI previousUri = UriBuilder.fromUri(ui.getBaseUri()).path(UriBuilder.fromResource(this.getClass()).build().toString())
                    .queryParam("customerId", customerIdStr).queryParam("startTime", startTimeStr).queryParam("endTime", endTimeStr)
                    .queryParam("type", phoneCallTypeStr).queryParam("startPos", prevStartIndex).queryParam("amount", amountStr).build();

            response.link(previousUri, "previous");
        }

        if(hasNext) {
            int nextStartIndex = startPos + amount;

            URI nextUri = UriBuilder.fromUri(ui.getBaseUri()).path(UriBuilder.fromResource(this.getClass()).build().toString())
                    .queryParam("customerId", customerIdStr).queryParam("startTime", startTimeStr).queryParam("endTime", endTimeStr)
                    .queryParam("type", phoneCallTypeStr).queryParam("startPos", nextStartIndex).queryParam("amount", amount).build();

            response.link(nextUri, "next");
        }

        return response.build();
    }


    private Response getCallsbyMonth(String customerIdStr, String monthStr, String yearStr, UriInfo ui, HttpHeaders headers)
            throws MonthNotClosedException, InputValidationException {

        Long customerId;
        Integer month, year;

        try {
            customerId = Long.valueOf(customerIdStr);
            month = Integer.valueOf(monthStr);
            year = Integer.valueOf(yearStr);
        }catch(NumberFormatException e){
            throw new InputValidationException(e.getMessage());
        }

        List<PhoneCallDtoJaxb> calls = PhoneCallDtoJaxb.from(
                        telcoService.getCallsbyMonth(
                                customerId,
                                month,
                                year
                        ), ui.getBaseUri(), this.getClass(), getAcceptableMediaType(headers));

        GenericEntity<List<PhoneCallDtoJaxb>> entity = new GenericEntity<>(calls){};

        return Response.ok(entity).link(ui.getRequestUri(), "self").build();
    }

    @POST
    @Path("/changestatus")
    @Operation(summary = "Cambiar el estado de unas llamadas",
            description = "Las llamadas pasan de estado PENDING a BILLED, o de BILLED a PAID, representando si están ya pagadas por el cliente")
    @ApiResponse(responseCode  = "204", description="Llamadas actualizadas con éxito")
    @ApiResponse(responseCode = "400", description="Los argumentos son inválidos o incorrectos",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    @ApiResponse(responseCode = "404", description="El ID de cliente especificado no existe",
            content = @Content(schema = @Schema(implementation = InstanceNotFoundException.class)))
    @ApiResponse(responseCode = "409", description="Error de lógica de aplicación: El mes indicado todavía no está cerrado",
            content = @Content(schema = @Schema(implementation = MonthNotClosedException.class)))
    @ApiResponse(responseCode = "409", description="Error de lógica de aplicación: Una llamada está en un estado incoherente",
            content = @Content(schema = @Schema(implementation = UnexpectedCallStatusException.class)))
    public void changeStatus(@Parameter(description = "ID del cliente a actualizar") @NotNull @QueryParam("customerId") String customerIdStr,
                             @Parameter(description = "Mes de las llamadas a actualizar") @NotNull @QueryParam("month") String monthStr,
                             @Parameter(description = "Año de las llamadas a actualizar") @NotNull @QueryParam("year") String yearStr,
                             @Parameter(description = "Nuevo estado para las llamadas") @NotNull @QueryParam("newStatus") String newStatusStr)
            throws InputValidationException, MonthNotClosedException, UnexpectedCallStatusException {
        Long customerId;
        Integer month, year;

        try {
            customerId = Long.valueOf(customerIdStr);
            month = Integer.valueOf(monthStr);
            year = Integer.valueOf(yearStr);
        }catch(NumberFormatException e){
            throw new InputValidationException(e.getMessage());
        }

        telcoService.changeCallsStatus(customerId, month, year, phoneCallStatusFromString(newStatusStr));
    }

    // FUNCIONES DE UTILIDAD PARA TRADUCIR LOS ENUMERADOS A STRINGS Y VICEVERSA

    private static PhoneCallType phoneCallTypeFromString(String str){
        switch (str.toUpperCase()){
            case "LOCAL": return PhoneCallType.LOCAL;
            case "NATIONAL": return PhoneCallType.NATIONAL;
            case "INTERNATIONAL": return PhoneCallType.INTERNATIONAL;
        }
        return null;
    }

    private static String phoneCallStatustoString(PhoneCallStatus phoneCallStatus){
        switch (phoneCallStatus){
            case PENDING: return "PENDING";
            case BILLED: return "BILLED";
            case PAID: return "PAID";
        }
        return null;
    }

    private static PhoneCallStatus phoneCallStatusFromString(String str){
        switch (str.toUpperCase()){
            case "PENDING": return PhoneCallStatus.PENDING;
            case "BILLED": return PhoneCallStatus.BILLED;
            case "PAID": return PhoneCallStatus.PAID;
        }
        return null;
    }

    private static String phoneCallTypetoString(PhoneCallType phoneCallType){
        switch (phoneCallType){
            case LOCAL: return "LOCAL";
            case NATIONAL: return "NATIONAL";
            case INTERNATIONAL: return "INTERNATIONAL";
        }
        return null;
    }

    static String getAcceptableMediaType(HttpHeaders headers){
        List<MediaType> acceptableMediaTypes = headers.getAcceptableMediaTypes();
        for (MediaType mt: acceptableMediaTypes) {
            if ((MediaType.APPLICATION_XML_TYPE.isCompatible(mt))
                    || (MediaType.APPLICATION_JSON_TYPE.isCompatible(mt))){
                return (mt == null ? null : mt.toString());
            }
        }
        return null;
    }
}
