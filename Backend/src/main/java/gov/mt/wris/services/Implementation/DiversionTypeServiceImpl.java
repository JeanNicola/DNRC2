package gov.mt.wris.services.Implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.ReferenceDto;
import gov.mt.wris.models.DiversionType;
import gov.mt.wris.repositories.DiversionTypeRepository;
import gov.mt.wris.services.DiversionTypeService;

@Service
public class DiversionTypeServiceImpl implements DiversionTypeService {
    private static Logger LOGGER = LoggerFactory.getLogger(DiversionTypeService.class);

    @Autowired
    private DiversionTypeRepository typeRepository;

    public AllReferencesDto getAllDiversionTypes() {
        LOGGER.info("Getting all the Diversion Types");

        List<DiversionType> results = typeRepository.findAllByOrderByDescription();

        AllReferencesDto dto = new AllReferencesDto()
            .results(
                results.stream()
                .map(type -> 
                    new ReferenceDto()
                        .value(type.getCode())
                        .description(type.getDescription())
                ).collect(Collectors.toList())
            );
        
        return dto;
    }
}
