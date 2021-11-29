package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.CustomerSortColumn;
import gov.mt.wris.dtos.ElectronicContactsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.ElectronicContacts;
import gov.mt.wris.repositories.CustomElectronicContactsRepository;
import org.hibernate.jpa.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomElectronicContactsRepositoryImpl implements CustomElectronicContactsRepository {

    public static Logger LOGGER = LoggerFactory.getLogger(CustomElectronicContactsRepositoryImpl.class);
    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<ElectronicContacts> searchElectronicContacts(Pageable pageable, ElectronicContactsSortColumn sortColumn, SortDirection sortDirection, Long customerId) {

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<ElectronicContacts> q = cb.createQuery(ElectronicContacts.class);
        Root<ElectronicContacts> root = q.from(ElectronicContacts.class);
        q.where(getPredicates(cb, root, customerId));

        List<Order> orderList = getOrders(cb, root, sortColumn, sortDirection);
        q.orderBy(orderList);


        List<ElectronicContacts> result = manager.createQuery(q)
            .setHint(QueryHints.HINT_READONLY, true)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ElectronicContacts> countRoot = countQuery.from(ElectronicContacts.class);
        countQuery.select(cb.count(countRoot))
                .where(getPredicates(cb, root, customerId));

        Long count = manager.createQuery(countQuery).getSingleResult();
        Page<ElectronicContacts> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;

    }

    private Predicate[] getPredicates(CriteriaBuilder cb, Root<ElectronicContacts> root, Long customerId) {

        List<Predicate> predicates = new ArrayList<>();
        if(customerId != null) {
            predicates.add(cb.like(root.get("customerId").as(String.class), customerId.toString()));
        }
        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);
        return pred;

    }

    private List<Order> getOrders(CriteriaBuilder cb, Root<ElectronicContacts> root, ElectronicContactsSortColumn sortColumn, SortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if(sortColumn == ElectronicContactsSortColumn.ELECTRONICID) {
                orderList.add(cb.asc(root.get("electronicId")));
            } else if (sortColumn == ElectronicContactsSortColumn.TYPE) {
                orderList.add(cb.asc(root.get("electronicType")));
                orderList.add(cb.asc(root.get("electronicId")));
            } else if (sortColumn == ElectronicContactsSortColumn.VALUE) {
                orderList.add(cb.asc(root.get("electronicValue")));
                orderList.add(cb.asc(root.get("electronicId")));
            } else {
                orderList.add(cb.asc(root.get("electronicNotes")));
                orderList.add(cb.asc(root.get("electronicId")));
            }
        } else {
            if(sortColumn == ElectronicContactsSortColumn.ELECTRONICID) {
                orderList.add(cb.desc(root.get("electronicId")));
            } else if (sortColumn == ElectronicContactsSortColumn.TYPE) {
                orderList.add(cb.desc(root.get("electronicType")));
                orderList.add(cb.asc(root.get("electronicId")));
            } else if (sortColumn == ElectronicContactsSortColumn.VALUE) {
                orderList.add(cb.desc(root.get("electronicValue")));
                orderList.add(cb.asc(root.get("electronicId")));
            } else {
                orderList.add(cb.desc(root.get("electronicNotes")));
                orderList.add(cb.asc(root.get("electronicId")));
            }
        }
        return orderList;
    }

}
