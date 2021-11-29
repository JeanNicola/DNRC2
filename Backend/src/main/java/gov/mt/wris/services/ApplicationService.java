package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

public interface ApplicationService {
    public ApplicationSearchPageDto getApplications(int pagenumber,
                                                int pagesize,
                                                ApplicationSortColumn sortColumn,
                                                DescSortDirection sortDirection,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode);

    public ApplicationOwnerSearchPageDto getApplicationsByOwners(int pagenumber,
                                                int pagesize,
                                                ApplicationOwnerSortColumn sortColumn,
                                                DescSortDirection sortDirection,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode,
                                                String ownerContactId,
                                                String ownerLastName,
                                                String ownerFirstName,
                                                String repContactId,
                                                String repLastName,
                                                String repFirstName);

    public OwnerApplicationRepPageDto getOwnersApplications(int pagenumber,
                                                int pagesize,
                                                OwnerApplicationSortColumn sortColumn,
                                                DescSortDirection sortDirection,
                                                long contactID,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode,
                                                String repContactId,
                                                String repLastName,
                                                String repFirstName);

    public ApplicationRepSearchPageDto getApplicationsByReps(int pagenumber,
                                                int pagesize,
                                                ApplicationRepSortColumn sortColumn,
                                                DescSortDirection sortDirection,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode,
                                                String repContactId,
                                                String repLastName,
                                                String repFirstName);

    public RepApplicationOwnerPageDto getRepsApplications(int pagenumber,
                                                int pagesize,
                                                RepApplicationSortColumn sortDTOColumn,
                                                DescSortDirection sortDirection,
                                                long repContactID,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode);

    public ApplicationDto getApplication(Long id);

    public ApplicationDto createApplication(ApplicationCreationDto newApplication);

    public ApplicationDto updateApplication(Long id, ApplicationUpdateDto newApplication);

    public void deleteApplication(Long applicationId);

    public RelatedApplicationPageDto findRelatedApplications(String applicationId,
                                                             Integer pageNumber,
                                                             Integer pageSize,
                                                             RelatedApplicationSortColumn sortColumn,
                                                             SortDirection sortDirection
    );

    public ApplicationWaterRightsSummaryDto getWaterRightSummary(Long applicationId);

    public ApplicationWaterRightsSummaryDto editWaterRightSummaryDto(Long applicationId, ApplicationWaterRightsSummaryDto summaryDto);

    public ChangeDto updateChange(String applicationId, ChangeDto changeDto);

    public ChangeDto getChange(String applicationId);

    public ResponsibleOfficeDto getResponsibleOffice(Long applicationId);

    public ResponsibleOfficeDto editResponsibleOffice(Long applicationId, ResponsibleOfficeDto dto);

    public ProcessorDto getProcessor(Long applicationId);

    public ProcessorDto editProcessor(Long applicationId, ProcessorDto dto);

    public OfficePageDto getApplicationsOffices(Long applicationId,
                                                int pageNumber,
                                                int pageSize,
                                                OfficeSortColumn sortDTOColumn,
                                                DescSortDirection sortDirection);

    public StaffPageDto getApplicationsStaff(Long applicationId,
                                                int pageNumber,
                                                int pageSize,
                                                StaffSortColumn sortDTOColumn,
                                                DescSortDirection sortDirection);

    public OfficeDto addApplicationOffice(Long applicationId, OfficeCreationDto dto);

    public OfficeDto editApplicationOffice(Long applicationId, Long officeXrefId, OfficeDto dto);

    public void deleteApplicationOffice(Long applicationId, Long officeXrefId);

    public StaffDto editApplicationStaff(Long applicationId, Long staffXrefId, StaffDto dto);

    public StaffDto addApplicationStaff(Long applicationId, StaffCreationDto dto);

    public void deleteApplicationStaff(Long applicationId, Long staffXrefId);

}
