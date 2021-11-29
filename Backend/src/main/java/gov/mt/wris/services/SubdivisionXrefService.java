package gov.mt.wris.services;

import gov.mt.wris.dtos.SubdivisionCreationDto;
import gov.mt.wris.dtos.SubdivisionDto;

import java.math.BigDecimal;

public interface SubdivisionXrefService {

    public SubdivisionDto createSubdivisionForPlaceOfUse(BigDecimal placeId, BigDecimal purposeId, SubdivisionCreationDto subdivisionCreationDto);
    public SubdivisionDto updateSubdivisionForPlaceOfUse(BigDecimal purposeId, BigDecimal placeId, String code, SubdivisionCreationDto subdivisionCreationDto);
    public void deleteSubdivisionFromPlaceOfUse(BigDecimal purposeId, BigDecimal placeId, String code);
    public Integer PlaceOfUseCopyPods(BigDecimal purposeId);

}
