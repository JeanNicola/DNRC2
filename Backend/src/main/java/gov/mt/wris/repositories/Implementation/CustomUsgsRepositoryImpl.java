package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Usgs;
import gov.mt.wris.repositories.CustomUsgsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomUsgsRepositoryImpl implements CustomUsgsRepository {

    @PersistenceContext
    private EntityManager manager;

    public Page<Usgs> searchUsgsQuadMapValues(Pageable pageable, SortDirection sortDirection, String usgsQuadMapName) {

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        // Query
        CriteriaQuery<Usgs> query = cb.createQuery(Usgs.class);
        Root<Usgs> root = query.from(Usgs.class);

        query.where(getPredicates(cb, root, usgsQuadMapName));

        List<Order> orders = getOrders(cb, root, sortDirection);

        query.orderBy(orders);

        List<Usgs> result = manager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Usgs> countRoot = countQuery.from(Usgs.class);
        countQuery.select(cb.count(countRoot))
                .where(getPredicates(cb, countRoot, usgsQuadMapName));

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<Usgs> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getPredicates(CriteriaBuilder cb, Root<Usgs> root, String usgsQuadMapName) {

        List<Predicate> predicates = new ArrayList<>();

        if(usgsQuadMapName != null) {
            predicates.add(cb.like(root.get("name"), usgsQuadMapName));
        }

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }

    private List<Order> getOrders(CriteriaBuilder cb, Root<Usgs> root, SortDirection sortDirection) {

        List<Order> orderList = new ArrayList<>();

        if(sortDirection == SortDirection.ASC) {
            orderList.add(cb.asc(root.get("name")));
        } else {
            orderList.add(cb.desc(root.get("name")));
        }

        return orderList;
    }

}
