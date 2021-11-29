package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesPageDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesSortColumn;

public interface VersionApplicationService {
    public WaterRightVersionApplicationReferencesPageDto getWaterRightVersionApplicationReferences(int pagenumber, int pagesize, WaterRightVersionApplicationReferencesSortColumn sortColumn, SortDirection sortDirection, Long waterRightId, Long versionNumber);

    public WaterRightVersionApplicationReferencesDto addApplicationReferenceToWaterRightVersion(Long waterRightId, Long versionNumber, Long applicationId);

    public void deleteApplicationReferenceToWaterRightVersion(Long waterRightId, Long versionNumber, Long applicationId);
}
