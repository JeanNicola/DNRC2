package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.BatchUpdateException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ValidationException;

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
import org.springframework.transaction.annotation.Transactional;

import gov.mt.wris.dtos.AllPodsDto;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.EnforcementSortColumn;
import gov.mt.wris.dtos.FlowRateSummaryDto;
import gov.mt.wris.dtos.FlowRateSummaryDtoResults;
import gov.mt.wris.dtos.PeriodOfDiversionDto;
import gov.mt.wris.dtos.PeriodOfDiversionPageDto;
import gov.mt.wris.dtos.PeriodOfDiversionSortColumn;
import gov.mt.wris.dtos.PodAddressUpdateDto;
import gov.mt.wris.dtos.PodCopyDto;
import gov.mt.wris.dtos.PodCreationDto;
import gov.mt.wris.dtos.PodDetailsDto;
import gov.mt.wris.dtos.PodDetailsDtoResults;
import gov.mt.wris.dtos.PodDetailsUpdateDto;
import gov.mt.wris.dtos.PodDto;
import gov.mt.wris.dtos.PodEnforcementDto;
import gov.mt.wris.dtos.PodEnforcementsPageDto;
import gov.mt.wris.dtos.PodSourceUpdateDto;
import gov.mt.wris.dtos.PodsDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.SubdivisionUpdateDto;
import gov.mt.wris.dtos.VersionPodPageDto;
import gov.mt.wris.dtos.VersionPodSortColumn;
import gov.mt.wris.dtos.WellDataUpdateDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Address;
import gov.mt.wris.models.City;
import gov.mt.wris.models.County;
import gov.mt.wris.models.Ditch;
import gov.mt.wris.models.LegalLandDescription;
import gov.mt.wris.models.MeansOfDiversion;
import gov.mt.wris.models.MinorType;
import gov.mt.wris.models.PeriodOfUse;
import gov.mt.wris.models.PointOfDiversion;
import gov.mt.wris.models.PointOfDiversionEnforcement;
import gov.mt.wris.models.Reference;
import gov.mt.wris.models.Source;
import gov.mt.wris.models.SubdivisionCode;
import gov.mt.wris.models.TRS;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.models.ZipCode;
import gov.mt.wris.repositories.AddressRepository;
import gov.mt.wris.repositories.PeriodOfUseRepository;
import gov.mt.wris.repositories.PointOfDiversionEnforcementRepository;
import gov.mt.wris.repositories.PointOfDiversionRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.LegalLandService;
import gov.mt.wris.services.VersionPodService;

@Service
public class VersionPodServiceImpl implements VersionPodService {
    private static Logger LOGGER = LoggerFactory.getLogger(VersionPodService.class);

    @Autowired
    private PointOfDiversionRepository podRepository;

    @Autowired
    private LegalLandService legalLandService;

    @Autowired
    private WaterRightVersionRepository versionRepository;
    
    @Autowired
    private PeriodOfUseRepository periodOfUseRepository;

    @Autowired
    private PointOfDiversionEnforcementRepository enforcementRepository;

    @Autowired
    private AddressRepository addressRepository;

