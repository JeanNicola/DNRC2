package gov.mt.wris.repositories;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesSortColumn;
import gov.mt.wris.dtos.WaterRightVersionSortColumn;
import gov.mt.wris.dtos.WaterRightVersionsForRelatedRightSortColumn;
import gov.mt.wris.models.WaterRightVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CustomWaterRightVersionRepository {
    public Page<WaterRightVersion> getWaterRightVersions(Pageable pageable, WaterRightVersionSortColumn sortColumn, DescSortDirection sortDirection, Long waterRightId, String basin, String waterRightNumber, String versionNumber, String versionType);

    public Page<WaterRightVersion> getWaterRightVersionsForRelatedRightReference(Pageable pageable, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection, Long relatedRightId, String basin, String waterRightNumber, String ext);

    public Page<WaterRightVersion> getWaterRightVersionsAll(Pageable pageable, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection, String basin, String waterRightNumber, String ext);

    public int endDateWaterRightGeocodes(Long waterRightId);

    public int createVersion(BigDecimal waterRightId, String versionType);

    public int testAttainableVolume(BigDecimal waterRightId, BigDecimal versionId);

    public Page<WaterRightVersionApplicationReferencesDto> getWaterRightVersionApplicationReferences(Pageable pageable, WaterRightVersionApplicationReferencesSortColumn sortColumn, SortDirection sortDirection, Long waterRightId, Long versionNumber);
}
