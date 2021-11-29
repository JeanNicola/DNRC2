package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.ReservoirCreationDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionReservoirsDto;
import gov.mt.wris.dtos.WaterRightVersionReservoirsPageDto;
import gov.mt.wris.dtos.WaterRightVersionReservoirsSortColumn;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.LegalLandDescription;
import gov.mt.wris.models.Reservoir;
import gov.mt.wris.models.TRS;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.repositories.LegalLandDescriptionRepository;
import gov.mt.wris.repositories.ReservoirRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.LegalLandService;
import gov.mt.wris.services.VersionReservoirService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VersionReservoirServiceImpl implements VersionReservoirService {
    private static Logger LOGGER = LoggerFactory.getLogger(VersionReservoirService.class);

    @Autowired
    ReservoirRepository reservoirRepository;

    @Autowired
    WaterRightVersionRepository versionRepository;

    @Autowired
    LegalLandDescriptionRepository legalRepository;

    @Autowired
    LegalLandService legalLandService;
    
    public WaterRightVersionReservoirsPageDto getVersionReservoirs(
        int pagenumber,
        int pagesize,
        WaterRightVersionReservoirsSortColumn sortColumn,
        SortDirection sortDirection,
        Long waterRightId,
        Long versionNumber
    ) {
        LOGGER.info("Getting a page of Reservoirs belonging to a Water Right Version");

        Sort sort = getReservoirSort(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);

        Page<Reservoir> results = reservoirRepository.findReservoirs(pageable, waterId, version);

        WaterRightVersionReservoirsPageDto page = new WaterRightVersionReservoirsPageDto();
        page.setResults(results.getContent().stream()
            .map(reservoir -> getReservoirDto(reservoir)
        ).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());

        page.setTotalElements(results.getTotalElements());
        page.setTotalPages(results.getTotalPages());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    private Sort getReservoirSort(WaterRightVersionReservoirsSortColumn sortColumn, SortDirection sortDirection) {
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;
        switch(sortColumn) {
            case RESERVOIRID:
                sort = Sort.by(direction, "id");
                break;
            case PODID:
                sort = Sort.by(direction, "pod.number");
                break;
            case RESERVOIRNAME:
                sort = Sort.by(direction, "name");
                break;
            case ONSTREAM:
                sort = Sort.by(direction, "typeCode");
                break;
            case CURRENTCAPACITY:
                sort = Sort.by(direction, "currentCapacity");
                break;
            case ENLARGEDCAPACITY:
                sort = Sort.by(direction, "enlargedCapacity");
                break;
            case MAXDEPTH:
                sort = Sort.by(direction, "depth");
                break;
            case DAMHEIGHT:
                sort = Sort.by(direction, "height");
                break;
            case SURFACEAREA:
                sort = Sort.by(direction, "surfaceArea");
                break;
            case ELEVATION:
                sort = Sort.by(direction, "elevation");
                break;
            case RESERVOIRORIGINDESCRIPTION:
                sort = Sort.by(direction, "o.meaning");
                break;
            case COMPLETELEGALLANDDESCRIPTION:
                sort = Sort.by(direction, "ll.governmentLot");
                sort = sort.and(Sort.by(direction, "ll.description320"));
                sort = sort.and(Sort.by(direction, "ll.description160"));
                sort = sort.and(Sort.by(direction, "ll.description80"));
                sort = sort.and(Sort.by(direction, "ll.description40"));
                sort = sort.and(Sort.by(direction, "trs.section"));
                sort = sort.and(Sort.by(direction, "trs.township"));
                sort = sort.and(Sort.by(direction, "trs.townshipDirection"));
                sort = sort.and(Sort.by(direction, "trs.range"));
                sort = sort.and(Sort.by(direction, "trs.rangeDirection"));
                sort = sort.and(Sort.by(direction, "c.name"));
                sort = sort.and(Sort.by(direction, "c.stateCode"));
                break;
            default:
                sort = Sort.by(Sort.Direction.ASC, "id");
        }
        sort.and(Sort.by(Sort.Direction.ASC, "id"));
        return sort;
    }

    private WaterRightVersionReservoirsDto getReservoirDto(Reservoir reservoir) {
        WaterRightVersionReservoirsDto dto = new WaterRightVersionReservoirsDto();

        dto.setReservoirId(reservoir.getId().longValue());
        if(reservoir.getPointOfDiversion() != null) {
            dto.setPodNumber(reservoir.getPointOfDiversion().getNumber().longValue());
            dto.setPodId(reservoir.getPointOfDiversion().getId().longValue());
        }
        dto.setReservoirName(reservoir.getName());
        dto.setReservoirTypeCode(reservoir.getTypeCode());
        dto.setReservoirTypeDescription(reservoir.getTypeReference() != null ? reservoir.getTypeReference().getMeaning() : null);
        dto.setChanged("Y".equals(reservoir.getChanged()));
        if(reservoir.getCurrentCapacity() != null) dto.setCurrentCapacity(reservoir.getCurrentCapacity().doubleValue());
        if(reservoir.getEnlargedCapacity() != null) dto.setEnlargedCapacity(reservoir.getEnlargedCapacity().doubleValue());
        if(reservoir.getDepth() != null) dto.setMaxDepth(reservoir.getDepth().doubleValue());
        if(reservoir.getHeight() != null) dto.setDamHeight(reservoir.getHeight().doubleValue());
        if(reservoir.getSurfaceArea() != null) dto.setSurfaceArea(reservoir.getSurfaceArea().doubleValue());
        if(reservoir.getElevation() != null) dto.setElevation(reservoir.getElevation().doubleValue());
        dto.setReservoirOriginCode(reservoir.getOrigin());
        dto.setReservoirOriginDescription(reservoir.getOriginReference().getMeaning());

        dto.setCompleteLegalLandDescription(legalLandService.buildLegalLandDescription(reservoir.getLegalLandDescription(), reservoir.getCounty()));

        if(reservoir.getLegalLandDescription() != null) {
            LegalLandDescription land = reservoir.getLegalLandDescription();
            dto.setDescription320(land.getDescription320());
            dto.setDescription160(land.getDescription160());
            dto.setDescription80(land.getDescription40());
            dto.setDescription40(land.getDescription40());
            if(land.getGovernmentLot() != null) dto.setGovernmentLot(land.getGovernmentLot().longValue());
            TRS trs = land.getTrs();
            if(trs.getTownship() != null) dto.setTownship(trs.getTownship().longValue());
            dto.setTownshipDirection(trs.getTownshipDirection());
            if(trs.getRange() != null) dto.setRange(trs.getRange().longValue());
            dto.setRangeDirection(trs.getRangeDirection());
            if(trs.getSection() != null) dto.setSection(trs.getSection().longValue());
        }
        dto.setCountyId(reservoir.getCountyId().longValue());

        return dto;
    }

    @Transactional
    public void addVersionReservoir(Long waterRightId, Long versionNumber, ReservoirCreationDto dto) {
        LOGGER.info("Adding a Reservoir to a Version");

        List<Boolean> requiredFields = getRequiredFields(dto);

        // if any legal land descriptions are set,
        // check that all of requiredFields are set
        // and that the legal land description is not set
        requireFullLegalLandDescriptionIfSome(dto);

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepository.findByWaterRightIdAndVersionId(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This Water Right Version does not exist");
        }

        Long legalId = null;
        if(dto.getLegalLandDescriptionId() != null) {
            legalId = dto.getLegalLandDescriptionId();
        } else if(requiredFields.contains(true)) { // this is sufficient to check if any legal land description values are set because of the above validation
            legalId = legalLandService.getLegalLandDescriptionId(dto.getDescription320(), dto.getDescription160(), dto.getDescription80(), dto.getDescription40(), dto.getGovernmentLot(), dto.getTownship(), dto.getTownshipDirection(), dto.getRange(), dto.getRangeDirection(), dto.getSection(), dto.getCountyId());
        }

        Reservoir newReservoir = getReservoir(waterId, versionId, dto);

        if(legalId != null) {
            newReservoir.setLegalLandDescriptionId(BigDecimal.valueOf(legalId));
        }

        reservoirRepository.save(newReservoir);

        return;
    }

    private Reservoir getReservoir(BigDecimal waterRightId, BigDecimal versionId, ReservoirCreationDto dto) {
        Reservoir model = new Reservoir();
        model.setWaterRightId(waterRightId);
        model.setVersion(versionId);
        model.setCountyId(BigDecimal.valueOf(dto.getCountyId()));
        model.setName(dto.getReservoirName());
        if(dto.getPodId() != null) model.setPointOfDiversionId(BigDecimal.valueOf(dto.getPodId()));

        model.setTypeCode(dto.getReservoirTypeCode());

        if(dto.getCurrentCapacity() != null) model.setCurrentCapacity(BigDecimal.valueOf(dto.getCurrentCapacity()));
        if(dto.getEnlargedCapacity() != null) model.setEnlargedCapacity(BigDecimal.valueOf(dto.getEnlargedCapacity()));
        if(dto.getMaxDepth() != null) model.setDepth(BigDecimal.valueOf(dto.getMaxDepth()));
        if(dto.getDamHeight() != null) model.setHeight(BigDecimal.valueOf(dto.getDamHeight()));
        if(dto.getSurfaceArea() != null) model.setSurfaceArea(BigDecimal.valueOf(dto.getSurfaceArea()));
        if(dto.getElevation() != null) model.setElevation(BigDecimal.valueOf(dto.getElevation()));
        model.setOrigin(dto.getReservoirOriginCode());
        model.setChanged(dto.getChanged() != null && dto.getChanged() ? "Y" : "N");

        return model;
    }

    public void updateReservoir(Long waterRightId, Long versionNumber, Long reservoirId, ReservoirCreationDto dto) {
        LOGGER.info("Updating a Reservoir attached to a Version");

        // if any legal land descriptions are set,
        // check that all of required fields are set
        // and that the legal land description is not set
        requireFullLegalLandDescriptionIfSome(dto);

        List<Boolean> requiredFields = getRequiredFields(dto);

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepository.findById(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This Water Right Version does not exist");
        }

        BigDecimal resId = BigDecimal.valueOf(reservoirId);
        Optional<Reservoir> foundReservoir = reservoirRepository.findById(resId);
        if(!foundReservoir.isPresent()) {
            throw new NotFoundException("This Reservoir does not exist");
        }
        Reservoir reservoir = foundReservoir.get();

        // require that the user is either leaving legal land descriptions alone or
        // adding the basic legal land description fields
        if(reservoir.getLegalLandDescriptionId() != null &&
            dto.getLegalLandDescriptionId() != null &&
            reservoir.getLegalLandDescriptionId().longValue() != dto.getLegalLandDescriptionId()
        ) {
            throw new ValidationException("Cannot use the POD ID to change the legal land description");
        }
        if(reservoir.getLegalLandDescriptionId() != null && 
            !requiredFields.contains(true) &&
            dto.getLegalLandDescriptionId() == null
        ) {
            throw new ValidationException("Cannot delete the Legal Land Description, only updates are allowed");
        }

        if(
            requiredFields.contains(true) // this is sufficient to check if any legal land description values are set because of the first validation above
        ) {
            Long legalId = legalLandService.getLegalLandDescriptionId(dto.getDescription320(), dto.getDescription160(), dto.getDescription80(), dto.getDescription40(), dto.getGovernmentLot(), dto.getTownship(), dto.getTownshipDirection(), dto.getRange(), dto.getRangeDirection(), dto.getSection(), dto.getCountyId());
            reservoir.setLegalLandDescriptionId(BigDecimal.valueOf(legalId));
        }

        reservoir.setName(dto.getReservoirName());
        reservoir.setTypeCode(dto.getReservoirTypeCode());
        if(dto.getPodId() != null) {
            reservoir.setPointOfDiversionId(BigDecimal.valueOf(dto.getPodId()));
        }
        reservoir.setCurrentCapacity(dto.getCurrentCapacity() != null ? BigDecimal.valueOf(dto.getCurrentCapacity()) : null);
        reservoir.setEnlargedCapacity(dto.getEnlargedCapacity() != null ? BigDecimal.valueOf(dto.getEnlargedCapacity()) : null);
        reservoir.setDepth(dto.getMaxDepth() != null ? BigDecimal.valueOf(dto.getMaxDepth()) : null);
        reservoir.setHeight(dto.getDamHeight() != null ? BigDecimal.valueOf(dto.getDamHeight()) : null);
        reservoir.setSurfaceArea(dto.getSurfaceArea() != null ? BigDecimal.valueOf(dto.getSurfaceArea()) : null);
        reservoir.setElevation(dto.getElevation() != null ? BigDecimal.valueOf(dto.getElevation()) : null);
        reservoir.setOrigin(dto.getReservoirOriginCode());
        reservoir.setChanged(dto.getChanged() != null && dto.getChanged() ? "Y" : "N");

        reservoirRepository.save(reservoir);

        return;
    }

    private void requireFullLegalLandDescriptionIfSome(ReservoirCreationDto dto) {
        List<Boolean> requiredFields = getRequiredFields(dto);
        if(
            (requiredFields.contains(true) ||
            dto.getGovernmentLot() != null ||
            dto.getDescription40() != null ||
            dto.getDescription80() != null ||
            dto.getDescription160() != null ||
            dto.getDescription320() != null) &&
            requiredFields.contains(false)
        ) {
            throw new ValidationException("When setting any Legal Land Description fields, Sec, Twp, N/S, Rge, E/W, County and State are all required.");
        }

        // we assume that if the user changes any of the
        // legal land description, the frontend will not use the id 
        // provided by the dropdown
        if(dto.getLegalLandDescriptionId() != null && requiredFields.contains(true)) {
            throw new ValidationException("Cannot use a Legal Land Description Id and Legal Land Description fields at the same time.");
        }

    }

    private List<Boolean> getRequiredFields(ReservoirCreationDto dto) {
        return Arrays.asList(dto.getSection() != null,
            dto.getTownship() != null,
            dto.getTownshipDirection() != null,
            dto.getRange() != null,
            dto.getRangeDirection() != null);
    }

    public void deleteReservoir(Long waterRightId, Long versionNumber, Long reservoirId) {
        LOGGER.info("Deleting a Reservoir attached to a Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);
        BigDecimal resId   = BigDecimal.valueOf(reservoirId);

        reservoirRepository.deleteByIdAndWaterRightIdAndVersion(resId, waterId, version);

        return;
    }

}
