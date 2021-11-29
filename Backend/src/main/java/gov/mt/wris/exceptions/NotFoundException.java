package gov.mt.wris.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private static final long serialVersionUID = 123555L;

    private String message;

    public NotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public NotFoundException(String message, Exception throwable) {
        super(message, throwable);
        this.message = message;
    }
    
}