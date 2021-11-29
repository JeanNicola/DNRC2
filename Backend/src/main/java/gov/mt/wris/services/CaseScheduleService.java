package gov.mt.wris.services;

import gov.mt.wris.dtos.ScheduleEventCreateDto;
import gov.mt.wris.dtos.ScheduleEventDetailDto;
import gov.mt.wris.dtos.ScheduleEventUpdateDto;
import gov.mt.wris.dtos.ScheduleEventsPageDto;
import gov.mt.wris.dtos.ScheduleEventsSortColumn;
import gov.mt.wris.dtos.SortDirection;

import java.math.BigDecimal;

public interface CaseScheduleService {

    public ScheduleEventsPageDto getScheduleEvents(int pagenumber, int pagesize, ScheduleEventsSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId);

    public ScheduleEventDetailDto createScheduleEvent(Long caseId, ScheduleEventCreateDto createDto);

    public ScheduleEventDetailDto updateScheduleEvent(Long caseId, Long scheduleId, ScheduleEventUpdateDto updateDto);

    public void deleteScheduleEvent(Long caseId, Long scheduleId);

}
