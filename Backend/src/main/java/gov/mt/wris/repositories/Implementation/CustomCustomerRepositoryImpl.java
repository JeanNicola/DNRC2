package gov.mt.wris.repositories.Implementation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import gov.mt.wris.dtos.*;
import gov.mt.wris.models.CustomerXref;
import gov.mt.wris.models.OwnershipUpdate;
import gov.mt.wris.models.WaterRighOwnshiptXref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import gov.mt.wris.models.Customer;
import gov.mt.wris.models.CustomerTypes;
import gov.mt.wris.models.Owner;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.repositories.CustomCustomerRepository;
import gov.mt.wris.utils.Helpers;

import static gov.mt.wris.constants.Constants.*;

@Repository
public class CustomCustomerRepositoryImpl implements CustomCustomerRepository {

    public static Logger LOGGER = LoggerFactory.getLogger(CustomCustomerRepositoryImpl.class);
    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<Customer> searchCustomers(Pageable pageable, CustomerSortColumn sortColumn, SortDirection sortDirection, String contactID, String lastName, String firstName, String firstLastName) {

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Customer> q = cb.createQuery(Customer.class);

        Root<Customer> root = q.from(Customer.class);
        Join<Customer, CustomerTypes> type = (Join) root.fetch("contactTypeValue", JoinType.INNER);

        q.where(getPredicates(cb, root, contactID, lastName, firstName, firstLastName));

        List<Order> orderList = getOrders(cb, root, type, sortColumn, sortDirection);
        q.orderBy(orderList);

        List<Customer> result = manager.createQuery(q)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Customer> countRoot = countQuery.from(Customer.class);
        countQuery.select(cb.count(countRoot))
                .where(getPredicates(cb, root, contactID, lastName, firstName, firstLastName));

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<Customer> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    @Override
    public Page<Customer> searchActiveSellersOwnershipUpdate(Pageable pageable, CustomerSortColumn sortColumn, SortDirection sortDirection, List<Long> waterRightIds, Long ownerUpdateId, String contactID, String lastName, String firstName) {

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Customer> q = cb.createQuery(Customer.class);
        Root<Customer> root = q.from(Customer.class);

        Join owners = (Join) root.fetch("owners", JoinType.INNER);
        Join waterRight = (Join) owners.fetch("waterRight", JoinType.INNER);
        Join ownershipUpdate = (Join) waterRight.fetch("ownershipUpdate", JoinType.INNER);

        Subquery sq = q.subquery(CustomerXref.class);
        Root<CustomerXref> subRoot = sq.from(CustomerXref.class);
        Join<CustomerXref, OwnershipUpdate> ownershipUpdateJoin = subRoot.join("ownershipUpdate", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(ownershipUpdateJoin.get("ownerUpdateId"), ownerUpdateId));
        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);
        sq.select(subRoot.get("customerId"));
        sq.where(pred);

        predicates = getPredicateList(cb, root, contactID, lastName, firstName, null);
        predicates.add(owners.get("endDate").isNull());
        predicates.add(cb.equal(ownershipUpdate.get("ownerUpdateId"), ownerUpdateId));
        predicates.add(root.get("customerId").in(sq).not());

        pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        q.where(pred);

        List<Order> orderList = getOrders(cb, root, null, sortColumn, sortDirection);
        q.orderBy(orderList);
        List<Customer> result = manager.createQuery(q)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Customer> countRoot = countQuery.from(Customer.class);
        Join<Customer, Owner> countOwners = countRoot.join("owners", JoinType.INNER);
        Join<Owner, WaterRighOwnshiptXref> countWaterRight = countOwners.join("waterRight", JoinType.INNER);

        predicates = getPredicateList(cb, countRoot, contactID, lastName, firstName, null);
        predicates.add(countOwners.get("endDate").isNull());
        predicates.add(countWaterRight.get("waterRightId").in(waterRightIds));

        pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        countQuery.select(cb.countDistinct(countRoot)).where(pred);
        Long count = manager.createQuery(countQuery).getSingleResult();
        Page<Customer> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    @Override
    public Page<Customer> searchCustomersByWaterRights(Pageable pageable, CustomerSortColumn sortColumn, SortDirection sortDirection, List<Long> waterRightIds, String contactID, String lastName, String firstName) {

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Customer> q = cb.createQuery(Customer.class);

        Root<Customer> root = q.from(Customer.class);
        Join<Customer, Owner> owners = root.join("owners", JoinType.INNER);
        Join<Owner, WaterRight> waterRight = owners.join("waterRight", JoinType.INNER);

        List<Predicate> predicates = getPredicateList(cb, root, contactID, lastName, firstName, null);
        predicates.add(owners.get("endDate").isNull());
        predicates.add(waterRight.get("waterRightId").in(waterRightIds));

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        q.where(pred);

        List<Order> orderList = getOrders(cb, root, null, sortColumn, sortDirection);
        q.orderBy(orderList);

        q.distinct(true);

        List<Customer> result = manager.createQuery(q)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

        Root<Customer> countRoot = countQuery.from(Customer.class);
        Join<Customer, Owner> countOwners = countRoot.join("owners", JoinType.INNER);
        Join<Owner, WaterRight> countWaterRight = countOwners.join("waterRight", JoinType.INNER);

        predicates = getPredicateList(cb, countRoot, contactID, lastName, firstName, null);
        predicates.add(countOwners.get("endDate").isNull());
        predicates.add(countWaterRight.get("waterRightId").in(waterRightIds));

        pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        countQuery.select(cb.countDistinct(countRoot)).where(pred);

        Long count = manager.createQuery(countQuery).getSingleResult();

        Page<Customer> resultPage = new PageImpl<>(result, pageable, count);

        return resultPage;
    }

    private Predicate[] getPredicates(CriteriaBuilder cb, Root<Customer> root, String contactID, String lastName, String firstName, String firstLastName) {
        List<Predicate> predicates = getPredicateList(cb, root, contactID, lastName, firstName, firstLastName);

        Predicate[] pred = new Predicate[predicates.size()];
        predicates.toArray(pred);

        return pred;
    }

    private List<Predicate> getPredicateList(CriteriaBuilder cb, Root<Customer> root, String contactID, String lastName, String firstName, String firstLastName) {
        List<Predicate> predicates = new ArrayList<>();
        if(contactID != null) {
            predicates.add(cb.like(root.get("customerId").as(String.class), contactID));
        }
        if(lastName != null) {
            predicates.add(cb.like(root.get("lastName"), lastName));
        }
        if(firstName != null) {
            predicates.add(cb.like(root.get("firstName"), firstName));
        }
        if(firstLastName != null) {
            predicates.add(
                cb.like(
                    this.concat(cb,
                        cb.concat(root.get("firstName"), this.extraSpaceWhenNotNull(cb, root.get("firstName"))),
                        cb.concat(root.get("middleInitial"), this.extraSpaceWhenNotNull(cb, root.get("middleInitial"))),
                        root.get("lastName"),
                        cb.concat(this.extraSpaceWhenNotNull(cb, root.get("suffix")), root.get("suffix"))
                    ),
                    firstLastName
                )
            );
        }
        return predicates;
    }

    private static Expression<String> extraSpaceWhenNotNull(CriteriaBuilder cb, Expression<String> exp) {
        return cb.selectCase().when(
            exp.isNotNull(),
            " "
        ).otherwise("").as(String.class);
    }

    // prevents the pyramid of doom
    private static Expression<String> concat(CriteriaBuilder cb, Expression<String>... expressions) {
        return Arrays.stream(expressions).skip(1).reduce(expressions[0], (partial, exp) -> cb.concat(partial, exp));
    }

    private List<Order> getOrders(CriteriaBuilder cb, Root<Customer> root, Join type,  CustomerSortColumn sortColumn, SortDirection sortDirection) {
        List<Order> orderList = new ArrayList<>();
        if(sortDirection == SortDirection.ASC) {
            if(sortColumn == CustomerSortColumn.CONTACTID) {
                orderList.add(cb.asc(root.get("customerId")));
            } else if (sortColumn == CustomerSortColumn.FIRSTLASTNAME) {
                orderList.add(cb.asc(root.get("firstName")));
                orderList.add(cb.asc(root.get("middleInitial")));
                orderList.add(cb.asc(root.get("lastName")));
                orderList.add(cb.asc(root.get("suffix")));
                orderList.add(cb.asc(root.get("customerId")));
            } else if (sortColumn == CustomerSortColumn.CONTACTTYPEDESCRIPTION && type != null) {
                orderList.add(cb.asc(type.get("description")));
            } else {
                orderList.add(cb.asc(root.get("lastName")));
                orderList.add(cb.asc(root.get("firstName")));
                orderList.add(cb.asc(root.get("middleInitial")));
                orderList.add(cb.asc(root.get("suffix")));
                orderList.add(cb.asc(root.get("customerId")));
            }
        } else {
            if(sortColumn == CustomerSortColumn.CONTACTID) {
                orderList.add(cb.desc(root.get("customerId")));
            } else if (sortColumn == CustomerSortColumn.FIRSTLASTNAME) {
                orderList.add(cb.desc(root.get("firstName")));
                orderList.add(cb.desc(root.get("middleInitial")));
                orderList.add(cb.desc(root.get("lastName")));
                orderList.add(cb.desc(root.get("suffix")));
                orderList.add(cb.desc(root.get("customerId")));
            } else if (sortColumn == CustomerSortColumn.CONTACTTYPEDESCRIPTION) {
                orderList.add(cb.desc(type.get("description")));
            } else {
                orderList.add(cb.desc(root.get("lastName")));
                orderList.add(cb.desc(root.get("firstName")));
                orderList.add(cb.desc(root.get("middleInitial")));
                orderList.add(cb.desc(root.get("suffix")));
                orderList.add(cb.desc(root.get("customerId")));
            }
        }
        return orderList;
    }

    @Override
    public Page<CustomerContactSearchResultDto> searchCustomerContacts(Pageable pageable, CustomerContactsSortColumn sortColumn, SortDirection sortDirection, String contactId, String lastName, String firstName, String middleInitial, String suffix, String contactType, String contactStatus) {

        LOGGER.info("Search Customer Contacts");
        String mainOrderBy = buildCustomerContactSearchSort(sortColumn, sortDirection);

        String subSelect =
                " SELECT  \n" +
                        "     customers.CUST_ID_SEQ as cust_id_seq, \n" +
                        "     customers.SEND_MAIL as send_mail, \n" +
                        "     customers.CTTP_CD as cttp_cd, \n" +
                        "     customers.FST_NM as fst_nm, \n" +
                        "     customers.LST_NM_OR_BUSN_NM as lst_nm_or_busn_nm, \n" +
                        "     customers.MID_INT as mid_int, \n" +
                        "     customers.SUFX as sufx, \n" +
                        "     addresses.CUST_ID_SEQ as addr_cust_id_seq, \n" +
                        "     addresses.ADDR_ID_SEQ as addr_id_seq, \n" +
                        "     addresses.ADDR_LN_1 as addr_ln_1, \n" +
                        "     addresses.ADDR_LN_2 as addr_ln_2, \n" +
                        "     addresses.ADDR_LN_3 as addr_ln_3, \n" +
                        "     addresses.CREATED_BY as created_by, \n" +
                        "     addresses.DTM_CREATED as dtm_created, \n" +
                        "     addresses.FRGN_ADDR as frgn_addr, \n" +
                        "     addresses.FRGN_POSTAL as frgn_postal, \n" +
                        "     addresses.MOD_REASON as mod_reason, \n" +
                        "     addresses.PL_4 as PL_4, \n" +
                        "     addresses.PRI_MAIL as pri_mail, \n" +
                        "     addresses.ZPCD_ID_SEQ as zip_code_id, \n" +
                        "     addresses.UNRESOLVED_FLAG as unresolved_flag, \n" +
                        "     addresses.MOD_BY as mod_by, \n" +
                        "     addresses.DTM_MOD as dtm_mod, \n" +
                        "     refcodes1.rv_meaning as contact_status, \n" +
                        "     refcodes2.rv_meaning as customer_suffix, \n" +
                        "     refcodes4.rv_meaning as foreign_address_val, \n" +
                        "     refcodes3.rv_meaning as primary_mail_val, \n" +
                        "     refcodes5.rv_meaning as unresolved_flag_val, \n" +
                        "     zipcodes.ZPCD_ID_SEQ as zpcd_id_seq, \n" +
                        "     zipcodes.ZIP_CD as zip_cd, \n" +
                        "     cities.CITY_ID_SEQ as city_id_seq, \n" +
                        "     cities.NM as city_nm, \n" +
                        "     states.STT_CD as stt_cd, \n" +
                        "     states.NM as state_nm, \n" +
                        "     customertypes.\"DESCR\" as contact_type_val, \n" +
                        "     CASE WHEN created.lst_nm IS NOT NULL THEN created.fst_nm || ' ' || created.lst_nm END as created_by_val, \n" +
                        "     CASE WHEN modified.lst_nm IS NOT NULL THEN modified.fst_nm  || ' ' || modified.lst_nm END as modified_by_val, \n" +
                        "     (select count(*) from wrd_addresses A where A.cust_id_seq = customers.cust_id_seq) as addressCount" +
                        " FROM \n" +
                        "         WRD_CUSTOMERS customers \n" +
                        "     left outer join \n" +
                        "         WRD_ADDRESSES addresses \n" +
                        "             on ( \n" +
                        "                customers.CUST_ID_SEQ=addresses.CUST_ID_SEQ \n" +
                        "                AND addresses.PRI_MAIL = 'Y' \n" +
                        "             ) \n" +
                        "     left outer join \n" +
                        "         WRD_ZIP_CODES zipcodes \n" +
                        "             on addresses.ZPCD_ID_SEQ=zipcodes.ZPCD_ID_SEQ \n" +
                        "     left outer join \n" +
                        "         WRD_CITIES cities \n" +
                        "             on zipcodes.CITY_ID_SEQ=cities.CITY_ID_SEQ \n" +
                        "     left outer join \n" +
                        "         WRD_STATES states \n" +
                        "             on cities.STT_CD=states.STT_CD \n" +
                        "     left outer join \n" +
                        "         WRD_CUSTOMER_TYPES customertypes \n" +
                        "             on customers.CTTP_CD=customertypes.CTTP_CD \n" +
                        "     left outer join \n" +
                        "         WRD_REF_CODES refcodes1 \n" +
                        "             on ( \n" +
                        "                customers.SEND_MAIL=refcodes1.RV_LOW_VALUE \n" +
                        "                and refcodes1.rv_domain = 'SEND MAIL' \n" +
                        "             ) \n" +
                        "     left outer join \n" +
                        "         WRD_REF_CODES refcodes2 \n" +
                        "            on ( \n" +
                        "               customers.SUFX=refcodes2.RV_LOW_VALUE \n" +
                        "               and refcodes2.rv_domain = 'SUFFIX' \n" +
                        "            ) \n" +
                        "     left outer join \n" +
                        "         WRD_REF_CODES refcodes3 \n" +
                        "            on ( \n" +
                        "               addresses.PRI_MAIL=refcodes3.RV_LOW_VALUE \n" +
                        "               and refcodes3.rv_domain = 'YES_NO' \n" +
                        "            ) \n" +
                        "     left outer join \n" +
                        "         WRD_REF_CODES refcodes4 \n" +
                        "            on ( \n" +
                        "               addresses.FRGN_ADDR=refcodes4.RV_LOW_VALUE \n" +
                        "               and refcodes4.rv_domain = 'YES_NO' \n" +
                        "            ) \n" +
                        "     left outer join \n" +
                        "         WRD_REF_CODES refcodes5 \n" +
                        "            on ( \n" +
                        "               addresses.UNRESOLVED_FLAG=refcodes5.RV_LOW_VALUE \n" +
                        "               and refcodes5.rv_domain = 'YES_NO' \n" +
                        "            ) \n" +
                        "     left outer join \n" +
                        "         WRD_MASTER_STAFF_INDEXES created \n" +
                        "            on created.dnrc_id = ( \n" +
                        "               SELECT m.dnrc_id \n" +
                        "               FROM WRD_MASTER_STAFF_INDEXES m \n" +
                        "               WHERE (m.END_DT is null or m.END_DT > DTM_MOD) \n" +
                        "               AND m.BGN_DT <= DTM_MOD \n" +
                        "               AND m.C_NO = MOD_BY \n" +
                        "         ) \n" +
                        "     left outer join \n" +
                        "        WRD_MASTER_STAFF_INDEXES modified \n" +
                        "           on created.dnrc_id = ( \n" +
                        "              SELECT m.dnrc_id \n" +
                        "              FROM WRD_MASTER_STAFF_INDEXES m \n" +
                        "              WHERE (m.END_DT is null or m.END_DT > DTM_CREATED) \n" +
                        "              AND m.BGN_DT <= DTM_CREATED \n" +
                        "              AND m.C_NO = CREATED_BY \n" +
                        "        )             \n" +
                        " WHERE 1=1 \n";

        String countSelect =
                " SELECT count(*) \n" +
                        "        FROM \n" +
                        "                WRD_CUSTOMERS customers \n" +
                        "            left outer join \n" +
                        "                WRD_ADDRESSES addresses \n" +
                        "                   on ( \n" +
                        "                        customers.CUST_ID_SEQ=addresses.CUST_ID_SEQ \n" +
                        "                        AND addresses.PRI_MAIL = 'Y' \n" +
                        "                   ) \n" +
                        "        WHERE 1=1 \n";

        List<String> whereConditions = new ArrayList<>();
        if (contactId != null)
            whereConditions.add(" AND cast(customers.CUST_ID_SEQ as varchar2(255 char)) like '" + contactId +"' \n");
        if (lastName != null)
            whereConditions.add(" AND customers.LST_NM_OR_BUSN_NM like '" + lastName +"' \n");
        if (firstName != null)
            whereConditions.add(" AND customers.FST_NM like '" + firstName +"' \n");
        if (middleInitial != null)
            whereConditions.add(" AND customers.MID_INT like '" + middleInitial +"' \n");
        if (suffix != null)
            whereConditions.add(" AND customers.SUFX like '" + suffix +"' \n");
        if (contactType != null)
            whereConditions.add(" AND customers.CTTP_CD like '" + contactType +"' \n");
        if (contactStatus != null)
            whereConditions.add(" AND customers.SEND_MAIL like '" + contactStatus +"' \n");
        String whereClause = whereConditions.stream().collect(Collectors.joining(" \n")) + " \n";

        String fullQuery =
                " SELECT * FROM ( \n"+
                        "   SELECT all_.*, \n" +
                        "   rownum rownum_ \n" +
                        "   FROM ( \n" +
                        subSelect +
                        whereClause +
                        mainOrderBy +
                        "   ) all_ \n" +
                        "   WHERE rownum <= :upperLimit \n" +
                        " ) \n" +
                        " WHERE rownum_ > :lowerLimit \n";

        Query q = manager.createNativeQuery(fullQuery);
        //q.setParameter("pageSize", pageSize);
        q.setParameter("upperLimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerLimit", pageable.getOffset());
        List<Object[]> results = q.getResultList();

        List<CustomerContactSearchResultDto> searchResults = new ArrayList<>();
        for (Object[] res : results) {
            CustomerContactSearchResultDto searchResult = new CustomerContactSearchResultDto();
            searchResult = mapObjectToCustomer(res);
            List<AddressDto> addressList = new ArrayList<>();
            addressList.add(mapObjectToAddress(res));
            searchResult.setAddresses(addressList);
            searchResults.add(searchResult);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countSelect + whereClause)
                .getSingleResult())
                .longValue();

        Page<CustomerContactSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;
    }

