package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightNotificationDto;
import gov.mt.wris.dtos.WaterRightNotificationPageDto;
import gov.mt.wris.dtos.WaterRightNotificationSortColumn;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.WaterRightRepository;
import gov.mt.wris.services.WaterRightNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class WaterRightNotificationServiceImpl implements WaterRightNotificationService {

    private static Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private WaterRightRepository repo;

    @Autowired
    private ApplicationRepository appRepo;

    @Override
    public WaterRightNotificationPageDto findWaterRightsByMailingJobId(
            String applicationId,
            String mailingJobId,
            Integer pageNumber,
            Integer pageSize,
            WaterRightNotificationSortColumn sortColumn,
            SortDirection sortDirection
    ) {
        Sort.Direction sortOrderDirection = sortDirection.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(
                sortOrderDirection, getEntitySortColumn(sortColumn)).
                and(Sort.by(Sort.Direction.DESC, "waterRightNumber"))
        );
        Page<WaterRight> resultPage = repo.findWaterRightsByMailingJobId(pageable, new BigDecimal(applicationId), new BigDecimal(mailingJobId));
        WaterRightNotificationPageDto page = new WaterRightNotificationPageDto();

        page.setResults(resultPage.getContent().stream().map(model -> {
            return getWaterRightDto(model);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());
        return page;
    }

    private WaterRightNotificationDto getWaterRightDto(WaterRight model) {
        WaterRightNotificationDto newDto = new WaterRightNotificationDto();
        newDto.setBasin(model.getBasin());
        newDto.setExt(model.getExt());
        newDto.setStatus(model.getWaterRightStatus() != null ? model.getWaterRightStatus().description : null);
        newDto.setTypeDescription(model.getWaterRightType() != null ? model.getWaterRightType().description : null);
        newDto.setWaterRightNumber(model.getWaterRightNumber() != null ? model.getWaterRightNumber().longValue() : null);
        newDto.setId(model.getWaterRightId().longValue());
        return newDto;
    }

    private String getEntitySortColumn(WaterRightNotificationSortColumn sortColumn) {
        if (sortColumn == WaterRightNotificationSortColumn.BASIN)
            return "basin";
        else if (sortColumn == WaterRightNotificationSortColumn.EXT)
            return "ext";
        else if (sortColumn == WaterRightNotificationSortColumn.STATUS)
            return "ws.description";
        else if (sortColumn == WaterRightNotificationSortColumn.TYPEDESCRIPTION)
            return "wt.description";
        else if (sortColumn == WaterRightNotificationSortColumn.WATERRIGHTNUMBER)
            return "waterRightNumber";
        else {
            return "waterRightNumber";
        }
    }
}
