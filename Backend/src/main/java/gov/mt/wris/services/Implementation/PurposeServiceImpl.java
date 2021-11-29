package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.CalcVolWarningDto;
import gov.mt.wris.dtos.PeriodOfUseDto;
import gov.mt.wris.dtos.PlaceOfUseDto;
import gov.mt.wris.dtos.PlacesOfUseSortColumn;
import gov.mt.wris.dtos.PurposeDetailDto;
import gov.mt.wris.dtos.PurposeDto;
import gov.mt.wris.dtos.PurposeSearchType;
import gov.mt.wris.dtos.PurposeUpdateDto;
import gov.mt.wris.dtos.PurposesSearchPageDto;
import gov.mt.wris.dtos.PurposesSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WaterRightVersionPurposeCreationDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.*;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.repositories.PeriodOfUseRepository;
import gov.mt.wris.repositories.PlaceOfUseRepository;
import gov.mt.wris.repositories.PurposeIrrigationXrefRepository;
import gov.mt.wris.repositories.PurposeRepository;
import gov.mt.wris.repositories.ReservoirRepository;
import gov.mt.wris.repositories.RetiredPlaceOfUseRepository;
import gov.mt.wris.repositories.VersionRemarkRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.InquiryService;
import gov.mt.wris.services.LegalLandService;
import gov.mt.wris.services.PurposeService;
import gov.mt.wris.services.PurposeVolumeCalculationService;
import gov.mt.wris.utils.Helpers;

enum PlaceOfUseStatus {
    ACTIVE, RETIRED
}

@Service
public class PurposeServiceImpl implements PurposeService {

    private static Logger LOGGER = LoggerFactory.getLogger(PurposeServiceImpl.class);

    public static List<String> calcVolTypes =
            Arrays.asList(
                    Constants.PURPOSE_TYPE_CODE_DOMESTIC,
                    Constants.PURPOSE_TYPE_CODE_MULTIPLE_DOMESTIC,
                    Constants.PURPOSE_TYPE_CODE_STOCK,
                    Constants.PURPOSE_TYPE_CODE_LAWN_GARDEN,
                    Constants.PURPOSE_TYPE_CODE_IRRIGATION
            );

    private List<String> testVersionTypes = new ArrayList<>(Arrays.asList("CHAU","REDU","REDX","CHSP"));

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private PlaceOfUseRepository placeOfUseRepository;

    @Autowired
    private RetiredPlaceOfUseRepository retiredPlaceOfUseRepository;

    @Autowired
    private PeriodOfUseRepository periodOfUseRepository;

    @Autowired
    private ReservoirRepository reservoirRepository;

    @Autowired
    private PurposeIrrigationXrefRepository purposeIrrigationXrefRepository;

    @Autowired
    private InquiryService inquiryService;


    @Autowired
    private PurposeVolumeCalculationService purposeVolumeCalculationService;

    @Autowired
    private WaterRightVersionRepository waterRightVersionRepository;

    @Autowired
    private VersionRemarkRepository versionRemarkRepository;

    @Autowired
    private LegalLandService legalLandService;

    @Autowired
    private MasterStaffIndexesRepository generalStaffRepo;

