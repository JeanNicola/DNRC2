package gov.mt.wris.services.Implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

import gov.mt.wris.dtos.CaseAssignmentTypeDto;
import gov.mt.wris.dtos.CaseAssignmentTypePageDto;
import gov.mt.wris.dtos.SortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.CaseAssignmentType;
import gov.mt.wris.repositories.CaseAssignmentTypeRepository;
import gov.mt.wris.services.CaseAssignmentTypeService;


@Service
public class CaseAssignmentTypeServiceImpl implements CaseAssignmentTypeService{
    private static Logger LOGGER = LoggerFactory.getLogger(CaseAssignmentTypeServiceImpl.class);

    @Autowired
    CaseAssignmentTypeRepository caseRepository;

    @Override
    public Optional<CaseAssignmentTypeDto> getCase(String code){
        LOGGER.info("Getting a specific Case Assignment Type");
        Optional<CaseAssignmentType> caseType = caseRepository.findById(code);
        CaseAssignmentTypeDto caseTypeDto = null;
        if(caseType.isPresent()) {
            caseTypeDto = getCaseAssignmentDto(caseType.get());
        } 
        return Optional.ofNullable(caseTypeDto);
    }

    @Override
    public CaseAssignmentTypePageDto getCaseAssignmentTypes(int pagenumber, int pagesize, SortColumn sortDTOColumn, SortDirection sortDirection, String Code, String AssignmentType, String Program){
        LOGGER.info("Getting a Page of Case Assignment Types");
        //pagination by default uses 0 to start, shift it so we can use number displayed to users
        Pageable request = PageRequest.of(pagenumber-1,pagesize);
        //keep the conversion from dto column to entity column in the service layer
        String sortColumn = getEntitySortColumn(sortDTOColumn);
        Page<CaseAssignmentType> resultsPage = caseRepository.getCaseAssignmentTypes(request, sortColumn, sortDirection, Code, AssignmentType, Program);

        CaseAssignmentTypePageDto casePage = new CaseAssignmentTypePageDto();

        casePage.setResults(resultsPage.getContent().stream().map(type -> {
            return getCaseAssignmentDto(type);
        }).collect(Collectors.toList()));

        casePage.setCurrentPage(resultsPage.getNumber()+1);
        casePage.setPageSize(resultsPage.getSize());

        casePage.setTotalPages(resultsPage.getTotalPages());
        casePage.setTotalElements(resultsPage.getTotalElements());

        casePage.setSortColumn(sortDTOColumn);
        casePage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(Code != null) {
            filters.put("code", Code);
        }
        if(AssignmentType != null) {
            filters.put("assignmentType", AssignmentType);
        }
        if(Program != null) {
            filters.put("program", Program);
        }
        casePage.setFilters(filters);

        return casePage;
    }

    private CaseAssignmentTypeDto getCaseAssignmentDto(CaseAssignmentType ModelType) {
        CaseAssignmentTypeDto dto = new CaseAssignmentTypeDto();
        dto.setCode(ModelType.getCode());
        dto.setAssignmentType(ModelType.getDescription());
        dto.setProgram(CaseAssignmentTypeDto.ProgramEnum.fromValue(ModelType.getProgram()));
        if(ModelType.getProgramReference() != null) {
            dto.setProgramDescription(ModelType.getProgramReference().getMeaning());
        }
        return dto;
    }
    private CaseAssignmentType getCaseAssignment(CaseAssignmentTypeDto dto) {
        CaseAssignmentType caseAssignmentType = new CaseAssignmentType();
        caseAssignmentType.setCode(dto.getCode());
        caseAssignmentType.setDescription(dto.getAssignmentType());
        caseAssignmentType.setProgram(dto.getProgram().getValue());
        return caseAssignmentType;
    }

    public CaseAssignmentTypeDto createCase(CaseAssignmentTypeDto caseDto) {
        LOGGER.info("Updating a Case Assignment Type");
        //need to check if it already exists, otherwise this will just do a PUT
        Optional<CaseAssignmentTypeDto> existingCase = getCase(caseDto.getCode());
        if(existingCase.isPresent()) {
            throw new DataConflictException("A Case Assignment Type with this Code already exists");
        }
        CaseAssignmentType caseType = caseRepository.save(getCaseAssignment(caseDto));
        return getCaseAssignmentDto(caseType);
    }

    
    public void deleteCase(String code) {
        LOGGER.info("Deleting a Case Assignment Type");
        try {
            caseRepository.deleteById(code);
        } catch(EmptyResultDataAccessException e) {
            throw new NotFoundException("Case Assignment Type with code " + code + " not found");
		} catch (DataIntegrityViolationException ex) {
			throw new DataConflictException("Unable to delete. Record with code "+ code +" is in use.");
		}
    }

    @Transactional
    public CaseAssignmentTypeDto replaceCase(CaseAssignmentTypeDto caseDto, String code){
        LOGGER.info("Updating a Case Assignment Type");
        // Grab old version with id
        Optional<CaseAssignmentType> foundCaseType = caseRepository.findById(code);
        CaseAssignmentType newCaseType = null;

        if(!foundCaseType.isPresent()) {
            throw new NotFoundException("The Case Assignment Type with code " + code + " was not found");
        }

        // Update version to new dto, including with new id
        if(!code.equals(caseDto.getCode())) {
            throw new DataConflictException("Changing the Case Assignment Type Code isn't allowed. Delete the Event Type and create a new one");
        } else {
            newCaseType = caseRepository.save(getCaseAssignment(caseDto));
            return getCaseAssignmentDto(newCaseType);
        }
    }

    private String getEntitySortColumn(SortColumn DTOColumn) {
        if (DTOColumn == SortColumn.CODE) return "code";
        if (DTOColumn == SortColumn.PROGRAMDESCRIPTION) return "meaning";
        return "description";
    }

    public CaseAssignmentTypeDto toUpperCase(CaseAssignmentTypeDto Dto) {
        CaseAssignmentTypeDto updateDto = new CaseAssignmentTypeDto();
        updateDto.setCode(Dto.getCode().toUpperCase());
        updateDto.setAssignmentType(Dto.getAssignmentType().toUpperCase());
        updateDto.setProgram(Dto.getProgram());
        return updateDto;
    }
}
