package gov.mt.wris.services;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.GeocodeWaterRightPageDto;
import gov.mt.wris.dtos.GeocodeWaterRightSortColumn;
import gov.mt.wris.dtos.WaterRightGeocodeDto;
import gov.mt.wris.dtos.WaterRightGeocodePageDto;
import gov.mt.wris.dtos.WaterRightGeocodeSortColumn;
import gov.mt.wris.dtos.WaterRightGeocodesCreationDto;

public interface WaterRightGeocodeService {
    public GeocodeWaterRightPageDto getGeocodeWaterRights(int pagenumber, int pagesize, GeocodeWaterRightSortColumn sortColumn, DescSortDirection sortDirection, String geocodeId);
    public WaterRightGeocodePageDto getWaterRightGeocodes(int pagenumber, int pagesize, WaterRightGeocodeSortColumn sortColumn, DescSortDirection sortDirection, Long waterRightId);

    public void addWaterRightGeocode(Long waterRightId, WaterRightGeocodesCreationDto dto);

    public WaterRightGeocodeDto editGeocode(Long waterRightId, Long xrefId, WaterRightGeocodeDto dto);

    public void deleteGeocode(Long waterRightId, Long xrefId);

    public void deleteInvalidGeocodes(Long waterRightId);

    public void unresolveWaterRightGeocodes(Long waterRightId);

    public void severWaterRightGeocodes(Long waterRightId);
}
