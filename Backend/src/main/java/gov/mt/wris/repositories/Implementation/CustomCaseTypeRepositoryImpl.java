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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.CaseType;
import gov.mt.wris.repositories.CustomCaseTypeRepository;
import gov.mt.wris.dtos.SortDirection;

@Repository
public class CustomCaseTypeRepositoryImpl implements CustomCaseTypeRepository{
    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<CaseType> getCaseTypes(Pageable pageable, String sortColumn, SortDirection sortDirection, String code, String description, String program) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<CaseType> q = cb.createQuery(CaseType.class);

        Root<CaseType> c = q.from(CaseType.class);
        Join join = (Join) c.fetch("programReference", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        if(code != null) {
            predicates.add(cb.like(c.get("code"), code));
        }
        if(description != null) {
            predicates.add(cb.like(c.get("description"), description));
        }
        if(program != null) {
            predicates.add(cb.like(c.get("program"), program));
        }
        q.where(predicates.toArray(new Predicate[predicates.size()]));

        List<Order> orderList = new ArrayList<Order>();
        if(sortDirection == SortDirection.ASC) {
            if(sortColumn.equals("meaning")) {
                orderList.add(cb.asc(join.get(sortColumn)));
            } else {
                orderList.add(cb.asc(c.get(sortColumn)));
            }
        }else{
            if(sortColumn.equals("meaning")) {
                orderList.add(cb.desc(join.get(sortColumn)));
            } else {
                orderList.add(cb.desc(c.get(sortColumn)));
            }
        }
        orderList.add(cb.asc(c.get("code")));
        q.orderBy(orderList);

        List<CaseType> result = manager.createQuery(q)
                                                    .setFirstResult((int) pageable.getOffset())
                                                    .setMaxResults(pageable.getPageSize())
                                                    .getResultList();


        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CaseType> caseTypeRootCount = countQuery.from(CaseType.class);
        countQuery.select(cb.count(caseTypeRootCount)).where(predicates.toArray(new Predicate[predicates.size()]));
        
        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<CaseType> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }
}