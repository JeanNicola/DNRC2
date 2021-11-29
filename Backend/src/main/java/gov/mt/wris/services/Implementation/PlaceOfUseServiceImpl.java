package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.LegalLandCreationDto;
import gov.mt.wris.dtos.PlaceOfUseCreationDto;
import gov.mt.wris.dtos.PlaceOfUseDto;
import gov.mt.wris.dtos.PlacesOfUsePageDto;
import gov.mt.wris.dtos.PlacesOfUseSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.PlaceOfUse;
import gov.mt.wris.models.Purpose;
import gov.mt.wris.models.PurposeVolumeCalculation;
import gov.mt.wris.repositories.PlaceOfUseExaminationXrefRepository;
import gov.mt.wris.repositories.PlaceOfUseRepository;
import gov.mt.wris.repositories.PurposeRepository;
import gov.mt.wris.repositories.SubdivisionXrefRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.LegalLandService;
import gov.mt.wris.services.PlaceOfUseService;
import gov.mt.wris.services.PurposeVolumeCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PlaceOfUseServiceImpl implements PlaceOfUseService {

    private static Logger LOGGER = LoggerFactory.getLogger(PlaceOfUseServiceImpl.class);

    @Autowired
    private PlaceOfUseRepository placeOfUseRepository;

    @Autowired
    private LegalLandService legalLandService;

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private PlaceOfUseExaminationXrefRepository placeOfUseExaminationXrefRepository;

    @Autowired
    private SubdivisionXrefRepository subdivisionXrefRepository;

    @Autowired
    private PurposeVolumeCalculationService purposeVolumeCalculationService;

    @Autowired
    private WaterRightVersionRepository waterRightVersionRepository;

    public PlacesOfUsePageDto getPlacesOfUse(int pageNumber, int pageSize, PlacesOfUseSortColumn sortColumn, SortDirection sortDirection, Long purposeId) {

        LOGGER.info("Get Places of Use");

        Sort sortDtoColumn = PurposeServiceImpl.getPlaceOfUseSort(sortColumn, sortDirection, PlaceOfUseStatus.ACTIVE);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<PlaceOfUse> results = placeOfUseRepository.getPlaceOfUseByPurposeId(request, new BigDecimal(purposeId));

        PlacesOfUsePageDto page = new PlacesOfUsePageDto();
        page.setResults(results.getContent().stream().map(p -> {
            PlaceOfUseDto dto = PurposeServiceImpl.getPlaceOfUseDto(p);
            dto.setPlaceId(p.getPlaceId().longValue());
            dto.setPurposeId(p.getPurposeId().longValue());
            dto.setModifiedByThisChange(p.getModified());
            dto.setHasSubdivisions(p.getSubdivisions().size() > 0);
            dto.setHasExaminations(p.getExaminations().size() > 0);
            if (p.getModifiedReference() != null)
                dto.setModifiedByThisChangeDescription(p.getModifiedReference().getMeaning());
            return dto;
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    public PlaceOfUseDto createPlaceOfUse(BigDecimal purposeId, PlaceOfUseCreationDto createDto) {

        LOGGER.info("Create Place Of Use (default)");
        return createPlaceOfUse(purposeId, createDto, true);

    }

    @Transactional
    public PlaceOfUseDto createPlaceOfUse(BigDecimal purposeId, PlaceOfUseCreationDto placeOfUseCreationDto, Boolean sort) {

        LOGGER.info("Create Place Of Use");

        LegalLandCreationDto legalLand = placeOfUseCreationDto.getLegalLand();
        Optional<Purpose> foundPurpose = purposeRepository.getPurpose(purposeId);
        if(!foundPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose id %s not found.", purposeId));

        Long legalId = legalLandService.getLegalLandDescriptionId(legalLand.getDescription320(), legalLand.getDescription160(), legalLand.getDescription80(), legalLand.getDescription40(), legalLand.getGovernmentLot(), legalLand.getTownship(), legalLand.getTownshipDirection(), legalLand.getRange(), legalLand.getRangeDirection(), legalLand.getSection(), legalLand.getCountyId());
        PlaceOfUse plou = new PlaceOfUse();
        plou.setPlaceId(placeOfUseRepository.getNextPlaceOfUseId(purposeId));
        plou.setPurposeId(purposeId);
        if (placeOfUseCreationDto.getAcreage()!=null)
            plou.setAcreage(placeOfUseCreationDto.getAcreage());
        plou.setElementOrigin(placeOfUseCreationDto.getElementOrigin());
        plou.setLegalLandDescriptionId(new BigDecimal(legalId));
        plou.setCountyId(new BigDecimal(legalLand.getCountyId()));
        plou.setModified(placeOfUseCreationDto.getModifiedByThisChange());

        PlaceOfUseDto result = PurposeServiceImpl.getPlaceOfUseDto(placeOfUseRepository.save(plou));
        result.setPlaceId(plou.getPlaceId().longValue());


        Purpose purpose = foundPurpose.get();

        placeOfUseExaminationXrefRepository.insertPlaceOfUseIntoAllDataSources(purposeId, plou.getPlaceId());

        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .appendPattern("MM-dd-yyyy")
                .optionalStart()
                .appendPattern(" HH:mm")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();

        if ((PurposeServiceImpl.calcVolTypes.contains(purpose.getPurposeTypeCode())) && purpose.getVolumeAmount() == null &&
                (purpose.getWaterRightVersion().getPriorityDate() != null && purpose.getWaterRightVersion().getPriorityDate().isAfter(LocalDateTime.parse(Constants.DNRC_THRESHOLD_PRIORITY_DATE, dtf))) &&
                (purpose.getWaterRightVersion().getWaterRight() != null && purpose.getWaterRightVersion().getWaterRight().getWaterRightType().getCode().equals(Constants.WATER_RIGHT_TYPE_GROUND_WATER_CERT))
        ) {
            PurposeVolumeCalculation volumeCalculation = purposeVolumeCalculationService.calculateVolume(purpose.getPurposeId(), purpose.getPurposeTypeCode(),
                    (purpose.getHousehold() != null) ? purpose.getHousehold() : 0, purpose.getClimaticAreaCode(),
                    purpose.getAnimalUnits(), purpose.getPeriods());

            if (volumeCalculation.getMessages().isEmpty()) {
                purpose.setVolumeAmount(volumeCalculation.getVolume());
            }

            purposeRepository.saveAndFlush(purpose);
            String program = purpose.getWaterRightVersion().getWaterRight().getWaterRightType().getProgram();

            if ("NA".equals(program)) {
                BigDecimal maxVolume = purposeRepository.getTotalPurposeVolumeForWaterRightVersion(purpose.getWaterRightVersion().getWaterRightId(), purpose.getWaterRightVersion().getVersion());
                waterRightVersionRepository.updateMaxVolume(purpose.getWaterRightVersion().getWaterRightId(), purpose.getWaterRightVersion().getVersion(), maxVolume);
            }

        }

        if (sort)
            placeOfUseRepository.reNumberPlaceOfUse("TRS", foundPurpose.get().getWaterRightId(), foundPurpose.get().getVersionId());

        return result;

    }

    @Transactional
    public PlaceOfUseDto updatePlaceOfUse(BigDecimal purposeId, BigDecimal placeId, PlaceOfUseCreationDto placeOfUseCreationDto) {

        LOGGER.info("Update Place Of Use");

        LegalLandCreationDto legalLand = placeOfUseCreationDto.getLegalLand();
        Optional<PlaceOfUse> fndPlou = placeOfUseRepository.findPlaceOfUseByPlaceIdAndPurposeIdWithPurpose(placeId, purposeId);
        if(!fndPlou.isPresent())
            throw new NotFoundException(String.format("Place Of Use id %s of Purpose %s not found.", placeId, purposeId));
        Long legalId = legalLandService.getLegalLandDescriptionId(legalLand.getDescription320(), legalLand.getDescription160(), legalLand.getDescription80(), legalLand.getDescription40(), legalLand.getGovernmentLot(), legalLand.getTownship(), legalLand.getTownshipDirection(), legalLand.getRange(), legalLand.getRangeDirection(), legalLand.getSection(), legalLand.getCountyId());
        PlaceOfUse plou = fndPlou.get();
        if (placeOfUseCreationDto.getAcreage()!=null)
            plou.setAcreage(placeOfUseCreationDto.getAcreage());
        plou.setElementOrigin(placeOfUseCreationDto.getElementOrigin());
        plou.setLegalLandDescriptionId(new BigDecimal(legalId));
        plou.setCountyId(new BigDecimal(legalLand.getCountyId()));
        plou.setModified(placeOfUseCreationDto.getModifiedByThisChange());

        Purpose purpose = fndPlou.get().getPurpose();
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .appendPattern("MM-dd-yyyy")
                .optionalStart()
                .appendPattern(" HH:mm")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();

        if ((PurposeServiceImpl.calcVolTypes.contains(purpose.getPurposeTypeCode())) && purpose.getVolumeAmount() == null &&
                (purpose.getWaterRightVersion().getPriorityDate() != null && purpose.getWaterRightVersion().getPriorityDate().isAfter(LocalDateTime.parse(Constants.DNRC_THRESHOLD_PRIORITY_DATE, dtf))) &&
                (purpose.getWaterRightVersion().getWaterRight() != null && purpose.getWaterRightVersion().getWaterRight().getWaterRightType().getCode().equals(Constants.WATER_RIGHT_TYPE_GROUND_WATER_CERT))
        ) {
            PurposeVolumeCalculation volumeCalculation = purposeVolumeCalculationService.calculateVolume(purpose.getPurposeId(), purpose.getPurposeTypeCode(),
                    (purpose.getHousehold() != null) ? purpose.getHousehold() : 0, purpose.getClimaticAreaCode(),
                    purpose.getAnimalUnits(), purpose.getPeriods());

            if (volumeCalculation.getMessages().isEmpty()) {
                purpose.setVolumeAmount(volumeCalculation.getVolume());
            }

            purposeRepository.saveAndFlush(purpose);
            String program = purpose.getWaterRightVersion().getWaterRight().getWaterRightType().getProgram();

            if ("NA".equals(program)) {
                BigDecimal maxVolume = purposeRepository.getTotalPurposeVolumeForWaterRightVersion(purpose.getWaterRightVersion().getWaterRightId(), purpose.getWaterRightVersion().getVersion());
                waterRightVersionRepository.updateMaxVolume(purpose.getWaterRightVersion().getWaterRightId(), purpose.getWaterRightVersion().getVersion(), maxVolume);
            }
        }

        return PurposeServiceImpl.getPlaceOfUseDto(placeOfUseRepository.save(plou));

    }

    @Transactional
    public void deletePlaceOfUse(BigDecimal purposeId, BigDecimal placeId) {

        LOGGER.info("Delete Place Of Use");

        Optional<PlaceOfUse> fndPlou = placeOfUseRepository.findPlaceOfUseByPlaceIdAndPurposeId(placeId, purposeId);
        if(!fndPlou.isPresent())
            throw new NotFoundException(String.format("Place Of Use id %s of Purpose %s not found.", placeId, purposeId));

        /* Delete all associated examination and subdivision records then place */
        placeOfUseExaminationXrefRepository.deletePlaceOfUseExaminations(purposeId, placeId);
        subdivisionXrefRepository.deletePlaceOfUseSubdivisions(purposeId, placeId);
        placeOfUseRepository.deletePlaceOfUse(purposeId, placeId);
        /* Auto renumber and sort */
        PlaceOfUse plou = fndPlou.get();
        placeOfUseRepository.reNumberPlaceOfUse("TRS", plou.getPurpose().getWaterRightId(), plou.getPurpose().getVersionId());

    }

    public void pousCopyPods(BigDecimal purposeId) {

        LOGGER.info("Place Of Use copy PODs");

        Optional<Purpose> purpose = purposeRepository.getPurpose(purposeId);
        if (!purpose.isPresent())
            throw new NotFoundException(String.format("Purpose %s not found",purposeId));
        placeOfUseRepository.replicatePodsPlus(purposeId);

    }

}
