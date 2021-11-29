package gov.mt.wris.repositories.Implementation;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubcompactSortColumn;
import gov.mt.wris.models.Compact;
import gov.mt.wris.models.Subcompact;
import gov.mt.wris.repositories.CustomCompactRepository;

public class CustomCompactRepositoryImpl implements CustomCompactRepository {
    public static Logger LOGGER = LoggerFactory.getLogger(CustomCompactRepository.class);

    @PersistenceContext
    EntityManager manager;

    public Page<Subcompact> searchSubcompacts(Pageable pageable, SubcompactSortColumn sortColumn, SortDirection sortDirection, String subcompact, String compact) {
        LOGGER.info("Searching for Compacts");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Subcompact> q = cb.createQuery(Subcompact.class);

        Root<Subcompact> root = q.from(Subcompact.class);
        Join compactJoin = (Join) root.fetch("compact", JoinType.INNER);

        q.where(getPredicates(cb, root, compactJoin, subcompact, compact));

        List<Order> orderList = getOrders(cb, root, compactJoin, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<Subcompact> result = manager.createQuery(q)
                                    .setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

        Root<Subcompact> countRoot = countQuery.from(Subcompact.class);
        Join<Subcompact, Compact> countCompact = countRoot.join("compact", JoinType.INNER);

        countQuery.select(cb.count(countRoot)).where(getPredicates(cb, countRoot, countCompact, subcompact, compact));

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<Subcompact> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getPredicates(CriteriaBuilder cb, Root<Subcompact> root, Join compactJoin, String subcompact, String compact) {
        List<Predicate> predicates = new ArrayList<>();
        if(subcompact != null) {
            predicates.add(cb.like(root.get("name"), subcompact));
        }
        if(compact != null) {
            predicates.add(cb.like(compactJoin.get("name"), compact));
        }
        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);
        return pred;
    }

    private List<Order> getOrders(CriteriaBuilder cb, Root<Subcompact> root, Join compactJoin, SubcompactSortColumn sortColumn, SortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if(sortColumn == SubcompactSortColumn.COMPACT) {
                orderList.add(cb.asc(compactJoin.get("name")));
            } else {
                orderList.add(cb.asc(root.get("name")));
            }
        } else {
            if(sortColumn == SubcompactSortColumn.COMPACT) {
                orderList.add(cb.desc(compactJoin.get("name")));
            } else {
                orderList.add(cb.desc(root.get("name")));
            }
        }
        orderList.add(cb.asc(compactJoin.get("name")));
        orderList.add(cb.asc(root.get("name")));
        return orderList;
    }
}
