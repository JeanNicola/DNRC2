package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionObjectionsElementsDto;
import gov.mt.wris.dtos.WaterRightVersionObjectionsElementsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectionsElementsSortColumn;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.ElementObjection;
import gov.mt.wris.models.Objection;
import gov.mt.wris.repositories.ElementObjectionRepository;
import gov.mt.wris.repositories.ObjectionsRepository;
import gov.mt.wris.services.ElementObjectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElementObjectionServiceImpl implements ElementObjectionService {

    private static Logger LOGGER = LoggerFactory.getLogger(ElementObjectionServiceImpl.class);

    @Autowired
    private ObjectionsRepository objectionsRepository;

    @Autowired
    private ElementObjectionRepository elementObjectionRepository;

    @Override
    public WaterRightVersionObjectionsElementsPageDto getWaterRightVersionObjectionElements(Integer pageNumber, Integer pageSize, WaterRightVersionObjectionsElementsSortColumn sortColumn, SortDirection sortDirection, BigDecimal waterRightId, BigDecimal versionId, BigDecimal objectionId) {

        LOGGER.info("Get water right version element objections");

        Optional<Objection> obj = objectionsRepository.findById(objectionId);
        if (!obj.isPresent()) {
            throw new NotFoundException("Objection with id " + objectionId + " not found.");
        }
        if (!(obj.get().getWaterRightId().equals(waterRightId) && obj.get().getVersionId().equals(versionId))) {
            throw new DataConflictException(
                    String.format("Water right %s, version %s does not have objection id %s",
                            waterRightId, versionId, objectionId)
            );
        }

        Sort sortDtoColumn = getWaterRightVersionObjectionElementsSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<ElementObjection> results = elementObjectionRepository.findElementObjectionByObjectionId(request, objectionId);

        WaterRightVersionObjectionsElementsPageDto page = new WaterRightVersionObjectionsElementsPageDto();
        page.setResults(results.getContent().stream().map(element -> {
            return getWaterRightVersionObjectionsElementsDto(element);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getWaterRightVersionObjectionElementsSortColumn(WaterRightVersionObjectionsElementsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();

        switch (sortColumn) {
            case ELEMENTTYPE:
                orders.add(new Sort.Order(direction, "type"));
                break;
            case ELEMENTOBJECTIONID:
                orders.add(new Sort.Order(direction, "id"));
                break;
            case ELEMENTOBJECTIONREMARK:
                orders.add(new Sort.Order(direction, "comment"));
                break;
            case ELEMENTTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "typeReference.description"));
        }
        // Secondary - ELEMENTTYPEDESCRIPTION
        orders.add(new Sort.Order(Sort.Direction.ASC, "typeReference.description"));
        return Sort.by(orders);

    }

    private WaterRightVersionObjectionsElementsDto getWaterRightVersionObjectionsElementsDto(ElementObjection model) {

        WaterRightVersionObjectionsElementsDto dto = new WaterRightVersionObjectionsElementsDto();
        dto.setElementObjectionId(model.getId().longValue());
        dto.setElementObjectionRemark(model.getComment());
        dto.setElementType(model.getType());
        if (model.getTypeReference() != null)
           dto.setElementTypeDescription(model.getTypeReference().getDescription());
        return dto;

    }
}
