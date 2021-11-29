package gov.mt.wris.repositories.Implementation;

import gov.mt.wris.dtos.*;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.CustomerXref;
import gov.mt.wris.models.OwnershipUpdate;
import gov.mt.wris.repositories.CustomOwnershipUpdateRepository;
import gov.mt.wris.utils.Helpers;
import org.hibernate.Session;
import org.hibernate.jpa.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static gov.mt.wris.constants.Constants.*;
import static gov.mt.wris.utils.Helpers.buildCompleteWaterRightNumber;

public class CustomOwnershipUpdateRepositoryImpl implements CustomOwnershipUpdateRepository {

    public static Logger LOGGER = LoggerFactory.getLogger(CustomOwnershipUpdateRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<OwnershipUpdateSearchResultDto> searchOwnershipUpdatesWithCounts(Pageable pageable, OwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, String ownershipUpdateId, String ownershipUpdateType, String waterRightNumber, LocalDate dateReceived, LocalDate dateSale, LocalDate dateProcessed, LocalDate dateTerminated) {

        LOGGER.info("Search Ownership Updates");

        String where = buildOwnershipUpdatesWithCountsWhere(ownershipUpdateId, ownershipUpdateType, waterRightNumber, dateReceived, dateSale, dateProcessed, dateTerminated);
        String orderBy = buildOwnershipUpdatesWithCountsOrderBy(sortColumn, sortDirection);

        String select =
                " select  \n" +
                        "     osu.OWNR_UPDT_ID, \n" +
                        "     osu.TRN_TYP, \n" +
                        "     (CASE WHEN osu.TRN_TYP != '" + DOR_608_TRANSACTION_TYPE + "' THEN osu.dt_received ELSE null END) as dt_received, \n" +
                        "     (CASE WHEN osu.TRN_TYP = '" + DOR_608_TRANSACTION_TYPE + "' THEN osu.dt_received ELSE null END) as sale_dt, \n" +
                        "     (select count(*) from WRD_WTR_RGT_OWNSHIP_UPDT_XREFS where ownr_updt_id = osu.OWNR_UPDT_ID) as water_rights_count, \n" +
                        "     refs.RV_MEANING, \n" +
                        "     osu.DT_TERMINATED, \n" +
                        "     osu.DT_PROCESSED \n" +
                        "  from  \n" +
                        "    WRD_OWNERSHIP_UPDATES osu \n" +
                        "  left outer join \n" +
                        "    WRD_REF_CODES refs \n" +
                        "       on (osu.TRN_TYP = refs.RV_LOW_VALUE and refs.RV_DOMAIN = '" + OWNERSHIP_TRANSFER_DOMAIN + "') \n" +
                        " where 1=1 \n" +
                        where +
                        orderBy;

        String fullQuery =
                " SELECT * FROM ( \n"+
                        "   SELECT all_.*, \n" +
                        "   rownum rownum_ \n" +
                        "   FROM ( \n" +
                        select +
                        "   ) all_ \n" +
                        "   WHERE rownum <= :upperLimit \n" +
                        " ) \n" +
                        " WHERE rownum_ > :lowerLimit \n";

        String countSelect =
                " SELECT count(*) \n" +
                        "        FROM \n" +
                        "          WRD_OWNERSHIP_UPDATES osu  \n" +
                        "        WHERE 1=1 \n";

        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("upperLimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerLimit", pageable.getOffset());
        List<Object[]> results = q.getResultList();

        List<OwnershipUpdateSearchResultDto> searchResults = new ArrayList<>();
        for (Object[] res : results) {
            OwnershipUpdateSearchResultDto searchResult = new OwnershipUpdateSearchResultDto();
            searchResult = mapObjectToOwnershipUpdate(res);
            searchResults.add(searchResult);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countSelect + where)
                .getSingleResult())
                .longValue();

        Page<OwnershipUpdateSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;

    }

    private String buildOwnershipUpdatesWithCountsOrderBy(OwnershipUpdateSortColumn sortColumn, SortDirection sortDirection) {

        String dir = (sortDirection.getValue().equals("ASC"))?"ASC":"DESC";
        String order = " order by ";
        if(sortColumn == OwnershipUpdateSortColumn.OWNERSHIPUPDATETYPEVALUE) {
            order += " refs.RV_MEANING " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == OwnershipUpdateSortColumn.RECEIVEDDATE) {
            order += " osu.dt_received " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == OwnershipUpdateSortColumn.SALEDATE) {
            order += " osu.dt_received " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == OwnershipUpdateSortColumn.OWNERSHIPUPDATETYPE) {
            order += " osu.TRN_TYP " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else {
            order += " osu.OWNR_UPDT_ID " + dir;
        }
        return " " + order + " ";

    }

    private String buildOwnershipUpdatesWithCountsWhere(String ownershipUpdateId, String ownershipUpdateType, String waterRightNumber, LocalDate dateReceived, LocalDate dateSale, LocalDate dateProcessed, LocalDate dateTerminated) {

        List<String> whereConditions = new ArrayList<>();
        whereConditions.add(" \n");
        if (ownershipUpdateId != null)
            whereConditions.add(" AND osu.OWNR_UPDT_ID like '" + ownershipUpdateId +"' \n");
        if (ownershipUpdateType != null)
            whereConditions.add(" AND osu.TRN_TYP like '" + ownershipUpdateType +"' \n");
        if (dateReceived != null)
            whereConditions.add(" AND osu.dt_received like TO_DATE('" + dateReceived.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "', 'DD-MM-YYYY') \n");
        if (dateSale != null)
            whereConditions.add(" AND (osu.dt_received like TO_DATE('" + dateSale.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "', 'DD-MM-YYYY') AND osu.TRN_TYP = '" + DOR_608_TRANSACTION_TYPE + "') \n");
        if (dateProcessed != null)
            whereConditions.add(" AND osu.dt_processed like TO_DATE('" + dateProcessed.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "', 'DD-MM-YYYY') \n");
        if (dateTerminated != null)
            whereConditions.add(" AND osu.dt_terminated like TO_DATE('" + dateTerminated.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "', 'DD-MM-YYYY') \n");
        if (waterRightNumber != null)
            whereConditions.add(" and osu.OWNR_UPDT_ID in (select ownr_updt_id from WRD_WTR_RGT_OWNSHIP_UPDT_XREFS a, WRD_WATER_RIGHTS b where a.wrgt_id_seq = b.wrgt_id_seq and b.wtr_id like '" + waterRightNumber + "') \n");

        return whereConditions.stream().collect(Collectors.joining(" \n")) + " \n";

    }

    private OwnershipUpdateSearchResultDto mapObjectToOwnershipUpdate(Object[] res) {

        OwnershipUpdateSearchResultDto dto = new OwnershipUpdateSearchResultDto();
        dto.setOwnershipUpdateId(((BigDecimal) res[0]).longValue());
        dto.setOwnershipUpdateType((String)res[1]);
        if (res[2] != null)
            dto.setReceivedDate(((Timestamp) res[2]).toLocalDateTime().toLocalDate());
        if (res[3] != null)
            dto.setSaleDate(((Timestamp) res[3]).toLocalDateTime().toLocalDate());
        dto.setWaterRightCount(((BigDecimal) res[4]).intValue());
        dto.setOwnershipUpdateTypeValue((String)res[5]);
        if (res[6] != null) {
            dto.setDateTerminated(((Timestamp) res[6]).toLocalDateTime().toLocalDate());
        }
        if (res[7] != null) {
            dto.setDateProcessed(((Timestamp) res[7]).toLocalDateTime().toLocalDate());
        }

        return dto;
    }


    @Override
    public Page<OwnershipUpdatesForContactSearchResultDto> getOwnershipUpdatesForContact(Pageable pageable, OwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection, Long contactId) {

        LOGGER.info("Get Ownership Updates for a Contact");

        int pageSize = 25;
        String orderBy = buildOwnershipUpdatesForContactOrderBy(sortColumn, sortDirection);
        String where = " WHERE osu.OWNR_UPDT_ID in (select ownr_updt_id from WRD_CUSTM_OWNERSHIP_UPDT_XREFS a, WRD_CUSTOMERS b where a.cust_id_seq = b.cust_id_seq and b.cust_id_seq = " + contactId + ") \n";
        String select =
                " SELECT  \n" +
                        "    osu.OWNR_UPDT_ID as ownership_update_id, \n" +
                        "    osu.TRN_TYP  as ownership_update_type, \n" +
                        "    refs.RV_MEANING as ownership_update_type_val, \n" +
                        "    (CASE WHEN osu.TRN_TYP != '" + DOR_608_TRANSACTION_TYPE + "' THEN osu.dt_received ELSE null END) as dt_received, \n" +
                        "    (CASE WHEN osu.TRN_TYP = '" + DOR_608_TRANSACTION_TYPE + "' THEN osu.dt_received ELSE null END) as sale_dt, \n" +
                        "    (select count(*) from WRD_WTR_RGT_OWNSHIP_UPDT_XREFS where ownr_updt_id = osu.OWNR_UPDT_ID) as water_rights_count \n" +
                        " FROM  \n" +
                        "    WRD_OWNERSHIP_UPDATES osu \n" +
                        " JOIN \n" +
                        "    WRD_REF_CODES refs \n" +
                        "       on (osu.TRN_TYP = refs.RV_LOW_VALUE and refs.RV_DOMAIN = '" + OWNERSHIP_TRANSFER_DOMAIN + "') \n" +
                        where +
                        orderBy;

        String fullQuery =
                " SELECT * FROM ( \n"+
                        "   SELECT all_.*, \n" +
                        "   rownum rownum_ \n" +
                        "   FROM ( \n" +
                        select +
                        "   ) all_ \n" +
                        "   WHERE rownum <= :upperLimit \n" +
                        " ) \n" +
                        " WHERE rownum_ > :lowerLimit \n";

        String countSelect =
                " SELECT count(*) \n" +
                        " FROM \n" +
                        "    WRD_OWNERSHIP_UPDATES osu  \n" +
                        where;

        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("upperLimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerLimit", pageable.getOffset());
        List<Object[]> results = q.getResultList();

        List<OwnershipUpdatesForContactSearchResultDto> searchResults = new ArrayList<>();
        for (Object[] res : results) {
            OwnershipUpdatesForContactSearchResultDto searchResult = new OwnershipUpdatesForContactSearchResultDto();
            searchResult = mapObjectToOwnershipUpdatesForContact(res);
            searchResults.add(searchResult);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countSelect)
                .getSingleResult())
                .longValue();

        Page<OwnershipUpdatesForContactSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;

    }

