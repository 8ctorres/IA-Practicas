package es.udc.rs.telco.client.service;

import es.udc.rs.telco.client.service.rest.CustomerDto;
import es.udc.rs.telco.client.service.rest.PhoneCallDto;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallStatus;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallType;
import es.udc.rs.telco.client.service.rest.exceptions.CustomerHasCallsClientException;
import es.udc.rs.telco.client.service.rest.exceptions.MonthNotClosedClientException;
import es.udc.rs.telco.client.service.rest.exceptions.UnexpectedCallStatusClientException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientTelcoService {

    CustomerDto addCustomer(CustomerDto newCustomer) throws InputValidationException;

    PhoneCallDto addCall(PhoneCallDto newCall) throws InputValidationException, InstanceNotFoundException;

    void removeCustomer(Long idCust) throws InputValidationException, InstanceNotFoundException, CustomerHasCallsClientException;

    List<PhoneCallDto> getCalls(Long customerId, LocalDateTime from, LocalDateTime to, PhoneCallType type) throws InputValidationException, InstanceNotFoundException, MonthNotClosedClientException;

    void changeCallStatus(Long customerId, Integer month, Integer year, PhoneCallStatus newstatus) throws InputValidationException, InstanceNotFoundException, MonthNotClosedClientException, UnexpectedCallStatusClientException;
}
