package gov.mt.wris.repositories;

import java.math.BigDecimal;

public interface CustomRetiredPlaceOfUseRepository {

    public BigDecimal getNextRetiredPlaceOfUseId(BigDecimal purposeId);

    public Integer replicateRetPods(BigDecimal purposeId);

    public Integer reNumberRetPlaceOfUse(String sortCode, BigDecimal waterRightId, BigDecimal versionId);

}
