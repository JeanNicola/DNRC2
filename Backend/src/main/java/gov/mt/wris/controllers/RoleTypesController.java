package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.RoleTypesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.RoleTypesResponseDto;
import gov.mt.wris.services.RoleTypesService;

/**
 * Controller for RoleTypes REST service.
 *
 * @author Cesar.Zamorano
 */
@Controller
public class RoleTypesController implements RoleTypesApiDelegate {

	private static Logger LOGGER = LoggerFactory.getLogger(RoleTypesController.class);

	@Autowired
	private RoleTypesService service;

	@Override
	@PermissionsNeeded(@Permission(verb = Constants.SELECT, table = Constants.ROLE_TYPES_TABLE))
	public ResponseEntity<RoleTypesResponseDto> getRoleTypes(){
		LOGGER.info("Getting all Role Types");
		return ResponseEntity.ok(service.getRoleTypes());
	}
}
