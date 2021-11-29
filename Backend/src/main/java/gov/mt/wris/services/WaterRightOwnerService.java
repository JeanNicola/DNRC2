package gov.mt.wris.services;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.WaterRightOwnerDto;
import gov.mt.wris.dtos.WaterRightOwnerPageDto;
import gov.mt.wris.dtos.WaterRightOwnerSortColumn;
import gov.mt.wris.dtos.WaterRightOwnerUpdateDto;
import gov.mt.wris.dtos.WaterRightRepresentativeDto;
import gov.mt.wris.dtos.WaterRightRepresentativePageDto;
import gov.mt.wris.dtos.WaterRightRepresentativeSortColumn;
import gov.mt.wris.dtos.WaterRightRepresentativeUpdateDto;

public interface WaterRightOwnerService {
    public WaterRightOwnerPageDto getWaterRightOwners(int pagenumber,
        int pagesize,
        WaterRightOwnerSortColumn sortColumn,
        DescSortDirection sortDirection,
        Long waterRightId);

    public WaterRightOwnerDto updateWaterRightOwner(Long waterRightId, Long ownerId, Long contactId, WaterRightOwnerUpdateDto updateDto);

    public WaterRightRepresentativePageDto getWaterRightRepresentatives(int pagenumber,
        int pagesize,
        WaterRightRepresentativeSortColumn sortColumn,
        DescSortDirection sortDirection,
        Long waterRightId,
        Long ownerId,
        Long contactId);

    public WaterRightRepresentativeDto addWaterRightRepresentative(Long waterRightId, Long ownerId, Long contactId, WaterRightRepresentativeDto createDto);

    public WaterRightRepresentativeDto editWaterRightRepresentative(Long waterRightId, Long ownerId, Long contactId, Long representativeId, WaterRightRepresentativeUpdateDto updateDto);
}
