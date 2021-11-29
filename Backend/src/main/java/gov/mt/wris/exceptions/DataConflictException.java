package gov.mt.wris.exceptions;

public class DataConflictException extends RuntimeException {
    
    private static final long serialVersionUID = 164243L;

    public DataConflictException(String errorMessage) {
        super(errorMessage);
    }
}
