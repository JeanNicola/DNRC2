package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightOwnershipSortColumn;
import gov.mt.wris.dtos.WaterRightOwnershipUpdateDto;
import gov.mt.wris.dtos.WaterRightOwnershipUpdatePageDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.OwnershipUpdate;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.repositories.OwnershipUpdateRepository;
import gov.mt.wris.repositories.WaterRightRepository;
import gov.mt.wris.services.WaterRightOwnershipUpdateService;

@Service
public class WaterRightOwnershipUpdateServiceImpl implements WaterRightOwnershipUpdateService {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightOwnershipUpdateService.class);
    
    @Autowired
    private WaterRightRepository waterRepo;

    @Autowired
    private OwnershipUpdateRepository updateRepo;

    public WaterRightOwnershipUpdatePageDto getWaterRightOwnershipUpdates(int pagenumber,
        int pagesize,
        WaterRightOwnershipSortColumn sortDTOColumn,
        SortDirection sortDirection,
        Long waterRightId
    ) {
        LOGGER.info("Getting the Ownership Updates associated with a Water Right");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(waterId);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This water right does not exist");
        }

        String sortColumn = getOwnershipUpdateSortColumn(sortDTOColumn);
        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, Sort.by(direction, sortColumn).and(Sort.by(Sort.Direction.DESC, "ownerUpdateId")));

        WaterRightOwnershipUpdatePageDto page = new WaterRightOwnershipUpdatePageDto();

        Page<OwnershipUpdate> resultsPage = updateRepo.findByWaterRightId(pageable, waterId);

        page.setResults(resultsPage.getContent().stream().map(update -> {
            return getOwnershipUpdateDto(update);
        }).collect(Collectors.toList()));

        page.setSortColumn(sortDTOColumn);
        page.setSortDirection(sortDirection);

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        return page;
    } 

    private WaterRightOwnershipUpdateDto getOwnershipUpdateDto(OwnershipUpdate update) {
        WaterRightOwnershipUpdateDto dto = new WaterRightOwnershipUpdateDto();
        dto.setOwnerUpdateId(update.getOwnerUpdateId().longValue());
        dto.setUpdateType(update.getUpdateTypeValue().getMeaning());
        dto.setDateProcessed(update.getDateProcessed());
        dto.setDateReceived(update.getDateReceived());
        dto.setDateTerminated(update.getDateTerminated());
        return dto;
    }

    private String getOwnershipUpdateSortColumn(WaterRightOwnershipSortColumn sortColumn) {
        switch(sortColumn) {
            case DATEPROCESSED:
                return "dateProcessed";
            case DATERECEIVED:
                return "dateReceived";
            case DATETERMINATED:
                return "dateTerminated";
            case UPDATETYPE:
                return "t.meaning";
            default:
                return "ownerUpdateId";
        }
    }
}
