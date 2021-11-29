package gov.mt.wris.repositories;

import gov.mt.wris.models.IdClasses.PointOfDiversionEnforcementId;
import gov.mt.wris.models.PointOfDiversionEnforcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

public interface PointOfDiversionEnforcementRepository extends JpaRepository<PointOfDiversionEnforcement, PointOfDiversionEnforcementId> {
    public Page<PointOfDiversionEnforcement> findByPointOfDiversionId(Pageable pageable, BigDecimal pointOfDiversionId);

    @Modifying
    @Transactional
    public int deleteByPointOfDiversionIdAndEnforcementIdAndEnforcementNumber(BigDecimal podId, String enforcementId, String enforcementNumber);

    public Optional<PointOfDiversionEnforcement> findByPointOfDiversionIdAndEnforcementIdAndEnforcementNumber(BigDecimal podId, String enforcementId, String enforcementNumber);

    @Query(value = " SELECT pode\n" +
            " FROM PointOfDiversionEnforcement pode\n" +
            " LEFT JOIN FETCH pode.pointOfDiversion pod\n" +
            " LEFT JOIN FETCH pod.county c\n" +
            " LEFT JOIN FETCH pod.version v\n" +
            " LEFT JOIN FETCH v.waterRight w\n" +
            " LEFT JOIN FETCH pod.meansOfDiversion md\n" +
            " LEFT JOIN FETCH pod.majorTypeReference mj\n" +
            " LEFT JOIN FETCH pod.legalLandDescription ll\n" +
            " LEFT JOIN FETCH ll.trs trs\n" +
            " LEFT JOIN FETCH pod.ditch d\n" +
            " LEFT JOIN FETCH d.legalLandDescription dll\n" +
            " LEFT JOIN FETCH dll.trs dtrs\n" +
            " LEFT JOIN FETCH d.county dc\n" +
            " LEFT JOIN FETCH d.diversionType dt\n" +
            " WHERE pode.enforcementId = :enforcementId\n",
            countQuery = "SELECT COUNT(pode) FROM PointOfDiversionEnforcement pode WHERE pode.enforcementId = :enforcementId")
    public Page<PointOfDiversionEnforcement> findAllByEnforcementId(Pageable pageable, @Param("enforcementId") String enforcementId);

}