        public PurposesSearchPageDto searchPurposes(Integer pageNumber, Integer pageSize, PurposesSortColumn sortColumn, SortDirection sortDirection, PurposeSearchType purposeSearchType, String basin, String waterRightNumber, String waterRightType, String ext, String versionType, String versionNumber) {

        LOGGER.info("Search Purposes");

        Sort sortDtoColumn = getPurposesSortColumn(sortColumn, sortDirection, purposeSearchType);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<Purpose> results = purposeRepository.searchPurposes(request, basin, waterRightNumber, waterRightType, ext, versionType, versionNumber);

        PurposesSearchPageDto page = new PurposesSearchPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return purposeDtoLoader(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if (basin != null) filters.put("basin", basin);
        if (waterRightNumber != null) filters.put("waterRightNumber", waterRightNumber);
        if (waterRightType != null) filters.put("waterRightType", waterRightType);
        if (versionType != null) filters.put("versionType", versionType);
        if (versionNumber != null) filters.put("versionNumber", versionNumber);
        page.setFilters(filters);
        return page;

    }

    private Sort getPurposesSortColumn(PurposesSortColumn sortColumn, SortDirection sortDirection, PurposeSearchType purposeSearchType) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        switch (sortColumn) {
            case COMPLETEWATERRIGHTNUMBER:
                orders.add(new Sort.Order(direction, "v.waterRight.basin"));
                orders.add(new Sort.Order(direction, "v.waterRight.waterRightNumber"));
                orders.add(new Sort.Order(direction, "v.waterRight.ext"));
                break;
            case WATERRIGHTTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "v.waterRight.waterRightTypeCode"));
                break;
            case COMPLETEWATERRIGHTVERSION:
                orders.add(new Sort.Order(direction, "v.typeReference.meaning"));
                orders.add(new Sort.Order(direction, "v.version"));
                orders.add(new Sort.Order(direction, "v.versionStatus.description"));
                break;
            case PURPOSEDESCRIPTION:
            case COMPLETEPURPOSECODE:
                orders.add(new Sort.Order(direction, "t.description"));
                break;
            case ELEMENTORIGINDESCRIPTION:
                orders.add(new Sort.Order(direction, "r.meaning"));
                break;
            case CLIMATICCODEDESCRIPTION:
                orders.add(new Sort.Order(direction, "climaticArea.description"));
                break;
            case CLARIFICATION:
                orders.add(new Sort.Order(direction, "purposeClarification"));
                break;
            case PURPOSEVOLUME:
                orders.add(new Sort.Order(direction, "volumeAmount"));
                break;
        }
        /* Secondary - COMPLETEWATERRIGHTNUMBER */
        if (purposeSearchType == PurposeSearchType.PURPOSES) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "v.waterRight.basin"));
            orders.add(new Sort.Order(Sort.Direction.ASC, "v.waterRight.waterRightNumber"));
            orders.add(new Sort.Order(Sort.Direction.ASC, "v.waterRight.ext"));
        } else if (purposeSearchType == PurposeSearchType.WATERRIGHTVERSION) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "t.description"));
        }

        Sort fullSort = Sort.by(orders);
        return fullSort;
    }

    private PurposeDto purposeDtoLoader(Purpose model) {

        PurposeDto dto = new PurposeDto();
        dto.setPurposeId(model.getPurposeId().longValue());
        dto.setClarification(model.getPurposeClarification());
        dto.setPurposeVolume(model.getVolumeAmount());
        dto.setWaterRightId(model.getWaterRightId().longValue());
        dto.setVersionId(model.getVersionId().longValue());
        dto.setCompleteWaterRightNumber(
                Helpers.buildCompleteWaterRightNumber(
                        model.getWaterRightVersion().getWaterRight().getBasin(),
                        model.getWaterRightVersion().getWaterRight().getWaterRightNumber().toString(),
                        model.getWaterRightVersion().getWaterRight().getExt()
                )
        );
        if (model.getClimaticArea() != null) {
            dto.setClimaticCode(model.getClimaticArea().getCode());
            dto.setClimaticCodeDescription(model.getClimaticArea().getDescription());
        }
        if (model.getPurposeTypeCode().equals(Constants.PURPOSE_TYPE_CODE_IRRIGATION) && model.getPurposeIrrigationXrefs() != null && model.getPurposeIrrigationXrefs().size() > 0) {
            dto.setCompletePurposeCode(
                    String.format("%s - %s",
                            model.getPurposeType().getDescription(),
                            (model.getPurposeIrrigationXrefs().get(0).getIrrigationType().getDescription())
                    )
            );
        } else {
            dto.setCompletePurposeCode(model.getPurposeType().getDescription());
        }
        if (model.getWaterRightVersion().getWaterRight() != null) {
            dto.setWaterRightTypeCode(model.getWaterRightVersion().getWaterRight().getWaterRightType().getCode());
            dto.setWaterRightTypeDescription(model.getWaterRightVersion().getWaterRight().getWaterRightType().getDescription());
        }
        dto.setCompleteWaterRightVersion(
                Helpers.buildCompleteWaterRightVersion(
                        model.getWaterRightVersion().getTypeReference().getMeaning(),
                        model.getVersionId().toString(),
                        model.getWaterRightVersion().getVersionStatus() != null ? model.getWaterRightVersion().getVersionStatus().getDescription() : ""
                )
        );
        dto.setPurposeCode(model.getPurposeTypeCode());
        if (model.getPurposeType() != null)
            dto.setPurposeDescription(model.getPurposeType().getDescription());
        dto.setElementOrigin(model.getElementOrigin());
        if (model.getElementOriginReference() != null)
            dto.setElementOriginDescription(model.getElementOriginReference().getMeaning());
        return dto;

    }

    public PurposeDetailDto getPurpose(BigDecimal purposeId) {

        LOGGER.info("Get Purpose");

        Optional<Purpose> purpose = purposeRepository.getPurpose(purposeId);
        if (!purpose.isPresent())
            throw new NotFoundException(String.format("Purpose %s not found",purposeId));
        Optional<PurposeIrrigationXref> pix = purposeIrrigationXrefRepository.findOneByPurposeId(purposeId);
        PurposeIrrigationXref pixModel = null;
        if (pix.isPresent())
            pixModel = pix.get();
        Purpose model = purpose.get();
        PurposeDetailDto dto = purposeDetailDtoLoader(model, pixModel, null, null);
        dto.setReservoirCount(reservoirRepository.countReservoirByWaterRightIdAndVersion(model.getWaterRightId(), model.getVersionId()));
        return dto;

    }

    @Transactional
    public PurposeDetailDto updatePurpose(BigDecimal purposeId, PurposeUpdateDto dto) {

        LOGGER.info("Update purpose");

        Purpose oldPurpose = new Purpose();
        PurposeIrrigationXref irrigation = null;
        PurposeVolumeCalculation volumeCalculation = null;
        LocalDateTime priorityDate = null;
        List<String> additionalWarnings = new ArrayList<>();
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .appendPattern("MM-dd-yyyy")
                .optionalStart()
                .appendPattern(" HH:mm")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();

        Optional<Purpose> foundPurpose = purposeRepository.getPurpose(purposeId);
        if(!foundPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose id %s not found.", purposeId));
        oldPurpose = foundPurpose.get();
        WaterRightVersion version = oldPurpose.getWaterRightVersion();

        if (version.getPriorityDate() != null) {
            priorityDate = version.getPriorityDate();
        }

        irrigation = irrigationXrefMaintenance(oldPurpose, dto);
        if (irrigation !=  null) {
            List<PurposeIrrigationXref> xref = new ArrayList<>();
            xref.add(irrigation);
            oldPurpose.setPurposeIrrigationXrefs(xref);
        }
        oldPurpose.setPurposeTypeCode(dto.getPurposeCode());
        oldPurpose.setElementOrigin(dto.getPurposeOrigin() != null ? dto.getPurposeOrigin() : null);
        oldPurpose.setPurposeClarification(dto.getClarification());
        oldPurpose.setAnimalUnits(dto.getAnimalUnits());
        oldPurpose.setHousehold(dto.getHousehold());
        oldPurpose.setClimaticAreaCode(dto.getClimaticCode());
        oldPurpose.setCropRotation(dto.getRotation());
        oldPurpose.setModified(dto.getModifiedByThisChange());

        // Find existing periods
        List<PeriodOfUse> periods =  periodOfUseRepository.findDistinctAllByPurposeId(purposeId);

        if ((calcVolTypes.contains(dto.getPurposeCode())) &&
                (priorityDate != null && priorityDate.isAfter(LocalDateTime.parse(Constants.DNRC_THRESHOLD_PRIORITY_DATE, dtf))) &&
                (version.getWaterRight().getWaterRightType().getCode().equals(Constants.WATER_RIGHT_TYPE_GROUND_WATER_CERT))
        ) {

            if (dto.getPurposeVolume() != null) {
                /* user is sending their own volume */

                /* sum all the purpose volumes for this water right version including this override amount */
                BigDecimal total = purposeRepository.getTotalPurposeVolumeForWaterRightVersion(oldPurpose.getWaterRightId(), oldPurpose.getVersionId());
                total = total.add(dto.getPurposeVolume() != null ? dto.getPurposeVolume() : new BigDecimal(BigInteger.ZERO));
                if (oldPurpose.getVolumeAmount() != null) total = total.subtract(oldPurpose.getVolumeAmount());
                if (compareVolume(total, new BigDecimal(10)) > 0)
                    additionalWarnings.add(String.format("Volume is greater than 10 AF, calculated volume is %s", total));

                oldPurpose.setVolumeAmount(dto.getPurposeVolume());

            } else {
                /* user wants volume calculated */

                volumeCalculation = purposeVolumeCalculationService.calculateVolume(purposeId, dto.getPurposeCode(),
                        (dto.getHousehold() != null) ? dto.getHousehold() : 0, dto.getClimaticCode(), dto.getAnimalUnits(), periods);

                if (volumeCalculation.getMessages().isEmpty())  { /* no calculation errors */
                    oldPurpose.setVolumeAmount(volumeCalculation.getVolume());
                } else {
                    oldPurpose.setVolumeAmount(null);
                }

            }


        } else {
            if (compareVolume(oldPurpose.getVolumeAmount(), dto.getPurposeVolume())!=0)
                oldPurpose.setVolumeAmount(dto.getPurposeVolume());
        }

        if (attainableVolumeExceeded(version))
            additionalWarnings.add("Max Volume entered exceeds attainable volume based on flow rate & diversion period. " +
                    "Contact owner for clarification and/or enter individual purpose volumes and a VA remark. " +
                    "MaxFlowRate<=AttainableVol.");

        purposeRepository.saveAndFlush(oldPurpose);

        String program = version.getWaterRight().getWaterRightType().getProgram();

        if ("NA".equals(program)) {
            BigDecimal maxVolume = purposeRepository.getTotalPurposeVolumeForWaterRightVersion(version.getWaterRightId(), version.getVersion());
            waterRightVersionRepository.updateMaxVolume(version.getWaterRightId(), version.getVersion(), maxVolume);
        }

        PurposeDetailDto out = purposeDetailDtoLoader(oldPurpose, irrigation, volumeCalculation, additionalWarnings);
        return out;

    }

    private PurposeIrrigationXref irrigationXrefMaintenance(Purpose oldPurpose, PurposeUpdateDto dto) {

        PurposeIrrigationXref irrigation = null;

        // is change to irrigation type
        if (dto.getPurposeCode().equals(Constants.PURPOSE_TYPE_CODE_IRRIGATION)) {
            irrigation = new PurposeIrrigationXref();
            irrigation.setPurposeId(oldPurpose.getPurposeId());
            irrigation.setIrrigationTypeCode(dto.getIrrigationCode());
            if (oldPurpose.getPurposeIrrigationXrefs().size() == 0 || !oldPurpose.getPurposeIrrigationXrefs().get(0).getIrrigationTypeCode().equals(dto.getIrrigationCode())) {
                purposeIrrigationXrefRepository.deleteAllByPurposeId(oldPurpose.getPurposeId());
                purposeIrrigationXrefRepository.saveAndFlush(irrigation);
            }
            // is change from irrigation type
        } else if (oldPurpose.getPurposeTypeCode().equals(Constants.PURPOSE_TYPE_CODE_IRRIGATION)) {
            // Changing from irrigation purpose type to something else, delete *all* xref
            // records for this purpose...
            purposeIrrigationXrefRepository.deleteAllByPurposeId(oldPurpose.getPurposeId());
        }

        return irrigation;

    }

    private int compareVolume(BigDecimal vol1, BigDecimal vol2) {

        if (vol1 == null && vol2 == null) return 0;
        if (vol1 == null)
            return -1;
        else if (vol2 == null)
            return 1;
        return vol1.compareTo(vol2);

    }

    private PurposeDetailDto purposeDetailDtoLoader(Purpose model, PurposeIrrigationXref pixModel, PurposeVolumeCalculation volumeCalculation, List<String> additionalWarnings) {

        PurposeDetailDto dto = new PurposeDetailDto();
        dto.setPurposeId(model.getPurposeId().longValue());
        dto.setWaterRightId(model.getWaterRightId().longValue());
        dto.setVersionNumber(model.getVersionId().longValue());
        dto.setVersionTypeCode(model.getWaterRightVersion().getTypeCode());
        dto.setCanPrintDecreeReport(generalStaffRepo.hasRoles(Arrays.asList(Constants.PRINT_DECREE_REPORT)) > 0);
        dto.setPurposeCode(model.getPurposeTypeCode());
        if (model.getPurposeType() != null) {
            dto.setPurposeCodeDescription(model.getPurposeType().getDescription());
            dto.setCompletePurposeCode(model.getPurposeType().getDescription());
        }

        if (model.getExaminations() != null && model.getExaminations().size() > 0) {
            dto.setExaminationId(model.getExaminations().get(0).getExaminationId().longValue());
        }

        if (model.getWaterRightVersion() != null && model.getWaterRightVersion().getVersionCompacts() != null && model.getWaterRightVersion().getVersionCompacts().size() > 0) {
            dto.setVersionHasCompact(true);
        }

        dto.setCompleteWaterRightNumber(
                Helpers.buildCompleteWaterRightNumber(
                        model.getWaterRightVersion().getWaterRight().getBasin(),
                        model.getWaterRightVersion().getWaterRight().getWaterRightNumber().toString(),
                        model.getWaterRightVersion().getWaterRight().getExt()
                )
        );
        dto.setCompleteWaterRightVersion(
                Helpers.buildCompleteWaterRightVersion(
                        model.getWaterRightVersion().getTypeReference().getMeaning(),
                        model.getVersionId().toString(),
                        model.getWaterRightVersion().getVersionStatus() != null ? model.getWaterRightVersion().getVersionStatus().getDescription() : ""
                )
        );
        dto.setWaterRightTypeCode(model.getWaterRightVersion().getWaterRight().getWaterRightType().getCode());
        dto.setWaterRightTypeDescription(model.getWaterRightVersion().getWaterRight().getWaterRightType().getDescription());
        dto.setWaterRightStatusCode(model.getWaterRightVersion().getWaterRight().getWaterRightStatus().getCode());
        dto.setWaterRightStatusDescription(model.getWaterRightVersion().getWaterRight().getWaterRightStatus().getDescription());
        if (pixModel != null) {
            dto.setIrrigationCode(pixModel.getIrrigationTypeCode());
            dto.setIrrigationCodeDescription(pixModel.getIrrigationType()!=null?pixModel.getIrrigationType().getDescription():"");
            if (model.getPurposeTypeCode().equals(Constants.PURPOSE_TYPE_CODE_IRRIGATION)) {
                dto.setCompletePurposeCode(
                        String.format("%s - %s",
                                model.getPurposeType().getDescription(),
                                (pixModel.getIrrigationType()!=null?pixModel.getIrrigationType().getDescription():"")
                        )
                );
            }
        }
        if (model.getPurposeClarification() != null)
            dto.setClarification(model.getPurposeClarification());
        dto.setPurposeOrigin(model.getElementOrigin());
        if (model.getElementOriginReference()!= null)
            dto.setPurposeOriginDescription(model.getElementOriginReference().getMeaning());
        if (model.getVolumeAmount() != null)
            dto.setPurposeVolume(model.getVolumeAmount());
        if (model.getAnimalUnits() != null)
            dto.setAnimalUnits(model.getAnimalUnits());
        if (model.getHousehold() != null)
            dto.setHousehold(model.getHousehold());
        dto.setClimaticCode(model.getClimaticAreaCode());
        if (model.getClimaticArea() != null)
            dto.setClimaticCodeDescription(model.getClimaticArea().getDescription());
        dto.setRotation(model.getCropRotation());
        if (model.getCropRotationReference() != null)
            dto.setRotationDescription(model.getCropRotationReference().getMeaning());
        dto.setModifiedByThisChange(model.getModified());
        if (model.getModifiedReference() != null)
            dto.setModifiedByThisChangeDescription(model.getModifiedReference().getMeaning());
        if (model.getWaterRightVersion().getApplications().size() > 0)
            dto.setApplicationTypeCodes(getApplicationTypes(model));

            Map<String, Boolean> decreeFlags = inquiryService.isUneditable(model.getWaterRightId(), model.getVersionId(), model.getWaterRightVersion().getTypeCode());

            dto.setIsDecreed(decreeFlags.get("isDecreed"));
            dto.setIsVersionLocked(decreeFlags.get("isVersionLocked"));
            dto.setIsEditableIfDecreed(decreeFlags.get("isEditableIfDecreed"));
            dto.setCanReexamineDecree(decreeFlags.get("canReexamineDecree"));
            dto.setCanModifySplitDecree(decreeFlags.get("canModifySplitDecree"));

        // Volume calculation warnings
        if (volumeCalculation !=null && !volumeCalculation.getMessages().isEmpty()) {
            List<CalcVolWarningDto> warnings =
                    volumeCalculation.getMessages().stream()
                            .map(msg -> new CalcVolWarningDto().warning(msg))
                            .collect(Collectors.toList());
            dto.setCalcVolWarnings(warnings);
        }

        // Additional warning messages
        if (additionalWarnings != null && !additionalWarnings.isEmpty()) {
            if (dto.getCalcVolWarnings() != null) {
                List<CalcVolWarningDto> warnings = dto.getCalcVolWarnings();
                warnings.stream().sequential().collect(Collectors.toCollection(() -> additionalWarnings.stream()
                        .map(msg -> new CalcVolWarningDto().warning(msg))
                        .collect(Collectors.toList())
                ));
                dto.setCalcVolWarnings(warnings);
            } else {
                List<CalcVolWarningDto> warnings =
                        additionalWarnings.stream()
                                .map(msg -> new CalcVolWarningDto().warning(msg))
                                .collect(Collectors.toList());
                dto.setCalcVolWarnings(warnings);
            }
        }

        return dto;

    }

    private List<String> getApplicationTypes(Purpose model) {
        return model.getWaterRightVersion()
                .getApplications()
                .stream().map(application -> {
                    return application.getType().getCode();
                }).collect(Collectors.toList());
    }

    @Transactional
    public PurposeDetailDto createPurpose(BigDecimal waterRightId, BigDecimal versionId, WaterRightVersionPurposeCreationDto dto) {

        LOGGER.info("Create new Purpose");

        Purpose newPurpose = new Purpose();
        WaterRightVersion waterRightVersion = null;
        PurposeVolumeCalculation volumeCalculation = null;
        List<String> additionalWarnings = new ArrayList<>();
        LocalDateTime priorityDate = null;
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .appendPattern("MM-dd-yyyy")
                .optionalStart()
                .appendPattern(" HH:mm")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();

        newPurpose.setPurposeTypeCode(dto.getPurposeCode());
        newPurpose.setWaterRightId(waterRightId);
        newPurpose.setVersionId(versionId);
        newPurpose.setElementOrigin(dto.getPurposeOrigin());
        newPurpose.setAnimalUnits(dto.getAnimalUnits());
        newPurpose.setPurposeClarification(dto.getClarification());
        newPurpose.setHousehold(dto.getHousehold());
        newPurpose.setClimaticAreaCode(dto.getClimaticCode());
        newPurpose.setCropRotation(dto.getRotation());
        newPurpose.setModified(dto.getModifiedByThisChange());

        /* set volume if not special purpose type and amount was entered
        if ((!calcVolTypes.contains(dto.getPurposeCode()) && (dto.getPurposeVolume() != null))) {
            newPurpose.setVolumeAmount(dto.getPurposeVolume());
        }
        */
        Optional<WaterRightVersion> fndWaterRightVersion = waterRightVersionRepository.findByIdAndFetchWaterRight(waterRightId, versionId);
        if (fndWaterRightVersion.isPresent())
            waterRightVersion = fndWaterRightVersion.get();

        if (waterRightVersion.getPriorityDate()!=null) {
            priorityDate = waterRightVersion.getPriorityDate();
        }

        /* Purpose save needs to happen after retrieving water right version */
        Purpose purpose = purposeRepository.saveAndFlush(newPurpose);

        PurposeIrrigationXref irrigation = null;
        if (dto.getIrrigationCode() != null)
            irrigation = createIrrigation(dto.getIrrigationCode(), purpose);

        List<PeriodOfUse> periods = null;
        if (dto.getPeriodsOfUse() != null)
            periods = createPeriodOfUse(dto, purpose);

        List<PlaceOfUse> places = null;
        if (dto.getPlacesOfUse() != null)
            places = createPlaceOfUse(dto, purpose);

        /* these purpose types require special volume calculation handling */
        if ((calcVolTypes.contains(dto.getPurposeCode())) &&
                (priorityDate != null && priorityDate.isAfter(LocalDateTime.parse(Constants.DNRC_THRESHOLD_PRIORITY_DATE, dtf))) &&
                (waterRightVersion != null && waterRightVersion.getWaterRight().getWaterRightType().getCode().equals(Constants.WATER_RIGHT_TYPE_GROUND_WATER_CERT))
        ) {
            volumeCalculation = purposeVolumeCalculationService.calculateVolume(purpose.getPurposeId(), purpose.getPurposeTypeCode(),
                    (purpose.getHousehold() != null) ? purpose.getHousehold() : 0, purpose.getClimaticAreaCode(),
                    purpose.getAnimalUnits(),periods);

            if (volumeCalculation.getMessages().isEmpty())
                purpose.setVolumeAmount(volumeCalculation.getVolume());

        } else {

            purpose.setVolumeAmount(dto.getPurposeVolume());

        }
        purposeRepository.saveAndFlush(purpose);
        String program = waterRightVersion.getWaterRight().getWaterRightType().getProgram();
        if ("NA".equals(program)) {
            BigDecimal maxVolume = purposeRepository.getTotalPurposeVolumeForWaterRightVersion(waterRightId, versionId);
            waterRightVersionRepository.updateMaxVolume(waterRightId, versionId, maxVolume);
        }

        if (attainableVolumeExceeded(waterRightVersion))
            additionalWarnings.add("Max Volume entered exceeds attainable volume based on flow rate & diversion period. " +
                    "Contact owner for clarification and/or enter individual purpose volumes and a VA remark. " +
                    "MaxFlowRate<=AttainableVol.");

        PurposeDetailDto out = createPurposeDetailDtoLoader(purpose, irrigation, volumeCalculation, periods, places, additionalWarnings);
        return out;

    }

    @Transactional
    public void deletePurpose(BigDecimal purposeId) {
       LOGGER.info("Deleting a Purpose");

        Optional<Purpose> foundPurpose = purposeRepository.getPurpose(purposeId);
        if(!foundPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose id %s not found.", purposeId));

        Purpose purpose = foundPurpose.get();
        purposeRepository.delete(foundPurpose.get());

        String program = purpose.getWaterRightVersion().getWaterRight().getWaterRightType().getProgram();

        if ("NA".equals(program)) {
            BigDecimal maxVolume = purposeRepository.getTotalPurposeVolumeForWaterRightVersion(purpose.getWaterRightVersion().getWaterRightId(), purpose.getWaterRightVersion().getVersion());
            waterRightVersionRepository.updateMaxVolume(purpose.getWaterRightVersion().getWaterRightId(), purpose.getWaterRightVersion().getVersion(), maxVolume);
        }

    }

    private boolean attainableVolumeExceeded(WaterRightVersion waterRightVersion) {

        String type = waterRightVersion.getTypeCode();
        String program = waterRightVersion.getWaterRight().getWaterRightType().getProgram();
        int count = versionRemarkRepository.combinedPurposeVolumeRemarkCount(waterRightVersion.getWaterRightId(), waterRightVersion.getVersion());
        return ((count == 0 && "NA".equals(program)) || (!"NA".equals(program) && testVersionTypes.contains(type))) &&
                waterRightVersionRepository.testAttainableVolume(waterRightVersion.getWaterRightId(), waterRightVersion.getVersion())==1;

    }

    private PurposeIrrigationXref createIrrigation(String irrigationCode, Purpose purpose) {

        PurposeIrrigationXref xref = new PurposeIrrigationXref();
        xref.setIrrigationTypeCode(irrigationCode);
        xref.setPurposeId(purpose.getPurposeId());
        PurposeIrrigationXref savedXref = purposeIrrigationXrefRepository.saveAndFlush(xref);
        return savedXref;

    }

    private List<PeriodOfUse> createPeriodOfUse(WaterRightVersionPurposeCreationDto dto, Purpose purpose) {

        List<PeriodOfUse> list = new ArrayList<>();
        dto.getPeriodsOfUse().forEach(p-> {
            PeriodOfUse pou = new PeriodOfUse();
            pou.setPurposeId(purpose.getPurposeId());
            pou.setBeginDate(p.getBeginDate());
            pou.setEndDate(p.getEndDate());
            pou.setElementOrigin(purpose.getElementOrigin());
            if (p.getWaterRightId()!=null)
                pou.setWaterRightId(new BigDecimal(p.getWaterRightId()));
            if (p.getVersionId()!=null)
                pou.setVersionId(new BigDecimal(p.getVersionId()));
            if (p.getFlowRate() != null)
                pou.setFlowRate(p.getFlowRate());
            list.add(pou);
        });
        periodOfUseRepository.saveAll(list);
        return list;

    }

    private List<PlaceOfUse> createPlaceOfUse(WaterRightVersionPurposeCreationDto dto, Purpose purpose) {

        List<PlaceOfUse> list = new ArrayList<>();
        dto.getPlacesOfUse().forEach(p-> {
            PlaceOfUse pou = new PlaceOfUse();
            BigDecimal placeId = placeOfUseRepository.getNextPlaceOfUseId(purpose.getPurposeId());
            pou.setPlaceId(placeId);
            pou.setPurposeId(purpose.getPurposeId());
            if (p.getAcreage() != null)
                pou.setAcreage(p.getAcreage());
            pou.setElementOrigin(purpose.getElementOrigin());
            pou.setLegalLandDescriptionId(new BigDecimal(p.getLegalId()));
            pou.setCountyId(new BigDecimal(p.getCountyId()));
            pou.setModified(dto.getModifiedByThisChange());
            list.add(pou);
        });
        placeOfUseRepository.saveAll(list);
        return list;

    }

    private PurposeDetailDto createPurposeDetailDtoLoader(Purpose model, PurposeIrrigationXref pixModel, PurposeVolumeCalculation volumeCalculation, List<PeriodOfUse> periods, List<PlaceOfUse> places, List<String> additionalWarnings) {

        PurposeDetailDto dto = new PurposeDetailDto();
        dto.setPurposeId(model.getPurposeId().longValue());
        dto.setWaterRightId(model.getWaterRightId().longValue());
        dto.setVersionNumber(model.getVersionId().longValue());
        dto.setPurposeCode(model.getPurposeTypeCode());
        dto.setCompletePurposeCode(model.getPurposeTypeCode());

        if (pixModel != null) {
            dto.setIrrigationCode(pixModel.getIrrigationTypeCode());
            dto.setIrrigationCodeDescription(pixModel.getIrrigationType()!=null?pixModel.getIrrigationType().getDescription():"");
            if (model.getPurposeTypeCode().equals(Constants.PURPOSE_TYPE_CODE_IRRIGATION)) {
                dto.setCompletePurposeCode(
                        String.format("%s - %s",
                                (model.getPurposeType()!=null?model.getPurposeType().getDescription():""),
                                (pixModel.getIrrigationType()!=null?pixModel.getIrrigationType().getDescription():"")
                        )
                );
            }
        }

        dto.setClarification(model.getPurposeClarification());
        dto.setPurposeOrigin(model.getElementOrigin());
        dto.setAnimalUnits(model.getAnimalUnits());
        dto.setHousehold(model.getHousehold());
        dto.setClimaticCode(model.getClimaticAreaCode());
        dto.setRotation(model.getCropRotation());
        dto.setModifiedByThisChange(model.getModified());
        dto.setPurposeVolume(model.getVolumeAmount());

        // Volume calculation warnings
        if (volumeCalculation !=null && !volumeCalculation.getMessages().isEmpty()) {
            List<CalcVolWarningDto> warnings =
                    volumeCalculation.getMessages().stream()
                            .map(msg -> new CalcVolWarningDto().warning(msg))
                            .collect(Collectors.toList());
            dto.setCalcVolWarnings(warnings);
        }

        // Additional warning messages
        if (additionalWarnings != null && !additionalWarnings.isEmpty()) {
            if (dto.getCalcVolWarnings() != null) {
                List<CalcVolWarningDto> warnings = dto.getCalcVolWarnings();
                warnings.stream().sequential().collect(Collectors.toCollection(() -> additionalWarnings.stream()
                        .map(msg -> new CalcVolWarningDto().warning(msg))
                        .collect(Collectors.toList())
                ));
                dto.setCalcVolWarnings(warnings);
            } else {
                List<CalcVolWarningDto> warnings =
                        additionalWarnings.stream()
                                .map(msg -> new CalcVolWarningDto().warning(msg))
                                .collect(Collectors.toList());
                dto.setCalcVolWarnings(warnings);
            }
        }

        // Period of uses
        if (periods != null && !periods.isEmpty()) {
            List<PeriodOfUseDto> pouds = periods.stream().map(p ->
                    new PeriodOfUseDto()
                            .purposeId(p.getPurposeId().longValue())
                            .beginDate(p.getBeginDate())
                            .endDate(p.getEndDate())
                            .elementOrigin(p.getElementOrigin())
                            .elementOriginDescription(p.getElementOriginReference()!=null?p.getElementOriginReference().getMeaning():null)
                            .flowRate(p.getFlowRate()!=null?p.getFlowRate():null)
                            .periodId(p.getPeriodId()!=null?p.getPeriodId().longValue():null)
                            .waterRightId(p.getWaterRightId()!=null?p.getWaterRightId().longValue():null)
                            .versionId(p.getVersionId()!=null?p.getVersionId().longValue():null)
            ).collect(Collectors.toList());
            dto.setPeriodsOfUse(pouds);
        }

        // Place of uses
        if (places!=null && !places.isEmpty()) {
            List<PlaceOfUseDto> pouds = places.stream().map(p ->
                    new PlaceOfUseDto()
                            .purposeId(p.getPurposeId().longValue())
                            .legalId(p.getLegalLandDescriptionId().longValue())
                            .elementOrigin(p.getElementOrigin())
                            .elementOriginDescription(p.getElementOriginReference()!=null?p.getElementOriginReference().getMeaning():null)
                            .acreage(p.getAcreage()!=null?p.getAcreage():null)
                            .countyId(p.getCountyId()!=null?p.getCountyId().longValue():null)
                            .modifiedByThisChange(p.getModified()!=null?p.getModified():null)
                            .modifiedByThisChangeDescription(p.getModifiedReference()!=null?p.getModifiedReference().getMeaning():null)
                            .placeId(p.getPlaceId()!=null?p.getPlaceId().longValue():null)
            ).collect(Collectors.toList());
            dto.setPlacesOfUse(pouds);
        }

        return dto;

    }

    static PlaceOfUseDto getPlaceOfUseDto(PlaceOfUseSharedProps placeOfUse) {

        PlaceOfUseDto pouDto = new PlaceOfUseDto();
        pouDto.setAcreage(placeOfUse.getAcreage());
        pouDto.setElementOrigin(placeOfUse.getElementOrigin());
        if (placeOfUse.getElementOriginReference() != null)
            pouDto.setElementOriginDescription(placeOfUse.getElementOriginReference().getMeaning());
        if (placeOfUse.getLegalLandDescription() != null && placeOfUse.getCounty() != null)
            pouDto.setCompleteLegalLandDescription(Helpers.buildLegalLandDescription(placeOfUse.getLegalLandDescription(), placeOfUse.getCounty()));
        if(placeOfUse.getLegalLandDescription() != null) {
            LegalLandDescription land = placeOfUse.getLegalLandDescription();
            pouDto.setDescription320(land.getDescription320());
            pouDto.setDescription160(land.getDescription160());
            pouDto.setDescription80(land.getDescription80());
            pouDto.setDescription40(land.getDescription40());
            if(land.getGovernmentLot() != null) pouDto.setGovernmentLot(land.getGovernmentLot().longValue());
            TRS trs = land.getTrs();
            if(trs.getTownship() != null) pouDto.setTownship(trs.getTownship().longValue());
            pouDto.setTownshipDirection(trs.getTownshipDirection());
            if(trs.getRange() != null) pouDto.setRange(trs.getRange().longValue());
            pouDto.setRangeDirection(trs.getRangeDirection());
            if(trs.getSection() != null) pouDto.setSection(trs.getSection().longValue());
        }
        pouDto.setCountyId(placeOfUse.getCountyId().longValue());
        pouDto.setLegalId(placeOfUse.getLegalLandDescriptionId().longValue());
        if (placeOfUse.getModifiedReference() != null) {
            pouDto.setModifiedByThisChange(placeOfUse.getModifiedReference().getValue());
            pouDto.setModifiedByThisChange(placeOfUse.getModifiedReference().getMeaning());
        }
        return pouDto;

    }

    static Sort getPlaceOfUseSort(PlacesOfUseSortColumn column, SortDirection direction, PlaceOfUseStatus status) {

        Sort sort;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;
        String id = status == PlaceOfUseStatus.ACTIVE ? "placeId" : "retiredPlaceId";

        switch (column) {
            case ACREAGE:
                primary = Sort.by(sortOrderDirection, "acreage");
                secondary = Sort.by(Sort.Direction.ASC, id);
                sort = primary.and(secondary);
                break;
            case ELEMENTORIGINDESCRIPTION:
                primary = Sort.by(sortOrderDirection, "elementOriginReference.meaning");
                secondary = Sort.by(Sort.Direction.ASC, id);
                sort = primary.and(secondary);
                break;
            case MODIFIEDBYTHISCHANGEDESCRIPTION:
                primary = Sort.by(sortOrderDirection, "mod.meaning");
                secondary = Sort.by(Sort.Direction.ASC, id);
                sort = primary.and(secondary);
                break;
            case COMPLETELEGALLANDDESCRIPTION:
                primary = Sort.by(sortOrderDirection, "lld.governmentLot");
                primary = primary.and(Sort.by(sortOrderDirection, "lld.description320"));
                primary = primary.and(Sort.by(sortOrderDirection, "lld.description160"));
                primary = primary.and(Sort.by(sortOrderDirection, "lld.description80"));
                primary = primary.and(Sort.by(sortOrderDirection, "lld.description40"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.section"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.township"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.townshipDirection"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.range"));
                primary = primary.and(Sort.by(sortOrderDirection, "trs.rangeDirection"));
                primary = primary.and(Sort.by(sortOrderDirection, "c.name"));
                primary = primary.and(Sort.by(sortOrderDirection, "c.stateCode"));
                secondary = Sort.by(Sort.Direction.ASC, id);
                sort = primary.and(secondary);
                break;
            default:
                sort = Sort.by(sortOrderDirection, id);
        }
        return sort;

    }

}
