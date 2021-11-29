package gov.mt.wris.exceptions;

import java.util.List;

import lombok.Getter;

@Getter
public class FieldValidationException extends RuntimeException {

    private static final long serialVersionUID = 12245678L;

    private String userMessage;
    private String developerMessage;
    private List<String> fields;

    public FieldValidationException(String userMessage, String developerMessage, List<String> fields) {
        super(userMessage);
        this.userMessage = userMessage;
        this.developerMessage = developerMessage;
        this.fields = fields;
    }
}
