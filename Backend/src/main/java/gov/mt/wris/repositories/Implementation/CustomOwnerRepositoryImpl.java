package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.ApplicantDto;
import gov.mt.wris.dtos.ApplicantSortColumn;
import gov.mt.wris.repositories.CustomOwnerRepository;
import gov.mt.wris.utils.NativeLocalDate;
import gov.mt.wris.utils.PagedQueryBuilder;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomOwnerRepositoryImpl implements CustomOwnerRepository {
    public static Logger LOGGER = LoggerFactory.getLogger(CustomOwnerRepository.class);

    @PersistenceContext
    EntityManager manager;

    @Autowired
    PagedQueryBuilder pagedQueryBuilder;

    private static Sort getApplicantSort(
        ApplicantSortColumn column,
        Sort.Direction direction
    ) {

        Sort sort = null;

        switch (column) {
            case BEGINDATE:
                sort = Sort
                    .by(direction, "owner.BGN_DT");
                break;
            case ENDDATE:
                sort = Sort
                    .by(direction, "owner.END_DT");
                break;
            case FULLNAME:
                sort = Sort
                    .by(direction, "customer.LST_NM_OR_BUSN_NM")
                    .and(Sort.by(direction, "customer.FST_NM"))
                    .and(Sort.by(direction, "customer.MID_INT"))
                    .and(Sort.by(direction, "customer.SUFX"));
                break;
            case CONTACTID:
                sort = Sort.by(direction, "owner.CUST_ID_SEQ");
                break;
        }

        if (EnumUtils.isValidEnum(ApplicantSortColumn.class, column.toString()) && !ApplicantSortColumn.FULLNAME.equals(column)) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "customer.LST_NM_OR_BUSN_NM"))
                    .and(Sort.by(Sort.Direction.ASC, "customer.FST_NM"))
                    .and(Sort.by(Sort.Direction.ASC, "customer.MID_INT"))
                    .and(Sort.by(Sort.Direction.ASC, "customer.SUFX"));
        }

        if (EnumUtils.isValidEnum(ApplicantSortColumn.class, column.toString()) && !ApplicantSortColumn.CONTACTID.equals(column)) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "owner.CUST_ID_SEQ"));
        }

        return sort;
    }

    private static final String findApplicantsByApplicationIdSql =
        "SELECT\n" +
            "owner.OWNR_ID_SEQ,\n" +
            "owner.CUST_ID_SEQ,\n" +
            "owner.BGN_DT,\n" +
            "owner.END_DT,\n" +
            "customer.LST_NM_OR_BUSN_NM,\n" +
            "customer.FST_NM,\n" +
            "customer.MID_INT,\n" +
            "customer.SUFX,\n" +
            "reps.END_DT AS LATEST_END_DT,\n" +
            "reps.TOTAL\n" +
        "FROM WRD_OWNERS owner\n" +
        "INNER JOIN WRD_CUSTOMERS customer\n" +
        "ON customer.CUST_ID_SEQ = owner.CUST_ID_SEQ\n" +
        "LEFT JOIN (\n" +
            "SELECT\n" +
                "rep.OWNR_ID_SEQ,\n" +
                "rep.CUST_ID_SEQ_SEC,\n" +
                "COUNT(rep.REPT_ID_SEQ) AS total,\n" +
                "MAX(rep.END_DT) AS end_dt\n" +
            "FROM WRD_REPRESENTATIVES rep\n" +
            "GROUP BY rep.OWNR_ID_SEQ, rep.CUST_ID_SEQ_SEC\n" +
        ") reps\n" +
        "ON reps.OWNR_ID_SEQ = owner.OWNR_ID_SEQ\n" +
        "AND reps.CUST_ID_SEQ_SEC = owner.CUST_ID_SEQ\n" +
        "WHERE owner.APPL_ID_SEQ = :applicationId";

    private static final String findApplicantsByApplicationIdCountSql =
        "SELECT COUNT(owner.OWNR_ID_SEQ)\n" +
        "FROM WRD_OWNERS owner\n" +
        "WHERE owner.APPL_ID_SEQ = :applicationId";

    @Override
    public Page<ApplicantDto> findApplicantsByApplicationId(
        Pageable pageable,
        BigDecimal applicationId,
        ApplicantSortColumn column,
        Sort.Direction direction
    ) {
        LOGGER.info("Searching all Applicants on an Application");

        Query query = pagedQueryBuilder
            .createNativeQuery(
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    getApplicantSort(column, direction)
                ),
                findApplicantsByApplicationIdSql
            )
            .setParameter("applicationId", applicationId);

        List<Object[]> raw = query.getResultList();
        List<ApplicantDto> results = raw
            .stream()
            .map(result ->
                new ApplicantDto()
                    .ownerId(((BigDecimal) result[0]).longValue())
                    .contactId(((BigDecimal) result[1]).longValue())
                    .beginDate(NativeLocalDate.cast(result[2]))
                    .endDate(NativeLocalDate.cast(result[3]))
                    .lastName((String) result[4])
                    .firstName((String) result[5])
                    .middleInitial((String) result[6])
                    .suffix((String) result[7])
                    .latestRepresentativeEndDate(NativeLocalDate.cast(result[8]))
                    .representativeCount(
                        result[9] != null
                            ? ((BigDecimal) result[9]).longValue()
                            : 0L
                    )
            )
            .collect(Collectors.toList());

        BigDecimal count = (BigDecimal) manager
            .createNativeQuery(findApplicantsByApplicationIdCountSql)
            .setParameter("applicationId", applicationId)
            .getSingleResult();

        return new PageImpl<>(results, pageable, count.longValue());
    }
}
