package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.RemarkElement;

@Repository
public interface RemarkElementRepository extends JpaRepository<RemarkElement, BigDecimal>, CustomRemarkElementRepository {
    public Page<RemarkElement> findByRemarkId(Pageable pageable, BigDecimal remarkId);

    @Query(value = "SELECT e\n" +
        "FROM RemarkElement e\n" +
        "JOIN FETCH e.variable v\n" +
        "WHERE e.remarkId = :remarkId",
        countQuery = "SELECT COUNT(e)\n" +
            "FROM RemarkElement e\n" +
            "WHERE e.remarkId = :remarkId")
    public Page<RemarkElement> findFullElement(Pageable pageable, @Param("remarkId") BigDecimal remarkId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO RemarkElement (remarkId, variableId)\n" +
                "SELECT vr.id, v.variableId\n" +
                "FROM VersionRemark vr\n" +
                "JOIN vr.remarkCodeLibrary cl\n" +
                "JOIN cl.variables v\n" +
                "WHERE vr.id = :remarkId")
    public void createVariables(@Param("remarkId") BigDecimal remarkId);

    @Transactional
    @Modifying
    public int deleteByRemarkId(BigDecimal remarkId);

    // only return text when the category code
    // matches the remark code
    // ^ Currently, this is not how the legacy application is working,
    // even though the code says so, waiting on response from DCoey
    @Query(value = "SELECT e\n" +
        "FROM VersionRemark r\n" +
        "JOIN r.remarkCodeLibrary rl\n" +
        "JOIN r.elements e\n" +
        "JOIN FETCH e.variable v\n" +
        "WHERE r.id = :remarkId\n"
        // "AND rl.category = rl.code"
        )
    public List<RemarkElement> findAllElements(@Param("remarkId") BigDecimal remarkId);

    @Query(value = "SELECT e\n" +
        "FROM RemarkElement e\n" +
        "LEFT JOIN FETCH e.variable v\n" +
        "WHERE e.id = :dataId")
    public Optional<RemarkElement> findFullElementById(@Param("dataId") BigDecimal dataId);
}
