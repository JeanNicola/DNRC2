package gov.mt.wris.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.ApplicationTypeXref;
import gov.mt.wris.models.IdClasses.ApplicationTypeXrefId;

@Repository
public interface ApplicationTypeXrefRepository extends CrudRepository<ApplicationTypeXref, ApplicationTypeXrefId> {
    @Modifying
    @Query(value = "DELETE\n" +
                    "FROM ApplicationTypeXref a\n" +
                    "WHERE a.eventCode = :eventCode")
    void deleteAllByEventCode(@Param("eventCode") String eventCode);
}
