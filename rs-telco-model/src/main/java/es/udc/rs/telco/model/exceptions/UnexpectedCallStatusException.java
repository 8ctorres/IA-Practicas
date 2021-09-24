package es.udc.rs.telco.model.exceptions;

public class UnexpectedCallStatusException extends Exception{
    public UnexpectedCallStatusException(String message){
        super(message);
    }
}
