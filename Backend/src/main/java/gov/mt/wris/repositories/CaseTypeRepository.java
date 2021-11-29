package gov.mt.wris.repositories;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CaseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// adding a custom pagination and filter function and the functions from CrudRepository
public interface CaseTypeRepository extends JpaRepository<CaseType, String>, CustomCaseTypeRepository{
    public Page<CaseType> getCaseTypes(Pageable pageable, String sortColumn, SortDirection sortDirection, String code, String description, String program);

    public void deleteById(String id);

    @Query(value = "select count(c.CATP_CD)\n"+
            "FROM WRD_WATER_COURT_CASES c\n"+
            "where c.CATP_CD = :code",
            nativeQuery = true)
    int existsInCourtCasesCount(@Param("code") String code);

    @Query(value = "select count(e)\n"+
            "FROM CaseTypeXref e\n"+
            "where e.caseCode = :code")
    int existsInEventTypesCount(@Param("code") String code);

    List<CaseType> findAllByCodeNotInOrderByDescriptionAsc(List<String> unsupportedCodes);

    Optional<CaseType> getCaseTypeByCode(String code);

}
