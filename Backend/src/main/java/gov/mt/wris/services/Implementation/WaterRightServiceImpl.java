package gov.mt.wris.services.Implementation;

import static gov.mt.wris.utils.Helpers.buildCompleteWaterRightNumber;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.ChildRightDto;
import gov.mt.wris.dtos.ChildRightPageDto;
import gov.mt.wris.dtos.ChildRightSortColumn;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.WaterRightCreationDto;
import gov.mt.wris.dtos.WaterRightDto;
import gov.mt.wris.dtos.WaterRightPageDto;
import gov.mt.wris.dtos.WaterRightSortColumn;
import gov.mt.wris.dtos.WaterRightUpdateDividedOwnershipDto;
import gov.mt.wris.dtos.WaterRightUpdateDto;
import gov.mt.wris.dtos.WaterRightVersionSearchDto;
import gov.mt.wris.dtos.WaterRightVersionSearchPageDto;
import gov.mt.wris.dtos.WaterRightVersionSearchSortColumn;
import gov.mt.wris.dtos.WaterRightViewDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.MasterStaffIndexes;
import gov.mt.wris.models.Owner;
import gov.mt.wris.models.OwnershipUpdate;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightOffice;
import gov.mt.wris.models.WaterRightStaff;
import gov.mt.wris.models.WaterRightStatus;
import gov.mt.wris.models.WaterRightType;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.repositories.OwnershipUpdateRepository;
import gov.mt.wris.repositories.WaterRightGeocodeRepository;
import gov.mt.wris.repositories.WaterRightOfficeRepository;
import gov.mt.wris.repositories.WaterRightRepository;
import gov.mt.wris.repositories.WaterRightStaffRepository;
import gov.mt.wris.services.MasterStaffIndexesService;
import gov.mt.wris.services.WaterRightService;

