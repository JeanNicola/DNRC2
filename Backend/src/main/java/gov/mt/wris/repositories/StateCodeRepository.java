package gov.mt.wris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.mt.wris.models.StateCode;

public interface StateCodeRepository extends CrudRepository<StateCode, String> {
    @Query(value = "SELECT s\n"+
                    "FROM StateCode s\n" +
                    "ORDER BY CASE WHEN s.code = 'MT'\n" +
                        "THEN 0\n" +
                        "ELSE 1\n" +
                    "END,\n" +
                    "s.name")
    public List<StateCode> findAllSorted();
}