    private String buildOwnershipUpdatesForContactOrderBy(OwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection) {

        String dir = (sortDirection.getValue().equals("ASC"))?"ASC":"DESC";
        String order = " order by ";
        if(sortColumn == OwnershipUpdatesForContactSortColumn.OWNERSHIPUPDATETYPEVALUE) {
            order += " refs.RV_MEANING " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == OwnershipUpdatesForContactSortColumn.RECEIVEDDATE) {
            order += " osu.dt_received " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == OwnershipUpdatesForContactSortColumn.SALEDATE) {
            order += " osu.dt_received " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == OwnershipUpdatesForContactSortColumn.OWNERSHIPUPDATETYPE) {
            order += " osu.TRN_TYP " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else {
            order += " osu.OWNR_UPDT_ID " + dir;
        }
        return order;

    }

    private OwnershipUpdatesForContactSearchResultDto mapObjectToOwnershipUpdatesForContact(Object[] res) {

        OwnershipUpdatesForContactSearchResultDto dto = new OwnershipUpdatesForContactSearchResultDto();
        dto.setOwnershipUpdateId(((BigDecimal) res[0]).longValue());
        dto.setOwnershipUpdateType((String)res[1]);
        dto.setOwnershipUpdateTypeVal((String)res[2]);
        if (res[3] != null)
            dto.setReceivedDate(((Timestamp) res[3]).toLocalDateTime().toLocalDate());
        if (res[4] != null)
            dto.setSaleDate(((Timestamp) res[4]).toLocalDateTime().toLocalDate());
        dto.setWaterRightCount(((BigDecimal) res[5]).intValue());
        return dto;

    }


    @Override
    public Page<OwnershipUpdateSellersAndBuyerSearchResultDto> searchOwnershipUpdateBuyersWithCounts(Pageable pageable, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId) {

        LOGGER.info("Search Ownership Update Buyers");

        String where = buildOwnershipUpdateBuyersWithCountsWhere(lastName, firstName, contactId);
        String orderBy = buildOwnershipUpdateBuyersWithCountsOrderBy(sortColumn, sortDirection);

        String select =
                " select distinct \n" +
                        "     s.contact_id,  \n" +
                        "     s.last_name,  \n" +
                        "     s.first_name,  \n" +
                        "     s.middle_init,  \n" +
                        "     s.suffix,  \n" +
                        "     s.role,  \n" +
                        "     s.ownershipupdate_count \n" +
                        " from \n" +
                        "     (select b.cust_id_seq as contact_id, \n" +
                        "            b.lst_nm_or_busn_nm as last_name, \n" +
                        "            b.fst_nm as first_name, \n" +
                        "            b.mid_int as middle_init, \n" +
                        "            b.sufx as suffix, \n" +
                        "            a.role as role, \n" +
                        "    (select count(DISTINCT OWNR_UPDT_ID) from WRD_CUSTM_OWNERSHIP_UPDT_XREFS where cust_id_seq = b.cust_id_seq and role = '" + WTR_XREF_BUYER_ROLE + "') as ownershipupdate_count \n" +
                        "     from  \n" +
                        "       WRD_CUSTM_OWNERSHIP_UPDT_XREFS a \n" +
                        "     inner join   \n" +
                        "       WRD_CUSTOMERS b \n" +
                        "          on (b.cust_id_seq = a.cust_id_seq and a.role = '" + WTR_XREF_BUYER_ROLE + "') \n" +
                        "     where 1=1 \n" +
                        where +
                        "     ) s \n" +
                        orderBy;

        String fullQuery =
                " SELECT * FROM ( \n"+
                        "   SELECT all_.*, \n" +
                        "   rownum rownum_ \n" +
                        "   FROM ( \n" +
                        select +
                        "   ) all_ \n" +
                        "   WHERE rownum <= :upperLimit \n" +
                        " ) \n" +
                        " WHERE rownum_ > :lowerLimit \n";

        String countSelect =
                " SELECT count(distinct a.cust_id_seq) \n" +
                        "     from  \n" +
                        "       WRD_CUSTM_OWNERSHIP_UPDT_XREFS a \n" +
                        "     inner join   \n" +
                        "       WRD_CUSTOMERS b \n" +
                        "          on (b.cust_id_seq = a.cust_id_seq and a.role = '" + WTR_XREF_BUYER_ROLE + "') \n" +
                        " WHERE 1=1 \n";

        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("upperLimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerLimit", pageable.getOffset());
        List<Object[]> results = q.getResultList();

        List<OwnershipUpdateSellersAndBuyerSearchResultDto> searchResults = new ArrayList<>();
        for (Object[] res : results) {
            OwnershipUpdateSellersAndBuyerSearchResultDto searchResult = new OwnershipUpdateSellersAndBuyerSearchResultDto();
            searchResult.setContactId(((BigDecimal) res[0]).longValue());
            searchResult.setLastName((String)res[1]);
            searchResult.setFirstName((String)res[2]);
            searchResult.setName(Helpers.buildName((String) res[1], (String) res[2], (String) res[3], (String) res[4]));
            searchResult.setCount(((BigDecimal) res[6]).intValue());
            searchResults.add(searchResult);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countSelect + where)
                .getSingleResult())
                .longValue();

        Page<OwnershipUpdateSellersAndBuyerSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;
    }

    private String buildOwnershipUpdateBuyersWithCountsWhere(String lastName, String firstName, String contactId) {

        List<String> whereConditions = new ArrayList<>();
        whereConditions.add(" \n");
        if (contactId != null)
            whereConditions.add(" AND a.cust_id_seq like '" + contactId +"' \n");
        if (lastName != null)
            whereConditions.add(" AND b.lst_nm_or_busn_nm like '" + lastName +"' \n");
        if (firstName != null)
            whereConditions.add(" AND b.fst_nm like '" + firstName + "' \n");

        return whereConditions.stream().collect(Collectors.joining(" \n")) + " \n";
    }

    private String buildOwnershipUpdateBuyersWithCountsOrderBy(OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection) {

        String dir = (sortDirection.getValue().equals("ASC"))?"ASC":"DESC";
        String order = " order by ";
        if(sortColumn == OwnershipUpdateBuyersSortColumn.NAME) {
            order += " s.last_name " + dir;
            order += ", s.first_name " + dir;
            order += ", s.middle_init " + dir;
            order += ", s.suffix " + dir;
            order += ", s.contact_id " + SortDirection.ASC.toString();
        } else {
            order += " s.contact_id " + dir;
        }
        return " " + order + " ";

    }


