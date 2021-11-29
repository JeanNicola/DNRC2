package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubdivisionCreationDto;
import gov.mt.wris.dtos.SubdivisionDto;
import gov.mt.wris.dtos.SubdivisionPageDto;
import gov.mt.wris.dtos.SubdivisionSortColumn;

import java.math.BigDecimal;

public interface RetiredPouSubdivisionXrefService {

    public SubdivisionPageDto getSubdivisionsForRetPou(BigDecimal retiredPlaceId, BigDecimal purposeId, Integer pageNumber, Integer pageSize, SubdivisionSortColumn sortColumn, SortDirection sortDirection);

    public SubdivisionDto createSubdivisionForRetPou(BigDecimal retiredPlaceId, BigDecimal purposeId, SubdivisionCreationDto subdivisionCreationDto);

    public SubdivisionDto updateSubdivisionForRetPou(BigDecimal purposeId, BigDecimal retiredPlaceId, String code, SubdivisionCreationDto subdivisionCreationDto);

    public void deleteSubdivisionFromRetPou(BigDecimal purposeId, BigDecimal retiredPlaceId, String code);

    public Integer retiredPousCopyPods(BigDecimal purposeId);
}
