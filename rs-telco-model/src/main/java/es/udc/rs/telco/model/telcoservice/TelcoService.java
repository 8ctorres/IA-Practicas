package es.udc.rs.telco.model.telcoservice;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.phonecall.PhoneCall;
import es.udc.rs.telco.model.phonecall.PhoneCallType;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import es.udc.rs.telco.model.customer.Customer;
import es.udc.rs.telco.model.exceptions.CustomerHasCallsException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface TelcoService {
    public Customer addCustomer(String name, String DNI, String address, String phone) throws InputValidationException;

    public void updateCustomer(Long id, String name, String DNI, String address) throws InstanceNotFoundException, InputValidationException;

    public Customer findCustomerByDNI(String dni) throws InstanceNotFoundException;

    public List<Customer> getCustomersbyName(String name, Integer start_position, Integer amount);

    public PhoneCall AddCall(Long customerId, LocalDateTime startDate, Long duration,
                             PhoneCallType tipo, String destinationNumber);
    public void removeCustomer(Long id) throws InstanceNotFoundException, CustomerHasCallsException;

    public Customer findCustomerById(Long id) throws InstanceNotFoundException;
}
