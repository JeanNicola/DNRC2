package gov.mt.wris.services;

import java.math.BigDecimal;
import java.util.Map;

public interface InquiryService {

    public Map<String, Boolean> isUneditable(BigDecimal waterRightId, BigDecimal versionId, String versionType);

}
