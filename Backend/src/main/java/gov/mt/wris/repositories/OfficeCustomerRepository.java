package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.OfficeCustomer;
import gov.mt.wris.models.IdClasses.OfficeCustomerId;

@Repository
public interface OfficeCustomerRepository extends JpaRepository<OfficeCustomer, OfficeCustomerId> {
}
