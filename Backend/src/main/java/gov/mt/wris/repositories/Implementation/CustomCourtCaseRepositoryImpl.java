package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.CaseSearchSortColumn;
import gov.mt.wris.dtos.EligibleWaterRightsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CaseApplicationXref;
import gov.mt.wris.models.CaseStatus;
import gov.mt.wris.models.CaseType;
import gov.mt.wris.models.CourtCase;
import gov.mt.wris.repositories.CustomCourtCaseRepository;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomCourtCaseRepositoryImpl implements CustomCourtCaseRepository {

    public static Logger LOGGER = LoggerFactory.getLogger(CustomCourtCaseRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    public Page<CourtCase> searchCases(Pageable pageable, CaseSearchSortColumn sortColumn, SortDirection sortDirection, String applicationId, String caseNumber, String caseTypeCode, String caseStatusCode, String waterCourtCaseNumber) {

        LOGGER.info("Search Cases and Hearings");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<CourtCase> q = cb.createQuery(CourtCase.class);
        Root<CourtCase> root = q.from(CourtCase.class);
        Join caseApplicationXrefs = (Join) root.fetch("caseApplicationXrefs", JoinType.LEFT);
        Join caseStatus = (Join) root.fetch("caseStatus", JoinType.LEFT);
        Join caseType = (Join) root.fetch("caseType", JoinType.LEFT);
        Join application = (Join) caseApplicationXrefs.fetch("application", JoinType.LEFT);
        Join applicationType = (Join) application.fetch("type", JoinType.LEFT);
        Join office = (Join) application.fetch("office", JoinType.LEFT);

        q.where(getSearchCasesPredicates(cb, root, caseApplicationXrefs, caseStatus, caseType, applicationId, caseNumber, caseTypeCode, caseStatusCode, waterCourtCaseNumber));
        List<Order> orderList = getSearchCasesOrderBy(cb, root, caseApplicationXrefs, caseStatus, caseType, application, applicationType, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<CourtCase> result = manager.createQuery(q)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CourtCase> cr = countQuery.from(CourtCase.class);
        Join<CourtCase, CaseApplicationXref> cj1 = cr.join("caseApplicationXrefs", JoinType.LEFT);
        Join<CourtCase, CaseStatus> cj2 = cr.join("caseStatus", JoinType.LEFT);
        Join<CourtCase, CaseType> cj3 = cr.join("caseType", JoinType.LEFT);
        countQuery.select(cb.count(cr)).where(getSearchCasesPredicates(cb, cr, cj1, cj2, cj3, applicationId, caseNumber, caseTypeCode, caseStatusCode, waterCourtCaseNumber));
        Long cnt = manager.createQuery(countQuery).getSingleResult();

        Page<CourtCase> page = new PageImpl<>(result, pageable, cnt);
        return page;

    }

    private Predicate[] getSearchCasesPredicates(CriteriaBuilder cb, Root<CourtCase> root, Join caseApplicationXrefs, Join caseStatus, Join caseType, String applicationId, String caseNumber, String caseTypeCode, String caseStatusCode, String waterCourtCaseNumber) {

        List<Predicate> predicates = new ArrayList<>();
        if(applicationId != null) {
            predicates.add(cb.like(caseApplicationXrefs.get("applicationId").as(String.class), applicationId));
        }
        if(caseNumber != null) {
            predicates.add(cb.like(root.get("id").as(String.class), caseNumber));
        }
        if(caseTypeCode != null) {
            predicates.add(cb.like(caseType.get("code"), caseTypeCode));
        }
        if(caseStatusCode != null) {
            predicates.add(cb.like(caseStatus.get("code"), caseStatusCode));
        }
        if(waterCourtCaseNumber != null) {
            predicates.add(cb.like(root.get("caseNumber"), waterCourtCaseNumber));
        }
        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);
        return pred;

    }

    private List<Order> getSearchCasesOrderBy(CriteriaBuilder cb, Root<CourtCase> root, Join caseApplicationXrefs, Join caseStatus, Join caseType, Join application, Join applicationType, CaseSearchSortColumn sortColumn, SortDirection sortDirection) {

        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if(sortColumn == CaseSearchSortColumn.CASENUMBER) {
                orderList.add(cb.asc(root.get("id")));
            } else if(sortColumn == CaseSearchSortColumn.CASETYPEDESCRIPTION) {
                orderList.add(cb.asc(caseType.get("description")));
            } else if(sortColumn == CaseSearchSortColumn.BASIN) {
                orderList.add(cb.asc(application.get("basin")));
            } else if(sortColumn == CaseSearchSortColumn.APPLICATIONID) {
                orderList.add(cb.asc(application.get("id")));
            } else if(sortColumn == CaseSearchSortColumn.COMPLETEAPPLICATIONTYPE) {
                orderList.add(cb.asc(applicationType.get("code")));
            } else { /* CaseSearchSortColumn.CASESTATUSDESCRIPTION */
                orderList.add(cb.asc(caseStatus.get("description")));
            }
        } else {
            if(sortColumn == CaseSearchSortColumn.CASENUMBER) {
                orderList.add(cb.desc(root.get("id")));
            } else if(sortColumn == CaseSearchSortColumn.CASETYPEDESCRIPTION) {
                orderList.add(cb.desc(caseType.get("description")));
            } else if(sortColumn == CaseSearchSortColumn.BASIN) {
                orderList.add(cb.desc(application.get("basin")));
            } else if(sortColumn == CaseSearchSortColumn.APPLICATIONID) {
                orderList.add(cb.desc(application.get("id")));
            } else if(sortColumn == CaseSearchSortColumn.COMPLETEAPPLICATIONTYPE) {
                orderList.add(cb.desc(applicationType.get("code")));
            } else { /* CaseSearchSortColumn.CASESTATUSDESCRIPTION */
                orderList.add(cb.desc(caseStatus.get("description")));
            }
        }
        orderList.add(cb.asc(root.get("id")));
        return orderList;

    }

}
