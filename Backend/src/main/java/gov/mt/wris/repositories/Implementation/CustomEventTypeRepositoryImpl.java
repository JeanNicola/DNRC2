package gov.mt.wris.repositories.Implementation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.ApplicationTypeXref;
import gov.mt.wris.models.EventType;
import gov.mt.wris.repositories.CustomEventTypeRepository;
import gov.mt.wris.dtos.SortDirection;

@Repository
public class CustomEventTypeRepositoryImpl implements CustomEventTypeRepository{
    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<EventType> getEventTypes(Pageable pageable, String sortColumn, SortDirection sortDirection, String code, String description, String dueDays) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<EventType> q = cb.createQuery(EventType.class);

        Root<EventType> c = q.from(EventType.class);
        List<Predicate> predicates = new ArrayList<>();
        if(code != null) {
            predicates.add(cb.like(c.get("code"), code));
        }
        if(description != null) {
            predicates.add(cb.like(c.get("description"), description));
        }
        if(dueDays != null) {
            predicates.add(cb.like(c.get("dueDays").as(String.class), dueDays));
        }
        q.where(predicates.toArray(new Predicate[predicates.size()]));

        List<Order> orderList = new ArrayList<Order>();
        if(sortDirection == SortDirection.ASC) {
            orderList.add(cb.asc(c.get(sortColumn)));
        }else{
            orderList.add(cb.desc(c.get(sortColumn)));
        }
        orderList.add(cb.asc(c.get("code")));
        q.orderBy(orderList);

        List<EventType> result = manager.createQuery(q)
                                                    .setFirstResult((int) pageable.getOffset())
                                                    .setMaxResults(pageable.getPageSize())
                                                    .getResultList();


        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EventType> eventTypeRootCount = countQuery.from(EventType.class);
        countQuery.select(cb.count(eventTypeRootCount)).where(predicates.toArray(new Predicate[predicates.size()]));
        
        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<EventType> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }
}