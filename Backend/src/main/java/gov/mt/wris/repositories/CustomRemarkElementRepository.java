package gov.mt.wris.repositories;

public interface CustomRemarkElementRepository {
    public Boolean validateAllowableVariableText(String tableName,
        String columnName,
        String value);
}
