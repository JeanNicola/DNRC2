package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesSortColumn;
import gov.mt.wris.dtos.WaterRightVersionSortColumn;
import gov.mt.wris.dtos.WaterRightVersionsForRelatedRightSortColumn;
import gov.mt.wris.models.Reference;
import gov.mt.wris.models.RelatedRightVerXref;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightType;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.repositories.CustomWaterRightVersionRepository;
import gov.mt.wris.utils.Helpers;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomWaterRightVersionRepositoryImpl implements CustomWaterRightVersionRepository {
    public static Logger LOGGER = LoggerFactory.getLogger(CustomWaterRightVersionRepository.class);

    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<WaterRightVersion> getWaterRightVersionsAll(Pageable pageable, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection, String basin, String waterRightNumber, String ext) {

        LOGGER.info("Search all Water Rights Versions");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<WaterRightVersion> query = cb.createQuery(WaterRightVersion.class);

        Root<WaterRightVersion> v = query.from(WaterRightVersion.class);
        Join w = (Join) v.fetch("waterRight", JoinType.INNER);
        Join type = (Join) w.fetch("waterRightType", JoinType.INNER);
        Join status = (Join) w.fetch("waterRightStatus", JoinType.LEFT);

        query.where(getWaterRightVersionsForRelatedRightReferencePredicates(cb, v, w, basin, waterRightNumber, ext, null));

        List<Order> orders = getWaterRightVersionsForRelatedRightReferenceOrderBy(cb, v, w, type, status, sortColumn, sortDirection);
        query.orderBy(orders);

        List<WaterRightVersion> result = manager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<WaterRightVersion> vc = cq.from(WaterRightVersion.class);
        Join<WaterRightVersion, WaterRight> wc = vc.join("waterRight", JoinType.INNER);
        Join<WaterRight, WaterRightType> wt = wc.join("waterRightType", JoinType.INNER);

        cq.where(getWaterRightVersionsForRelatedRightReferencePredicates(cb, vc, wc, basin, waterRightNumber, ext, null));

        cq.select(cb.count(vc));
        Long count = manager.createQuery(cq).getSingleResult();
        Page<WaterRightVersion> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;

    }

    @Override
    public Page<WaterRightVersion> getWaterRightVersionsForRelatedRightReference(Pageable pageable, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection, Long relatedRightId, String basin, String waterRightNumber, String ext) {

        LOGGER.info("Search for Water Rights Versions not Associated with Related Right");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<WaterRightVersion> query = cb.createQuery(WaterRightVersion.class);

        Root<WaterRightVersion> v = query.from(WaterRightVersion.class);
        Join w = (Join) v.fetch("waterRight", JoinType.INNER);
        Join type = (Join) w.fetch("waterRightType", JoinType.INNER);
        Join status = (Join) w.fetch("waterRightStatus", JoinType.LEFT);

        Subquery<RelatedRightVerXref> subQuery = query.subquery(RelatedRightVerXref.class);
        Root<RelatedRightVerXref> subRoot = subQuery.from(RelatedRightVerXref.class);
        subQuery.select(subRoot).where(
            cb.equal(subRoot.get("relatedRightId"), new BigDecimal(relatedRightId)),
            cb.equal(subRoot.get("waterRightId"), v.get("waterRightId")),
            cb.equal(subRoot.get("versionId"), v.get("version"))
        );
        query.where(getWaterRightVersionsForRelatedRightReferencePredicates(cb, v, w, basin, waterRightNumber, ext, subQuery));

        List<Order> orders = getWaterRightVersionsForRelatedRightReferenceOrderBy(cb, v, w, type, status, sortColumn, sortDirection);
        query.orderBy(orders);

        List<WaterRightVersion> result = manager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<WaterRightVersion> vc = cq.from(WaterRightVersion.class);
        Join<WaterRightVersion, WaterRight> wc = vc.join("waterRight", JoinType.INNER);
        Join<WaterRight, WaterRightType> wt = wc.join("waterRightType", JoinType.INNER);

        Subquery<Long> sq = query.subquery(Long.class);
        Root<RelatedRightVerXref> sqr = sq.from(RelatedRightVerXref.class);
        sq.select(sqr.get("waterRightId")).where(cb.equal(sqr.get("relatedRightId"), new BigDecimal(relatedRightId)));
        cq.where(getWaterRightVersionsForRelatedRightReferencePredicates(cb, vc, wc, basin, waterRightNumber, ext, sq));

        cq.select(cb.count(vc));
        Long count = manager.createQuery(cq).getSingleResult();
        Page<WaterRightVersion> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;

    }

    private Predicate[] getWaterRightVersionsForRelatedRightReferencePredicates(CriteriaBuilder cb, Root<WaterRightVersion> v, Join w, String basin, String waterRightNumber, String ext, Subquery subQuery) {

        List<Predicate> predicates = new ArrayList<>();

        if(basin != null) {
            predicates.add(cb.like(w.get("basin"), basin));
        }
        if(waterRightNumber != null) {
            predicates.add(cb.like(w.get("waterRightNumber").as(String.class), waterRightNumber));
        }
        if(ext != null) {
            predicates.add(cb.like(w.get("ext").as(String.class), ext));
        }
        predicates.add(v.get("typeCode").in(Arrays.asList("ORIG", "POST", "SPPD", "SPLT", "REXM")));
        if (subQuery != null) {
            predicates.add(cb.exists(subQuery).not());
        }

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }

    private List<Order> getWaterRightVersionsForRelatedRightReferenceOrderBy(CriteriaBuilder cb, Root<WaterRightVersion> v, Join w, Join type, Join status, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection) {

        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if(sortColumn == WaterRightVersionsForRelatedRightSortColumn.BASIN) {
                orderList.add(cb.asc(w.get("basin")));
            } else if (sortColumn == WaterRightVersionsForRelatedRightSortColumn.VERSION) {
                orderList.add(cb.asc(v.get("typeCode")));
                orderList.add(cb.asc(v.get("version")));
            } else if (sortColumn == WaterRightVersionsForRelatedRightSortColumn.EXT) {
                orderList.add(cb.asc(w.get("ext")));
            } else if (sortColumn == WaterRightVersionsForRelatedRightSortColumn.TYPEDESCRIPTION) {
                orderList.add(cb.asc(type.get("description")));
            } else if (sortColumn == WaterRightVersionsForRelatedRightSortColumn.STATUS) {
                orderList.add(cb.asc(status.get("description")));
            } else { // WaterRightVersionsForRelatedRightSortColumn.COMPLETEWATERRIGHTNUMBER)
                orderList.add(cb.asc(w.get("basin")));
                orderList.add(cb.asc(w.get("waterRightNumber")));
                orderList.add(cb.asc(w.get("ext")));
            }
        } else {
            if(sortColumn == WaterRightVersionsForRelatedRightSortColumn.BASIN) {
                orderList.add(cb.desc(w.get("basin")));
            } else if (sortColumn == WaterRightVersionsForRelatedRightSortColumn.VERSION) {
                orderList.add(cb.desc(v.get("typeCode")));
                orderList.add(cb.desc(v.get("version")));
            } else if (sortColumn == WaterRightVersionsForRelatedRightSortColumn.EXT) {
                orderList.add(cb.desc(w.get("ext")));
            } else if (sortColumn == WaterRightVersionsForRelatedRightSortColumn.TYPEDESCRIPTION) {
                orderList.add(cb.desc(type.get("description")));
            } else if (sortColumn == WaterRightVersionsForRelatedRightSortColumn.STATUS) {
                orderList.add(cb.desc(status.get("description")));
            } else { // WaterRightVersionsForRelatedRightSortColumn.COMPLETEWATERRIGHTNUMBER)
                orderList.add(cb.desc(w.get("basin")));
                orderList.add(cb.desc(w.get("waterRightNumber")));
                orderList.add(cb.desc(w.get("ext")));
            }
        }
        if (!sortColumn.equals(WaterRightVersionsForRelatedRightSortColumn.COMPLETEWATERRIGHTNUMBER)) {
            orderList.add(cb.asc(w.get("basin")));
            orderList.add(cb.asc(w.get("waterRightNumber")));
            orderList.add(cb.asc(w.get("ext")));
        }
        return orderList;

    }

    @Override
    public Page<WaterRightVersion> getWaterRightVersions(Pageable pageable, WaterRightVersionSortColumn sortColumn, DescSortDirection sortDirection, Long waterRightId, String basin, String waterRightNumber, String versionNumber, String versionType) {
        LOGGER.info("Searching for Water Right Versions");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<WaterRightVersion> query = cb.createQuery(WaterRightVersion.class);

        Root<WaterRightVersion> v = query.from(WaterRightVersion.class);
        Join type = (Join) v.fetch("typeReference", JoinType.INNER);
        Join w = (Join) v.fetch("waterRight", JoinType.INNER);
        Join waterType = (Join) w.fetch("waterRightType", JoinType.INNER);
        Join status = (Join) v.fetch("versionStatus", JoinType.LEFT);
        Join waterStatus = (Join) w.fetch("waterRightStatus", JoinType.LEFT);

        query.where(getWaterRightVersionPredicates(cb, v, w, type, waterRightId, basin, waterRightNumber, versionNumber, versionType));

        List<Order> orders = getOrders(cb, v, w, type, status, waterType, waterStatus, sortColumn, sortDirection);
        query.orderBy(orders);

        List<WaterRightVersion> result = manager.createQuery(query)
                                                    .setFirstResult((int) pageable.getOffset())
                                                    .setMaxResults(pageable.getPageSize())
                                                    .getResultList();
        
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<WaterRightVersion> versionCount = countQuery.from(WaterRightVersion.class);
        Join<WaterRightVersion, WaterRight> wc = versionCount.join("waterRight", JoinType.INNER);
        Join<WaterRightVersion, Reference> tc = versionCount.join("typeReference", JoinType.INNER);
        countQuery.select(cb.count(versionCount))
            .where(getWaterRightVersionPredicates(cb, v, wc, tc, waterRightId, basin, waterRightNumber, versionNumber, versionType));

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<WaterRightVersion> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getWaterRightVersionPredicates(CriteriaBuilder cb, Root<WaterRightVersion> v, Join w, Join type, Long waterRightId, String basin, String waterRightNumber, String versionNumber, String versionType) {
        List<Predicate> predicates = new ArrayList<>();
        if(waterRightId != null) {
            predicates.add(cb.equal(w.get("waterRightId"), waterRightId));
        }
        if(basin != null) {
            predicates.add(cb.like(w.get("basin"), basin));
        }
        if(waterRightNumber != null) {
            predicates.add(cb.like(w.get("waterRightNumber").as(String.class), waterRightNumber));
        } 
        if(versionNumber != null) {
            predicates.add(cb.like(v.get("version").as(String.class), versionNumber));
        }
        if(versionType != null) {
            predicates.add(cb.like(type.get("meaning"), versionType));
        }

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }

    private List<Order> getOrders(CriteriaBuilder cb, Root<WaterRightVersion> v, Join w, Join type, Join status, Join waterType, Join waterStatus, WaterRightVersionSortColumn sortColumn, DescSortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();
        Sort.Direction direction = (sortDirection == DescSortDirection.DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;

        if(sortDirection == DescSortDirection.ASC) {
            if(sortColumn == WaterRightVersionSortColumn.BASIN) {
                orderList.add(cb.asc(w.get("basin")));
            } else if (sortColumn == WaterRightVersionSortColumn.VERSION) {
                orderList.add(cb.asc(v.get("version")));
            } else if (sortColumn == WaterRightVersionSortColumn.EXT) {
                orderList.add(cb.asc(w.get("ext")));
            } else if (sortColumn == WaterRightVersionSortColumn.WATERRIGHTTYPEDESCRIPTION) {
                orderList.add(cb.asc(waterType.get("description")));
            } else if (sortColumn == WaterRightVersionSortColumn.WATERRIGHTSTATUSDESCRIPTION) {
                orderList.add(cb.asc(waterStatus.get("description")));
            } else if (sortColumn == WaterRightVersionSortColumn.VERSIONTYPEDESCRIPTION) {
                orderList.add(cb.asc(type.get("meaning")));
            } else if (sortColumn == WaterRightVersionSortColumn.VERSIONSTATUSDESCRIPTION) {
                orderList.add(cb.asc(status.get("description")));
            } else if (sortColumn == WaterRightVersionSortColumn.FLOWRATE) {
                orderList.add(cb.asc(v.get("maximumFlowRate")));
            } else if (sortColumn == WaterRightVersionSortColumn.OPERATINGAUTHORITY) {
                orderList.add(cb.asc(v.get("operatingAuthority")));
            } else if (sortColumn == WaterRightVersionSortColumn.PRIORITYDATE) {
                orderList.add(cb.asc(v.get("priorityDate")));
            } else if (sortColumn == WaterRightVersionSortColumn.SCANNED) {
                orderList.add(cb.asc(v.get("scanned")));
            } else if (sortColumn == WaterRightVersionSortColumn.STANDARDSUPDATED) {
                orderList.add(cb.asc(v.get("standardsUpdated")));
            } else if (sortColumn == WaterRightVersionSortColumn.VOLUME) {
                orderList.add(cb.asc(v.get("maximumVolume")));
            } else if (sortColumn == WaterRightVersionSortColumn.COMPLETEWATERRIGHTNUMBER) {
                orderList.add(cb.asc(w.get("basin")));
                orderList.add(cb.asc(w.get("waterRightNumber")));
                orderList.add(cb.asc(w.get("ext")));
            } else if (sortColumn == WaterRightVersionSortColumn.COMPLETEVERSION) {
                orderList.add(cb.asc(v.get("typeCode")));
                orderList.add(cb.asc(v.get("version")));
            } else { /* WaterRightVersionSortColumn.WATERRIGHTNUMBER */
               orderList.add(cb.asc(w.get("waterRightNumber")));
            }
        } else {
            if(sortColumn == WaterRightVersionSortColumn.BASIN) {
                orderList.add(cb.desc(w.get("basin")));
            } else if (sortColumn == WaterRightVersionSortColumn.VERSION) {
                orderList.add(cb.desc(v.get("version")));
            } else if (sortColumn == WaterRightVersionSortColumn.EXT) {
                orderList.add(cb.desc(w.get("ext")));
            } else if (sortColumn == WaterRightVersionSortColumn.WATERRIGHTTYPEDESCRIPTION) {
                orderList.add(cb.desc(waterType.get("description")));
            } else if (sortColumn == WaterRightVersionSortColumn.WATERRIGHTSTATUSDESCRIPTION) {
                orderList.add(cb.desc(waterStatus.get("description")));
            } else if (sortColumn == WaterRightVersionSortColumn.VERSIONTYPEDESCRIPTION) {
                orderList.add(cb.desc(type.get("meaning")));
            } else if (sortColumn == WaterRightVersionSortColumn.VERSIONSTATUSDESCRIPTION) {
                orderList.add(cb.desc(status.get("description")));
            } else if (sortColumn == WaterRightVersionSortColumn.FLOWRATE) {
                orderList.add(cb.desc(v.get("maximumFlowRate")));
            } else if (sortColumn == WaterRightVersionSortColumn.OPERATINGAUTHORITY) {
                orderList.add(cb.desc(v.get("operatingAuthority")));
            } else if (sortColumn == WaterRightVersionSortColumn.PRIORITYDATE) {
                orderList.add(cb.desc(v.get("priorityDate")));
            } else if (sortColumn == WaterRightVersionSortColumn.SCANNED) {
                orderList.add(cb.desc(v.get("scanned")));
            } else if (sortColumn == WaterRightVersionSortColumn.STANDARDSUPDATED) {
                orderList.add(cb.desc(v.get("standardsUpdated")));
            } else if (sortColumn == WaterRightVersionSortColumn.VOLUME) {
                orderList.add(cb.desc(v.get("maximumVolume")));
            } else if (sortColumn == WaterRightVersionSortColumn.COMPLETEWATERRIGHTNUMBER) {
                orderList.add(cb.desc(w.get("basin")));
                orderList.add(cb.desc(w.get("waterRightNumber")));
                orderList.add(cb.desc(w.get("ext")));
            } else if (sortColumn == WaterRightVersionSortColumn.COMPLETEVERSION) {
                orderList.add(cb.desc(v.get("typeCode")));
                orderList.add(cb.desc(v.get("version")));
            } else { /* WaterRightVersionSortColumn.WATERRIGHTNUMBER */
                orderList.add(cb.desc(w.get("waterRightNumber")));
            }
        }
        /* SECONDARY SORT */
        orderList.add(cb.asc(w.get("waterRightNumber")));
        orderList.add(cb.asc(w.get("basin")));
        orderList.add(cb.asc(w.get("ext")));
        orderList.add(cb.asc(v.get("version")));

        return orderList;
    }

    public int endDateWaterRightGeocodes(Long waterRightId) {
        LOGGER.info("Calling the End Date Water Right Geocodes function");
        Session session = manager.unwrap(Session.class);
        int endDate = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection
                            .prepareCall(
                                    "{ ? = call WRD_COMMON_FUNCTIONS.END_DATE_WR_GEOCODES(?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setLong(2, waterRightId);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                });
        return endDate;
    }

    public int createVersion(BigDecimal waterRightId, String versionType) {
        Session session = manager.unwrap(Session.class);
        Integer ac = session.doReturningWork(
            connection -> {
                try (CallableStatement callableStatement = connection.prepareCall("{ ? = call WRD_COMMON_FUNCTIONS.CREATE_VERSION(?, ?) }")) {
                    callableStatement.registerOutParameter(1, Types.INTEGER);
                    callableStatement.setBigDecimal(2, waterRightId);
                    callableStatement.setString(3, versionType);
                    callableStatement.execute();
                    return callableStatement.getInt(1);
                }
            }
        );
        return ac;
    }

    public int testAttainableVolume(BigDecimal waterRightId, BigDecimal versionid) {
        Session session = manager.unwrap(Session.class);
        Integer ac = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection.prepareCall("{ ? = call WRD_COMMON_FUNCTIONS.TEST_ATTAINABLE_VOLUME(?, ?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setBigDecimal(2, waterRightId);
                        callableStatement.setBigDecimal(3, versionid);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                }
        );
        return ac;
    }

    @Override
    public Page<WaterRightVersionApplicationReferencesDto> getWaterRightVersionApplicationReferences(
        Pageable pageable,
        WaterRightVersionApplicationReferencesSortColumn sortDTOColumn,
        SortDirection sortDirection,
        Long waterRightId,
        Long versionNumber
    ) {

        LOGGER.info("Get Application references for a Water Right Version");

        String sortColumn = getApplicationSortColumn(sortDTOColumn, sortDirection);

        String topQuery = "SELECT a.APPL_ID_SEQ,\n" +
                                    "a.BOCA_CD,\n" +
                                    "a.APTP_CD || '-' || at.DESCR,\n" +
                                    "d.dateReceived dt_received,\n" +
                                    "owners.CUST_ID_SEQ,\n" +
                                    "owners.LST_NM,\n" +
                                    "owners.FST_NM,\n" +
                                    "owners.MID_INT,\n" +
                                    "owners.SUFX\n";

        String countTop = "SELECT Count(*)";

        String baseQuery = "\nFROM WRD_VERSION_APPLICATION_XREFS vax\n" +
                            "inner join WRD_APPLICATIONS a\n" +
                            "on vax.APPL_ID_SEQ = a.APPL_ID_SEQ\n" +
                            "inner join WRD_APPLICATION_TYPES at\n" +
                            "on a.APTP_CD = at.APTP_CD\n" +
                            "left join (\n" +
                                "SELECT ao.APPL_ID_SEQ,\n" +
                                "MIN(c.CUST_ID_SEQ) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) CUST_ID_SEQ,\n" +
                                "MIN(c.LST_NM_OR_BUSN_NM) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) LST_NM,\n" +
                                "MIN(c.FST_NM) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) FST_NM,\n" +
                                "MIN(c.MID_INT) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) MID_INT,\n" +
                                "MIN(c.SUFX) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) SUFX\n" +
                                "FROM WRD_APPLICATIONS ao\n" +
                                "inner join WRD_OWNERS o\n" +
                                "on o.APPL_ID_SEQ = ao.APPL_ID_SEQ\n" +
                                "inner join WRD_CUSTOMERS c\n" +
                                "on o.CUST_ID_SEQ = c.CUST_ID_SEQ\n" +
                                "WHERE o.END_DT is null\n" +
                                "group by ao.APPL_ID_SEQ\n" +
                            ") owners\n" +
                            "on owners.APPL_ID_SEQ = a.APPL_ID_SEQ\n" +
                            "left join (\n" +
                            "SELECT MIN(e.DT_OF_EVNT) dateReceived, e.APPL_ID_SEQ\n" +
                            "FROM WRD_EVENT_DATES e\n" +
                            "inner join WRD_APPLICATIONS ae\n" +
                            "on ae.APPL_ID_SEQ = e.APPL_ID_SEQ\n" +
                            "WHERE (\n" +
                                "(e.EVTP_CD = 'PAMH' and ae.APTP_CD in ('600P', '606P')) or\n" +
                                "(e.EVTP_CD = 'FRMR' and ae.APTP_CD not in ('600P', '606P'))\n" +
                            ")\n" +
                            "group by e.APPL_ID_SEQ\n" +
                            ") d\n" +
                            "on d.APPL_ID_SEQ = a.APPL_ID_SEQ\n" +
                            "WHERE vax.WRGT_ID_SEQ = :waterRightId\n" +
                            "AND vax.VERS_ID_SEQ = :versionNumber\n";
        String fullQuery = "SELECT *\n" +
                            "FROM (\n" +
                                "SELECT all_.*,\n" +
                                    "rownum rownum_\n" +
                                "FROM (\n" +
                                    topQuery + baseQuery +
                                    "order by " + sortColumn + " " + sortDirection.getValue() + ", a.APPL_ID_SEQ asc\n" +
                                ") all_\n" +
                                "WHERE rownum <= :upperlimit\n" +
                            ")\n" +
                            "WHERE rownum_ > :lowerlimit\n";

        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("waterRightId", waterRightId);
        q.setParameter("versionNumber", versionNumber);
        q.setParameter("upperlimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerlimit", pageable.getOffset());

        List<Object[]> results = q.getResultList();
        List<WaterRightVersionApplicationReferencesDto> resultList = results.stream().map(res ->
            new WaterRightVersionApplicationReferencesDto()
                .applicationId(((BigDecimal) res[0]).longValue())
                .basin((String) res[1])
                .applicationType((String) res[2])
                .dateTimeReceived(res[3] != null ? ((Timestamp) res[3]).toLocalDateTime() : null)
                .contactId(res[4] != null ? ((BigDecimal) res[4]).longValue() : null)
                .applicant(Helpers.buildName((String) res[5], (String) res[6], (String) res[7], (String) res[8]))
        ).collect(Collectors.toList());

        Query countQuery = manager.createNativeQuery(countTop + baseQuery);
        countQuery.setParameter("waterRightId", waterRightId);
        countQuery.setParameter("versionNumber", versionNumber);
        Long count = ((BigDecimal) countQuery.getSingleResult()).longValue();
        
        Page<WaterRightVersionApplicationReferencesDto> resultPage = new PageImpl<>(resultList, pageable, count);

        return resultPage;

    }
    public String getApplicationSortColumn(WaterRightVersionApplicationReferencesSortColumn sortDTOColumn, SortDirection sortDirection) {
        switch(sortDTOColumn) {
            case APPLICATIONID:
                return "a.APPL_ID_SEQ";
            case BASIN:
                return "a.BOCA_CD";
            case APPLICATIONTYPE:
                return "a.APTP_CD " + sortDirection.getValue() + ", at.DESCR";
            case DATETIMERECEIVED:
                return "d.dateReceived";
            case CONTACTID:
                return "owners.CUST_ID_SEQ";
            case APPLICANT:
                return "owners.LST_NM " + sortDirection.getValue() + ",\n" +
                        "owners.FST_NM " + sortDirection.getValue() + ",\n" +
                        "owners.MID_INT " + sortDirection.getValue() + ",\n" +
                        "owners.SUFX";
            default:
                return "a.APPL_ID_SEQ";
        }
    }
}
