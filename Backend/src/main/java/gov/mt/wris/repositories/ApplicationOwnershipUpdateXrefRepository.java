package gov.mt.wris.repositories;

import gov.mt.wris.models.ApplicationOwnshipXref;
import gov.mt.wris.models.IdClasses.ApplicationOwnshipXrefId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ApplicationOwnershipUpdateXrefRepository extends JpaRepository<ApplicationOwnshipXref, ApplicationOwnshipXrefId> {
    public int deleteByOwnershipUpdateId(BigDecimal ownershipUpdateId);

    public List<ApplicationOwnshipXref> findByOwnershipUpdateId(BigDecimal ownershipUpdateId);

    public ApplicationOwnshipXref findApplicationOwnshipXrefByOwnershipUpdateIdAndApplicationId(BigDecimal ownershipUpdateId, BigDecimal applicationId);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM ApplicationOwnshipXref ax\n" +
            "WHERE ax.ownershipUpdateId = :ownershipUpdateId and ax.applicationId = :applicationId \n")
    public int deleteByOwnershipUpdateIdAndApplicationId(@Param("ownershipUpdateId") BigDecimal ownershipUpdateId, @Param("applicationId") BigDecimal applicationId);

}
