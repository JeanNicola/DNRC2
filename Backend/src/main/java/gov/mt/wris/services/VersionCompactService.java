package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.VersionCompactDto;
import gov.mt.wris.dtos.VersionCompactSortColumn;
import gov.mt.wris.dtos.VersionCompactsPageDto;

public interface VersionCompactService {
    public VersionCompactsPageDto getVersionCompacts(
        int pagenumber,
        int pagesize,
        VersionCompactSortColumn sortColumn,
        SortDirection sortDirection,
        Long waterRightId,
        Long versionNumber);

    public VersionCompactDto createVersionCompact(Long waterRightId, Long versionNumber, VersionCompactDto dto);

    public VersionCompactDto updateVersionCompact(Long waterRightId, Long versionNumber, Long compactId, VersionCompactDto dto);

    public void deleteVersionCompact(Long compactId);
}