    @Override
    public Page<Customer> getOwnershipUpdateBuyers(Pageable pageable, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId) {

        LOGGER.info("Get Ownership Update Buyers Repository");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Customer> q = cb.createQuery(Customer.class);

        Root<Customer> root = q.from(Customer.class);
        Join customerXref = (Join) root.fetch("customerXref", JoinType.INNER);
        Join ownershipUpdates = (Join) customerXref.fetch("ownershipUpdate", JoinType.INNER);
        q.where(getOwnershipUpdateBuyersPredicates(cb, root, customerXref, ownershipUpdates, ownershipUpdateId));

        List<Order> orderList = getOwnershipUpdateBuyersOrderBy(cb, root, customerXref, ownershipUpdates, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<Customer> result = manager.createQuery(q)
                .setHint(QueryHints.HINT_READONLY, true)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Customer> cr = countQuery.from(Customer.class);
        Join<Customer, CustomerXref> cj1 = cr.join("customerXref", JoinType.INNER);
        Join<CustomerXref, OwnershipUpdate> cj2 = cj1.join("ownershipUpdate", JoinType.INNER);
        countQuery.select(cb.count(cr))
                .where(getOwnershipUpdateBuyersPredicates(cb, cr, cj1, cj2, ownershipUpdateId));

        Long count = manager.createQuery(countQuery).getSingleResult();
        Page<Customer> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getOwnershipUpdateBuyersPredicates(CriteriaBuilder cb, Root<Customer> root, Join customerXref, Join ownershipUpdate, Long ownershipUpdateId) {

        List<Predicate> predicates = new ArrayList<>();
        if(ownershipUpdateId != 0) {
            predicates.add(cb.like(ownershipUpdate.get("ownerUpdateId").as(String.class), ownershipUpdateId.toString()));
        }
        predicates.add(cb.like(customerXref.get("role"), WTR_XREF_BUYER_ROLE));

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;

    }

    private List<Order> getOwnershipUpdateBuyersOrderBy(CriteriaBuilder cb, Root<Customer> root, Join customerXref, Join ownershipUpdates, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection) {

        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if (sortColumn == OwnershipUpdateBuyersSortColumn.NAME){
                orderList.add(cb.asc(root.get("lastName")));
                orderList.add(cb.asc(root.get("firstName")));
                orderList.add(cb.asc(root.get("middleInitial")));
                orderList.add(cb.asc(root.get("suffix")));
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
            } else if(sortColumn == OwnershipUpdateBuyersSortColumn.ENDDATE) {
                orderList.add(cb.asc(customerXref.get("endDate")));
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
            } else {
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
                orderList.add(cb.asc(root.get("lastName")));
                orderList.add(cb.asc(root.get("firstName")));
                orderList.add(cb.asc(root.get("middleInitial")));
                orderList.add(cb.asc(root.get("suffix")));
            }
        } else {
            if (sortColumn == OwnershipUpdateBuyersSortColumn.NAME){
                orderList.add(cb.desc(root.get("lastName")));
                orderList.add(cb.desc(root.get("firstName")));
                orderList.add(cb.desc(root.get("middleInitial")));
                orderList.add(cb.desc(root.get("suffix")));
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
            } else if(sortColumn == OwnershipUpdateBuyersSortColumn.ENDDATE) {
                orderList.add(cb.desc(customerXref.get("endDate")));
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
            } else {
                orderList.add(cb.desc(ownershipUpdates.get("ownerUpdateId")));
                orderList.add(cb.asc(root.get("lastName")));
                orderList.add(cb.asc(root.get("firstName")));
                orderList.add(cb.asc(root.get("middleInitial")));
                orderList.add(cb.asc(root.get("suffix")));
            }
        }
        return orderList;

    }


    @Override
    public Page<OwnershipUpdateSellersAndBuyerSearchResultDto> searchOwnershipUpdateSellersWithCounts(Pageable pageable, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId) {

        LOGGER.info("Search Ownership Update Sellers");

        String where = buildOwnershipUpdateSellersWithCountsWhere(lastName, firstName, contactId);
        String orderBy = buildOwnershipUpdateSellersWithCountsOrderBy(sortColumn, sortDirection);

        String select =
                " select distinct \n" +
                        "     s.contact_id,  \n" +
                        "     s.last_name,  \n" +
                        "     s.first_name,  \n" +
                        "     s.middle_init,  \n" +
                        "     s.suffix,  \n" +
                        "     s.role,  \n" +
                        "     s.ownershipupdate_count \n" +
                        " from \n" +
                        "     (select b.cust_id_seq as contact_id, \n" +
                        "            b.lst_nm_or_busn_nm as last_name, \n" +
                        "            b.fst_nm as first_name, \n" +
                        "            b.mid_int as middle_init, \n" +
                        "            b.sufx as suffix, \n" +
                        "            a.role as role, \n" +
                        "            (select count(DISTINCT OWNR_UPDT_ID) from WRD_CUSTM_OWNERSHIP_UPDT_XREFS where cust_id_seq = b.cust_id_seq and role = '" + WTR_XREF_SELLER_ROLE + "') as ownershipupdate_count \n" +
                        "     from  \n" +
                        "       WRD_CUSTM_OWNERSHIP_UPDT_XREFS a \n" +
                        "     inner join   \n" +
                        "       WRD_CUSTOMERS b \n" +
                        "          on (b.cust_id_seq = a.cust_id_seq and a.role = '" + WTR_XREF_SELLER_ROLE + "') \n" +
                        "     where 1=1\n" +
                        where +
                        "     ) s \n" +
                        orderBy;

        String fullQuery =
                " SELECT * FROM ( \n"+
                        "   SELECT all_.*, \n" +
                        "   rownum rownum_ \n" +
                        "   FROM ( \n" +
                        select +
                        "   ) all_ \n" +
                        "   WHERE rownum <= :upperLimit \n" +
                        " ) \n" +
                        " WHERE rownum_ > :lowerLimit \n";

        String countSelect =
                " SELECT count(distinct a.cust_id_seq) \n" +
                        "     from  \n" +
                        "       WRD_CUSTM_OWNERSHIP_UPDT_XREFS a \n" +
                        "     inner join   \n" +
                        "       WRD_CUSTOMERS b \n" +
                        "          on (b.cust_id_seq = a.cust_id_seq and a.role = '" + WTR_XREF_SELLER_ROLE + "') \n" +
                        " WHERE 1=1 \n";

        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("upperLimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerLimit", pageable.getOffset());
        List<Object[]> results = q.getResultList();

        List<OwnershipUpdateSellersAndBuyerSearchResultDto> searchResults = new ArrayList<>();
        for (Object[] res : results) {
            OwnershipUpdateSellersAndBuyerSearchResultDto searchResult = new OwnershipUpdateSellersAndBuyerSearchResultDto();
            searchResult.setContactId(((BigDecimal) res[0]).longValue());
            searchResult.setLastName((String)res[1]);
            searchResult.setFirstName((String)res[2]);
            searchResult.setName(Helpers.buildName((String) res[1], (String) res[2], (String) res[3], (String) res[4]));
            searchResult.setCount(((BigDecimal) res[6]).intValue());
            searchResults.add(searchResult);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countSelect + where)
                .getSingleResult())
                .longValue();

        Page<OwnershipUpdateSellersAndBuyerSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;
    }

    private String buildOwnershipUpdateSellersWithCountsWhere(String lastName, String firstName, String contactId) {

        List<String> whereConditions = new ArrayList<>();
        whereConditions.add(" \n");
        if (contactId != null)
            whereConditions.add(" AND a.cust_id_seq like '" + contactId +"' \n");
        if (lastName != null)
            whereConditions.add(" AND b.lst_nm_or_busn_nm like '" + lastName +"' \n");
        if (firstName != null)
            whereConditions.add(" AND b.fst_nm like '" + firstName + "' \n");

        return whereConditions.stream().collect(Collectors.joining(" \n")) + " \n";
    }

    private String buildOwnershipUpdateSellersWithCountsOrderBy(OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection) {

        String dir = (sortDirection.getValue().equals("ASC"))?"ASC":"DESC";
        String order = " order by ";
        if(sortColumn == OwnershipUpdateSellersSortColumn.NAME) {
            order += " s.last_name " + dir;
            order += ", s.first_name " + dir;
            order += ", s.middle_init " + dir;
            order += ", s.suffix " + dir;
            order += ", s.contact_id " + SortDirection.ASC.toString();
        } else {
            order += " s.contact_id " + dir;
        }
        return " " + order + " ";

    }


    @Override
    public Page<Customer> getOwnershipUpdateSellers(Pageable pageable, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId) {

        LOGGER.info("Get Ownership Update Sellers Repository");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Customer> q = cb.createQuery(Customer.class);

        Root<Customer> root = q.from(Customer.class);
        Join customerXref = (Join) root.fetch("customerXref", JoinType.INNER);
        Join ownershipUpdates = (Join) customerXref.fetch("ownershipUpdate", JoinType.INNER);
        q.where(getOwnershipUpdateSellersPredicates(cb, root, customerXref, ownershipUpdates, ownershipUpdateId));

        List<Order> orderList = getOwnershipUpdateSellersOrderBy(cb, root, customerXref, ownershipUpdates, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<Customer> result = manager.createQuery(q)
                .setHint(QueryHints.HINT_READONLY, true)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Customer> cr = countQuery.from(Customer.class);
        Join<Customer, CustomerXref> cj1 = cr.join("customerXref", JoinType.INNER);
        Join<CustomerXref, OwnershipUpdate> cj2 = cj1.join("ownershipUpdate", JoinType.INNER);
        countQuery.select(cb.count(cr))
                .where(getOwnershipUpdateSellersPredicates(cb, cr, cj1, cj2, ownershipUpdateId));

        Long count = manager.createQuery(countQuery).getSingleResult();
        Page<Customer> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;

    }

    private Predicate[] getOwnershipUpdateSellersPredicates(CriteriaBuilder cb, Root<Customer> root, Join customerXref, Join ownershipUpdate, Long ownershipUpdateId) {

        List<Predicate> predicates = new ArrayList<>();
        if(ownershipUpdateId != 0) {
            predicates.add(cb.like(ownershipUpdate.get("ownerUpdateId").as(String.class), ownershipUpdateId.toString()));
        }
        predicates.add(cb.like(customerXref.get("role"), WTR_XREF_SELLER_ROLE));

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;

    }

    private List<Order> getOwnershipUpdateSellersOrderBy(CriteriaBuilder cb, Root<Customer> root, Join customerXref, Join ownershipUpdates, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection) {

        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if (sortColumn == OwnershipUpdateSellersSortColumn.NAME){
                orderList.add(cb.asc(root.get("lastName")));
                orderList.add(cb.asc(root.get("firstName")));
                orderList.add(cb.asc(root.get("middleInitial")));
                orderList.add(cb.asc(root.get("suffix")));
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
            } else if(sortColumn == OwnershipUpdateSellersSortColumn.ENDDATE) {
                orderList.add(cb.asc(customerXref.get("endDate")));
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
            } else {
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
                orderList.add(cb.asc(root.get("lastName")));
                orderList.add(cb.asc(root.get("firstName")));
                orderList.add(cb.asc(root.get("middleInitial")));
                orderList.add(cb.asc(root.get("suffix")));
            }
        } else {
            if (sortColumn == OwnershipUpdateSellersSortColumn.NAME){
                orderList.add(cb.desc(root.get("lastName")));
                orderList.add(cb.desc(root.get("firstName")));
                orderList.add(cb.desc(root.get("middleInitial")));
                orderList.add(cb.desc(root.get("suffix")));
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
            } else if(sortColumn == OwnershipUpdateSellersSortColumn.ENDDATE) {
                orderList.add(cb.desc(customerXref.get("endDate")));
                orderList.add(cb.asc(ownershipUpdates.get("ownerUpdateId")));
            } else {
                orderList.add(cb.desc(ownershipUpdates.get("ownerUpdateId")));
                orderList.add(cb.asc(root.get("lastName")));
                orderList.add(cb.asc(root.get("firstName")));
                orderList.add(cb.asc(root.get("middleInitial")));
                orderList.add(cb.asc(root.get("suffix")));
            }
        }
        return orderList;

    }


