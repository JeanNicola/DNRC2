package gov.mt.wris.exceptions;

public class DataUsedElsewhereException extends RuntimeException {

    private static final long serialVersionUID = 1234567L;

    public DataUsedElsewhereException(String errorMessage) {
        super(errorMessage);
    }
}