    private String buildCustomerContactSearchSort(CustomerContactsSortColumn sortColumn, SortDirection sortDirection) {

        String dir = (sortDirection.getValue().equals("ASC"))?"ASC":"DESC";
        String order = " order by ";
        if(sortColumn == CustomerContactsSortColumn.CONTACTID) {
            order += " cust_id_seq " + dir;
        } else if (sortColumn == CustomerContactsSortColumn.NAME) {
            order += " lst_nm_or_busn_nm " + dir;
            order += ", fst_nm " + dir;
            order += ", mid_int " + dir;
            order += ", sufx " + dir;
            order += ", cust_id_seq " + SortDirection.ASC.toString();
        } else {
            order += " addr_ln_1 " + dir;
            order += ", cust_id_seq " + SortDirection.ASC.toString();
        }
        return " " + order + " ";

    }

    private CustomerContactSearchResultDto mapObjectToCustomer(Object[] res) {

        CustomerContactSearchResultDto dto = new CustomerContactSearchResultDto();
        dto.setContactId(((BigDecimal) res[0]).longValue());
        String fullName = Helpers.buildName((String) res[4], (String) res[3], (String) res[5], (String) res[6]);
        dto.setName(fullName);
        dto.setContactStatus((String)res[1]);
        dto.setContactStatusValue((String)res[23]);
        dto.setContactType((String)res[2]);
        dto.setContactTypeValue((String)res[34]);
        dto.setFirstName((String)res[3]);
        dto.setLastName((String)res[4]);
        dto.setMiddleInitial((String)res[5]);
        dto.setSuffix((String)res[6]);
        dto.setSuffixValue((String)res[24]);
        dto.setAddressCount(((BigDecimal)res[37]).intValue());
        return dto;

    }

