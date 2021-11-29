package gov.mt.wris.services.Implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllBasinsDto;
import gov.mt.wris.dtos.AllSubBasinsDto;
import gov.mt.wris.dtos.BasinDto;
import gov.mt.wris.dtos.SubBasinDto;
import gov.mt.wris.models.BasinCompacts;
import gov.mt.wris.repositories.BasinRepository;
import gov.mt.wris.services.BasinService;

@Service
public class BasinServiceImpl implements BasinService {
    private static Logger LOGGER = LoggerFactory.getLogger(BasinServiceImpl.class);

    @Autowired
    BasinRepository basinRepo;

    @Override
    public AllBasinsDto getBasins() {
        LOGGER.info("Getting a list of all the basins");

        List<BasinCompacts> basins = basinRepo.findAllByOrderByCodeAsc();

        AllBasinsDto allBasins = new AllBasinsDto();
        
        allBasins.setResults(basins.stream().map(basin -> {
            return getBasinDto(basin);
        }).collect(Collectors.toList()));

        return allBasins;
    }

    private BasinDto getBasinDto(BasinCompacts basin) {
        BasinDto dto = new BasinDto();
        dto.setCode(basin.getCode());
        dto.setDescription(basin.getDescription());
        return dto;
    }

    public AllSubBasinsDto getSubBasins() {
        LOGGER.info("Getting a list of all the Sub Basins");

        List<BasinCompacts> basins = basinRepo.findByTypeOrderByCodeAsc(Constants.SUBBASIN_TYPE);

        AllSubBasinsDto allBasins = new AllSubBasinsDto();
        
        allBasins.setResults(basins.stream().map(basin -> {
            return getSubBasinDto(basin);
        }).collect(Collectors.toList()));

        return allBasins;
    }

    private SubBasinDto getSubBasinDto(BasinCompacts basin) {
        SubBasinDto dto = new SubBasinDto();
        dto.setCode(basin.getCode());
        dto.setDescription(basin.getDescription());
        dto.setParent(basin.getParentBasin());
        return dto;
    }
}
