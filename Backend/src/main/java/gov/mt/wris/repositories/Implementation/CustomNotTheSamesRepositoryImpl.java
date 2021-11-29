package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.NotTheSameSearchResultDto;
import gov.mt.wris.dtos.NotTheSameSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.repositories.CustomNotTheSamesRepository;
import gov.mt.wris.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomNotTheSamesRepositoryImpl implements CustomNotTheSamesRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(CustomNotTheSamesRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<NotTheSameSearchResultDto> searchNotTheSamesByCustomerId(Pageable pageable, NotTheSameSortColumn sortColumn, SortDirection sortDirection, BigDecimal contactId) {

        String select = "select b.NTSM_ID_SEQ, c.CUST_ID_SEQ, c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX \n";
        String selectCount = "select count(*) \n";
        String fromWhere = " from \n" +
                "  WRD_NOT_THE_SAMES a \n" +
                "inner join \n" +
                "  WRD_CUST_NOT_THE_SAME_XREFS b \n" +
                "    on a.NTSM_ID_SEQ = b.NTSM_ID_SEQ \n" +
                "inner join \n" +
                "  WRD_CUSTOMERS c \n" +
                "    on c.CUST_ID_SEQ = b.CUST_ID_SEQ \n" +
                "where 1=1 \n" +
                "and a.NTSM_ID_SEQ = (select ntsm_id_seq from WRD_CUST_NOT_THE_SAME_XREFS where cust_id_seq = " + contactId + ") \n" +
                "and c.CUST_ID_SEQ != " + contactId + " \n";
        String orderBy = getOrderByColumns(sortColumn, sortDirection);

        String dataQuery = select + fromWhere + orderBy;
        String dataQueryCount = selectCount + fromWhere;

        dataQuery = "SELECT * FROM (" +
            "SELECT all_.*, \n" +
            "rownum rownum_ \n" +
            "FROM ( \n" +
            dataQuery +
            ") all_ \n" +
            "WHERE rownum <= :upperlimit \n" +
            ")\n" +
            "WHERE rownum_ > :lowerlimit ";

        Query q = manager.createNativeQuery(dataQuery);
        q.setParameter("upperlimit", pageable.getPageSize()*(pageable.getPageNumber()+1));
        q.setParameter("lowerlimit", pageable.getOffset());

        List<Object[]> results = q.getResultList();
        List<NotTheSameSearchResultDto> ntssrds = new ArrayList<>();
        for(Object[] nts : results) {
            //b.NTSM_ID_SEQ, c.CUST_ID_SEQ, c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX
            NotTheSameSearchResultDto new_nts = new NotTheSameSearchResultDto();
            // NTSM_ID_SEQ
            new_nts.setNotthesameId(Long.valueOf(nts[0].toString()));
            // CUST_ID_SEQ
            new_nts.setContactId(Long.valueOf(nts[1].toString()));
            // LST_NM_OR_BUSN_NM
            new_nts.setLastName((String) nts[2]);
            // FST_NM
            if (nts[3]!=null) new_nts.setFirstName((String) nts[3]);
            // MID_INT
            if (nts[4]!=null) new_nts.setMiddleInitial((String) nts[4]);
            // SUFX
            if (nts[5]!=null) new_nts.setSuffix((String) nts[5]);
            new_nts.setName(Helpers.buildName((String) nts[2], (String) nts[3], (String) nts[4], (String) nts[5]));
            ntssrds.add(new_nts);
        }
        long count = ((BigDecimal) manager.createNativeQuery(dataQueryCount).getSingleResult()).longValue();
        Page<NotTheSameSearchResultDto> resultPage = new PageImpl<>(ntssrds, pageable, count);

        return resultPage;
    }

    private String  getOrderByColumns(NotTheSameSortColumn sortColumn, SortDirection sortDirection) {

        String sort = " order by \n";
        switch (sortColumn)
        {
            case NAME:
                sort += " c.LST_NM_OR_BUSN_NM " + sortDirection.getValue() + ", \n";
                sort += " c.FST_NM " + sortDirection.getValue() + ", \n";
                sort += " c.MID_INT " + sortDirection.getValue() + ", \n";
                sort += " c.SUFX " + sortDirection.getValue() + " \n";
                break;
            default:
                sort += " c.CUST_ID_SEQ " + sortDirection.getValue() + " \n";

        }
        return sort;

    }

}