    private AddressDto mapObjectToAddress(Object[] res) {

        AddressDto dto = new AddressDto();
        // Indian reservation or bad data? Should always have an address
        if (res[8]!=null) {
            dto.setCustomerId(((BigDecimal)res[0]).longValue());
            dto.setAddressId(((BigDecimal)res[8]).longValue());
            dto.setAddressLine1((String) res[9]);
            dto.setAddressLine2((String) res[10]);
            dto.setAddressLine3((String) res[11]);
            dto.setCreatedBy((String) res[12]);
            dto.setCreatedDate(((Timestamp) res[13]).toLocalDateTime().toLocalDate());
            dto.foreignAddress((String) res[14]);
            dto.foreignAddressValue((String) res[25]);
            dto.setForeignPostal((String) res[15]);
            dto.setModReason((String) res[16]);
            dto.setPl4((String) res[17]);
            dto.setPrimaryMail((String) res[18]);
            dto.setPrimaryMailValue((String) res[26]);
            dto.setUnresolvedFlag((String) res[20]);
            dto.setUnresolvedFlagValue((String) res[27]);
            // some addresses won't have valid zip code id seq
            if (res[28]!=null) {
                dto.setZipCodeId(((BigDecimal) res[28]).longValue());
                dto.setZipCode((String) res[29]);
                dto.setCityId(((BigDecimal) res[30]).longValue());
                dto.setCityName((String) res[31]);
                dto.setStateCode((String) res[32]);
                dto.setStateName((String) res[33]);
            }
            dto.setCreatedByValue((String)res[35]);
            dto.setModifiedByValue((String)res[36]);
        }
        return dto;

    }


