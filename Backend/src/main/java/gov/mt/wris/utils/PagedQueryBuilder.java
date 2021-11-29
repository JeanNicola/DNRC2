package gov.mt.wris.utils;

import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PagedQueryBuilder {
    @PersistenceContext
    EntityManager manager;

    private static final String prefix =
        "SELECT *\n" +
        "FROM (\n" +
            "SELECT\n" +
                "all_.*,\n" +
                "ROWNUM ROWNUM_\n" +
            "FROM (\n";

    private static final String suffix =
            ") all_\n" +
            "WHERE ROWNUM <= :upper_\n" +
        ")\n" +
        "WHERE ROWNUM_ > :lower_";

    private static String orderBy(Sort sort) {
        if (sort == null) return "";
        return sort
            .stream()
            .map(order -> order.getProperty() + " " + order.getDirection().toString())
            .collect(Collectors.joining(",\n", "\nORDER BY\n", "\n"));
    }

    public Query createNativeQuery(Pageable pageable, String query) {
        return manager
            .createNativeQuery(prefix + query + orderBy(pageable.getSort()) + suffix)
            .setParameter("lower_", pageable.getOffset())
            .setParameter(
                "upper_",
                pageable.getPageSize() * (pageable.getPageNumber() + 1)
            );
    }
}
