package gov.mt.wris.utils;

import javax.persistence.AttributeConverter;

public class BooleanConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean value) {
        return value == null ? null : value ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String value) {
        return value == null ? null : "Y".equals(value);
    }
}
