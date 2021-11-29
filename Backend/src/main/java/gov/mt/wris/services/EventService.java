package gov.mt.wris.services;

import gov.mt.wris.dtos.EventsDto;
import gov.mt.wris.dtos.EventsPageDto;
import gov.mt.wris.dtos.EventsSortColumn;
import gov.mt.wris.dtos.SortDirection;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementation of event service
 *
 * @author Vannara Houth
 */
public interface EventService {


    /**
     * @param applicationId
     * @param eventId
     * @param eventsDto
     * @return
     */
    public Optional<EventsDto> changeEvent(
            String applicationId,
            Long eventId,
            EventsDto eventsDto
    );

    /**
     * @param applicationId
     * @param eventId
     */
    public void deleteEvent(String applicationId, Long eventId);

    /**
     * @param applicationId
     * @param eventsDto
     * @return
     */
    public EventsDto createEvent(String applicationId, EventsDto eventsDto);

    /**
     * @param dto
     * @return
     */
    public EventsDto toUpperCase(EventsDto dto);

    public EventsPageDto findEventsByApplicationId(String applicationId,
                                                   Integer pageNumber,
                                                   Integer pageSize,
                                                   EventsSortColumn sortColumn,
                                                   SortDirection sortDirection
    );

    public void deleteCaseRegisterEvent(BigDecimal caseId, BigDecimal eventId);

}
