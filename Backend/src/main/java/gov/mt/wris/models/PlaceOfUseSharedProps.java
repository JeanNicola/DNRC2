package gov.mt.wris.models;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PlaceOfUseSharedProps {
    private BigDecimal purposeId;
    private BigDecimal acreage;
    private String elementOrigin;
    private Reference elementOriginReference;
    private BigDecimal legalLandDescriptionId;
    public LegalLandDescription legalLandDescription;
    private String modified;
    private Reference modifiedReference;
    private BigDecimal countyId;
    public Purpose purpose;
    public County county;
}
