package gov.mt.wris.services;

import gov.mt.wris.dtos.RemarkDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.VersionRemarkCreateDto;
import gov.mt.wris.dtos.VersionRemarksPageDto;
import gov.mt.wris.dtos.VersionRemarksSortColumn;

public interface VersionRemarkService {
    public VersionRemarksPageDto getVersionRemarks(Long waterRightId,
        Long versionNumber,
        int pagenumber,
        int pagesize,
        VersionRemarksSortColumn sortColumn,
        SortDirection sortDirection);

    public RemarkDto createRemark(Long waterRightId,
        Long versionNumber,
        VersionRemarkCreateDto createDto);
}
