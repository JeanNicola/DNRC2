package gov.mt.wris.repositories;

import gov.mt.wris.models.IdClasses.ReferenceId;
import gov.mt.wris.models.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, ReferenceId> {

    List<Reference> findAllByDomain(String domain);
    List<Reference> findAllByDomainOrderByValueAsc(String domain);
    List<Reference> findAllByDomainOrderByMeaningAsc(String domain);
    List<Reference> findAllByDomainOrderByMeaningDesc(String domain);
    List<Reference> findAllByDomainAndValueNotInOrderByMeaningAsc(String domain, List<String> unsupportedTypes);
    Reference findByValue(String value);

    @Query("SELECT r FROM Reference r WHERE r.domain = :domain AND r.value LIKE CONCAT('%',:lowValue,'%')")
    Reference findReportUrl(String domain, String lowValue);

    @Query("SELECT r FROM Reference r WHERE r.domain = :domain AND r.value LIKE CONCAT('%',:lowValue,'%') AND r.abbreviation = :abbreviation")
    Reference findReportUrlWithAbbreviation(String domain, String lowValue, String abbreviation);

}
