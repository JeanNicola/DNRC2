package gov.mt.wris.services;

import gov.mt.wris.dtos.RoleTypesResponseDto;

/**
 * The service which contains only get operation against Role Types table.
 *
 * @author Cesar.Zamorano
 */
public interface RoleTypesService {
	RoleTypesResponseDto getRoleTypes();
}
