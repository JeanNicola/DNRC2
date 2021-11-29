package gov.mt.wris.repositories;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.CustomerXref;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustomerXrefRepository extends JpaRepository<CustomerXref, BigDecimal> {
    
    @Modifying
    @Transactional
    @Query(value = "DELETE\n" +
                    "FROM CustomerXref cx\n" +
                    "WHERE cx.couxIdSeq in (\n" +
                        "SELECT x.couxIdSeq\n" +
                        "FROM CustomerXref x\n" +
                        "JOIN x.ownershipUpdate ou\n" +
                        "WHERE ou.ownerUpdateId = :ownershipUpdateId\n" +
                    ")\n")
    public int deleteByOwnershipUpdateId(@Param("ownershipUpdateId") BigDecimal ownershipUpdateId);

    @Query(value = "SELECT cx \n" +
            "FROM CustomerXref cx\n" +
            "WHERE cx.ownershipUpdate.ownerUpdateId = :ownershipUpdateId and cx.customerId = :customerId and cx.role = :customerRole \n")
    public CustomerXref findCustomerXrefByOwnershipUpdateIdAndCustomerIdAndRole(BigDecimal ownershipUpdateId, BigDecimal customerId, String customerRole);

    @Query(value = "SELECT cx \n" +
            "FROM CustomerXref cx\n" +
            "WHERE cx.ownershipUpdate.ownerUpdateId = :ownershipUpdateId and cx.role = :customerRole \n")
    public List<CustomerXref> findCustomerXrefByOwnershipUpdateIdAndRole(BigDecimal ownershipUpdateId, String customerRole);

    public int deleteByCouxIdSeqIn(List<BigDecimal> couxIdsSeq);

    @Query(value = "SELECT cx.customerId\n" +
                "FROM CustomerXref cx\n" +
                "WHERE cx.customerId in :customerList\n" +
                "AND cx.role = :customerRole\n" +
                "AND cx.ownershipUpdate.ownerUpdateId = :ownershipUpdateId")
    List<BigDecimal> getCustomerIds(BigDecimal ownershipUpdateId, List<BigDecimal> customerList, String customerRole);

    @Query(value = "SELECT NVL(ref." + Constants.MEANING + ", 'NO')\n" +
                "FROM " + Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE + " cx\n" +
                "LEFT JOIN " + Constants.REFERENCE_TABLE + " ref\n" +
                "ON ref." + Constants.DOMAIN + " = '" + Constants.CONTRACT_FOR_DEED_RLE_DOMAIN + "'\n" +
                "    AND ref." + Constants.LOW_VALUE + " = cx." + Constants.CONTT_FOR_DEED + "\n" +
                "WHERE cx." + Constants.OWNR_UPDT_ID + " = :ownerUpdateId\n" +
                "FETCH FIRST ROW ONLY\n",
                nativeQuery = true)
    public String getFirstConttForDeedByOwnerUpdateId(BigDecimal ownerUpdateId);
}
