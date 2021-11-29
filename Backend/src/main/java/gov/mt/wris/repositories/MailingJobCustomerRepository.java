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
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.MailingJobCustomer;
import gov.mt.wris.models.OfficeCustomer;
import gov.mt.wris.models.IdClasses.MailingJobCustomerId;

@Repository
public interface MailingJobCustomerRepository extends JpaRepository<MailingJobCustomer, MailingJobCustomerId> {

    @Query(value = "SELECT mc\n" +
        "FROM MailingJobCustomer mc\n" +
        "JOIN FETCH mc.customer c\n" +
        "JOIN FETCH c.contactTypeValue t\n" +
        "WHERE mc.mailingJobId = :mailingJobId",
    countQuery = "SELECT count(mc)\n" +
        "FROM MailingJobCustomer mc\n" +
        "WHERE mc.mailingJobId = :mailingJobId")
    public Page<MailingJobCustomer> findByMailingJobId(Pageable pageable, BigDecimal mailingJobId);

    public Optional<MailingJobCustomer> findByMailingJobIdAndContactId(BigDecimal mailingJobId, BigDecimal contactId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO MailingJobCustomer(mailingJobId, contactId)\n" +
        "SELECT :mailingJobId, oc.contactId\n" +
        "FROM OfficeCustomer oc\n" +
        "WHERE oc.officeId = :officeId\n" +
        "AND oc.contactId not in :contactIds\n" +
        "AND oc.contactId not in (\n" +
            "SELECT mc.contactId\n" +
            "FROM MailingJobCustomer mc\n" +
            "WHERE mc.mailingJobId = :mailingJobId\n" +
        ")\n")
    public int addPartiesByOfficeInclusive(BigDecimal mailingJobId, BigDecimal officeId, List<BigDecimal> contactIds);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO MailingJobCustomer(mailingJobId, contactId)\n" +
        "SELECT :mailingJobId, oc.contactId\n" +
        "FROM OfficeCustomer oc\n" +
        "WHERE oc.officeId = :officeId\n" +
        "AND oc.contactId in :contactIds\n" +
        "AND oc.contactId not in (\n" +
            "SELECT mc.contactId\n" +
            "FROM MailingJobCustomer mc\n" +
            "WHERE mc.mailingJobId = :mailingJobId\n" +
        ")\n")
    public int addPartiesByOfficeExclusive(BigDecimal mailingJobId, BigDecimal officeId, List<BigDecimal> contactIds);

    @Transactional
    @Modifying
    public int deleteByMailingJobId(BigDecimal mailingJobId);

    @Transactional
    @Modifying
    public int deleteByMailingJobIdAndContactId(BigDecimal mailingJobId, BigDecimal contactId);

    @Modifying
    @Query(value = "INSERT INTO MailingJobCustomer(mailingJobId, contactId)\n" +
        "SELECT :mailingJobId, oc.contactId\n" +
        "FROM MailingJob m\n" +
        "JOIN m.application a\n" +
        "JOIN OfficeCustomer oc\n" +
        "ON oc.officeId = a.officeId\n"+
        "WHERE m.id = :mailingJobId\n" +
        "AND oc.defaultParty = 'Y'")
    public int addDefaultInterestedParties(BigDecimal mailingJobId);

    @Query(value = "SELECT oc\n" +
        "FROM OfficeCustomer oc\n" +
        "JOIN FETCH oc.customer c\n" +
        "JOIN FETCH c.contactTypeValue t\n" +
        "WHERE oc.officeId = :officeId\n" +
        "AND oc.contactId not in (\n"+
            "SELECT mc.contactId\n" +
            "FROM MailingJobCustomer mc\n" +
            "WHERE mc.mailingJobId = :mailingJobId\n" +
        ")\n",
    countQuery = "SELECT count(oc)\n" +
        "FROM OfficeCustomer oc\n" +
        "WHERE oc.officeId = :officeId\n" +
        "AND oc.contactId not in (\n"+
            "SELECT mc.contactId\n" +
            "FROM MailingJobCustomer mc\n" +
            "WHERE mc.mailingJobId = :mailingJobId\n" +
        ")\n"
    )
    public Page<OfficeCustomer> findByOfficeId(Pageable pageable, BigDecimal mailingJobId, BigDecimal officeId);
}
