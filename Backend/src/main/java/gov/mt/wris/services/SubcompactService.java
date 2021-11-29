package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubcompactPageDto;
import gov.mt.wris.dtos.SubcompactSortColumn;

public interface SubcompactService {
    public SubcompactPageDto searchSubcompacts(int pagenumber,
        int pagesize,
        SubcompactSortColumn sortColumn,
        SortDirection sortDirection,
        String subcompact,
        String compact);
}
