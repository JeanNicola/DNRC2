package gov.mt.wris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.mt.wris.models.BasinCompacts;

public interface BasinRepository extends JpaRepository<BasinCompacts, String>{
    public List<BasinCompacts> findByTypeOrderByCodeAsc(String type);

    public List<BasinCompacts> findAllByOrderByCodeAsc();
}
