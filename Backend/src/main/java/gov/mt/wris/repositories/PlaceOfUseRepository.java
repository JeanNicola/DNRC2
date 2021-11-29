package gov.mt.wris.repositories;

import gov.mt.wris.models.PlaceOfUse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceOfUseRepository extends JpaRepository<PlaceOfUse, BigDecimal>, CustomPlaceOfUseRepository {

    public List<PlaceOfUse> findAllByPurposeId(BigDecimal purposeId);

    @Query(value = " \n" +
            " SELECT SUM(a.ACREAGE) AS ACREAGE from \n" +
            " (SELECT DISTINCT PUR2.PURS_ID_SEQ, PUR2.PURT_CD, PLOU.PUSE_ID_SEQ, NVL(PLOU.ACREAGE, 0) ACREAGE \n" +
            "  FROM WRD_PURPOSES PUR1, WRD_PURPOSES PUR2, WRD_PLACE_OF_USES PLOU \n" +
            "  WHERE PUR1.PURS_ID_SEQ =  :purposeId \n" +
            "  AND PUR1.PURS_ID_SEQ = PLOU.PURS_ID_SEQ \n" +
            "  AND PUR2.WRGT_ID_SEQ = PUR1.WRGT_ID_SEQ \n" +
            "  AND PUR2.VERS_ID_SEQ = PUR1.VERS_ID_SEQ \n" +
            "  AND PUR2.PURT_CD = :purposeType) a \n",
            nativeQuery = true
    )
    public Optional<BigDecimal> getOtherPurposeAcreage(@Param("purposeId") BigDecimal purposeId, @Param("purposeType") String purposeType);

    @Query(value = " \n" +
            " SELECT SUM(a.ACREAGE) AS ACREAGE FROM \n" +
            " (SELECT DISTINCT PLOU.PUSE_ID_SEQ, NVL(PLOU.ACREAGE, 0) ACREAGE \n" +
            "  FROM WRD_PLACE_OF_USES PLOU \n" +
            "  WHERE PLOU.PURS_ID_SEQ = :purposeId) a \n",
            nativeQuery = true
    )
    public Optional<BigDecimal> getPurposePlaceOfUseAcreage(@Param("purposeId") BigDecimal purposeId);

    @Query(value = " \n" +
            " SELECT plou \n" +
            " FROM PlaceOfUse plou \n" +
            " JOIN FETCH plou.legalLandDescription lld \n" +
            " JOIN FETCH lld.trs trs \n" +
            " JOIN FETCH plou.county c \n" +
            " LEFT JOIN FETCH plou.modifiedReference mod\n" +
            " LEFT JOIN FETCH plou.elementOriginReference org\n" +
            " LEFT JOIN FETCH plou.subdivisions sd \n" +
            " WHERE plou.purposeId = :purposeId \n",
            countQuery = "SELECT COUNT(plou) FROM PlaceOfUse plou WHERE plou.purposeId = :purposeId"
    )
    public Page<PlaceOfUse> getPlaceOfUseByPurposeId(Pageable pageable, @Param("purposeId") BigDecimal purposeId);


    @Query(value =  "SELECT plou \n" +
            " FROM PlaceOfUse plou \n" +
            " JOIN FETCH plou.legalLandDescription lld \n" +
            " JOIN FETCH plou.county c \n" +
            " JOIN FETCH lld.trs trs\n" +
            " LEFT JOIN FETCH plou.modifiedReference mod\n" +
            " JOIN FETCH plou.elementOriginReference org\n" +
            " WHERE plou.purposeId = :purposeId ",
            countQuery = "SELECT count(plou) FROM PlaceOfUse plou WHERE plou.purposeId = :purposeId ")
    public Page<PlaceOfUse> findPlaceOfUseByPurposeId(Pageable pageable, BigDecimal purposeId);

    public Optional<PlaceOfUse> findPlaceOfUseByPlaceIdAndPurposeId(BigDecimal placeId, BigDecimal purposeId);

    @Query(value =  "SELECT plou \n" +
            " FROM PlaceOfUse plou \n" +
            " JOIN FETCH plou.purpose p \n" +
            " JOIN FETCH p.waterRightVersion v \n" +
            " JOIN FETCH v.waterRight w \n" +
            " JOIN FETCH w.waterRightType  wt \n" +
            " WHERE plou.placeId = :placeId " +
            " AND plou.purposeId = :purposeId ")
    public Optional<PlaceOfUse> findPlaceOfUseByPlaceIdAndPurposeIdWithPurpose(BigDecimal placeId, BigDecimal purposeId);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM PlaceOfUse p\n" +
            "WHERE p.purposeId = :purposeId AND p.placeId = :placeId \n")
    public int deletePlaceOfUse(@Param("purposeId") BigDecimal purposeId, @Param("placeId") BigDecimal placeId);

}
