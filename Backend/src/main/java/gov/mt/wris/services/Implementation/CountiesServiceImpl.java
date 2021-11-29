package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.CountiesResponseDto;
import gov.mt.wris.dtos.CountyDto;
import gov.mt.wris.dtos.WaterSurveyCountiesResponseDto;
import gov.mt.wris.dtos.WaterSurveyCountyDto;
import gov.mt.wris.models.County;
import gov.mt.wris.models.WaterResourceSurvey;
import gov.mt.wris.repositories.CountiesRepository;
import gov.mt.wris.repositories.WaterResourceSurveyRepository;
import gov.mt.wris.services.CountiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CountiesService
 * 
 * @author Cesar.Zamorano
 *
 */
@Service
public class CountiesServiceImpl implements CountiesService {
	private static Logger LOGGER = LoggerFactory.getLogger(CountiesServiceImpl.class);

	@Autowired
	private CountiesRepository countiesRepository;

	@Autowired
	private WaterResourceSurveyRepository waterResourceSurveyRepository;

	public CountiesResponseDto getCountiesOfMontana(Boolean all) {
		LOGGER.info("Getting all Montana Counties");
		// keep the conversion from dto column to entity column in the service layer
		CountiesResponseDto allMontanaCounties = new CountiesResponseDto();

		List<County> results;

		if(all) {
			results = countiesRepository.findByOrderByName();
		} else {
			results = countiesRepository.getCountiesOfMontana();
		}

		allMontanaCounties.setResults(results.stream().map(county -> {
			return getCountyDto(county);
		}).collect(Collectors.toList()));

		return allMontanaCounties;
	}

	private CountyDto getCountyDto(County model) {
		CountyDto dto = new CountyDto();
		dto.setFipsCode(model.getFipsCode());
		dto.setId(model.getId());
		dto.setName(model.getName());
		dto.setStateCountyNumber(model.getStateCountyNumber());
		dto.setStateCode(model.getStateCode());
		return dto;
	}

	private WaterSurveyCountyDto getWaterSurveyCountyDto(WaterResourceSurvey model) {
		WaterSurveyCountyDto dto = new WaterSurveyCountyDto();
		dto.setSurveyId(model.getSurveyId().longValue());
		dto.setId(model.getCounty().getId().longValue());
		dto.setName(model.getCounty().getName());
		dto.setYr(model.getYr());
		dto.setStateCode(model.getCounty().getStateCode());
		return dto;
	}

	public WaterSurveyCountiesResponseDto getWaterSurveyCounties() {

		LOGGER.info("Getting all Water Survey Counties");

		WaterSurveyCountiesResponseDto allCounties = new WaterSurveyCountiesResponseDto();
		List<WaterResourceSurvey> results = waterResourceSurveyRepository.getAllWaterSurveyCounties();

		allCounties.setResults(results.stream().map(county -> {
			return getWaterSurveyCountyDto(county);
		}).collect(Collectors.toList()));

		return allCounties;
	}

	public CountiesResponseDto getDistrictCourtCounties(Integer districtCourt) {


		LOGGER.info("Get list of Counties for District Court");

		CountiesResponseDto responseDto = new CountiesResponseDto();
		List<County> results = countiesRepository.getCountiesByDistrictCourtOrderByName(districtCourt);
		responseDto.setResults(results.stream().map(county -> {
			return getCountyDto(county);
		}).collect(Collectors.toList()));
		return responseDto;

	}

}
