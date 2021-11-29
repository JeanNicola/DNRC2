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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.County;
import gov.mt.wris.models.SubdivisionCode;
import gov.mt.wris.repositories.CustomSubdivisionCodesRepository;

/**
 * Implementation of custom repository methods for SubdivisionCodes table.
 * 
 * @author Cesar.Zamorano
 *
 */
@Repository
public class CustomSubdivisionCodesRepositoryImpl implements CustomSubdivisionCodesRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(CustomSubdivisionCodesRepositoryImpl.class);

	@PersistenceContext
	EntityManager manager;

	@Override
	public Page<SubdivisionCode> getSubdivisionCodes(Pageable pageable, String sortColumn, SortDirection sortDirection,
			String code, BigDecimal countyId, String countyName, String dnrcName, String dorName) {
		LOGGER.info("Creating query for database.");

		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaQuery<SubdivisionCode> query = cb.createQuery(SubdivisionCode.class);

		Root<SubdivisionCode> root = query.from(SubdivisionCode.class);
		Join join = (Join) root.fetch("county", JoinType.INNER);

		query.where(getPredicates(cb, root, join, code, countyId, countyName, dnrcName, dorName));

        List<Order> orderList = new ArrayList<Order>();
		if (sortDirection == SortDirection.ASC) {
			if(sortColumn != "name") {
				orderList.add(cb.asc(root.get(sortColumn)));
			} else {
				orderList.add(cb.asc(join.get(sortColumn)));
			}
		} else {
			if(sortColumn != "name") {
				orderList.add(cb.desc(root.get(sortColumn)));
			} else {
				orderList.add(cb.desc(join.get(sortColumn)));
			}
		}
        orderList.add(cb.asc(root.get("code")));
        query.orderBy(orderList);

		List<SubdivisionCode> result = manager.createQuery(query).setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();

		LOGGER.info("Creating count query for database.");

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<SubdivisionCode> countRoot = countQuery.from(SubdivisionCode.class);
		Join<SubdivisionCode, County> joinRoot = countRoot.join("county", JoinType.INNER);
		countQuery.select(cb.count(countRoot))
				.where(getPredicates(cb, countRoot, joinRoot, code, countyId, countyName, dnrcName, dorName));

		Long count = manager.createQuery(countQuery).getSingleResult();

		Page<SubdivisionCode> resultPage = new PageImpl<>(result, pageable, count);
		LOGGER.info("Finishing access to database.");

		return resultPage;
	}

	private Predicate[] getPredicates(CriteriaBuilder cb, Root<SubdivisionCode> root,
			Join join, String code, BigDecimal countyId, String countyName, String dnrcName,
			String dorName) {

		List<Predicate> predicates = new ArrayList<>();
		if (code != null) {
			predicates.add(cb.like(root.get("code"), code));
		}
		if (countyId != null) {
			predicates.add(cb.equal(root.get("countyId"), countyId));
		}
		if (countyName != null) {
			predicates.add(cb.like(join.get("name"), countyName));
		}
		if (dnrcName != null) {
			predicates.add(cb.like(root.get("dnrcName"), dnrcName));
		}
		if (dorName != null) {
			predicates.add(cb.like(root.get("dorName"), dorName));
		}
		Predicate[] pred = new Predicate[predicates.size()];
		predicates.toArray(pred);

		return pred;
	}
}