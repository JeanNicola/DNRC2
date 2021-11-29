package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.ApplicationOwnerSearchResultDto;
import gov.mt.wris.dtos.ApplicationOwnerSortColumn;
import gov.mt.wris.dtos.ApplicationRepSearchResultDto;
import gov.mt.wris.dtos.ApplicationRepSortColumn;
import gov.mt.wris.dtos.ApplicationSearchResultDto;
import gov.mt.wris.dtos.ApplicationSortColumn;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.OwnerApplicationRepListDto;
import gov.mt.wris.dtos.OwnerApplicationSortColumn;
import gov.mt.wris.dtos.RepApplicationOwnerListDto;
import gov.mt.wris.dtos.RepApplicationSortColumn;

public interface CustomApplicationRepository {
    Page<ApplicationSearchResultDto> getApplications(Pageable pageable,
                                                    ApplicationSortColumn sortDTOColumn,
                                                    DescSortDirection sortDirection,
                                                    String basin,
                                                    String applicationId,
                                                    String applicationTypeCode);

    Page<ApplicationOwnerSearchResultDto> getApplicationsByOwners(Pageable pageable,
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

    Page<OwnerApplicationRepListDto> getOwnersApplications(Pageable pageable,
                                                    OwnerApplicationSortColumn sortColumn,
                                                    DescSortDirection sortDirection,
                                                    Long contactID,
                                                    String basin,
                                                    String applicationId,
                                                    String applicationTypeCode,
                                                    String repContactId,
                                                    String repLastName,
                                                    String repFirstName);

    Page<ApplicationRepSearchResultDto> getApplicationsByRepresentatives(Pageable pageable,
                                                    ApplicationRepSortColumn sortColumn,
                                                    DescSortDirection sortDirection,
                                                    String basin,
                                                    String applicationId,
                                                    String applicationTypeCode,
                                                    String repContactId,
                                                    String repLastName,
                                                    String repFirstName);

    Page<RepApplicationOwnerListDto> getRepsApplications(Pageable pageable,
                                                    RepApplicationSortColumn sortColumn,
                                                    DescSortDirection sortDirection,
                                                    Long repContactId,
                                                    String basin,
                                                    String applicationId,
                                                    String applicationTypeCode);
}