    @Override
    public TreeMap<String, Integer> getOwnershipUpdateCountsForTransfer(BigDecimal ownerUpdateId) {

        LOGGER.info("Get Seller, Buyer and Water Rights counts for an Ownership Update Id");

        TreeMap<String, Integer> countsMap = new TreeMap<>();
        String selectCounts =
                " select seller.cnt as sellers, buyer.cnt as buyers, rights.cnt as rights \n" +
                        " from ( \n" +
                        "     select count(*) as cnt from ( \n" +
                        "         select coxref.* \n" +
                        "         from  \n" +
                        "            WRD_CUSTM_OWNERSHIP_UPDT_XREFS coxref \n" +
                        "         inner join \n" +
                        "            WRD_OWNERSHIP_UPDATES osu \n" +
                        "               on (coxref.ownr_updt_id = osu.ownr_updt_id and coxref.role = '" + WTR_XREF_SELLER_ROLE + "') \n" +
                        "         where osu.ownr_updt_id = :owner_update_id \n" +
                        "     ) \n" +
                        " ) seller, \n" +
                        " ( \n" +
                        "     select count(*) as cnt from ( \n" +
                        "         select coxref.* \n" +
                        "         from  \n" +
                        "            WRD_CUSTM_OWNERSHIP_UPDT_XREFS coxref \n" +
                        "         inner join \n" +
                        "            WRD_OWNERSHIP_UPDATES osu \n" +
                        "               on (coxref.ownr_updt_id = osu.ownr_updt_id and coxref.role = '" + WTR_XREF_BUYER_ROLE + "') \n" +
                        "         where osu.ownr_updt_id = :owner_update_id \n" +
                        "     ) \n" +
                        " ) buyer, \n" +
                        " ( \n" +
                        "     select count(*) as cnt \n" +
                        "         from  \n" +
                        "            WRD_WTR_RGT_OWNSHIP_UPDT_XREFS a \n" +
                        "         inner join \n" +
                        "            WRD_WATER_RIGHTS b \n" +
                        "               on (b.wrgt_id_seq = a.wrgt_id_seq and b.wrst_cd <> '" + WATER_RIGHT_STATUS_CODE_TERMINATED + "') \n" +
                        "         where a.ownr_updt_id = :owner_update_id \n" +
                        " ) rights \n";

        Query q = manager.createNativeQuery(selectCounts);
        q.setParameter("owner_update_id", ownerUpdateId);
        List<Object[]> results = q.getResultList();

        for (Object[] res : results) {

            countsMap.put("sellers", ((BigDecimal) res[0]).intValue());
            countsMap.put("buyers", ((BigDecimal) res[1]).intValue());
            countsMap.put("rights", ((BigDecimal) res[2]).intValue());

        }



        return countsMap;


    }


