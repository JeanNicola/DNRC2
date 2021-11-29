package gov.mt.wris.services;

import gov.mt.wris.dtos.ReservoirCreationDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionReservoirsPageDto;
import gov.mt.wris.dtos.WaterRightVersionReservoirsSortColumn;

public interface VersionReservoirService {
    public WaterRightVersionReservoirsPageDto getVersionReservoirs(int pagenumber, int pagesize, WaterRightVersionReservoirsSortColumn sortColumn, SortDirection sortDirection, Long waterRightId, Long versionNumber);

    public void addVersionReservoir(Long waterRightId, Long versionNumber, ReservoirCreationDto dto);

    public void updateReservoir(Long waterRightId, Long versionNumber, Long reservoirId, ReservoirCreationDto dto);

    public void deleteReservoir(Long waterRightId, Long versionNumber, Long reservoirId);
}