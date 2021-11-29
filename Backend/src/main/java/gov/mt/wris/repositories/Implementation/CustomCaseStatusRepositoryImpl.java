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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.CaseStatus;
import gov.mt.wris.repositories.CustomCaseStatusRepository;

/**
 * Implementation of custom repository methods for Case Status table.
 *
 * @author Cesar.Zamorano
 */
@Repository
public class CustomCaseStatusRepositoryImpl implements CustomCaseStatusRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(CustomCaseStatusRepositoryImpl.class);

	@PersistenceContext
	EntityManager manager;

	@Override
	public Page<CaseStatus> getCaseStatuses(Pageable pageable, String sortColumn, SortDirection sortDirection,
			String code, String description) {
		LOGGER.info("Creating query for database.");

		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaQuery<CaseStatus> q = cb.createQuery(CaseStatus.class);

		Root<CaseStatus> c = q.from(CaseStatus.class);
		List<Predicate> predicates = new ArrayList<>();
		if (code != null) {
			predicates.add(cb.like(c.get("code"), code));
		}
		if (description != null) {
			predicates.add(cb.like(c.get("description"), description));
		}
		q.where(predicates.toArray(new Predicate[predicates.size()]));

        List<Order> orderList = new ArrayList<Order>();
		if (sortDirection == SortDirection.ASC) {
			orderList.add(cb.asc(c.get(sortColumn)));
		} else {
			orderList.add(cb.desc(c.get(sortColumn)));
		}
        orderList.add(cb.asc(c.get("code")));
        q.orderBy(orderList);

		List<CaseStatus> result = manager.createQuery(q).setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();

		LOGGER.info("Creating count query for database.");

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<CaseStatus> caseTypeRootCount = countQuery.from(CaseStatus.class);
		countQuery.select(cb.count(caseTypeRootCount)).where(predicates.toArray(new Predicate[predicates.size()]));

		Long count = manager.createQuery(countQuery).getSingleResult();

		Page<CaseStatus> resultPage = new PageImpl<>(result, pageable, count);
		LOGGER.info("Finishing access to database.");

		return resultPage;
	}

}
