package gov.mt.wris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.MinorType;

@Repository
public interface MinorTypeRepository extends JpaRepository<MinorType, String> {
    public List<MinorType> findAllByOrderByDescription();
}
