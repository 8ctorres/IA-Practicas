package es.udc.rs.telco.jaxrs.resources;

import es.udc.rs.telco.jaxrs.dto.CustomerDtoJaxb;
import es.udc.rs.telco.jaxrs.dto.PhoneCallDtoJaxb;
import es.udc.rs.telco.model.exceptions.MonthNotClosedException;
import es.udc.rs.telco.model.exceptions.UnexpectedCallStatusException;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;
import es.udc.rs.telco.model.phonecall.PhoneCallType;
import es.udc.rs.telco.model.telcoservice.TelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
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
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Creación de una nueva llamada", description = "Los parámetros de la llamada deben indicarse en el cuerpo de la petición")
    @ApiResponse(responseCode = "201", description = "Llamada creada correctamente",
            content = @Content(schema = @Schema(implementation = PhoneCallDtoJaxb.class)))
    @ApiResponse(responseCode = "400", description = "Algún parámetro es incorrecto",
            content = @Content(schema = @Schema(implementation = InputValidationException.class)))
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
            content = @Content(schema = @Schema(implementation = InstanceNotFoundException.class)))
    public Response addPhoneCall(PhoneCallDtoJaxb newCall, @Context UriInfo ui) throws InstanceNotFoundException, InputValidationException {

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
    @Produces(MediaType.APPLICATION_XML)
    public Response getCallsBy(@NotNull @QueryParam("customerId") String customerIdStr,
                                             @DefaultValue("null") @QueryParam("startTime") String startTimeStr,
                                             @DefaultValue("null") @QueryParam("endTime") String endTimeStr,
                                             @DefaultValue("null") @QueryParam("type") String phoneCallTypeStr,
                                             @DefaultValue("null") @QueryParam("startPos") String startPosStr,
                                             @DefaultValue("null") @QueryParam("amount") String amountStr,
                                             @DefaultValue("null") @QueryParam("month") String monthStr,
                                             @DefaultValue("null") @QueryParam("year") String yearStr,
                                             @Context UriInfo ui
                                             ) throws InputValidationException, InstanceNotFoundException, MonthNotClosedException, UnexpectedCallStatusException {

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
            return getCallsbyMonth(customerIdStr, monthStr, yearStr, ui);
        }
        // Si no están AMBOS parámetros mes y año, no puede estar ninguno de ellos
        else{
            if (!(monthStr.equals("null")) || !(yearStr.equals("null"))){
                throw new InputValidationException("Parámetros inválidos");
            }
            // Si no están mes ni año, es una llamada genérica de getCallsByCustomerId
            return getCallsByCustomerId(customerIdStr, startTimeStr, endTimeStr, phoneCallTypeStr,
                    startPosStr, amountStr, ui);
        }
    }


    private Response getCallsByCustomerId(String customerIdStr, String startTimeStr, String endTimeStr,
                                                       String phoneCallTypeStr,String startPosStr, String amountStr, UriInfo ui)
            throws InstanceNotFoundException, InputValidationException {

        Long customerId;
        try {
            customerId = Long.valueOf(customerIdStr);
        }catch(NumberFormatException e){
            throw new InputValidationException(e.getMessage());
        }

        LocalDateTime startTime = (startTimeStr.equals("null") ? null : LocalDateTime.parse(startPosStr));
        LocalDateTime endTime = (endTimeStr.equals("null") ? null : LocalDateTime.parse(amountStr));

        //Valor por defecto, startPos = 0
        int startPos = (startPosStr.equals("null") ? 0 : Integer.parseInt(startPosStr));
        //Valor por defecto, amount = 2
        int amount = (amountStr.equals("null") ? 2 : Integer.parseInt(amountStr));

        List<PhoneCallDtoJaxb> foundCalls = PhoneCallDtoJaxb.from(
                telcoService.getCallsbyId(
                        customerId,
                        startTime,
                        endTime,
                        phoneCallTypeFromString(phoneCallTypeStr)
                ),ui.getBaseUri(), this.getClass(), MediaType.APPLICATION_XML);

        //int startIndex = (foundCalls.size() >= startPos ? foundCalls.size()-1 : startPos);
        //int toIndex = (foundCalls.size() >= (startIndex+amount) ? foundCalls.size() : startIndex+amount);
        int startIndex = Math.max(foundCalls.size()-1, startPos);
        int toIndex = Math.max(foundCalls.size(), startIndex + amount);
        Response.ResponseBuilder response = Response.ok(foundCalls.subList(startIndex, toIndex));

        response.link(ui.getRequestUri(), "self");

        if(startIndex > 0) {
            int prevStartIndex = Math.max((startIndex - amount), 0);

            URI previousUri = UriBuilder.fromUri(ui.getBaseUri()).path(UriBuilder.fromResource(this.getClass()).build().toString())
                    .queryParam("customerId", customerIdStr).queryParam("startTime", startTimeStr).queryParam("endTime", endTimeStr)
                    .queryParam("startPos", prevStartIndex).queryParam("amount", amountStr).build();

            response.link(previousUri, "previous");
        }

        if((startIndex + amount) < foundCalls.size()) {
            int nextStartIndex = startIndex + amount;

            URI nextUri = UriBuilder.fromUri(ui.getBaseUri()).path(UriBuilder.fromResource(this.getClass()).build().toString())
                    .queryParam("customerId", customerIdStr).queryParam("startTime", startTimeStr).queryParam("endTime", endTimeStr)
                    .queryParam("startPos", nextStartIndex).queryParam("amount", amountStr).build();

            response.link(nextUri, "next");
        }
        return response.build();
    }


    private Response getCallsbyMonth(String customerIdStr, String monthStr, String yearStr, UriInfo ui)
            throws MonthNotClosedException, UnexpectedCallStatusException, InputValidationException {

        Long customerId;
        Integer month, year;

        try {
            customerId = Long.valueOf(customerIdStr);
            month = Integer.valueOf(monthStr);
            year = Integer.valueOf(yearStr);
        }catch(NumberFormatException e){
            throw new InputValidationException(e.getMessage());
        }

        return Response.ok(
                PhoneCallDtoJaxb.from(
                telcoService.getCallsbyMonth(
                        customerId,
                        month,
                        year
                ), ui.getBaseUri(), this.getClass(), MediaType.APPLICATION_XML)
        ).build();
    }

    @POST
    @Path("/changestatus")
    public void changeStatus(@NotNull @QueryParam("customerId") String customerIdStr,
                             @NotNull @QueryParam("month") String monthStr,
                             @NotNull @QueryParam("year") String yearStr,
                             @NotNull @QueryParam("newStatus") String newStatusStr)
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
}
