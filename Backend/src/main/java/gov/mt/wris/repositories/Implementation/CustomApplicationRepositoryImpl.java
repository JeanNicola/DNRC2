package gov.mt.wris.repositories.Implementation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import gov.mt.wris.dtos.ApplicationOwnerSearchResultDto;
import gov.mt.wris.dtos.ApplicationOwnerSortColumn;
import gov.mt.wris.dtos.ApplicationRepSearchResultDto;
import gov.mt.wris.dtos.ApplicationRepSortColumn;
import gov.mt.wris.dtos.ApplicationSearchResultDto;
import gov.mt.wris.dtos.ApplicationSortColumn;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.OwnerApplicationRepListDto;
import gov.mt.wris.dtos.OwnerApplicationRepPageDto;
import gov.mt.wris.dtos.OwnerApplicationSortColumn;
import gov.mt.wris.dtos.RepApplicationOwnerListDto;
import gov.mt.wris.dtos.RepApplicationOwnerPageDto;
import gov.mt.wris.dtos.RepApplicationSortColumn;
import gov.mt.wris.repositories.CustomApplicationRepository;
import gov.mt.wris.utils.Helpers;

@Repository
public class CustomApplicationRepositoryImpl implements CustomApplicationRepository {
    public static Logger LOGGER = LoggerFactory.getLogger(CustomApplicationRepositoryImpl.class);

    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<ApplicationSearchResultDto> getApplications(Pageable pageable, 
                                            ApplicationSortColumn sortDTOColumn,
                                            DescSortDirection sortDirection,
                                            String basin,
                                            String applicationId,
                                            String applicationTypeCode) {
        String sortColumn = getApplicationSortColumn(sortDTOColumn);

        String usedQuery;
        String nonOwnerQuery = "SELECT a.APPL_ID_SEQ,\n" +
                                    "a.BOCA_CD,\n" +
                                    "a.APTP_CD,\n" +
                                    "at.DESCR,\n" +
                                    "a.OFFC_ID_SEQ,\n" +
                                    "d.dateReceived dt_received,\n" +
                                    "owners.CUST_ID_SEQ,\n" +
                                    "owners.LST_NM,\n" +
                                    "owners.FST_NM,\n" +
                                    "owners.MID_INT,\n" +
                                    "owners.SUFX\n";

        String countQuery = "SELECT Count(*)";

        String baseQuery = "\nFROM WRD_APPLICATIONS a\n" +
                            "inner join WRD_APPLICATION_TYPES at\n" +
                            "on a.APTP_CD = at.APTP_CD\n" +
                            "left join (\n" +
                                "SELECT ao.APPL_ID_SEQ,\n" +
                                "MIN(c.CUST_ID_SEQ) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) CUST_ID_SEQ,\n" +
                                "MIN(c.LST_NM_OR_BUSN_NM) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) LST_NM,\n" +
                                "MIN(c.FST_NM) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) FST_NM,\n" +
                                "MIN(c.MID_INT) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) MID_INT,\n" +
                                "MIN(c.SUFX) keep (dense_rank first order by c.LST_NM_OR_BUSN_NM, c.FST_NM, c.MID_INT, c.SUFX, c.CUST_ID_SEQ) SUFX\n" +
                                "FROM WRD_APPLICATIONS ao\n" +
                                "inner join WRD_OWNERS o\n" +
                                "on o.APPL_ID_SEQ = ao.APPL_ID_SEQ\n" +
                                "inner join WRD_CUSTOMERS c\n" +
                                "on o.CUST_ID_SEQ = c.CUST_ID_SEQ\n" +
                                "WHERE o.END_DT is null\n" +
                                "group by ao.APPL_ID_SEQ\n" +
                            ") owners\n" +
                            "on owners.APPL_ID_SEQ = a.APPL_ID_SEQ\n" +
                            "left join (\n" +
                            "SELECT MIN(e.DT_OF_EVNT) dateReceived, e.APPL_ID_SEQ\n" +
                            "FROM WRD_EVENT_DATES e\n" +
                            "inner join WRD_APPLICATIONS ae\n" +
                            "on ae.APPL_ID_SEQ = e.APPL_ID_SEQ\n" +
                            "WHERE (\n" +
                                "(e.EVTP_CD = 'PAMH' and ae.APTP_CD in ('600P', '606P')) or\n" +
                                "(e.EVTP_CD = 'FRMR' and ae.APTP_CD not in ('600P', '606P'))\n" +
                            ")\n" +
                            "group by e.APPL_ID_SEQ\n" +
                            ") d\n" +
                            "on d.APPL_ID_SEQ = a.APPL_ID_SEQ\n";

        // generate a list of where conditions
        List<String> whereConditions = new ArrayList<>();
        if(basin != null) {
            whereConditions.add("a.BOCA_CD like '" + basin + "'");
        }
        if(applicationId != null) {
            whereConditions.add("to_char(a.APPL_ID_SEQ) like '" + applicationId + "'");
        }
        if(applicationTypeCode != null) {
            whereConditions.add("a.APTP_CD like '" + applicationTypeCode + "'");
        }

        // we don't care about the single owner per application in the count
        String whereBeginning = "\nWHERE ";
        countQuery += "\nFROM WRD_APPLICATIONS a";

        // Concatenate Where into from claus 
        String whereClause = (whereConditions.size() > 0) ? whereBeginning : "";
        whereClause += whereConditions.stream().collect(Collectors.joining("\nAND "));

        baseQuery += whereClause;
        countQuery += whereClause;

        nonOwnerQuery += baseQuery;

        nonOwnerQuery += "\norder by " + sortColumn + " " + sortDirection.getValue() + ", a.APPL_ID_SEQ desc\n";

        usedQuery = nonOwnerQuery;

        usedQuery = "SELECT * FROM (\n" +
                    "SELECT all_.*,\n" +
                    "rownum rownum_\n" +
                    "FROM (\n" +
                    usedQuery + 
                    ") all_\n" +
                    "WHERE rownum <= :upperlimit\n" +
                    ")\n" +
                    "WHERE rownum_ > :lowerlimit";
        
        Query q = manager.createNativeQuery(usedQuery);
        q.setParameter("upperlimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerlimit", pageable.getOffset());

        List<Object[]> results = q.getResultList();

        List<ApplicationSearchResultDto> applications = new ArrayList<>();
        for(Object[] app : results) {
            ApplicationSearchResultDto dto = new ApplicationSearchResultDto();
            dto.setApplicationId(((BigDecimal) app[0]).longValue());
            dto.setBasin((String) app[1]);
            dto.setApplicationTypeCode((String) app[2]);
            dto.setApplicationTypeDescription((String) app[3]);
            if(app[6] != null) dto.setContactId(((BigDecimal) app[6]).longValue());
            Timestamp timestamp = (Timestamp) app[5];
            // ZonedDateTime datetime = ZonedDateTime.parse(timestamp.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n").withZone(ZoneId.of("Z")));
            // OffsetDateTime time = datetime.toOffsetDateTime();
            if(timestamp != null) dto.setDateTimeReceived(timestamp.toLocalDateTime());
            String name = Helpers.buildName((String) app[7], (String) app[8], (String) app[9], (String) app[10]);
            dto.setOwnerName(name);
            dto.setOwnerLastName((String) app[7]);
            dto.setOwnerFirstName((String) app[8]);
            dto.setOfficeId((BigDecimal)app[4]);
            applications.add(dto);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countQuery).getSingleResult()).longValue();

