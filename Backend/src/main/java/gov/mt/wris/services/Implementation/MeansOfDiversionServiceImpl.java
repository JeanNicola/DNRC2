package gov.mt.wris.services.Implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.ReferenceDto;
import gov.mt.wris.models.MeansOfDiversion;
import gov.mt.wris.repositories.MeansOfDiversionRepository;
import gov.mt.wris.services.MeansOfDiversionService;

@Service
public class MeansOfDiversionServiceImpl implements MeansOfDiversionService {
    public static Logger LOGGER = LoggerFactory.getLogger(MeansOfDiversionService.class);

    @Autowired
    private MeansOfDiversionRepository menasRepository;

    public AllReferencesDto getMeansOfDiversions() {
        LOGGER.info("Getting all the Means of Diversions");

        List<MeansOfDiversion> means = menasRepository.findByOrderByDescription();

        AllReferencesDto dto = new AllReferencesDto()
            .results(
                means.stream().map(mean -> {
                    return new ReferenceDto()
                        .description(mean.getDescription())
                        .value(mean.getMeansCode());
                }).collect(Collectors.toList())
            );
        return dto;
    }
}
