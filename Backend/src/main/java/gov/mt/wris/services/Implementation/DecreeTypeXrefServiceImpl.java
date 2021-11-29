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
import gov.mt.wris.models.DecreeTypeXref;
import gov.mt.wris.models.IdClasses.DecreeTypeXrefId;
import gov.mt.wris.repositories.DecreeTypeRepository;
import gov.mt.wris.repositories.DecreeTypeXrefRepository;
import gov.mt.wris.repositories.EventTypeRepository;
import gov.mt.wris.services.DecreeTypeXrefService;

@Service
public class DecreeTypeXrefServiceImpl implements DecreeTypeXrefService{
    private static Logger LOGGER = LoggerFactory.getLogger(DecreeTypeXrefServiceImpl.class);
    @Autowired
    DecreeTypeXrefRepository decreeXrefRepository;

    @Autowired
    EventTypeRepository eventRepository;

    @Autowired
    DecreeTypeRepository decreeRepository;

    // wrapper for repository and idclass
    private boolean existsById(String eventCode, String decreeCode) {
        DecreeTypeXrefId caseId = new DecreeTypeXrefId(eventCode, decreeCode);
        return decreeXrefRepository.existsById(caseId);
    }

    @Override
    public TypeXrefDto addDecreeType(String eventCode, TypeXrefDto typeDto) {
        LOGGER.info("Added a Decree Type");
        if(existsById(eventCode, typeDto.getCode())) {
            throw new DataConflictException("This Decree Type already exists for the Event Type");
        }
        //make sure both sides exist
        if(!eventRepository.existsById(eventCode)) throw new NotFoundException("The Event Type doesn't exist");
        if(!decreeRepository.existsById(typeDto.getCode())) throw new NotFoundException("The Decree Type doesn't exist");

        DecreeTypeXref decreeTypeXref = decreeXrefRepository.save(getDecreeXref(eventCode,typeDto));
        return getDecreeXrefDto(decreeTypeXref);
    }

    @Override
    public void removeDecreeType(String eventCode, String code) {
        LOGGER.info("Removing a Decree Type from an Event Type");
        DecreeTypeXrefId decreeId = new DecreeTypeXrefId(eventCode, code);
        try {
        	decreeXrefRepository.deleteById(decreeId);
        } catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Decree Type with code " + code + " not found");
		} catch (DataIntegrityViolationException ex) {
			throw new DataConflictException("Unable to delete. Record with code "+ code +" is in use.");
		}
    }

    private DecreeTypeXref getDecreeXref(String eventCode, TypeXrefDto typeDto) {
        DecreeTypeXref decreeXref = new DecreeTypeXref();
        decreeXref.setEventCode(eventCode);
        decreeXref.setDecreeCode(typeDto.getCode());
        return decreeXref;
    }

    private TypeXrefDto getDecreeXrefDto(DecreeTypeXref decreeXref) {
        return new TypeXrefDto().code(decreeXref.getDecreeCode());
    }

    public TypeXrefDto toUpperCase(TypeXrefDto typeDto) {
        typeDto.setCode(typeDto.getCode().toUpperCase());
        return typeDto;
    }
}