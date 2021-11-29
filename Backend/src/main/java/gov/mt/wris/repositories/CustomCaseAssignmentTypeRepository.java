package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CaseAssignmentType;

public interface CustomCaseAssignmentTypeRepository {
    public Page<CaseAssignmentType> getCaseAssignmentTypes(Pageable pageable, String sortColumn, SortDirection sortDirection, String code, String description, String program);
}