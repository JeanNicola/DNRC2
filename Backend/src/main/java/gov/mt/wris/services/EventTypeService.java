package gov.mt.wris.services;

import gov.mt.wris.dtos.AllApplicationTypesDto;
import gov.mt.wris.dtos.AllCaseTypesDto;
import gov.mt.wris.dtos.AllDecreeTypesDto;
import gov.mt.wris.dtos.AllEventCodeDescDto;
import gov.mt.wris.dtos.EventTypeDto;
import gov.mt.wris.dtos.EventTypePageDto;
import gov.mt.wris.dtos.EventTypeSortColumn;
import gov.mt.wris.dtos.SortDirection;

import java.util.Optional;

public interface EventTypeService {
    Optional<EventTypeDto> getEvent(String code);

    EventTypePageDto getEventTypes(int pagenumber, int pagesize, EventTypeSortColumn sortColumn, SortDirection sortDirection, String Code, String Description, String DueDays);

    AllCaseTypesDto getEventCaseTypes(String eventCode);

    AllApplicationTypesDto getEventApplicationTypes(String eventCode);

    AllEventCodeDescDto getEventTypeCodeByApplicationTypeCode(String typeCode);

    AllDecreeTypesDto getEventDecreeTypes(String eventCode);

    // EventTypeScreenDto getEventTypeScreen(String eventTypeCode);

    EventTypeDto createEventType(EventTypeDto eventDto);

    void deleteEventType(String code);

    EventTypeDto replaceEventType(EventTypeDto eventDto, String code);

    EventTypeDto toUpperCase(EventTypeDto Dto);

    AllEventCodeDescDto getAllEventCodeDesc();

    AllEventCodeDescDto getEventTypeCodeByCaseTypeCode(String typeCode, Integer supported);
}
