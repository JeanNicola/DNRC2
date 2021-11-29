package gov.mt.wris.services;

import gov.mt.wris.dtos.DitchCreationDto;
import gov.mt.wris.dtos.DitchDto;
import gov.mt.wris.dtos.DitchPageDto;
import gov.mt.wris.dtos.DitchSortColumn;
import gov.mt.wris.dtos.SortDirection;

public interface DitchService {
    public DitchPageDto searchDitches(
        int pagenumber,
        int pagesize,
        DitchSortColumn sortColumn,
        SortDirection sortDirection,
        String name);

    public DitchDto createDitch(DitchCreationDto dto);
}
