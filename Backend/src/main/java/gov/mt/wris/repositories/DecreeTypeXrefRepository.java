package gov.mt.wris.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.DecreeTypeXref;
import gov.mt.wris.models.IdClasses.DecreeTypeXrefId;

@Repository
public interface DecreeTypeXrefRepository extends CrudRepository<DecreeTypeXref, DecreeTypeXrefId> {
    @Modifying
    @Query(value = "DELETE\n" +
                    "FROM DecreeTypeXref d\n" +
                    "WHERE d.eventCode = :eventCode")
    void deleteAllByEventCode(@Param("eventCode") String eventCode);
}
