package es.udc.rs.telco.model.exceptions;

public class CallNotPendingException extends Exception{
    public CallNotPendingException (String message){
        super(message);
    }
}
