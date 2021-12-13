package es.udc.rs.telco.jaxrs.dto;

import es.udc.rs.telco.model.phonecall.PhoneCall;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;
import es.udc.rs.telco.model.phonecall.PhoneCallType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "phoneCall")
@XmlType(name = "phoneCallJaxbType", propOrder = {"startDate",
        "duration", "destinationNumber", "phoneCallType", "phoneCallStatus", "selfLink", "customerLink"})
public class PhoneCallDtoJaxb {
    @XmlAttribute(name = "phoneCallId", required = false)
    private Long phoneCallId;
    @XmlAttribute(name = "customerId",required = true)
    private Long customerId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    private LocalDateTime startDate;
    @XmlElement(required = true)
    private Long duration;
    @XmlElement(required = true)
    private String destinationNumber;
    @XmlElement(required = true)
    private PhoneCallType phoneCallType;
    @XmlElement(required = false)
    private PhoneCallStatus phoneCallStatus;
    @XmlElement(name = "selfLink", namespace = "http://www.w3.org/2005/Atom")
    private AtomLinkDtoJaxb selfLink;
    @XmlElement(name = "customerLink", namespace = "http://www.w3.org/2005/Atom")
    private AtomLinkDtoJaxb customerLink;

    public PhoneCallDtoJaxb(){}

    public PhoneCallDtoJaxb(Long phoneCallId, Long customerId, LocalDateTime startDate,
                            Long duration, String destinationNumber, PhoneCallType phoneCallType,
                            PhoneCallStatus phoneCallStatus, AtomLinkDtoJaxb selfLink, AtomLinkDtoJaxb customerLink) {
        this.phoneCallId = phoneCallId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.duration = duration;
        this.destinationNumber = destinationNumber;
        this.phoneCallType = phoneCallType;
        this.phoneCallStatus = phoneCallStatus;
        this.selfLink = selfLink;
        this.customerLink = customerLink;
    }

    public Long getPhoneCallId() {
        return phoneCallId;
    }

    public void setPhoneCallId(Long phoneCallId) {
        this.phoneCallId = phoneCallId;
    }

    @Schema(description = "ID del cliente", allowableValues =  {}, required=true)
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    //Almacenamos el tiempo en formato epoch para que JAX-B no tenga problemas a la hora de serializarlo

    @Schema(description = "Fecha y hora de inicio de la llamada", allowableValues =  {}, required=true)
    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @Schema(description = "Duración de la llamada en segundos", allowableValues =  {}, required=true)
    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Schema(description = "Número de teléfono del destinatario de la llamada", allowableValues =  {}, required=true)
    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    @Schema(description = "Tipo de llamada (local, nacional, internacional)", allowableValues =  {}, required=true)
    public PhoneCallType getPhoneCallType() {
        return phoneCallType;
    }

    public void setPhoneCallType(PhoneCallType phoneCallType) {
        this.phoneCallType = phoneCallType;
    }

    @Schema(description = "Estado de la llamada (pendiente, facturada, pagada)", allowableValues =  {}, required=true)
    public PhoneCallStatus getPhoneCallStatus() {
        return phoneCallStatus;
    }

    public void setPhoneCallStatus(PhoneCallStatus phoneCallStatus) {
        this.phoneCallStatus = phoneCallStatus;
    }
    
    public AtomLinkDtoJaxb getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(AtomLinkDtoJaxb selfLink) {
        this.selfLink = selfLink;
    }

    public AtomLinkDtoJaxb getCustomerLink() {
        return customerLink;
    }

    public void setCustomerLink(AtomLinkDtoJaxb customerLink) {
        this.customerLink = customerLink;
    }

    @Override
    public String toString() {
        return "PhoneCallDto{" +
                "phoneCallId=" + phoneCallId +
                ", customerId=" + customerId +
                ", startDate=" + startDate +
                ", duration=" + duration +
                ", destinationNumber='" + destinationNumber + '\'' +
                ", phoneCallType=" + phoneCallType +
                ", phoneCallStatus=" + phoneCallStatus +
                '}';
    }

    public PhoneCall toModel(PhoneCallDtoJaxb call){
        return new PhoneCall(
                call.getCustomerId(),
                call.getStartDate(),
                call.getDuration(),
                call.getDestinationNumber(),
                call.getPhoneCallType());
    }

    public static PhoneCallDtoJaxb from(PhoneCall call, URI baseUri, Class<?> resourceClass,
                                        String mediaType, Class<?> customerResourceClass){
        //Construye la URI del Link añadiendole el recurso y el phoneCallId a la ruta
        URI linkUri = UriBuilder.fromUri(baseUri).
                path(UriBuilder.fromResource(resourceClass).build().toString()).
                path(call.getPhoneCallId().toString()).build();
        URI customerLinkUri = UriBuilder.fromUri(baseUri).
                path(UriBuilder.fromResource(customerResourceClass).build().toString()).
                path(call.getCustomerId().toString()).build();

        return new PhoneCallDtoJaxb(
                call.getPhoneCallId(),
                call.getCustomerId(),
                call.getStartDate(),
                call.getDuration(),
                call.getDestinationNumber(),
                call.getPhoneCallType(),
                call.getPhoneCallStatus(),
                new AtomLinkDtoJaxb(linkUri, "self", mediaType, "Self link"),
                new AtomLinkDtoJaxb(customerLinkUri, "customer", mediaType, "Customer Link"));
    }

    public static List<PhoneCallDtoJaxb> from(Collection<PhoneCall> phoneCalls, URI baseUri,
                                              Class<?> resourceClass, String mediaType, Class<?> customerResourceClass){
        return phoneCalls.stream().map((c) ->
                PhoneCallDtoJaxb.from(c, baseUri, resourceClass, mediaType, customerResourceClass)).collect(Collectors.toList());
    }
}
