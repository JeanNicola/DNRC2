package gov.mt.wris.services.Implementation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllApplicationTypesDto;
import gov.mt.wris.dtos.ApplicationTypeDto;
import gov.mt.wris.models.ApplicationType;
import gov.mt.wris.repositories.ApplicationTypeRepository;
import gov.mt.wris.services.ApplicationTypeService;

@Service
public class ApplicationTypeServiceImpl implements ApplicationTypeService{
    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationTypeServiceImpl.class);
    
    @Autowired
    ApplicationTypeRepository applicationRepository;
    
    @Override
    public AllApplicationTypesDto getAllApplicationTypes() {
        LOGGER.info("Getting all the Application Types");
        AllApplicationTypesDto allApplicationTypesDto = new AllApplicationTypesDto();
        List<ApplicationTypeDto> applicationTypeList = StreamSupport.stream(applicationRepository.findAll(Sort.by(Sort.Direction.ASC, "code")).spliterator(), false)
                                    .map(type -> {
                                        return getApplicationTypeDto(type);
                                    }).collect(Collectors.toList());
        
        allApplicationTypesDto.setResults(applicationTypeList);
        return allApplicationTypesDto;
    }

    private ApplicationTypeDto getApplicationTypeDto(ApplicationType modelType) {
        ApplicationTypeDto dto = new ApplicationTypeDto();
        dto.setCode(modelType.getCode());
        dto.setDescription(modelType.getDescription());
        return dto;
    }
}
