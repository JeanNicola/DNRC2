package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SourceCreationDto;
import gov.mt.wris.dtos.SourceDto;
import gov.mt.wris.dtos.SourcePageDto;

public interface SourceService {
    public SourcePageDto searchSources(int pagenumber,
        int pagesize,
        SortDirection sortDirection,
        String sourceName);

    public SourceDto createSource(SourceCreationDto creationDto);
}
