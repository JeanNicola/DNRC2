package gov.mt.wris.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.Ditch;

@Repository
public interface DitchRepository extends JpaRepository<Ditch, BigDecimal> {
    @Query(value = "SELECT d\n" +
        "FROM Ditch d\n" +
        "LEFT JOIN FETCH d.county c\n" +
        "LEFT JOIN FETCH d.legalLandDescription ll\n" +
        "LEFT JOIN FETCH ll.trs t\n" +
        "WHERE d.name like :name\n",
        countQuery = "SELECT COUNT(d)\n" +
            "FROM Ditch d\n" +
            "WHERE d.name like :name")
    public Page<Ditch> searchDitches(Pageable pageable, String name);
}
