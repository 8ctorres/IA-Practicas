package es.udc.rs.telco.client.service.rest;

import es.udc.rs.telco.client.service.rest.dto.CustomerType;
import es.udc.rs.telco.model.customer.Customer;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDto {

    private Long customerId;
    private String name;
    private String dni;
    private String address;
    private LocalDateTime creationDate;
    private String phoneNumber;

    public CustomerDto(Long id, String name, String dni, String address, String phoneNumber) {
        this.customerId =id;
        this.name = name;
        this.dni = dni;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CustomerType toDtoJaxb(){
        CustomerType customerJaxb = new CustomerType();
        customerJaxb.setCustomerId(this.getCustomerId());
        customerJaxb.setName(this.getName());
        customerJaxb.setDni(this.getDni());
        customerJaxb.setAddress(this.getAddress());
        customerJaxb.setPhoneNumber(this.getPhoneNumber());
        return customerJaxb;
    }

    public static CustomerDto from(CustomerType customer){
        return new CustomerDto(
                customer.getCustomerId(),
                customer.getName(),
                customer.getDni(),
                customer.getAddress(),
                customer.getPhoneNumber()
        );
    }

    public static List<CustomerDto> from(Collection<CustomerType> customerList){
        return customerList.stream().map((c) -> CustomerDto.from(c)).collect(Collectors.toList());
    }
}
