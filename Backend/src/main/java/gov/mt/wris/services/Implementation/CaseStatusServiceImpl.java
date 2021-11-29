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

import gov.mt.wris.dtos.CaseStatusDto;
import gov.mt.wris.dtos.CaseStatusPageDto;
import gov.mt.wris.dtos.CaseStatusSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.CaseStatus;
import gov.mt.wris.repositories.CaseStatusRepository;
import gov.mt.wris.services.CaseStatusService;

/**
 * Implementation of CaseStatusService.
 * 
 * @author Cesar.Zamorano
 *
 */
@Service
public class CaseStatusServiceImpl implements CaseStatusService {

	private static Logger LOGGER = LoggerFactory.getLogger(CaseStatusServiceImpl.class);

	@Autowired
	CaseStatusRepository caseRepository;

	@Override
	public Optional<CaseStatusDto> getCaseStatus(String code) {
		LOGGER.info("Getting a specific Case Status");

		Optional<CaseStatus> caseStatus = caseRepository.findById(code);
		CaseStatusDto caseStatusDto = null;
		if (caseStatus.isPresent()) {
			caseStatusDto = getCaseStatusDto(caseStatus.get());
		}
		return Optional.ofNullable(caseStatusDto);
	}

	@Override
	public CaseStatusPageDto getCaseStatuses(int pagenumber, int pagesize, CaseStatusSortColumn sortDTOColumn,
			SortDirection sortDirection, String code, String description) {
		LOGGER.info("Getting a Page of Case Statuses");

		// pagination by default uses 0 to start, shift it so we can use number
		// displayed to users
		Pageable request = PageRequest.of(pagenumber - 1, pagesize);
		// keep the conversion from dto column to entity column in the service layer
		String sortColumn = getEntitySortColumn(sortDTOColumn);
		Page<CaseStatus> resultsPage = caseRepository.getCaseStatuses(request, sortColumn, sortDirection, code,
				description);

		CaseStatusPageDto casePage = new CaseStatusPageDto();

		casePage.setResults(resultsPage.getContent().stream().map(cs -> {
			return getCaseStatusDto(cs);
		}).collect(Collectors.toList()));

		casePage.setCurrentPage(resultsPage.getNumber() + 1);
		casePage.setPageSize(resultsPage.getSize());

		casePage.setTotalPages(resultsPage.getTotalPages());
		casePage.setTotalElements(resultsPage.getTotalElements());

		casePage.setSortColumn(sortDTOColumn);
		casePage.setSortDirection(sortDirection);

		Map<String, String> filters = new HashMap<String, String>();
		if (code != null) {
			filters.put("code", code);
		}
		if (description != null) {
			filters.put("description", description);
		}
		casePage.setFilters(filters);

		return casePage;
	}

	private CaseStatusDto getCaseStatusDto(CaseStatus model) {
		CaseStatusDto dto = new CaseStatusDto();
		dto.setCode(model.getCode());
		dto.setDescription(model.getDescription());
		return dto;
	}

	private CaseStatus getCaseStatus(CaseStatusDto dto) {
		CaseStatus caseStatus = new CaseStatus();
		caseStatus.setCode(dto.getCode());
		caseStatus.setDescription(dto.getDescription());
		return caseStatus;
	}

	@Override
	public CaseStatusDto createCaseStatus(CaseStatusDto caseDto) {
		LOGGER.info("Creating a Case Status");
		// need to check if it already exists, otherwise this will just do a PUT
		Optional<CaseStatusDto> existingCase = getCaseStatus(caseDto.getCode());
		if (existingCase.isPresent()) {
			throw new DataConflictException("A Case Status with this Code already exists");
		}
		CaseStatus caseStatus = caseRepository.save(getCaseStatus(caseDto));
		return getCaseStatusDto(caseStatus);
	}

	@Override
	public void deleteCaseStatus(String code) {
		LOGGER.info("Deleting a Case Status");
		try {
			caseRepository.deleteById(code);
		} catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Case Status with code " + code + " not found");
		} catch (DataIntegrityViolationException ex) {
			throw new DataConflictException("Unable to delete. Record with code "+ code +" is in use.");
		}
	}

	@Override
	public Optional<CaseStatusDto> replaceCaseStatus(CaseStatusDto caseDto, String code) {
		LOGGER.info("Updating a Case Status");
		// Grab old version with id
		Optional<CaseStatus> foundCaseStatus = caseRepository.findById(code);
		CaseStatus newCaseStatus = null;

		if (foundCaseStatus.isPresent()) {
			newCaseStatus = caseRepository.save(getCaseStatus(caseDto));
			return Optional.ofNullable(getCaseStatusDto(newCaseStatus));
		} else {
			// Otherwise, return error
			throw new NotFoundException("The Case Status with code " + code + " was not found");
		}
	}

	private String getEntitySortColumn(CaseStatusSortColumn DTOColumn) {
		if (DTOColumn == CaseStatusSortColumn.CODE)
			return "code";
		if (DTOColumn == CaseStatusSortColumn.DESCRIPTION)
			return "description";
		return "code";
	}

	public CaseStatusDto toUpperCase(CaseStatusDto Dto) {
		CaseStatusDto updateDto = new CaseStatusDto();
		updateDto.setCode(Dto.getCode().toUpperCase());
		updateDto.setDescription(Dto.getDescription().toUpperCase());
		return updateDto;
	}
}
