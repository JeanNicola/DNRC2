package gov.mt.wris.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.City;
import gov.mt.wris.models.ZipCode;

@Repository
public interface CityRepository extends CrudRepository<City, BigInteger>{
    public List<City> findByCityNameAndStateCode(String cityName, String stateCode);

    @Query(value = "SELECT z\n" +
                    "from ZipCode z\n" +
                    "inner join City c\n" +
                    "on c.cityId = z.cityIdSeq\n" +
                    "WHERE z.zipCode = :zipCode\n" +
                    "AND c.cityName = :cityName\n" +
                    "AND c.stateCode = :stateCode")
    public List<ZipCode> findByZipCodeAndCityNameAndStateCode(@Param("zipCode") String zipCode,
                                            @Param("cityName") String cityName,
                                            @Param("stateCode") String stateCode);

    @Modifying
    @Query(value = "DELETE from WRD_CITIES where rowid in (\n" +
                    "SELECT c.rowid FROM WRD_CITIES c\n" +
                    "left join WRD_ZIP_CODES z\n" +
                    "on c.CITY_ID_SEQ = z.CITY_ID_SEQ\n" +
                    "left join WRD_WATER_COURT_CASES w\n" +
                    "on w.CITY_ID_SEQ = c.CITY_ID_SEQ\n" +
                    "WHERE c.CITY_ID_SEQ = :cityId\n" +
                    "AND z.ZPCD_ID_SEQ is null\n" +
                    "AND w.CASE_ID_SEQ is null)",
            nativeQuery = true)
    public void deleteByIdIfNoZipCodesOrCases(@Param("cityId") long cityId);

    @Query(value = "select case when count(z) > 0 then true else false end\n" +
                    "FROM ZipCode z\n" +
                    "WHERE z.cityIdSeq = :cityId")
    public boolean existsByZipCode(@Param("cityId") BigInteger cityId);
    @Query(value = "select case when count(c) > 0 then true else false end\n" +
                    "FROM CourtCase c\n" +
                    "WHERE c.cityId = :cityId")
    public boolean existsByCourtCase(@Param("cityId") BigDecimal cityId);
}
