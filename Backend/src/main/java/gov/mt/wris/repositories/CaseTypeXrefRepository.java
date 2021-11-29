package gov.mt.wris.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.CaseTypeXref;
import gov.mt.wris.models.IdClasses.CaseTypeXrefId;

@Repository
public interface CaseTypeXrefRepository extends CrudRepository<CaseTypeXref, CaseTypeXrefId> {
    @Modifying
    @Query(value = "DELETE\n" +
                    "FROM CaseTypeXref c\n" +
                    "WHERE c.eventCode = :eventCode")
    void deleteAllByEventCode(@Param("eventCode") String eventCode);
}
