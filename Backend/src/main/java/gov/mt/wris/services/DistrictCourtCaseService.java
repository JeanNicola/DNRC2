package gov.mt.wris.services;

import gov.mt.wris.dtos.DistrictCourtCreateDto;
import gov.mt.wris.dtos.DistrictCourtDetailDto;
import gov.mt.wris.dtos.DistrictCourtEventCreateDto;
import gov.mt.wris.dtos.DistrictCourtEventDetailDto;
import gov.mt.wris.dtos.DistrictCourtEventUpdateDto;
import gov.mt.wris.dtos.DistrictCourtEventsPageDto;
import gov.mt.wris.dtos.DistrictCourtEventsSortColumn;
import gov.mt.wris.dtos.DistrictCourtUpdateDto;
import gov.mt.wris.dtos.DistrictCourtsPageDto;
import gov.mt.wris.dtos.DistrictCourtsSortColumn;
import gov.mt.wris.dtos.SortDirection;

import java.math.BigDecimal;

public interface DistrictCourtCaseService {

    public DistrictCourtsPageDto getDistrictCourts(int pagenumber, int pagesize, DistrictCourtsSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId);

    public DistrictCourtEventsPageDto getDistrictCourtEvents(int pagenumber, int pagesize, DistrictCourtEventsSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId, BigDecimal districtId);

    public DistrictCourtDetailDto createDistrictCourt(Long caseId, DistrictCourtCreateDto createDto);

    public DistrictCourtDetailDto updateDistrictCourt(Long caseId, Long districtId, DistrictCourtUpdateDto updateDto);

    public void deleteDistrictCourt(Long caseId, Long districtId);

    public DistrictCourtEventDetailDto createDistrictCourtEvent(Long caseId, Long districtId, DistrictCourtEventCreateDto createDto);

    public DistrictCourtEventDetailDto updateDistrictCourtEvent(Long caseId, Long districtId, Long eventDateId, DistrictCourtEventUpdateDto updateDto);

    public void deleteDistrictCourtEvent(Long caseId, Long districtId, Long eventDateId);

}
