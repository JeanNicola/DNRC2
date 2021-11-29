package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CaseType;

public interface CustomCaseTypeRepository {
    public Page<CaseType> getCaseTypes(Pageable pageable, String sortColumn, SortDirection sortDirection, String code, String description, String program);
}