package gov.mt.wris.repositories;

import gov.mt.wris.models.IdClasses.SubdivisionXrefId;
import gov.mt.wris.models.SubdivisionXref;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface SubdivisionXrefRepository extends JpaRepository<SubdivisionXref, SubdivisionXrefId> {

    @Query(value =  " SELECT sdx \n" +
            " FROM SubdivisionXref sdx \n" +
            " JOIN FETCH sdx.subdivisionCode subdivisionCode \n" +
            " WHERE sdx.placeId = :placeId \n" +
            " AND sdx.purposeId = :purposeId \n",
            countQuery = "SELECT COUNT(sdx) FROM SubdivisionXref sdx WHERE sdx.placeId = :placeId AND sdx.purposeId = :purposeId \n")
    public Page<SubdivisionXref> findByPlaceIdAndPurposeId(Pageable pageable, BigDecimal placeId, BigDecimal purposeId);

    public Optional<SubdivisionXref> findSubdivisionByPlaceIdAndPurposeIdAndCode(BigDecimal placeId, BigDecimal purposeId, String code);

    @Modifying
    @Transactional
    @Query(value = "DELETE \n" +
            "FROM SubdivisionXref s\n" +
            "WHERE s.purposeId = :purposeId AND s.placeId = :placeId \n")
    public int deletePlaceOfUseSubdivisions(@Param("purposeId") BigDecimal purposeId, @Param("placeId") BigDecimal placeId);

}
