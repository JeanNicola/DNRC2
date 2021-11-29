package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.ReferenceDto;
import gov.mt.wris.dtos.VersionHistoricalChangesDto;
import gov.mt.wris.dtos.VersionHistoricalClaimFilingDto;
import gov.mt.wris.dtos.VersionHistoricalCourthouseFilingDto;
import gov.mt.wris.dtos.VersionHistoricalDto;
import gov.mt.wris.dtos.VersionHistoricalPriorityDateDto;
import gov.mt.wris.dtos.VersionHistoricalWithReferencesDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Reference;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.models.IdClasses.ReferenceId;
import gov.mt.wris.repositories.CountiesRepository;
import gov.mt.wris.repositories.ReferenceRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.VersionHistoricalService;

@Service
public class VersionHistoricalServiceImpl implements VersionHistoricalService {

    private static Logger LOGGER = LoggerFactory.getLogger(
        VersionHistoricalServiceImpl.class
    );

    @Autowired
    private WaterRightVersionRepository versionRepo;

    @Autowired
    private ReferenceRepository referenceRepo;

    @Autowired
    private CountiesRepository countyRepo;

    private static VersionHistoricalDto getHistoricalDto(
        WaterRightVersion version
    ) {
        return new VersionHistoricalDto()
            .priorityDate(version.getPriorityDate())
            .priorityDateOrigin(version.getPriorityDateOrigin())
            .priorityDateOriginMeaning(
                version.getPriorityDateOriginReference() != null
                    ? version.getPriorityDateOriginReference().getMeaning()
                    : null
            )
            .enforceableDate(version.getEnforceablePriorityDate())
            .adjudicationProcess(version.getAdjudicationProcess())
            .adjudicationProcessMeaning(
                version.getAdjudicationProcessReference() != null
                    ? version.getAdjudicationProcessReference().getMeaning()
                    : null
            )
            .flowRate(version.getChangeAuthorizationFlowRate())
            .flowRateUnit(version.getChangeAuthorizationFlowUnit())
            .flowRateUnitMeaning(
                version.getChangeAuthorizationFlowUnitReference() != null
                    ? version
                        .getChangeAuthorizationFlowUnitReference()
                        .getMeaning()
                    : null
            )
            .divertedVolume(version.getChangeAuthorizationDivertedVolume())
            .consumptiveVolume(
                version.getChangeAuthorizationConsumptiveVolume()
            )
            .dateReceived(version.getDateReceived())
            .lateDesignation(version.getLateDesignation())
            .feeReceived(version.getFeeReceived())
            .impliedClaim(version.getImpliedClaim())
            .exemptClaim(version.getExemptClaim())
            .countyId(version.getCounty() != null ? version.getCounty().getId().longValue() : null)
            .county(version.getCounty() != null ? version.getCounty().getName(): null)
            .caseNumber(version.getHistoricalCaseNumber())
            .filingDate(version.getHistoricalFilingDate())
            .rightType(version.getHistoricalRightType())
            .rightTypeMeaning(
                version.getHistoricalRightTypeReference() != null
                    ? version.getHistoricalRightTypeReference().getMeaning()
                    : null
            )
            .rightTypeOrigin(version.getHistoricalRightTypeOrigin())
            .rightTypeOriginMeaning(
                version.getHistoricalRightTypeOriginReference() != null
                    ? version
                        .getHistoricalRightTypeOriginReference()
                        .getMeaning()
                    : null
            )
            .decreeAppropriator(version.getHistoricalDecreeAppropriator())
            .source(version.getHistoricalSource())
            .decreedMonth(version.getHistoricalDecreeMonth())
            .decreedDay(version.getHistoricalDecreeDay())
            .decreedYear(version.getHistoricalDecreeYear())
            .minersInches(version.getHistoricalMinersInches())
            .flowDescription(version.getHistoricalFlowDescription());
    }

    private static ReferenceDto getReferenceDto(Reference reference) {
        return new ReferenceDto()
            .value(reference.getValue())
            .description(reference.getMeaning());
    }

