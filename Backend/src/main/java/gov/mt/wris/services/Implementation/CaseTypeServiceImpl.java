package gov.mt.wris.services.Implementation;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.mt.wris.dtos.AllCaseTypesDto;
import gov.mt.wris.dtos.CaseTypeDto;
import gov.mt.wris.dtos.CaseTypePageDto;
import gov.mt.wris.dtos.CaseTypeSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataUsedElsewhereException;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.CaseType;
import gov.mt.wris.repositories.CaseTypeRepository;
import gov.mt.wris.services.CaseTypeService;

@Service
public class CaseTypeServiceImpl implements CaseTypeService{
    private static Logger LOGGER = LoggerFactory.getLogger(CaseTypeServiceImpl.class);

    @Autowired
    CaseTypeRepository caseRepository;

    @Override
    public Optional<CaseTypeDto> getCase(String code){
        LOGGER.info("Getting a specific Case Type");
        Optional<CaseType> caseType = caseRepository.findById(code);
        CaseTypeDto caseTypeDto = null;
        if(caseType.isPresent()) {
            caseTypeDto = getCaseDto(caseType.get());
        } 
        return Optional.ofNullable(caseTypeDto);
    }

    @Override
    public AllCaseTypesDto getAllCaseTypes() {
        LOGGER.info("Getting all the Case Types");
        AllCaseTypesDto allCaseTypes = new AllCaseTypesDto();
        List<CaseTypeDto> caseTypeList = StreamSupport.stream(caseRepository.findAll(Sort.by(Sort.Direction.ASC, "description")).spliterator(), false).map(type -> {
            return getCaseDto(type);
        }).collect(Collectors.toList());

        allCaseTypes.setResults(caseTypeList);
        return allCaseTypes;
    }

    @Override
    public CaseTypePageDto getCaseTypes(int pagenumber, int pagesize, CaseTypeSortColumn sortDTOColumn, SortDirection sortDirection, String Code, String Description, String Program){
        LOGGER.info("Getting a Page of Case Types");
        //pagination by default uses 0 to start, shift it so we can use number displayed to users
        Pageable request = PageRequest.of(pagenumber-1,pagesize);
        //keep the conversion from dto column to entity column in the service layer
        String sortColumn = getEntitySortColumn(sortDTOColumn);
        Page<CaseType> resultsPage = caseRepository.getCaseTypes(request, sortColumn, sortDirection, Code, Description, Program);

        CaseTypePageDto casePage = new CaseTypePageDto();

        casePage.setResults(resultsPage.getContent().stream().map(type -> {
            return getCaseDto(type);
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
        if(Description != null) {
            filters.put("description", Description);
        }
        if(Program != null) {
            filters.put("program", Program);
        }
        casePage.setFilters(filters);

        return casePage;
    }

    private CaseTypeDto getCaseDto(CaseType ModelType) {
        CaseTypeDto dto = new CaseTypeDto();
        dto.setCode(ModelType.getCode());
        dto.setDescription(ModelType.getDescription());
        if (ModelType.getProgram() != null) {
            dto.setProgram(CaseTypeDto.ProgramEnum.fromValue(ModelType.getProgram()));
        } else {
            dto.setProgram(null);
        }
        if(ModelType.getProgramReference() != null) {
            dto.setProgramDescription(ModelType.getProgramReference().getMeaning());
        }
        return dto;
    }
    private CaseType getCase(CaseTypeDto dto) {
        CaseType caseType = new CaseType();
        caseType.setCode(dto.getCode().toUpperCase());
        caseType.setDescription(dto.getDescription().toUpperCase());
        caseType.setProgram(dto.getProgram().getValue());
        return caseType;
    }

    public CaseTypeDto createCase(CaseTypeDto caseDto) {
        LOGGER.info("Creating a Case Type");
        //need to check if it already exists, otherwise this will just do a PUT
        Optional<CaseTypeDto> existingCase = getCase(caseDto.getCode());
        if(existingCase.isPresent()) {
            throw new DataConflictException("A Case Type with this Code already exists");
        }
        CaseType caseType = caseRepository.save(getCase(caseDto));
        return getCaseDto(caseType);
    }

    
    public void deleteCase(String code) {
        LOGGER.info("Deleting a Case Type");
        code = code.toUpperCase();
        if (caseRepository.existsInCourtCasesCount(code) > 0) {
            throw new DataUsedElsewhereException("This Case Type is used in a Water Court Case");
        } else if(caseRepository.existsInEventTypesCount(code) > 0) {
            throw new DataUsedElsewhereException("This Case Type is used in a Case Type Event");
        }
        try {
            caseRepository.deleteById(code);
        } catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Case Type with code " + code + " not found");
		} catch (DataIntegrityViolationException ex) {
			throw new DataConflictException("Unable to delete. Record with code "+ code +" is in use.");
		}
    }

    @Transactional
    public CaseTypeDto replaceCase(CaseTypeDto caseDto, String code){
        LOGGER.info("Updating a Case Type");
        code = code.toUpperCase();
        // Grab old version with id
        Optional<CaseType> foundCaseType = caseRepository.findById(code);
        CaseType newCaseType = null;

        if(!foundCaseType.isPresent()) {
            throw new NotFoundException("The Case Type with code " + code + " was not found");
        }

        // Update version to new dto, including with new id
        if(!code.equals(caseDto.getCode().toUpperCase())) {
            throw new DataConflictException("Changing the Case Type Code isn't allowed. Delete the Case Type and create a new one");
        } else {
            newCaseType = caseRepository.save(getCase(caseDto));
            return getCaseDto(newCaseType);
        }
    }

    private String getEntitySortColumn(CaseTypeSortColumn DTOColumn) {
        if (DTOColumn == CaseTypeSortColumn.CODE) return "code";
        if (DTOColumn == CaseTypeSortColumn.PROGRAMDESCRIPTION) return "meaning";
        return "description";
    }

    public CaseTypeDto toUpperCase(CaseTypeDto Dto) {
        CaseTypeDto updateDto = new CaseTypeDto();
        updateDto.setCode(Dto.getCode().toUpperCase());
        updateDto.setDescription(Dto.getDescription().toUpperCase());
        updateDto.setProgram(Dto.getProgram());
        return updateDto;
    }
}
