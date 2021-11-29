package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.ApplicationOwnshipXref;
import gov.mt.wris.models.OwnershipUpdate;
import gov.mt.wris.models.WaterRighOwnshiptXref;

@Repository
public interface OwnershipUpdateRepository extends JpaRepository<OwnershipUpdate, BigDecimal>, CustomOwnershipUpdateRepository {

    @Query(value = "SELECT o\n" +
                "FROM OwnershipUpdate o\n" +
                "JOIN FETCH o.updateTypeValue ot\n" +
                "LEFT JOIN FETCH o.pendingDorVal p\n" +
                "LEFT JOIN FETCH o.receivedAs608Val r\n" +
                "WHERE o.ownerUpdateId = :ownershipUpdateId")
    public Optional<OwnershipUpdate> getOwnershipUpdate(@Param("ownershipUpdateId") BigDecimal ownershipUpdateId);

    @Query(value = "SELECT wr from WaterRighOwnshiptXref wr\n" +
                    "join wr.ownershipUpdate\n" +
                    "join fetch wr.waterRight w\n" +
                    "join fetch w.validity valid\n" +
                    "join fetch w.waterRightType wt\n" +
                    "left join fetch w.waterRightStatus ws\n" +
                    "left join fetch w.originalWaterRight ow\n" +
                    "left join fetch ow.waterRightType owt\n" +
                    "left join fetch ow.waterRightStatus ows\n" +
                    "WHERE wr.ownershipUpdate.ownerUpdateId = :ownerUpdateId",
            countQuery = "SELECT count(wr.ownershipUpdate.ownerUpdateId) from WaterRighOwnshiptXref wr join wr.ownershipUpdate WHERE wr.ownershipUpdate.ownerUpdateId = :ownerUpdateId")
    public Page<WaterRighOwnshiptXref> getOwnershipUpdateWaterRights(Pageable pageable, BigDecimal ownerUpdateId);

    @Query(value = "SELECT count(wr.waterRight) from WaterRighOwnshiptXref wr join wr.ownershipUpdate WHERE wr.ownershipUpdate.ownerUpdateId = :ownerUpdateId AND wr.waterRight.waterRightStatusCode NOT IN ('WDRN', 'SSPD', 'PEND')")
    public BigDecimal getActiveOwnershipUpdateWaterRightsCount(BigDecimal ownerUpdateId);

    @Query(value = "SELECT appl from ApplicationOwnshipXref appl join appl.ownershipUpdate join fetch appl.application a WHERE appl.ownershipUpdate.ownerUpdateId = :ownerUpdateId",
            countQuery = "SELECT count(appl.ownershipUpdate.ownerUpdateId) from ApplicationOwnshipXref appl join appl.ownershipUpdate WHERE appl.ownershipUpdate.ownerUpdateId = :ownerUpdateId")
    public Page<ApplicationOwnshipXref> getOwnershipUpdateApplications(Pageable pageable, BigDecimal ownerUpdateId);

    public Optional<OwnershipUpdate> getOwnershipUpdateByOwnerUpdateId(BigDecimal ownerUpdateId);

    @Query(value = "" +
        "SELECT o\n" +
        "FROM OwnershipUpdate o\n" +
        "LEFT JOIN FETCH o.office oo\n" +
        "WHERE o.id = :ownershipUpdateId")
    public Optional<OwnershipUpdate> findOwnershipUpdateWithOffice(@Param("ownershipUpdateId") BigDecimal id);

    @Query(value = "" +
        "SELECT o\n" +
        "FROM OwnershipUpdate o\n" +
        "LEFT JOIN FETCH o.processorOffice oo\n" +
        "LEFT JOIN FETCH o.processorStaff os\n" +
        "WHERE o.id = :ownershipUpdateId")
    public Optional<OwnershipUpdate> findOwnershipUpdateWithProcessor(@Param("ownershipUpdateId") BigDecimal id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO WRD_APPL_OWNSHIP_UPDT_XREFS\n" +
                "(appl_id_seq, ownr_updt_id)\n" +
                "SELECT DISTINCT vax.appl_id_seq,\n" +
                                "woux.ownr_updt_id\n" +
                "FROM wrd_wtr_rgt_ownship_updt_xrefs woux\n" +
                "JOIN wrd_version_application_xrefs vax\n" +
                "on woux.wrgt_id_seq = vax.wrgt_id_seq\n" +
                "JOIN wrd_applications appl\n" +
                "ON vax.appl_id_seq = appl.appl_id_seq\n" +
                "JOIN wrd_event_dates evdt\n" +
                "ON appl.appl_id_seq = evdt.appl_id_seq\n" +
                "WHERE appl.aptp_cd IN ('606', '634', '635', '644')\n" +
                "AND evdt.evtp_cd = 'ISSU'\n" +
                // make sure at least one of the sellers exists
                // on the application
                "AND EXISTS (\n" +
                    "SELECT o.cust_id_seq\n" +
                    "FROM wrd_owners o\n" +
                    "WHERE o.appl_id_seq = vax.appl_id_seq\n" +
                    "AND o.end_dt is null\n" +
                    "AND o.cust_id_seq in (\n" +
                        "SELECT coux.cust_id_seq\n" +
                        "FROM wrd_custm_ownership_updt_xrefs coux\n" +
                        "WHERE coux.ownr_updt_id = woux.ownr_updt_id\n" +
                        "AND coux.role = 'SEL'\n" +
                    ")\n" +
                ")\n" +
                "AND NOT EXISTS (\n" +
                    "SELECT aoux.appl_id_seq\n" +
                    "FROM wrd_appl_ownship_updt_xrefs aoux\n" +
                    "WHERE aoux.ownr_updt_id = woux.ownr_updt_id\n" +
                    "AND aoux.appl_id_seq = vax.appl_id_seq\n" +
                ")\n" +
                "AND woux.ownr_updt_id = :ownerUpdateId",
            nativeQuery = true)
    public int addMissingChangeApplications(@Param("ownerUpdateId") BigDecimal ownerUpdateId);
    
    @Query(value = "SELECT ou\n" +
                "FROM OwnershipUpdate ou\n" +
                "inner join fetch ou.updateTypeValue t\n" +
                "inner join ou.waterRights w\n" +
                "WHERE w.waterRightId = :waterRightId\n" +
                "  AND ou.trnType <> 'ADM'",
            countQuery = "SELECT COUNT(ou)\n" +
                    "FROM OwnershipUpdate ou\n" +
                    "inner join ou.waterRights w\n" +
                    "WHERE w.waterRightId = :waterRightId\n" +
                    "  AND ou.trnType <> 'ADM'")
    public Page<OwnershipUpdate> findByWaterRightId(Pageable pageable, @Param("waterRightId") BigDecimal waterRightId);
    
    @Query(
        value = "SELECT ou\n" +
                "FROM OwnershipUpdate ou\n" +
                "LEFT JOIN FETCH ou.updateTypeValue udv\n" +
                "WHERE ou.ownerUpdateId IN (\n" +
                "    SELECT ownerUpdateId\n" +
                "    FROM CustomerXref xref\n" +
                "    WHERE customerId = :customerId\n" +
                ")"
                ,
                countQuery = "SELECT COUNT(ou)\n" +
                "FROM OwnershipUpdate ou\n" +
                "WHERE ou.ownerUpdateId IN (\n" +
                "    SELECT ownerUpdateId\n" +
                "    FROM CustomerXref xref\n" +
                "    WHERE customerId = :customerId\n" +
                ")"
    )
    public Page<OwnershipUpdate> getCustomerOwnershipUpdates(Pageable pageable, BigDecimal customerId);

}