@Service
public class WaterRightServiceImpl implements WaterRightService {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightService.class);

    @Autowired
    private WaterRightRepository waterRepo;

    @Autowired
    private MasterStaffIndexesService masterStaffIndexesService;

    @Autowired
    private WaterRightOfficeRepository officeRepository;

    @Autowired
    private WaterRightStaffRepository staffRepository;

    @Autowired
    MasterStaffIndexesRepository staffRepo;

    @Autowired
    private OwnershipUpdateRepository ownershipUpdateRepository;

    @Autowired
    private WaterRightGeocodeRepository geocodeRepository;

    @Override
    public WaterRightPageDto searchWaterRights(int pagenumber,
        int pagesize,
        WaterRightSortColumn sortColumn,
        DescSortDirection sortDirection,
        String basin,
        String waterRightNumber,
        String ext,
        String typeCode,
        String statusCode,
        String subBasin,
        String waterReservationId,
        String conservationDistrictNumber,
        Boolean countActiveChangeAuthorizationVersions
    ) {
        LOGGER.info("Searching for water rights");

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize);

        WaterRightPageDto waterPage = new WaterRightPageDto();

        if(countActiveChangeAuthorizationVersions) {
            Page<Object[]> resultsPage = waterRepo.getWaterRightsWithChangeAuthorizationCount(pageable, sortColumn, sortDirection, basin, waterRightNumber, ext, typeCode, statusCode, subBasin, waterReservationId, conservationDistrictNumber);

            waterPage.setResults(resultsPage.getContent().stream().map(water -> {
                return getWaterRightDto((WaterRight) water[0], (Long) water[1]);
            }).collect(Collectors.toList()));

            waterPage.setCurrentPage(resultsPage.getNumber() + 1);
            waterPage.setPageSize(resultsPage.getSize());

            waterPage.setTotalPages(resultsPage.getTotalPages());
            waterPage.setTotalElements(resultsPage.getTotalElements());
        } else {
            Page<WaterRight> resultsPage = waterRepo.getWaterRights(pageable, sortColumn, sortDirection, basin, waterRightNumber, ext, typeCode, statusCode, subBasin, waterReservationId, conservationDistrictNumber);

            waterPage.setResults(resultsPage.getContent().stream().map(water -> {
                return getWaterRightDto(water, null);
            }).collect(Collectors.toList()));

            waterPage.setCurrentPage(resultsPage.getNumber() + 1);
            waterPage.setPageSize(resultsPage.getSize());

            waterPage.setTotalPages(resultsPage.getTotalPages());
            waterPage.setTotalElements(resultsPage.getTotalElements());
        }


        waterPage.setSortColumn(sortColumn);
        waterPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(basin != null) {
            filters.put("basin", basin);
        }
        if(waterRightNumber != null) {
            filters.put("waterRightNumber", waterRightNumber);
        }
        if(ext != null) {
            filters.put("ext", ext);
        }
        waterPage.setFilters(filters);

        return waterPage;
    }

    private WaterRightDto getWaterRightDto(WaterRight waterRight, Long activeCount) {
        WaterRightDto dto = new WaterRightDto();

        dto.setWaterRightId(waterRight.getWaterRightId().longValue());
        dto.setBasin(waterRight.getBasin());
        dto.setSubBasin(waterRight.getSubBasin());
        dto.setWaterRightNumber(waterRight.getWaterRightNumber().longValue());
        dto.setExt(waterRight.getExt());

        WaterRightStatus waterRightStatus = waterRight.getWaterRightStatus();
        if(waterRightStatus != null) dto.setStatusDescription(waterRightStatus.getDescription());
        dto.setStatusCode(waterRight.getWaterRightStatusCode());

        WaterRightType type = waterRight.getWaterRightType();
        if(type != null) {
            dto.setTypeDescription(type.getDescription());
            dto.setTypeCode(type.getCode());
        }

        dto.setCompleteWaterRightNumber(buildCompleteWaterRightNumber(waterRight.getBasin(), waterRight.getWaterRightNumber().toString(), waterRight.getExt()));
        dto.setConservationDistrictNumber(waterRight.getConDistNo());
        dto.setConservationDistrictDate(waterRight.getConDistDate());
        if(waterRight.getWaterReservationId() != null) dto.setWaterReservationId(waterRight.getWaterReservationId().longValue());

        dto.setDividedOwnership("Y".equals(waterRight.getDividedOwnship()));
        dto.setSevered("Y".equals(waterRight.getSevered()));

        if(activeCount != null) dto.setActiveChangeAuthorizationVersions(activeCount);

        return dto;
    }

    public WaterRightVersionSearchPageDto searchWaterRightsByVersions(int pagenumber,
        int pagesize,
        WaterRightVersionSearchSortColumn sortColumn,
        DescSortDirection sortDirection,
        String waterRightNumber,
        String version,
        String versionType
    ) {
        LOGGER.info("Searching for water rights by version info");

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize);

        WaterRightVersionSearchPageDto page = new WaterRightVersionSearchPageDto();

        Page<Object[]> resultsPage = waterRepo.getWaterRightsByVersions(pageable, sortColumn, sortDirection, waterRightNumber, version, versionType);

        page.setResults(resultsPage.getContent().stream().map(water -> {
            return getWaterRightVersionSearchDto((WaterRight) water[0], (Long) water[1]);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(waterRightNumber != null) {
            filters.put("waterRightNumber", waterRightNumber);
        }
        if(version != null) {
            filters.put("version", version);
        }
        if(versionType != null) {
            filters.put("versionType", versionType);
        }
        page.setFilters(filters);

        return page;
    }

    private WaterRightVersionSearchDto getWaterRightVersionSearchDto(WaterRight waterRight, Long versionCount) {
        WaterRightVersionSearchDto dto = new WaterRightVersionSearchDto();
        dto.setBasin(waterRight.getBasin());
        dto.setExt(waterRight.getExt());
        dto.setWaterRightNumber(waterRight.getWaterRightNumber().longValue());
        dto.setWaterRightId(waterRight.getWaterRightId().longValue());
        dto.setTypeDescription(waterRight.getWaterRightType().getDescription());
        if(waterRight.getWaterRightStatus() != null) dto.setStatusDescription(waterRight.getWaterRightStatus().getDescription());
        dto.setVersionCount(versionCount);
        return dto;
    }

    @Transactional
    public WaterRightDto createWaterRight(WaterRightCreationDto dto) {
        LOGGER.info("Creating a new Water Right");

        if(!Constants.WATER_RIGHT_ALLOWED_CREATION_TYPE.contains(dto.getTypeCode())) {
            throw new ValidationException("This Water Right Type is not allowed during creation");
        }

        WaterRight model = new WaterRight();
        model.setBasin(dto.getBasin());
        model.setWaterRightTypeCode(dto.getTypeCode());

        model.setWaterRightNumber(waterRepo.getNewWaterRightNumberForCreation());

        String directoryUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		MasterStaffIndexes staff = masterStaffIndexesService.getLocationStaffInfo(directoryUserName);
        model.setOfficeId(staff.getOfficeId());

        List<Owner> owners = dto.getContactIds().stream()
        .map(contactId ->
            createOwner(contactId)
        ).collect(Collectors.toList());
        model.setOwners(owners);

        model = waterRepo.save(model);

        WaterRightStaff staffXref = new WaterRightStaff();
        staffXref.setWaterRightId(model.getWaterRightId());
        staffXref.setStaffId(staff.getId());
        staffXref.setBeginDate(LocalDate.now());
        staffRepository.save(staffXref);
        WaterRightOffice officeXref = new WaterRightOffice();
        officeXref.setWaterRightId(model.getWaterRightId());
        officeXref.setOfficeId(staff.getOfficeId());;
        officeXref.setReceivedDate(LocalDate.now());
        officeRepository.save(officeXref);

        return getWaterRightDto(model, null);
    }

    private Owner createOwner(Long contactId) {
        Owner newOwner = new Owner();
        newOwner.setCustomerId(BigDecimal.valueOf(contactId));
        newOwner.setBeginDate(LocalDate.now());
        return newOwner;
    }

    public WaterRightViewDto getWaterRight(Long waterRightId) {
        LOGGER.info("Finding a Water Right");

        BigDecimal id = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findByIdWithOriginal(id);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();

        int compactRole = staffRepo.hasRoles(Arrays.asList(Constants.COMPACT_ROLE));

        Long childRightCount = waterRepo.countChildRights(id);

        Boolean isDecreed = waterRepo.needsDecreePermission(id);
        Boolean canModifyDecreed = staffRepo.hasRoles(Arrays.asList(Constants.DECREE_MODIFY_ROLE)) > 0;

        // when the water right is decreed, certain fields are only editable when the user has the Modify Decree role
        // otherwise, everything is editable
        return getWaterRightViewDto(waterRight, compactRole > 0, childRightCount, isDecreed, canModifyDecreed);
    }

    private WaterRightViewDto getWaterRightViewDto(WaterRight waterRight, Boolean canCompact, Long childCount, Boolean isDecreed, Boolean canModifyDecreed) {
        WaterRightViewDto dto = new WaterRightViewDto();
        dto.setBasin(waterRight.getBasin());
        dto.setWaterRightId(waterRight.getWaterRightId().longValue());
        dto.setWaterRightNumber(waterRight.getWaterRightNumber().longValue());
        dto.setSubBasin(waterRight.getSubBasin());
        dto.setExt(waterRight.getExt());
        // water right type always exists
        dto.setTypeCode(waterRight.getWaterRightType().getCode());
        dto.setTypeDescription(waterRight.getWaterRightType().getDescription());
        dto.setCreatedDate(waterRight.getCreatedDate());
        dto.setConservationDistrictDate(waterRight.getConDistDate());
        dto.setConservationDistrictNumber(waterRight.getConDistNo());
        if(waterRight.getWaterReservationId() != null) dto.setWaterReservationId(waterRight.getWaterReservationId().longValue());
        
        WaterRightStatus status = waterRight.getWaterRightStatus();
        if(status != null) {
            dto.setStatusCode(status.getCode());
            dto.setStatusDescription(status.getDescription());
        }

        dto.setDividedOwnership("Y".equals(waterRight.getDividedOwnship()));
        dto.setSevered("Y".equals(waterRight.getSevered()));

        WaterRight originalWaterRight = waterRight.getOriginalWaterRight();
        if(originalWaterRight != null) {
            dto.setOriginalBasin(originalWaterRight.getBasin());
            dto.setOriginalExt(originalWaterRight.getExt());
            dto.setOriginalWaterRightId(originalWaterRight.getWaterRightId().longValue());
            if(originalWaterRight.getWaterRightNumber() != null) dto.setOriginalWaterRightNumber(originalWaterRight.getWaterRightNumber().longValue());
            if(originalWaterRight.getWaterRightType() != null) dto.setOriginalTypeDescription(originalWaterRight.getWaterRightType().getDescription());
            WaterRightStatus originalStatus = originalWaterRight.getWaterRightStatus();
            if(originalStatus != null) dto.setOriginalStatusDescription(originalStatus.getDescription());
        }

        dto.setCanCompactType(canCompact);

        int changeVersionCount = 0;
        if(waterRight.getOriginalWaterRight() != null) {
            changeVersionCount = waterRepo.countChangeVersions(waterRight.getOriginalWaterRight().getWaterRightId());
        }
        dto.setOriginalHasChange(changeVersionCount > 0);

        dto.setChildRightCount(childCount);

        dto.setIsDecreed(isDecreed);
        dto.setIsEditableIfDecreed(canModifyDecreed);
 
        if(waterRight.getSubcompactId() != null) dto.setSubcompactId(waterRight.getSubcompactId().longValue());
        if(waterRight.getSubcompact() != null) {
            dto.setSubcompact(waterRight.getSubcompact().getName());
            dto.setCompact(waterRight.getSubcompact().getCompact().getName()); // every subcompact is attached to a compact
            dto.setCompactId(waterRight.getSubcompact().getCompact().getId().longValue());
        }

        return dto;
    }

    public WaterRightViewDto updateWaterRight(Long waterRightId, WaterRightUpdateDto updateDto) {
        LOGGER.info("Updating a Water Right");

        BigDecimal id = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findByIdWithOriginal(id);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();

        int compactRole = staffRepo.hasRoles(Arrays.asList(Constants.COMPACT_ROLE));
        if(compactRole == 0 && "CMPT".equals(updateDto.getTypeCode()) && !"CMPT".equals(waterRight.getWaterRightTypeCode())) {
            throw new ValidationException("More permissions are required to select Compact as the Water Right Type");
        }

        waterRight.setBasin(updateDto.getBasin());
        waterRight.setSubBasin(updateDto.getSubBasin());
        waterRight.setExt(updateDto.getExt());
        waterRight.setWaterRightTypeCode(updateDto.getTypeCode());
        waterRight.setDividedOwnship(updateDto.getDividedOwnership() ? "Y" : "N");
        waterRight.setSevered(updateDto.getSevered() ? "Y" : "N");
        waterRight.setConDistNo(updateDto.getConservationDistrictNumber());
        waterRight.setConDistDate(updateDto.getConservationDistrictDate());
        // deleting
        if(updateDto.getConservationDistrictNumber() == null) {
            waterRight.setConDistNo(null);
            waterRight.setConDistDate(null);
        }

        if (updateDto.getWaterReservationId() != null) {
            BigDecimal reservationId = BigDecimal.valueOf(updateDto.getWaterReservationId());
            waterRight.setWaterReservationId(reservationId);
        } else {
            waterRight.setWaterReservationId(null);
        }
        if(updateDto.getSubcompactId() != null) {
            BigDecimal subcompactId = BigDecimal.valueOf(updateDto.getSubcompactId());
            waterRight.setSubcompactId(subcompactId);
        } else {
            waterRight.setSubcompactId(null);
        }

        if(updateDto.getOriginalWaterRightId() != null) {
            BigDecimal originalId = BigDecimal.valueOf(updateDto.getOriginalWaterRightId());
            WaterRight original = new WaterRight();
            original.setWaterRightId(originalId);
            waterRight.setOriginalWaterRight(original);
        } else {
            waterRight.setOriginalWaterRight(null);
        }

        try {
            waterRight = waterRepo.save(waterRight);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException sc = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("WRGT_WTSV_FK")) {
                    throw new NotFoundException("Invalid Water Reservation Number: " + updateDto.getWaterReservationId());
                }
            }
            throw e;
        }

        Long childRightCount = waterRepo.countChildRights(id);

        Boolean isDecreed = waterRepo.needsDecreePermission(id);
        Boolean canModifyDecreed = staffRepo.hasRoles(Arrays.asList(Constants.DECREE_MODIFY_ROLE)) > 0;

        // when the water right is decreed, certain fields are only editable when the user has the Modify Decree role
        // otherwise, everything is editable
        return getWaterRightViewDto(waterRight, compactRole > 0, childRightCount, isDecreed, canModifyDecreed);
    }

    @Override
    public WaterRightUpdateDividedOwnershipDto updateWaterRightDividedOwnership(Long ownershipUpdateId,Long waterRightId, WaterRightUpdateDividedOwnershipDto updateDto) {
        LOGGER.info("Updating a Water Right");

        BigDecimal id = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findByIdWithOriginal(id);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }
        WaterRight waterRight = foundWaterRight.get();

        Optional<OwnershipUpdate> foundOwnershipUpdate = ownershipUpdateRepository.findById(BigDecimal.valueOf(ownershipUpdateId));
        if(!foundOwnershipUpdate.isPresent())
            throw new NotFoundException(String.format("Ownership Update id %s not found.", ownershipUpdateId));

        waterRight.setDividedOwnship(updateDto.getDividedOwnship() ? "Y" : "N");

        waterRight = waterRepo.save(waterRight);

        return updateDto;
    }

    public ChildRightPageDto getChildRights(int pageNumber,
                                            int pageSize,
                                            ChildRightSortColumn sortDTOColumn,
                                            DescSortDirection sortDirection,
                                            Long waterRightId
    ) {
        LOGGER.info("Getting a page of Water Rights");

        String sortColumn = getChildRightSortColumn(sortDTOColumn);
        Sort.Direction direction = (sortDirection == DescSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortColumn).and(Sort.by(Sort.Direction.DESC, "id")));

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Page<WaterRight> resultPage = waterRepo.getChildRights(pageable, waterId);

        ChildRightPageDto dto = new ChildRightPageDto();

        dto.setResults(resultPage.getContent().stream().map(childRight -> {
            return getChildRightDto(childRight);
        }).collect(Collectors.toList()));

        dto.setCurrentPage(resultPage.getNumber() + 1);
        dto.setPageSize(resultPage.getSize());

        dto.setTotalPages(resultPage.getTotalPages());
        dto.setTotalElements(resultPage.getTotalElements());

        dto.setSortColumn(sortDTOColumn);
        dto.setSortDirection(sortDirection);

        return dto;
    }

    private String getChildRightSortColumn(ChildRightSortColumn sortColumn) {
        if(sortColumn == ChildRightSortColumn.BASIN) {
            return "basin";
        } else if (sortColumn == ChildRightSortColumn.EXT) {
            return "ext";
        } else if (sortColumn == ChildRightSortColumn.STATUSDESCRIPTION) {
            return "s.description";
        } else if (sortColumn == ChildRightSortColumn.TYPEDESCRIPTION) {
            return "t.description";
        } else {
            return "waterRightNumber";
        }
    }

    private ChildRightDto getChildRightDto(WaterRight childRight) {
        ChildRightDto dto = new ChildRightDto();
        dto.setWaterRightId(childRight.getWaterRightId().longValue());
        dto.setWaterRightNumber(childRight.getWaterRightNumber().longValue());
        dto.setBasin(childRight.getBasin());
        dto.setExt(childRight.getExt());
        dto.setTypeDescription(childRight.getWaterRightType().getDescription());
        WaterRightStatus status = childRight.getWaterRightStatus();
        if(status != null) {
            dto.setStatusDescription(status.getDescription());
        }
        return dto;
    }

    // for tests only
    @Transactional
    public void deleteWaterRight(Long waterRightId) {
        LOGGER.info("deleting a water right");

        BigDecimal id = BigDecimal.valueOf(waterRightId);

        officeRepository.deleteByWaterRightId(id);

        staffRepository.deleteByWaterRightId(id);

        geocodeRepository.deleteByWaterRightId(id);

        waterRepo.deleteById(id);
    }

}