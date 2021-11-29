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

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.MailingJobSortColumn;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.ApplicationType;
import gov.mt.wris.models.MailingJob;
import gov.mt.wris.repositories.CustomMailingJobRepository;

public class CustomMailingJobRepositoryImpl implements CustomMailingJobRepository {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomMailingJobRepository.class);

    @PersistenceContext
    private EntityManager manager;

    public Page<MailingJob> searchMailingJobs(Pageable pageable,
        MailingJobSortColumn sortColumn,
        DescSortDirection sortDirection,
        String mailingJobNumber,
        String mailingJobHeader,
        String applicationId
    ) {
        LOGGER.info("Searching Mailing Jobs");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<MailingJob> query = cb.createQuery(MailingJob.class);

        Root<MailingJob> m = query.from(MailingJob.class);
        Join<MailingJob, Application> application = (Join) m.fetch("application", JoinType.INNER);
        Join<Application, ApplicationType> type = (Join) application.fetch("type", JoinType.INNER);

        Predicate[] predicates = getMailingJobPredicates(cb, m, mailingJobNumber, mailingJobHeader, applicationId);
        query.where(predicates);

        List<Order> orders = getMailingJobSort(cb, m, type, sortColumn, sortDirection);
        query.orderBy(orders);

        List<MailingJob> result = manager.createQuery(query)
                                    .setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MailingJob> jobCount = countQuery.from(MailingJob.class);
        countQuery.select(cb.count(jobCount))
            .where(getMailingJobPredicates(cb, jobCount, mailingJobNumber, mailingJobHeader, applicationId));
        
        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<MailingJob> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getMailingJobPredicates(CriteriaBuilder cb,
        Root<MailingJob> m,
        String mailingJobNumber,
        String mailingJobHeader,
        String applicationId
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if(mailingJobNumber != null) {
            predicates.add(cb.like(m.get("id").as(String.class), mailingJobNumber));
        }
        if(mailingJobHeader != null) {
            predicates.add(cb.like(m.get("header"), mailingJobHeader));
        }
        if(applicationId != null) {
            predicates.add(cb.like(m.get("applicationId").as(String.class), applicationId));
        }

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }

    private List<Order> getMailingJobSort(CriteriaBuilder cb,
        Root<MailingJob> m,
        Join appType,
        MailingJobSortColumn sortColumn,
        DescSortDirection sortDirection
    ) {
        List<Order> orderList = new ArrayList<>();
        if(sortDirection == DescSortDirection.ASC) {
            switch(sortColumn) {
                case APPLICATIONID:
                    orderList.add(cb.asc(m.get("applicationId")));
                    break;
                case APPLICATIONTYPEDESCRIPTION:
                    orderList.add(cb.asc(appType.get("code")));
                    break;
                case GENERATEDDATE:
                    orderList.add(cb.asc(m.get("dateGenerated")));
                    break;
                case MAILINGJOBNUMBER:
                    orderList.add(cb.asc(m.get("id")));
                    break;
                case MAILINGJOBHEADER:
                    orderList.add(cb.asc(m.get("header")));
                    break;
            }
        } else {
            switch(sortColumn) {
                case APPLICATIONID:
                    orderList.add(cb.desc(m.get("applicationId")));
                    break;
                case APPLICATIONTYPEDESCRIPTION:
                    orderList.add(cb.desc(appType.get("code")));
                    break;
                case GENERATEDDATE:
                    orderList.add(cb.desc(m.get("dateGenerated")));
                    break;
                case MAILINGJOBNUMBER:
                    orderList.add(cb.desc(m.get("id")));
                    break;
                case MAILINGJOBHEADER:
                    orderList.add(cb.desc(m.get("header")));
                    break;
            }
        }
        orderList.add(cb.asc(m.get("id")));
        return orderList;
    }
}
