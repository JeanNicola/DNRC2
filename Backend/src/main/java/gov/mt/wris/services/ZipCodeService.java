package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.ZipCodeDto;
import gov.mt.wris.dtos.ZipCodePageDto;
import gov.mt.wris.dtos.ZipCodeSortColumn;

public interface ZipCodeService {
    public ZipCodePageDto getZipCodes(int pagenumber, int pagesize, ZipCodeSortColumn sortDTOColumn, SortDirection sortDirection, String zipCode, String cityName, String stateCode);

    public ZipCodeDto createZipCode(ZipCodeDto dto);

    public void deleteZipCode(Long zipCodeId);

    public ZipCodeDto changeZipCode(Long zipCodeId, ZipCodeDto dto);

    public ZipCodeDto toUpperCase(ZipCodeDto zipDto);
}
