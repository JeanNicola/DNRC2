package gov.mt.wris.repositories;

import gov.mt.wris.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Repository
public interface AddressRepository extends JpaRepository<Address, BigInteger>, CustomAddressRepository {

    Optional<Address> getAddressByAddressId(BigInteger id);
    List<Address> findAllByCustomerId(BigInteger customerId);
    List<Address> findAllByPointOfDiversionId(BigDecimal pointOfDiversionId);

    public Boolean existsByPointOfDiversionId(BigDecimal pointOfDiversionId);

    @Transactional
    @Modifying
    @Query("UPDATE Address a\n" +
        "SET a.addressLine1 = :addressLine1,\n" +
            "a.zipCodeId = :zipCodeId\n" +
        "WHERE a.pointOfDiversionId = :podId")
    public int updateAddress(BigDecimal podId, String addressLine1, BigInteger zipCodeId);

    @Transactional
    @Modifying
    public int deleteByPointOfDiversionId(BigDecimal pointOfDiversionId);
}
