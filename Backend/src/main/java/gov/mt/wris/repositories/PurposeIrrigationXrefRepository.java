package gov.mt.wris.repositories;

import gov.mt.wris.models.IdClasses.PurposeIrrigationXrefId;
import gov.mt.wris.models.PurposeIrrigationXref;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PurposeIrrigationXrefRepository extends JpaRepository<PurposeIrrigationXref, PurposeIrrigationXrefId> {

    public Optional<PurposeIrrigationXref> findOneByPurposeId(BigDecimal purposeId);

    public Optional<PurposeIrrigationXref> findOneByPurposeIdAndIrrigationTypeCode(BigDecimal purposeId, String irrigationType);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM PurposeIrrigationXref pix\n" +
            "WHERE pix.purposeId = :purposeId \n")
    public int deleteAllByPurposeId(@Param("purposeId") BigDecimal purposeId);

}
