package gov.mt.wris.services;

import gov.mt.wris.models.County;
import gov.mt.wris.models.LegalLandDescription;

public interface LegalLandService {

    public Long getLegalLandDescriptionId(String description320, String description160, String description80, String description40, Long governmentLot, Long township, String townshipDirection, Long range, String rangeDirection, Long section, Long countyId);

    public String buildLegalLandDescription(LegalLandDescription land, County county);
}
