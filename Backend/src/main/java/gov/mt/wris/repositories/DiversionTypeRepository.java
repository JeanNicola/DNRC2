package gov.mt.wris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.DiversionType;

@Repository
public interface DiversionTypeRepository extends JpaRepository<DiversionType, String> {
    public List<DiversionType> findAllByOrderByDescription();
}
