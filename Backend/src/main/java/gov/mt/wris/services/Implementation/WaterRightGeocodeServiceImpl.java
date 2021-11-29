package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.GeocodeWaterRightDto;
import gov.mt.wris.dtos.GeocodeWaterRightPageDto;
import gov.mt.wris.dtos.GeocodeWaterRightSortColumn;
import gov.mt.wris.dtos.WaterRightGeocodeDto;
import gov.mt.wris.dtos.WaterRightGeocodeNewDto;
import gov.mt.wris.dtos.WaterRightGeocodePageDto;
import gov.mt.wris.dtos.WaterRightGeocodeSortColumn;
import gov.mt.wris.dtos.WaterRightGeocodesCreationDto;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.MasterStaffIndexes;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.models.WaterRightGeocode;
import gov.mt.wris.models.WaterRightStatus;
import gov.mt.wris.models.WaterRightType;
import gov.mt.wris.repositories.GeocodeRepository;
import gov.mt.wris.repositories.WaterRightGeocodeRepository;
import gov.mt.wris.repositories.WaterRightRepository;
import gov.mt.wris.services.WaterRightGeocodeService;
import gov.mt.wris.utils.Helpers;

@Service
public class WaterRightGeocodeServiceImpl implements WaterRightGeocodeService {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightGeocodeService.class);

    @Autowired
    GeocodeRepository geocodeRepository;

    @Autowired
    WaterRightGeocodeRepository xrefRepository;

    @Autowired
    WaterRightRepository waterRepo;

    public GeocodeWaterRightPageDto getGeocodeWaterRights(int pagenumber, int pagesize, GeocodeWaterRightSortColumn sortColumn, DescSortDirection sortDirection, String geocodeId) {
        LOGGER.info("Getting a page of Water Rights attached to a Geocode");

        Sort geocodeSort = getGeocodeSort(sortColumn, sortDirection);

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, geocodeSort);

        Page<WaterRightGeocode> resultsPage = xrefRepository.findByGeocodeId(pageable, geocodeId);

        GeocodeWaterRightPageDto page = new GeocodeWaterRightPageDto();

        page.setResults(resultsPage.getContent().stream().map(geocodeWater -> {
            return getWaterRightDto(geocodeWater);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());
        
        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        page.setFormattedGeocode(formatGeocode(geocodeId));
        return page;
    }

    private GeocodeWaterRightDto getWaterRightDto(WaterRightGeocode geocode) {
        WaterRight waterRight = geocode.getWaterRight();
        GeocodeWaterRightDto dto = new GeocodeWaterRightDto();

        dto.setWaterRightId(waterRight.getWaterRightId().longValue());
        dto.setBasin(waterRight.getBasin());
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

        dto.setBeginDate(geocode.getBeginDate());
        dto.setEndDate(geocode.getEndDate());
        dto.setValid("Y".equals(geocode.getValid()) ? true : false);
        dto.setComments(geocode.getComments());

        return dto;
    }

    private Sort getGeocodeSort(GeocodeWaterRightSortColumn sortColumn, DescSortDirection sortDirection) {
        Sort.Direction direction = sortDirection == DescSortDirection.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort fallback = Sort
            .by(Sort.Direction.DESC, "w.waterRightNumber")
            .and(Sort.by(Sort.Direction.DESC, "w.basin"))
            .and(Sort.by(Sort.Direction.DESC, "w.waterRightId"));

        switch (sortColumn) {
            case BASIN:
                return Sort
                    .by(direction, "w.basin")
                    .and(Sort.by(Sort.Direction.DESC, "w.waterRightNumber"))
                    .and(Sort.by(Sort.Direction.DESC, "w.waterRightId"));

            case EXT:
                return Sort
                    .by(direction, "w.ext")
                    .and(fallback);

            case STATUSDESCRIPTION:
            case STATUSCODE:
                return Sort
                    .by(direction, "s.description")
                    .and(fallback);

            case TYPEDESCRIPTION:
                return Sort
                    .by(direction, "t.description")
                    .and(fallback);

            case BEGINDATE:
                return Sort
                    .by(direction, "beginDate")
                    .and(fallback);

            case ENDDATE:
                return Sort
                    .by(direction, "endDate")
                    .and(fallback);

            case VALID:
                return Sort
                    .by(direction, "valid")
                    .and(fallback);

            case COMMENTS:
                return Sort
                    .by(direction, "comments")
                    .and(fallback);

            default:
                return Sort
                    .by(direction, "w.waterRightNumber")
                    .and(Sort.by(Sort.Direction.DESC, "w.basin"))
                    .and(Sort.by(Sort.Direction.DESC, "w.waterRightId"));
        }
    }

    public WaterRightGeocodePageDto getWaterRightGeocodes(int pagenumber, int pagesize, WaterRightGeocodeSortColumn sortColumn, DescSortDirection sortDirection, Long waterRightId) {
        LOGGER.info("Getting a page of Geocodes attached to a Water Right");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRight> foundWaterRight = waterRepo.findById(waterId);
        if(!foundWaterRight.isPresent()) {
            throw new NotFoundException("This Water Right does not exist");
        }

        String sort = getWaterRightGeocodeSort(sortColumn);
        Sort.Direction direction = sortDirection == DescSortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pagenumber - 1, pagesize, Sort.by(direction, sort).and(Sort.by(Sort.Direction.DESC, "geocodeId")));

        Page<WaterRightGeocode> resultsPage = xrefRepository.findByWaterRightId(pageable, waterId);

        WaterRightGeocodePageDto page = new WaterRightGeocodePageDto();

        page.setResults(resultsPage.getContent().stream().map(geocodeWater -> {
            return getWaterRightGeocodeDto(geocodeWater);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());
        
        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        page.setAllUnresolved(xrefRepository.allUnresolved(waterId));
        page.setAllSevered(xrefRepository.allSevered(waterId));
        page.setAllValid(xrefRepository.allValid(waterId));
        page.setGeocodeUrl(xrefRepository.getGeocodeUrl());
        page.setNrisUrl(xrefRepository.getNRISMapURL());
        page.setMapVersionNumber(waterRepo.getMostRecentOperatingVersion(waterId).longValue());

        return page;
    }

    private WaterRightGeocodeDto getWaterRightGeocodeDto(WaterRightGeocode geocode) {
        WaterRightGeocodeDto dto = new WaterRightGeocodeDto();

        dto.setBeginDate(geocode.getBeginDate());
        dto.setEndDate(geocode.getEndDate());
        dto.setComments(geocode.getComments());
        dto.setGeocodeId(geocode.getGeocodeId());
        dto.setFormattedGeocode(geocode.getGeocode().getFormattedGeocode());
        dto.setSever("Y".equals(geocode.getSever()));
        dto.setUnresolved("Y".equals(geocode.getUnresolved()));
        dto.setValid("Y".equals(geocode.getValid()));
        dto.setXrefId(geocode.getId().longValue());
        dto.setCreatedDate(geocode.getCreatedDate());
        dto.setModifiedDate(geocode.getModifiedDate());
        MasterStaffIndexes createdBy = geocode.getCreatedByName();
        if(createdBy != null) dto.setCreatedBy(Helpers.buildName(createdBy.getLastName(), createdBy.getFirstName(), createdBy.getMidInitial()));
        MasterStaffIndexes modifiedBy = geocode.getModifiedByName();
        if(modifiedBy != null) dto.setModifiedBy(Helpers.buildName(modifiedBy.getLastName(), modifiedBy.getFirstName(), modifiedBy.getMidInitial()));

        return dto;
    }

    private String getWaterRightGeocodeSort(WaterRightGeocodeSortColumn sortColumn) {
        if (sortColumn == WaterRightGeocodeSortColumn.BEGINDATE) {
            return "beginDate";
        } else if (sortColumn == WaterRightGeocodeSortColumn.ENDDATE) {
            return "endDate";
        } else if (sortColumn == WaterRightGeocodeSortColumn.COMMENTS) {
            return "comments";
        } else if (sortColumn == WaterRightGeocodeSortColumn.FORMATTEDGEOCODE) {
            return "geocodeId";
        } else if (sortColumn == WaterRightGeocodeSortColumn.SEVER) {
            return "sever";
        } else if (sortColumn == WaterRightGeocodeSortColumn.UNRESOLVED) {
            return "unresolved";
        }
        return "valid";
    }

    public void addWaterRightGeocode(Long waterRightId, WaterRightGeocodesCreationDto dto) {
        LOGGER.info("Adding Geocodes to a Water Right");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);

        List<String> geocodeIds = dto.getNewGeocodes().stream().map(geocode -> {
            return geocode.getGeocodeId();
        }).collect(Collectors.toList());

        // check that no duplicates exist
        List<String> duplicates = Helpers.findDuplicates(geocodeIds)
        .stream().map(geocodeId -> {
            return formatGeocode(geocodeId);
        }).collect(Collectors.toList());
        if(duplicates.size() > 0) {
            String duplicateError = duplicates.stream().collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Geocodes were entered at least twice:\n" + duplicateError);
        }
        duplicates = xrefRepository.getDuplicateGeocodes(waterId, geocodeIds).stream().map(geocodeId -> {
            return formatGeocode(geocodeId);
        }).collect(Collectors.toList());
        if(duplicates.size() > 0) {
            String duplicateError = duplicates.stream().collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Geocodes already exist:\n" + duplicateError);
        }

        // check that all are "valid" geocodes meaning they are 17 characters long
        List<String>  invalidGeocodes = geocodeIds.stream().filter(geocodeId -> {
            return geocodeId.length() != 17;
        }).collect(Collectors.toList());
        if(invalidGeocodes.size() > 0) {
            String invalidError = invalidGeocodes.stream().collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Geocodes are not the expected length:\n" + invalidError);
        }

        // check that all geocodes already exist in the WRD_GEOCODES table
        List<String> availableGeocodes = geocodeRepository.getGeocodeIds(geocodeIds);
        if(availableGeocodes.size() != geocodeIds.size()) {
            geocodeIds.removeAll(availableGeocodes);
            // Add the missing geocodes to WRD_GEOCODES
            for (String geocodeId: geocodeIds) {
                geocodeRepository.insertNewGeocode(geocodeId);
            }
        }

        List<WaterRightGeocode> geocodes = dto.getNewGeocodes().stream().map(geocode -> {
            return getNewGeocodeFromDto(waterId, geocode);
        }).collect(Collectors.toList());

        try {
            xrefRepository.saveAll(geocodes);
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                BatchUpdateException sc = (BatchUpdateException) e.getCause().getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("GWCX_WRGT_FK")) {
                    throw new DataIntegrityViolationException("This Water Right no longer exists");
                } else if (constraintMessage.contains("GWCX_WRGT_GOCD_U")) {
                    throw new DataIntegrityViolationException("Duplicate Geocodes are not allowed to be added");
                } else if (constraintMessage.contains("GWCX_GOCD_FK")) {
                    throw new DataIntegrityViolationException("One of the geocodes does not exists");
                } else {
                    throw e;
                }
            }
            throw e;
        }
        return;
    }

    private static String formatGeocode(String geocodeId) {
        return Arrays.asList(
            geocodeId.substring(0, 2),
            geocodeId.substring(2, 6),
            geocodeId.substring(6, 8),
            geocodeId.substring(8, 9),
            geocodeId.substring(9, 11),
            geocodeId.substring(11, 13),
            geocodeId.substring(13, 17)
        ).stream().collect(Collectors.joining("-"));
    }

    private WaterRightGeocode getNewGeocodeFromDto(BigDecimal waterRightId, WaterRightGeocodeNewDto dto) {
        WaterRightGeocode geocode = new WaterRightGeocode();
        geocode.setGeocodeId(dto.getGeocodeId());
        geocode.setBeginDate(dto.getBeginDate());
        geocode.setEndDate(dto.getEndDate());
        geocode.setComments(dto.getComments());
        geocode.setSever(dto.getSever() != null && dto.getSever() ? "Y" : "N");
        geocode.setUnresolved(dto.getUnresolved() != null && dto.getUnresolved() ? "Y" : "N");
        geocode.setValid(dto.getValid() != null && dto.getValid() ? "Y" : "N");
        geocode.setWaterRightId(waterRightId);
        return geocode;
    }

    public WaterRightGeocodeDto editGeocode(Long waterRightId, Long xrefId, WaterRightGeocodeDto dto) {
        LOGGER.info("Editing a Geocode attached to a Water Right");

        BigDecimal id = BigDecimal.valueOf(xrefId);
        Optional<WaterRightGeocode> foundGeocode = xrefRepository.findById(id);
        if(!foundGeocode.isPresent()) {
            throw new NotFoundException("This Geocode is not attached to this Water Right");
        }
        WaterRightGeocode geocode = foundGeocode.get();

        geocode.setBeginDate(dto.getBeginDate());
        geocode.setEndDate(dto.getEndDate());
        geocode.setValid(dto.getValid() ? "Y" : "N");
        geocode.setComments(dto.getComments());
        geocode.setSever(dto.getSever() ? "Y" : "N");
        geocode.setUnresolved(dto.getUnresolved() ? "Y" : "N");

        geocode = xrefRepository.save(geocode);

        return getWaterRightGeocodeDto(geocode);
    }

    public void deleteGeocode(Long waterRightId, Long xrefId) {
        LOGGER.info("Removing a Geocode from a Water Right");

        BigDecimal id = BigDecimal.valueOf(xrefId);

        xrefRepository.deleteById(id);
    }

    public void deleteInvalidGeocodes(Long waterRightId) {
        LOGGER.info("Removing Invalid Geocodes from a Water Right");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);

        xrefRepository.deleteInvalidGeocodes(waterId);
    }

    public void unresolveWaterRightGeocodes(Long waterRightId) {
        LOGGER.info("Unresolving all the Geocodes attached to a Water Right");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);

        xrefRepository.unresolveGeocodes(waterId);
    }

    public void severWaterRightGeocodes(Long waterRightId) {
        LOGGER.info("Sever all the Geocodes attached to a Water Right");

        BigDecimal waterId = BigDecimal.valueOf(waterRightId);

        xrefRepository.severGeocodes(waterId);
    }
}