    @Override
    public Page<CustomerXref> getBuyersForOwnershipUpdate(Pageable pageable, BuyersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId) {

        LOGGER.info("Get Associated Buyers for Ownership Update");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<CustomerXref> q = cb.createQuery(CustomerXref.class);

        Root<CustomerXref> root = q.from(CustomerXref.class);
        Join<CustomerXref, OwnershipUpdate> ownershipUpdates = root.join("ownershipUpdate", JoinType.INNER);
        Join<CustomerXref, Customer> customers = (Join) root.fetch("customer", JoinType.INNER);

        q.where(getBuyersForOwnershipUpdatePredicates(cb, root, ownershipUpdates, customers, ownershipUpdateId));

        List<Order> orderList = getBuyersForOwnershipUpdateOrderBy(cb, root, ownershipUpdates, customers, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<CustomerXref> result = manager.createQuery(q)
                .setHint(QueryHints.HINT_READONLY, true)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CustomerXref> cx = countQuery.from(CustomerXref.class);
        Join<CustomerXref, OwnershipUpdate> cj1 = cx.join("ownershipUpdate", JoinType.INNER);
        Join<CustomerXref, Customer> cj2 = cx.join("customer", JoinType.INNER);
        countQuery.select(cb.count(cx))
                .where(getBuyersForOwnershipUpdatePredicates(cb, cx, cj1, cj2, ownershipUpdateId));

        Long count = manager.createQuery(countQuery).getSingleResult();
        Page<CustomerXref> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getBuyersForOwnershipUpdatePredicates(CriteriaBuilder cb, Root<CustomerXref> root, Join ownershipUpdates, Join customers, Long ownershipUpdateId) {

        List<Predicate> predicates = new ArrayList<>();
        if(ownershipUpdateId != 0) {
            predicates.add(cb.like(ownershipUpdates.get("ownerUpdateId").as(String.class), ownershipUpdateId.toString()));
        }
        predicates.add(cb.like(root.get("role"), WTR_XREF_BUYER_ROLE));

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;

    }

    private List<Order> getBuyersForOwnershipUpdateOrderBy(CriteriaBuilder cb, Root<CustomerXref> root, Join ownershipUpdates, Join customers, BuyersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection) {

        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if (sortColumn == BuyersForOwnershipUpdateSortColumn.NAME){
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
                orderList.add(cb.asc(customers.get("customerId")));
            } else if (sortColumn == BuyersForOwnershipUpdateSortColumn.CONTACTID){
                orderList.add(cb.asc(customers.get("customerId")));
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
            } else { /* BuyersForOwnershipUpdateSortColumn.STARTDATE */
                orderList.add(cb.asc(root.get("strDate")));
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
            }
        } else {
            if (sortColumn == BuyersForOwnershipUpdateSortColumn.NAME){
                orderList.add(cb.desc(customers.get("lastName")));
                orderList.add(cb.desc(customers.get("firstName")));
                orderList.add(cb.desc(customers.get("middleInitial")));
                orderList.add(cb.desc(customers.get("suffix")));
                orderList.add(cb.asc(customers.get("customerId")));
            } else if (sortColumn == BuyersForOwnershipUpdateSortColumn.CONTACTID){
                orderList.add(cb.desc(customers.get("customerId")));
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
            } else { /* BuyersForOwnershipUpdateSortColumn.STARTDATE */
                orderList.add(cb.desc(root.get("strDate")));
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
            }
        }
        return orderList;

    }


    @Override
    public Page<CustomerXref> getSellersForOwnershipUpdate(Pageable pageable, SellersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId) {

        LOGGER.info("Get Associated Sellers for Ownership Update");

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<CustomerXref> q = cb.createQuery(CustomerXref.class);

        Root<CustomerXref> root = q.from(CustomerXref.class);
        Join<CustomerXref, OwnershipUpdate> ownershipUpdates = root.join("ownershipUpdate", JoinType.INNER);
        Join<CustomerXref, Customer> customers = (Join) root.fetch("customer", JoinType.INNER);

        q.where(getSellersForOwnershipUpdatePredicates(cb, root, ownershipUpdates, customers, ownershipUpdateId));

        List<Order> orderList = getSellersForOwnershipUpdateOrderBy(cb, root, ownershipUpdates, customers, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<CustomerXref> result = manager.createQuery(q)
                .setHint(QueryHints.HINT_READONLY, true)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CustomerXref> cx = countQuery.from(CustomerXref.class);
        Join<CustomerXref, OwnershipUpdate> cj1 = cx.join("ownershipUpdate", JoinType.INNER);
        Join<CustomerXref, Customer> cj2 = cx.join("customer", JoinType.INNER);
        countQuery.select(cb.count(cx))
                .where(getSellersForOwnershipUpdatePredicates(cb, cx, cj1, cj2, ownershipUpdateId));

        Long count = manager.createQuery(countQuery).getSingleResult();
        Page<CustomerXref> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getSellersForOwnershipUpdatePredicates(CriteriaBuilder cb, Root<CustomerXref> root, Join ownershipUpdates, Join customers, Long ownershipUpdateId) {

        List<Predicate> predicates = new ArrayList<>();
        if(ownershipUpdateId != 0) {
            predicates.add(cb.like(ownershipUpdates.get("ownerUpdateId").as(String.class), ownershipUpdateId.toString()));
        }
        predicates.add(cb.like(root.get("role"), WTR_XREF_SELLER_ROLE));

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;

    }

    private List<Order> getSellersForOwnershipUpdateOrderBy(CriteriaBuilder cb, Root<CustomerXref> root, Join ownershipUpdates, Join customers, SellersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection) {

        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if (sortColumn == SellersForOwnershipUpdateSortColumn.NAME){
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
                orderList.add(cb.asc(customers.get("customerId")));
            } else if (sortColumn == SellersForOwnershipUpdateSortColumn.CONTACTID){
                orderList.add(cb.asc(customers.get("customerId")));
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
            } else { /* SellersForOwnershipUpdateSortColumn.ENDDATE */
                orderList.add(cb.asc(root.get("endDate")));
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
            }
        } else {
            if (sortColumn == SellersForOwnershipUpdateSortColumn.NAME){
                orderList.add(cb.desc(customers.get("lastName")));
                orderList.add(cb.desc(customers.get("firstName")));
                orderList.add(cb.desc(customers.get("middleInitial")));
                orderList.add(cb.desc(customers.get("suffix")));
                orderList.add(cb.asc(customers.get("customerId")));
            } else if (sortColumn == SellersForOwnershipUpdateSortColumn.CONTACTID){
                orderList.add(cb.desc(customers.get("customerId")));
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
            } else { /* SellersForOwnershipUpdateSortColumn.ENDDATE */
                orderList.add(cb.desc(root.get("endDate")));
                orderList.add(cb.asc(customers.get("lastName")));
                orderList.add(cb.asc(customers.get("firstName")));
                orderList.add(cb.asc(customers.get("middleInitial")));
                orderList.add(cb.asc(customers.get("suffix")));
            }
        }
        return orderList;

    }

    @Override
    public Page<PopulateByGeocodesSearchResultDto> getWaterRightsByGeocode(Pageable pageable, PopulateByGeocodesSortColumn sortColumn, SortDirection sortDirection, Long ownerUpdateId) {

        LOGGER.info("Search Water Rights by Geocode");

        String orderBy = buildGetWaterRightsByGeocodeOrderBy(sortColumn, sortDirection);
        String select =
                " SELECT DISTINCT W.WRGT_ID_SEQ, W.WTR_ID, W.EXT, W.BOCA_CD, \n" +
                " (SELECT MAX(v.VERS_ID_SEQ) FROM wrd_versions v WHERE V.WRGT_ID_SEQ = W.WRGT_ID_SEQ) AS VERSION, \n" +
                " T.DESCR \n";
        String selectCount = "SELECT COUNT(*) \n";
        String from =
                " FROM WRD_WATER_RIGHTS W, WRD_WATER_RIGHT_TYPES T, \n" +
                " 	 WRD_WATER_RIGHT_STATUSES S, WRD_STAT_TYP_XREFS X, \n" +
                " 	 wrd_ownership_updates o \n" +
                " WHERE W.WRST_CD = S.WRST_CD \n" +
                " AND W.WRTE_CD = T.WRTE_CD \n" +
                " AND W.WRST_CD = X.WRST_CD \n" +
                " AND W.WRTE_CD = X.WRTE_CD \n" +
                " AND X.LOV_ITEM = 'Y' \n" +
                " AND o.OWNR_UPDT_ID = " + ownerUpdateId + " \n" +
                " AND ((o.trn_typ = '" + CD_608_TRANSACTION_TYPE + "' AND W.WRTE_CD = '" + WATER_RIGHT_TYPE_WTR_RESV + "') \n" +
                "   OR (o.trn_typ <> '" + CD_608_TRANSACTION_TYPE + "' AND W.WRTE_CD <> '" + WATER_RIGHT_TYPE_WTR_RESV + "')) \n" +
                " AND W.wrgt_id_seq IN \n" +
                "    ( \n" +
                " 	  SELECT DISTINCT GX.WRGT_ID_SEQ  \n" +
                " 	  FROM WRD_GEOCODE_WATER_RIGHT_XREFS GX \n" +
                " 	  WHERE GX.END_DT IS NULL \n" +
                " 	  AND GX.GOCD_ID_SEQ IN ( \n" +
                " 		SELECT DISTINCT GX1.GOCD_ID_SEQ  \n" +
                " 		FROM WRD_GEOCODE_WATER_RIGHT_XREFS GX1, \n" +
                " 		     WRD_WTR_RGT_OWNSHIP_UPDT_XREFS XREF1 \n" +
                " 		WHERE GX1.WRGT_ID_SEQ = XREF1.WRGT_ID_SEQ \n" +
                " 		AND GX1.END_DT IS NULL \n" +
                " 		AND XREF1.OWNR_UPDT_ID = " + ownerUpdateId + " \n" +
                "       ) \n" +
                " 	   MINUS \n" +
                " 		SELECT DISTINCT XREF2.WRGT_ID_sEQ \n" +
                " 		FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS XREF2 \n" +
                " 		WHERE XREF2.OWNR_UPDT_ID = " + ownerUpdateId + " \n" +
                " 	) \n";
        String fullQuery =
                " SELECT * FROM ( \n"+
                        "   SELECT all_.*, \n" +
                        "   rownum rownum_ \n" +
                        "   FROM ( \n" +
                        select +
                        from +
                        orderBy +
                        "   ) all_ \n" +
                        "   WHERE rownum <= :upperLimit \n" +
                        " ) \n" +
                        " WHERE rownum_ > :lowerLimit \n";

        String countSelect = selectCount + from;

        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("upperLimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerLimit", pageable.getOffset());
        List<Object[]> results = q.getResultList();

        List<PopulateByGeocodesSearchResultDto> searchResults = new ArrayList<>();
        for (Object[] res : results) {
            PopulateByGeocodesSearchResultDto dto = new PopulateByGeocodesSearchResultDto();
            dto.setWaterRightId(((BigDecimal) res[0]).longValue());
            dto.setWaterRightNumber(((BigDecimal) res[1]).longValue());
            if (res[2]!=null)
               dto.setExt((String)res[2]);
            if (res[3]!=null)
               dto.setBasin((String)res[3]);
            dto.setVersion(((BigDecimal) res[4]).toString());
            dto.setTypeDescription((String)res[5]);
            dto.setCompleteWaterRightNumber(buildCompleteWaterRightNumber(dto.getBasin(), dto.getWaterRightNumber().toString(), dto.getExt()));
            searchResults.add(dto);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countSelect )
                .getSingleResult())
                .longValue();

        Page<PopulateByGeocodesSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);
        return resultPage;

    }

    private String buildGetWaterRightsByGeocodeOrderBy(PopulateByGeocodesSortColumn sortColumn, SortDirection sortDirection) {

        String dir = (sortDirection.getValue().equals("ASC"))?"ASC":"DESC";
        String order = " ORDER BY ";
        if(sortColumn == PopulateByGeocodesSortColumn.WATERRIGHTID) {
            order += " W.WRGT_ID_SEQ " + dir;
            order += ", W.WTR_ID " + SortDirection.ASC.toString();
            order += ", W.EXT " + SortDirection.ASC.toString();
            order += ", W.BOCA_CD " + SortDirection.ASC.toString();
            order += ", VERSION " + SortDirection.ASC.toString();
            order += ", T.DESCR " + SortDirection.ASC.toString();
        } else if(sortColumn == PopulateByGeocodesSortColumn.BASIN) {
            order += " W.BOCA_CD " + dir;
            order += ", W.WRGT_ID_SEQ " + SortDirection.ASC.toString();
            order += ", W.WTR_ID " + SortDirection.ASC.toString();
            order += ", W.EXT " + SortDirection.ASC.toString();
            order += ", VERSION " + SortDirection.ASC.toString();
            order += ", T.DESCR " + SortDirection.ASC.toString();
        } else if(sortColumn == PopulateByGeocodesSortColumn.WATERRIGHTNUMBER) {
            order += " W.WTR_ID " + dir;
            order += ", W.WRGT_ID_SEQ " + SortDirection.ASC.toString();
            order += ", W.EXT " + SortDirection.ASC.toString();
            order += ", W.BOCA_CD " + SortDirection.ASC.toString();
            order += ", VERSION " + SortDirection.ASC.toString();
            order += ", T.DESCR " + SortDirection.ASC.toString();
        } else if(sortColumn == PopulateByGeocodesSortColumn.VERSION) {
            order += " VERSION " + dir;
            order += ", W.WRGT_ID_SEQ " + SortDirection.ASC.toString();
            order += ", W.WTR_ID " + SortDirection.ASC.toString();
            order += ", W.EXT " + SortDirection.ASC.toString();
            order += ", W.BOCA_CD " + SortDirection.ASC.toString();
            order += ", T.DESCR " + SortDirection.ASC.toString();
        } else if(sortColumn == PopulateByGeocodesSortColumn.EXT) {
            order += " W.EXT " + dir;
            order += ", W.WRGT_ID_SEQ " + SortDirection.ASC.toString();
            order += ", W.WTR_ID " + SortDirection.ASC.toString();
            order += ", W.BOCA_CD " + SortDirection.ASC.toString();
            order += ", VERSION " + SortDirection.ASC.toString();
            order += ", T.DESCR " + SortDirection.ASC.toString();
        } else if (sortColumn == PopulateByGeocodesSortColumn.COMPLETEWATERRIGHTNUMBER) {
            order += " W.BOCA_CD " + dir;
            order += ", W.WTR_ID " + dir;
            order += ", W.EXT " + dir;
            order += ", W.WRGT_ID_SEQ " + SortDirection.ASC.toString();
            order += ", VERSION " + SortDirection.ASC.toString();
            order += ", T.DESCR " + SortDirection.ASC.toString();
        } else { // PopulateByGeocodesSortColumn.TYPEDESCRIPTION
            order += " T.DESCR " + dir;
            order += ", W.WRGT_ID_SEQ " + SortDirection.ASC.toString();
            order += ", W.WTR_ID " + SortDirection.ASC.toString();
            order += ", W.EXT " + SortDirection.ASC.toString();
            order += ", W.BOCA_CD " + SortDirection.ASC.toString();
            order += ", VERSION " + SortDirection.ASC.toString();
        }
        return " " + order + " ";

    }


