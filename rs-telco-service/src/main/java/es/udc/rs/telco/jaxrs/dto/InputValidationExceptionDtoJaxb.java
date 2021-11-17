package es.udc.rs.telco.jaxrs.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name="inputValidationException")
@XmlType(name="inputValidationExceptionType")
public class InputValidationExceptionDtoJaxb {

    @XmlAttribute(required = true)
    private String errorType;
    @XmlElement(required = true)
    private String message;

    public InputValidationExceptionDtoJaxb(String message) {
        this.errorType = "InputValidation";
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
        return "InputValidationExceptionDtoJaxb{" +
                "errorType='" + errorType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}
