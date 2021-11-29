package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.CountiesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.CountiesResponseDto;
import gov.mt.wris.dtos.WaterSurveyCountiesResponseDto;
import gov.mt.wris.services.CountiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * Controller for Counties REST service.
 *
 * @author Cesar.Zamorano
 */
@Controller
public class CountiesController implements CountiesApiDelegate {

	private static Logger LOGGER = LoggerFactory.getLogger(CountiesController.class);

	@Autowired
	private CountiesService service;

	@PermissionsNeeded(@Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE))
	public ResponseEntity<CountiesResponseDto> getCounties(Boolean all) {
		LOGGER.info("Getting all Counties");
		return ResponseEntity.ok(service.getCountiesOfMontana(all));
	}

	@PermissionsNeeded({
			@Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE),
			@Permission(verb = Constants.SELECT, table = Constants.WATER_RESOURCE_SURVEYS_TABLE)
	})
	public ResponseEntity<WaterSurveyCountiesResponseDto> getWaterSurveyCounties() {
		LOGGER.info("Get all Water Survey Counties");
		return ResponseEntity.ok(service.getWaterSurveyCounties());
	}

	@PermissionsNeeded(@Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE))
	public ResponseEntity<CountiesResponseDto> getDistrictCourtCounties(Integer districtCourt) {

		LOGGER.info("Get list of Counties for District Court");
		return ResponseEntity.ok(service.getDistrictCourtCounties(districtCourt));

	}
}
