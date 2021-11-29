package gov.mt.wris.repositories;

import gov.mt.wris.models.RetiredPlaceOfUse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface RetiredPlaceOfUseRepository extends JpaRepository<RetiredPlaceOfUse, BigDecimal>, CustomRetiredPlaceOfUseRepository  {

    @Query(value =  "SELECT ret \n" +
            "FROM RetiredPlaceOfUse ret \n" +
            "join fetch ret.legalLandDescription lld \n" +
            "join fetch ret.county c \n" +
            "join fetch lld.trs trs\n" +
            "left join fetch ret.modifiedReference mod\n" +
            "join fetch ret.elementOriginReference org\n" +
            "WHERE ret.purposeId = :purposeId ",
            countQuery = "SELECT count(ret.retiredPlaceId) \n" +
                    "FROM RetiredPlaceOfUse ret \n" +
                    "WHERE ret.purposeId = :purposeId ")
    public Page<RetiredPlaceOfUse> findByPurposeId(Pageable pageable, BigDecimal purposeId);

    public Optional<RetiredPlaceOfUse> findByRetiredPlaceIdAndPurposeId(BigDecimal retiredPlaceId, BigDecimal purposeId);

}
