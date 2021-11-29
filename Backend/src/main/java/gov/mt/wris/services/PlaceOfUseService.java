package gov.mt.wris.services;

import gov.mt.wris.dtos.PlaceOfUseCreationDto;
import gov.mt.wris.dtos.PlaceOfUseDto;
import gov.mt.wris.dtos.PlacesOfUsePageDto;
import gov.mt.wris.dtos.PlacesOfUseSortColumn;
import gov.mt.wris.dtos.SortDirection;

import java.math.BigDecimal;

public interface PlaceOfUseService {

    PlacesOfUsePageDto getPlacesOfUse(int pagenumber, int pagesize, PlacesOfUseSortColumn sortColumn, SortDirection sortDirection, Long purposeId);

    PlaceOfUseDto createPlaceOfUse(BigDecimal purposeId, PlaceOfUseCreationDto createDto);

    PlaceOfUseDto createPlaceOfUse(BigDecimal purposeId, PlaceOfUseCreationDto placeOfUseCreationDto, Boolean sort);

    PlaceOfUseDto updatePlaceOfUse(BigDecimal purposeId, BigDecimal placeId, PlaceOfUseCreationDto placeOfUseCreationDto);

    void deletePlaceOfUse(BigDecimal purposeId, BigDecimal placeId);

}
