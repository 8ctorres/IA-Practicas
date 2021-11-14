package es.udc.rs.telco.model.customer;

import es.udc.rs.telco.model.phonecall.PhoneCall;

import java.time.LocalDateTime;
import java.util.Objects;

public class Customer {

    private Long customerId;
    private String name;
    private String dni;
    private String address;
    private LocalDateTime creationDate;
    private String phoneNumber;
    

	public Customer(String name, String dni, String address, String phoneNumber) {
		super();
		this.name = name;
		this.dni = dni;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

	public Customer(Customer c) {
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

	@Override
	public String toString() {
		return "Customer{" +
				"customerId=" + customerId +
				", name='" + name + '\'' +
				", dni='" + dni + '\'' +
				", address='" + address + '\'' +
				", creationDate=" + creationDate +
				", phoneNumber='" + phoneNumber + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Customer customer = (Customer) o;
		return this.customerId.equals(customer.customerId) &&
				Objects.equals(name, customer.name) &&
				Objects.equals(dni, customer.dni) &&
				Objects.equals(address, customer.address) &&
				Objects.equals(creationDate, customer.creationDate) &&
				Objects.equals(phoneNumber, customer.phoneNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(customerId, name, dni, address, creationDate, phoneNumber);
	}
}