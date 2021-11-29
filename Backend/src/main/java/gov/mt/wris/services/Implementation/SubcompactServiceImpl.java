package gov.mt.wris.services.Implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubcompactDto;
import gov.mt.wris.dtos.SubcompactPageDto;
import gov.mt.wris.dtos.SubcompactSortColumn;
import gov.mt.wris.models.Subcompact;
import gov.mt.wris.repositories.CompactRepository;
import gov.mt.wris.services.SubcompactService;

@Service
public class SubcompactServiceImpl implements SubcompactService {
    private static Logger LOGGER = LoggerFactory.getLogger(SubcompactService.class);

    @Autowired
    CompactRepository compactRepo;

    public SubcompactPageDto searchSubcompacts(int pagenumber,
        int pagesize,
        SubcompactSortColumn sortColumn,
        SortDirection sortDirection,
        String subcompact,
        String compact
    ) {
        LOGGER.info("Searching subcompacts");

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize);

        Page<Subcompact> resultsPage = compactRepo.searchSubcompacts(pageable, sortColumn, sortDirection, subcompact, compact);

        SubcompactPageDto dto = new SubcompactPageDto();
        dto.setResults(resultsPage.getContent().stream().map(sc -> {
            return getSubcompactDto(sc);
        }).collect(Collectors.toList()));

        dto.setCurrentPage(resultsPage.getNumber() + 1);
        dto.setPageSize(resultsPage.getSize());

        dto.setTotalPages(resultsPage.getTotalPages());
        dto.setTotalElements(resultsPage.getTotalElements());

        dto.setSortColumn(sortColumn);
        dto.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(subcompact != null) {
            filters.put("subcompact", subcompact);
        }
        if(compact != null) {
            filters.put("compact", compact);
        }
        dto.setFilters(filters);

        return dto;
    }

    private SubcompactDto getSubcompactDto(Subcompact subcompact) {
        SubcompactDto dto = new SubcompactDto();
        dto.setCompact(subcompact.getCompact().getName());
        dto.setSubcompact(subcompact.getName());
        dto.setSubcompactId(subcompact.getId().longValue());
        return dto;
    }
}
