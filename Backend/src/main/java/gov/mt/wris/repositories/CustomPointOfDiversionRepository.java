package gov.mt.wris.repositories;

import java.math.BigDecimal;

public interface CustomPointOfDiversionRepository {
    public int renumberPODs(String type,
        BigDecimal waterRightId,
        BigDecimal version);
}
