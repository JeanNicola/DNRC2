package gov.mt.wris.services.Implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.RoleTypeDto;
import gov.mt.wris.dtos.RoleTypesResponseDto;
import gov.mt.wris.models.RoleType;
import gov.mt.wris.repositories.RoleTypesRepository;
import gov.mt.wris.services.RoleTypesService;

/**
 * Implementation of RoleTypesService
 * 
 * @author Cesar.Zamorano
 *
 */
@Service
public class RoleTypesServiceImpl implements RoleTypesService {
	private static Logger LOGGER = LoggerFactory.getLogger(RoleTypesServiceImpl.class);

	@Autowired
	RoleTypesRepository repository;

	@Override
	public RoleTypesResponseDto getRoleTypes() {
		LOGGER.info("Getting all Role Types");

		// keep the conversion from dto column to entity column in the service layer
		RoleTypesResponseDto roleTypes = new RoleTypesResponseDto();
		List<RoleType> results = repository.getRoleTypes();

		roleTypes.setResults(results.stream().map(county -> {
			return getRoleTypeDto(county);
		}).collect(Collectors.toList()));

		return roleTypes;
	}

	private RoleTypeDto getRoleTypeDto(RoleType model) {
		RoleTypeDto dto = new RoleTypeDto();
		dto.setCode(model.getCode());
		dto.setDescription(model.getDescription());
		return dto;
	}
}
