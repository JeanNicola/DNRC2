package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.AddressSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Address;
import gov.mt.wris.repositories.CustomAddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.jpa.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class CustomAddressRepositoryImpl implements CustomAddressRepository {

    public static Logger LOGGER = LoggerFactory.getLogger(CustomAddressRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<Address> searchAddresses(Pageable pageable, AddressSortColumn sortColumn, SortDirection sortDirection, Long customerId) {

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Address> q = cb.createQuery(Address.class);
        Root<Address> root = q.from(Address.class);
        q.where(getPredicates(cb, root, customerId));

        Join zipcode = (Join) root.fetch("zipCode", JoinType.LEFT);
        Join city = (Join) zipcode.fetch("city", JoinType.LEFT);
        Join state = (Join) city.fetch("state", JoinType.LEFT);
        Join msiCreatedBy = (Join) root.fetch("createdByName", JoinType.LEFT);
        Join msiModifiedBy = (Join) root.fetch("modifiedByName", JoinType.LEFT);

        List<Order> orderList = getOrders(cb, root, zipcode, city, state, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<Address> result = manager.createQuery(q)
                .setHint(QueryHints.HINT_READONLY, true)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Address> countRoot = countQuery.from(Address.class);
        countQuery.select(cb.count(countRoot))
                .where(getPredicates(cb, root, customerId));

        Long count = manager.createQuery(countQuery).getSingleResult();
        Page<Address> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;

    }

    private Predicate[] getPredicates(CriteriaBuilder cb, Root<Address> root, Long customerId) {

        List<Predicate> predicates = new ArrayList<>();
        if(customerId != null) {
            predicates.add(cb.like(root.get("customerId").as(String.class), customerId.toString()));
        }
        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);
        return pred;

    }

    private List<Order> getOrders(CriteriaBuilder cb, Root<Address> root, Join zipcode, Join city, Join state, AddressSortColumn sortColumn, SortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            orderList.add(cb.desc(root.get("primaryMail")));
            if (sortColumn == AddressSortColumn.CUSTOMERID) {
                orderList.add(cb.asc(root.get("customerId")));
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.CITY){
                orderList.add(cb.asc(city.get("cityName")));
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.STATE){
                orderList.add(cb.asc(state.get("name")));
                orderList.add(cb.asc(city.get("cityName")));
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.ZIP){
                orderList.add(cb.asc(zipcode.get("zipCode")));
                orderList.add(cb.asc(state.get("name")));
                orderList.add(cb.asc(city.get("cityName")));
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.ADDRESSLINE1){
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.ADDRESSLINE2){
                orderList.add(cb.asc(root.get("addressLine2")));
            } else if (sortColumn == AddressSortColumn.ADDRESSLINE3){
                orderList.add(cb.asc(root.get("addressLine3")));
            } else {
                orderList.add(cb.asc(root.get("addressId")));
            }
        } else {
            orderList.add(cb.desc(root.get("primaryMail")));
            if (sortColumn == AddressSortColumn.CUSTOMERID) {
                orderList.add(cb.desc(root.get("customerId")));
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.CITY){
                orderList.add(cb.desc(city.get("cityName")));
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.STATE){
                orderList.add(cb.desc(state.get("name")));
                orderList.add(cb.asc(city.get("cityName")));
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.ZIP){
                orderList.add(cb.desc(zipcode.get("zipCode")));
                orderList.add(cb.asc(state.get("name")));
                orderList.add(cb.asc(city.get("cityName")));
                orderList.add(cb.asc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.ADDRESSLINE1){
                orderList.add(cb.desc(root.get("addressLine1")));
            } else if (sortColumn == AddressSortColumn.ADDRESSLINE2){
                orderList.add(cb.desc(root.get("addressLine2")));
            } else if (sortColumn == AddressSortColumn.ADDRESSLINE3){
                orderList.add(cb.desc(root.get("addressLine3")));
            } else {
                orderList.add(cb.desc(root.get("addressId")));
            }
        }
        return orderList;
    }

}
