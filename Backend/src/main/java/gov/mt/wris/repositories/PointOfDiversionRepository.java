package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.PointOfDiversion;

@Repository
public interface PointOfDiversionRepository extends JpaRepository<PointOfDiversion, BigDecimal>, CustomPointOfDiversionRepository {
    @Query("SELECT pod\n" +
            "FROM PointOfDiversion pod\n" +
            "join fetch pod.county c\n" +
            "left join fetch pod.legalLandDescription ll\n" +
            "left join fetch ll.trs trs\n" +
            "WHERE pod.version.waterRightId = :waterRightId\n" +
            "AND pod.version.version = :version\n" +
            "order by pod.number")
    public List<PointOfDiversion> findAllWithLegalLandDescriptions(@Param("waterRightId") BigDecimal waterRightId, @Param("version") BigDecimal version);

    @Query(value = "SELECT pod\n" +
        "FROM PointOfDiversion pod\n" +
        "JOIN FETCH pod.county c\n" +
        "LEFT JOIN FETCH pod.legalLandDescription ll\n" +
        "LEFT JOIN FETCH ll.trs trs\n" +
        "LEFT JOIN FETCH pod.majorTypeReference mj\n" +
        "LEFT JOIN FETCH pod.meansOfDiversion md\n" +
        "LEFT JOIN FETCH pod.ditch d\n" +
        "LEFT JOIN FETCH d.diversionType dt\n" +
        "WHERE pod.waterRightId = :waterRightId\n" +
        "AND pod.versionId = :version",
        countQuery = "SELECT COUNT(pod)\n" +
        "FROM PointOfDiversion pod\n" +
        "WHERE pod.waterRightId = :waterRightId\n" +
        "AND pod.versionId = :version")
    public Page<PointOfDiversion> findPointOfDiversions(Pageable pageable, BigDecimal waterRightId, BigDecimal version);

    @Query(value = "SELECT pod\n" +
            "FROM PointOfDiversion pod\n" +
            "JOIN FETCH pod.typeReference t\n" +
            "JOIN FETCH pod.originReference o\n" +
            "JOIN FETCH pod.meansOfDiversion m\n" +
            "JOIN FETCH pod.majorTypeReference mt\n" +
            "JOIN FETCH pod.county c\n" +
            "LEFT JOIN FETCH pod.ditch d\n" +
            "LEFT JOIN FETCH d.diversionType dt\n" +
            "LEFT JOIN FETCH pod.legalLandDescription ll\n" +
            "LEFT JOIN FETCH ll.trs tr\n" +
            "LEFT JOIN FETCH pod.sourceOriginReference sor\n" +
            "LEFT JOIN FETCH pod.source s\n" +
            "LEFT JOIN FETCH s.sourceName sn\n" +
            "LEFT JOIN FETCH pod.minorType mit\n" +
            "LEFT JOIN FETCH pod.subdivision sbcd\n" +
            "LEFT JOIN FETCH pod.addresses a\n" +
            "LEFT JOIN FETCH a.zipCode z\n" +
            "LEFT JOIN FETCH z.city c\n" +
            "WHERE pod.id = :id")
    public Optional<PointOfDiversion> findFullPOD(BigDecimal id);

    public int renumberPODs(String type,
        BigDecimal waterRightId,
        BigDecimal version);

    public Optional<PointOfDiversion> findById(BigDecimal id);
}
