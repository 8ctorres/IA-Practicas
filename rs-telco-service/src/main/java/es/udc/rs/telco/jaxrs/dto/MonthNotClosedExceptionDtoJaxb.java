package es.udc.rs.telco.jaxrs.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name="monthNotClosedException")
@XmlType(name="monthNotClosedExceptionType")
public class MonthNotClosedExceptionDtoJaxb {

    @XmlAttribute(required = true)
    private String errorType;
    @XmlElement(required = true)
    private String message;

    public MonthNotClosedExceptionDtoJaxb(String message) {
        this.errorType = "MonthNotClosed";
        this.message = message;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MonthNotClosedExceptionDtoJaxb{" +
                "errorType='" + errorType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}
