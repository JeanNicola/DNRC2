package gov.mt.wris.repositories;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.WaterRightSortColumn;
import gov.mt.wris.dtos.WaterRightVersionSearchSortColumn;
import gov.mt.wris.models.WaterRight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CustomWaterRightRepository {
    public Page<WaterRight> getWaterRights(Pageable pageable,
        WaterRightSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String ext,
        String typeCode,
        String statusCode,
        String subBasin,
        String waterReservationId,
        String conservationDistrictNumber);

    public Page<Object[]> getWaterRightsWithChangeAuthorizationCount(Pageable pageable,
        WaterRightSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String ext,
        String typeCode,
        String statusCode,
        String subBasin,
        String waterReservationId,
        String conservationDistrictNumber);

    public Page<Object[]> getWaterRightsByVersions(Pageable pageable,
        WaterRightVersionSearchSortColumn sortColumn,
        DescSortDirection sortDirection,
        String waterRightNumber,
        String version,
        String versionTypeMeaning);

    public String getScannedDocUrl(BigDecimal waterRightId, BigDecimal version);
}