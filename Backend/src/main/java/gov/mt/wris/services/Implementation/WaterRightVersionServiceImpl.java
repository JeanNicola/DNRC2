package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.HelpDeskNeededException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.DecreeVersion;
import gov.mt.wris.models.IdClasses.VersionApplicationXrefId;
import gov.mt.wris.models.MasterStaffIndexes;
import gov.mt.wris.models.Reference;
import gov.mt.wris.models.RelatedRight;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightStatus;
import gov.mt.wris.models.WaterRightType;
import gov.mt.wris.models.WaterRightVersion;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.DecreeVersionXrefRepository;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.repositories.RelatedRightVerXrefRepository;
import gov.mt.wris.repositories.SystemVariableRepository;
import gov.mt.wris.repositories.VersionApplicationXrefRepository;
import gov.mt.wris.repositories.WaterRightRepository;
import gov.mt.wris.repositories.WaterRightStatusRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.InquiryService;
import gov.mt.wris.services.MasterStaffIndexesService;
import gov.mt.wris.services.WaterRightVersionService;
import gov.mt.wris.utils.Helpers;
import oracle.jdbc.OracleDatabaseException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WaterRightVersionServiceImpl implements WaterRightVersionService{
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightVersionService.class);

    @Autowired
    ApplicationRepository appRepo;

    @Autowired
    WaterRightVersionRepository versionRepo;

    @Autowired
    VersionApplicationXrefRepository versionXrefRepo;

    @Autowired
    RelatedRightVerXrefRepository relatedXrefRepo;

    @Autowired
    WaterRightStatusRepository statusRepository;

    @Autowired
    WaterRightRepository waterRepo;

    @Autowired
    DecreeVersionXrefRepository decreeRepo;

    @Autowired
    SystemVariableRepository systemRepo;

    @Autowired
    MasterStaffIndexesRepository staffRepo;

    @Autowired
    MasterStaffIndexesService staffService;

    @Autowired
    MasterStaffIndexesRepository generalStaffRepo;

    @Autowired
    InquiryService inquiryService;

    @Override
    public ApplicationWaterRightsPageDto getWaterRights(int pagenumber,
                                                        int pagesize,
                                                        ApplicationWaterRightSortColumn sortColumn,
                                                        DescSortDirection sortDirection,
                                                        Long applicationId) {
        LOGGER.info("Getting the Water Right versions for an application");

        Sort sortDtoColumn = getWaterRightSortColumn(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pagenumber -1, pagesize, sortDtoColumn);

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepo.getApplicationById(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("Application with id " + applicationId + "does not exist");
        }
        Application app = foundApp.get();

        Page<Object[]> resultsPage = versionRepo.getApplicationWaterRightVersions(pageable, appId);

        ApplicationWaterRightsPageDto waterRightPage = new ApplicationWaterRightsPageDto();

        waterRightPage.setResults(resultsPage.getContent().stream().map(waterRightVersion -> {
            WaterRightVersion version = (WaterRightVersion) waterRightVersion[0];
            ApplicationWaterRightDto dto = getWaterRightVersionDto(version, app.getTypeCode(), (int) waterRightVersion[1]);
            try {
                String scannedDocUrl = waterRepo.getScannedDocUrl(version.getWaterRightId(), version.getVersion());
                if (!"-1".equals(scannedDocUrl)) dto.setScannedUrl(scannedDocUrl);
            } catch (Exception e) {
                LOGGER.error(String.format("ERROR - for waterRightId: %s and version: %s", version.getWaterRightId(), version.getVersion()));
                LOGGER.error(e.getMessage());
            }
            return dto;
        }).collect(Collectors.toList()));

        waterRightPage.setCurrentPage(resultsPage.getNumber() + 1);
        waterRightPage.setPageSize(resultsPage.getSize());

        waterRightPage.setTotalPages(resultsPage.getTotalPages());
        waterRightPage.setTotalElements(resultsPage.getTotalElements());

        waterRightPage.setSortColumn(sortColumn);
        waterRightPage.setSortDirection(sortDirection);

        return waterRightPage;
    }

    private ApplicationWaterRightDto getWaterRightVersionDto(WaterRightVersion version, String applicationTypeCode, Integer versionCount) {
        ApplicationWaterRightDto dto = new ApplicationWaterRightDto();

        WaterRight waterRight = version.getWaterRight();
        dto.setId(waterRight.getWaterRightId().longValue());
        dto.setBasin(waterRight.getBasin());
        dto.setWaterRightNumber(waterRight.getWaterRightNumber().longValue());
        dto.setExt(waterRight.getExt());
        dto.setVersion(version.getVersion().longValue());
        dto.setScanned(version.getScanned());

        dto.setNumVersions(versionCount);

        WaterRightStatus waterRightStatus = waterRight.getWaterRightStatus();
        if(waterRightStatus != null) dto.setStatusDescription(waterRightStatus.getDescription());
        dto.setStatusCode(waterRight.getWaterRightStatusCode());

        WaterRightType type = waterRight.getWaterRightType();
        if(type != null) {
            dto.setTypeDescription(type.getDescription());
            dto.setTypeCode(type.getCode());
        }

        WaterRightStatus versionStatus = version.getVersionStatus();
        if(versionStatus != null) dto.setVersionStatusDescription(versionStatus.getDescription());
        dto.setVersionStatusCode(version.getStatusCode());

        Reference versionType = version.getTypeReference();
        if(versionType != null) {
            dto.setVersionTypeDescription(versionType.getMeaning());
            dto.setVersionTypeCode(versionType.getValue());
        }

        return dto;
    }

    private Sort getWaterRightSortColumn(ApplicationWaterRightSortColumn sortColumn, DescSortDirection sortDirection) {
        Sort.Direction direction = (sortDirection == DescSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        if(sortColumn == ApplicationWaterRightSortColumn.BASIN) {
            orders.add(new Sort.Order(direction, "wr.basin"));
        } else if (sortColumn == ApplicationWaterRightSortColumn.EXT) {
            orders.add(new Sort.Order(direction, "wr.ext"));
        } else if (sortColumn == ApplicationWaterRightSortColumn.WATERRIGHTNUMBER) {
            orders.add(new Sort.Order(direction, "wr.waterRightId"));
        } else if (sortColumn == ApplicationWaterRightSortColumn.STATUSDESCRIPTION) {
            orders.add(new Sort.Order(direction, "wrs.description"));
        } else if (sortColumn == ApplicationWaterRightSortColumn.VERSIONSTATUSDESCRIPTION) {
            orders.add(new Sort.Order(direction, "vs.description"));
        } else if (sortColumn == ApplicationWaterRightSortColumn.TYPEDESCRIPTION) {
            orders.add(new Sort.Order(direction, "wrt.description"));
        } else if (sortColumn == ApplicationWaterRightSortColumn.VERSION) {
            orders.add(new Sort.Order(direction, "wrv.version"));
        } else if (sortColumn == ApplicationWaterRightSortColumn.VERSIONTYPEDESCRIPTION) {
            orders.add(new Sort.Order(direction, "vt.meaning"));
        }
        orders.add(new Sort.Order(Sort.Direction.DESC, "waterRightId"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "version"));

        Sort fullSort = Sort.by(orders);
        return fullSort;
    }

    public ApplicationWaterRightDto addWaterRight(Long applicationId, ApplicationWaterRightCreationDto newWaterRight) {
        LOGGER.info("Adding a new Water Right");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepo.getApplicationById(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("Application with id " + applicationId + "does not exist");
        }
        Application app = foundApp.get();

        BigDecimal waterRightId = BigDecimal.valueOf(newWaterRight.getId());
        BigDecimal versionId = BigDecimal.valueOf(newWaterRight.getVersion());
        Optional<WaterRightVersion> foundWaterRight = versionRepo.findByWaterRightIdAndVersionId(waterRightId, versionId);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This water right and version don't exist");
        }
        WaterRightVersion version = foundWaterRight.get();

        if(versionRepo.existsByApplicationIdAndWaterRightIdAndVersionId(appId, waterRightId, versionId)) {
            throw new DataIntegrityViolationException("This water right and version already exist on this application");
        }

        app.addWaterRightVersion(version);
        appRepo.save(app);

        ApplicationWaterRightDto newVersion = getWaterRightVersionDto(version, app.getTypeCode(), null);

        return newVersion;
    }

    public WaterRightVersionPageDto searchWaterRights(int pagenumber, int pagesize, WaterRightVersionSortColumn sortColumn, DescSortDirection sortDirection, String basin, String waterRightNumber, String version) {
        LOGGER.info("Searching for water right versions");

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize);

        Page<WaterRightVersion> resultsPage = versionRepo.getWaterRightVersions(pageable, sortColumn, sortDirection, null, basin, waterRightNumber, version, null);

        WaterRightVersionPageDto waterPage = new WaterRightVersionPageDto();

        waterPage.setResults(resultsPage.getContent().stream().map(water -> {
            return getWaterRightSearchDto(water);
        }).collect(Collectors.toList()));

        waterPage.setCurrentPage(resultsPage.getNumber() + 1);
        waterPage.setPageSize(resultsPage.getSize());

        waterPage.setTotalPages(resultsPage.getTotalPages());
        waterPage.setTotalElements(resultsPage.getTotalElements());

        waterPage.setSortColumn(sortColumn);
        waterPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(basin != null) {
            filters.put("basin", basin);
        }
        if(waterRightNumber != null) {
            filters.put("waterRightNumber", waterRightNumber);
        }
        if(version != null) {
            filters.put("version", version);
        }
        waterPage.setFilters(filters);

        return waterPage;
    }

    private WaterRightVersionDto getWaterRightSearchDto(WaterRightVersion version) {

        WaterRightVersionDto dto = new WaterRightVersionDto();
        WaterRight water = version.getWaterRight();
        dto.setBasin(water.getBasin());
        dto.setWaterRightId(water.getWaterRightId().longValue());
        dto.setWaterRightNumber(water.getWaterRightNumber().longValue());
        dto.setExt(water.getExt());
        dto.setVersion(version.getVersion().longValue());
        WaterRightType type = water.getWaterRightType();
        if(type != null) dto.setWaterRightTypeDescription(type.getDescription());
        WaterRightStatus status = water.getWaterRightStatus();
        if(status != null) {
            dto.setWaterRightStatusCode(status.getCode());
            dto.setWaterRightStatusDescription(status.getDescription());
        }
        dto.setCompleteWaterRightNumber(
            Helpers.buildCompleteWaterRightNumber(
                version.getWaterRight().getBasin(),
                version.getWaterRight().getWaterRightNumber().toString(),
                version.getWaterRight().getExt()
            )
        );
        if (version.getTypeReference() != null) {
            dto.setCompleteVersion(String.format("%s %s", version.getTypeReference().getMeaning(), version.getVersion().toString()));
        } else {
            dto.setCompleteVersion(String.format("%s", version.getVersion().toString()));
        }
        dto.setPriorityDate(version.getPriorityDate());
        dto.setEnforceablePriorityDate(version.getEnforceablePriorityDate());
        if (version.getOperatingAuthority() != null)
            dto.setOperatingAuthority(version.getOperatingAuthority());
        dto.setVersionType(version.getTypeCode());
        if (version.getVersionStatus() != null)
            dto.setVersionStatusDescription(version.getVersionStatus().getDescription());
        if (version.getTypeReference() != null)
            dto.setVersionTypeDescription(version.getTypeReference().getMeaning());
        return dto;

    }

    public ApplicationWaterRightDto editWaterRight(Long applicationId, Long waterRightId, Long versionIdSeq, ApplicationWaterRightDto dto) {
        try {
            return _editWaterRight(applicationId, waterRightId, versionIdSeq, dto);
        } catch (DataIntegrityViolationException e) {
            // check that the statuses are correct
            if(
                e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("WRGT_WRST_FK")) {
                    throw new DataIntegrityViolationException("Enter valid Statuses");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

    @Transactional
    private ApplicationWaterRightDto _editWaterRight(Long applicationId, Long waterRightId, Long versionIdSeq, ApplicationWaterRightDto dto) {
        LOGGER.info("Editing an Application Water Right");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepo.getApplicationById(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("Application with id " + applicationId + "does not exist");
        }
        Application app = foundApp.get();

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionIdSeq);
        Optional<WaterRightVersion> foundWater = versionRepo.findByWaterRightIdAndVersionId(waterId, versionId);
        if(!foundWater.isPresent()) {
            throw new NotFoundException("Water Right and Version doesn't exist");
        }
        WaterRightVersion version= foundWater.get();
        WaterRight water = version.getWaterRight();

        WaterRightStatus versionStatus = version.getVersionStatus();
        WaterRightStatus status = water.getWaterRightStatus();
        List<String> sameStatus = Arrays.asList("606", "634", "635", "644");
        if(!dto.getStatusCode().equals(dto.getVersionStatusCode()) && !sameStatus.contains(app.getTypeCode()) && water.getVersions().size() == 1) {
            throw new DataConflictException("For this application, the statuses must be the same");
        }

        if(
            versionStatus != null && dto.getVersionStatusCode() != null && 
            !versionStatus.getCode().equals(dto.getVersionStatusCode())
        ) {
            if(!canEditVersionStatus(app.getTypeCode(), water)) {
                throw new DataConflictException("Not allowed to edit this Version Status");
            } else {
                version.setStatusCode(dto.getVersionStatusCode());
            }
        }

        List<String> statusCodes = statusRepository.findByType(water.getWaterRightType().getCode())
                                    .stream().map(s -> {
                                        return s.getCode();
                                    }).collect(Collectors.toList());
        if(!statusCodes.contains(dto.getVersionStatusCode())) {
            throw new DataConflictException(dto.getVersionStatusCode() + " is not a valid Version Status Code for this Water Right version");
        } else if(!statusCodes.contains(dto.getStatusCode())) {
            throw new DataConflictException(dto.getVersionStatusCode() + " is not a valid Status Code for this Water Right");
        }

        String message = null;
        if(
            status != null && dto.getStatusCode() != null && 
            !status.getCode().equals(dto.getStatusCode())
        ) {
            List<String> geocodeStatusCodes = Arrays.asList("DENY", "TERM", "RVKD", "CANC", "DISS");
            if(geocodeStatusCodes.contains(dto.getStatusCode())) {
                int ret = versionRepo.endDateWaterRightGeocodes(waterRightId);
                if(ret > 0) {
                    message = ret + " geocode(s) have been end dated.";
                } else {
                    message = ret + "No geocodes to end date!";
                }
            }
            if(!canEditWaterRightStatus(app.getTypeCode(), water)) {
                throw new DataConflictException("Cannot edit the Water Right Status for any on this application");
            } else {
                water.setWaterRightStatusCode(dto.getStatusCode());
            }
        }

        version = versionRepo.save(version);

        ApplicationWaterRightDto waterRightDto = getWaterRightVersionDto(version, app.getTypeCode(), null);
        waterRightDto.setMessage(message);

        return waterRightDto;
    }

    public boolean canEditWaterRightStatus(String applicationTypeCode, WaterRight water) {
        List<String> disabledList = Arrays.asList("102", "606", "607", "608", "610", "617", "618", "626", "630", "631", "634", "635", "638", "644");
        if(disabledList.contains(applicationTypeCode)) {
            return false;
        }
        return true;
    }

    public boolean canEditVersionStatus(String applicationTypeCode, WaterRight water) {
        List<String> disabledList = Arrays.asList("102", "607", "608", "610", "617", "618", "630", "631", "638");
        if(disabledList.contains(applicationTypeCode)) {
            return false;
        }
        return true;
    }

    public void deleteApplicationWaterRightVersion(Long applicationId, Long waterRightId, Long versionIdSeq) {
        LOGGER.info("Deleting a Water Right Version from an Application");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionIdSeq);
        Optional<WaterRightVersion> foundWater = versionRepo.findByWaterRightIdAndVersionId(waterId, versionId);
        if(!foundWater.isPresent()) {
            throw new NotFoundException("Water Right and Version doesn't exist");
        }
        WaterRightVersion version= foundWater.get();
        WaterRight water = version.getWaterRight();

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepo.getApplicationById(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("Application with id " + applicationId + "does not exist");
        }
        Application app = foundApp.get();

        if(app.getId().longValue() == water.getWaterRightNumber().longValue()) {
            throw new DataIntegrityViolationException("Cannot remove Water Rights generated via Auto-Complete");
        }

        VersionApplicationXrefId xrefId = new VersionApplicationXrefId(appId, versionId, waterId);

        try{
            versionXrefRepo.deleteById(xrefId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Water Right doesn't exist on this application");
        }
    }

    public RelatedRightsPageDto getVersionRelatedRights(
        int pageNumber,
        int pageSize,
        RelatedRightSortColumn sortColumn,
        SortDirection sortDirection,
        Long waterRightId,
        Long versionId
    ) {
        LOGGER.info("Getting the Related Rights for a Water Right version");

        String column = getRelatedRightSortColumn(sortColumn);

        Sort.Direction direction = sortDirection.getValue().equals("ASC")
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
            pageNumber - 1,
            pageSize,
            Sort
                .by(direction, column)
                .and(Sort.by(Sort.Direction.ASC, "relatedRightId"))
        );

        Optional<WaterRightVersion> waterRightVersion = versionRepo
            .findByWaterRightIdAndVersionId(
                BigDecimal.valueOf(waterRightId),
                BigDecimal.valueOf(versionId)
            );

        if (!waterRightVersion.isPresent()) {
            throw new NotFoundException("This Water Right version does not exist");
        }

        Page<RelatedRight> results = relatedXrefRepo
            .findRelatedRightsByWaterRightIdAndVersionId(
                pageable,
                BigDecimal.valueOf(waterRightId),
                BigDecimal.valueOf(versionId)
            );

        return new RelatedRightsPageDto()
            .results(
                results
                    .getContent()
                    .stream()
                    .map(WaterRightVersionServiceImpl::getRelatedRightDto)
                    .collect(Collectors.toList())
            )
            .currentPage(results.getNumber() + 1)
            .pageSize(results.getSize())
            .totalPages(results.getTotalPages())
            .totalElements(results.getTotalElements())
            .sortColumn(sortColumn)
            .sortDirection(sortDirection);
    }

    private static RelatedRightDto getRelatedRightDto(RelatedRight related) {
        return new RelatedRightDto()
            .relatedRightId(related.getRelatedRightId().longValue())
            .relationshipType(related.getRelationshipType())
            .relationshipTypeVal(related.getRelationshipTypeVal().getMeaning())
            .maxFlowRate(related.getMaxFlowRate() != null ? related.getMaxFlowRate().longValue() : null)
            .flowRateUnit(related.getFlowRateUnit())
            .flowRateUnitVal(
                related.getFlowRateUnitVal() != null ? related.getFlowRateUnitVal().getMeaning() : null
            )
            .maxVolume(related.getMaxVolume())
            .maxAcres(related.getMaxAcres());
    }

    private static String getRelatedRightSortColumn(RelatedRightSortColumn column) {
        switch (column) {
            case RELATIONSHIPTYPE:
                return "relationshipType";
            case RELATIONSHIPTYPEVAL:
                return "relationshipTypeVal";
            case MAXFLOWRATE:
                return "maxFlowRate";
            case FLOWRATEUNIT:
                return "flowRateUnit";
            case FLOWRATEUNITVAL:
                return "flowRateUnitVal";
            case MAXACRES:
                return "maxAcres";
            case MAXVOLUME:
                return "maxVolume";
            default:
                return "relatedRightId";
        }
    }

    public VersionPageDto getVersions(int pagenumber,
        int pagesize,
        WaterRightVersionSortColumn sortColumn,
        SortDirection sortDirection,
        Long waterRightId,
        String versionNumber,
        String versionType
    ) {
        LOGGER.info("Getting the Water Right versions for an application");

        Pageable pageable = PageRequest.of(pagenumber -1, pagesize);

        DescSortDirection descSort = sortDirection == SortDirection.ASC ? DescSortDirection.ASC : DescSortDirection.DESC;
        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(waterId);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();

        Page<WaterRightVersion> resultsPage = versionRepo.getWaterRightVersions(pageable, sortColumn, descSort, waterRightId, null, null, versionNumber, versionType);

        VersionPageDto page = new VersionPageDto();

        page.setResults(resultsPage.getContent().stream().map(version -> {
            return getVersionDto(version);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        page.setAnyStandardsApplied(versionRepo.hasStandardsApplied(waterId));

        return page;
    }

    private VersionDto getVersionDto(WaterRightVersion version) {
        VersionDto dto = new VersionDto();

        dto.setWaterRightId(version.getWaterRightId().longValue());
        dto.setVersion(version.getVersion().longValue());
        dto.setScanned("Y".equals(version.getScanned()));
        dto.setOperatingAuthority(version.getOperatingAuthority());
        dto.setPriorityDate(version.getPriorityDate());
        dto.setEnforceablePriorityDate(version.getEnforceablePriorityDate());
        dto.setStandardsUpdated("Y".equals(version.getStandardsApplied()));
        String flowRate = version.getMaximumFlowRate() != null ? version.getMaximumFlowRate().toString() : "";
        String unitExtension = version.getFlowRateUnit() != null ? " " + version.getFlowRateUnit() : "";
        dto.setFlowRate(flowRate + unitExtension);
        if(version.getMaximumVolume() != null) dto.setVolume(version.getMaximumVolume().doubleValue());
        if(version.getMaximumAcres() != null) dto.setAcres(version.getMaximumAcres().doubleValue());

        WaterRightStatus versionStatus = version.getVersionStatus();
        if(versionStatus != null) {
            dto.setVersionStatusDescription(versionStatus.getDescription());
            dto.setVersionStatusCode(versionStatus.getCode());
        }

        Reference versionType = version.getTypeReference();
        if(versionType != null) {
            dto.setVersionTypeDescription(versionType.getMeaning());
            dto.setVersionTypeCode(versionType.getValue());
        }

        WaterRight waterRight = version.getWaterRight();
        if (waterRight != null && waterRight.getWaterRightType()!=null) {
            dto.setWaterRightTypeCode(version.getWaterRight().getWaterRightType().getCode());
            dto.setWaterRightTypeDescription(version.getWaterRight().getWaterRightType().getDescription());
        }

        if (waterRight != null && waterRight.getWaterRightStatus()!=null) {
            dto.setWaterRightStatusDescription(version.getWaterRight().getWaterRightStatus().getDescription());
            dto.setWaterRightStatusCode(version.getWaterRight().getWaterRightStatus().getCode());
        }

        if (version.getVersionStatus()!=null) {
            dto.setVersionStatusCode(version.getVersionStatus().getCode());
            dto.setVersionStatusDescription(version.getVersionStatus().getDescription());
        }

        return dto;
    }

    @Transactional
    public VersionDto createWaterRightVersion(Long waterRightId, VersionCreationDto createDto) {
        LOGGER.info("Creating a new Water Right Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(waterId);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();

        if(Arrays.asList("ADJ", "PR").contains(waterRight.getWaterRightType().getProgram())) {
            if(Arrays.asList("ORIG", "SPLT", "REXM", "SPPD", "POST").contains(createDto.getVersionTypeCode()) &&
                versionRepo.needsVersionDecree(waterId)
            ) {
                throw new DataIntegrityViolationException("A new " + createDto.getVersionTypeCode() + " version cannot be created because a Non-Decreed Reexam version was found. Contact the Adjudication Program to have the Reexam version deleted");
            }

            // check type and whether it's decree
            if (Arrays.asList("POST", "SPPD").contains(createDto.getVersionTypeCode())) {
                if(waterRepo.needsDecree(waterId)) {
                    throw new DataIntegrityViolationException("A Post Decree Version cannot be created until the Water Right has been Decreed");
                }
            }
        }

        if ("NFWP".equals(waterRight.getWaterRightTypeCode()) && !"CHAU".equals(createDto.getVersionTypeCode())) {
            throw new ValidationException("Non-File Water Project Versions can only have a Change Authorization Version type");
        }

        int version = versionRepo.createVersion(waterId, createDto.getVersionTypeCode());
        BigDecimal versionId = BigDecimal.valueOf(version);
        if(version < 0) {
            throw new DataIntegrityViolationException("Unable to create new Version");
        } else if(versionRepo.hasEnforcementAreas(waterId)) {
            LOGGER.info("Sending an enforcement area email");
            List<String> emails = systemRepo.findEmails();

            // check if in test database
            String environ = staffRepo.getDatabaseEnvironment();
            String dbEnv = environ.substring(0, environ.indexOf(".")).toUpperCase();
            String message = "";
            if(Arrays.asList("DNRDEV", "DNRTST", "DNRTRI", "TRIDEV").contains(dbEnv)) {
                message = "*** This is only a test ***\n-----------------------------\n";
            }

            // build message
            String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            MasterStaffIndexes staff = staffService.getLocationStaffInfo(username);
            message += staff.getFirstName() + " " + staff.getLastName() +
                        " has created a " + " version for Water Right " +
                        waterRight.getWaterRightNumber().toString() + " on " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + ".\n\n" +
                        "Database: " + dbEnv;
            // send emails
            if(emails.size() > 0) {
                Integer error = versionRepo.sendEmail(emails.get(0),
                                emails.size() > 1 ? emails.get(1): null,
                                emails.size() > 2 ? emails.get(2): null,
                                emails.size() > 3 ? emails.get(3): null,
                                "Post Decree - Enforcement Area Notification.",
                                "DNRWRDFR@mt.gov",
                                waterRight.getWaterRightNumber() + " is an enforcement action.",
                                message);
                if(error > 0) {
                    throw new HelpDeskNeededException("Error sending out emails. Try again later or contact the Help Desk.");
                }
            }
        }
        Optional<WaterRightVersion> foundVersion = versionRepo.findByWaterRightIdAndVersionId(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new DataIntegrityViolationException("Unable to create new Version");
        }
        WaterRightVersion v = foundVersion.get();
        return getVersionDto(v);
    }

    @Transactional
    public VersionDto createFirstVersion(Long waterRightId, FirstVersionCreationDto creationDto) {
        LOGGER.info("Creating the First Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(waterId);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();

        Integer versionId = versionRepo.getNextVersionId(waterId);
        WaterRightVersion version = new WaterRightVersion();
        version.setVersion(versionId != null ? BigDecimal.valueOf(versionId) : BigDecimal.ONE);
        version.setWaterRightId(waterId);
        version.setStatusCode(creationDto.getVersionStatusCode());
        version.setOperatingAuthority(creationDto.getOperatingAuthority());
        version.setTypeCode("ORIG");
        version.setFlowRateOrigin("ISSU");
        version.setVolumeOrigin("ISSU");
        version.setAcresOrigin("ISSU");

        version = versionRepo.save(version);

        if (!Arrays.asList("62GW", "EXEX", "GWCT", "PRPM", "TPRP", "STWP", "CDWR", "NAPP", "NFWP", "DMAL").contains(waterRight.getWaterRightTypeCode())) {
            waterRight.setWaterRightStatusCode(creationDto.getVersionStatusCode());
            waterRepo.save(waterRight);
        }

        return getVersionDto(version);
    }

    public void applyVersionStandards(Long waterRightId) {
        LOGGER.info("Applying Standards to Versions");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(waterId);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }

        if(versionRepo.hasStandardsApplied(waterId)) {
            throw new DataIntegrityViolationException("Standards has already been applied to one or more versions. Please uncheck all versions");
        }

        try {
            versionRepo.runStandards("S", "WRGT", null, waterId, null, null);
        } catch (JpaSystemException e) {
            if( e.getCause() instanceof GenericJDBCException &&
                e.getCause().getCause() instanceof SQLException &&
                e.getCause().getCause().getCause() instanceof OracleDatabaseException &&
                ((OracleDatabaseException) e.getCause().getCause().getCause()).getOracleErrorNumber() == 20996
            ) {
                throw new DataIntegrityViolationException("No Applicable Water Rights meet input criteria.");
            } else {
                throw e;
            }
        }
    }

    public VersionDto updateVersionStandards(Long waterRightId, Long versionNumber, VersionUpdateDto updateDto) {
        LOGGER.info("Updating a Water Right Version's Standards Updated");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepo.findByWaterRightIdAndVersionId(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This version does not exist on this water right");
        }
        WaterRightVersion version = foundVersion.get();

        if (!"Y".equals(version.getStandardsApplied()) && updateDto.getStandardsUpdated())
            throw new DataIntegrityViolationException("Standards Applied can only be unchecked, not checked");
        
        version.setStandardsApplied(updateDto.getStandardsUpdated() ? "Y" : "N");

        version = versionRepo.saveAndFlush(version);
        return getVersionDto(version);
    }

    @Transactional
    public VersionDto updateWaterRightVersion(Long waterRightId, Long versionNumber, VersionUpdateDto updateDto) {
        LOGGER.info("Updating a Water Right Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepo.findByWaterRightIdAndVersionId(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This version does not exist on this water right");
        }
        WaterRightVersion version = foundVersion.get();

        Map<String, Boolean> decreeFlags = inquiryService.isUneditable(waterId, versionId, version.getTypeCode());
        if (decreeFlags.get("isVersionLocked") && !decreeFlags.get("isEditableIfDecreed")) {
            throw new DataIntegrityViolationException("Not allowed to update this decreed version");
        }

        version.setStandardsApplied(updateDto.getStandardsUpdated() ? "Y" : "N");
        version.setTypeCode(updateDto.getVersionTypeCode());
        version.setStatusCode(updateDto.getVersionStatusCode());
        version.setOperatingAuthority(updateDto.getOperatingAuthority());

        version = versionRepo.saveAndFlush(version);
        return getVersionDto(version);

    }

    public void deleteVersion(Long waterRightId, Long version) {

        LOGGER.info("Delete Water Right Version");
        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(version);

        versionRepo.deleteByWaterRightIdAndVersion(waterId, versionId);
    }

    private List<String> getApplicationTypes(WaterRightVersion model) {
        return model.getApplications()
                .stream().map(application -> {
                    return application.getType().getCode();
                }).collect(Collectors.toList());
    }

    public VersionDetailDto getWaterRightVersionDetail(Long waterRightId, Long versionNumber) {

        LOGGER.info("Get Water Right Version detail");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);
        Optional<WaterRightVersion> foundVersion = versionRepo.findByWaterRightIdAndVersionId(waterId, versionId);
        if(!foundVersion.isPresent()) {
            throw new NotFoundException("This version does not exist on this water right");
        }

        VersionDetailDto dto = getVersionDetailDto(foundVersion.get());
        dto.setCanPrintDecreeReport(generalStaffRepo.hasRoles(Arrays.asList(Constants.PRINT_DECREE_REPORT)) > 0);
        dto.setCanCompact(generalStaffRepo.hasRoles(Arrays.asList(Constants.COMPACT_ROLE)) > 0);
        int applicationCount = versionRepo.countApplications(waterId, versionId);
        if(applicationCount == 0) {
            dto.setApplicationExists(false);
            dto.setSingleApplication(null);
        } else if (applicationCount > 1) {
            dto.setSingleApplication(null);
            dto.setApplicationExists(true);
        } else {
            List<BigDecimal> application = versionRepo.getApplicationIds(waterId, versionId);
            dto.setSingleApplication(application.get(0).longValue());
            dto.setApplicationExists(true);
        }
        return dto;

    }

    private VersionDetailDto getVersionDetailDto(WaterRightVersion version) {
        VersionDetailDto dto = new VersionDetailDto();

        dto.setWaterRightId(version.getWaterRightId().longValue());
        dto.setBasin(version.getWaterRight().getBasin());
        dto.setWaterRightNumber(version.getWaterRight().getWaterRightNumber().longValue());
        dto.setExt(version.getWaterRight().getExt());
        dto.setVersion(version.getVersion().longValue());
        dto.setScanned(version.getScanned());
        dto.setOperatingAuthority(version.getOperatingAuthority());
        dto.setPriorityDate(version.getPriorityDate());
        dto.setEnforceablePriorityDate(version.getEnforceablePriorityDate());
        dto.setStandardsUpdated("Y".equals(version.getStandardsApplied()));
        String flowRate = version.getMaximumFlowRate() != null ? version.getMaximumFlowRate().toString() : "";
        String unitExtension = version.getFlowRateUnit() != null ? " " + version.getFlowRateUnit() : "";
        dto.setFlowRate(flowRate + unitExtension);
        if(version.getMaximumVolume() != null) dto.setVolume(version.getMaximumVolume().doubleValue());
        if(version.getMaximumAcres() != null) dto.setAcres(version.getMaximumAcres().doubleValue());

        WaterRightStatus versionStatus = version.getVersionStatus();
        if(versionStatus != null) {
            dto.setVersionStatusDescription(versionStatus.getDescription());
            dto.setVersionStatusCode(versionStatus.getCode());
        }

        if (version.getApplications().size() > 0)
            dto.setApplicationTypeCodes(getApplicationTypes(version));

        Reference versionType = version.getTypeReference();
        if(versionType != null) {
            dto.setVersionTypeDescription(versionType.getMeaning());
            dto.setVersionTypeCode(versionType.getValue());
        }

        /* Additional values requested by FE 20210630 */
        if (version.getWaterRight().getWaterRightType()!=null) {
            dto.setWaterRightTypeCode(version.getWaterRight().getWaterRightType().getCode());
            dto.setWaterRightTypeDescription(version.getWaterRight().getWaterRightType().getDescription());
        }

        Map<String, Boolean> decreeFlags = inquiryService.isUneditable(version.getWaterRightId(), version.getVersion(), versionType.getValue());
        dto.setIsDecreed(decreeFlags.get("isDecreed"));
        dto.setIsVersionLocked(decreeFlags.get("isVersionLocked"));
        dto.setIsEditableIfDecreed(decreeFlags.get("isEditableIfDecreed"));
        dto.setCanReexamineDecree(decreeFlags.get("canReexamineDecree"));
        dto.setCanModifySplitDecree(decreeFlags.get("canModifySplitDecree"));

        if (version.getWaterRight().getWaterRightStatus()!=null) {
            dto.setWaterRightStatusDescription(version.getWaterRight().getWaterRightStatus().getDescription());
            dto.setWaterRightStatusCode(version.getWaterRight().getWaterRightStatus().getCode());
        }

        dto.setCompleteWaterRightNumber(
            Helpers.buildCompleteWaterRightNumber(
                    version.getWaterRight().getBasin(),
                    version.getWaterRight().getWaterRightNumber().toString(),
                    version.getWaterRight().getExt()
            )
        );
        dto.setCompleteVersion(
           String.format("%s %s", version.getTypeReference().getMeaning(), version.getVersion().toString())
        );

        return dto;
    }

    public WaterRightVersionDecreesPageDto getWaterRightVersionDecrees(
        Long waterRightId,
        Long versionId,
        Integer pageNumber,
        Integer pageSize,
        WaterRightVersionDecreeSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting the Decrees for a Water Right Version");

        Sort.Direction direction = sortDirection.getValue().equals("ASC")
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
            pageNumber - 1,
            pageSize,
            getDecreeSort(direction, sortColumn)
        );

        Page<DecreeVersion> results = decreeRepo.findByWaterRightIdAndVersionId(
            pageable,
            waterRightId,
            versionId
        );

        return new WaterRightVersionDecreesPageDto()
            .results(
                results
                    .getContent()
                    .stream()
                    .map(WaterRightVersionServiceImpl::getDecreeDto)
                    .collect(Collectors.toList())
            )
            .currentPage(results.getNumber() + 1)
            .pageSize(results.getSize())
            .totalPages(results.getTotalPages())
            .totalElements(results.getTotalElements())
            .sortColumn(sortColumn)
            .sortDirection(sortDirection);
    }

    // These methods are declared static since they should not depend on any
    // class attributes. In addition, being declared static allows these methods
    // to be passed to stream methods such as `map`.
    private static WaterRightVersionDecreeDto getDecreeDto(DecreeVersion decree) {
        return new WaterRightVersionDecreeDto()
            .decreeId(decree.getDecreeId())
            .description(decree.getDecree().getDecreeType().getDescription())
            .basin(decree.getDecree().getBasin())
            .eventDate(decree.getDecree().getIssuedDate())
            .missedInDecree(decree.getMissedInDecree() != null);
    }

    private static Sort getDecreeSort(
        Sort.Direction direction,
        WaterRightVersionDecreeSortColumn column
    ) {
        // `decreeId` is, unfortunately, stored as a string.
        // This is required so that sorting is done numerically
        // rather than alphabetically.
        Sort fallback = JpaSort.unsafe(
            Sort.Direction.ASC,
            "CAST(ref.decreeId AS integer)"
        );

        switch (column) {
            case DESCRIPTION:
                return JpaSort
                    .by(direction, "type.description")
                    .and(fallback);
            case BASIN:
                return JpaSort
                    .by(direction, "decree.basin")
                    .and(fallback);
            case EVENTDATE:
                return JpaSort
                    .by(direction, "decree.issuedDate")
                    .and(fallback);
            default:
                return JpaSort.unsafe(
                    direction,
                    "CAST(ref.decreeId AS integer)"
                );
        }
    }

    public VersionVolumeDto getVersionVolume(Long waterRightId, Long versionNumber) {
        LOGGER.info("Getting the volume information on a Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);

        Optional<WaterRightVersion> versionOpt = versionRepo
            .findByWaterRightIdAndVersionId(waterId, versionId);

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("The requested version was not found");
        }

        return getVersionVolumeDto(versionOpt.get());
    }

    public VersionVolumeDto updateVersionVolume(
        Long waterRightId,
        Long versionNumber,
        VersionVolumeDto update
    ) {
        LOGGER.info("Updating the volume information on a Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);

        Optional<WaterRightVersion> versionOpt = versionRepo
            .findByWaterRightIdAndVersionId(waterId, versionId);

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("The requested version was not found");
        }

        WaterRightVersion version = versionOpt
            .get()
            .setMaximumVolume(
                update.getVolume() != null
                    ? BigDecimal.valueOf(update.getVolume())
                    : null
            )
            .setVolumeOrigin(update.getVolumeOriginCode())
            .setVolumeDescription(update.getVolumeDescription());

        versionRepo.save(version);

        return getVersionVolumeDto(version);
    }

    private static VersionVolumeDto getVersionVolumeDto(WaterRightVersion version) {
        return new VersionVolumeDto()
            .volume(
                version.getMaximumVolume() != null
                ? version.getMaximumVolume().doubleValue()
                : null
            )
            .volumeOriginCode(version.getVolumeOrigin())
            .volumeOriginDescription(
                version.getVolumeOriginReference() != null
                ? version.getVolumeOriginReference().getMeaning()
                : null
            )
            .volumeDescription(version.getVolumeDescription());
    }

    public VersionAcreageDto getVersionAcreage(Long waterRightId, Long versionNumber) {
        LOGGER.info("Getting the acreage information on a Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);

        Optional<WaterRightVersion> versionOpt = versionRepo
            .findByWaterRightIdAndVersionId(waterId, versionId);

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("The requested version was not found");
        }

        return getVersionAcreageDto(versionOpt.get());
    }

    public VersionAcreageDto updateVersionAcreage(
        Long waterRightId,
        Long versionNumber,
        VersionAcreageDto update
    ) {
        LOGGER.info("Updating the acreage information on a Version");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        BigDecimal versionId = BigDecimal.valueOf(versionNumber);

        Optional<WaterRightVersion> versionOpt = versionRepo
            .findByWaterRightIdAndVersionId(waterId, versionId);

        if (!versionOpt.isPresent()) {
            throw new NotFoundException("The requested version was not found");
        }

        WaterRightVersion version = versionOpt
            .get()
            .setMaximumAcres(
                update.getAcres() != null
                    ? BigDecimal.valueOf(update.getAcres())
                    : null
            )
            .setAcresOrigin(update.getAcresOriginCode());

        versionRepo.save(version);

        return getVersionAcreageDto(version);
    }

    private static VersionAcreageDto getVersionAcreageDto(WaterRightVersion version) {
        return new VersionAcreageDto()
            .acres(
                version.getMaximumAcres() != null
                ? version.getMaximumAcres().doubleValue()
                : null
            )
            .acresOriginCode(version.getAcresOrigin())
            .acresOriginDescription(
                version.getAcresOriginReference() != null
                ? version.getAcresOriginReference().getMeaning()
                : null
            );
    }

    public EligibleWaterRightVersionPageDto getEligibleWaterRightVersions(int pageNumber, int pageSize, EligibleWaterRightVersionSortColumn sortColumn, SortDirection sortDirection, String basin, String waterNumber) {

        LOGGER.info("Get List of eligible Water Right Versions for Objection or Counter Objection");

        Sort sortDtoColumn = getEligibleWaterRightVersionSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<Object[]> results = versionRepo.getEligibleWaterRightVersions(request, basin, waterNumber);

        EligibleWaterRightVersionPageDto page = new EligibleWaterRightVersionPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return waterRightVersionDtoLoader(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);
        return page;

    }

    private Sort getEligibleWaterRightVersionSortColumn(EligibleWaterRightVersionSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        switch (sortColumn) {
            case WATERRIGHTNUMBER:
                orders.add(new Sort.Order(direction, "WATER_NUMBER"));
                break;
            case BASIN:
                orders.add(new Sort.Order(direction, "BASIN"));
                break;
            case EXT:
                orders.add(new Sort.Order(direction, "EXT"));
                break;
            case WATERRIGHTTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "TYPE"));
                break;
            case WATERRIGHTSTATUSDESCRIPTION:
                orders.add(new Sort.Order(direction, "STATUS"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "WATER_NUMBER"));
        return Sort.by(orders);

    }

    private WaterRightVersionDto waterRightVersionDtoLoader(Object[] model) {

        WaterRightVersionDto dto = new WaterRightVersionDto();
        dto.setWaterRightNumber(model[0]!=null?((BigDecimal)model[0]).longValue():null);
        dto.setBasin(model[1]!=null?(String)model[1]:null);
        dto.setVersion(model[2]!=null?((BigDecimal)model[2]).longValue():null);
        dto.setWaterRightId(model[3]!=null?((BigDecimal)model[3]).longValue():null);
        dto.setExt(model[4]!=null?(String)model[4]:null);
        dto.setWaterRightStatusDescription(model[5]!=null?(String)model[5]:null);
        dto.setWaterRightTypeDescription(model[6]!=null?(String)model[6]:null);
        dto.setCompleteVersion(String.format("%s %s", model[2]!=null?((BigDecimal)model[2]).longValue():null, model[7]!=null?(model[7].toString()):null));
        dto.setCompleteWaterRightNumber(
                Helpers.buildCompleteWaterRightNumber(
                        model[1]!=null?(String)model[1]:null,
                        model[0]!=null?((BigDecimal)model[0]).toString():null,
                        model[4]!=null?(String)model[4]:null
                )
        );
        return dto;

    }

}