    public AllPodsDto getAllPods(Long waterRightId, Long versionNumber) {
        LOGGER.info("Get all the Point of Diversion attached to a Water Right Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);

        List<PointOfDiversion> results = podRepository.findAllWithLegalLandDescriptions(waterId, version);
        
        AllPodsDto dto = new AllPodsDto();

        dto.setResults(results.stream().map(pod -> 
            getPodDto(pod)
        ).collect(Collectors.toList()));

        return dto;
    }

    private static PodsDto getPodDto(PointOfDiversion pod) {
        PodsDto dto = new PodsDto();
        dto.setPodId(pod.getId().longValue());
        dto.setCountyId(pod.getCountyId().longValue());
        dto.setPodNumber(pod.getNumber().longValue());
        if(pod.getLegalLandDescription() != null) {
            LegalLandDescription legal = pod.getLegalLandDescription();
            dto.setLegalLandDescriptionId(pod.getLegalLandDescriptionId().longValue());
            dto.setDescription320(legal.getDescription320());
            dto.setDescription160(legal.getDescription160());
            dto.setDescription80(legal.getDescription80());
            dto.setDescription40(legal.getDescription40());
            if(legal.getGovernmentLot() != null) dto.setGovernmentLot(legal.getGovernmentLot().longValue());
            TRS trs = legal.getTrs();
            if(trs.getSection() != null) dto.setSection(trs.getSection().longValue());
            dto.setTownship(trs.getTownship().longValue());
            dto.setTownshipDirection(trs.getTownshipDirection());
            dto.setRange(trs.getRange().longValue());
            dto.setRangeDirection(trs.getRangeDirection());
        }
        return dto;
    }

    public VersionPodPageDto getVersionPods(Long waterRightId,
        Long versionNumber,
        int pagenumber,
        int pagesize,
        VersionPodSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Point of Diversions belonging to a Water Right Version");

        Sort sort = getPodSort(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal version = BigDecimal.valueOf(versionNumber);

        Page<PointOfDiversion> results = podRepository.findPointOfDiversions(pageable, waterId, version);

        VersionPodPageDto page = new VersionPodPageDto()
            .results(
                results.getContent().stream()
                .map(pod -> getVersionPodDto(pod))
                .collect(Collectors.toList())
            )
            .currentPage(results.getNumber() + 1)
            .pageSize(results.getSize())
            .totalElements(results.getTotalElements())
            .totalPages(results.getTotalPages())
            .sortColumn(sortColumn)
            .sortDirection(sortDirection);
        
        return page;
    }

    private static Sort getPodSort(VersionPodSortColumn sortColumn, SortDirection sortDirection) {
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;
        switch(sortColumn) {
            case PODNUMBER:
                sort = Sort.by(direction, "number");
                break;
            case MAJORTYPEDESCRIPTION:
                sort = Sort.by(direction, "mj.meaning");
                break;
            case MEANSOFDIVERSIONDESCRIPTION:
                sort = Sort.by(direction, "number");
                break;
            case LEGALLANDDESCRIPTION:
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
                sort = Sort.by(direction, "number");
        }
        sort.and(Sort.by(Sort.Direction.ASC, "id"));
        return sort;
    }

    private PodDto getVersionPodDto(PointOfDiversion pointOfDiversion) {
        PodDto dto = new PodDto();
        dto.setPodId(pointOfDiversion.getId().longValue());
        if(pointOfDiversion.getNumber() != null) dto.setPodNumber(pointOfDiversion.getNumber().longValue());
        LegalLandDescription legalLandDescription = pointOfDiversion.getLegalLandDescription();
        County county = pointOfDiversion.getCounty();
        dto.setLegalLandDescription(legalLandService.buildLegalLandDescription(legalLandDescription, county));
        MeansOfDiversion means = pointOfDiversion.getMeansOfDiversion();
        if(means != null) dto.setMeansOfDiversionDescription(means.getDescription());
        Reference majorType = pointOfDiversion.getMajorTypeReference();
        if(majorType != null) dto.setMajorTypeDescription(majorType.getMeaning());
        return dto;
    }

    public PodDto createVersionPod(Long waterRightId,
        Long versionNumber,
        PodCreationDto creationDto
    ) {
        try {
            return this._createVersionPod(waterRightId, versionNumber, creationDto);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("PODV_VERS_FK")) {
                    throw new NotFoundException("This Water Right Version does not exist");
                }
            }
            throw e;
        }
    }

    @Transactional
    public PodDto _createVersionPod(Long waterRightId,
        Long versionNumber,
        PodCreationDto creationDto
    ) {
        LOGGER.info("Creating a Point of Diversion");

        BigDecimal legalLandDescriptionId = BigDecimal.valueOf(
            legalLandService.getLegalLandDescriptionId(
                creationDto.getDescription320(), 
                creationDto.getDescription160(),
                creationDto.getDescription80(), 
                creationDto.getDescription40(), 
                creationDto.getGovernmentLot(), 
                creationDto.getTownship(), 
                creationDto.getTownshipDirection(), 
                creationDto.getRange(), 
                creationDto.getRangeDirection(), 
                creationDto.getSection(), 
                creationDto.getCountyId()
            )
        );

        PointOfDiversion pod = new PointOfDiversion();
        pod.setLegalLandDescriptionId(legalLandDescriptionId);
        pod.setCountyId(BigDecimal.valueOf(creationDto.getCountyId()));
        pod.setSourceOriginCode(creationDto.getSourceOriginCode());
        pod.setUnnamedTributary(creationDto.getUnnamedTributary() ? "Y" : "N");
        if(creationDto.getSourceId() != null) pod.setSourceId(BigDecimal.valueOf(creationDto.getSourceId()));
        pod.setMajorTypeCode(creationDto.getMajorTypeCode());
        pod.setMeansCode(creationDto.getMeansOfDiversionCode());
        pod.setTypeCode(creationDto.getPodTypeCode());
        pod.setWaterRightId(BigDecimal.valueOf(waterRightId));
        pod.setVersionId(BigDecimal.valueOf(versionNumber));
        pod.setOriginCode(creationDto.getPodOriginCode());

        pod = podRepository.save(pod);

        podRepository.renumberPODs("SEQ", BigDecimal.valueOf(waterRightId), BigDecimal.valueOf(versionNumber));
        podRepository.renumberPODs("TRS", BigDecimal.valueOf(waterRightId), BigDecimal.valueOf(versionNumber));

        return getVersionPodDto(pod);
    }

