package gov.mt.wris.repositories.Implementation;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.WaterRightSortColumn;
import gov.mt.wris.dtos.WaterRightVersionSearchSortColumn;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightStatus;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.repositories.CustomWaterRightRepository;

@Repository
public class CustomWaterRightRepositoryImpl implements CustomWaterRightRepository {
    public static Logger LOGGER = LoggerFactory.getLogger(CustomWaterRightRepository.class);

    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<WaterRight> getWaterRights(Pageable pageable,
        WaterRightSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String ext,
        String typeCode,
        String statusCode,
        String subBasin,
        String waterReservationId,
        String conservationDistrictNumber
    ) {
        LOGGER.info("Searching for Water Rights");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<WaterRight> query = cb.createQuery(WaterRight.class);

        Root<WaterRight> w = query.from(WaterRight.class);
        Join type = (Join) w.fetch("waterRightType", JoinType.INNER);
        Join status = (Join) w.fetch("waterRightStatus", JoinType.LEFT);

        query.where(getWaterRightPredicates(cb, w, basin, waterRightNumber, ext, typeCode, statusCode, subBasin, waterReservationId, conservationDistrictNumber));

        List<Order> orders = getOrders(cb, w, type, status, sortColumn, sortDirection);
        query.orderBy(orders);

        List<WaterRight> result = manager.createQuery(query)
                                                    .setFirstResult((int) pageable.getOffset())
                                                    .setMaxResults(pageable.getPageSize())
                                                    .getResultList();
        
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<WaterRight> versionCount = countQuery.from(WaterRight.class);
        countQuery.select(cb.count(versionCount))
            .where(getWaterRightPredicates(cb, w, basin, waterRightNumber, ext, typeCode, statusCode, subBasin, waterReservationId, conservationDistrictNumber));

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<WaterRight> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    public Page<Object[]> getWaterRightsWithChangeAuthorizationCount(Pageable pageable,
        WaterRightSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String ext,
        String typeCode,
        String statusCode,
        String subBasin,
        String waterReservationId,
        String conservationDistrictNumber
    ) {
        LOGGER.info("Searching for Water Rights");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<WaterRight> w = query.from(WaterRight.class);
        Join type = (Join) w.fetch("waterRightType", JoinType.INNER);
        Join status = (Join) w.fetch("waterRightStatus", JoinType.LEFT);

        Subquery sub = query.subquery(Long.class);
        Root subRoot = sub.from(WaterRightVersion.class);
        Join<Reference, WaterRightVersion> versionType = subRoot.join("typeReference", JoinType.INNER);
        Join<WaterRightStatus, WaterRightVersion> versionStatus = subRoot.join("versionStatus", JoinType.INNER);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(w.get("waterRightId"), subRoot.get("waterRightId")));
        predicates.add(cb.like(versionType.get("value"), "CHAU%"));
        predicates.add(cb.equal(versionStatus.get("code"), "ACTV"));
        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        sub.select(cb.count(subRoot));
        sub.where(pred);

        query.where(getWaterRightPredicates(cb, w, basin, waterRightNumber, ext, typeCode, statusCode, subBasin, waterReservationId, conservationDistrictNumber));

        List<Order> orders = getOrders(cb, w, type, status, sortColumn, sortDirection);
        query.orderBy(orders);

        query.multiselect(w, sub.getSelection());

        List<Object[]> result = manager.createQuery(query)
                                                    .setFirstResult((int) pageable.getOffset())
                                                    .setMaxResults(pageable.getPageSize())
                                                    .getResultList();
        
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<WaterRight> versionCount = countQuery.from(WaterRight.class);
        countQuery.select(cb.count(versionCount))
            .where(getWaterRightPredicates(cb, w, basin, waterRightNumber, ext, typeCode, statusCode, subBasin, waterReservationId, conservationDistrictNumber));

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<Object[]> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getWaterRightPredicates(CriteriaBuilder cb, Root<WaterRight> w, String basin, String waterRightNumber, String ext, String typeCode, String statusCode, String subBasin, String waterReservationId, String conservationDistrictNumber) {
        List<Predicate> predicates = new ArrayList<>();
        if(basin != null) {
            predicates.add(cb.like(w.get("basin"), basin));
        }
        if(waterRightNumber != null) {
            predicates.add(cb.like(w.get("waterRightNumber").as(String.class), waterRightNumber));
        } 
        if(ext != null) {
            predicates.add(cb.like(w.get("ext"), ext));
        }
        if(typeCode != null) {
            predicates.add(cb.like(w.get("waterRightTypeCode"), typeCode));
        }
        if(statusCode != null) {
            predicates.add(cb.like(w.get("waterRightStatusCode"), statusCode));
        }
        if(subBasin != null) {
            predicates.add(cb.like(w.get("subBasin"), subBasin));
        }
        if(waterReservationId != null) {
            predicates.add(cb.like(w.get("waterReservationId").as(String.class), waterReservationId));
        } 
        if(conservationDistrictNumber != null) {
            predicates.add(cb.like(w.get("conDistNo"), conservationDistrictNumber));
        }

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }

    private List<Order> getOrders(CriteriaBuilder cb, Root<WaterRight> w, Join type, Join status, WaterRightSortColumn sortColumn, DescSortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();
        Sort.Direction direction = (sortDirection == DescSortDirection.DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;

        if(sortDirection == DescSortDirection.ASC) {
            if(sortColumn == WaterRightSortColumn.BASIN) {
                orderList.add(cb.asc(w.get("basin")));
            } else if (sortColumn == WaterRightSortColumn.EXT) {
                orderList.add(cb.asc(w.get("ext")));
            } else if (sortColumn == WaterRightSortColumn.TYPEDESCRIPTION) {
                orderList.add(cb.asc(type.get("description")));
            } else if (sortColumn == WaterRightSortColumn.STATUSDESCRIPTION) {
                orderList.add(cb.asc(status.get("description")));
            } else if (sortColumn == WaterRightSortColumn.STATUSCODE) {
                orderList.add(cb.asc(status.get("code")));
            } else if (sortColumn == WaterRightSortColumn.SUBBASIN) {
                orderList.add(cb.asc(w.get("subBasin")));
            } else if (sortColumn == WaterRightSortColumn.CONSERVATIONDISTRICTNUMBER) {
                orderList.add(cb.asc(w.get("conDistNo")));
            } else if (sortColumn == WaterRightSortColumn.CONSERVATIONDISTRICTDATE) {
                orderList.add(cb.asc(w.get("conDistDate")));
            } else if (sortColumn == WaterRightSortColumn.WATERRESERVATIONID) {
                orderList.add(cb.asc(w.get("waterReservationId")));
            } else {
                orderList.add(cb.asc(w.get("waterRightNumber")));
            }
        } else {
            if(sortColumn == WaterRightSortColumn.BASIN) {
                orderList.add(cb.desc(w.get("basin")));
            } else if (sortColumn == WaterRightSortColumn.EXT) {
                orderList.add(cb.desc(w.get("ext")));
            } else if (sortColumn == WaterRightSortColumn.TYPEDESCRIPTION) {
                orderList.add(cb.desc(type.get("description")));
            } else if (sortColumn == WaterRightSortColumn.STATUSDESCRIPTION) {
                orderList.add(cb.desc(status.get("description")));
            } else if (sortColumn == WaterRightSortColumn.STATUSCODE) {
                orderList.add(cb.desc(status.get("code")));
            } else if (sortColumn == WaterRightSortColumn.SUBBASIN) {
                orderList.add(cb.desc(w.get("subBasin")));
            } else if (sortColumn == WaterRightSortColumn.CONSERVATIONDISTRICTNUMBER) {
                orderList.add(cb.desc(w.get("conDistNo")));
            } else if (sortColumn == WaterRightSortColumn.CONSERVATIONDISTRICTDATE) {
                orderList.add(cb.desc(w.get("conDistDate")));
            } else if (sortColumn == WaterRightSortColumn.WATERRESERVATIONID) {
                orderList.add(cb.desc(w.get("waterReservationId")));
            } else {
                orderList.add(cb.desc(w.get("waterRightNumber")));
            }
        }
        orderList.add(cb.desc(w.get("waterRightNumber")));
        orderList.add(cb.asc(w.get("basin")));

        return orderList;
    }

    public Page<Object[]> getWaterRightsByVersions(Pageable pageable,
        WaterRightVersionSearchSortColumn sortColumn,
        DescSortDirection sortDirection,
        String waterRightNumber,
        String version,
        String versionTypeMeaning
    ) {
        LOGGER.info("Searching for Water Rights by Versions");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<WaterRight> w = query.from(WaterRight.class);
        Join type = (Join) w.fetch("waterRightType", JoinType.INNER);
        Join status = (Join) w.fetch("waterRightStatus", JoinType.LEFT);

        Subquery sub = attachVersionSubQuery(cb, query, w, version, versionTypeMeaning);

        query.where(getWaterRightVersionPredicates(cb, w, waterRightNumber, sub));

        List<Order> orders = getVersionOrders(cb, w, type, status, sortColumn, sortDirection);
        query.orderBy(orders);

        // Build the subselect to not include the version since we want all versions for the water right
        sub = attachVersionSubQuery(cb, query, w, null, null);
        query.multiselect(w, sub.getSelection());

        List<Object[]> result = manager.createQuery(query)
                                                    .setFirstResult((int) pageable.getOffset())
                                                    .setMaxResults(pageable.getPageSize())
                                                    .getResultList();
        
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<WaterRight> versionCount = countQuery.from(WaterRight.class);

        sub = attachVersionSubQuery(cb, countQuery, versionCount, version, versionTypeMeaning);

        countQuery.select(cb.count(versionCount))
            .where(getWaterRightVersionPredicates(cb, w, waterRightNumber, sub));

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<Object[]> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Subquery attachVersionSubQuery(CriteriaBuilder cb, CriteriaQuery query, Root root, String version, String versionTypeMeaning) {
        Subquery sub = query.subquery(Long.class);
        Root subRoot = sub.from(WaterRightVersion.class);
        Join<Reference, WaterRightVersion> versionType = subRoot.join("typeReference", JoinType.INNER);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("waterRightId"), subRoot.get("waterRightId")));
        if(version != null) {
            predicates.add(cb.like(subRoot.get("version").as(String.class), version));
        }
        if(versionTypeMeaning != null) {
            predicates.add(cb.like(versionType.get("meaning"), versionTypeMeaning));
        }
        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        sub.select(cb.count(subRoot));
        sub.where(pred);

        return sub;
    }

    private Predicate[] getWaterRightVersionPredicates(CriteriaBuilder cb, Root<WaterRight> w, String waterRightNumber, Subquery sub) {
        List<Predicate> predicates = new ArrayList<>();
        if(waterRightNumber != null) {
            predicates.add(cb.like(w.get("waterRightNumber").as(String.class), waterRightNumber));
        } 
        predicates.add(cb.greaterThan(sub.getSelection(), 0));

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }

    private List<Order> getVersionOrders(CriteriaBuilder cb, Root<WaterRight> w, Join type, Join status, WaterRightVersionSearchSortColumn sortColumn, DescSortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();
        Sort.Direction direction = (sortDirection == DescSortDirection.DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;

        if(sortDirection == DescSortDirection.ASC) {
            if(sortColumn == WaterRightVersionSearchSortColumn.BASIN) {
                orderList.add(cb.asc(w.get("basin")));
            } else if (sortColumn == WaterRightVersionSearchSortColumn.EXT) {
                orderList.add(cb.asc(w.get("ext")));
            } else if (sortColumn == WaterRightVersionSearchSortColumn.TYPEDESCRIPTION) {
                orderList.add(cb.asc(type.get("description")));
            } else if (sortColumn == WaterRightVersionSearchSortColumn.STATUSDESCRIPTION) {
                orderList.add(cb.asc(status.get("description")));
            } else {
                orderList.add(cb.asc(w.get("waterRightNumber")));
            }
        } else {
            if(sortColumn == WaterRightVersionSearchSortColumn.BASIN) {
                orderList.add(cb.desc(w.get("basin")));
            } else if (sortColumn == WaterRightVersionSearchSortColumn.EXT) {
                orderList.add(cb.desc(w.get("ext")));
            } else if (sortColumn == WaterRightVersionSearchSortColumn.TYPEDESCRIPTION) {
                orderList.add(cb.desc(type.get("description")));
            } else if (sortColumn == WaterRightVersionSearchSortColumn.STATUSDESCRIPTION) {
                orderList.add(cb.desc(status.get("description")));
            } else {
                orderList.add(cb.desc(w.get("waterRightNumber")));
            }
        }
        orderList.add(cb.desc(w.get("waterRightNumber")));
        orderList.add(cb.asc(w.get("basin")));
        orderList.add(cb.asc(w.get("ext")));

        return orderList;
    }

    @Override
    public String getScannedDocUrl(BigDecimal waterRightId, BigDecimal version) {

        Session session = manager.unwrap(Session.class);
        String ac = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection
                            .prepareCall(
                                    "{ ? = call WRD_COMMON_FUNCTIONS.GET_SCANNED_DOC_URL(?, ?) }")) {
                        callableStatement.registerOutParameter(1, Types.NCHAR);
                        callableStatement.setBigDecimal(2, waterRightId);
                        callableStatement.setBigDecimal(3, version);
                        callableStatement.execute();
                        return callableStatement.getString(1);
                    }
                });
        return ac;

    }
}