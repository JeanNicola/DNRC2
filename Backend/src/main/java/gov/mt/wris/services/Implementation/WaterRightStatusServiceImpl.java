package gov.mt.wris.services.Implementation;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllWaterRightStatusesDto;
import gov.mt.wris.dtos.WaterRightStatusDto;
import gov.mt.wris.models.WaterRightStatus;
import gov.mt.wris.repositories.WaterRightStatusRepository;
import gov.mt.wris.services.WaterRightStatusService;

@Service
public class WaterRightStatusServiceImpl implements WaterRightStatusService {
    public static Logger LOGGER = LoggerFactory.getLogger(WaterRightStatusService.class);

    @Autowired
    WaterRightStatusRepository statusRepository;

    public AllWaterRightStatusesDto getWaterRightStatuses(String waterRightTypeCode) {
        LOGGER.info("Getting the list of Water Right Statuses");

        AllWaterRightStatusesDto allDto = new AllWaterRightStatusesDto();

        allDto.setResults(statusRepository.findByType(waterRightTypeCode).stream().map(status -> {
            return getWaterRightStatusDto(status);
        }).collect(Collectors.toList()));

        return allDto;
    }

    private WaterRightStatusDto getWaterRightStatusDto(WaterRightStatus status) {
        WaterRightStatusDto dto = new WaterRightStatusDto();
        dto.setDescription(status.getDescription());
        dto.setValue(status.getCode());
        return dto;
    }
}
