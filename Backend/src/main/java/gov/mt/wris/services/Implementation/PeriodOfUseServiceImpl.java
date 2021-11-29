package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.CopyDiversionToPeriodResultsDto;
import gov.mt.wris.dtos.PeriodOfUseCreationDto;
import gov.mt.wris.dtos.PeriodOfUseDto;
import gov.mt.wris.dtos.PeriodOfUseUpdateDto;
import gov.mt.wris.dtos.PeriodsOfUsePageDto;
import gov.mt.wris.dtos.PeriodsOfUseSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.WarningDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.PeriodOfUse;
import gov.mt.wris.models.Purpose;
import gov.mt.wris.models.PurposeVolumeCalculation;
import gov.mt.wris.repositories.PeriodOfUseRepository;
import gov.mt.wris.repositories.PurposeRepository;
import gov.mt.wris.services.PeriodOfUseService;
import gov.mt.wris.services.PurposeVolumeCalculationService;
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

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PeriodOfUseServiceImpl implements PeriodOfUseService {

    private static Logger LOGGER = LoggerFactory.getLogger(PeriodOfUseServiceImpl.class);

    @Autowired
    private PeriodOfUseRepository periodOfUseRepository;

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private PurposeVolumeCalculationService purposeVolumeCalculationService;

    public CopyDiversionToPeriodResultsDto copyFirstPeriodOfDiversionToPeriodOfUse(BigDecimal purposeId) {

        LOGGER.info("Copy first period of diversion to period of use");

        if (periodOfUseRepository.countAllByPurposeId(purposeId) > 0)
            throw new ValidationException("No Period of Use records can exist to perform the copy.");

        Optional<Purpose> fndPurpose = purposeRepository.getPurpose(purposeId);
        if (!fndPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose record %s not found", purposeId));

        List<WarningDto> warnings = new ArrayList<>();
        Purpose purpose = fndPurpose.get();
        int rc = periodOfUseRepository.copyPeriodOfDiversion(purposeId);
        if (rc != 0 && purpose.getPurposeTypeCode().equals(Constants.PURPOSE_TYPE_CODE_LAWN_GARDEN))
            warnings.add(new WarningDto().warning("Purpose is Lawn and Garden. April 1 to October 31 will be inserted."));
        else if (rc == 0)
            warnings.add(new WarningDto().warning("There are no period of diversions to copy."));

        List<PeriodOfUseDto> periods = null;
        if (rc > 0) {
            periods = periodOfUseRepository.findAllByPurposeId(purposeId).stream()
                    .map(p ->new PeriodOfUseDto()
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
        }

        CopyDiversionToPeriodResultsDto dto = new CopyDiversionToPeriodResultsDto();
        dto.setWarnings(warnings);
        dto.setPeriods(periods);
        return dto;

    }

    public PeriodsOfUsePageDto getPeriodsOfUse(int pageNumber, int pageSize, PeriodsOfUseSortColumn sortColumn, SortDirection sortDirection, Long purposeId) {

        LOGGER.info("Get Periods of Use");

        Sort sortDtoColumn = getPeriodsOfUseSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<PeriodOfUse> results = periodOfUseRepository.getPeriodsOfUse(request, new BigDecimal(purposeId));

        PeriodsOfUsePageDto page = new PeriodsOfUsePageDto();
        page.setResults(results.getContent().stream().map(p -> {

            return periodOfUseDtoLoader(p);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getPeriodsOfUseSortColumn(PeriodsOfUseSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        switch (sortColumn) {
            case PERIODBEGIN:
                orders.add(new Sort.Order(direction, "beginDate"));
                break;
            case PERIODEND:
                orders.add(new Sort.Order(direction, "endDate"));
                break;
            case ELEMENTORIGINDESCRIPTION:
                orders.add(new Sort.Order(direction, "eor.meaning"));
                break;
            case LEASEYEAR:
                orders.add(new Sort.Order(direction, "leaseYear"));
                break;
        }
        /* Secondary - BEGINDATE */
        orders.add(new Sort.Order(Sort.Direction.ASC, "beginDate"));
        Sort fullSort = Sort.by(orders);
        return fullSort;
    }

    private PeriodOfUseDto periodOfUseDtoLoader(PeriodOfUse model) {

        PeriodOfUseDto dto = new PeriodOfUseDto();
        dto.setElementOrigin(model.getElementOrigin());
        if (model.getElementOriginReference() != null)
            dto.setElementOriginDescription(model.getElementOriginReference().getMeaning());
        if (model.getWaterRightVersion() != null) {
            dto.setWaterRightId(model.getWaterRightVersion().getWaterRightId().longValue());
            dto.setVersionId(model.getWaterRightVersion().getVersion().longValue());
        }
        dto.setPurposeId(model.getPurposeId().longValue());
        dto.setPeriodId(model.getPeriodId().longValue());
        dto.setFlowRate(model.getFlowRate());
        dto.setEndDate(model.getEndDate());
        dto.setBeginDate(model.getBeginDate());
        dto.setLeaseYear(model.getLeaseYear());
        return dto;

    }

    public PeriodOfUseDto getPeriodOfUse(BigDecimal periodId) {

        LOGGER.info("Get Period Of Use");

        Optional<PeriodOfUse> fndPeriod = periodOfUseRepository.findPeriodOfUseByPeriodId(periodId);
        if (!fndPeriod.isPresent())
            throw new NotFoundException(String.format("Period Of Use %s not found",periodId));
        return periodOfUseDtoLoader(fndPeriod.get());

    }

    public PeriodOfUseDto updatePeriodOfUse(BigDecimal periodId, PeriodOfUseUpdateDto dto) {

        LOGGER.info("Update Period Of Use");

        Optional<PeriodOfUse> fndPeriod = periodOfUseRepository.findPeriodOfUseByPeriodId(periodId);
        if (!fndPeriod.isPresent())
            throw new NotFoundException(String.format("Period Of Use %s not found",periodId));
        PeriodOfUse oldPeriod = fndPeriod.get();
        if (dto.getBeginDate() != null)
            oldPeriod.setBeginDate(dto.getBeginDate());
        if (dto.getEndDate() != null)
            oldPeriod.setEndDate(dto.getEndDate());
        if (dto.getElementOrigin() != null)
            oldPeriod.setElementOrigin(dto.getElementOrigin());
        oldPeriod.setFlowRate(dto.getFlowRate());
        oldPeriod.setLeaseYear(dto.getLeaseYear());

        PeriodOfUse updatedPeriod = null;
        try {
            updatedPeriod = periodOfUseRepository.saveAndFlush(oldPeriod);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException sc = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("POUS_LSYR_CK")) {
                    throw new DataIntegrityViolationException(String.format("Invalid Lease Year '%s', value must be '1ST' or '2ND'.", dto.getLeaseYear()));
                }
            }
            throw e;
        }

        Purpose purpose = fndPeriod.get().getPurpose();
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
        }

        return periodOfUseDtoLoader(updatedPeriod);

    }

    public void deletePeriodOfUse(BigDecimal periodId) {

        LOGGER.info("Delete Period Of Use");

        periodOfUseRepository.deletePeriodOfUse(periodId);

    }

    public PeriodOfUseDto createPeriodOfUse(BigDecimal purposeId, PeriodOfUseCreationDto createDto) {

        LOGGER.info("Create Period Of Use");

        Optional<Purpose> foundPurpose = purposeRepository.getPurpose(purposeId);

        if(!foundPurpose.isPresent())
            throw new NotFoundException(String.format("Purpose id %s not found.", purposeId));

        PeriodOfUse pou = new PeriodOfUse();
        pou.setPurposeId(purposeId);
        pou.setBeginDate(createDto.getBeginDate());
        pou.setEndDate(createDto.getEndDate());
        pou.setElementOrigin(createDto.getElementOrigin());
        if (createDto.getWaterRightId()!=null)
            pou.setWaterRightId(new BigDecimal(createDto.getWaterRightId()));
        if (createDto.getVersionId()!=null)
            pou.setVersionId(new BigDecimal(createDto.getVersionId()));
        if (createDto.getFlowRate() != null)
            pou.setFlowRate(createDto.getFlowRate());
        if (createDto.getLeaseYear() != null)
            pou.setLeaseYear(createDto.getLeaseYear());

        PeriodOfUse newPeriod = periodOfUseRepository.saveAndFlush(pou);

        Purpose purpose = foundPurpose.get();
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
        }

        return periodOfUseDtoLoader(newPeriod);

    }


}
