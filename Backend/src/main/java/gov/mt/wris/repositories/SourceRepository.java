package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.Source;

@Repository
public interface SourceRepository extends JpaRepository<Source, BigDecimal> {
    @Query(value = "SELECT s\n" +
        "FROM Source s\n" +
        "JOIN FETCH s.sourceName sn\n" +
        "WHERE CONCAT(sn.name, CASE WHEN s.forkName is null THEN '' ELSE CONCAT(', ', s.forkName) END) like :name",
        countQuery = "SELECT COUNT(s)\n" +
        "FROM Source s\n" +
        "JOIN s.sourceName sn\n" +
        "WHERE CONCAT(sn.name, CASE WHEN s.forkName is null THEN '' ELSE CONCAT(', ', s.forkName) END) like :name")
    public Page<Source> searchSource(Pageable pageable, String name);
}
