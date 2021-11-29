package gov.mt.wris.repositories.Implementation;

import java.math.BigDecimal;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import gov.mt.wris.dtos.RelatedRightSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.RelatedRight;
import gov.mt.wris.models.RelatedRightVerXref;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.repositories.CustomRelatedRightRepository;

@Repository
public class CustomRelatedRightRepositoryImpl implements CustomRelatedRightRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(CustomRelatedRightRepositoryImpl.class);


    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<Object[]> searchRelatedRights(Pageable pageable, RelatedRightSortColumn sortColumn, SortDirection sortDirection, String relatedRightId, String relationshipType, String waterRightNumber, String basin, String ext) {

        LOGGER.info("Searching for related rights");

        CriteriaBuilder cb = manager.getCriteriaBuilder();

        // Create empty main query and set the initial FROM clause
        CriteriaQuery<Object[]> mainQuery = cb.createQuery(Object[].class);
        Root<RelatedRight> mainRoot = mainQuery.from(RelatedRight.class);

        // Set water rights count subquery to get number of water rights associated with each related right
        Subquery<Long> countSubQuery = mainQuery.subquery(Long.class);
        Root<RelatedRightVerXref> countSubQueryRoot = countSubQuery.from(RelatedRightVerXref.class);
        Join<RelatedRightVerXref, WaterRight> joinWaterRight = countSubQueryRoot.join("waterRight", JoinType.INNER);

        // Set count subquery predicates
        List<Predicate> subQueryPredicates = setJoinPredicates(cb, joinWaterRight, waterRightNumber, basin, ext);
        subQueryPredicates.add(cb.equal(mainRoot.get("relatedRightId"), countSubQueryRoot.get("relatedRightId")));       
        Predicate[] subPred = new Predicate[subQueryPredicates.size()];
        subQueryPredicates.toArray(subPred);
        countSubQuery.select(cb.count(countSubQueryRoot));
        countSubQuery.where(subPred);
        
        // Set main select predicates
        List<Predicate> mainQueryPredicates = new ArrayList<>();
        if(relatedRightId != null) {
            mainQueryPredicates.add(cb.like(mainRoot.get("relatedRightId").as(String.class), relatedRightId));
        }

        if(relationshipType != null) {
            mainQueryPredicates.add(cb.like(mainRoot.get("relationshipType"), relationshipType));
        }

        // Set related rights subquery to limit the related rights to only those of a specific water right/basin/ext
        // Only do it *if* the criteria have been requested
        if (waterRightNumber != null || basin != null || ext != null) {
            Subquery<BigDecimal> relatedRightsSubQuery = mainQuery.subquery(BigDecimal.class);
            Root<RelatedRightVerXref> relatedRightsSubQueryRoot = relatedRightsSubQuery.from(RelatedRightVerXref.class);
            joinWaterRight = relatedRightsSubQueryRoot.join("waterRight", JoinType.INNER);

            // Build related rights subquery predicates
            subQueryPredicates = setJoinPredicates(cb, joinWaterRight, waterRightNumber, basin, ext);
            subPred = new Predicate[subQueryPredicates.size()];
            subQueryPredicates.toArray(subPred);    

            relatedRightsSubQuery.select(relatedRightsSubQueryRoot.get("relatedRightId"));
            relatedRightsSubQuery.where(subPred);
            mainQueryPredicates.add(cb.in(mainRoot.get("relatedRightId")).value(relatedRightsSubQuery));
        }

        Predicate[] mainPred = new Predicate[mainQueryPredicates.size()];
        mainQueryPredicates.toArray(mainPred);

        // Create Main SELECT clause
        mainQuery.multiselect(mainRoot, countSubQuery.getSelection());
        mainQuery.where(mainPred);

        // Set sort order
        List<Order> orderList = getOrders(cb, mainRoot, sortColumn, sortDirection);
        mainQuery.orderBy(orderList);

        // Get the data
        List<Object[]> result = manager.createQuery(mainQuery)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        // Main Count Query
        CriteriaQuery<Long> mainCountQuery = cb.createQuery(Long.class);
        Root<RelatedRight> mainCountRoot = mainCountQuery.from(RelatedRight.class);
        mainCountQuery.select(cb.count(mainCountRoot));
        mainCountQuery.where(mainPred);

        Long count = manager.createQuery(mainCountQuery).getSingleResult();
        Page<Object[]> resultPage = new PageImpl<>(result, pageable, count);
        return resultPage;
    }

    private List<Predicate> setJoinPredicates(CriteriaBuilder cb, Join<RelatedRightVerXref, WaterRight> joinEntity, String waterRightNumber, String basin, String ext) {
        List<Predicate> pred = new ArrayList<>();
        if (waterRightNumber != null) {
            pred.add(cb.like(joinEntity.get("waterRightNumber").as(String.class), waterRightNumber));
        }

        if (basin != null) {
            pred.add(cb.like(joinEntity.get("basin"), basin));
        }

        if (ext != null) {
            pred.add(cb.like(joinEntity.get("ext"), ext));
        }

        return pred;
    }

    private List<Order> getOrders(CriteriaBuilder cb, Root<RelatedRight> root, RelatedRightSortColumn sortColumn, SortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if(sortColumn == RelatedRightSortColumn.RELATEDRIGHTID) {
                orderList.add(cb.asc(root.get("relatedRightId")));
            } else {
                orderList.add(cb.asc(root.get("relationshipType")));
                orderList.add(cb.asc(root.get("relatedRightId")));
            }
        } else {
            if(sortColumn == RelatedRightSortColumn.RELATEDRIGHTID) {
                orderList.add(cb.desc(root.get("relatedRightId")));
            } else {
                orderList.add(cb.desc(root.get("relationshipType")));
                orderList.add(cb.asc(root.get("relatedRightId")));
            }
        }
        return orderList;
    }
}