        Page<ApplicationSearchResultDto> resultPage = new PageImpl<>(applications, pageable, count);

        return resultPage;
    }

    public Page<OwnerApplicationRepListDto> getOwnersApplications(Pageable pageable,
                                                                OwnerApplicationSortColumn sortColumn,
                                                                DescSortDirection sortDirection,
                                                                Long contactId,
                                                                String basin,
                                                                String applicationId,
                                                                String applicationTypeCode,
                                                                String repContactId,
                                                                String repLastName,
                                                                String repFirstName) {
        LOGGER.info("Searching applications owned by a particular owner");

        String orderBy = getOwnerApplicationSort(sortColumn, sortDirection);

        String select = "SELECT a.APPL_ID_SEQ applicationId,\n" +
                                "a.BOCA_CD basin,\n" +
                                "a.APTP_CD typeCode,\n" +
                                "at.DESCR typeDescription,\n" +
                                "(\n" +
                                    "SELECT MIN(e.DT_OF_EVNT)\n" +
                                    "FROM WRD_EVENT_DATES e\n" +
                                    "WHERE (e.EVTP_CD = 'PAMH' and a.APTP_CD in ('600P', '606P'))\n" +
                                    "or (e.EVTP_CD = 'FRMR' and a.APTP_CD not in ('600P', '606P'))\n" +
                                    "and e.APPL_ID_SEQ = a.APPL_ID_SEQ\n" +
                                ") dateReceived,\n" +
                                "reps.contactID repContactID,\n" +
                                "reps.lastName repLastName,\n" +
                                "reps.firstName repFirstName,\n" +
                                "reps.middleInitial repMiddleInitial,\n" +
                                "reps.sufx repSufx\n";
        String base =   "FROM WRD_APPLICATIONS a\n" +
                        "inner join WRD_OWNERS o\n" +
                        "on o.APPL_ID_SEQ = a.APPL_ID_SEQ\n" +
                        "inner join WRD_CUSTOMERS c\n" +
                        "on o.CUST_ID_SEQ = c.CUST_ID_SEQ\n" +
                        "inner join WRD_APPLICATION_TYPES at\n" +
                        "on at.APTP_CD = a.APTP_CD\n";
        
        String countQuery = "SELECT COUNT(*)\n";

        String repWhereJoin = "left join (\n" + 
                                "    SELECT r.OWNR_ID_SEQ,\n" + 
                                "            r.CUST_ID_SEQ_SEC,\n" + 
                                "            rc.CUST_ID_SEQ contactID,\n" + 
                                "            rc.LST_NM_OR_BUSN_NM lastName,\n" + 
                                "            rc.FST_NM firstName,\n" + 
                                "            rc.MID_INT middleInitial,\n" + 
                                "            rc.SUFX sufx\n" + 
                                "    FROM WRD_REPRESENTATIVES r\n" + 
                                "    left join WRD_CUSTOMERS rc\n" + 
                                "    on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n" + 
                                "    WHERE r.END_DT is null\n" + 
                                ") reps\n" + 
                                "on reps.OWNR_ID_SEQ = o.OWNR_ID_SEQ\n" + 
                                "and reps.CUST_ID_SEQ_SEC = o.CUST_ID_SEQ\n";
        String repNonWhereJoin = "left join (\n" + 
                                    "SELECT r.OWNR_ID_SEQ,\n" + 
                                    "        r.CUST_ID_SEQ_SEC,\n" + 
                                    "        MIN(rc.CUST_ID_SEQ) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) contactID,\n" + 
                                    "        MIN(rc.LST_NM_OR_BUSN_NM) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) lastName,\n" + 
                                    "        MIN(rc.FST_NM) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) firstName,\n" + 
                                    "        MIN(rc.MID_INT) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) middleInitial,\n" + 
                                    "        MIN(rc.SUFX) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) sufx\n" + 
                                    "FROM WRD_REPRESENTATIVES r\n" + 
                                    "left join WRD_CUSTOMERS rc\n" + 
                                    "on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n" + 
                                    "WHERE r.END_DT is null\n" + 
                                    "group by r.OWNR_ID_SEQ, r.CUST_ID_SEQ_SEC\n" + 
                                ") reps\n" + 
                                "on reps.OWNR_ID_SEQ = o.OWNR_ID_SEQ\n" + 
                                "and reps.CUST_ID_SEQ_SEC = o.CUST_ID_SEQ\n";
        
        List<String> whereConditions = new ArrayList<>();
        List<String> repWhereConditions = new ArrayList<>();
        List<String> countRepWhereConditions = new ArrayList<>();
        List<String> countWhereConditions = new ArrayList<>();
        whereConditions.add("c.CUST_ID_SEQ = " + contactId);
        countWhereConditions.add("c.CUST_ID_SEQ = " + contactId);
        if(basin != null) {
            whereConditions.add("a.BOCA_CD like '" + basin + "'");
            countWhereConditions.add("a.BOCA_CD like '" + basin + "'");
        }
        if(applicationId != null) {
            whereConditions.add("to_char(a.APPL_ID_SEQ) like '" + applicationId + "'");
            countWhereConditions.add("to_char(a.APPL_ID_SEQ) like '" + applicationId + "'");
        }
        if(applicationTypeCode != null) {
            whereConditions.add("a.APTP_CD like '" + applicationTypeCode + "'");
            countWhereConditions.add("a.APTP_CD like '" + applicationTypeCode + "'");
        }
        if(repContactId != null) {
            repWhereConditions.add("to_char(reps.contactID) like '" + repContactId + "'");
            countRepWhereConditions.add("to_char(r.CUST_ID_SEQ) like '" + repContactId + "'");
        }
        if(repLastName != null) {
            repWhereConditions.add("reps.lastName like '" + repLastName + "'");
            countRepWhereConditions.add("rc.LST_NM_OR_BUSN_NM like '" + repLastName + "'");
        }
        if(repFirstName != null) {
            repWhereConditions.add("reps.firstName like '" + repFirstName + "'");
            countRepWhereConditions.add("rc.LST_NM_OR_BUSN_NM like '" + repFirstName + "'");
        }

        // add base
        select += base;
        countQuery += base;

        // add the conditions to the separate count
        String countWhereClause;
        if(countRepWhereConditions.size() > 0) {
            countWhereConditions.add("r.END_DT is null");
            countWhereConditions.addAll(countRepWhereConditions);
            countQuery += "inner join WRD_REPRESENTATIVES r\n" +
                            "on o.OWNR_ID_SEQ = r.OWNR_ID_SEQ\n" +
                            "and o.CUST_ID_SEQ = r.CUST_ID_SEQ_SEC\n" +
                            "inner join WRD_CUSTOMERS rc\n" +
                            "on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n";
        }
        countWhereClause = (countWhereConditions.size() > 0) ? "WHERE " : "";
        countWhereClause += countWhereConditions.stream().collect(Collectors.joining("\nAND "));
        countQuery += countWhereClause;

        // add the right type of representative join
        // to either include all or just one per application
        if(repWhereConditions.size() > 0) {
            select += repWhereJoin;
            whereConditions.addAll(repWhereConditions);
        } else {
            select += repNonWhereJoin;
        }

        // include all where conditions
        String whereClause = "WHERE " + whereConditions.stream().collect(Collectors.joining("\nAND "));

        select += whereClause;

        // include pagination
        String fullQuery = "SELECT *\n" +
                            "FROM (\n" +
                            "SELECT DENSE_RANK() over (order by " + orderBy + ") rank,\n" +
                                    "norank.*\n" +
                            "FROM (\n" +
                            "   " + select + "\n" +
                            ") norank\n" +
                            ") whole\n" +
                            "WHERE whole.rank <= :upperlimit\n" +
                            "AND whole.rank > :lowerlimit\n";
        
        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("upperlimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerlimit", pageable.getOffset());

        List<Object[]> results = q.getResultList();

        List<OwnerApplicationRepListDto> apps = new ArrayList<>();
        for(Object[] res : results) {
            OwnerApplicationRepListDto app = new OwnerApplicationRepListDto();
            long id = ((BigDecimal) res[1]).longValue();
            app.setApplicationId(id);
            app.setApplicationTypeCode((String) res[3]);
            app.setApplicationTypeDescription((String) res[4]);
            app.setBasin((String) res[2]);
            if(res[5] != null) app.setDateTimeReceived(((Timestamp) res[5]).toLocalDateTime());
            if(res[6] != null) app.setContactId(((BigDecimal) res[6]).longValue());
            String name = Helpers.buildName((String) res[7], (String) res[8], (String) res[9], (String) res[10]);
            app.setRepName(name);
            apps.add(app);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countQuery)
                                        .getSingleResult())
                                        .longValue();

        return new PageImpl<>(apps, pageable, count);
    }

    private String getOwnerApplicationSort(OwnerApplicationSortColumn sort, DescSortDirection sortDirection) {
        String sortColumn;
        String direction;
        if(sortDirection == DescSortDirection.ASC) {
            direction = " ASC";
        } else {
            direction = " DESC";
        }
        if(sort == OwnerApplicationSortColumn.APPLICATIONID) {
            sortColumn = "applicationId";
        } else if(sort == OwnerApplicationSortColumn.BASIN) {
            sortColumn = "basin";
        } else if(sort == OwnerApplicationSortColumn.REPCONTACTID) {
            sortColumn = "repContactID";
        } else if(sort == OwnerApplicationSortColumn.DATETIMERECEIVED) {
            sortColumn = "dateReceived";
        } else if(sort == OwnerApplicationSortColumn.APPLICATIONTYPECODE) {
            sortColumn = "typeCode";
        } else if(sort == OwnerApplicationSortColumn.APPLICATIONTYPEDESCRIPTION) {
            sortColumn = "typeDescription";
        } else {
            sortColumn = "repLastName" + direction +
                            ",repFirstName" + direction +
                            ",repMiddleInitial" + direction +
                            ",repSufx";
        }

        sortColumn += direction;

        sortColumn += ", applicationId DESC, repContactID DESC";

        return sortColumn;
    }

    @Override
    public Page<ApplicationOwnerSearchResultDto> getApplicationsByOwners(Pageable pageable,
                                                ApplicationOwnerSortColumn sortColumn,
                                                DescSortDirection sortDirection,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode,
                                                String ownerContactId,
                                                String ownerLastName,
                                                String ownerFirstName,
                                                String repContactId,
                                                String repLastName,
                                                String repFirstName) {
        LOGGER.info("Searching for Applications by Owners");

        int pageSize = 25;

        String mainOrderBy = getApplicationOwnerSort(sortColumn, sortDirection);
        String applicationOrderBy = "a.APPL_ID_SEQ asc";

        String select = "SELECT DENSE_RANK() over (partition by c.CUST_ID_SEQ order by " + applicationOrderBy + ") application_rank,\n" + 
                                    "a.APPL_ID_SEQ applicationId,\n" + 
                                    "a.BOCA_CD basin,\n" + 
                                    "a.APTP_CD typeCode,\n" + 
                                    "at.DESCR typeDescription,\n" + 
                                    "o.OWNR_ID_SEQ,\n" + 
                                    "c.CUST_ID_SEQ contactID,\n" + 
                                    "c.LST_NM_OR_BUSN_NM lastName,\n" + 
                                    "c.FST_NM firstName,\n" + 
                                    "c.MID_INT middleInitial,\n" + 
                                    "c.SUFX suffix,\n" + 
                                    "reps.contactID repContactID,\n" + 
                                    "reps.lastName repLastName,\n" + 
                                    "reps.firstName repFirstName,\n" + 
                                    "reps.middleInitial repMiddleInitial,\n" + 
                                    "reps.sufx repSufx\n";
        String dateReceivedJoin = "SELECT MIN(e.DT_OF_EVNT) dateReceived, e.APPL_ID_SEQ\n" +
                                    "FROM WRD_EVENT_DATES e\n" +
                                    "inner join WRD_APPLICATIONS ea\n" +
                                    "on e.APPL_ID_SEQ = ea.APPL_ID_SEQ\n" +
                                    "WHERE (e.EVTP_CD = 'PAMH' and ea.APTP_CD in ('600P', '606P'))\n" +
                                    "OR (e.EVTP_CD = 'FRMR' and ea.APTP_CD not in ('600P', '606P'))\n" +
                                    "group by e.APPL_ID_SEQ\n";
        String subCount = "SELECT COUNT(*) subCount, ac.CUST_ID_SEQ\n" +
                            "FROM WRD_APPLICATIONS a2\n" +
                            "inner join WRD_OWNERS ao\n" +
                            "on ao.APPL_ID_SEQ = a2.APPL_ID_SEQ\n" +
                            "inner join WRD_CUSTOMERS ac\n" +
                            "on ac.CUST_ID_SEQ = ao.CUST_ID_SEQ\n";

        String repSubCount = "SELECT COUNT(*) subCount, ac.CUST_ID_SEQ\n" +
                            "FROM WRD_APPLICATIONS a2\n" +
                            "inner join WRD_OWNERS ao\n" +
                            "on ao.APPL_ID_SEQ = a2.APPL_ID_SEQ\n" +
                            "inner join WRD_CUSTOMERS ac\n" +
                            "on ac.CUST_ID_SEQ = ao.CUST_ID_SEQ\n" +
                            "inner join WRD_REPRESENTATIVES ar\n" +
                            "on ar.OWNR_ID_SEQ = ao.OWNR_ID_SEQ\n" +
                            "and ar.CUST_ID_SEQ_SEC = ao.CUST_ID_SEQ\n" +
                            "inner join WRD_CUSTOMERS arc\n" +
                            "on ar.CUST_ID_SEQ = arc.CUST_ID_SEQ\n";
        
        String countQuery = "SELECT COUNT(DISTINCT c.CUST_ID_SEQ)\n";

        String base = "FROM WRD_APPLICATIONS a\n" + 
                        "inner join WRD_OWNERS o\n" + 
                        "on o.APPL_ID_SEQ = a.APPL_ID_SEQ\n" + 
                        "inner join WRD_CUSTOMERS c\n" + 
                        "on o.CUST_ID_SEQ = c.CUST_ID_SEQ\n" + 
                        "inner join WRD_APPLICATION_TYPES at\n" + 
                        "on at.APTP_CD = a.APTP_CD\n";

        String repWhereJoin = "left join (\n" + 
                                "    SELECT r.OWNR_ID_SEQ,\n" + 
                                "            r.CUST_ID_SEQ_SEC,\n" + 
                                "            rc.CUST_ID_SEQ contactID,\n" + 
                                "            rc.LST_NM_OR_BUSN_NM lastName,\n" + 
                                "            rc.FST_NM firstName,\n" + 
                                "            rc.MID_INT middleInitial,\n" + 
                                "            rc.SUFX sufx\n" + 
                                "    FROM WRD_REPRESENTATIVES r\n" + 
                                "    left join WRD_CUSTOMERS rc\n" + 
                                "    on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n" + 
                                "    WHERE r.END_DT is null\n" + 
                                ") reps\n" + 
                                "on reps.OWNR_ID_SEQ = o.OWNR_ID_SEQ\n" + 
                                "and reps.CUST_ID_SEQ_SEC = o.CUST_ID_SEQ\n";
        String repNonWhereJoin = "left join (\n" + 
                                    "SELECT r.OWNR_ID_SEQ,\n" + 
                                    "        r.CUST_ID_SEQ_SEC,\n" + 
                                    "        MIN(rc.CUST_ID_SEQ) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) contactID,\n" + 
                                    "        MIN(rc.LST_NM_OR_BUSN_NM) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) lastName,\n" + 
                                    "        MIN(rc.FST_NM) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) firstName,\n" + 
                                    "        MIN(rc.MID_INT) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) middleInitial,\n" + 
                                    "        MIN(rc.SUFX) keep (dense_rank first order by rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX, r.CUST_ID_SEQ) sufx\n" + 
                                    "FROM WRD_REPRESENTATIVES r\n" + 
                                    "left join WRD_CUSTOMERS rc\n" + 
                                    "on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n" + 
                                    "WHERE r.END_DT is null\n" + 
                                    "group by r.OWNR_ID_SEQ, r.CUST_ID_SEQ_SEC\n" + 
                                ") reps\n" + 
                                "on reps.OWNR_ID_SEQ = o.OWNR_ID_SEQ\n" + 
                                "and reps.CUST_ID_SEQ_SEC = o.CUST_ID_SEQ\n";
        
        List<String> whereConditions = new ArrayList<>();
        List<String> repWhereConditions = new ArrayList<>();
        List<String> countRepWhereConditions = new ArrayList<>();
        List<String> countWhereConditions = new ArrayList<>();
        List<String> subCountWhereConditions = new ArrayList<>();
        List<String> subCountRepWhereConditions = new ArrayList<>();
        if(basin != null) {
            whereConditions.add("a.BOCA_CD like '" + basin + "'");
            countWhereConditions.add("a.BOCA_CD like '" + basin + "'");
            subCountWhereConditions.add("a2.BOCA_CD like '" + basin + "'");
        }
        if(applicationId != null) {
            whereConditions.add("to_char(a.APPL_ID_SEQ) like '" + applicationId + "'");
            countWhereConditions.add("to_char(a.APPL_ID_SEQ) like '" + applicationId + "'");
            subCountWhereConditions.add("to_char(a2.APPL_ID_SEQ) like '" + applicationId + "'");
        }
        if(applicationTypeCode != null) {
            whereConditions.add("a.APTP_CD like '" + applicationTypeCode + "'");
            countWhereConditions.add("a.APTP_CD like '" + applicationTypeCode + "'");
            subCountWhereConditions.add("a2.APTP_CD like '" + applicationTypeCode + "'");
        }
        if(ownerContactId != null) {
            whereConditions.add("to_char(c.CUST_ID_SEQ) like '" + ownerContactId + "'");
            countWhereConditions.add("to_char(c.CUST_ID_SEQ) like '" + ownerContactId + "'");
            subCountWhereConditions.add("to_char(ac.CUST_ID_SEQ) like '" + ownerContactId + "'");
        }
        if(ownerLastName != null) {
            whereConditions.add("c.LST_NM_OR_BUSN_NM like '" + ownerLastName + "'");
            countWhereConditions.add("c.LST_NM_OR_BUSN_NM like '" + ownerLastName + "'");
            subCountWhereConditions.add("ac.LST_NM_OR_BUSN_NM like '" + ownerLastName + "'");
        }
        if(ownerFirstName != null) {
            whereConditions.add("c.FST_NM like '" + ownerFirstName + "'");
            countWhereConditions.add("c.FST_NM like '" + ownerFirstName + "'");
            subCountWhereConditions.add("ac.FST_NM like '" + ownerFirstName + "'");
        }
        if(repContactId != null) {
            repWhereConditions.add("to_char(reps.contactID) like '" + repContactId + "'");
            subCountRepWhereConditions.add("to_char(arc.CUST_ID_SEQ) like '" + repContactId + "'");
            countRepWhereConditions.add("to_char(r.CUST_ID_SEQ) like '" + repContactId + "'");
        }
        if(repLastName != null) {
            repWhereConditions.add("reps.lastName like '" + repLastName + "'");
            subCountRepWhereConditions.add("arc.LST_NM_OR_BUSN_NM like '" + repLastName + "'");
            countRepWhereConditions.add("rc.LST_NM_OR_BUSN_NM like '" + repLastName + "'");
        }
        if(repFirstName != null) {
            repWhereConditions.add("reps.firstName like '" + repFirstName + "'");
            subCountRepWhereConditions.add("arc.LST_NM_OR_BUSN_NM like '" + repFirstName + "'");
            countRepWhereConditions.add("rc.LST_NM_OR_BUSN_NM like '" + repFirstName + "'");
        }

        // count the number of applications for each applicant
        // include the right type or representative join
        String subCountWhereClause;
        if(subCountRepWhereConditions.size() > 0) {
            subCountWhereConditions.addAll(subCountRepWhereConditions);
            subCount = repSubCount;
        }
        subCountWhereClause = (subCountWhereConditions.size() > 0) ? "AND " + subCountWhereConditions.stream().collect(Collectors.joining("\nAND ")): "";
        subCount = subCount + subCountWhereClause + "\ngroup by ac.CUST_ID_SEQ\n";

        // add from and join statements
        select += base;
        countQuery += base;

        // add the conditions to the separate count
        String countWhereClause;
        if(countRepWhereConditions.size() > 0) {
            countWhereConditions.add("r.END_DT is null");
            countWhereConditions.addAll(countRepWhereConditions);
            countQuery += "inner join WRD_REPRESENTATIVES r\n" +
                            "on o.OWNR_ID_SEQ = r.OWNR_ID_SEQ\n" +
                            "and o.CUST_ID_SEQ = r.CUST_ID_SEQ_SEC\n" +
                            "inner join WRD_CUSTOMERS rc\n" +
                            "on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n";
        }
        countWhereClause = (countWhereConditions.size() > 0) ? "WHERE " : "";
        countWhereClause += countWhereConditions.stream().collect(Collectors.joining("\nAND "));
        countQuery += countWhereClause;

        // include the right type of representative join
        // to either include all or just one per application
        if(repWhereConditions.size() > 0) {
            select += repWhereJoin;
            whereConditions.addAll(repWhereConditions);
        } else {
            select += repNonWhereJoin;
        }

        // include all where conditions
        String whereClause = (whereConditions.size() > 0) ? "WHERE " : "";
        whereClause += whereConditions.stream().collect(Collectors.joining("\nAND "));
        select += whereClause;

        // include pagination
        String fullQuery = "SELECT whole2.*,\n" + 
                                    "applicationCount.subCount,\n" +
                                    "dateReceived.dateReceived\n" +
                            "FROM (\n" +
                                "SELECT *\n" +
                                "FROM (\n" +
                                    "SELECT DENSE_RANK() over (order by " + mainOrderBy + ") rank,\n" +
                                            "norank.*\n" +
                                    "FROM (\n" +
                                    "    " + select + "\n" +
                                    ") norank\n" +
                                ") whole\n" +
                                "WHERE whole.application_rank <= :appPageSize\n" +
                                "AND whole.rank <= :upperlimit\n" +
                                "AND whole.rank > :lowerlimit\n" +
                            ") whole2\n" +
                            "inner join (\n" +
                                subCount +
                            ") applicationCount\n" + 
                            "on applicationCount.CUST_ID_SEQ = whole2.contactID\n" +
                            "left join (\n" +
                                dateReceivedJoin +
                            ") dateReceived\n" +
                            "on dateReceived.APPL_ID_SEQ = whole2.applicationId\n" +
                            "order by rank, application_rank\n";
        
        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("appPageSize", pageSize);
        q.setParameter("upperlimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerlimit", pageable.getOffset());

        List<Object[]> results = q.getResultList();

        List<ApplicationOwnerSearchResultDto> searchResults = new ArrayList<>();
        for(Object[] res : results) {
            ApplicationOwnerSearchResultDto searchResult = new ApplicationOwnerSearchResultDto();
            long id = ((BigDecimal) res[7]).longValue();
            if(searchResults.size() == 0 || searchResults.get(searchResults.size() - 1).getContactId() != id) {
                searchResult.setContactId(((BigDecimal) res[7]).longValue());
                String fullName = Helpers.buildName((String) res[8], (String) res[9], (String) res[10], (String) res[11]);
                searchResult.setOwnerName(fullName);
                searchResults.add(searchResult);
            }
            searchResult = searchResults.get(searchResults.size() - 1);

            OwnerApplicationRepListDto app = new OwnerApplicationRepListDto();
            app.setApplicationId(((BigDecimal) res[2]).longValue());
            app.setBasin((String) res[3]);
            app.setApplicationTypeCode((String) res[4]);
            app.setApplicationTypeDescription((String) res[5]);
            if(res[18] != null) app.setDateTimeReceived(((Timestamp) res[18]).toLocalDateTime());
            if(res[12] != null) app.setContactId(((BigDecimal) res[12]).longValue());
            String fullName = Helpers.buildName((String) res[13], (String) res[14], (String) res[15], (String) res[16]);
            app.setRepName(fullName);

            if(searchResult.getApplications() == null) {
                OwnerApplicationRepPageDto appPage = new OwnerApplicationRepPageDto();
                appPage.setCurrentPage(1);
                appPage.setPageSize(pageSize);
                long numElements = ((BigDecimal) res[17]).longValue();
                appPage.setTotalElements(numElements);
                appPage.setTotalPages((int) Math.floorDiv(numElements, pageSize) + 1);
                
                searchResult.setApplications(appPage);
            }
            searchResult.getApplications().addResultsItem(app);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countQuery)
                                        .getSingleResult())
                                        .longValue();
        Page<ApplicationOwnerSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;
    }

    private String getApplicationOwnerSort(ApplicationOwnerSortColumn sort, DescSortDirection sortDirection) {
        String sortColumn;
        String direction;
        if(sortDirection == DescSortDirection.ASC) {
            direction = " ASC";
        } else {
            direction = " DESC";
        }
        if(sort == ApplicationOwnerSortColumn.CONTACTID){
            sortColumn = "contactID";
        } else {
            sortColumn = "lastName" + direction +
                            ",firstName" + direction +
                            ",middleInitial" + direction +
                            ",suffix";
        }

        sortColumn += direction;

        sortColumn += ", contactID DESC";

        return sortColumn;
    }

    private String getApplicationSortColumn(ApplicationSortColumn sortDTOColumn) {
        if (sortDTOColumn == ApplicationSortColumn.APPLICATIONID) return "a.APPL_ID_SEQ";
        if (sortDTOColumn == ApplicationSortColumn.APPLICATIONTYPECODE) return "a.APTP_CD";
        if (sortDTOColumn == ApplicationSortColumn.APPLICATIONTYPEDESCRIPTION) return "at.APTP_CD";
        if (sortDTOColumn == ApplicationSortColumn.DATETIMERECEIVED) return "to_char(d.dateReceived, 'YYYYMMDDHHmm')";
        return "a.BOCA_CD";
    }

    @Override
    public Page<ApplicationRepSearchResultDto> getApplicationsByRepresentatives(
        Pageable pageable,
        ApplicationRepSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String applicationId,
        String applicationTypeCode,
        String repContactId,
        String repLastName,
        String repFirstName
    ) {
        LOGGER.info("Searching for applications by representatives");

        int pageSize = 25;

        String mainOrderBy = getApplicationRepSort(sortColumn, sortDirection);

        String select = "SELECT a.APPL_ID_SEQ applicationId,\n" +
                            "a.BOCA_CD basin,\n" +
                            "a.APTP_CD typeCode,\n" +
                            "at.DESCR typeDescription,\n" +
                            "rc.CUST_ID_SEQ repContactId,\n" +
                            "rc.LST_NM_OR_BUSN_NM repLastName,\n" +
                            "rc.FST_NM repFirstName,\n" +
                            "rc.MID_INT repMiddleInitial,\n" +
                            "rc.SUFX repSuffix,\n" +
                            "MIN(c.CUST_ID_SEQ) keep (dense_rank first order by c.CUST_ID_SEQ) contactId,\n" +
                            "MIN(c.LST_NM_OR_BUSN_NM) keep (dense_rank first order by c.CUST_ID_SEQ) lastName,\n" +
                            "MIN(c.FST_NM) keep (dense_rank first order by c.CUST_ID_SEQ) firstName,\n" +
                            "MIN(c.MID_INT) keep (dense_rank first order by c.CUST_ID_SEQ) middleInitial,\n" +
                            "MIN(c.SUFX) keep (dense_rank first order by c.CUST_ID_SEQ) sufx\n";
        String dateReceivedJoin = "SELECT MIN(e.DT_OF_EVNT) dateReceived, e.APPL_ID_SEQ\n" +
                                    "FROM WRD_EVENT_DATES e\n" +
                                    "inner join WRD_APPLICATIONS ea\n" +
                                    "on e.APPL_ID_SEQ = ea.APPL_ID_SEQ\n" +
                                    "WHERE (e.EVTP_CD = 'PAMH' and ea.APTP_CD in ('600P', '606P'))\n" +
                                    "OR (e.EVTP_CD = 'FRMR' and ea.APTP_CD not in ('600P', '606P'))\n" +
                                    "group by e.APPL_ID_SEQ\n";
        String subCount = "SELECT COUNT(DISTINCT A.APPL_ID_SEQ) subCount, r.CUST_ID_SEQ\n" +
                            "FROM WRD_APPLICATIONS a\n" +
                            "inner join WRD_OWNERS o\n" +
                            "on o.APPL_ID_SEQ = a.APPL_ID_SEQ\n" +
                            "inner join WRD_CUSTOMERS c\n" +
                            "on o.CUST_ID_SEQ = c.CUST_ID_SEQ\n" +
                            "inner join WRD_REPRESENTATIVES r\n" +
                            "on r.CUST_ID_SEQ_SEC = o.CUST_ID_SEQ\n" +
                            "and r.OWNR_ID_SEQ = o.OWNR_ID_SEQ\n" +
                            "inner join WRD_CUSTOMERS rc\n" +
                            "on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n";
        
        String countQuery = "SELECT COUNT(*)\n";
        
        String base = "FROM WRD_APPLICATIONS a\n" +
                        "inner join WRD_APPLICATION_TYPES at\n" +
                        "on at.APTP_CD = a.APTP_CD\n" +
                        "inner join WRD_OWNERS o\n" +
                        "on a.APPL_ID_SEQ = o.APPL_ID_SEQ\n" +
                        "inner join WRD_CUSTOMERS c\n" +
                        "on o.CUST_ID_SEQ = c.CUST_ID_SEQ\n" +
                        "inner join WRD_REPRESENTATIVES r\n" +
                        "on r.OWNR_ID_SEQ = o.OWNR_ID_SEQ\n" +
                        "and r.CUST_ID_SEQ_SEC = o.CUST_ID_SEQ\n" +
                        "inner join WRD_CUSTOMERS rc\n" +
                        "on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n";

        List<String> whereConditions = new ArrayList<>();
        if(basin != null) {
            whereConditions.add("a.BOCA_CD like '" + basin + "'");
        }
        if(applicationId != null) {
            whereConditions.add("to_char(a.APPL_ID_SEQ) like '" + applicationId + "'");
        }
        if(applicationTypeCode != null) {
            whereConditions.add("a.APTP_CD like '" + applicationTypeCode + "'");
        }
        if(repContactId != null) {
            whereConditions.add("to_char(r.CUST_ID_SEQ) like '" + repContactId + "'");
        }
        if(repLastName != null) {
            whereConditions.add("rc.LST_NM_OR_BUSN_NM like '" + repLastName + "'");
        }
        if(repFirstName != null) {
            whereConditions.add("rc.FST_NM like '" + repFirstName + "'");
        }
        whereConditions.add("r.END_DT is null");
        whereConditions.add("o.END_DT is null");

        String whereClause = "WHERE " + whereConditions.stream().collect(Collectors.joining("\nAND ")) + "\n";

        subCount += whereClause;
        subCount += "group by r.CUST_ID_SEQ\n";

        select += base;
        select += whereClause;
        select += "group by a.APPL_ID_SEQ, a.BOCA_CD, a.APTP_CD, at.DESCR, rc.CUST_ID_SEQ, rc.LST_NM_OR_BUSN_NM, rc.FST_NM, rc.MID_INT, rc.SUFX\n";

        countQuery += base;
        countQuery += whereClause;

        String fullQuery = "SELECT whole3.*,\n" +
                                "applicationCount.subCount,\n" +
                                "dateReceived.dateReceived\n" +
                            "FROM (\n" +
                                "SELECT *\n" +
                                "FROM (\n" +
                                    "SELECT DENSE_RANK() over (order by " + mainOrderBy + ") rank,\n" +
                                    "whole.*\n" +
                                    "FROM (\n" +
                                        "SELECT DENSE_RANK() over (partition by repContactId order by applicationId DESC) application_rank,\n" +
                                                "no_rank.*\n" +
                                        "FROM (\n" +
                                            select +
                                        ") no_rank\n" +
                                    ") whole\n" +
                                ") whole2\n" +
                                "WHERE whole2.application_rank <= :appPageSize\n" +
                                "AND whole2.rank <= :upperlimit\n" +
                                "AND whole2.rank > :lowerlimit\n" +
                            ") whole3\n" +
                            "inner join (\n" +
                                subCount +
                            ") applicationCount\n" +
                            "on applicationCount.CUST_ID_SEQ = whole3.repContactId\n" +
                            "left join (\n" +
                                dateReceivedJoin +
                            ") dateReceived\n" +
                            "on dateReceived.APPL_ID_SEQ = whole3.applicationId\n" +
                            "order by rank, application_rank\n";
        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("appPageSize", pageSize);
        q.setParameter("upperlimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerlimit", pageable.getOffset());

        List<Object[]> results = q.getResultList();

        List<ApplicationRepSearchResultDto> searchResults = new ArrayList<>();
        for(Object[] res : results) {
            ApplicationRepSearchResultDto searchResult = new ApplicationRepSearchResultDto();
            long id = ((BigDecimal) res[6]).longValue();
            if(searchResults.size() == 0 || searchResults.get(searchResults.size() - 1).getRepContactId() != id) {
                searchResult.setRepContactId(((BigDecimal) res[6]).longValue());
                String fullName = Helpers.buildName((String) res[7], (String) res[8], (String) res[9], (String) res[10]);
                searchResult.setRepName(fullName);
                searchResults.add(searchResult);
            }
            searchResult = searchResults.get(searchResults.size() - 1);

            RepApplicationOwnerListDto app = new RepApplicationOwnerListDto();
            app.setApplicationId(((BigDecimal) res[2]).longValue());
            app.setBasin((String) res[3]);
            app.setApplicationTypeCode((String) res[4]);
            app.setApplicationTypeDescription((String) res[5]);
            if(res[17] != null) app.setDateTimeReceived(((Timestamp) res[17]).toLocalDateTime());
            if(res[11] != null) app.setContactId(((BigDecimal) res[11]).longValue());
            String ownName = Helpers.buildName((String) res[12], (String) res[13], (String) res[14], (String) res[15]);
            app.setName(ownName);

            if(searchResult.getApplications() == null) {
                RepApplicationOwnerPageDto appPage = new RepApplicationOwnerPageDto();
                appPage.setCurrentPage(1);
                appPage.setPageSize(pageSize);
                long numElements = ((BigDecimal) res[16]).longValue();
                appPage.setTotalElements(numElements);
                appPage.setTotalPages((int) Math.floorDiv(numElements, pageSize) + 1);

                searchResult.setApplications(appPage);
            }
            searchResult.getApplications().addResultsItem(app);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countQuery)
                                        .getSingleResult())
                                        .longValue();
        Page<ApplicationRepSearchResultDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;
    }

    private String getApplicationRepSort(ApplicationRepSortColumn sort, DescSortDirection sortDirection) {
        String sortColumn;
        String direction;
        if(sortDirection == DescSortDirection.ASC) {
            direction = " ASC";
        } else {
            direction = " DESC";
        }
        if(sort == ApplicationRepSortColumn.REPCONTACTID){
            sortColumn = "repContactId";
        } else {
            sortColumn = "repLastName" + direction +
                            ",repFirstName" + direction +
                            ",repMiddleInitial" + direction +
                            ",repSuffix";
        }

        sortColumn += direction;

        sortColumn += ", contactID DESC";

        return sortColumn;
    }


    @Override
    public Page<RepApplicationOwnerListDto> getRepsApplications(
        Pageable pageable,
        RepApplicationSortColumn sortColumn,
        DescSortDirection sortDirection,
        Long repContactId,
        String basin,
        String applicationId,
        String applicationTypeCode
    ) {
        LOGGER.info("Searching for Applications owned by a specific representative");

        String mainOrderBy = getRepApplicationSort(sortColumn, sortDirection);

        String select = "SELECT a.APPL_ID_SEQ applicationId,\n" +
                            "a.BOCA_CD basin,\n" +
                            "a.APTP_CD typeCode,\n" +
                            "at.DESCR typeDescription,\n" +
                            "MIN(c.CUST_ID_SEQ) keep (dense_rank first order by c.CUST_ID_SEQ) contactId,\n" +
                            "MIN(c.LST_NM_OR_BUSN_NM) keep (dense_rank first order by c.CUST_ID_SEQ) lastName,\n" +
                            "MIN(c.FST_NM) keep (dense_rank first order by c.CUST_ID_SEQ) firstName,\n" +
                            "MIN(c.MID_INT) keep (dense_rank first order by c.CUST_ID_SEQ) middleInitial,\n" +
                            "MIN(c.SUFX) keep (dense_rank first order by c.CUST_ID_SEQ) sufx\n";
        String dateReceivedJoin = "SELECT MIN(e.DT_OF_EVNT) dateReceived, e.APPL_ID_SEQ\n" +
                                    "FROM WRD_EVENT_DATES e\n" +
                                    "inner join WRD_APPLICATIONS ea\n" +
                                    "on e.APPL_ID_SEQ = ea.APPL_ID_SEQ\n" +
                                    "WHERE (e.EVTP_CD = 'PAMH' and ea.APTP_CD in ('600P', '606P'))\n" +
                                    "OR (e.EVTP_CD = 'FRMR' and ea.APTP_CD not in ('600P', '606P'))\n" +
                                    "group by e.APPL_ID_SEQ\n";

        String countQuery = "SELECT COUNT(*)\n";
        
        String base = "FROM WRD_APPLICATIONS a\n" +
                        "inner join WRD_APPLICATION_TYPES at\n" +
                        "on at.APTP_CD = a.APTP_CD\n" +
                        "inner join WRD_OWNERS o\n" +
                        "on a.APPL_ID_SEQ = o.APPL_ID_SEQ\n" +
                        "inner join WRD_CUSTOMERS c\n" +
                        "on o.CUST_ID_SEQ = c.CUST_ID_SEQ\n" +
                        "inner join WRD_REPRESENTATIVES r\n" +
                        "on r.OWNR_ID_SEQ = o.OWNR_ID_SEQ\n" +
                        "and r.CUST_ID_SEQ_SEC = o.CUST_ID_SEQ\n" +
                        "inner join WRD_CUSTOMERS rc\n" +
                        "on rc.CUST_ID_SEQ = r.CUST_ID_SEQ\n";

        List<String> whereConditions = new ArrayList<>();
        if(basin != null) {
            whereConditions.add("a.BOCA_CD like '" + basin + "'");
        }
        if(applicationId != null) {
            whereConditions.add("to_char(a.APPL_ID_SEQ) like '" + applicationId + "'");
        }
        if(applicationTypeCode != null) {
            whereConditions.add("a.APTP_CD like '" + applicationTypeCode + "'");
        }
        whereConditions.add("r.CUST_ID_SEQ = " + repContactId);
        whereConditions.add("r.END_DT is null");
        whereConditions.add("o.END_DT is null");

        String whereClause = "WHERE " + whereConditions.stream().collect(Collectors.joining("\nAND ")) + "\n";

        select += base;
        select += whereClause;
        select += "group by a.APPL_ID_SEQ, a.BOCA_CD, a.APTP_CD, at.DESCR\n";

        countQuery += base;
        countQuery += whereClause;

        String fullQuery = "SELECT whole2.*\n" +
                            "FROM (\n" +
                                "SELECT whole.*\n" +
                                "FROM (\n" +
                                    "SELECT DENSE_RANK() over (order by " + mainOrderBy + ") rank,\n" +
                                            "no_rank.*,\n" +
                                            "dateReceived.dateReceived\n" +
                                    "FROM (\n" +
                                        select +
                                    ") no_rank\n" +
                                    "left join (\n" +
                                        dateReceivedJoin +
                                    ") dateReceived\n" +
                                    "on dateReceived.APPL_ID_SEQ = no_rank.applicationId\n" +
                                ") whole\n" +
                                "WHERE whole.rank <= :upperlimit\n" +
                                "AND whole.rank > :lowerlimit\n" +
                            ") whole2\n" +
                            "order by rank\n";
        Query q = manager.createNativeQuery(fullQuery);
        q.setParameter("upperlimit", pageable.getPageSize()*(pageable.getPageNumber() + 1));
        q.setParameter("lowerlimit", pageable.getOffset());

        List<Object[]> results = q.getResultList();

        List<RepApplicationOwnerListDto> searchResults = new ArrayList<>();
        for(Object[] res : results) {
            RepApplicationOwnerListDto app = new RepApplicationOwnerListDto();
            app.setApplicationId(((BigDecimal) res[1]).longValue());
            app.setBasin((String) res[2]);
            app.setApplicationTypeCode((String) res[3]);
            app.setApplicationTypeDescription((String) res[4]);
            if(res[10] != null) app.setDateTimeReceived(((Timestamp) res[10]).toLocalDateTime());
            if(res[5] != null) app.setContactId(((BigDecimal) res[5]).longValue());
            String ownName = Helpers.buildName((String) res[6], (String) res[7], (String) res[8], (String) res[9]);
            app.setName(ownName);
            searchResults.add(app);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countQuery)
                                        .getSingleResult())
                                        .longValue();
        Page<RepApplicationOwnerListDto> resultPage = new PageImpl<>(searchResults, pageable, count);

        return resultPage;
    }

    private String getRepApplicationSort(RepApplicationSortColumn sort, DescSortDirection sortDirection) {
        String sortColumn;
        String direction;
        if(sortDirection == DescSortDirection.ASC) {
            direction = " ASC";
        } else {
            direction = " DESC";
        }

        if(sort == RepApplicationSortColumn.BASIN) {
            sortColumn = "basin";
        } else if(sort == RepApplicationSortColumn.APPLICATIONTYPECODE) {
            sortColumn = "typeCode";
        } else if(sort == RepApplicationSortColumn.APPLICATIONTYPEDESCRIPTION) {
            sortColumn = "typeCode";
        } else if(sort == RepApplicationSortColumn.NAME) {
            sortColumn = "lastName" + direction +
                            ", firstName" + direction +
                            ", middleInitial" + direction +
                            ", sufx";
        } else if(sort == RepApplicationSortColumn.DATETIMERECEIVED) {
            sortColumn = "dateReceived.dateReceived";
        } else {
            sortColumn = "applicationId ";
        }

        sortColumn += direction;

        sortColumn += ", applicationId DESC";

        return sortColumn;
    }

}
