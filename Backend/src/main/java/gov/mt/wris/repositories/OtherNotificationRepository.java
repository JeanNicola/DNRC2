package gov.mt.wris.repositories;

import gov.mt.wris.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OtherNotificationRepository extends JpaRepository<Customer, String> {

    @Query(value = "" +
            "select c\n" +
            "from Customer c \n" +
            "join fetch c.mailingXrefs ref\n" +
            "where ref.mailingJobId=:mailingJobId\n" +
            "and ref.mailingJob.applicationId=:applicationId\n",
            countQuery = "" +
                    "select count(c) \n" +
                    "from Customer c \n" +
                    "left join c.mailingXrefs ref\n" +
                    "where ref.mailingJobId=:mailingJobId\n" +
                    "and ref.mailingJob.applicationId=:applicationId\n"
            )
    public Page<Customer> findOtherNotificationByAppId(Pageable pageable, @Param("applicationId") BigDecimal id, @Param("mailingJobId") BigDecimal mailingJobId);
}
