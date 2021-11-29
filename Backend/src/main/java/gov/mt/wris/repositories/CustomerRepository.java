package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.Customer;
import gov.mt.wris.models.CustomerXref;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, BigDecimal>, CustomCustomerRepository {

    public Optional<Customer> getCustomersByCustomerId(BigDecimal id);

    @Query(
        value = "SELECT xref\n" +
                "FROM CustomerXref xref\n" +
                "INNER JOIN FETCH xref.ownershipUpdate ownership\n" +
                "LEFT JOIN FETCH xref.conttForDeedValue cfd\n" +
                "LEFT JOIN FETCH ownership.updateTypeValue udv\n" +
                "WHERE xref.customerId = :customerId\n" +
                "   AND (xref.role = :role)",
        countQuery = "SELECT COUNT(xref)\n" +
                "FROM CustomerXref xref\n" +
                "WHERE xref.customerId = :customerId\n" +
                "   AND (xref.role = :role)"
    )
    public Page<CustomerXref> getCustomerOwnershipUpdatesByRole(Pageable pageable, BigDecimal customerId, String role);


    @Query(value = "SELECT c.customerId\n" +
                    "FROM Customer c\n" +
                    "WHERE c.customerId in :customerList")
    List<BigDecimal> getCustomerId(@Param("customerList") List<BigDecimal> customerList);

}
