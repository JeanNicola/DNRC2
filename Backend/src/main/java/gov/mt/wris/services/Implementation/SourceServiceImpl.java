package gov.mt.wris.services.Implementation;

import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SourceCreationDto;
import gov.mt.wris.dtos.SourceDto;
import gov.mt.wris.dtos.SourcePageDto;
import gov.mt.wris.models.AlsoKnown;
import gov.mt.wris.models.Source;
import gov.mt.wris.models.SourceName;
import gov.mt.wris.repositories.SourceRepository;
import gov.mt.wris.services.SourceService;

@Service
public class SourceServiceImpl implements SourceService {
    public static Logger LOGGER = LoggerFactory.getLogger(SourceService.class);

    @Autowired
    private SourceRepository sourceRepository;

    public SourcePageDto searchSources(int pagenumber,
        int pagesize,
        SortDirection sortDirection,
        String sourceName
    ) {
        LOGGER.info("Searching for Sources");

        sourceName = sourceName != null ? sourceName : "%";

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "sn.name").and(Sort.by(direction, "forkName")).and(Sort.by(Sort.Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        Page<Source> results = sourceRepository.searchSource(pageable, sourceName);

        SourcePageDto page = new SourcePageDto();
        page.setResults(
            results.getContent().stream()
            .map(source -> getSourceDto(source))
            .collect(Collectors.toList())
        );

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());

        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());

        page.setSortDirection(sortDirection);
        Map<String, String> filters = new HashMap<String, String>();
        filters.put("sourceName", sourceName);

        page.setFilters(filters);

        return page;
    }

    private static SourceDto getSourceDto(Source source) {
        String nameEnd = source.getForkName() != null ? ", " + source.getForkName() : "";
        return new SourceDto()
            .sourceId(source.getId().longValue())
            .sourceName(source.getSourceName().getName() + nameEnd);
    }

    public SourceDto createSource(SourceCreationDto creationDto) {
        LOGGER.info("Creating a new Source");

        Source source = new Source();
        source.setForkName(creationDto.getForkName());
        SourceName name = new SourceName();
        name.setName(creationDto.getSourceName());
        source.setSourceName(name);
        if(creationDto.getKnownAs() != null) {
            AlsoKnown known = new AlsoKnown();
            known.setName(creationDto.getKnownAs());
            source.setAlsoKnowns(Arrays.asList(known));
        }

        try {
            source = sourceRepository.save(source);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("SRNM_UK")) {
                    throw new ValidationException("A Source with this name already exists, choose another");
                }
            }
            throw e;
        }

        return getSourceDto(source);
    }
}
