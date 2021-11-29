package gov.mt.wris.repositories;

import gov.mt.wris.models.CustomerTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerTypesRepository extends JpaRepository<CustomerTypes, String> {

    @Query("SELECT DISTINCT ct FROM CustomerTypes ct ORDER BY ct.description")
    public List<CustomerTypes> findAllCustomerTypes();

}
