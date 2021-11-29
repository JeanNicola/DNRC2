package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.VersionCompact;

@Repository
public interface VersionCompactRepository extends JpaRepository<VersionCompact, BigDecimal> {
    @Query(value = "SELECT vc\n" +
        "FROM VersionCompact vc\n" +
        "JOIN FETCH vc.subcompact s\n" +
        "JOIN FETCH s.compact c\n" +
        "WHERE vc.waterRightId = :waterRightId\n" +
        "AND vc.versionId = :versionId",
        countQuery = "SELECT Count(vc)\n" +
        "FROM VersionCompact vc\n" +
        "WHERE vc.waterRightId = :waterRightId\n" +
        "AND vc.versionId = :versionId")
    public Page<VersionCompact> findCompactsByVersion(Pageable pageable, BigDecimal waterRightId, BigDecimal versionId);

    @Query(value = "SELECT vc\n" +
        "FROM VersionCompact vc\n" +
        "JOIN FETCH vc.subcompact s\n" +
        "JOIN FETCH s.compact c\n" +
        "WHERE vc.id = :id")
    public Optional<VersionCompact> findFullCompactById(BigDecimal id);
}
