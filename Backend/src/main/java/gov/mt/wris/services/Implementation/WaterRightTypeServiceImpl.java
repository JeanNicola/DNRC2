package gov.mt.wris.services.Implementation;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.ReferenceDto;
import gov.mt.wris.models.WaterRightType;
import gov.mt.wris.repositories.WaterRightTypeRepository;
import gov.mt.wris.services.WaterRightTypeService;

@Service
public class WaterRightTypeServiceImpl implements WaterRightTypeService {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightTypeService.class);

    @Autowired
    private WaterRightTypeRepository typeRepo;

    public AllReferencesDto getWaterRightCreationTypes() {
        LOGGER.info("Getting the Water Right Creation Types");

        AllReferencesDto allDto = new AllReferencesDto();

        allDto.setResults(typeRepo.findInList(Constants.WATER_RIGHT_ALLOWED_CREATION_TYPE).stream().map(type -> {
            return getWaterRightTypeDto(type);
        }).collect(Collectors.toList()));

        return allDto;
    }

    private ReferenceDto getWaterRightTypeDto(WaterRightType type) {
        ReferenceDto dto = new ReferenceDto();
        dto.setDescription(type.getDescription());
        dto.setValue(type.getCode());
        return dto;
    }

    public AllReferencesDto getWaterRightTypes() {
        LOGGER.info("Getting all the water right types");

        AllReferencesDto allDto = new AllReferencesDto();

        allDto.setResults(typeRepo.findAllByOrderByDescriptionAsc().stream().map(type -> {
            return getWaterRightTypeDto(type);
        }).collect(Collectors.toList()));

        return allDto;
    }
}
