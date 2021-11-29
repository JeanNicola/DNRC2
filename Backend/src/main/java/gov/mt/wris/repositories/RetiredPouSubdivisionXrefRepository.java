package gov.mt.wris.repositories;

import gov.mt.wris.models.RetiredPouSubdivisionXref;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface RetiredPouSubdivisionXrefRepository extends JpaRepository<RetiredPouSubdivisionXref, BigDecimal>  {

    @Query(value =  "SELECT retSubXref \n" +
            "FROM RetiredPouSubdivisionXref retSubXref \n" +
            "join fetch retSubXref.subdivisionCode subdivisionCode \n" +
            "WHERE retSubXref.retiredPlaceId = :retiredPlaceId \n" +
            "AND retSubXref.purposeId = :purposeId \n",
            countQuery = "SELECT count(retSubXref.retiredPlaceId) \n" +
                    "FROM RetiredPouSubdivisionXref retSubXref \n" +
                    "WHERE retSubXref.retiredPlaceId = :retiredPlaceId \n" +
                    "AND retSubXref.purposeId = :purposeId \n")
    public Page<RetiredPouSubdivisionXref> findByRetiredPlaceIdAndPurposeId(Pageable pageable, BigDecimal retiredPlaceId, BigDecimal purposeId);

    public Optional<RetiredPouSubdivisionXref> findByRetiredPlaceIdAndPurposeIdAndCode(BigDecimal retiredPlaceId, BigDecimal purposeId, String code);

}
