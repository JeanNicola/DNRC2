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
import gov.mt.wris.models.CaseTypeXref;
import gov.mt.wris.models.IdClasses.CaseTypeXrefId;
import gov.mt.wris.repositories.CaseTypeRepository;
import gov.mt.wris.repositories.CaseTypeXrefRepository;
import gov.mt.wris.repositories.EventTypeRepository;
import gov.mt.wris.services.CaseTypeXrefService;

@Service
public class CaseTypeXrefServiceImpl implements CaseTypeXrefService{
    private static Logger LOGGER = LoggerFactory.getLogger(CaseTypeServiceImpl.class);
    @Autowired
    CaseTypeXrefRepository caseXrefRepository;

    @Autowired
    EventTypeRepository eventRepository;

    @Autowired
    CaseTypeRepository caseRepository;

    // wrapper for repository and idclass
    private boolean existsById(String eventCode, String caseCode) {
        CaseTypeXrefId caseId = new CaseTypeXrefId(eventCode, caseCode);
        return caseXrefRepository.existsById(caseId);
    }

    @Override
    public TypeXrefDto addCaseType(String eventCode, TypeXrefDto typeDto) {
        LOGGER.info("Added a Case Type");
        if(existsById(eventCode, typeDto.getCode())) {
            throw new DataConflictException("This Case Type already exists for the Event Type");
        }
        //make sure both sides exist
        if(!eventRepository.existsById(eventCode)) throw new NotFoundException("The Event Type doesn't exist");
        if(!caseRepository.existsById(typeDto.getCode())) throw new NotFoundException("The Case Type doesn't exist");

        CaseTypeXref caseTypeXref = caseXrefRepository.save(getCaseXref(eventCode,typeDto));
        return getCaseXrefDto(caseTypeXref);
    }

    @Override
    public void removeCaseType(String eventCode, String code) {
        LOGGER.info("Removing a Case Type from an Event Type");
        CaseTypeXrefId caseId = new CaseTypeXrefId(eventCode, code);
        try {
        	caseXrefRepository.deleteById(caseId);
        } catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Case Type with code " + code + " not found");
		} catch (DataIntegrityViolationException ex) {
			throw new DataConflictException("Unable to delete. Record with code "+ code +" is in use.");
		}
    }

    private CaseTypeXref getCaseXref(String eventCode, TypeXrefDto typeDto) {
        CaseTypeXref caseXref = new CaseTypeXref();
        caseXref.setEventCode(eventCode);
        caseXref.setCaseCode(typeDto.getCode());
        return caseXref;
    }

    private TypeXrefDto getCaseXrefDto(CaseTypeXref caseXref) {
        return new TypeXrefDto().code(caseXref.getCaseCode());
    }

    public TypeXrefDto toUpperCase(TypeXrefDto typeDto) {
        typeDto.setCode(typeDto.getCode().toUpperCase());
        return typeDto;
    }
}