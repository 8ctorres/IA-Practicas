package es.udc.rs.telco.model.telcoservice;

import es.udc.rs.telco.model.exceptions.CallNotPendingException;
import es.udc.rs.telco.model.exceptions.MonthNotClosedException;
import es.udc.rs.telco.model.phonecall.PhoneCall;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;
import es.udc.rs.telco.model.phonecall.PhoneCallType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface TelcoService {
    public List<PhoneCall> getCallsbyId(Long customerId, LocalDateTime start_time, LocalDateTime end,
                                        PhoneCallType tipo, Integer start_position, Integer amount);

    public Collection<PhoneCall> getCallsbyMonth(Long customerId, int month, int year) throws MonthNotClosedException, CallNotPendingException;

    public void changeCallsStatus(Long customerId, int month, int year, PhoneCallStatus newstatus) throws CallNotPendingException, MonthNotClosedException;

}
