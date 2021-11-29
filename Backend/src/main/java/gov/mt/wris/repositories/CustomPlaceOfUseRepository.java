package gov.mt.wris.repositories;

import java.math.BigDecimal;

public interface CustomPlaceOfUseRepository {

    public BigDecimal getNextPlaceOfUseId(BigDecimal purposeId);

    public Integer reNumberPlaceOfUse(String sortCode, BigDecimal waterRightId,  BigDecimal versionId);

    public Integer replicatePodsPlus(BigDecimal purposeId);

}
