package gov.mt.wris.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.CaseStatusApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.CaseStatusDto;
import gov.mt.wris.dtos.CaseStatusPageDto;
import gov.mt.wris.dtos.CaseStatusSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.services.CaseStatusService;

/**
 * Controller for Case Status REST service.
 * 
 * @author Cesar.Zamorano
 */
@Controller
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CaseStatusController implements CaseStatusApiDelegate {

	@Autowired
	private CaseStatusService caseService;

	private static Logger LOGGER = LoggerFactory.getLogger(CaseStatusController.class);

	@Override
	@PermissionsNeeded(@Permission(verb = Constants.SELECT, table = Constants.CASE_STATUS_TABLE))
	public ResponseEntity<CaseStatusPageDto> getCaseStatuses(Integer pagenumber, Integer pagesize,
			CaseStatusSortColumn sortcolumn, SortDirection sortdirection, String code, String description) {
		LOGGER.info("Getting Case Statuses.");

		if (code != null)
			code = code.toUpperCase();
		if (description != null)
			description = description.toUpperCase();
		CaseStatusPageDto casePageDto = caseService.getCaseStatuses(pagenumber, pagesize, sortcolumn, sortdirection,
				code, description);
		return ResponseEntity.ok(casePageDto);
	}

	@Override
	@PermissionsNeeded(@Permission(verb = Constants.INSERT, table = Constants.CASE_STATUS_TABLE))
	public ResponseEntity<CaseStatusDto> createCaseStatus(CaseStatusDto caseStatusDto)
			throws InvalidDataAccessApiUsageException {
		LOGGER.info("Creating a new Case Status.");

		CaseStatusDto savedCase = caseService.createCaseStatus(caseStatusDto);
		return new ResponseEntity<CaseStatusDto>(savedCase, null, HttpStatus.CREATED);
	}

	@Override
	@PermissionsNeeded(@Permission(verb = Constants.DELETE, table = Constants.CASE_STATUS_TABLE))
	public ResponseEntity<Void> deleteCaseStatus(String code) {
		LOGGER.info("Deleting a Case Status");

		caseService.deleteCaseStatus(code);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@Override
	@PermissionsNeeded(@Permission(verb = Constants.UPDATE, table = Constants.CASE_STATUS_TABLE))
	public ResponseEntity<CaseStatusDto> changeCaseStatus(String code, CaseStatusDto caseStatusDto)
			throws InvalidDataAccessApiUsageException {
		LOGGER.info("Making changes to a Case Status.");

		// Check that we're not changing the code
		if (!code.equals(caseStatusDto.getCode()))
			throw new DataConflictException(
					"Changing the Case Status Code isn't allowed. Delete the Case Status and create a new one");

		Optional<CaseStatusDto> changedCase = caseService.replaceCaseStatus(caseStatusDto, code);
		if (changedCase.isPresent()) {
			if (changedCase.get().getCode().equals(caseStatusDto.getCode())) {
				return ResponseEntity.ok(changedCase.get());
			} else {
				return new ResponseEntity<CaseStatusDto>(changedCase.get(), null, HttpStatus.MOVED_PERMANENTLY);
			}
		} else {
			// this will be changed into an error thrown from the service, so we don't have
			// to worry about the optional business
			return new ResponseEntity<CaseStatusDto>(changedCase.get(), null, HttpStatus.NOT_FOUND);
		}
	}
}