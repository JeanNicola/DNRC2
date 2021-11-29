package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.ScheduleEventCreateDto;
import gov.mt.wris.dtos.ScheduleEventDetailDto;
import gov.mt.wris.dtos.ScheduleEventUpdateDto;
import gov.mt.wris.dtos.ScheduleEventsPageDto;
import gov.mt.wris.dtos.ScheduleEventsSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.CaseSchedule;
import gov.mt.wris.repositories.CaseScheduleRepository;
import gov.mt.wris.services.CaseScheduleService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CaseScheduleServiceImpl implements CaseScheduleService {

    private static Logger LOGGER = LoggerFactory.getLogger(CaseScheduleServiceImpl.class);

    @Autowired
    private CaseScheduleRepository caseScheduleRepository;

    public ScheduleEventsPageDto getScheduleEvents(int pageNumber, int pageSize, ScheduleEventsSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId) {

        LOGGER.info("Get Schedule of Events for Court Case or Hearing");

        Sort sortDtoColumn = getScheduleEventsSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<CaseSchedule> results = caseScheduleRepository.findAllByCaseId(request, caseId);

        ScheduleEventsPageDto page = new ScheduleEventsPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return scheduleEventDetailDtoLoader(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);
        return page;

    }

    private Sort getScheduleEventsSortColumn(ScheduleEventsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();

        switch (sortColumn) {
            case EVENTTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "et.description"));
                break;
            case EVENTSTATUS:
                orders.add(new Sort.Order(direction, "scheduleStatus"));
                break;
            case EVENTDATE:
                orders.add(new Sort.Order(direction, "scheduleDate"));
            case NOTES:
                orders.add(new Sort.Order(direction, "scheduleComments"));
                break;
            case EVENTBEGINTIME:
                orders.add(new Sort.Order(direction, "scheduleBeginTime"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "et.description"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "scheduleStatus"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "scheduleDate"));
        return Sort.by(orders);

    }

    private ScheduleEventDetailDto scheduleEventDetailDtoLoader(CaseSchedule model) {

        ScheduleEventDetailDto dto = new ScheduleEventDetailDto();
        dto.setScheduleId(model.getScheduleId().longValue());
        dto.setEventType(model.getEventTypeCode());
        if (model.getEventType()!=null)
            dto.setEventTypeDescription(model.getEventType().getDescription());
        dto.setEventStatus(model.getScheduleStatus());
        dto.setEventDate(model.getScheduleDate() != null ? model.getScheduleDate().toLocalDate() : null);
        dto.setEventBeginTime(model.getScheduleBeginTime());
        dto.setNotes(model.getScheduleComments()!=null?model.getScheduleComments():null);
        return dto;

    }

    public ScheduleEventDetailDto createScheduleEvent(Long caseId, ScheduleEventCreateDto createDto) {

        try {
            return createScheduleEventTransaction(caseId, createDto);
        } catch (DataIntegrityViolationException e){
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.SCH_CASE_FK")) {
                    throw new DataIntegrityViolationException("The case id " + caseId + " does not exist");
                } else if (constraintMessage.contains("WRD.SCH_EVENT_FK")) {
                    throw new DataIntegrityViolationException("The case event type " + createDto.getEventType() + " does not exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }

    }

    @Transactional
    private ScheduleEventDetailDto createScheduleEventTransaction(Long caseId, ScheduleEventCreateDto createDto) {

        LOGGER.info("Create Schedule Event for Court Case or Hearing");

        CaseSchedule cs = new CaseSchedule();
        cs.setCaseId(new BigDecimal(caseId));
        cs.setScheduleStatus(createDto.getEventStatus());
        cs.setEventTypeCode(createDto.getEventType());
        cs.setScheduleDate(createDto.getEventDate().atStartOfDay());
        if (createDto.getEventBeginTime()!=null)
            cs.setScheduleBeginTime(createDto.getEventBeginTime());
        if (createDto.getNotes()!=null)
            cs.setScheduleComments(createDto.getNotes());
        return scheduleEventDetailDtoLoader(caseScheduleRepository.saveAndFlush(cs));

    }

    @Transactional
    public ScheduleEventDetailDto updateScheduleEvent(Long caseId, Long scheduleId, ScheduleEventUpdateDto updateDto) {

        LOGGER.info("Update Schedule Event for Court Case or Hearing");

        Optional<CaseSchedule> fndCs =  caseScheduleRepository.findCaseScheduleByCaseIdAndScheduleId(new BigDecimal(caseId), new BigDecimal(scheduleId));
        if (!fndCs.isPresent())
            throw new NotFoundException(String.format("Case Id %s or Schedule Id %s not found.",caseId, scheduleId));
        CaseSchedule cs = fndCs.get();
        if (!updateDto.getEventType().equals(cs.getEventTypeCode())) {
            /** User is changing event type, which now requires event status and event date **/
            if (updateDto.getEventDate()==null || updateDto.getEventStatus()==null)
                throw new NotFoundException("Event Status and Event Date are required when Event Type is changed.");
        }
        cs.setEventTypeCode(updateDto.getEventType());
        cs.setScheduleStatus(updateDto.getEventStatus()!=null?updateDto.getEventStatus():null);
        cs.setScheduleDate(updateDto.getEventDate()!=null?updateDto.getEventDate().atStartOfDay():null);
        cs.setScheduleBeginTime(updateDto.getEventBeginTime()!=null?updateDto.getEventBeginTime():null);
        cs.setScheduleComments(updateDto.getNotes()!=null?updateDto.getNotes():null);
        return scheduleEventDetailDtoLoader(caseScheduleRepository.saveAndFlush(cs));

    }

    public void deleteScheduleEvent(Long caseId, Long scheduleId) {

        LOGGER.info("Delete Schedule Event for Court Case or Hearing");
        caseScheduleRepository.deleteCaseScheduleByCaseIdAndScheduleId(new BigDecimal(caseId), new BigDecimal(scheduleId));

    }

}
