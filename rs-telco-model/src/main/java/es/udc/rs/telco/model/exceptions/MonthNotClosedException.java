package es.udc.rs.telco.model.exceptions;

import java.time.Month;

public class MonthNotClosedException extends Exception {
    public MonthNotClosedException(String message){
        super(message);
    }
}
