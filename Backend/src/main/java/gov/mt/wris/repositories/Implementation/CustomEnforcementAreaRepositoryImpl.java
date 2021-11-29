package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.EnforcementsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.EnforcementArea;
import gov.mt.wris.models.PointOfDiversion;
import gov.mt.wris.models.PointOfDiversionEnforcement;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.repositories.CustomEnforcementAreaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomEnforcementAreaRepositoryImpl implements CustomEnforcementAreaRepository {

    public static Logger LOGGER = LoggerFactory.getLogger(CustomEnforcementAreaRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    public Page<PointOfDiversionEnforcement> searchEnforcements(Pageable pageable, EnforcementsSortColumn sortColumn, SortDirection sortDirection, String area, String name, String enforcementNumber, String basin, String waterNumber) {

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<PointOfDiversionEnforcement> q = cb.createQuery(PointOfDiversionEnforcement.class);
        Root<PointOfDiversionEnforcement> root = q.from(PointOfDiversionEnforcement.class);

        Join diversion = (Join) root.fetch("pointOfDiversion", JoinType.INNER);
        Join enforcement = (Join) root.fetch("enforcementArea", JoinType.INNER);
        Join version = (Join) diversion.fetch("version", JoinType.INNER);
        Join right = (Join) version.fetch("waterRight", JoinType.INNER);

        q.where(getEnforcementPredicates(cb, root, diversion, enforcement, right, area, name, enforcementNumber, basin, waterNumber));
        List<Order> orderList = getEnforcementOrders(cb, root,enforcement, right, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<PointOfDiversionEnforcement> result = manager.createQuery(q)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        /* wow */
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PointOfDiversionEnforcement> cr = countQuery.from(PointOfDiversionEnforcement.class);
        Join<PointOfDiversionEnforcement, PointOfDiversion> cj1 = cr.join("pointOfDiversion", JoinType.INNER);
        Join<PointOfDiversionEnforcement, EnforcementArea> cj2 = cr.join("enforcementArea", JoinType.INNER);
        Join<PointOfDiversion, WaterRightVersion> cj3 = cj1.join("version", JoinType.INNER);
        Join<WaterRightVersion, WaterRight> cj4 = cj3.join("waterRight", JoinType.INNER);
        countQuery.select(cb.count(cr)).where(getEnforcementPredicates(cb, cr, cj1, cj2, cj4, area, name, enforcementNumber, basin, waterNumber));
        Long cnt = manager.createQuery(countQuery).getSingleResult();

        Page<PointOfDiversionEnforcement> page = new PageImpl<>(result, pageable, cnt);
        return page;

    }

    private Predicate[] getEnforcementPredicates(CriteriaBuilder cb, Root<PointOfDiversionEnforcement> root, Join diversion, Join enforcement, Join right, String area, String name, String enforcementNumber, String basin, String waterNumber) {

        List<Predicate> predicates = new ArrayList<>();
        if(area != null) {
            predicates.add(cb.like(root.get("enforcementId").as(String.class), area));
        }
        if(name != null) {
            predicates.add(cb.like(enforcement.get("name"), name));
        }
        if(enforcementNumber != null) {
            predicates.add(cb.like(root.get("enforcementNumber"), enforcementNumber));
        }
        if(basin != null) {
            predicates.add(cb.like(right.get("basin"), basin));
        }
        if(waterNumber != null) {
            predicates.add(cb.like(right.get("waterRightNumber").as(String.class), waterNumber));
        }
        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);
        return pred;

    }

    private List<Order> getEnforcementOrders(CriteriaBuilder cb, Root<PointOfDiversionEnforcement> root, Join enforcement, Join right, EnforcementsSortColumn sortColumn, SortDirection sortDirection) {

        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if(sortColumn == EnforcementsSortColumn.COMPLETEWATERRIGHTNUMBER) {
                orderList.add(cb.asc(right.get("basin")));
                orderList.add(cb.asc(right.get("waterRightNumber")));
                orderList.add(cb.asc(right.get("ext")));
            } else if(sortColumn == EnforcementsSortColumn.ENFORCEMENTAREA) {
                orderList.add(cb.asc(root.get("enforcementId")));
            } else if (sortColumn == EnforcementsSortColumn.ENFORCEMENTNUMBER) {
                orderList.add(cb.asc(root.get("enforcementNumber")));
            } else { /* EnforcementsSortColumn.ENFORCEMENTNAME */
                orderList.add(cb.asc(enforcement.get("name")));
            }
        } else {
            if(sortColumn == EnforcementsSortColumn.COMPLETEWATERRIGHTNUMBER) {
                orderList.add(cb.desc(right.get("basin")));
                orderList.add(cb.desc(right.get("waterRightNumber")));
                orderList.add(cb.desc(right.get("ext")));
            } else if(sortColumn == EnforcementsSortColumn.ENFORCEMENTAREA) {
                orderList.add(cb.desc(root.get("enforcementId")));
            } else if (sortColumn == EnforcementsSortColumn.ENFORCEMENTNUMBER) {
                orderList.add(cb.desc(root.get("enforcementNumber")));
            } else { /* EnforcementsSortColumn.ENFORCEMENTNAME */
                orderList.add(cb.desc(enforcement.get("name")));
            }
        }
        orderList.add(cb.asc(root.get("enforcementId")));
        orderList.add(cb.asc(root.get("enforcementNumber")));
        orderList.add(cb.asc(right.get("basin")));
        orderList.add(cb.asc(right.get("waterRightNumber")));
        orderList.add(cb.asc(right.get("ext")));
        return orderList;

    }

}
