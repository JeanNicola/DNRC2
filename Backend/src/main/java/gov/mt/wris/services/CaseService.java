package gov.mt.wris.services;

import gov.mt.wris.dtos.*;
import gov.mt.wris.dtos.CaseCreationDto;
import gov.mt.wris.dtos.CaseDto;
import gov.mt.wris.dtos.CaseRegisterCreateUpdateDto;
import gov.mt.wris.dtos.CaseRegisterDetailDto;
import gov.mt.wris.dtos.CaseRegisterPageDto;
import gov.mt.wris.dtos.CaseRegisterSortColumn;
import gov.mt.wris.dtos.CaseSearchResultPageDto;
import gov.mt.wris.dtos.CaseSearchSortColumn;
import gov.mt.wris.dtos.SearchBasinsResultPageDto;
import gov.mt.wris.dtos.SearchBasinsSortColumn;
import gov.mt.wris.dtos.CaseUpdateDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionCasesPageDto;
import gov.mt.wris.dtos.WaterRightVersionCasesSortColumn;

import java.math.BigDecimal;

public interface CaseService {

    public WaterRightVersionCasesPageDto getWaterRightVersionCases(int pagenumber, int pagesize, WaterRightVersionCasesSortColumn sortColumn, SortDirection sortDirection, BigDecimal waterRightId, BigDecimal versionId);

    public CaseSearchResultPageDto searchCases(int pagenumber, int pagesize, CaseSearchSortColumn sortColumn, SortDirection sortDirection, String applicationId, String caseNumber, String caseTypeCode, String caseStatusCode, String waterCourtCaseNumber);

    public CaseDto createCase(CaseCreationDto createDto);

    public CaseDto getCourtCase(Long caseId);

    public SearchBasinsResultPageDto searchBasins(int pagenumber, int pagesize, SearchBasinsSortColumn sortColumn, SortDirection sortDirection, String basin);

    public CaseDto updateCourtCase(Long caseId, CaseUpdateDto updateDto);

    public void deleteCaseHearing(BigDecimal caseId);

    public CaseRegisterPageDto getCaseEvents(int pagenumber, int pagesize, CaseRegisterSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId);

    public CaseRegisterDetailDto updateCaseEvent(Long caseId, Long eventId, CaseRegisterCreateUpdateDto updateDto);

    public CaseRegisterDetailDto createCaseEvent(Long caseId, CaseRegisterCreateUpdateDto createDto);

    public ApplicantsPageDto getCaseApplicationApplicants(int pagenumber, int pagesize, ApplicantSortColumn sortColumn, SortDirection sortDirection, Long caseId);

    public ObjectionsPageDto getCaseApplicationObjections(int pagenumber, int pagesize, ObjectionSortColumn sortColumn, SortDirection sortDirection, Long caseId);

    public CaseCommentsDto getCaseComments(Long caseId);

    public CaseCommentsDto updateCaseComments(Long caseId, CaseCommentsDto updateDto);

}
