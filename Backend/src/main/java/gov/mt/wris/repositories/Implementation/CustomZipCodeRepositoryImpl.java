package gov.mt.wris.repositories.Implementation;

import java.math.BigDecimal;
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

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.ZipCodeDto;
import gov.mt.wris.dtos.ZipCodeSortColumn;
import gov.mt.wris.repositories.CustomZipCodeRepository;

public class CustomZipCodeRepositoryImpl implements CustomZipCodeRepository{
    private static Logger LOGGER = LoggerFactory.getLogger(CustomZipCodeRepositoryImpl.class); 

    @PersistenceContext
    EntityManager manager;

    @Override
    public Page<ZipCodeDto> getZipCodes(Pageable pageable, ZipCodeSortColumn sortDTOColumn, SortDirection sortDirection, String zipCode, String cityName, String stateCode) {
        String sortColumn = getZipCodeSortColumn(sortDTOColumn);
        String dataQuery = "SELECT DISTINCT z.ZIP_CD,\n" +
                                            "c.NM,\n" +
                                            "c.STT_CD,\n" +
                                            "s.NM STATE_NAME,\n" +
                                            "z.ZPCD_ID_SEQ,\n" +
                                            "c.CITY_ID_SEQ\n";
        String countQuery = "Select Count(*)\n";

        String basicQuery = "FROM WRD_CITIES c\n" +
                            "JOIN WRD_STATES s\n" +
                            "on s.STT_CD = c.STT_CD\n" +
                            "LEFT JOIN WRD_ZIP_CODES z\n" +
                            "ON z.CITY_ID_SEQ = c.CITY_ID_SEQ\n";
        List<String> whereConditions = new ArrayList<>();
        if(zipCode != null) {
            whereConditions.add("z.ZIP_CD like '"+zipCode+"'");
        }
        if(cityName != null) {
            whereConditions.add("c.NM like '"+cityName+"'");
        }
        if(stateCode != null) {
            whereConditions.add("c.STT_CD like '"+stateCode+"'");
        }
        basicQuery += (whereConditions.size() > 0) ? "WHERE " : "";
        basicQuery += whereConditions.stream().collect(Collectors.joining("\nAND "));

        /*
        * Set the requested sort column.
        * Use Zipcode as the secondary sort unless query column is ZipCode
        * then use state and city as secondary sorts
        */
        basicQuery += "\norder by " + sortColumn + " " + sortDirection.getValue();
        if (sortColumn == "z.ZIP_CD") {
            basicQuery += ", s.NM ASC, c.NM ASC\n";
        } else {
            basicQuery += ", z.ZIP_CD ASC\n";
        }

        countQuery += basicQuery;
        dataQuery  += basicQuery;
        dataQuery =    "SELECT * FROM ("+
                        "SELECT all_.*,\n" +
                        "rownum rownum_\n" +
                        "FROM (\n" +
                        dataQuery +
                        ") all_\n" +
                        "WHERE rownum <= :upperlimit\n" +
                        ")\n" +
                        "WHERE rownum_ > :lowerlimit";

        Query q = manager.createNativeQuery(dataQuery);
        q.setParameter("upperlimit", pageable.getPageSize()*(pageable.getPageNumber()+1));
        q.setParameter("lowerlimit", pageable.getOffset());

        List<Object[]> results = q.getResultList();

        List<ZipCodeDto> zipCodes = new ArrayList<>();
        for(Object[] zip : results) {
            ZipCodeDto dto = new ZipCodeDto();
            dto.setZipCode((String) zip[0]);
            dto.setCityName((String) zip[1]);
             dto.setStateCode((String) zip[2]);
            dto.setStateName((String) zip[3]);
            if(zip[4] != null) dto.setId(((BigDecimal) zip[4]).longValue());
            if(zip[5] != null) dto.setCityId(((BigDecimal) zip[5]).longValue());
            zipCodes.add(dto);
        }

        long count = ((BigDecimal) manager.createNativeQuery(countQuery).getSingleResult()).longValue();

        Page<ZipCodeDto> resultPage = new PageImpl<>(zipCodes, pageable, count);

        return resultPage;
    }

    private String getZipCodeSortColumn(ZipCodeSortColumn DTOColumn) {
        if (DTOColumn == ZipCodeSortColumn.CITYNAME) return "c.NM";
        if (DTOColumn == ZipCodeSortColumn.ZIPCODE) return "z.ZIP_CD";
        if (DTOColumn == ZipCodeSortColumn.STATENAME) return "s.NM";
        return "c.STT_CD";
    }
}