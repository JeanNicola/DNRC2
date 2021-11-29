package gov.mt.wris.repositories;

import gov.mt.wris.models.AerialPhoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface AerialPhotoRepository extends JpaRepository<AerialPhoto, BigDecimal> {

    public Page<AerialPhoto> findByPexmId(Pageable pageable, BigDecimal pexmId);

    public Optional<AerialPhoto> findByAerialId(BigDecimal aerialId);

    @Query( value = "SELECT aer\n"
        + "FROM AerialPhoto aer\n"
        + "LEFT JOIN FETCH aer.placesOfUse pou\n"
        + "JOIN FETCH aer.pouExamination pouExam \n"
        + "JOIN FETCH pouExam.examination exam \n"
        + "WHERE aer.pexmId = :pexmId \n"
        + "AND aer.aerialId = :aerialId \n")
    public Optional<AerialPhoto> findByPexmIdAndAerialId(BigDecimal pexmId, BigDecimal aerialId);

}
