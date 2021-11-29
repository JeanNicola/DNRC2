package gov.mt.wris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.mt.wris.models.MeansOfDiversion;

public interface MeansOfDiversionRepository extends JpaRepository<MeansOfDiversion, String> {
    public List<MeansOfDiversion> findByOrderByDescription();
}
