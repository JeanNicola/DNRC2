package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gov.mt.wris.models.Office;

public interface OfficeRepository extends JpaRepository<Office, BigDecimal> {
    List<Office> findAllByOrderByDescriptionAsc();

    @Query(value = "SELECT DISTINCT o\n" +
        "FROM OfficeCustomer oc\n" +
        "JOIN oc.office o\n" +
        "order by o.description asc")
    public List<Office> findAllRegionalOffices();
}
