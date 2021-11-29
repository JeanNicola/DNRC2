package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubdivisionCodeDto;
import gov.mt.wris.dtos.SubdivisionCodePageDto;
import gov.mt.wris.dtos.SubdivisionCodesSortColumn;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.County;
import gov.mt.wris.models.SubdivisionCode;
import gov.mt.wris.repositories.CountiesRepository;
import gov.mt.wris.repositories.SubdivisionCodesRepository;
import gov.mt.wris.services.SubdivisionCodesService;

/**
 * Implementation of SubdivisionCodesService
 * 
 * @author Cesar.Zamorano
 *
 */
@Service
public class SubdivisionCodesServiceImpl implements SubdivisionCodesService {
	private static Logger LOGGER = LoggerFactory.getLogger(SubdivisionCodesServiceImpl.class);

	@Autowired
	SubdivisionCodesRepository subCodesRepo;

	@Autowired
	CountiesRepository countiesRepo;

	@Override
	public Optional<SubdivisionCodeDto> getSubdivisionCode(String code) {
		LOGGER.info("Getting a specific Subdivision Code");

		Optional<SubdivisionCode> subCode = subCodesRepo.findById(code);
		SubdivisionCodeDto subCodeDTO = null;
		if (subCode.isPresent()) {
			subCodeDTO = getSubdivisionCodeDto(subCode.get());
		}
		return Optional.ofNullable(subCodeDTO);
	}

	@Override
	public SubdivisionCodePageDto getSubdivisionCodes(int pagenumber, int pagesize,
			SubdivisionCodesSortColumn sortColumn, SortDirection sortDirection, String code, BigDecimal countyId,
			String countyName, String dnrcName, String dorName) {
		LOGGER.info("Getting a Page of Subdivision Codes");

		// pagination by default uses 0 to start, shift it so we can use number
		// displayed to users
		Pageable request = PageRequest.of(pagenumber - 1, pagesize);
		// keep the conversion from dto column to entity column in the service layer
		String sortDtoColumn = getEntitySortColumn(sortColumn);
		Page<SubdivisionCode> resultsPage = subCodesRepo.getSubdivisionCodes(request, sortDtoColumn, sortDirection,
				code, countyId, countyName, dnrcName, dorName);

		SubdivisionCodePageDto page = new SubdivisionCodePageDto();

		page.setResults(resultsPage.getContent().stream().map(sbcd -> {
			return getSubdivisionCodeDto(sbcd);
		}).collect(Collectors.toList()));

		page.setCurrentPage(resultsPage.getNumber() + 1);
		page.setPageSize(resultsPage.getSize());

		page.setTotalPages(resultsPage.getTotalPages());
		page.setTotalElements(resultsPage.getTotalElements());

		page.setSortColumn(sortColumn);
		page.setSortDirection(sortDirection);

		Map<String, String> filters = new HashMap<String, String>();
		if (code != null) {
			filters.put("code", code);
		}
		if (countyName != null) {
			filters.put("countyName", countyName);
		}
		if (countyId != null) {
			filters.put("countyId", countyId.toString());
		}
		if (dnrcName != null) {
			filters.put("dnrcName", dnrcName);
		}
		if (dorName != null) {
			filters.put("dorName", dorName);
		}
		page.setFilters(filters);

		return page;
	}

	@Override
	public SubdivisionCodeDto createSubdivisionCode(SubdivisionCodeDto subdivisionCodeDTO) {
		LOGGER.info("Creating a Subdivision Code");

		// need to check if it already exists, otherwise this will just do a PUT
		Optional<SubdivisionCodeDto> existingCode = getSubdivisionCode(subdivisionCodeDTO.getCode());
		if (existingCode.isPresent()) {
			throw new DataConflictException("A Subdivision Code with Code already exists");
		}
		// check if StateCountyNumber in Counties Table is the same as the two first
		// characters in Code
		String stateCountyNumber = subdivisionCodeDTO.getCode().substring(0, 2);
		Optional<County> existingCounty = countiesRepo.findById(subdivisionCodeDTO.getCountyId());
		if (existingCounty.isPresent() && !stateCountyNumber.equals(existingCounty.get().getStateCountyNumber())) {
			throw new ValidationException(
					"Subdivision Code first two characters must be the State County Number");
		}
		SubdivisionCode code = subCodesRepo.save(getSubdivisionCodeEntity(subdivisionCodeDTO));
		return getSubdivisionCodeDto(code);
	}

