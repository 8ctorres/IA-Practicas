package es.udc.rs.telco.jaxrs.dto;

import es.udc.rs.telco.model.customer.Customer;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "customer")
@XmlType(name="customerJaxbType", propOrder = {"name", "dni", "address", "phoneNumber", "link"})
public class CustomerDtoJaxb {
    @XmlAttribute(name = "customerId", required = false)
    private Long customerId;
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String dni;
    @XmlElement(required = false)
    private String address;
    @XmlElement(required = false)
    private String phoneNumber;
    @XmlElement(name = "link", namespace = "http://www.w3.org/2005/Atom")
    private AtomLinkDtoJaxb link;

    public CustomerDtoJaxb(){}

    public CustomerDtoJaxb(Long customerId, String name, String dni,
                           String address, String phoneNumber, AtomLinkDtoJaxb link) {
        this.customerId = customerId;
        this.name = name;
        this.dni = dni;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.link = link;
    }

    @Schema(description = "ID del cliente", allowableValues =  {}, required=true)
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Schema(description = "Nombre del cliente", allowableValues =  {}, required=true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Schema(description = "DNI del cliente", allowableValues =  {}, required=true)
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    @Schema(description = "Dirección del cliente", allowableValues =  {}, required=true)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Schema(description = "Número de teléfono del cliente", allowableValues =  {}, required=true)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AtomLinkDtoJaxb getLink() {
        return link;
    }

    public void setLink(AtomLinkDtoJaxb link) {
        this.link = link;
    }

    public static CustomerDtoJaxb from(Customer customer, URI baseUri, Class<?> resourceClass,
                                       String mediaType){
        //Construye la URI del Link añadiendole el recurso y el customerId a la ruta
        URI linkUri = UriBuilder.fromUri(baseUri).
                path(UriBuilder.fromResource(resourceClass).build().toString()).
                path(customer.getCustomerId().toString()).build();

        return new CustomerDtoJaxb(
                customer.getCustomerId(),
                customer.getName(),
                customer.getDni(),
                customer.getAddress(),
                customer.getPhoneNumber(),
                new AtomLinkDtoJaxb(linkUri, "self", mediaType, "Self Link")
        );
    }

    public Customer toModel(){
        return new Customer(
                this.getName(),
                this.getDni(),
                this.getAddress(),
                this.getPhoneNumber()
        );
    }

    public static List<CustomerDtoJaxb> from(Collection<Customer> customerList, URI baseUri,
                                             Class<?> resourceClass, String mediaType){
        return customerList.stream().map((c) ->
                CustomerDtoJaxb.from(c, baseUri, resourceClass, mediaType)).collect(Collectors.toList());
    }
}
