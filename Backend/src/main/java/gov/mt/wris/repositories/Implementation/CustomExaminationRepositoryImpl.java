package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.ExaminationsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Examination;
import gov.mt.wris.repositories.CustomExaminationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomExaminationRepositoryImpl implements CustomExaminationRepository {

    @PersistenceContext
    private EntityManager manager;

    public Page<Examination> searchExaminations(Pageable pageable, ExaminationsSortColumn sortColumn, SortDirection sortDirection, String basin, String waterRightNumber, String waterRightType, String versionType, String versionNumber) {

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        // Query
        CriteriaQuery<Examination> query = cb.createQuery(Examination.class);
        Root<Examination> e = query.from(Examination.class);

        Join p = (Join) e.fetch("purpose", JoinType.INNER);
        Join v = (Join) p.fetch("waterRightVersion", JoinType.INNER);
        Join vtype = (Join) v.fetch("typeReference", JoinType.INNER);
        Join vstatus = (Join) v.fetch("versionStatus", JoinType.INNER);
        Join wr = (Join) v.fetch("waterRight", JoinType.INNER);
        Join type = (Join) wr.fetch("waterRightType", JoinType.INNER);
        Join status = (Join) wr.fetch("waterRightStatus", JoinType.LEFT);

        query.where(getExaminationPredicates(cb, v, wr, basin, waterRightNumber, waterRightType, versionType, versionNumber));

        List<Order> orders = getOrders(cb, v, wr, type, status, vtype, vstatus, sortColumn, sortDirection);

        query.orderBy(orders);

        List<Examination> result = manager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Examination> ec = countQuery.from(Examination.class);
        Join pc = ec.join("purpose", JoinType.INNER);
        Join vc = pc.join("waterRightVersion", JoinType.INNER);
        Join wrc = vc.join("waterRight", JoinType.INNER);
        countQuery.select(cb.count(ec))
                .where(getExaminationPredicates(cb, vc, wrc, basin, waterRightNumber, waterRightType, versionType, versionNumber));

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<Examination> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getExaminationPredicates(CriteriaBuilder cb, Join v, Join wr, String basin, String waterRightNumber, String waterRightType, String versionType, String versionNumber) {

        List<Predicate> predicates = new ArrayList<>();
        if(basin != null) {
            predicates.add(cb.like(wr.get("basin"), basin));
        }
        if(waterRightNumber != null) {
            predicates.add(cb.like(wr.get("waterRightNumber").as(String.class), waterRightNumber));
        }
        if(waterRightType != null) {
            predicates.add(cb.like(wr.get("waterRightTypeCode"), waterRightType));
        }
        if(versionType != null) {
            predicates.add(cb.like(v.get("typeCode"), versionType));
        }
        if(versionNumber != null) {
            predicates.add(cb.like(v.get("version").as(String.class), versionNumber));
        }

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }

    private List<Order> getOrders(CriteriaBuilder cb, Join v, Join wr, Join type, Join status, Join vtype, Join vstatus, ExaminationsSortColumn sortColumn, SortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();

        if(sortDirection == SortDirection.ASC) {
            if (sortColumn == ExaminationsSortColumn.WATERRIGHTTYPEDESCRIPTION) {
                orderList.add(cb.asc(type.get("description")));
            } else if (sortColumn == ExaminationsSortColumn.WATERRIGHTSTATUSDESCRIPTION) {
                orderList.add(cb.asc(status.get("description")));
            } else if (sortColumn == ExaminationsSortColumn.COMPLETEWATERRIGHTVERSION) {
                orderList.add(cb.asc(vtype.get("meaning")));
                orderList.add(cb.asc(v.get("version")));
                orderList.add(cb.asc(vstatus.get("description")));
            }
        } else {
            if (sortColumn == ExaminationsSortColumn.COMPLETEWATERRIGHTNUMBER) {
                orderList.add(cb.desc(wr.get("basin")));
                orderList.add(cb.desc(wr.get("waterRightNumber")));
                orderList.add(cb.desc(wr.get("ext")));
            } else if (sortColumn == ExaminationsSortColumn.WATERRIGHTTYPEDESCRIPTION) {
                orderList.add(cb.desc(type.get("description")));
            } else if (sortColumn == ExaminationsSortColumn.WATERRIGHTSTATUSDESCRIPTION) {
                orderList.add(cb.desc(status.get("description")));
            } else if (sortColumn == ExaminationsSortColumn.COMPLETEWATERRIGHTVERSION) {
                orderList.add(cb.desc(vtype.get("meaning")));
                orderList.add(cb.desc(v.get("version")));
                orderList.add(cb.desc(vstatus.get("description")));
            }
        }

        if (sortColumn != ExaminationsSortColumn.COMPLETEWATERRIGHTNUMBER || sortDirection != SortDirection.DESC) {
            orderList.add(cb.asc(wr.get("basin")));
            orderList.add(cb.asc(wr.get("waterRightNumber")));
            orderList.add(cb.asc(wr.get("ext")));
        }

        return orderList;
    }

}
