package es.udc.rs.telco.client.service.rest;

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


    public CustomerDto(Long id, String name, String dni, String address, String phoneNumber, LocalDateTime date) {
        this.customerId =id;
        this.name = name;
        this.dni = dni;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.creationDate = date;
    }

    public CustomerDto(CustomerDto c) {
        this.customerId =c.getCustomerId();
        this.name = c.getName();
        this.dni = c.getDni();
        this.address = c.getAddress();
        this.phoneNumber = c.getPhoneNumber();
        this.creationDate = c.getCreationDate();
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Customer toModel(){
        return new Customer(
                this.getName(),
                this.getDni(),
                this.getAddress(),
                this.getPhoneNumber()
        );
    }

    public static CustomerDto from(Customer customer){
        return new CustomerDto(
                customer.getCustomerId(),
                customer.getName(),
                customer.getDni(),
                customer.getAddress(),
                customer.getPhoneNumber(),
                customer.getCreationDate()
        );
    }

    public static List<CustomerDto> from(Collection<Customer> customerList){
        return customerList.stream().map((c) -> CustomerDto.from(c)).collect(Collectors.toList());
    }
}
