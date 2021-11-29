package gov.mt.wris.services;

import gov.mt.wris.dtos.PlaceOfUseCreationDto;
import gov.mt.wris.dtos.PlaceOfUseDto;
import gov.mt.wris.dtos.PlacesOfUsePageDto;
import gov.mt.wris.dtos.PlacesOfUseSortColumn;
import gov.mt.wris.dtos.SortDirection;

import java.math.BigDecimal;

public interface RetiredPlacesOfUseService {

    public PlacesOfUsePageDto getRetiredPlacesOfUse(Integer pageNumber,
                                                   Integer pageSize,
                                                   PlacesOfUseSortColumn sortColumn, SortDirection sortDirection, BigDecimal purposeId);

    public PlaceOfUseDto createRetiredPlaceOfUse(BigDecimal purposeId, PlaceOfUseCreationDto placeOfUseCreationDto, Boolean sort);

    public PlaceOfUseDto updateRetiredPlaceOfUse(BigDecimal purposeId, BigDecimal retiredPlaceId, PlaceOfUseCreationDto placeOfUseCreationDto);

    public void deleteRetiredPlaceOfUse(BigDecimal purposeId, BigDecimal retiredPlaceId);
}
