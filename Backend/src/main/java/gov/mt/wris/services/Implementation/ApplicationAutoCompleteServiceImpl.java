package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.ApplicationAutoCompleteDto;
import gov.mt.wris.dtos.ApplicationDto;
import gov.mt.wris.dtos.ApplicationTypeDto;
import gov.mt.wris.dtos.EventDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.*;
import gov.mt.wris.repositories.*;
import gov.mt.wris.services.ApplicationAutoCompleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationAutoCompleteServiceImpl implements ApplicationAutoCompleteService {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationAutoCompleteServiceImpl.class);
    private static final String APPL_SAVE_ERROR = "Please save the application before performing transfer.";
    private static final String NO_VALUE_AUTO_COMPLETE_ERROR = "No value for Auto-Complete. Please contact DBA.";
    private static final String MISSING_BASIN_ERROR = "Please specify a basin before performing the transfer.";
    private static final String NO_APPL_OWNER_ERROR = "At least one application owner is required for the auto-complete function.";
    private static final String NO_WATER_RIGHT_ERROR = "An error has occurred. The water right has not been attached to the application.";
    private static final String NO_FRMR_EVENT_TYPE_ERROR = "A form received event is required for the auto-complete function.";
    private static final String APPLICATION_NOT_ELIGIBLE_ERROR = "Application fails UI validation check and is not eligible for auto completion.";
    private static final String FORM_RECEIVED_EVENT = "FRMR";

    @Autowired
    ApplicationRepository appRepository;

    @Autowired
    OwnerRepository ownRepository;

    @Autowired
    ApplicationTypeRepository appTypeRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    CustomAutoCompleteRepository autoCompleteRepository;

    @Autowired
    VersionApplicationXrefRepository vaxrefRepository;

    @Autowired
    ApplicationTypeRepository applicationTypeRepository;

    @Autowired
    WaterRightRepository waterRightRepository;

    @Override
    public ApplicationAutoCompleteDto autoComplete(Long applicationId) {

        /* Get application thereby validating it exists */
        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findById(appId);
        if(!foundApp.isPresent()) {
            LOGGER.error(APPL_SAVE_ERROR);
            throw new NotFoundException(APPL_SAVE_ERROR);
        }
        ApplicationDto app = getApplicationDto(foundApp.get());

        /* Check if this application bypassed UI validation check */
        if (!canAutoComplete(appId)) {
            LOGGER.error(APPLICATION_NOT_ELIGIBLE_ERROR);
            throw new ValidationException(APPLICATION_NOT_ELIGIBLE_ERROR);
        }

        /* Check that we have auto complete type code */
        Optional<ApplicationType> foundAppType = appTypeRepository.findById(app.getApplicationTypeCode());
        if(!foundAppType.isPresent()) {
            LOGGER.error(NO_VALUE_AUTO_COMPLETE_ERROR);
            throw new NotFoundException(NO_VALUE_AUTO_COMPLETE_ERROR);
        }
        ApplicationTypeDto appType = getApplicationTypeDto(foundAppType.get());

        /* Check that basin code exists */
        if (app.getBasin()==null || app.getBasin().isEmpty()) {
            LOGGER.error(MISSING_BASIN_ERROR);
            throw new NotFoundException(MISSING_BASIN_ERROR);
        }

        /* Check that we have 1 or more owner */
        if (ownRepository.countOwnerByAppId(appId) < 1) {
            LOGGER.error(NO_APPL_OWNER_ERROR);
            throw new NotFoundException(NO_APPL_OWNER_ERROR);
        }

        /* Check that we have form received event (FRMR) */
        Optional<Event> foundEvent = eventRepository.findByAppIdAndEventTypeCode(appId, FORM_RECEIVED_EVENT);
        if(!foundEvent.isPresent()) {
            LOGGER.error(NO_FRMR_EVENT_TYPE_ERROR);
            throw new NotFoundException(NO_FRMR_EVENT_TYPE_ERROR);
        }

        if (autoCompleteRepository.callAutoComplete(applicationId) != 0) {
            LOGGER.error(NO_WATER_RIGHT_ERROR);
            throw new NotFoundException(NO_WATER_RIGHT_ERROR);
        }

        // Once auto-complete is done, get the water right id and return it.
        List<VersionApplicationXref> foundVax = vaxrefRepository.getByApplicationIdOrderByWaterRightIdDesc(appId);
        if(foundVax.size() == 0) {
            LOGGER.error(NO_WATER_RIGHT_ERROR);
            throw new NotFoundException(NO_WATER_RIGHT_ERROR);
        }

        ApplicationAutoCompleteDto dto = new ApplicationAutoCompleteDto();
        dto.setWaterRightId(foundVax.get(0).getWaterRightId().longValue());

        return dto;
    }

    private ApplicationDto getApplicationDto(Application model) {
        ApplicationDto dto = new ApplicationDto();
        dto.setApplicationId(model.getId().longValue());
        dto.setApplicationTypeCode(model.getTypeCode());
        dto.setBasin(model.getBasin());
        if(model.getType() != null) dto.setApplicationTypeDescription(model.getType().getDescription());
        return dto;
    }

    private ApplicationTypeDto getApplicationTypeDto(ApplicationType model) {
        ApplicationTypeDto dto = new ApplicationTypeDto();
        dto.setCode((model.getCode()));
        dto.setDescription(model.getDescription());
        return dto;
    }

    private EventDto getEventDto(Event model) {
        EventDto dto = new EventDto();
        dto.setTypeCode((model.getEventTypeCode()));
        dto.setComment(model.getEventComment());
        dto.setDate(model.getEventDate());
        dto.setResponseDueDate(model.getResponseDueDate());
        return dto;
    }

    private VersionApplicationXref getApplicationDto(VersionApplicationXref model) {
        VersionApplicationXref bean = new VersionApplicationXref();
        bean.setApplicationId(model.getApplicationId());
        bean.setWaterRightId(model.getWaterRightId());
        bean.setCreatedBy(model.getCreatedBy());
        bean.setDtmCreated(model.getDtmCreated());
        bean.setDtmMod(model.getDtmMod());
        bean.setModBy(model.getModBy());
        bean.setVersionId(model.getVersionId());
        return bean;
    }

    private ApplicationAutoCompleteDto getApplicationAutoCompleteDto(ApplicationAutoCompleteDto model) {
        ApplicationAutoCompleteDto dto = new ApplicationAutoCompleteDto();
        dto.setWaterRightId((model.getWaterRightId()));
        return dto;
    }

    public boolean canAutoComplete(BigDecimal applicationId) {

        LOGGER.debug(String.format("Verifying application (%s) is eligible for auto completion", applicationId));

        LOGGER.debug("-- Checking Application");

        /* Retrieve application */
        Optional<Application> foundApp = appRepository.getApplicationById(applicationId);
        if(!foundApp.isPresent()) {
            return false;
        }

        LOGGER.debug("-- Checking Existing Water Right");

        /* Check if water right exists for this application */
        if (waterRightRepository.existsByWaterRightNumberAndBasin(applicationId, foundApp.get().getBasin())) {
            return false;
        }

        LOGGER.debug("-- Checking Version");

        /* Check if any water rights exist for this application */
        if(vaxrefRepository.existsByApplicationId(applicationId)) {
            return false;
        }

        LOGGER.debug("-- Checking Appl Type 606/634 and Non Filed Water Project");

        if ((foundApp.get().getTypeCode().equalsIgnoreCase("606") ||
             foundApp.get().getTypeCode().equalsIgnoreCase("634")) &&
            (foundApp.get().getNonFiledWaterProject().equalsIgnoreCase("N"))) {
            return false;
        }

        // Get application type data for validation
        Optional<ApplicationType> foundAppType = applicationTypeRepository.findById(foundApp.get().getTypeCode());
        if(!foundAppType.isPresent()) {
            return false;
        }
        
        boolean isAfterFeeFilingDate = eventRepository
                .existsEventsByAppIdAndEventTypeCodeAndEventDateAfter(applicationId, "FRMR", Constants.FILING_FEE_START_DATE);

        LOGGER.debug("-- Checking Fee Status");
        LOGGER.debug(String.format("  Fee Status: (%s)", foundApp.get().getFeeStatus()));
        LOGGER.debug(String.format("  ApplType In: (%s)", foundApp.get().getTypeCode()));
        LOGGER.debug(String.format("  ApplType Out: (%s)", foundAppType.get().getCode()));
        LOGGER.debug(String.format("  Autocomplete: (%s)", foundAppType.get().getAutoCompleteType()));
        LOGGER.debug(String.format("  Appl Rcv'd After Filing Date: (%b)", isAfterFeeFilingDate));

        if ((foundApp.get().getFeeStatus() != null && !foundApp.get().getFeeStatus().equalsIgnoreCase("FULL")) ||
            foundAppType.get().getAutoCompleteType() == null ||
            !isAfterFeeFilingDate) {
            return false;
        }

        // if (foundApp.get().getFeeStatus() != null &&
        //     (foundApp.get().getFeeStatus().equalsIgnoreCase("NONE") || foundApp.get().getFeeStatus().equalsIgnoreCase("PARTIAL")) &&
        //     (atfFeeCount == 1 && atfFeeCountDate == 1)) {
        //     return false;
        // }

        LOGGER.debug(String.format("ApplicationId (%s) is eligible for auto complete", applicationId));
        return true;

    }

}
