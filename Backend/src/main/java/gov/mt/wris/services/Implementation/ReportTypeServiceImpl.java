package gov.mt.wris.services.Implementation;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.ReferenceDto;
import gov.mt.wris.models.ReportType;
import gov.mt.wris.repositories.ReportTypeRepository;
import gov.mt.wris.services.ReportTypeService;

@Service
public class ReportTypeServiceImpl implements ReportTypeService {
    public static Logger LOGGER = LoggerFactory.getLogger(ReportTypeService.class);

    @Autowired
    ReportTypeRepository typeRepository;

    public AllReferencesDto getAllReportTypes() {
        LOGGER.info("Getting all Report Types");

        AllReferencesDto dto = new AllReferencesDto();

        dto.setResults(
            typeRepository.findByOrderByDescription()
            .stream()
            .map(type -> getReferenceDto(type))
            .collect(Collectors.toList())
        );

        return dto;
    }

    private ReferenceDto getReferenceDto(ReportType model) {
        return new ReferenceDto()
            .description(model.getDescription())
            .value(model.getReportTypeCode());
    }
}
