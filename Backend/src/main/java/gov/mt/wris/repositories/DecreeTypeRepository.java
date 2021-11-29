package gov.mt.wris.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.mt.wris.models.DecreeType;

public interface DecreeTypeRepository extends JpaRepository<DecreeType, String> {

}