package gov.mt.wris.services;

import gov.mt.wris.dtos.CountiesResponseDto;
import gov.mt.wris.dtos.WaterSurveyCountiesResponseDto;

/**
 * The service which contains only get operation against Counties table.
 *
 * @author Cesar.Zamorano
 */
public interface CountiesService {

	public CountiesResponseDto getCountiesOfMontana(Boolean all);

	public WaterSurveyCountiesResponseDto getWaterSurveyCounties();

	public CountiesResponseDto getDistrictCourtCounties(Integer districtCourt);

}