    @Transactional
    @Override
    public TransferWaterRightsOwnershipResultDto transferWaterRightsOwnership(OwnershipUpdate model) {

        LOGGER.info("Transfer Water Rights for Ownership Update");

        TransferWaterRightsOwnershipResultDto dto = new TransferWaterRightsOwnershipResultDto();
        List<String> messages = new ArrayList<>();
        LocalDate dt_processed = LocalDate.now();
        Query query;

        String programNACountSql =
            " select count(distinct wrt.program) \n" +
            " from wrd_water_right_types wrt, \n" +
            "      wrd_water_rights wr, \n" +
            "      wrd_wtr_rgt_ownship_updt_xrefs xref \n" +
            " where wr.wrte_cd = wrt.wrte_cd \n" +
            " and wr.wrgt_id_seq = xref.wrgt_id_seq \n" +
            " and wrt.program = 'NA' \n" +
            " and  xref.ownr_updt_id = " + model.getOwnerUpdateId() + " \n";

        String buyer643CountSql =
            " select count(cust_id_Seq) \n" +
            " from WRD_CUSTM_OWNERSHIP_UPDT_XREFS \n" +
            " where role = 'BUY' \n" +
            " and ownr_updt_id = " + model.getOwnerUpdateId() + " \n";

        String waterRights643CountSql =
            " select count(xref.wrgt_id_seq)  \n" +
            " from wrd_wtr_rgt_ownship_updt_xrefs xref \n" +
            " where xref.ownr_updt_id = " + model.getOwnerUpdateId() + " \n";

        String programs641CountSql =
            " select count(distinct wr.wrte_cd) \n" +
            " from wrd_water_rights wr, \n" +
            "      wrd_wtr_rgt_ownship_updt_xrefs xref \n" +
            " where wr.wrgt_id_seq = xref.wrgt_id_seq \n" +
            " and  xref.ownr_updt_id = " + model.getOwnerUpdateId() + " \n";

        query = manager.createNativeQuery(programNACountSql);
        Object r_program_na_count = query.getSingleResult();
        Long program_na_count = ((BigDecimal) r_program_na_count).longValue();

        query = manager.createNativeQuery(buyer643CountSql);
        Object r_buyer_643_count = query.getSingleResult();
        Long buyer_643_count = ((BigDecimal) r_buyer_643_count).longValue();

        query = manager.createNativeQuery(waterRights643CountSql);
        Object r_water_rights_643_count = query.getSingleResult();
        Long water_rights_643_count = ((BigDecimal) r_water_rights_643_count).longValue();
        if (model.getTrnType().equals(SWR_643_608_TRANSACTION_TYPE)) {
            if (water_rights_643_count > 1) {
               throw new ValidationException("This 643 update has more than one Water Right. Only ONE Water Right is allowed to be processed by the 643 OWNERSHIP UPDATE.");
            }
        }

        if (model.getTrnType().equals(DI_641_608_TRANSACTION_TYPE) || model.getTrnType().equals(EWR_642_608_TRANSACTION_TYPE)) {

            query = manager.createNativeQuery(programs641CountSql);
            Object r_programs_641_count = query.getSingleResult();
            Long programs_641_count = ((BigDecimal) r_programs_641_count).longValue();
            if (programs_641_count > 1) {
                throw new ValidationException("This 641/642 update has water rights from more than one program. Only water rights from one program can be processed by the 641/642 OWNERSHIP UPDATE.");
            }

            if (program_na_count > 0) {
                P_ADD_641_608_EVENTS(model);
                messages.add("Transfer operation added a processed date and events to applications.");
            } else {
                messages.add("Transfer operation added a processed dt only.");
            }
            manager.createNativeQuery("UPDATE WRD_OWNERSHIP_UPDATES SET DT_PROCESSED = ? WHERE OWNR_UPDT_ID = ?")
                    .setParameter(1, dt_processed)
                    .setParameter(2, model.getOwnerUpdateId())
                    .executeUpdate();

        }

        if (model.getTrnType().equals(SWR_643_608_TRANSACTION_TYPE) && water_rights_643_count == 1 && buyer_643_count == 0) {

            manager.createNativeQuery("UPDATE WRD_OWNERSHIP_UPDATES SET DT_PROCESSED = ? WHERE OWNR_UPDT_ID = ?")
                    .setParameter(1, dt_processed)
                    .setParameter(2, model.getOwnerUpdateId())
                    .executeUpdate();

            if (program_na_count > 0) {
                if (P_ADD_643_608_EVENTS(model) > 0) {
                    messages.add("Transfer operation added a processed date and events to applications.");
                } else {
                    messages.add("No application attached to version #1 of water right.");
                }
            } else {
                messages.add("Transfer operation added a processed date.");
            }
        }

        int process_std_ouid = 0;
        if (!model.getTrnType().equals(DI_641_608_TRANSACTION_TYPE) &&
            !model.getTrnType().equals(EWR_642_608_TRANSACTION_TYPE) &&
            !model.getTrnType().equals(SWR_643_608_TRANSACTION_TYPE) &&
            !model.getTrnType().equals(ADM_TRANSACTION_TYPE)) {
            process_std_ouid = 1;
        } else if(model.getTrnType().equals(SWR_643_608_TRANSACTION_TYPE) && buyer_643_count > 0) {
            process_std_ouid = 1;
        }

        int rc = 0;
        if (process_std_ouid==1) {
            rc = callOwnershipUpdate(model.getOwnerUpdateId().longValue());
            switch (rc) {
                case 0:
                    messages.add("Ownership update successfully processed.");
                    break;
                case -1:
                    throw new ValidationException("An owner has been specified that is not a current owner of a water right and no chain of title has been specified. No changes have been made.");
                case -2:
                    throw new ValidationException("An error has occurred while attempting to process the ownership update. No changes have been made to the system.");
                case -3:
                    throw new ValidationException("Ownership update record not found.");
                default:
                    throw new ValidationException(String.format("Unknown return value from call to ownership update process: %s.", rc));
            }

        }

        /* P_CHECK_DOR_608 */
        if (model.getTrnType().equals(DOR_608_TRANSACTION_TYPE) && model.getPendingDor().equals("Y")) {
            manager.createNativeQuery("UPDATE WRD_OWNERSHIP_UPDATES SET PENDING_DOR = NULL WHERE OWNR_UPDT_ID = ?")
                    .setParameter(1, model.getOwnerUpdateId())
                    .executeUpdate();
        }

        dto.setOwnershipUpdateId(model.getOwnerUpdateId().longValue());
        dto.setMessages(messages);
        return dto;

    }

    private void P_ADD_641_608_EVENTS(OwnershipUpdate model) {

        Integer parentCount = getParentApplicationCountForOwnershipUpdate(model.getOwnerUpdateId());
        Integer childCount = getChildApplicationCountForOwnershipUpdate(model.getOwnerUpdateId());
        Query query;
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd[ HH:mm:ss.S]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MICRO_OF_SECOND, 0)
                .toFormatter();

        String additional642ProcessingSql =
                " select distinct xref.appl_id_Seq \n" +
                " from wrd_version_application_xrefs xref, \n" +
                "      wrd_versions vr \n" +
                " where xref.wrgt_id_seq = vr.wrgt_id_seq \n" +
                " and xref.vers_id_seq = vr.vers_id_seq \n" +
                " and vr.ver_typ = 'ORIG' \n" +
                " and vr.vers_id_seq = 1 \n" +
                " AND xref.wrgt_id_Seq IN ( \n" +
                "     SELECT WRGT_ID_sEQ \n" +
                "     FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS \n" +
                "     WHERE OWNR_UPDT_ID = " + model.getOwnerUpdateId() + ") \n";

        String childrenAppAndV1Sql =
                " select distinct xref.appl_id_Seq \n" +
                " from wrd_version_application_xrefs xref, \n" +
                "      wrd_versions vr \n" +
                " where xref.wrgt_id_seq = vr.wrgt_id_seq \n" +
                " and xref.vers_id_seq = vr.vers_id_seq \n" +
                " and vr.ver_typ = 'ORIG' \n" +
                " AND NOT EXISTS ( \n" +
                "    SELECT 1 \n" +
                "    FROM WRD_VERSIONS \n" +
                "    WHERE WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
                "    AND VER_TYP IN ('SPLT','ERSV')) \n" +
                " AND vr.vers_id_seq = 1 \n" +
                " AND xref.wrgt_id_Seq IN ( \n" +
                "    SELECT WRGT_ID_sEQ \n" +
                "    FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS \n" +
                "    WHERE OWNR_UPDT_ID = " + model.getOwnerUpdateId() + ") \n";

        String childrenWaterRightsSql =
                "  select distinct vr.wrgt_id_seq \n" +
                " from wrd_versions vr \n" +
                " where NOT EXISTS ( \n" +
                "   SELECT 1 \n" +
                "   FROM WRD_VERSIONS \n" +
                "   WHERE WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
                "   AND VER_TYP IN ('SPLT','ERSV')) \n" +
                " AND vr.vers_id_seq = 1 \n" +
                " AND vr.wrgt_id_Seq IN ( \n" +
                "    SELECT WRGT_ID_sEQ \n" +
                "    FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS \n" +
                "    WHERE OWNR_UPDT_ID = " + model.getOwnerUpdateId() + ") \n";

