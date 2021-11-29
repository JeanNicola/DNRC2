package gov.mt.wris.services.Implementation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllStateCodesDto;
import gov.mt.wris.dtos.StateCodeDto;
import gov.mt.wris.models.StateCode;
import gov.mt.wris.repositories.StateCodeRepository;
import gov.mt.wris.services.StateCodeService;

@Service
public class StateCodeServiceImpl implements StateCodeService{
    private static Logger LOGGER = LoggerFactory.getLogger(StateCodeServiceImpl.class);
    
    @Autowired
    StateCodeRepository stateRepository;
    
    @Override
    public AllStateCodesDto getAllStateCodes() {
        LOGGER.info("Getting all the State Codes");
        AllStateCodesDto allStateCodesDto = new AllStateCodesDto();
        List<StateCodeDto> stateCodeList = StreamSupport.stream(stateRepository.findAllSorted().spliterator(), false)
                                    .map(state -> {
                                        return getStateCodeDto(state);
                                    }).collect(Collectors.toList());
        
        allStateCodesDto.setResults(stateCodeList);
        return allStateCodesDto;
    }

    private StateCodeDto getStateCodeDto(StateCode modelState) {
        StateCodeDto dto = new StateCodeDto();
        dto.setCode(modelState.getCode());
        dto.setName(modelState.getName());
        return dto;
    }
}