package gov.mt.wris.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.mt.wris.models.ApplicationType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationTypeRepository extends JpaRepository<ApplicationType, String> {

    Optional<ApplicationType> findApplicationTypeByCode(String code);
}