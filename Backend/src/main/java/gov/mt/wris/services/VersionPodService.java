package gov.mt.wris.services;

import gov.mt.wris.dtos.AllPodsDto;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.EnforcementSortColumn;
import gov.mt.wris.dtos.FlowRateSummaryDto;
import gov.mt.wris.dtos.FlowRateSummaryDtoResults;
import gov.mt.wris.dtos.PeriodOfDiversionDto;
import gov.mt.wris.dtos.PeriodOfDiversionPageDto;
import gov.mt.wris.dtos.PeriodOfDiversionSortColumn;
import gov.mt.wris.dtos.PodAddressUpdateDto;
import gov.mt.wris.dtos.PodCopyDto;
import gov.mt.wris.dtos.PodCreationDto;
import gov.mt.wris.dtos.PodDetailsDto;
import gov.mt.wris.dtos.PodDetailsUpdateDto;
import gov.mt.wris.dtos.PodDto;
import gov.mt.wris.dtos.PodEnforcementDto;
import gov.mt.wris.dtos.PodEnforcementsPageDto;
import gov.mt.wris.dtos.PodSourceUpdateDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubdivisionUpdateDto;
import gov.mt.wris.dtos.VersionPodPageDto;
import gov.mt.wris.dtos.VersionPodSortColumn;
import gov.mt.wris.dtos.WellDataUpdateDto;

public interface VersionPodService {
    public AllPodsDto getAllPods(Long waterRightId, Long versionNumber);

    public VersionPodPageDto getVersionPods(Long waterRightId,
        Long versionNumber,
        int pagenumber,
        int pagesize,
        VersionPodSortColumn sortColumn,
        SortDirection sortDirection);

    public PodDto createVersionPod(Long waterRightId,
        Long versionNumber,
        PodCreationDto creationDto);

    public PodDto copyVersionPod(Long waterRightId, Long versionNumber, Long podId, PodCopyDto copyDto);

    public void deletePod(Long waterRightId,
        Long versionNumber,
        Long podId);

    public FlowRateSummaryDto getFlowRateSummary(Long waterRightId, Long versionNumber);

    public FlowRateSummaryDto updateFlowRateSummary(Long waterRightId,
        Long versionNumber,
        FlowRateSummaryDtoResults updateDto);

    public PodDetailsDto getPodDetails(Long waterRightId, Long versionNumber, Long podId);

    public void updatePodDetails(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodDetailsUpdateDto updateDto);

    public void updatePodAddress(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodAddressUpdateDto updateDto);

    public void updateSubdivision(Long waterRightId,
        Long versionNumber,
        Long podId,
        SubdivisionUpdateDto updateDto);

    public void updateWellData(Long waterRightId,
        Long versionNumber,
        Long podId,
        WellDataUpdateDto updateDto);

    public void updatePodSource(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodSourceUpdateDto updateDto);

    public PeriodOfDiversionPageDto getPeriodOfDiversions(Long waterRightId,
        Long versionNumber,
        Long podId,
        int pagenumber,
        int pagesize,
        PeriodOfDiversionSortColumn sortColumn,
        DescSortDirection sortDirection);

    public PeriodOfDiversionDto addPeriodOfDiversion(Long waterRightId,
        Long versionNumber,
        Long podId,
        PeriodOfDiversionDto creationDto);

    public PeriodOfDiversionDto updatePeriodOfDiversion(Long waterRightId,
        Long versionNumber,
        Long podId,
        Long periodId,
        PeriodOfDiversionDto updateDto);

    public void deletePeriodOfDiversion(Long waterRightid,
        Long versionNumber,
        Long podId,
        Long periodId);

    public PodEnforcementsPageDto getEnforcements(Long waterRightId,
        Long versionNumber,
        Long podId,
        int pagenumber,
        int pagesize,
        EnforcementSortColumn sortColumn,
        SortDirection sortDirection);

    public PodEnforcementDto addEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodEnforcementDto creationDto);

    public PodEnforcementDto updateEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        String areaId,
        String enforcementNumber,
        PodEnforcementDto updateDto);

    public void deleteEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        String areaId,
        String enforcementNumber);
}
