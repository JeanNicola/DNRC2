package gov.mt.wris.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.SubdivisionCodesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubdivisionCodeDto;
import gov.mt.wris.dtos.SubdivisionCodePageDto;
import gov.mt.wris.dtos.SubdivisionCodesSortColumn;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.services.SubdivisionCodesService;

/**
 * Controller for SubdivisionCodes REST service.
 *
 * @author Cesar.Zamorano
 */
@Controller
public class SubdivisionCodesController implements SubdivisionCodesApiDelegate {

	private static Logger LOGGER = LoggerFactory.getLogger(SubdivisionCodesController.class);

	@Autowired
	private SubdivisionCodesService subCodeService;

	@Override
	@PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.SUBDIVISION_CODES_TABLE),
			@Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE) })
	public ResponseEntity<SubdivisionCodePageDto> getSubdivisionCodes(Integer pageNumber, Integer pageSize,
			SubdivisionCodesSortColumn sortColumn, SortDirection sortDirection, String code, BigDecimal countyId,
			String countyName, String dnrcName, String dorName) {
		LOGGER.info("Getting a Page of Subdivision Codes");

		if (code != null)
			code = code.toUpperCase();
		if (countyName != null)
			countyName = countyName.toUpperCase();
		if (dnrcName != null)
			dnrcName = dnrcName.toUpperCase();
		if (dorName != null)
			dorName = dorName.toUpperCase();
		SubdivisionCodePageDto casePageDto = subCodeService.getSubdivisionCodes(pageNumber, pageSize, sortColumn,
				sortDirection, code, countyId, countyName, dnrcName, dorName);
		return ResponseEntity.ok(casePageDto);
	}

	@Override
	@PermissionsNeeded(@Permission(verb = Constants.INSERT, table = Constants.SUBDIVISION_CODES_TABLE))
	public ResponseEntity<SubdivisionCodeDto> createSubdivisionCode(SubdivisionCodeDto subCodeDto)
			throws HttpMessageNotReadableException {
		LOGGER.info("Creating a new Subdivision Code.");

		// convert everything to upper case
		subCodeDto = subCodeService.toUpperCase(subCodeDto);

		SubdivisionCodeDto savedCode = subCodeService.createSubdivisionCode(subCodeDto);
		return new ResponseEntity<SubdivisionCodeDto>(savedCode, null, HttpStatus.CREATED);
	}

	@Override
	@PermissionsNeeded(@Permission(verb = Constants.DELETE, table = Constants.SUBDIVISION_CODES_TABLE))
	public ResponseEntity<Void> deleteSubdivisionCode(String code) {
		LOGGER.info("Deleting a Subdivision Code.");

		// convert everything to upper case
		code = code.toUpperCase();
		subCodeService.deleteSubdivisionCode(code);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	@PermissionsNeeded(@Permission(verb = Constants.UPDATE, table = Constants.SUBDIVISION_CODES_TABLE))
	public ResponseEntity<SubdivisionCodeDto> changeSubdivisionCode(String code, SubdivisionCodeDto subCodeDto)
			throws HttpMessageNotReadableException {
		LOGGER.info("Modifying a Subdivision Code.");

		// convert everything to upper case
		subCodeDto = subCodeService.toUpperCase(subCodeDto);
		code = code.toUpperCase();

		// Validation of code
		if (!code.equals(subCodeDto.getCode())) {
			throw new DataConflictException(
					"Changing the SubdivisionCode Code isn't allowed. Delete the SubdivisionCode and create a new one.");
		}

		Optional<SubdivisionCodeDto> subCode = subCodeService.replaceSubdivisionCode(subCodeDto, code);
		if (subCode.isPresent()) {
			if (subCode.get().getCode().equals(subCodeDto.getCode())) {
				return ResponseEntity.ok(subCode.get());
			} else {
				return new ResponseEntity<SubdivisionCodeDto>(subCode.get(), null, HttpStatus.MOVED_PERMANENTLY);
			}
		} else {
			// this will be changed into an error thrown from the service, so we don't have
			// to worry about the optional business
			return new ResponseEntity<SubdivisionCodeDto>(subCode.get(), null, HttpStatus.NOT_FOUND);
		}
	}
}
