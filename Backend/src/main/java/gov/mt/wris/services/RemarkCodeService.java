package gov.mt.wris.services;

import gov.mt.wris.dtos.AllRemarkCodeReferencesDto;

import java.math.BigDecimal;

import gov.mt.wris.dtos.RemarkCodeSearchPageDto;
import gov.mt.wris.dtos.RemarkCodeSortColumn;
import gov.mt.wris.dtos.SortDirection;

public interface RemarkCodeService {

    public RemarkCodeSearchPageDto searchRemarkCodesByWaterRightType(int pagenumber,
        int pagesize,
        RemarkCodeSortColumn sortColumn,
        SortDirection sortDirection,
        String remarkCode,
        Long waterRightId);

    public AllRemarkCodeReferencesDto getReportRemarkCodes();
}
