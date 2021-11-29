package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.ZipCodeDto;
import gov.mt.wris.dtos.ZipCodeSortColumn;

public interface CustomZipCodeRepository {
    public Page<ZipCodeDto> getZipCodes(Pageable pageable, ZipCodeSortColumn sortColumn, SortDirection sortDirection, String zipCode, String cityName, String stateCode);
}
