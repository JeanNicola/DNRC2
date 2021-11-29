package gov.mt.wris.services;

import java.util.Optional;

import gov.mt.wris.dtos.AllCaseTypesDto;
import gov.mt.wris.dtos.CaseTypeDto;
import gov.mt.wris.dtos.CaseTypePageDto;
import gov.mt.wris.dtos.CaseTypeSortColumn;
import gov.mt.wris.dtos.SortDirection;

public interface CaseTypeService {
    Optional<CaseTypeDto> getCase(String code);

    AllCaseTypesDto getAllCaseTypes();

    CaseTypePageDto getCaseTypes(int pagenumber,int pagesize, CaseTypeSortColumn sortColumn, SortDirection sortDirection, String Code, String AssignmentType, String Program);

    CaseTypeDto createCase(CaseTypeDto caseDTO);

    void deleteCase(String code);

    CaseTypeDto replaceCase(CaseTypeDto caseDto, String code);

    CaseTypeDto toUpperCase(CaseTypeDto Dto);
}
