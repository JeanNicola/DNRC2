package gov.mt.wris.repositories;

import gov.mt.wris.dtos.CaseSearchSortColumn;
import gov.mt.wris.dtos.EligibleWaterRightsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CourtCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CustomCourtCaseRepository {

    public Page<CourtCase> searchCases(Pageable pageable, CaseSearchSortColumn sortColumn, SortDirection sortDirection, String applicationId, String caseNumber, String caseTypeCode, String caseStatusCode, String waterCourtCaseNumber);

}