	@Override
	public void deleteSubdivisionCode(String code) {
		LOGGER.info("Deleting a Subdivision Code");
		try {
			subCodesRepo.deleteById(code);
		} catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Subdivision Code with code " + code + " not found");
		} catch (DataIntegrityViolationException ex) {
			throw new DataConflictException("Unable to delete. Record with code "+ code +" is in use.");
		}
	}

	@Override
	public Optional<SubdivisionCodeDto> replaceSubdivisionCode(SubdivisionCodeDto subCodeDTO, String code) {
		LOGGER.info("Updating a Subdivision Code");

		// Grab old version with id
		Optional<SubdivisionCode> foundSubdivisionCode = subCodesRepo.findById(code);
		SubdivisionCode newSubCode = null;

		if (foundSubdivisionCode.isPresent()) {
			newSubCode = subCodesRepo.save(getSubdivisionCodeEntity(subCodeDTO));
			return Optional.ofNullable(getSubdivisionCodeDto(newSubCode));
		} else {
			// Otherwise, return error
			throw new NotFoundException("The Subdivision Code with code " + code + " was not found");
		}
	}

	private SubdivisionCodeDto getSubdivisionCodeDto(SubdivisionCode model) {
		SubdivisionCodeDto dto = new SubdivisionCodeDto();
		dto.setCode(model.getCode());
		dto.setCountyId(model.getCountyId());
		dto.setCountyName((model.getCounty() != null) ? model.getCounty().getName() : null);
		dto.setDnrcName(model.getDnrcName());
		dto.setDorName(model.getDorName());
		return dto;
	}

	private SubdivisionCode getSubdivisionCodeEntity(SubdivisionCodeDto dto) {
		SubdivisionCode subCode = new SubdivisionCode();
		subCode.setCode(dto.getCode());
		subCode.setCountyId(dto.getCountyId());
		subCode.setDnrcName(dto.getDnrcName());
		subCode.setDorName(dto.getDorName());
		return subCode;
	}

	private String getEntitySortColumn(SubdivisionCodesSortColumn DTOColumn) {
		if (DTOColumn == SubdivisionCodesSortColumn.CODE)
			return "code";
		if (DTOColumn == SubdivisionCodesSortColumn.COUNTYNAME)
			return "name";
		if (DTOColumn == SubdivisionCodesSortColumn.COUNTYID)
			return "countyId";
		if (DTOColumn == SubdivisionCodesSortColumn.DNRCNAME)
			return "dnrcName";
		if (DTOColumn == SubdivisionCodesSortColumn.DORNAME)
			return "dorName";
		return "code";
	}

	@Override
	public SubdivisionCodeDto toUpperCase(SubdivisionCodeDto subCodeDto) {
		SubdivisionCodeDto updatedDto = new SubdivisionCodeDto();
		updatedDto.setCode(subCodeDto.getCode().toUpperCase());
		updatedDto.setDnrcName(subCodeDto.getDnrcName().toUpperCase());
		updatedDto.setDorName(subCodeDto.getDorName() != null ? subCodeDto.getDorName().toUpperCase() : null);
		updatedDto.setCountyName(subCodeDto.getCountyName() != null ? subCodeDto.getCountyName().toUpperCase() : null);
		updatedDto.setCountyId(subCodeDto.getCountyId());
		return updatedDto;
	}

}
