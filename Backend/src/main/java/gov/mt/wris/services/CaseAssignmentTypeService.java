package gov.mt.wris.services;

import java.util.Optional;

import gov.mt.wris.dtos.CaseAssignmentTypeDto;
import gov.mt.wris.dtos.CaseAssignmentTypePageDto;
import gov.mt.wris.dtos.SortColumn;
import gov.mt.wris.dtos.SortDirection;

public interface CaseAssignmentTypeService {
    Optional<CaseAssignmentTypeDto> getCase(String code);

    CaseAssignmentTypePageDto getCaseAssignmentTypes(int pagenumber,int pagesize, SortColumn sortColumn, SortDirection sortDirection, String Code, String AssignmentType, String Program);

    CaseAssignmentTypeDto createCase(CaseAssignmentTypeDto caseDTO);

    void deleteCase(String code);

    CaseAssignmentTypeDto replaceCase(CaseAssignmentTypeDto caseDto, String code);

    CaseAssignmentTypeDto toUpperCase(CaseAssignmentTypeDto Dto);
}