    public PodDto copyVersionPod(Long waterRightId, Long versionNumber, Long podId, PodCopyDto copyDto) {
      LOGGER.info("Copying a Point of Diversion");

      BigDecimal id = BigDecimal.valueOf(podId);
      Optional<PointOfDiversion> foundPOD = podRepository.findFullPOD(id);
      if(!foundPOD.isPresent()) {
          throw new NotFoundException("This Point of Diversion does not exist");
      }

      PointOfDiversion existingPod = foundPOD.get();

      if (existingPod.waterRightId.compareTo(BigDecimal.valueOf(waterRightId)) != 0 || 
        existingPod.versionId.compareTo(BigDecimal.valueOf(versionNumber)) != 0) {
        throw new DataIntegrityViolationException("Water Right or Version do not match the POD");
      }

      BigDecimal newPodId;
      try {
        newPodId = this._copyVersionPod(existingPod);
      } catch(Exception e) {
        throw e;
      }

      PodDto newPodDto = new PodDto();
      newPodDto.setPodId(newPodId.longValue());
      return newPodDto; 
    }

    @Transactional
    public BigDecimal _copyVersionPod(PointOfDiversion existingPOD) {
      
      // Copy over the existing fields to the new PointOfDiversion
      PointOfDiversion newPOD = new PointOfDiversion()
        .setBlock(existingPOD.getBlock())
        .setCastingDiameter(existingPOD.getCastingDiameter())
        .setCountyId(existingPOD.getCountyId())
        .setDitchId(existingPOD.getDitchId())
        .setEnforcementAreaId(existingPOD.getEnforcementAreaId())
        .setEnforcementComment(existingPOD.getEnforcementComment())
        .setEnforcementNumber(existingPOD.getEnforcementNumber())
        .setFlowing(existingPOD.getFlowing())
        .setLegalLandDescriptionId(existingPOD.getLegalLandDescriptionId())
        .setLot(existingPOD.getLot())
        .setMajorTypeCode(existingPOD.getMajorTypeCode())
        .setMeansCode(existingPOD.getMeansCode())
        .setMinorTypeCode(existingPOD.getMinorTypeCode())
        .setModified(existingPOD.getModified())
        .setModifiedElementOrigin(existingPOD.getModifiedElementOrigin())
        .setNumber(existingPOD.getNumber())
        .setOriginCode(existingPOD.getOriginCode())
        .setPercentageOfReach(existingPOD.getPercentageOfReach())
        .setPodIdAft(existingPOD.getPodIdAft())
        .setPrcsSts(existingPOD.getPrcsSts())
        .setPreMjr(existingPOD.getPreMjr())
        .setPreMrtp(existingPOD.getPreMrtp())
        .setPreSourIdSeq(existingPOD.getPreSourIdSeq())
        .setPreUt(existingPOD.getPreUt())
        .setPumpSize(existingPOD.getPumpSize())
        .setReacIdSeq(existingPOD.getReacIdSeq())
        .setSourceId(existingPOD.getSourceId())
        .setSourceOriginCode(existingPOD.getSourceOriginCode())
        .setSubdivisionCode(existingPOD.getSubdivisionCode())
        .setSvtpIdSeq(existingPOD.getSvtpIdSeq())
        .setTestRate(existingPOD.getTestRate())
        .setTract(existingPOD.getTract())
        .setTransitory(existingPOD.getTransitory())
        .setTypeCode(existingPOD.getTypeCode())
        .setUnnamedTributary(existingPOD.getUnnamedTributary())
        .setVersionId(existingPOD.getVersionId())
        .setWaterLevel(existingPOD.getWaterLevel())
        .setWaterRightId(existingPOD.getWaterRightId())
        .setWaterTemp(existingPOD.getWaterTemp())
        .setWellDepth(existingPOD.getWellDepth())
        .setWrKey(existingPOD.getWrKey())
        .setXCoordinate(existingPOD.getXCoordinate())
        .setYCoordinate(existingPOD.getYCoordinate());

      newPOD = podRepository.save(newPOD);

      // Copy the address(es) from the original POD to the new one
      List<Address> addresses = addressRepository.findAllByPointOfDiversionId(existingPOD.id);

      for (Address address : addresses) {
        Address newAddress = new Address()
          .setAddressLine1(address.getAddressLine1())
          .setZipCodeId(address.getZipCodeId())
          .setPrimaryMail(address.getPrimaryMail())
          .setPointOfDiversionId(newPOD.id);

        // Insert new address
        addressRepository.save(newAddress);
      }

      podRepository.renumberPODs("SEQ", newPOD.waterRightId, newPOD.versionId);
      podRepository.renumberPODs("TRS", newPOD.waterRightId, newPOD.versionId);

      return newPOD.id;
    }

