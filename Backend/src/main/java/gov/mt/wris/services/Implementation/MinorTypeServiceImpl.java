package gov.mt.wris.services.Implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.ReferenceDto;
import gov.mt.wris.models.MinorType;
import gov.mt.wris.repositories.MinorTypeRepository;
import gov.mt.wris.services.MinorTypeService;

@Service
public class MinorTypeServiceImpl implements MinorTypeService {
    private static Logger LOGGER = LoggerFactory.getLogger(MinorTypeService.class);

    @Autowired
    private MinorTypeRepository minorTypeRepository;

    public AllReferencesDto getAllMinorTypes() {
        LOGGER.info("Getting all the Minor Types");

        List<MinorType> types = minorTypeRepository.findAllByOrderByDescription();

        AllReferencesDto dto = new AllReferencesDto()
            .results(
                types.stream()
                .map(type -> 
                    new ReferenceDto()
                        .value(type.getMinorTypeCode())
                        .description(type.getDescription())
                ).collect(Collectors.toList())
            );

        return dto;
    }
}
