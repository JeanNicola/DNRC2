package gov.mt.wris.services;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.OfficeCreationDto;
import gov.mt.wris.dtos.OfficeDto;
import gov.mt.wris.dtos.OfficePageDto;
import gov.mt.wris.dtos.OfficeSortColumn;
import gov.mt.wris.dtos.ResponsibleOfficeDto;
import gov.mt.wris.dtos.StaffCreationDto;
import gov.mt.wris.dtos.StaffDto;
import gov.mt.wris.dtos.StaffPageDto;
import gov.mt.wris.dtos.StaffSortColumn;

public interface WaterRightFileLocationService {
    public ResponsibleOfficeDto getResponsibleOffice(Long waterRightId);

    public ResponsibleOfficeDto editResponsibleOffice(Long waterRightId, ResponsibleOfficeDto dto);

    public OfficePageDto getWaterRightsOffices(Long waterRightId,
        int pageNumber,
        int pageSize,
        OfficeSortColumn sortDTOColumn,
        DescSortDirection sortDirection);

    public OfficeDto addWaterRightOffice(Long waterRightId, OfficeCreationDto dto);

    public OfficeDto editWaterRightOffice(Long waterRightId, Long officeXrefId, OfficeDto dto);

    public void deleteWaterRightOffice(Long waterRightId, Long officeXrefId);

    public StaffPageDto getWaterRightStaff(Long waterRightId,
        int pageNumber,
        int pageSize,
        StaffSortColumn sortDTOColumn,
        DescSortDirection sortDirection);

    public StaffDto addWaterRightStaff(Long waterRightId, StaffCreationDto dto);

    public StaffDto editWaterRightStaff(Long waterRightId, Long staffXrefId, StaffDto dto);

    public void deleteWaterRightStaff(Long waterRightId, Long staffXrefId);
}