    public VersionHistoricalWithReferencesDto getHistorical(
        Long waterRightId,
        Long versionId
    ) {
        LOGGER.info("Getting the Historical data on a Version");

        Optional<WaterRightVersion> versionOpt = versionRepo.findByIdWithCounty(
            BigDecimal.valueOf(waterRightId),
            BigDecimal.valueOf(versionId)
        );

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("Could not find Water Right Version");
        }

        return new VersionHistoricalWithReferencesDto()
            .record(getHistoricalDto(versionOpt.get()))
            .rightTypes(
                referenceRepo
                    .findAllByDomainOrderByMeaningAsc(
                        Constants.HISTORICAL_RIGHT_TYPE_DOMAIN
                    )
                    .stream()
                    .map(VersionHistoricalServiceImpl::getReferenceDto)
                    .collect(Collectors.toList())
            )
            .elementOrigins(
                referenceRepo
                    .findAllByDomainOrderByMeaningAsc(
                        Constants.OWNER_ORIGIN_DOMAIN
                    )
                    .stream()
                    .map(VersionHistoricalServiceImpl::getReferenceDto)
                    .collect(Collectors.toList())
            )
            .adjudicationProcesses(
                referenceRepo
                    .findAllByDomainOrderByMeaningAsc(
                        Constants.ADJUDICATION_PROCESS_DOMAIN
                    )
                    .stream()
                    .map(VersionHistoricalServiceImpl::getReferenceDto)
                    .collect(Collectors.toList())
            )
            .flowRateUnits(
                referenceRepo
                    .findAllByDomainOrderByMeaningAsc(
                        Constants.FLOW_RATE_FULL_UNIT_DOMAIN
                    )
                    .stream()
                    .map(VersionHistoricalServiceImpl::getReferenceDto)
                    .collect(Collectors.toList())
            );
    }

    public VersionHistoricalDto updatePriorityDate(
        Long waterRightId,
        Long versionId,
        VersionHistoricalPriorityDateDto update
    ) {
        LOGGER.info("Updating the Priority Date on a Version");

        final List<String> adjudicationWaterRightTypes = Arrays.asList(
            "CMPT",
            "HDRT",
            "IRRD",
            "ITSC",
            "NNAD",
            "PRDL",
            "RSCL",
            "STOC"
        );

        final List<String> adjudicationVersions = Arrays.asList(
            "ORIG",
            "POST",
            "SPLT",
            "SPPD",
            "REXM",
            "FINL"
        );

        Optional<WaterRightVersion> versionOpt = versionRepo.findByWaterRightIdAndVersionId(
            BigDecimal.valueOf(waterRightId),
            BigDecimal.valueOf(versionId)
        );

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("Could not find Water Right Version");
        }

        WaterRightVersion version = versionOpt.get();

        if (update.getPriorityDateOrigin() != null) {
            if (
                "CMPT".equals(version.getWaterRight().getWaterRightTypeCode()) ^
                "CMPT".equals(update.getPriorityDateOrigin())
            ) {
                throw new ValidationException(
                    "Origins on Compact Version types may only be Compacted"
                );
            }
        }

        if (
            update.getAdjudicationProcess() != null &&
            !update
                .getAdjudicationProcess()
                .equals(version.getAdjudicationProcess()) &&
            (
                !adjudicationWaterRightTypes.contains(
                    version.getWaterRight().getWaterRightTypeCode()
                ) ||
                !adjudicationVersions.contains(version.getTypeCode())
            )
        ) {
            throw new ValidationException(
                "Adjudication Process can only be set on adjudication related Water Rights"
            );
        }

        if (
            update.getPriorityDate() != null &&
            update.getPriorityDate().isAfter(LocalDateTime.now())
        ) {
            throw new ValidationException("Priority Date must be before today");
        }

        if (
            update.getEnforceableDate() != null &&
            update.getEnforceableDate().isAfter(LocalDateTime.now())
        ) {
            throw new ValidationException(
                "Enforceable Priority Date must be before today"
            );
        }

        if (
            update.getPriorityDate() != null &&
            update.getEnforceableDate() != null &&
            update.getPriorityDate().isAfter(update.getEnforceableDate())
        ) {
            throw new ValidationException(
                "Priority Date cannot be after the Enforceable Priority Date"
            );
        }

        if (
            update.getPriorityDateOrigin() == null ||
            !update
                .getPriorityDateOrigin()
                .equals(version.getPriorityDateOrigin())
        ) {
            version.setPriorityDateOriginReference(
                update.getPriorityDateOrigin() != null
                    ? referenceRepo
                        .findById(
                            new ReferenceId(
                                Constants.ELEMENT_ORIGIN_DOMAIN,
                                update.getPriorityDateOrigin()
                            )
                        )
                        .orElse(null)
                    : null
            );
        }

        if (
            update.getAdjudicationProcess() == null ||
            !update
                .getAdjudicationProcess()
                .equals(version.getAdjudicationProcess())
        ) {
            version.setAdjudicationProcessReference(
                update.getAdjudicationProcess() != null
                    ? referenceRepo
                        .findById(
                            new ReferenceId(
                                Constants.ADJUDICATION_PROCESS_DOMAIN,
                                update.getAdjudicationProcess()
                            )
                        )
                        .orElse(null)
                    : null
            );
        }

        /*
         The dates in this section have their seconds truncated because 
         something in the frontend is adding seconds for some reason.
         Since the user does not enter seconds in the front end, seconds are 
         not importsant to this field.

         NOTE: If seconds before important, this code needs to change.
        */ 
        version
            .setPriorityDate(update.getPriorityDate().truncatedTo(ChronoUnit.MINUTES))
            .setPriorityDateOrigin(update.getPriorityDateOrigin())
            .setEnforceablePriorityDate(update.getEnforceableDate().truncatedTo(ChronoUnit.MINUTES))
            .setAdjudicationProcess(update.getAdjudicationProcess());

        versionRepo.save(version);

        return getHistoricalDto(version);
    }

    public VersionHistoricalDto updateClaimFiling(
        Long waterRightId,
        Long versionId,
        VersionHistoricalClaimFilingDto update
    ) {
        LOGGER.info("Updating the Claim Filing on a Version");

        Optional<WaterRightVersion> versionOpt = versionRepo.findByIdWithCounty(
            BigDecimal.valueOf(waterRightId),
            BigDecimal.valueOf(versionId)
        );

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("Could not find Water Right Version");
        }

        WaterRightVersion version = versionOpt.get();

        if (Arrays.asList("CHAU", "CHSP", "REDU", "REDX", "ERSV").contains(version.getTypeCode())) {
            throw new ValidationException(
                "This Version does not allow for updating Claim Filing Information"
            );
        }

        version
            .setDateReceived(update.getDateReceived())
            .setLateDesignation(update.getLateDesignation())
            .setFeeReceived(update.getFeeReceived())
            .setImpliedClaim(update.getImpliedClaim())
            .setExemptClaim(update.getExemptClaim());

        versionRepo.save(version);

        return getHistoricalDto(version);
    }

    public VersionHistoricalDto updateCourthouseFiling(
        Long waterRightId,
        Long versionId,
        VersionHistoricalCourthouseFilingDto update
    ) {
        LOGGER.info("Updating the Courthouse Filing on a Version");

        final boolean isDecreed =
            update.getRightType() != null &&
            update.getRightType().equals("DECR");

        Optional<WaterRightVersion> versionOpt = versionRepo.findByIdWithCounty(
            BigDecimal.valueOf(waterRightId),
            BigDecimal.valueOf(versionId)
        );

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("Could not find Water Right Version");
        }

        WaterRightVersion version = versionOpt.get();

        final boolean canEditNonDecree = !Arrays
            .asList("CHAU", "CHSP", "REDU", "REDX", "ERSV")
            .contains(version.getTypeCode());

        if (canEditNonDecree) {
            if (
                update.getRightType() == null ||
                !update.getRightType().equals(version.getHistoricalRightType())
            ) {
                version.setHistoricalRightTypeReference(
                    update.getRightType() != null
                    ? referenceRepo
                    .findById(
                        new ReferenceId(
                            Constants.HISTORICAL_RIGHT_TYPE_DOMAIN,
                            update.getRightType()
                        )
                    )
                    .orElse(null)
                    : null
                );
            }

            if (
                update.getRightTypeOrigin() == null ||
                !isDecreed ||
                !update
                .getRightTypeOrigin()
                .equals(version.getHistoricalRightTypeOrigin())
            ) {
                version.setHistoricalRightTypeOriginReference(
                    update.getRightTypeOrigin() != null
                    ? referenceRepo
                    .findById(
                        new ReferenceId(
                            Constants.ELEMENT_ORIGIN_DOMAIN,
                            update.getRightTypeOrigin()
                        )
                    )
                    .orElse(null)
                    : null
                );
            }

            if (
                update.getCountyId() == null ||
                !isDecreed ||
                !update.getCountyId().equals(version.getCountyId())
            ) {
                version.setCounty(
                    update.getCountyId() != null
                    ? countyRepo
                    .findById(BigDecimal.valueOf(update.getCountyId()))
                    .orElse(null)
                    : null
                );
            }

            version
                .setHistoricalRightType(update.getRightType())
                .setHistoricalRightTypeOrigin(update.getRightTypeOrigin())
                .setHistoricalCaseNumber(update.getCaseNumber())
                .setHistoricalFilingDate(update.getFilingDate())
                .setCountyId(update.getCountyId());
        }

        version
            .setHistoricalDecreeAppropriator(
                isDecreed ? update.getDecreeAppropriator() : null
            )
            .setHistoricalSource(isDecreed ? update.getSource() : null)
            .setHistoricalDecreeMonth(
                isDecreed ? update.getDecreedMonth() : null
            )
            .setHistoricalDecreeDay(isDecreed ? update.getDecreedDay() : null)
            .setHistoricalDecreeYear(isDecreed ? update.getDecreedYear() : null)
            .setHistoricalMinersInches(
                isDecreed ? update.getMinersInches() : null
            )
            .setHistoricalFlowDescription(
                isDecreed ? update.getFlowDescription() : null
            );

        versionRepo.save(version);

        return getHistoricalDto(version);
    }

    public VersionHistoricalDto updateChanges(
        Long waterRightId,
        Long versionId,
        VersionHistoricalChangesDto update
    ) {
        LOGGER.info("Updating the Courthouse Filing on a Version");

        Optional<WaterRightVersion> versionOpt = versionRepo.findByIdWithCounty(
            BigDecimal.valueOf(waterRightId),
            BigDecimal.valueOf(versionId)
        );

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("Could not find Water Right Version");
        }

        if (update.getFlowRate() != null && update.getFlowRateUnit() == null) {
            throw new ValidationException(
                "Cannot set the Flow Rate without a Unit"
            );
        }

        WaterRightVersion version = versionOpt.get();

        if (!Arrays.asList("CHAU", "CHSP", "REDX").contains(version.getTypeCode())) {
            throw new ValidationException(
                "Cannot update Historical Changes on a non-authorized Version type"
            );
        }

        if (
            update.getFlowRateUnit() == null ||
            !update
                .getFlowRateUnit()
                .equals(version.getChangeAuthorizationFlowUnit())
        ) {
            version.setChangeAuthorizationFlowUnitReference(
                update.getFlowRateUnit() != null
                    ? referenceRepo
                        .findById(
                            new ReferenceId(
                                Constants.FLOW_RATE_FULL_UNIT_DOMAIN,
                                update.getFlowRateUnit()
                            )
                        )
                        .orElse(null)
                    : null
            );
        }

        version
            .setChangeAuthorizationFlowRate(update.getFlowRate())
            .setChangeAuthorizationFlowUnit(update.getFlowRateUnit())
            .setChangeAuthorizationDivertedVolume(update.getDivertedVolume())
            .setChangeAuthorizationConsumptiveVolume(
                update.getConsumptiveVolume()
            );

        versionRepo.save(version);

        return getHistoricalDto(version);
    }
}
