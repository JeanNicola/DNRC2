package gov.mt.wris.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CaseAssignmentType;

// adding a custom pagination and filter function and the functions from CrudRepository
public interface CaseAssignmentTypeRepository extends CrudRepository<CaseAssignmentType, String>, CustomCaseAssignmentTypeRepository{
    public Page<CaseAssignmentType> getCaseAssignmentTypes(Pageable pageable, String sortColumn, SortDirection sortDirection, String code, String description, String program);

    public void deleteById(String id);
}
