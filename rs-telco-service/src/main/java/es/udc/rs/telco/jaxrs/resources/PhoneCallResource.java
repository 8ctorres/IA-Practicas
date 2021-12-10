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
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

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
    public Response addPhoneCall(PhoneCallDtoJaxb newCall, @Context UriInfo ui) throws InstanceNotFoundException, InputValidationException {
        PhoneCallDtoJaxb addedCall = PhoneCallDtoJaxb.from(
                telcoService.addCall(
                        newCall.getCustomerId(),
                        newCall.getStartDate(),
                        newCall.getDuration(),
                        newCall.getPhoneCallType(),
                        newCall.getDestinationNumber()
                )
        );

        return Response.created(
                URI.create(
                        ui.getRequestUri().toString() + "/" + addedCall.getPhoneCallId().toString()
                )
        ).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public List<PhoneCallDtoJaxb> getCallsBy(@NotNull @QueryParam("customerId") String customerIdStr,
                                             @DefaultValue("null") @QueryParam("startTime") String startTimeStr,
                                             @DefaultValue("null") @QueryParam("endTime") String endTimeStr,
                                             @DefaultValue("null") @QueryParam("type") String phoneCallTypeStr,
                                             @DefaultValue("null") @QueryParam("startPos") String startPosStr,
                                             @DefaultValue("null") @QueryParam("amount") String amountStr,
                                             @DefaultValue("null") @QueryParam("month") String monthStr,
                                             @DefaultValue("null") @QueryParam("year") String yearStr
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
            return getCallsbyMonth(customerIdStr, monthStr, yearStr);
        }
        // Si no están AMBOS parámetros mes y año, no puede estar ninguno de ellos
        else{
            if (!(monthStr.equals("null")) || !(yearStr.equals("null"))){
                throw new InputValidationException("Parámetros inválidos");
            }
            // Si no están mes ni año, es una llamada genérica de getCallsByCustomerId
            return getCallsByCustomerId(customerIdStr, startTimeStr, endTimeStr, phoneCallTypeStr,
                    startPosStr, amountStr);
        }
    }


    private List<PhoneCallDtoJaxb> getCallsByCustomerId(String customerIdStr, String startTimeStr, String endTimeStr,
                                                       String phoneCallTypeStr,String startPosStr, String amountStr)
            throws InstanceNotFoundException, InputValidationException {

        Long customerId;
        try {
            customerId = Long.valueOf(customerIdStr);
        }catch(NumberFormatException e){
            throw new InputValidationException(e.getMessage());
        }

        //TODO: Pendiente valorar otras opciones de pasar las fechas
        LocalDateTime startTime = (startTimeStr.equals("null") ? null : LocalDateTime.parse(startPosStr));
        LocalDateTime endTime = (endTimeStr.equals("null") ? null : LocalDateTime.parse(amountStr));

        Integer startPos = (startPosStr.equals("null") ? null : Integer.valueOf(startPosStr));
        Integer amount = (amountStr.equals("null") ? null : Integer.valueOf(amountStr));

        return PhoneCallDtoJaxb.from(
                telcoService.getCallsbyId(
                        customerId,
                        startTime,
                        endTime,
                        phoneCallTypeFromString(phoneCallTypeStr),
                        startPos,
                        amount
                )
        );
    }


    private List<PhoneCallDtoJaxb> getCallsbyMonth(String customerIdStr, String monthStr, String yearStr)
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

        return PhoneCallDtoJaxb.from(
                telcoService.getCallsbyMonth(
                        customerId,
                        month,
                        year
                )
        );
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
