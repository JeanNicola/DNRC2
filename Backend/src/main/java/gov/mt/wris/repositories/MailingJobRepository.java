package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.MailingJobSortColumn;
import gov.mt.wris.models.MailingJob;

@Repository
public interface MailingJobRepository extends JpaRepository<MailingJob, BigDecimal>, CustomMailingJobRepository {
    public Page<MailingJob> searchMailingJobs(Pageable pageable,
        MailingJobSortColumn sortColumn,
        DescSortDirection sortDirection,
        String mailingJobNumber,
        String mailingJobHeader,
        String applicationId);

    @Query(value = "SELECT m\n" +
        "FROM MailingJob m\n" +
        "JOIN FETCH m.application a\n" +
        "JOIN FETCH a.type t\n" +
        "WHERE m.id = :id")
    public Optional<MailingJob> findWithApplication(BigDecimal id);

    @Query(value = "SELECT COUNT(c)\n" +
        "FROM MailingJobCustomer c\n" +
        "WHERE c.mailingJobId = :id\n")
    public int countInterestedParties(BigDecimal id);

    @Query(value = "SELECT COUNT(c)\n" +
        "FROM MailingJobWaterRight c\n" +
        "WHERE c.mailingJobId = :id\n")
    public int countWaterRights(BigDecimal id);
}
