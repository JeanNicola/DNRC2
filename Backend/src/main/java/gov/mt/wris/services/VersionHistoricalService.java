package gov.mt.wris.services;

import gov.mt.wris.dtos.VersionHistoricalChangesDto;
import gov.mt.wris.dtos.VersionHistoricalClaimFilingDto;
import gov.mt.wris.dtos.VersionHistoricalCourthouseFilingDto;
import gov.mt.wris.dtos.VersionHistoricalDto;
import gov.mt.wris.dtos.VersionHistoricalPriorityDateDto;
import gov.mt.wris.dtos.VersionHistoricalWithReferencesDto;

public interface VersionHistoricalService {
    public VersionHistoricalWithReferencesDto getHistorical(
        Long waterRightId,
        Long versionId
    );

    public VersionHistoricalDto updatePriorityDate(
        Long waterRightId,
        Long versionId,
        VersionHistoricalPriorityDateDto update
    );

    public VersionHistoricalDto updateClaimFiling(
        Long waterRightId,
        Long versionId,
        VersionHistoricalClaimFilingDto update
    );

    public VersionHistoricalDto updateCourthouseFiling(
        Long waterRightId,
        Long versionId,
        VersionHistoricalCourthouseFilingDto update
    );

    public VersionHistoricalDto updateChanges(
        Long waterRightId,
        Long versionId,
        VersionHistoricalChangesDto update
    );
}
