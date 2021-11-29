package gov.mt.wris.services;

import java.math.BigDecimal;

import gov.mt.wris.dtos.*;
import gov.mt.wris.models.WaterRight;

public interface WaterRightVersionService {
    public ApplicationWaterRightsPageDto getWaterRights(int pagenumber, 
                                                        int pagesize,
                                                        ApplicationWaterRightSortColumn sortColumn,
                                                        DescSortDirection sortDirection,
                                                        Long applicationId);

    public ApplicationWaterRightDto addWaterRight(Long applicationId, ApplicationWaterRightCreationDto newWaterRight);

    public WaterRightVersionPageDto searchWaterRights(int pagenumber, int pagesize, WaterRightVersionSortColumn sortColumn, DescSortDirection sortDirection, String basin, String waterRightNumber, String version);

    public ApplicationWaterRightDto editWaterRight(Long applicationId, Long waterRightId, Long versionIdSeq, ApplicationWaterRightDto dto);

    public boolean canEditWaterRightStatus(String applicationTypeCode, WaterRight water);
    public boolean canEditVersionStatus(String applicationTypeCode, WaterRight water);

    public void deleteApplicationWaterRightVersion(Long applicationId, Long waterRightId, Long versionIdSeq);

    public RelatedRightsPageDto getVersionRelatedRights(int pageNumber, int pageSize, RelatedRightSortColumn sortColumn, SortDirection sortDirection, Long waterRightId, Long versionId);

    public VersionPageDto getVersions(int pagenumber, int pagesize, WaterRightVersionSortColumn sortColumn, SortDirection sortDirection, Long waterRightId, String version, String versionType);

    public VersionDto createWaterRightVersion(Long waterRightId, VersionCreationDto createDto);
    public VersionDto createFirstVersion(Long waterRightId, FirstVersionCreationDto creationDto);

    public void applyVersionStandards(Long waterRightId);

    public VersionDto updateVersionStandards(Long waterRightId, Long versionNumber, VersionUpdateDto updateDto);

    public VersionDto updateWaterRightVersion(Long waterRightId, Long versionNumber, VersionUpdateDto updateDto);

    public void deleteVersion(Long waterRightId, Long version);

    public VersionDetailDto getWaterRightVersionDetail(Long waterRightId, Long versionNumber);

    public WaterRightVersionDecreesPageDto getWaterRightVersionDecrees(
        Long waterRightId,
        Long versionNumber,
        Integer pageNumber,
        Integer pageSize,
        WaterRightVersionDecreeSortColumn sortColumn,
        SortDirection sortDirection
    );

    public VersionVolumeDto getVersionVolume(Long waterRightId, Long versionId);

    public VersionVolumeDto updateVersionVolume(
        Long waterRightId,
        Long versionId,
        VersionVolumeDto update
    );

    public VersionAcreageDto getVersionAcreage(Long waterRightId, Long versionId);

    public VersionAcreageDto updateVersionAcreage(
        Long waterRightId,
        Long versionId,
        VersionAcreageDto update
    );

    public EligibleWaterRightVersionPageDto getEligibleWaterRightVersions(int pagenumber, int pagesize, EligibleWaterRightVersionSortColumn sortColumn, SortDirection sortDirection, String basin, String waterNumber);
}
