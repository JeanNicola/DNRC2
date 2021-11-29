package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.DistrictCourtCase;
import gov.mt.wris.models.Event;
import gov.mt.wris.repositories.DistrictCourtCaseRepository;
import gov.mt.wris.repositories.EventRepository;
import gov.mt.wris.services.DistrictCourtCaseService;
import gov.mt.wris.utils.Helpers;
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
public class DistrictCourtCaseServiceImpl implements DistrictCourtCaseService {

    private static Logger LOGGER = LoggerFactory.getLogger(DistrictCourtCaseServiceImpl.class);

    @Autowired
    private DistrictCourtCaseRepository districtCourtCaseRepository;

    @Autowired
    private EventRepository eventRepository;

    public DistrictCourtsPageDto getDistrictCourts(int pageNumber, int pageSize, DistrictCourtsSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId) {

        LOGGER.info("Get District Court details for Court Case or Hearing");

        Sort sortDtoColumn = getDistrictCourtsSortColumnSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<DistrictCourtCase> results = districtCourtCaseRepository.findAllDistrictCourtCaseByCaseId(request, caseId);

        DistrictCourtsPageDto page = new DistrictCourtsPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return getDistrictCourtDetailDto(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getDistrictCourtsSortColumnSortColumn(DistrictCourtsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        switch (sortColumn) {
            case CAUSENUMBER:
                orders.add(new Sort.Order(direction, "causeNumber"));
                break;
            case DISTRICTCOURTNUMBER:
                orders.add(new Sort.Order(direction, "districtCourt"));
                break;
            case COMPLETENAME:
                orders.add(new Sort.Order(direction, "judge.lastName"));
                orders.add(new Sort.Order(direction, "judge.firstName"));
                orders.add(new Sort.Order(direction, "judge.midInitial"));
                break;
            case COUNTYNAME:
                orders.add(new Sort.Order(direction, "county.name"));
                break;
            case SUPREMECOURTCAUSENUMBER:
                orders.add(new Sort.Order(direction, "supremeCourtCauseNumber"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "causeNumber"));
        return Sort.by(orders);

    }

    private DistrictCourtDetailDto getDistrictCourtDetailDto(DistrictCourtCase model) {

        DistrictCourtDetailDto dto = new DistrictCourtDetailDto();
        dto.setDistrictId(model.getDistrictId().longValue());
        dto.setCauseNumber(model.getCauseNumber()!=null?model.getCauseNumber():null);
        dto.setDnrcId(model.getJudgeId()!=null?Long.valueOf(model.getJudgeId()):null);
        if (model.getJudge()!=null)
            dto.setCompleteName(Helpers.buildName(
                model.getJudge().getLastName(),
                model.getJudge().getFirstName(),
                model.getJudge().getMidInitial()));
        dto.setCountyId(model.getCountyId()!=null?model.getCountyId().longValue():null);
        dto.setCountyName(model.getCounty()!=null?model.getCounty().getName():null);
        dto.setDistrictCourtNumber(model.getDistrictCourt()!=null?model.getDistrictCourt():null);
        dto.setSupremeCourtCauseNumber(model.getSupremeCourtCauseNumber()!=null?model.getSupremeCourtCauseNumber():null);
        return dto;

    }

    public DistrictCourtEventsPageDto getDistrictCourtEvents(int pageNumber, int pageSize, DistrictCourtEventsSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId, BigDecimal districtId) {

        LOGGER.info("Get District Court Events for Court Case or Hearing");

        Sort sortDtoColumn = getDistrictCourtEventsSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<Event> results = districtCourtCaseRepository.findDistrictCourtEvents(request, caseId, districtId);

        DistrictCourtEventsPageDto page = new DistrictCourtEventsPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return getDistrictCourtEventDetailDto(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getDistrictCourtEventsSortColumn(DistrictCourtEventsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        switch (sortColumn) {
            case EVENTTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "eventType.description"));
                break;
            case EVENTDATE:
                orders.add(new Sort.Order(direction, "eventDate"));
                break;
            case COMMENTS:
                orders.add(new Sort.Order(direction, "eventComment"));
                break;
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "eventType.description"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "eventDate"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "eventComment"));
        return Sort.by(orders);

    }

    private DistrictCourtEventDetailDto getDistrictCourtEventDetailDto(Event model) {

        DistrictCourtEventDetailDto dto = new DistrictCourtEventDetailDto();
        dto.setEventDateId(model.getEventId().longValue());
        dto.setEventDate(model.getEventDate().toLocalDate());
        dto.setEventType(model.getEventTypeCode());
        dto.setEventTypeDescription(model.getEventType()!=null?model.getEventType().getDescription():null);
        dto.setComments(model.getEventComment()!=null?model.getEventComment():null);
        return dto;

    }

    public DistrictCourtDetailDto createDistrictCourt(Long caseId, DistrictCourtCreateDto createDto) {

        try {
            return createDistrictCourtTransaction(caseId, createDto);
        } catch (DataIntegrityViolationException e){
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.DIST_CASE_FK")) {
                    throw new DataIntegrityViolationException("The case id " + caseId + " does not exist");
                } if (constraintMessage.contains("WRD.DIST_CNTY_FK")) {
                    throw new DataIntegrityViolationException("The county id " + createDto.getCountyId() + " does not exist");
                } if (constraintMessage.contains("WRD.DIST_MSI_FK")) {
                    throw new DataIntegrityViolationException("The dnrc id " + createDto.getDnrcId() + " does not exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }

    }

    @Transactional
    private DistrictCourtDetailDto createDistrictCourtTransaction(Long caseId, DistrictCourtCreateDto createDto) {

        LOGGER.info("Create District Court for Court Case or Hearing");

        DistrictCourtCase model = new DistrictCourtCase();
        model.setCaseId(new BigDecimal(caseId));
        model.setCauseNumber(createDto.getCauseNumber());
        if (createDto.getDistrictCourtNumber()!=null)
            model.setDistrictCourt(createDto.getDistrictCourtNumber());
        if (createDto.getDnrcId()!=null)
            model.setJudgeId(createDto.getDnrcId().toString());
        if (createDto.getCountyId()!=null)
            model.setCountyId(new BigDecimal(createDto.getCountyId()));
        if (createDto.getSupremeCourtCauseNumber()!=null)
            model.setSupremeCourtCauseNumber(createDto.getSupremeCourtCauseNumber());
        return getDistrictCourtDetailDto(districtCourtCaseRepository.saveAndFlush(model));

    }

    public DistrictCourtDetailDto updateDistrictCourt(Long caseId, Long districtId, DistrictCourtUpdateDto updateDto) {

        LOGGER.info("Update District Court for Court Case or Hearing");

        Optional<DistrictCourtCase> fndCase = districtCourtCaseRepository.getDistrictCourtCaseByCaseIdAndDistrictId(new BigDecimal(caseId), new BigDecimal(districtId));
        if (!fndCase.isPresent())
            throw new NotFoundException(String.format("Case %s or District Court %s not found",caseId, districtId));
        DistrictCourtCase model = fndCase.get();
        model.setJudgeId(updateDto.getDnrcId()!=null?updateDto.getDnrcId().toString():null);
        model.setCauseNumber(updateDto.getCauseNumber());
        model.setSupremeCourtCauseNumber(updateDto.getSupremeCourtCauseNumber());
        model.setCountyId(updateDto.getCountyId()!=null?new BigDecimal(updateDto.getCountyId()):null);
        model.setDistrictCourt(updateDto.getDistrictCourtNumber());
        return getDistrictCourtDetailDto(districtCourtCaseRepository.saveAndFlush(model));

    }

    public void deleteDistrictCourt(Long caseId, Long districtId) {

        LOGGER.info("Delete District Court for Court Case or Hearing");
        
        districtCourtCaseRepository.deleteDistrictCourtCaseByCaseIdAndDistrictId(new BigDecimal(caseId), new BigDecimal(districtId));

    }

    public DistrictCourtEventDetailDto createDistrictCourtEvent(Long caseId, Long districtId, DistrictCourtEventCreateDto createDto) {

        try {
            return createDistrictCourtEventTransaction(caseId, districtId, createDto);
        } catch (DataIntegrityViolationException e){
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.EVDT_CASE_FK")) {
                    throw new DataIntegrityViolationException("The case id " + caseId + " does not exist");
                } if (constraintMessage.contains("WRD.EVDT_EVTP_FK")) {
                    throw new DataIntegrityViolationException("The event type " + createDto.getEventType() + " does not exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }

    }

    @Transactional
    private DistrictCourtEventDetailDto createDistrictCourtEventTransaction(Long caseId, Long districtId, DistrictCourtEventCreateDto createDto) {

        LOGGER.info("Create District Court Event for Court Case or Hearing");

        Event model = new Event();
        model.setCaseId(new BigDecimal(caseId));
        model.setDistrictId(new BigDecimal(districtId));
        model.setEventTypeCode(createDto.getEventType());
        model.setEventDate(createDto.getEventDate().atStartOfDay());
        if (createDto.getComments()!=null)
            model.setEventComment(createDto.getComments());
        return getDistrictCourtEventDetailDto(eventRepository.saveAndFlush(model));

    }

    public DistrictCourtEventDetailDto updateDistrictCourtEvent(Long caseId, Long districtId, Long eventDateId, DistrictCourtEventUpdateDto updateDto) {

        LOGGER.info("Update District Court Event for Court Case or Hearing");

        Optional<Event> fndEvent = eventRepository.getDistrictCourtEvent(new BigDecimal(districtId), new BigDecimal(eventDateId));
        if (!fndEvent.isPresent())
            throw new NotFoundException(String.format("District Court Event %s not found",eventDateId));
        Event model = fndEvent.get();
        if ((updateDto.getEventType()!=null) && (!updateDto.getEventType().equals(model.getEventTypeCode()))) {
            /** Cause Event (event type) is changing and that requires new Cause Date (event date) **/
            model.setEventTypeCode(updateDto.getEventType());
        } else {
            if (updateDto.getEventDate()!=null) updateDto.getEventDate().atStartOfDay();
        }
        model.setEventDate(updateDto.getEventDate().atStartOfDay());
        model.setEventComment(updateDto.getComments()!=null?updateDto.getComments():null);
        return getDistrictCourtEventDetailDto(eventRepository.saveAndFlush(model));

    }

    public void deleteDistrictCourtEvent(Long caseId, Long districtId, Long eventDateId) {

        LOGGER.info("Delete District Court Event for Court Case or Hearing");
        eventRepository.deleteEventByDistrictIdAndEventId(new BigDecimal(districtId), new BigDecimal(eventDateId));

    }

}
