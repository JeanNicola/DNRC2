package gov.mt.wris.repositories;

import gov.mt.wris.models.RelatedRight;
import gov.mt.wris.models.RelatedRightVerXref;
import gov.mt.wris.models.WaterRight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
public interface RelatedRightRepository extends JpaRepository<RelatedRight, BigDecimal>, CustomRelatedRightRepository {

    @Query(value =  "SELECT w\n" +
                    "FROM WaterRight w \n" +
                    "join w.waterRightType wt\n" +
                    "left join fetch w.waterRightStatus ws\n" +
                    "left join fetch w.originalWaterRight ow\n" +
                    "left join fetch ow.waterRightType owt\n" +
                    "left join fetch ow.waterRightStatus ows\n" +
                    "WHERE w.waterRightId IN (" +
                    "SELECT rr.waterRight.waterRightId \n" +
                    "FROM RelatedRightVerXref rr\n" +
                    "WHERE rr.relatedRightId = :relatedRightId )",
            countQuery = "SELECT count(w.waterRightId)\n" +
                        "FROM WaterRight w \n" +
                        "join w.waterRightType wt\n" +
                        "left join w.waterRightStatus ws\n" +
                        "left join w.originalWaterRight ow\n" +
                        "left join ow.waterRightType owt\n" +
                        "left join ow.waterRightStatus ows\n" +
                        "WHERE w.waterRightId IN (" +
                        "SELECT rr.waterRight.waterRightId \n" +
                        "FROM RelatedRightVerXref rr\n" +
                        "WHERE rr.relatedRightId = :relatedRightId )")
    public Page<WaterRight> getRelatedRightWaterRights(Pageable pageable, BigDecimal relatedRightId);

    @Query(value =  "SELECT rr\n" +
            "FROM RelatedRightVerXref rr \n" +
            "join fetch rr.waterRight w\n" +
            "join fetch w.waterRightType wt\n" +
            "left join fetch w.waterRightStatus ws\n" +
            "left join fetch w.originalWaterRight ow\n" +
            "left join fetch ow.waterRightType owt\n" +
            "left join fetch ow.waterRightStatus ows\n" +
            "WHERE rr.relatedRightId = :relatedRightId ",
            countQuery = "SELECT count(rr.waterRightId)\n" +
                    "FROM RelatedRightVerXref rr \n" +
                    "join rr.waterRight w\n" +
                    "join w.waterRightType wt\n" +
                    "left join w.waterRightStatus ws\n" +
                    "left join w.originalWaterRight ow\n" +
                    "left join ow.waterRightType owt\n" +
                    "left join ow.waterRightStatus ows\n" +
                    "WHERE rr.relatedRightId = :relatedRightId")
    public Page<RelatedRightVerXref> getRelatedRightWaterRightsWithVersions(Pageable pageable, BigDecimal relatedRightId);

    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
            "FROM RelatedRight r\n" +
            "WHERE r.relatedRightId = :relatedRightId \n")
    public int deleteRelatedRight(@Param("relatedRightId") BigDecimal relatedRightId);

}
