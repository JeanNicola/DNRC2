package gov.mt.wris.services.Implementation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.mt.wris.dtos.AllApplicationTypesDto;
import gov.mt.wris.dtos.AllCaseTypesDto;
import gov.mt.wris.dtos.AllDecreeTypesDto;
import gov.mt.wris.dtos.AllEventCodeDescDto;
import gov.mt.wris.dtos.ApplicationTypeDto;
import gov.mt.wris.dtos.CaseTypeDto;
import gov.mt.wris.dtos.DecreeTypeDto;
import gov.mt.wris.dtos.EventCodeDescDto;
import gov.mt.wris.dtos.EventTypeDto;
import gov.mt.wris.dtos.EventTypePageDto;
import gov.mt.wris.dtos.EventTypeSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.DataUsedElsewhereException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.ApplicationTypeXref;
import gov.mt.wris.models.CaseTypeXref;
import gov.mt.wris.models.DecreeTypeXref;
import gov.mt.wris.models.EventType;
import gov.mt.wris.repositories.ApplicationTypeXrefRepository;
import gov.mt.wris.repositories.CaseTypeXrefRepository;
import gov.mt.wris.repositories.DecreeTypeXrefRepository;
import gov.mt.wris.repositories.EventTypeRepository;
import gov.mt.wris.services.EventTypeService;



@Service
public class EventTypeServiceImpl implements EventTypeService {

    private static Logger LOGGER = LoggerFactory.getLogger(EventTypeServiceImpl.class);
    private static List<String> unsupportedCaseTypes = Arrays.asList("OBJL", "ISRO", "MOTA", "OACT", "SPLT", "DCC", "AWC", "EXPN");

    @Autowired
    EventTypeRepository eventRepository;

    @Autowired
    ApplicationTypeXrefRepository appRepository;

    @Autowired
    CaseTypeXrefRepository caseRepository;

    @Autowired
    DecreeTypeXrefRepository decreeRepository;

    @Override
    public Optional<EventTypeDto> getEvent(String code) {
        LOGGER.info("Getting a specific Case Type");
        Optional<EventType> eventType = eventRepository.findById(code);
        EventTypeDto eventTypeDto = null;
        if (eventType.isPresent()) {
            eventTypeDto = getEventDto(eventType.get());
        }
        return Optional.ofNullable(eventTypeDto);
    }

    @Override
    public EventTypePageDto getEventTypes(int pagenumber, int pagesize, EventTypeSortColumn sortDTOColumn, SortDirection sortDirection, String Code, String Description, String DueDays) {
        LOGGER.info("Getting a Page of Event Types");
        //pagination by default uses 0 to start, shift it so we can use number displayed to users
        Pageable request = PageRequest.of(pagenumber - 1, pagesize);
        //keep the conversion from dto column to entity column in the service layer
        String sortColumn = getEntitySortColumn(sortDTOColumn);
        Page<EventType> resultsPage = eventRepository.getEventTypes(request, sortColumn, sortDirection, Code, Description, DueDays);

        EventTypePageDto eventPage = new EventTypePageDto();

        eventPage.setResults(resultsPage.getContent().stream().map(type -> {
            return getEventDto(type);
        }).collect(Collectors.toList()));

        eventPage.setCurrentPage(resultsPage.getNumber() + 1);
        eventPage.setPageSize(resultsPage.getSize());

        eventPage.setTotalPages(resultsPage.getTotalPages());
        eventPage.setTotalElements(resultsPage.getTotalElements());

        eventPage.setSortColumn(sortDTOColumn);
        eventPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if (Code != null) {
            filters.put("code", Code);
        }
        if (Description != null) {
            filters.put("description", Description);
        }
        if (DueDays != null) {
            filters.put("responseDueDays", DueDays);
        }
        eventPage.setFilters(filters);

        return eventPage;
    }

    @Override
    public AllCaseTypesDto getEventCaseTypes(String eventCode) {
        Optional<EventType> eventType = eventRepository.findById(eventCode);
        if (!eventType.isPresent()) {
            throw new NotFoundException("No Event Type found with this event Code");
        }

        AllCaseTypesDto caseTypes = new AllCaseTypesDto();
        caseTypes.setResults(eventRepository.getCaseTypesByEventCode(eventCode).stream().map(caseType -> {
            return getCaseTypeDto(caseType);
        }).collect(Collectors.toList()));
        return caseTypes;
    }


