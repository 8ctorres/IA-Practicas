package es.udc.rs.telco.jaxrs.dto;

import es.udc.rs.telco.model.phonecall.PhoneCall;
import es.udc.rs.telco.model.phonecall.PhoneCallStatus;
import es.udc.rs.telco.model.phonecall.PhoneCallType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "phoneCall")
@XmlType(name = "phoneCallType", propOrder = {"phoneCallId", "customerId", "startDate",
        "duration", "destinationNumber", "phoneCallType", "phoneCallStatus"})
public class PhoneCallDto {
    @XmlAttribute(name = "phoneCallId", required = true)
    private Long phoneCallId;
    @XmlAttribute(name = "customerId",required = true)
    private Long customerId;
    @XmlElement(required = true)
    private LocalDateTime startDate;
    @XmlElement(required = true)
    private Long duration;
    @XmlElement(required = true)
    private String destinationNumber;
    @XmlElement(required = true)
    private PhoneCallType phoneCallType;
    @XmlElement(required = true)
    private PhoneCallStatus phoneCallStatus;

    public PhoneCallDto(Long phoneCallId, Long customerId, LocalDateTime startDate,
                        Long duration, String destinationNumber, PhoneCallType phoneCallType,
                        PhoneCallStatus phoneCallStatus) {
        this.phoneCallId = phoneCallId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.duration = duration;
        this.destinationNumber = destinationNumber;
        this.phoneCallType = phoneCallType;
        this.phoneCallStatus = phoneCallStatus;
    }

    public Long getPhoneCallId() {
        return phoneCallId;
    }

    public void setPhoneCallId(Long phoneCallId) {
        this.phoneCallId = phoneCallId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    public PhoneCallType getPhoneCallType() {
        return phoneCallType;
    }

    public void setPhoneCallType(PhoneCallType phoneCallType) {
        this.phoneCallType = phoneCallType;
    }

    public PhoneCallStatus getPhoneCallStatus() {
        return phoneCallStatus;
    }

    public void setPhoneCallStatus(PhoneCallStatus phoneCallStatus) {
        this.phoneCallStatus = phoneCallStatus;
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

    public PhoneCall toModel(PhoneCallDto call){
        return new PhoneCall(
                call.getCustomerId(),
                call.getStartDate(),
                call.getDuration(),
                call.getDestinationNumber(),
                call.getPhoneCallType());
    }

    public static PhoneCallDto from(PhoneCall call){
        return new PhoneCallDto(
                call.getPhoneCallId(),
                call.getCustomerId(),
                call.getStartDate(),
                call.getDuration(),
                call.getDestinationNumber(),
                call.getPhoneCallType(),
                call.getPhoneCallStatus());
    }

    public static List<PhoneCallDto> from(Collection<PhoneCall> phoneCalls){
        return phoneCalls.stream().map((c) -> PhoneCallDto.from(c)).collect(Collectors.toList());
    }
}
