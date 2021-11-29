package gov.mt.wris.exceptions;

public class HelpDeskNeededException extends RuntimeException {
    private static final long serialVersionUID = 532424L;

    public HelpDeskNeededException(String errorMessage) {
        super(errorMessage);
    }
}
