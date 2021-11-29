package gov.mt.wris.models;

import java.math.BigDecimal;

public interface ApplicationCaseSummary {
    BigDecimal getCaseId();
    String getCaseStatusDescription();
    String getCaseTypeDescription();
}
