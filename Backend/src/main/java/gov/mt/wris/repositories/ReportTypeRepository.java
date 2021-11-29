package gov.mt.wris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.ReportType;

@Repository
public interface ReportTypeRepository extends JpaRepository<ReportType, String> {
    List<ReportType> findByOrderByDescription();
}