        String parentAppSql =
                " select distinct xref.appl_id_Seq \n" +
                " from wrd_version_application_xrefs xref, \n" +
                "      wrd_versions vr\n" +
                " where xref.wrgt_id_seq = vr.wrgt_id_seq \n" +
                " and xref.vers_id_seq = vr.vers_id_seq \n" +
                " and vr.ver_typ = 'ORIG' \n" +
                " AND EXISTS ( \n" +
                "    SELECT 1 \n" +
                "    FROM WRD_VERSIONS \n" +
                "    WHERE WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
                "    AND VER_TYP in('SPLT','ERSV')) \n" +
                " and vr.vers_id_seq = 1 \n" +
                " AND xref.wrgt_id_Seq IN ( \n" +
                "     SELECT WRGT_ID_sEQ \n" +
                "     FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS \n" +
                "     WHERE OWNR_UPDT_ID = " + model.getOwnerUpdateId() + ") \n";

        String parentIssuedDateSql =
                " select distinct MIN(ED.DT_OF_EVNT) \n" +
                " from wrd_version_application_xrefs xref, \n" +
                "      wrd_versions vr, \n" +
                "      wrd_event_dates ed \n" +
                " where xref.wrgt_id_seq = vr.wrgt_id_seq \n" +
                " and xref.vers_id_seq = vr.vers_id_seq \n" +
                " and xref.appl_id_seq = ed.appl_id_seq \n" +
                " and ed.evtp_cd = 'ISSU' \n" +
                " and vr.ver_typ = 'ORIG' \n" +
                " AND EXISTS   ( \n" +
                "    SELECT 1 \n" +
                "    FROM WRD_VERSIONS \n" +
                "    WHERE WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
                "    AND VER_TYP IN('SPLT','ERSV')) \n" +
                " and vr.vers_id_seq = 1 \n" +
                " AND xref.wrgt_id_Seq IN ( \n" +
                "    SELECT WRGT_ID_sEQ \n" +
                "    FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS \n" +
                "    WHERE OWNR_UPDT_ID = " + model.getOwnerUpdateId() + ") \n";

        String insertParentAppAndV1Sql =
                " INSERT INTO WRD_EVENT_DATES (EVTP_CD, DT_OF_EVNT, EVNT_COMT, RSPNS_DUE_DT, APPL_ID_SEQ) \n" +
                " select \n" +
                " 	distinct  ED.EVTP_CD, \n" +
                " 	ED.DT_OF_EVNT, \n" +
                " 	ED.EVNT_COMT, \n" +
                " 	ED.RSPNS_DUE_DT, \n" +
                " 	( \n" +
                " 		select \n" +
                " 		distinct xref.appl_id_Seq    \n" +
                " 		from \n" +
                " 			wrd_version_application_xrefs xref, \n" +
                " 			wrd_versions vr    \n" +
                " 		where \n" +
                " 			xref.wrgt_id_seq = vr.wrgt_id_seq    \n" +
                " 			and xref.vers_id_seq = vr.vers_id_seq    \n" +
                " 			and vr.ver_typ = 'ORIG'    \n" +
                " 			AND NOT EXISTS ( \n" +
                " 				SELECT \n" +
                " 					1       \n" +
                " 				FROM \n" +
                " 					WRD_VERSIONS       \n" +
                " 				WHERE \n" +
                " 					WRGT_ID_SEQ = VR.WRGT_ID_SEQ       \n" +
                " 					AND VER_TYP IN ( \n" +
                " 						'SPLT','ERSV' \n" +
                " 					) \n" +
                " 			)    \n" +
                " 			AND vr.vers_id_seq = 1    \n" +
                " 			AND xref.wrgt_id_Seq IN ( \n" +
                " 				SELECT \n" +
                " 					WRGT_ID_sEQ       \n" +
                " 				FROM \n" +
                " 					WRD_WTR_RGT_OWNSHIP_UPDT_XREFS       \n" +
                " 				WHERE \n" +
                " 					OWNR_UPDT_ID = ? \n" +
                " 			) \n" +
                " 	) as app_id_seq     \n" +
                " from \n" +
                " 	wrd_version_application_xrefs xref, \n" +
                " 	wrd_versions vr, \n" +
                " 	wrd_event_dates ed    \n" +
                " where \n" +
                " 	xref.wrgt_id_seq = vr.wrgt_id_seq    \n" +
                " 	and xref.vers_id_seq = vr.vers_id_seq    \n" +
                " 	and xref.appl_id_seq = ed.appl_id_seq    \n" +
                " 	and ed.evtp_cd in ( \n" +
                " 		'FRMR','ISSU','PCND','PCNR','VFCF' \n" +
                " 	)    \n" +
                " 	and vr.ver_typ = 'ORIG'    \n" +
                " 	AND EXISTS   ( \n" +
                " 		SELECT \n" +
                " 			1       \n" +
                " 		FROM \n" +
                " 			WRD_VERSIONS        \n" +
                " 		WHERE \n" +
                " 			WRGT_ID_SEQ = VR.WRGT_ID_SEQ        \n" +
                " 			AND VER_TYP IN( \n" +
                " 				'SPLT','ERSV' \n" +
                " 			) \n" +
                " 	)    \n" +
                " 	AND vr.vers_id_seq = 1    \n" +
                " 	AND xref.wrgt_id_Seq IN ( \n" +
                " 		SELECT \n" +
                " 			WRGT_ID_SEQ        \n" +
                " 		FROM \n" +
                " 			WRD_WTR_RGT_OWNSHIP_UPDT_XREFS        \n" +
                " 		WHERE \n" +
                " 			OWNR_UPDT_ID = ? \n" +
                " 	)";

        if (childCount > 0 && parentCount > 0) {

            /* get parent appl_id_seq */
            query = manager.createNativeQuery(parentAppSql);
            Object r_parent_app = query.getSingleResult();
            BigDecimal p_appl_id_seq = ((BigDecimal) r_parent_app);

            /* v1 applications children loop */
            query = manager.createNativeQuery(childrenAppAndV1Sql);
            List<Object[]> r_children = query.getResultList();
            for (Object c_res : r_children) {

                manager.createNativeQuery("DELETE FROM WRD_EVENT_DATES WHERE EVTP_CD = ? AND APPL_ID_SEQ = ?")
                        .setParameter(1, EVENT_FRMR)
                        .setParameter(2, ((BigDecimal) c_res))
                        .executeUpdate();

                manager.createNativeQuery(insertParentAppAndV1Sql)
                        .setParameter(1,((BigDecimal) c_res))
                        .setParameter(2,((BigDecimal) c_res))
                        .executeUpdate();

                if (model.getTrnType().equals(DI_641_608_TRANSACTION_TYPE)) {
                    manager.createNativeQuery("INSERT INTO WRD_EVENT_DATES (EVTP_CD, DT_OF_EVNT, APPL_ID_SEQ) VALUES(?,?,?)")
                            .setParameter(1, EVENT_F641)
                            .setParameter(2, model.getDateReceived())
                            .setParameter(3, ((BigDecimal) c_res))
                            .executeUpdate();
                } else {
                    manager.createNativeQuery("INSERT INTO WRD_EVENT_DATES (EVTP_CD, DT_OF_EVNT, APPL_ID_SEQ) VALUES(?,?,?)")
                            .setParameter(1, EVENT_F642)
                            .setParameter(2, model.getDateReceived())
                            .setParameter(3, ((BigDecimal) c_res))
                            .executeUpdate();
                }

            }

            if (model.getTrnType().equals(DI_641_608_TRANSACTION_TYPE)) {
                manager.createNativeQuery("INSERT INTO WRD_EVENT_DATES (EVTP_CD, DT_OF_EVNT, APPL_ID_SEQ) VALUES(?,?,?)")
                        .setParameter(1, EVENT_F641)
                        .setParameter(2, model.getDateReceived())
                        .setParameter(3, p_appl_id_seq)
                        .executeUpdate();
            } else {
                manager.createNativeQuery("INSERT INTO WRD_EVENT_DATES (EVTP_CD, DT_OF_EVNT, APPL_ID_SEQ) VALUES(?,?,?)")
                        .setParameter(1, EVENT_F642)
                        .setParameter(2, model.getDateReceived())
                        .setParameter(3, p_appl_id_seq)
                        .executeUpdate();
            }

            if (model.getTrnType().equals(EWR_642_608_TRANSACTION_TYPE)) {

                query = manager.createNativeQuery(parentIssuedDateSql);
                Object r_parent_issued_date = query.getSingleResult();
                LocalDate parent_issue_date = ((Timestamp) r_parent_issued_date).toLocalDateTime().toLocalDate();

                query = manager.createNativeQuery(childrenWaterRightsSql);
                List<Object[]> r_642_wr_children = query.getResultList();

                for (Object c_res : r_642_wr_children) {
                    manager.createNativeQuery("UPDATE WRD_WATER_RIGHTS SET WRST_CD = ? WHERE WRGT_ID_SEQ = ?")
                            .setParameter(1, WATER_RIGHT_SEVERED_CD)
                            .setParameter(2, ((BigDecimal) c_res))
                            .executeUpdate();

                    manager.createNativeQuery("UPDATE WRD_VERSIONS SET WRST_CD = ?, OPER_AUTHORITY = ? WHERE WRGT_ID_SEQ = ?")
                            .setParameter(1, WATER_RIGHT_SEVERED_CD)
                            .setParameter(2, parent_issue_date)
                            .setParameter(3, ((BigDecimal) c_res))
                            .executeUpdate();
                }

            }
        }

