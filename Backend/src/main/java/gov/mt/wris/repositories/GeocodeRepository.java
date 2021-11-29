package gov.mt.wris.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.models.Geocode;


public interface GeocodeRepository extends JpaRepository<Geocode, String> {
    @Query(value = "SELECT g.geocodeId\n" +
                    "FROM Geocode g\n" +
                    "WHERE g.geocodeId in :geocodeList")
    List<String> getGeocodeIds(@Param("geocodeList") List<String> geocodeList);

    @Modifying
    @Transactional
    @Procedure(procedureName = "WRD.WRD_GEOCODE_INSERT")
    void insertNewGeocode(@Param("i_GEO_CD") String geocode);
}
