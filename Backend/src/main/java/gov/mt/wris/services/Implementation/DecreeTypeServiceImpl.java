package gov.mt.wris.services.Implementation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllDecreeTypesDto;
import gov.mt.wris.dtos.DecreeTypeDto;
import gov.mt.wris.models.DecreeType;
import gov.mt.wris.repositories.DecreeTypeRepository;
import gov.mt.wris.services.DecreeTypeService;

@Service
public class DecreeTypeServiceImpl implements DecreeTypeService{
    private static Logger LOGGER = LoggerFactory.getLogger(DecreeTypeServiceImpl.class);
    
    @Autowired
    DecreeTypeRepository decreeRepository;
    
    @Override
    public AllDecreeTypesDto getAllDecreeTypes() {
        LOGGER.info("Getting all the Decree Types");
        AllDecreeTypesDto allDecreeTypesDto = new AllDecreeTypesDto();
        List<DecreeTypeDto> decreeTypeList = StreamSupport.stream(decreeRepository.findAll(Sort.by(Sort.Direction.ASC, "description")).spliterator(), false)
                                    .map(type -> {
                                        return getDecreeTypeDto(type);
                                    }).collect(Collectors.toList());
        
        allDecreeTypesDto.setResults(decreeTypeList);
        return allDecreeTypesDto;
    }

    private DecreeTypeDto getDecreeTypeDto(DecreeType modelType) {
        DecreeTypeDto dto = new DecreeTypeDto();
        dto.setCode(modelType.getCode());
        dto.setDescription(modelType.getDescription());
        return dto;
    }
}