package gov.mt.wris.repositories;

import java.math.BigInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.ZipCodeDto;
import gov.mt.wris.dtos.ZipCodeSortColumn;
import gov.mt.wris.models.ZipCode;

public interface ZipCodeRepository extends CrudRepository<ZipCode, BigInteger>,
                                            CustomZipCodeRepository{
    public Page<ZipCodeDto> getZipCodes(Pageable pageable, ZipCodeSortColumn sortColumn, SortDirection sortDirection, String zipCode, String cityName, String stateCode);

    public void deleteById(BigInteger zipIdSeq);

    @Query(value = "SELECT case when count(a) > 0 then true else false end\n" +
                    "FROM Address a\n" +
                    "WHERE a.zipCodeId = :zipCodeId")
    public boolean existsInAddresses(@Param("zipCodeId") BigInteger zipCodeId);

}