    @Override
    public Page<BuyerSellerOwnershipUpdatesForContactSearchResultDto> getBuyerSellerOwnershipUpdatesForContact(Pageable pageable, BuyerSellerOwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection, Long contactId, OwnershipUpdateRole ownershipUpdateRole) {

        LOGGER.info("Get Buyer or Seller Ownership Updates for a Contact");

        int pageSize = 25;
        String orderBy = buildBuyerSellerOwnershipUpdatesForContactOrderBy(sortColumn, sortDirection);
        String where = " WHERE osu.OWNR_UPDT_ID in (select ownr_updt_id from WRD_CUSTM_OWNERSHIP_UPDT_XREFS a, WRD_CUSTOMERS b where a.cust_id_seq = b.cust_id_seq and b.cust_id_seq = :contactId and a.role = '" + ownershipUpdateRole + "' ) \n";
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
        q.setParameter("contactId", contactId);
        q.setParameter("upperLimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerLimit", pageable.getOffset());
        List<Object[]> results = q.getResultList();

        List<BuyerSellerOwnershipUpdatesForContactSearchResultDto> searchResults = new ArrayList<>();
        for (Object[] res : results) {
            BuyerSellerOwnershipUpdatesForContactSearchResultDto searchResult = new BuyerSellerOwnershipUpdatesForContactSearchResultDto();
            searchResult = mapObjectToBuyerSellerOwnershipUpdatesForContact(res);
            searchResults.add(searchResult);
        }

        Query qc = manager.createNativeQuery(countSelect);
        qc.setParameter("contactId", contactId);
        long count = ((BigDecimal) qc
                .getSingleResult())
                .longValue();

        Page<BuyerSellerOwnershipUpdatesForContactSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;
    }

