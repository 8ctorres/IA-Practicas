package es.udc.rs.telco.client.service.dto;

import es.udc.rs.telco.client.service.rest.dto.ObjectFactory;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallDtoJaxb;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallStatus;
import es.udc.rs.telco.client.service.rest.dto.PhoneCallType;
import jakarta.xml.bind.JAXBElement;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PhoneCallDto {

    private Long phoneCallId;
    private Long customerId;
    private LocalDateTime startDate;
    private Long duration;
    private String destinationNumber;
    private PhoneCallType phoneCallType;
    private PhoneCallStatus phoneCallStatus;

    public PhoneCallDto(Long phoneCallId, Long customerId,
                        LocalDateTime startDate, Long duration,
                        String destinationNumber, PhoneCallType phoneCallType,
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

    public static PhoneCallDto from(PhoneCallDtoJaxb phoneCallDtoJaxb){
        return new PhoneCallDto(
                phoneCallDtoJaxb.getPhoneCallId(),
                phoneCallDtoJaxb.getCustomerId(),
                phoneCallDtoJaxb.getStartDate(),
                phoneCallDtoJaxb.getDuration(),
                phoneCallDtoJaxb.getDestinationNumber(),
                phoneCallDtoJaxb.getPhoneCallType(),
                phoneCallDtoJaxb.getPhoneCallStatus()
        );
    }

    public static List<PhoneCallDto> from(Collection<PhoneCallDtoJaxb> phoneCallDtoJaxbs){
        return phoneCallDtoJaxbs.stream().map((x) -> PhoneCallDto.from(x)).collect(Collectors.toList());
    }

    public JAXBElement<PhoneCallDtoJaxb> toDtoJaxb(){
       PhoneCallDtoJaxb phoneCallDtoJaxb = new PhoneCallDtoJaxb();
       phoneCallDtoJaxb.setPhoneCallId(this.getPhoneCallId());
       phoneCallDtoJaxb.setCustomerId(this.getCustomerId());phoneCallDtoJaxb.setStartDate(this.getStartDate());
       phoneCallDtoJaxb.setDuration(this.getDuration());
       phoneCallDtoJaxb.setDestinationNumber(this.getDestinationNumber());
       phoneCallDtoJaxb.setPhoneCallType(this.getPhoneCallType());
       phoneCallDtoJaxb.setPhoneCallStatus(this.getPhoneCallStatus());

       return new ObjectFactory().createPhoneCall(phoneCallDtoJaxb);
    }

    @Override
    public String toString() {
        return "PhoneCall{" +
                "phoneCallId=" + phoneCallId +
                ", customerId=" + customerId +
                ", startDate=" + startDate +
                ", duration=" + duration +
                ", destinationNumber='" + destinationNumber + '\'' +
                ", phoneCallType=" + phoneCallType +
                ", phoneCallStatus=" + phoneCallStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneCallDto that = (PhoneCallDto) o;
        return Objects.equals(phoneCallId, that.phoneCallId) && customerId.equals(that.customerId) && startDate.equals(that.startDate) && duration.equals(that.duration) && destinationNumber.equals(that.destinationNumber) && phoneCallType == that.phoneCallType && phoneCallStatus == that.phoneCallStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneCallId, customerId, startDate, duration, destinationNumber, phoneCallType, phoneCallStatus);
    }
}
