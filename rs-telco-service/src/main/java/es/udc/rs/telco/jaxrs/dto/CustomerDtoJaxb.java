package es.udc.rs.telco.jaxrs.dto;

import es.udc.rs.telco.model.customer.Customer;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "customer")
@XmlType(name="customerType", propOrder = {"name", "dni", "address", "phoneNumber"})
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

    public CustomerDtoJaxb(){}

    public CustomerDtoJaxb(Long customerId, String name, String dni, String address, String phoneNumber) {
        this.customerId = customerId;
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

    public static CustomerDtoJaxb from(Customer customer){
        return new CustomerDtoJaxb(
                customer.getCustomerId(),
                customer.getName(),
                customer.getDni(),
                customer.getAddress(),
                customer.getPhoneNumber()
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

    public static List<CustomerDtoJaxb> from(Collection<Customer> customerList){
        return customerList.stream().map((c) -> CustomerDtoJaxb.from(c)).collect(Collectors.toList());
    }
}