    private CaseTypeDto getCaseTypeDto(CaseTypeXref model) {
        CaseTypeDto caseType = new CaseTypeDto();

        caseType.setCode(model.getCaseCode());
        caseType.setDescription(model.getCaseType().getDescription());

        return caseType;
    }

    @Override
    public AllApplicationTypesDto getEventApplicationTypes(String eventCode) {
        Optional<EventType> eventType = eventRepository.findById(eventCode);
        if (!eventType.isPresent()) {
            throw new NotFoundException("No Event Type found with this event Code");
        }

        AllApplicationTypesDto appTypes = new AllApplicationTypesDto();
        appTypes.setResults(eventRepository.getApplicationTypesByEventCode(eventCode).stream().map(appType -> {
            return getApplicationTypeDto(appType);
        }).collect(Collectors.toList()));
        return appTypes;
    }

    private ApplicationTypeDto getApplicationTypeDto(ApplicationTypeXref model) {
        ApplicationTypeDto appType = new ApplicationTypeDto();

        appType.setCode(model.getApplicationCode());
        appType.setDescription(model.getApplicationType().getDescription());

        return appType;
    }

    @Override
    public AllDecreeTypesDto getEventDecreeTypes(String eventCode) {
        Optional<EventType> eventType = eventRepository.findById(eventCode);
        if (!eventType.isPresent()) {
            throw new NotFoundException("No Event Type found with this event Code");
        }

        AllDecreeTypesDto decreeTypes = new AllDecreeTypesDto();
        decreeTypes.setResults(eventRepository.getDecreeTypesByEventCode(eventCode).stream().map(decreeType -> {
            return getDecreeTypeDto(decreeType);
        }).collect(Collectors.toList()));
        return decreeTypes;
    }

    private DecreeTypeDto getDecreeTypeDto(DecreeTypeXref model) {
        DecreeTypeDto decreeType = new DecreeTypeDto();

        decreeType.setCode(model.getDecreeCode());
        decreeType.setDescription(model.getDecreeType().getDescription());

        return decreeType;
    }

    public EventTypeDto createEventType(EventTypeDto eventDto) {
        LOGGER.info("Creating a Event Type");
        // need to check if it already exists, otherwise save would just do an update
        Optional<EventTypeDto> existingEvent = getEvent(eventDto.getCode());
        if (existingEvent.isPresent()) {
            throw new DataConflictException("An Event Type with Code " + eventDto.getCode() + " already exists");
        }
        EventType eventType = eventRepository.save(getEvent(eventDto));
        return getEventDto(eventType);
    }

    @Transactional
    public void deleteEventType(String code) {
        LOGGER.info("Deleting an Event Type");
        // check for xrefs, if so
        int appCount = eventRepository.countApplicationXref(code);
        int caseCount = eventRepository.countCaseXref(code);
        int decreeCount = eventRepository.countDecreeXref(code);

        // if only one total, delete that one
        if (appCount + caseCount + decreeCount == 1) {
            if (appCount > 0) {
                appRepository.deleteAllByEventCode(code);
            } else if (caseCount > 0) {
                caseRepository.deleteAllByEventCode(code);
            } else {
                decreeRepository.deleteAllByEventCode(code);
            }
        } else if (appCount + caseCount + decreeCount > 1) {
            throw new DataUsedElsewhereException(code + " has multiple application, case or decree types associated with it");
        }

        try {
            eventRepository.deleteById(code);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Event Type with code " + code + " not found");
        } catch (DataIntegrityViolationException ex) {
            throw new DataConflictException("Unable to delete. Record with code " + code + " is in use.");
        }
    }

    @Transactional
    public EventTypeDto replaceEventType(EventTypeDto eventDto, String code) {
        LOGGER.info("Updating an Event Type");
        // Grab old version with id
        Optional<EventType> foundEventType = eventRepository.findById(code);
        EventType newEventType = null;

        if (!foundEventType.isPresent()) {
            throw new NotFoundException("The Event Type with code " + code + " was not found");
        }

        // Update version to new dto, including with the new id
        if (!code.equals(eventDto.getCode())) {
            throw new DataConflictException("Changing the Event Type Code isn't allowed. Delete the Event Type and create a new one");
        } else {
            newEventType = eventRepository.save(getEvent(eventDto));
            return getEventDto(newEventType);
        }
    }

