package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.DitchCreationDto;
import gov.mt.wris.dtos.DitchDto;
import gov.mt.wris.dtos.DitchPageDto;
import gov.mt.wris.dtos.DitchSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.Ditch;
import gov.mt.wris.repositories.DitchRepository;
import gov.mt.wris.services.DitchService;
import gov.mt.wris.services.LegalLandService;

@Service
public class DitchServiceImpl implements DitchService {
    private static Logger LOGGER = LoggerFactory.getLogger(DitchService.class);

    @Autowired
    private DitchRepository ditchRepository;

    @Autowired
    private LegalLandService legalLandService;

    public DitchPageDto searchDitches(
        int pagenumber,
        int pagesize,
        DitchSortColumn sortColumn,
        SortDirection sortDirection,
        String name
    ) {
        LOGGER.info("Searching for ditches");

        Sort sort = getDitchSortColumn(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        name = name != null ? name : "%";

        Page<Ditch> results = ditchRepository.searchDitches(pageable, name);

        DitchPageDto page = new DitchPageDto();
        page.setResults(
            results.getContent().stream()
            .map(ditch -> getDitchDto(ditch))
            .collect(Collectors.toList())
        );

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());

        page.setTotalElements(results.getTotalElements());
        page.setTotalPages(results.getTotalPages());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    private static Sort getDitchSortColumn(DitchSortColumn sortColumn, SortDirection sortDirection) {
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;
        switch(sortColumn) {
            case DITCHNAME:
                sort = Sort.by(direction, "name");
                break;
            case LEGALLANDDESCRIPTION:
                sort = Sort.by(direction, "ll.governmentLot");
                sort = sort.and(Sort.by(direction, "ll.description320"));
                sort = sort.and(Sort.by(direction, "ll.description160"));
                sort = sort.and(Sort.by(direction, "ll.description80"));
                sort = sort.and(Sort.by(direction, "ll.description40"));
                sort = sort.and(Sort.by(direction, "t.section"));
                sort = sort.and(Sort.by(direction, "t.township"));
                sort = sort.and(Sort.by(direction, "t.townshipDirection"));
                sort = sort.and(Sort.by(direction, "t.range"));
                sort = sort.and(Sort.by(direction, "t.rangeDirection"));
                sort = sort.and(Sort.by(direction, "c.name"));
                sort = sort.and(Sort.by(direction, "c.stateCode"));
                break;
            default:
                sort = Sort.by(Sort.Direction.ASC, "name");
        }
        sort.and(Sort.by(Sort.Direction.ASC, "id"));
        return sort;
    }

    private DitchDto getDitchDto(Ditch ditch) {
        return new DitchDto()
            .ditchId(ditch.getId().longValue())
            .ditchName(ditch.getName())
            .ditchType(ditch.getDiversionTypeCode())
            .legalLandDescription(
                legalLandService.buildLegalLandDescription(ditch.getLegalLandDescription(), ditch.getCounty())
            );
    }

    public DitchDto createDitch(DitchCreationDto dto) {
        LOGGER.info("Creating a new Ditch");

        Ditch ditch = new Ditch();

        ditch.setName(dto.getDitchName());
        ditch.setDiversionTypeCode(dto.getDitchTypeCode());
        ditch.setCapacity(dto.getCapacity());
        ditch.setDepth(dto.getDepth());
        ditch.setWidth(dto.getWidth());
        ditch.setLength(dto.getLength());
        ditch.setSlope(dto.getSlope());
        dto.setValid("Y".equals(ditch.getValid()));

        Stream<Boolean> legalLandFields = Stream.of(
            dto.getDescription320(),
            dto.getDescription160(),
            dto.getDescription80(),
            dto.getDescription40(),
            dto.getGovernmentLot(),
            dto.getTownship(),
            dto.getTownshipDirection(),
            dto.getRange(),
            dto.getRangeDirection(),
            dto.getSection(),
            dto.getCountyId()
        ).map(field -> field != null);

        if(legalLandFields.anyMatch(field -> field)) {
            BigDecimal legalLandDescriptionId = BigDecimal.valueOf(
                legalLandService.getLegalLandDescriptionId(
                    dto.getDescription320(), 
                    dto.getDescription160(),
                    dto.getDescription80(), 
                    dto.getDescription40(), 
                    dto.getGovernmentLot(), 
                    dto.getTownship(), 
                    dto.getTownshipDirection(), 
                    dto.getRange(), 
                    dto.getRangeDirection(), 
                    dto.getSection(), 
                    dto.getCountyId()
                )
            );
            ditch.setLegalLandDescriptionId(legalLandDescriptionId);
            BigDecimal countyId = BigDecimal.valueOf(dto.getCountyId());
            ditch.setCountyId(countyId);
        }

        ditch = ditchRepository.save(ditch);

        return getDitchDto(ditch);
    }
}
