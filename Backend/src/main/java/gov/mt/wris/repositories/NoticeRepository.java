package gov.mt.wris.repositories;

import gov.mt.wris.models.MailingJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface NoticeRepository extends JpaRepository<MailingJob, String> {

    public Page<MailingJob> findAllByApplicationId(Pageable pageable, @Param("applicationId") BigDecimal appId);

}
