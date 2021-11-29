package gov.mt.wris.services.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.TypeXrefDto;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.ApplicationTypeXref;
import gov.mt.wris.models.IdClasses.ApplicationTypeXrefId;
import gov.mt.wris.repositories.ApplicationTypeRepository;
import gov.mt.wris.repositories.ApplicationTypeXrefRepository;
import gov.mt.wris.repositories.EventTypeRepository;
import gov.mt.wris.services.ApplicationTypeXrefService;

@Service
public class ApplicationTypeXrefServiceImpl implements ApplicationTypeXrefService{
    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationTypeXrefServiceImpl.class);
    @Autowired
    ApplicationTypeXrefRepository applicationXrefRepository;

    @Autowired
    EventTypeRepository eventRepository;

    @Autowired
    ApplicationTypeRepository applicationRepository;

    // wrapper for repository and idclass
    private boolean existsById(String eventCode, String applicationCode) {
        ApplicationTypeXrefId applicationId = new ApplicationTypeXrefId(eventCode, applicationCode);
        return applicationXrefRepository.existsById(applicationId);
    }

    @Override
    public TypeXrefDto addApplicationType(String eventCode, TypeXrefDto typeDto) {
        LOGGER.info("Added an Application Type");
        if(existsById(eventCode, typeDto.getCode())) {
            throw new DataConflictException("This Application Type already exists for the Event Type");
        }
        //make sure both sides exist
        if(!eventRepository.existsById(eventCode)) throw new NotFoundException("The Event Type doesn't exist");
        if(!applicationRepository.existsById(typeDto.getCode())) throw new NotFoundException("The Application Type doesn't exist");

        ApplicationTypeXref applicationTypeXref = applicationXrefRepository.save(getApplicationXref(eventCode,typeDto));
        return getApplicationXrefDto(applicationTypeXref);
    }

    @Override
    public void removeApplicationType(String eventCode, String code) {
        LOGGER.info("Removing an Application Type from an Event Type");
        ApplicationTypeXrefId applicationId = new ApplicationTypeXrefId(eventCode, code);
        try {
        	applicationXrefRepository.deleteById(applicationId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Application Type with code " + code + " not found");
		} catch (DataIntegrityViolationException ex) {
			throw new DataConflictException("Unable to delete. Record with code "+ code +" is in use.");
		}
    }

    private ApplicationTypeXref getApplicationXref(String eventCode, TypeXrefDto typeDto) {
        ApplicationTypeXref applicationXref = new ApplicationTypeXref();
        applicationXref.setEventCode(eventCode);
        applicationXref.setApplicationCode(typeDto.getCode());
        return applicationXref;
    }

    private TypeXrefDto getApplicationXrefDto(ApplicationTypeXref applicationXref) {
        return new TypeXrefDto().code(applicationXref.getApplicationCode());
    }

    public TypeXrefDto toUpperCase(TypeXrefDto typeDto) {
        typeDto.setCode(typeDto.getCode().toUpperCase());
        return typeDto;
    }
}