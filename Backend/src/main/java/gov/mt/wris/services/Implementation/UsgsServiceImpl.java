package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.UsgsDto;
import gov.mt.wris.dtos.UsgsPageDto;
import gov.mt.wris.models.Usgs;
import gov.mt.wris.repositories.UsgsRepository;
import gov.mt.wris.services.UsgsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UsgsServiceImpl implements UsgsService {

    private static Logger LOGGER = LoggerFactory.getLogger(UsgsServiceImpl.class);

    @Autowired
    private UsgsRepository usgsRepository;

    private UsgsDto getUsgsDto(Usgs model) {

        UsgsDto dto = new UsgsDto();

        dto.setName(model.getName());
        dto.setUtmpId(model.getUtmpId().longValue());

        return dto;

    }

    public UsgsPageDto getUsgsQuadMapsList(Integer pageNumber, Integer pageSize, SortDirection sortDirection, String usgsQuadMapName) {

        LOGGER.info("Searching USGS Quad Map Values");

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        UsgsPageDto page = new UsgsPageDto();

        Page<Usgs> resultsPage = usgsRepository.searchUsgsQuadMapValues(pageable, sortDirection, usgsQuadMapName);

        page.setResults(resultsPage.getContent().stream().map(usgs -> {
            return getUsgsDto(usgs);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());
        page.setSortDirection(sortDirection);
        page.setTotalElements(resultsPage.getTotalElements());

        return page;
    }
}
