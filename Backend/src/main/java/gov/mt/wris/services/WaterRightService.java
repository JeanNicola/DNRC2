package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

public interface WaterRightService {
    public WaterRightPageDto searchWaterRights(int pagenumber,
        int pagesize,
        WaterRightSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String ext,
        String typeCode,
        String statusCode,
        String subBasin,
        String waterReservationId,
        String conservationDistrictNumber,
        Boolean countActiveChangeAuthorizationVersions);

    public WaterRightVersionSearchPageDto searchWaterRightsByVersions(int pagenumber,
        int pagesize,
        WaterRightVersionSearchSortColumn sortColumn,
        DescSortDirection sortDirection,
        String waterRightNumber,
        String version,
        String versionType);

    public WaterRightDto createWaterRight(WaterRightCreationDto dto);

    public WaterRightViewDto getWaterRight(Long waterRightId);

    public WaterRightViewDto updateWaterRight(Long waterRightId, WaterRightUpdateDto updateDto);

    public WaterRightUpdateDividedOwnershipDto updateWaterRightDividedOwnership(Long ownershipUpdateId,Long waterRightId, WaterRightUpdateDividedOwnershipDto updateDto);

    public ChildRightPageDto getChildRights(int pageNumber,
        int pageSize,
        ChildRightSortColumn sortDTOColumn,
        DescSortDirection sortDirection,
        Long waterRightId
    );

    public void deleteWaterRight(Long waterRightId);
}