    private EventTypeDto getEventDto(EventType ModelType) {
        EventTypeDto dto = new EventTypeDto();
        dto.setCode(ModelType.getCode());
        dto.setDescription(ModelType.getDescription());
        dto.setResponseDueDays(ModelType.getDueDays());
        return dto;
    }

    private EventType getEvent(EventTypeDto dto) {
        EventType eventType = new EventType();
        eventType.setCode(dto.getCode().toUpperCase());
        eventType.setDescription(dto.getDescription().toUpperCase());
        eventType.setDueDays(dto.getResponseDueDays());
        return eventType;
    }

    private String getEntitySortColumn(EventTypeSortColumn DTOColumn) {
        if (DTOColumn == EventTypeSortColumn.CODE) return "code";
        if (DTOColumn == EventTypeSortColumn.RESPONSEDUEDAYS) return "dueDays";
        return "description";
    }

    public EventTypeDto toUpperCase(EventTypeDto Dto) {
        EventTypeDto updateDto = new EventTypeDto();
        updateDto.setCode(Dto.getCode().toUpperCase());
        updateDto.setResponseDueDays(Dto.getResponseDueDays());    
        updateDto.setDescription((Dto.getDescription().toUpperCase()));
        return updateDto;
    }

    @Override
    public AllEventCodeDescDto getAllEventCodeDesc() {
        AllEventCodeDescDto all = new AllEventCodeDescDto();
        List<EventCodeDescDto> allEventTypes = StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .map(event -> {
                    return getEventCodeDescDto(event);
                }).collect(Collectors.toList());
        all.setResults(allEventTypes);
        return all;
    }

    private EventCodeDescDto getEventCodeDescDto(EventType model) {
        EventCodeDescDto dto = new EventCodeDescDto();
        dto.setCode(model.getCode());
        dto.setDescription(model.getDescription());
        dto.setResponseDueDays(model.getDueDays());
        return dto;
    }


    private EventCodeDescDto getEventCodeDescDto(ApplicationTypeXref model) {
        EventCodeDescDto dto = new EventCodeDescDto();
        dto.setCode(model.getEventType().getCode());
        dto.setDescription(model.getEventType().getDescription());
        dto.setResponseDueDays(model.getEventType().getDueDays());
        return dto;
    }

    private EventCodeDescDto getEventCodeDescDto(CaseTypeXref model) {
        EventCodeDescDto dto = new EventCodeDescDto();
        dto.setCode(model.getEventType().getCode());
        dto.setDescription(model.getEventType().getDescription());
        dto.setResponseDueDays(model.getEventType().getDueDays()!=null?model.getEventType().getDueDays():0);
        return dto;
    }

    @Override
    public AllEventCodeDescDto getEventTypeCodeByApplicationTypeCode(String typeCode) {
        AllEventCodeDescDto all = new AllEventCodeDescDto();
        List<EventCodeDescDto> allEventTypes = StreamSupport.stream(eventRepository.getEventCodeByApplicationType(typeCode).spliterator(),false)
                .map(event -> {
                    return getEventCodeDescDto(event);

                }).collect(Collectors.toList());
        all.setResults(allEventTypes);
        return all;
    }

    public AllEventCodeDescDto getEventTypeCodeByCaseTypeCode(String typeCode, Integer supported) {

        LOGGER.info("Get eligible Event Types for a Case or Hearing Type");
        AllEventCodeDescDto all = new AllEventCodeDescDto();
        if (supported != null && supported==0) {
            List<EventCodeDescDto> allEventTypes = StreamSupport.stream(eventRepository.getEventCodeByCaseType(typeCode).spliterator(),false)
                    .map(event -> {
                        return getEventCodeDescDto(event);

                    }).collect(Collectors.toList());
            all.setResults(allEventTypes);
        } else {
            List<EventCodeDescDto> allEventTypes = StreamSupport.stream(eventRepository.getEventCodeByCaseType(typeCode).spliterator(),false)
                    .filter(event -> !unsupportedCaseTypes.contains(event.getCaseType().getCode()))
                    .map(event -> {
                        return getEventCodeDescDto(event);

                    }).collect(Collectors.toList());
            all.setResults(allEventTypes);
        }

        return all;

    }

}