    private String buildBuyerSellerOwnershipUpdatesForContactOrderBy(BuyerSellerOwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection) {

        String dir = (sortDirection.getValue().equals("ASC"))?"ASC":"DESC";
        String order = " order by ";
        if(sortColumn == BuyerSellerOwnershipUpdatesForContactSortColumn.OWNERSHIPUPDATETYPEVALUE) {
            order += " refs.RV_MEANING " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == BuyerSellerOwnershipUpdatesForContactSortColumn.RECEIVEDDATE) {
            order += " osu.dt_received " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == BuyerSellerOwnershipUpdatesForContactSortColumn.SALEDATE) {
            order += " osu.dt_received " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else if (sortColumn == BuyerSellerOwnershipUpdatesForContactSortColumn.OWNERSHIPUPDATETYPE) {
            order += " osu.TRN_TYP " + dir;
            order += ", osu.OWNR_UPDT_ID " + SortDirection.ASC.toString();
        } else {
            order += " osu.OWNR_UPDT_ID " + dir;
        }
        return order;

    }

    private BuyerSellerOwnershipUpdatesForContactSearchResultDto mapObjectToBuyerSellerOwnershipUpdatesForContact(Object[] res) {

        BuyerSellerOwnershipUpdatesForContactSearchResultDto dto = new BuyerSellerOwnershipUpdatesForContactSearchResultDto();
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

}