    public void deletePod(Long waterRightId,
        Long versionNumber,
        Long podId
    ) {
        LOGGER.info("Deleting a Point of Diversion");

        BigDecimal id = BigDecimal.valueOf(podId);

        try {
            podRepository.deleteById(id);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("POUS_PODV_FK")) {
                    throw new ValidationException("Delete All Periods of Diversion first");
                } else if(constraintMessage.contains("RESV_PODV_FK")) {
                    throw new ValidationException("Remove this POD ID from all Reservoirs first");
                } else if(constraintMessage.contains("WEPX_PODV_FK")) {
                    throw new ValidationException("Delete All Enforcements first");
                }
            }
            throw e;
        }

        podRepository.renumberPODs("SEQ", BigDecimal.valueOf(waterRightId), BigDecimal.valueOf(versionNumber));
        podRepository.renumberPODs("TRS", BigDecimal.valueOf(waterRightId), BigDecimal.valueOf(versionNumber));
    }

    public FlowRateSummaryDto getFlowRateSummary(Long waterRightId, Long versionNumber) {
        LOGGER.info("Getting the Flow Rate Summary for a Water Right Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepository.getVersionWithFlowRate(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This Water Right Version does not exist");
        }
        WaterRightVersion version = foundVersion.get();

        return getFlowRateSummaryDto(version);
    }

    private static FlowRateSummaryDto getFlowRateSummaryDto(WaterRightVersion version) {
        Double flowRate = version.getMaximumFlowRate() != null ? version.getMaximumFlowRate().doubleValue() : null;
        String unit = version.getFlowRateUnitReference() != null ? version.getFlowRateUnitReference().getMeaning() : null;
        DecimalFormat df = new DecimalFormat("########.##");
        return new FlowRateSummaryDto()
            .results(new FlowRateSummaryDtoResults()
                .flowRateDescription(version.getFlowRateDescription())
                .flowRateSummary(
                    Stream.of(
                        flowRate != null ? df.format(flowRate) : null,
                        unit
                    ).filter(s -> s != null)
                    .collect(Collectors.joining(" "))
                )
                .flowRateUnit(unit)
                .flowRate(flowRate)
                .originCode(version.getFlowRateOrigin())
                .originDescription(version.getFlowRateOriginReference().getMeaning())
            );
    }

    public FlowRateSummaryDto updateFlowRateSummary(Long waterRightId,
        Long versionNumber,
        FlowRateSummaryDtoResults updateDto
    ) {
        LOGGER.info("Updating the Flow Rate Summary for a Water Right Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepository.getVersionWithFlowRate(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This Water Right Version does not exist");
        }
        WaterRightVersion version = foundVersion.get();

        Double flowRate = updateDto.getFlowRate();
        version.setMaximumFlowRate(flowRate != null ? BigDecimal.valueOf(flowRate) : null);
        version.setFlowRateUnit(updateDto.getFlowRateUnit());
        version.setFlowRateOrigin(updateDto.getOriginCode());
        version.setFlowRateDescription(updateDto.getFlowRateDescription());

        versionRepository.save(version);

        return getFlowRateSummaryDto(version);
    }

    public PodDetailsDto getPodDetails(Long waterRightId, Long versionNumber, Long podId) {
        LOGGER.info("Getting the Details for a specific POD");

        BigDecimal id = BigDecimal.valueOf(podId);
        Optional<PointOfDiversion> foundPOD = podRepository.findFullPOD(id);
        if(!foundPOD.isPresent()) {
            throw new NotFoundException("This Point of Diversion does not exist");
        }
        PointOfDiversion pod = foundPOD.get();

        // POD Origin
        PodDetailsDtoResults results = new PodDetailsDtoResults()
            .podNumber(pod.getNumber().longValue())
            .podOriginCode(pod.getOriginCode())
            .podOriginDescription(pod.getOriginReference().getMeaning())
        // Means of Diversion
            .meansOfDiversionCode(pod.getMeansCode())
            .meansOfDiversionDescription(pod.getMeansOfDiversion().getDescription())
            .podTypeCode(pod.getTypeCode())
            .podTypeDescription(pod.getTypeReference().getMeaning());
        Ditch ditch =  pod.getDitch();
        if(ditch != null) {
            results.setDitchId(ditch.getId().longValue());
            results.setDiversionTypeCode(ditch.getDiversionTypeCode());
            results.setDiversionTypeDescription(ditch.getDiversionType().getDescription());
            results.setDitchName(ditch.getName());
        }
        results.setTransitory("Y".equals(pod.getTransitory()));

        // Legal Land Description
        LegalLandDescription legalLandDescription = pod.getLegalLandDescription();
        results.setLegalLandDescription(legalLandService.buildLegalLandDescription(legalLandDescription, pod.getCounty()));
        if(legalLandDescription != null) {
            results.setLegalLandDescriptionId(legalLandDescription.getId().longValue());
            results.setGovernmentLot(legalLandDescription.getGovernmentLot() != null ? legalLandDescription.getGovernmentLot().longValue() : null);
            results.setDescription40(legalLandDescription.getDescription40());
            results.setDescription80(legalLandDescription.getDescription80());
            results.setDescription160(legalLandDescription.getDescription160());
            results.setDescription320(legalLandDescription.getDescription320());
            TRS trs = legalLandDescription.getTrs();
            results.setSection(trs.getSection() != null ? trs.getSection().longValue() : null);
            results.setTownship(trs.getTownship().longValue());
            results.setTownshipDirection(trs.getTownshipDirection());
            results.setRange(trs.getRange().longValue());
            results.setRangeDirection(trs.getRangeDirection());
        }
        results.setCountyId(pod.getCountyId().longValue());
        results.setModified("Y".equals(pod.getModified()));
        results.setxCoordinate(pod.getXCoordinate() != null ? pod.getXCoordinate().doubleValue() : null);
        results.setyCoordinate(pod.getYCoordinate() != null ? pod.getYCoordinate().doubleValue() : null);

        // Source
        Reference sourceOrigin = pod.getSourceOriginReference();
        if(sourceOrigin != null) {
            results.setSourceOriginCode(sourceOrigin.getValue());
            results.setSourceOriginDescription(sourceOrigin.getMeaning());
        }
        results.setUnnamedTributary("Y".equals(pod.getUnnamedTributary()));

        Source source = pod.getSource();
        if(source != null && source.getSourceName() != null) {
            results.setSourceId(source.getId().longValue());
            results.setSourceName(
                Arrays.asList(
                    source.getSourceName().getName(),
                    source.getForkName()
                ).stream()
                .filter(name -> name != null)
                .collect(Collectors.joining(", "))
            );
        }
        results.setMajorTypeCode(pod.getMajorTypeCode());
        results.setMajorTypeDescription(pod.getMajorTypeReference().getMeaning());
        MinorType minorType = pod.getMinorType();
        if(minorType != null) {
            results.setMinorTypeCode(pod.getMinorTypeCode());
            results.setMinorTypeDescription(minorType.getDescription());
        }

        // Subdivision Info
        SubdivisionCode subdivision = pod.getSubdivision();
        if(subdivision != null) {
            results.setSubdivisionCode(subdivision.getCode());
            results.setDnrcName(subdivision.getDnrcName());
            results.setDorName(subdivision.getDorName());
        }
        results.setLot(pod.getLot());
        results.setBlock(pod.getBlock());
        results.setTract(pod.getTract());

        // Well Data
        results.setWellDepth(pod.getWellDepth());
        results.setStaticWaterLevel(pod.getWaterLevel());
        results.setCastingDiameter(pod.getCastingDiameter());
        results.setFlowing("Y".equals(pod.getFlowing()));
        results.setPumpSize(pod.getPumpSize());
        results.setWaterTemp(pod.getWaterTemp());
        results.setTestRate(pod.getTestRate());

        // Property Address
        List<Address> addresses = pod.getAddresses();
        if(!addresses.isEmpty()) {
            Address address = addresses.get(0);
            results.setAddressId(address.getAddressId().longValue());
            results.setAddressLine(address.getAddressLine1());
            ZipCode zipCode = address.getZipCode();
            if(zipCode != null) {
                results.setZipCodeId(zipCode.getZipCodeId().longValue());
                results.setZipCode(zipCode.getZipCode());
                City city = zipCode.getCity();
                results.setCityName(city.getCityName());
                results.setFullAddress(
                    String.join("\n", 
                        address.getAddressLine1() != null ? address.getAddressLine1() : "",
                        String.join(" ", 
                            city.getCityName(),
                            zipCode.getZipCode(),
                            address.getPlFour() != null ? address.getPlFour() : ""
                        ).trim()
                    )
                );
            } else {
                results.setFullAddress(results.getAddressLine());
            }
        }

        PodDetailsDto dto = new PodDetailsDto().results(results);

        return dto;
    }

    public void updatePodDetails(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodDetailsUpdateDto updateDto
    ) {
        LOGGER.info("Updating the POD Details");

        BigDecimal id = BigDecimal.valueOf(podId);
        Optional<PointOfDiversion> foundPod = podRepository.findById(id);
        if(!foundPod.isPresent()) {
            throw new NotFoundException("This Point of Diversion does not exist");
        }
        PointOfDiversion pod = foundPod.get();

        if(
            updateDto.getSection() == null ||
            updateDto.getTownship() == null ||
            updateDto.getTownshipDirection() == null ||
            updateDto.getRange() == null ||
            updateDto.getRangeDirection() == null
        ) {
            throw new NotFoundException("The Legal Land Description Section, Township, N/S, Range, E/W and County are required");
        }

        BigDecimal legalLandDescriptionId = BigDecimal.valueOf(
            legalLandService.getLegalLandDescriptionId(
                updateDto.getDescription320(), 
                updateDto.getDescription160(),
                updateDto.getDescription80(), 
                updateDto.getDescription40(), 
                updateDto.getGovernmentLot(), 
                updateDto.getTownship(), 
                updateDto.getTownshipDirection(), 
                updateDto.getRange(), 
                updateDto.getRangeDirection(), 
                updateDto.getSection(), 
                updateDto.getCountyId()
            )
        );
        
        pod.setLegalLandDescriptionId(legalLandDescriptionId);
        pod.setOriginCode(updateDto.getPodOriginCode());
        pod.setMeansCode(updateDto.getMeansOfDiversionCode());
        pod.setDitchId(updateDto.getDitchId() != null ? BigDecimal.valueOf(updateDto.getDitchId()) : null);
        pod.setTypeCode(updateDto.getPodTypeCode());
        pod.setXCoordinate(updateDto.getxCoordinate() != null ? BigDecimal.valueOf(updateDto.getxCoordinate()) : null);
        pod.setYCoordinate(updateDto.getyCoordinate() != null ? BigDecimal.valueOf(updateDto.getyCoordinate()) : null);
        pod.setModified(updateDto.getModified() != null && updateDto.getModified() ? "Y" : "N");
        pod.setCountyId(BigDecimal.valueOf(updateDto.getCountyId()));
        pod.setTransitory(updateDto.getTransitory() ? "Y" : "N");

        pod = podRepository.save(pod);
    }

    public void updatePodAddress(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodAddressUpdateDto updateDto
    ) {
        LOGGER.info("Updating a POD Address");

        BigDecimal id = BigDecimal.valueOf(podId);

        if(updateDto.getAddressLine() != null && updateDto.getZipCodeId() != null) {
            Boolean addressExists = addressRepository.existsByPointOfDiversionId(id);
            BigInteger zipId = BigInteger.valueOf(updateDto.getZipCodeId());
            try {
                //update
                if(addressExists) {
                    addressRepository.updateAddress(id, updateDto.getAddressLine(), zipId);
                // insert
                } else {
                    Address a = new Address();
                    a.setAddressLine1(updateDto.getAddressLine());
                    a.setZipCodeId(zipId);
                    a.setPointOfDiversionId(id);
                    a.setPrimaryMail("N");
                    addressRepository.save(a);
                }
            } catch(DataIntegrityViolationException e) {
                if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException
                ) {
                    BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                    String constraintMessage = be.getMessage();
                    if(constraintMessage.contains("ADDR_ZPCD_FK")) {
                        throw new NotFoundException("This Zip Code does not exist");
                    }
                }
                throw e;
            }
        // delete
        } else if (updateDto.getAddressLine() == null && updateDto.getZipCodeId() == null) {
            addressRepository.deleteByPointOfDiversionId(id);
        } else {
            throw new ValidationException("Either clear out the address line and the zip code or enter both");
        }
    }

    public void updateSubdivision(Long waterRightId,
        Long versionNumber,
        Long podId,
        SubdivisionUpdateDto updateDto
    ) {
        LOGGER.info("Updating a Point of Diversion's Subdivision Info");

        BigDecimal id = BigDecimal.valueOf(podId);
        Optional<PointOfDiversion> foundPOD = podRepository.findById(id);
        if(!foundPOD.isPresent()) {
            throw new NotFoundException("This Point of Diversion does not exist");
        }
        PointOfDiversion pod = foundPOD.get();

        pod.setSubdivisionCode(updateDto.getSubdivisionCode());
        pod.setLot(updateDto.getLot());
        pod.setBlock(updateDto.getBlock());
        pod.setTract(updateDto.getTract());

        podRepository.save(pod);
    }

    public void updateWellData(Long waterRightId,
        Long versionNumber,
        Long podId,
        WellDataUpdateDto updateDto
    ) {
        LOGGER.info("Updating the Well Data of a Point of Diversion");

        BigDecimal id = BigDecimal.valueOf(podId);
        Optional<PointOfDiversion> foundPOD = podRepository.findById(id);
        if(!foundPOD.isPresent()) {
            throw new NotFoundException("This Point of Diversion does not exist");
        }
        PointOfDiversion pod = foundPOD.get();

        pod.setWellDepth(updateDto.getWellDepth());
        pod.setWaterLevel(updateDto.getStaticWaterLevel());
        pod.setCastingDiameter(updateDto.getCastingDiameter());
        pod.setFlowing(updateDto.getFlowing() != null && updateDto.getFlowing() ? "Y" : "N");
        pod.setPumpSize(updateDto.getPumpSize());
        pod.setWaterTemp(updateDto.getWaterTemp());
        pod.setTestRate(updateDto.getTestRate());

        podRepository.save(pod);
    }
    
    public void updatePodSource(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodSourceUpdateDto updateDto
    ) {
        LOGGER.info("Updating the Point of Diversion Source");

        BigDecimal id = BigDecimal.valueOf(podId);
        Optional<PointOfDiversion> foundPod = podRepository.findById(id);
        if(!foundPod.isPresent()) {
            throw new NotFoundException("This Point of Diversion does not exist");
        }
        PointOfDiversion pod = foundPod.get();

        pod.setSourceOriginCode(updateDto.getSourceOriginCode());
        pod.setUnnamedTributary(updateDto.getUnnamedTributary() != null && updateDto.getUnnamedTributary() ? "Y" : "N");
        pod.setSourceId(updateDto.getSourceId() != null ? BigDecimal.valueOf(updateDto.getSourceId()) : null);
        pod.setMajorTypeCode(updateDto.getMajorTypeCode());
        pod.setMinorTypeCode(updateDto.getMinorTypeCode());

        podRepository.save(pod);
    }

    public PeriodOfDiversionPageDto getPeriodOfDiversions(Long waterRightId,
        Long versionNumber,
        Long podId,
        int pagenumber,
        int pagesize,
        PeriodOfDiversionSortColumn sortColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting the Period of Diversions of a POD");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepository.findById(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This Water Right Version does not exist");
        }
        WaterRightVersion version = foundVersion.get();

        Sort.Direction direction = sortDirection == DescSortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, getPeriodOfDiversionSortColumn(sortColumn)).and(Sort.by(Sort.Direction.ASC, "id"));
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        BigDecimal id = BigDecimal.valueOf(podId);

        Page<PeriodOfUse> results = periodOfUseRepository.findbyPodId(pageable, id);

        PeriodOfDiversionPageDto page = new PeriodOfDiversionPageDto();
        page.setResults(results.getContent().stream()
            .map(period -> getPeriodDto(version, period)
            ).collect(Collectors.toList())
        );

        page.setMaxFlowRate(version.getMaximumFlowRate() != null ? version.getMaximumFlowRate().doubleValue() : 0);

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());

        page.setTotalElements(results.getTotalElements());
        page.setTotalPages(results.getTotalPages());
        
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    private static String getPeriodOfDiversionSortColumn(PeriodOfDiversionSortColumn sortColumn) {
        switch(sortColumn) {
            case BEGINDATE:
                return "beginDateString";
            case ENDDATE:
                return "endDateString";
            case DIVERSIONORIGINDESCRIPTION:
                return "r.meaning";
            case FLOWRATESUMMARY:
                return "flowRate";
            default:
                return "id";
        }
    }

    private static PeriodOfDiversionDto getPeriodDto(WaterRightVersion version, PeriodOfUse periodOfUse) {
        DecimalFormat df = new DecimalFormat("########.##");
        Double flowRate = periodOfUse.getFlowRate() != null ? periodOfUse.getFlowRate().doubleValue() : null;
        String unit = version.getFlowRateUnit();
        return new PeriodOfDiversionDto()
            .periodId(periodOfUse.getPeriodId().longValue())
            .beginDate(periodOfUse.getBeginDate())
            .endDate(periodOfUse.getEndDate())
            .diversionOriginCode(periodOfUse.getElementOrigin())
            .diversionOriginDescription(periodOfUse.getElementOriginReference() != null ? periodOfUse.getElementOriginReference().getMeaning() : null)
            .flowRate(flowRate)
            .flowRateUnit(unit)
            .flowRateSummary(
                Stream.of(
                    flowRate != null ? df.format(flowRate) : null,
                    unit
                ).filter(s -> s != null)
                .collect(Collectors.joining(" "))
            );
    }

    public PeriodOfDiversionDto addPeriodOfDiversion(Long waterRightId,
        Long versionNumber,
        Long podId,
        PeriodOfDiversionDto creationDto
    ) {
        LOGGER.info("Adding a Period of Diversion to a POD");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepository.findWithWaterRight(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This Water Right Version does not exist");
        }
        WaterRightVersion version = foundVersion.get();

        validatePeriodOfDiversion(creationDto, version);

        PeriodOfUse period = getPeriodOfDiversion(podId, creationDto);

        periodOfUseRepository.save(period);

        return getPeriodDto(version, period);
    }

    private static PeriodOfUse getPeriodOfDiversion(long podId, PeriodOfDiversionDto dto) {
        BigDecimal id = BigDecimal.valueOf(podId);
        PeriodOfUse period = new PeriodOfUse();
        period.setBeginDate(dto.getBeginDate());
        period.setEndDate(dto.getEndDate());
        period.setElementOrigin(dto.getDiversionOriginCode());
        if(dto.getFlowRate() != null) period.setFlowRate(BigDecimal.valueOf(dto.getFlowRate()));
        period.setPodId(id);
        return period;
    }

    private static void validatePeriodOfDiversion(PeriodOfDiversionDto dto, WaterRightVersion version) {
        if(!"CMPT".equals(version.getWaterRight().getWaterRightTypeCode()) &&
            "CMPT".equals(dto.getDiversionOriginCode())
        ) {
            throw new ValidationException("Compacted may only be used as an element of origin when the Water Right Type is Compacted");
        }
    }

    public PeriodOfDiversionDto updatePeriodOfDiversion(Long waterRightId,
        Long versionNumber,
        Long podId,
        Long periodId,
        PeriodOfDiversionDto updateDto
    ) {
        LOGGER.info("Adding a Period of Diversion to a POD");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepository.findWithWaterRight(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This Water Right Version does not exist");
        }
        WaterRightVersion version = foundVersion.get();

        validatePeriodOfDiversion(updateDto, version);

        BigDecimal id = BigDecimal.valueOf(periodId);

        Optional<PeriodOfUse> foundPeriod = periodOfUseRepository.findById(id);
        if(!foundPeriod.isPresent()) {
            throw new NotFoundException("This Period of Diversion does not exist");
        }
        PeriodOfUse period = foundPeriod.get();

        period.setBeginDate(updateDto.getBeginDate());
        period.setEndDate(updateDto.getEndDate());
        period.setElementOrigin(updateDto.getDiversionOriginCode());
        period.setFlowRate(updateDto.getFlowRate() != null ? BigDecimal.valueOf(updateDto.getFlowRate()) : null);

        periodOfUseRepository.save(period);

        return getPeriodDto(version, period);
    }

    public void deletePeriodOfDiversion(Long waterRightid,
        Long versionNumber,
        Long podId,
        Long periodId
    ) {
        LOGGER.info("Deleting a Period of Diversion");

        BigDecimal id = BigDecimal.valueOf(periodId);

        periodOfUseRepository.deleteById(id);
    }

    public PodEnforcementsPageDto getEnforcements(Long waterRightId,
        Long versionNumber,
        Long podId,
        int pagenumber,
        int pagesize,
        EnforcementSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting the Enforcements for a POD");

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, getEnforcementSortColumn(sortColumn)).and(Sort.by(Sort.Direction.ASC, "enforcementId"));
        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, sort);

        BigDecimal id = BigDecimal.valueOf(podId);

        Page<PointOfDiversionEnforcement> results = enforcementRepository.findByPointOfDiversionId(pageable, id);

        PodEnforcementsPageDto page = new PodEnforcementsPageDto();
        page.setResults(results.getContent().stream()
            .map(period -> getPodEnforcementDto(period)
            ).collect(Collectors.toList())
        );

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());

        page.setTotalElements(results.getTotalElements());
        page.setTotalPages(results.getTotalPages());
        
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    private static String getEnforcementSortColumn(EnforcementSortColumn sortColumn) {
        switch(sortColumn) {
            case AREAID:
                return "enforcementId";
            case ENFORCEMENTNUMBER:
                return "enforcementNumber";
            case COMMENTS:
                return "comments";
            default:
                return "enforcementId";
        }
    }

    private static PodEnforcementDto getPodEnforcementDto(PointOfDiversionEnforcement enforcement) {
        return new PodEnforcementDto()
            .areaId(enforcement.getEnforcementId())
            .enforcementNumber(enforcement.getEnforcementNumber())
            .comments(enforcement.getComments());
    }

    public PodEnforcementDto addEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodEnforcementDto creationDto
    ) {
        LOGGER.info("Adding an Enforcement to a Water Right Version");

        BigDecimal id = BigDecimal.valueOf(podId);

        Optional<PointOfDiversionEnforcement> foundEnforcement = enforcementRepository.findByPointOfDiversionIdAndEnforcementIdAndEnforcementNumber(id, creationDto.getAreaId(), creationDto.getEnforcementNumber());
        if(foundEnforcement.isPresent()) {
            throw new ValidationException("Cannot have two Enforcements with the same Enf Area and Enf #");
        }

        PointOfDiversionEnforcement enforcement = getEnforcement(podId, creationDto);

        try {
            enforcement = enforcementRepository.save(enforcement);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("WEPX_ENFA_FK")) {
                    throw new NotFoundException(String.format("This Enforcement Area does not exist: %s", creationDto.getAreaId()));
                }
            }
            throw e;
        }

        return getPodEnforcementDto(enforcement);
    }

    @Transactional
    public PodEnforcementDto updateEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        String areaId,
        String enforcementNumber,
        PodEnforcementDto updateDto
    ) {
        LOGGER.info("Updating an Enforcement attached to a Water Right Version");

        BigDecimal id = BigDecimal.valueOf(podId);

        if(!updateDto.getAreaId().equals(areaId) || !updateDto.getEnforcementNumber().equals(enforcementNumber)) {
            Optional<PointOfDiversionEnforcement> foundEnforcement = enforcementRepository.findByPointOfDiversionIdAndEnforcementIdAndEnforcementNumber(id, updateDto.getAreaId(), updateDto.getEnforcementNumber());
            if(foundEnforcement.isPresent()) {
                throw new ValidationException("Cannot have two Enforcements with the same Enf Area and Enf #");
            }
        }

        enforcementRepository.deleteByPointOfDiversionIdAndEnforcementIdAndEnforcementNumber(id, areaId, enforcementNumber);
        PointOfDiversionEnforcement enforcement = getEnforcement(podId, updateDto);

        try {
            enforcement = enforcementRepository.save(enforcement);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException be = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = be.getMessage();
                if(constraintMessage.contains("WEPX_ENFA_FK")) {
                    throw new NotFoundException(String.format("This Enforcement Area does not exist: %s", updateDto.getAreaId()));
                }
            }
            throw e;
        }

        return getPodEnforcementDto(enforcement);
    }

    private static PointOfDiversionEnforcement getEnforcement(long podId, PodEnforcementDto dto) {
        PointOfDiversionEnforcement enforcement = new PointOfDiversionEnforcement();
        enforcement.setPointOfDiversionId(BigDecimal.valueOf(podId));
        enforcement.setEnforcementId(dto.getAreaId());
        enforcement.setEnforcementNumber(dto.getEnforcementNumber());
        enforcement.setComments(dto.getComments());
        return enforcement;
    }

    public void deleteEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        String areaId,
        String enforcementNumber
    ) {
        LOGGER.info("Removing an Enforcement from a POD");

        BigDecimal id = BigDecimal.valueOf(podId);

        enforcementRepository.deleteByPointOfDiversionIdAndEnforcementIdAndEnforcementNumber(id, areaId, enforcementNumber);
    }
}