        /* additional 642 processing */
        if (childCount==1 && parentCount==0 && model.getTrnType().equals(EWR_642_608_TRANSACTION_TYPE)) {
            query = manager.createNativeQuery(additional642ProcessingSql);
            Object r_add_642_prod = query.getSingleResult();
            BigDecimal appl_id_seq = ((BigDecimal) r_add_642_prod);
            manager.createNativeQuery("INSERT INTO WRD_EVENT_DATES (EVTP_CD, DT_OF_EVNT, APPL_ID_SEQ) VALUES(?,?,?)")
                    .setParameter(1, EVENT_F642)
                    .setParameter(2, model.getDateReceived())
                    .setParameter(3, appl_id_seq)
                    .executeUpdate();
        }

        return;

    }

    private Long P_ADD_643_608_EVENTS(OwnershipUpdate model) {

        Long rc = Long.valueOf(-1); /* 'No application attached to version #1 of water right' */
        String parentAppSql =
            " select distinct xref.appl_id_seq  \n" +
            " from wrd_version_application_xrefs xref, \n" +
            "      wrd_versions vr \n" +
            " where xref.wrgt_id_seq = vr.wrgt_id_seq \n" +
            " and xref.vers_id_seq = vr.vers_id_seq \n" +
            " and vr.ver_typ = 'ORIG' \n" +
            " and vr.vers_id_seq = 1 \n" +
            " AND xref.wrgt_id_Seq IN ( \n" +
            "    SELECT WRGT_ID_sEQ \n" +
            "    FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS \n" +
            "    WHERE OWNR_UPDT_ID = " + model.getOwnerUpdateId() + ") \n";

        Query query = manager.createNativeQuery(parentAppSql);
        Object r_parent_appl_id_seq = query.getSingleResult();
        if (r_parent_appl_id_seq!=null) {
            BigDecimal parent_appl_id_seq = ((BigDecimal) r_parent_appl_id_seq);
            /* ADD 641 RECEIVED TO PARENT APP */
            manager.createNativeQuery("INSERT INTO WRD_EVENT_DATES (EVTP_CD, DT_OF_EVNT, APPL_ID_SEQ) VALUES(?,?,?)")
                    .setParameter(1, EVENT_F643)
                    .setParameter(2, model.getDateReceived().format(DateTimeFormatter.ofPattern(EVENT_DATE_FORMAT_OWNERSHIP_TRANSFER)))
                    .setParameter(3, parent_appl_id_seq)
                    .executeUpdate();
            rc = parent_appl_id_seq.longValue();
        }
        return rc;

    }

    private int callOwnershipUpdate(Long ownerUpdateId) {

        Session session = manager.unwrap(Session.class);
        Integer ac = session.doReturningWork(
                connection -> {
                    try (CallableStatement callableStatement = connection
                            .prepareCall(
                                    "{ ? = call WRD_COMMON_FUNCTIONS.UPDATE_OWNERSHIP(?) }")) {
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.setLong(2, ownerUpdateId);
                        callableStatement.execute();
                        return callableStatement.getInt(1);
                    }
                });
        return ac;

    }

    @Override
    public Integer getParentApplicationCountForOwnershipUpdate(BigDecimal ownerUpdateId) {

        Integer count = 0;
        String parentCountSql =
                " select count(distinct vr.wrgt_id_seq) \n" +
                " from wrd_version_application_xrefs xref, \n" +
                "      wrd_versions vr \n" +
                " where xref.wrgt_id_seq = vr.wrgt_id_seq \n" +
                " and xref.vers_id_seq = vr.vers_id_seq \n" +
                " and vr.ver_typ = 'ORIG' \n" +
                " AND EXISTS   ( \n" +
                "    SELECT 1 \n" +
                "    FROM WRD_VERSIONS \n" +
                "    WHERE WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
                "    AND VER_TYP IN ('SPLT','ERSV')) \n" +
                " and vr.vers_id_seq = 1 \n" +
                " AND xref.wrgt_id_Seq IN ( \n" +
                "    SELECT WRGT_ID_sEQ \n" +
                "    FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS \n" +
                "    WHERE OWNR_UPDT_ID = " + ownerUpdateId + ") \n";

        Query query = manager.createNativeQuery(parentCountSql);
        Object result = query.getSingleResult();
        count = ((BigDecimal) result).intValue();
        return count;

    }

    @Override
    public Integer getChildApplicationCountForOwnershipUpdate(BigDecimal ownerUpdateId) {

        Integer count = 0;
        String childCountSql =
                " select count(distinct vr.wrgt_id_seq) \n" +
                " from wrd_version_application_xrefs xref, \n" +
                "      wrd_versions vr \n" +
                " where xref.wrgt_id_seq = vr.wrgt_id_seq \n" +
                " and xref.vers_id_seq = vr.vers_id_seq \n" +
                " and vr.ver_typ = 'ORIG' \n" +
                " AND NOT EXISTS   ( \n" +
                "     SELECT 1 \n" +
                "     FROM WRD_VERSIONS \n" +
                "     WHERE WRGT_ID_SEQ = VR.WRGT_ID_SEQ \n" +
                "     AND vers_id_Seq > 1 \n" +
                "     AND VER_TYP IN ('SPLT','ERSV')) \n" +
                " and vr.vers_id_seq = 1 \n" +
                " AND xref.wrgt_id_Seq IN ( \n" +
                "     SELECT WRGT_ID_sEQ \n" +
                "     FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS \n" +
                "     WHERE OWNR_UPDT_ID = " + ownerUpdateId + ") \n";

        Query query = manager.createNativeQuery(childCountSql);
        Object result = query.getSingleResult();
        count = ((BigDecimal) result).intValue();
        return count;

    }

    @Override
    public boolean getAllApplicationsIncludedFlag(BigDecimal ownerUpdateId) {

        boolean flag = false; /* DEFAULT - not all applications are included */
        String allAppsIncludedSql =
        "   SELECT DISTINCT VAX.APPL_ID_SEQ \n" +
        "   FROM WRD_WTR_RGT_OWNSHIP_UPDT_XREFS WOUX \n" +
        "      , WRD_VERSION_APPLICATION_XREFS VAX \n" +
        "      , WRD_APPLICATIONS APPL \n" +
        "      , WRD_EVENT_DATES EVDT \n" +
        "   WHERE WOUX.WRGT_ID_SEQ = VAX.WRGT_ID_SEQ \n" +
        "      AND VAX.APPL_ID_SEQ = APPL.APPL_ID_SEQ \n" +
        "      AND APPL.APPL_ID_SEQ = EVDT.APPL_ID_SEQ \n" +
        "      AND APPL.APTP_CD IN ('606', '634', '635', '644') \n" +
        "      AND EVDT.EVTP_CD = 'ISSU' \n" +
        "      AND WOUX.OWNR_UPDT_ID = " + ownerUpdateId + " \n" +
        "      AND VAX.APPL_ID_SEQ NOT IN (SELECT AOUX.APPL_ID_SEQ \n" +
        "                                  FROM WRD_APPL_OWNSHIP_UPDT_XREFS AOUX \n" +
        "                                  WHERE AOUX.OWNR_UPDT_ID = " + ownerUpdateId + ") \n";

        Query query = manager.createNativeQuery(allAppsIncludedSql);
        List<Object[]> results = query.getResultList();
        if (results!=null && results.size()==0) flag = true;
        return flag;

    }

    @Override
    public boolean getWaterRightSharedWithOtherOwnershipUpdateFlag(OwnershipUpdate model) {

        boolean flag = false; /* DEFAULT - ouid does not have water right that is also associated with another ouid */
        String otherOuidSql =
        "  select distinct XREF.OWNR_UPDT_ID, to_char(updt.dt_processed, 'mm/dd/yyyy') dt_processed \n" +
        "   , WR.BOCA_CD||' '||WR.WTR_ID||DECODE(WR.EXT, NULL,NULL, ' '||WR.EXT) wr \n" +
        "   from wrd_wtr_rgt_ownship_updt_xrefs xref \n" +
        "   , wrd_ownership_updates updt \n" +
        "   , wrd_water_rights wr \n" +
        "   where xref.ownr_updt_id = updt.ownr_updt_id \n" +
        "   and xref.wrgt_id_seq = wr.wrgt_id_seq \n" +
        "   and updt.ownr_updt_id <> " + model.getOwnerUpdateId() + " \n" +
        "   and updt.dt_processed > TO_DATE('" + model.getDateReceived() + "', 'yyyy-mm-dd') \n" +
        "   and updt.dt_terminated is null \n" +
        "   and updt.trn_typ not in ('ADM') \n" +
        "   and xref.wrgt_id_Seq in \n" +
        "       (select wrgt_id_seq \n" +
        "         from wrd_wtr_rgt_ownship_updt_xrefs \n" +
        "         where ownr_updt_id = " + model.getOwnerUpdateId() + ") \n";

        Query query = manager.createNativeQuery(otherOuidSql);
        List<Object[]> results = query.getResultList();
        for (Object[] res : results) {
            if (res[0] != null) {
                /* a result means an associated water right for this
                ouid is also associated with another ouid */
                flag = true;
                break;
            }
        }
        return flag;
    }

}
