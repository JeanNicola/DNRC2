package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

import java.math.BigDecimal;

public interface CaseWaterRightVersionService {

    public CaseWaterRightVersionsPageDto getCaseWaterRightVersions(int pagenumber, int pagesize, CaseWaterRightVersionsSortColumn sortColumn, SortDirection sortDirection, Long caseId);

    public CaseWaterRightVersionObjectionsPageDto getCaseWaterRightVersionObjections(int pagenumber, int pagesize, CaseWaterRightVersionObjectionsSortColumn sortColumn, SortDirection sortDirection, Long caseId, Long waterRightId, Long versionId);

    public CaseWaterRightVersionReferenceDto createCaseWaterRightVersionReference(Long caseId, CaseWaterRightVersionReferenceDto createDto);

    public void deleteCaseWaterRightVersionReference(Long caseId, Long waterRightId, Long versionId);

    public EligibleWaterRightsPageDto getEligibleWaterRights(int pagenumber, int pagesize, EligibleWaterRightsSortColumn sortColumn, SortDirection sortDirection, String waterNumber, String decreeId, String basin);

}
