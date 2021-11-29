package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.PurposesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.services.ExaminationService;
import gov.mt.wris.services.Implementation.PlaceOfUseServiceImpl;
import gov.mt.wris.services.Implementation.SubdivisionXrefServiceImpl;
import gov.mt.wris.services.PeriodOfUseService;
import gov.mt.wris.services.PurposeService;
import gov.mt.wris.services.RetiredPlacesOfUseService;
import gov.mt.wris.services.RetiredPouSubdivisionXrefService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class PurposesController implements PurposesApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(PurposesController.class);

    @Autowired
    private PurposeService purposeService;
    @Autowired
    private RetiredPlacesOfUseService retiredPlacesOfUseService;

    @Autowired
    private RetiredPouSubdivisionXrefService retiredPouSubdivisionXrefService;

    @Autowired
    private PeriodOfUseService periodOfUseService;

    @Autowired
    private PlaceOfUseServiceImpl placeOfUseService;

    @Autowired
    private SubdivisionXrefServiceImpl subdivisionXrefService;

    @Autowired
    private ExaminationService examinationsService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CLIMATIC_AREAS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<PurposesSearchPageDto> searchPurposes(String basin, String waterRightNumber, String waterRightType, Integer pageNumber, Integer pageSize, PurposesSortColumn sortColumn, SortDirection sortDirection, String versionType, String version, PurposeSearchType purposeSearchType, String ext) {
        LOGGER.info("Search Purposes");
        PurposesSearchPageDto dto = purposeService.searchPurposes(pageNumber, pageSize, sortColumn, sortDirection, purposeSearchType, basin, waterRightNumber, waterRightType, ext, versionType, version);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CLIMATIC_AREAS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSE_IRRIGATION_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.IRRIGATION_TYPES_TABLE)
    })
    public ResponseEntity<PurposeDetailDto> getPurpose(Long purposeId) {

        LOGGER.info("Search Purposes");
        PurposeDetailDto dto = purposeService.getPurpose(new BigDecimal(purposeId));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CLIMATIC_AREAS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.IRRIGATION_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSE_IRRIGATION_XREF_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PURPOSE_IRRIGATION_XREF_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.PURPOSE_IRRIGATION_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<PurposeDetailDto> updatePurpose(Long purposeId, PurposeUpdateDto updateDto) {

        LOGGER.info("Search Purposes");
        PurposeDetailDto dto = purposeService.updatePurpose(new BigDecimal(purposeId), updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.PURPOSES_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.PURPOSE_IRRIGATION_XREF_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.PLACE_OF_USES_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<Void> deletePurpose(Long purposeId) {
        LOGGER.info("Deleting a Purpose");
        purposeService.deletePurpose(new BigDecimal(purposeId));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<CopyDiversionToPeriodResultsDto> copyDiversionToPeriod(Long purposeId, Object body) {

        LOGGER.info("Copy first period of diversion to period of use");
        CopyDiversionToPeriodResultsDto dto = periodOfUseService.copyFirstPeriodOfDiversionToPeriodOfUse(new BigDecimal(purposeId));
        return new ResponseEntity<CopyDiversionToPeriodResultsDto>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.RETIRED_PLACE_OF_USE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.TRS_LOCATION_TABLE)
    })
    public ResponseEntity<PlacesOfUsePageDto> getRetiredPlacesOfUse(Long purposeId, Integer pageNumber, Integer pageSize, PlacesOfUseSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Search Retired Places Of Use for Purpose: " + purposeId );
        PlacesOfUsePageDto dto = retiredPlacesOfUseService.getRetiredPlacesOfUse(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(purposeId));
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.INSERT, table = Constants.RETIRED_PLACE_OF_USE_TABLE),
            @Permission(verb = Constants.EXECUTE, table = Constants.COMMON_FUNCTIONS),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),

    })
    public ResponseEntity<PlaceOfUseDto> createRetiredPlaceForPurpose(Long purposeId, PlaceOfUseCreationDto placeOfUseCreationDto,  Boolean sort) {
        LOGGER.info("Create Retired Place Of Use for Purpose: " + purposeId );
        PlaceOfUseDto dto = retiredPlacesOfUseService.createRetiredPlaceOfUse(new BigDecimal(purposeId), placeOfUseCreationDto, sort == null ? true : sort);
        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.RETIRED_PLACE_OF_USE_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.RETIRED_PLACE_OF_USE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
    })
    public ResponseEntity<PlaceOfUseDto> updateRetiredPlaceForPurpose(Long purposeId, Long retiredPlaceId, PlaceOfUseCreationDto placeOfUseCreationDto) {
        LOGGER.info("Update Retired Place Of Use for Purpose: " + purposeId);
        PlaceOfUseDto dto = retiredPlacesOfUseService.updateRetiredPlaceOfUse(new BigDecimal(purposeId), new BigDecimal(retiredPlaceId), placeOfUseCreationDto);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<PeriodsOfUsePageDto> getPeriodsOfUse(Long purposeId,
                                                        Integer pageNumber,
                                                        Integer pageSize,
                                                        PeriodsOfUseSortColumn sortColumn,
                                                        SortDirection sortDirection) {

        LOGGER.info("Search Period Of Use");
        PeriodsOfUsePageDto dto = periodOfUseService.getPeriodsOfUse(pageNumber,pageSize, sortColumn, sortDirection, purposeId);
        return ResponseEntity.ok(dto);

    }

   @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PLACE_OF_USES_TABLE)
    })
    public ResponseEntity<Integer> pousCopyPods(Long purposeId, Object body) {
        LOGGER.info("Copy POD to Place Of Use");
        Integer result = subdivisionXrefService.PlaceOfUseCopyPods(new BigDecimal(purposeId));
        return new ResponseEntity<Integer>(result, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<PlacesOfUsePageDto> getPlacesOfUse(Long purposeId,
                                                      Integer pageNumber,
                                                      Integer pageSize,
                                                      PlacesOfUseSortColumn sortColumn,
                                                      SortDirection sortDirection) {
        LOGGER.info("Get Places Of Use");
        PlacesOfUsePageDto dto = placeOfUseService.getPlacesOfUse(pageNumber, pageSize, sortColumn, sortDirection, purposeId);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<PlaceOfUseDto> updatePlaceOfUse(Long purposeId,
                                                   Long placeId,
                                                   PlaceOfUseCreationDto updateDto) {
        LOGGER.info("Update Place Of Use");
        PlaceOfUseDto dto = placeOfUseService.updatePlaceOfUse(new BigDecimal(purposeId), new BigDecimal(placeId), updateDto);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.RETIRED_PLACE_OF_USE_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.RETIRED_PLACE_OF_USE_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE)
    })
    public ResponseEntity<Void> deleteRetiredPlaceOfUse(Long purposeId, Long retiredPlaceId) {
        LOGGER.info("Delete Retired Place Of Use " + retiredPlaceId + " from Purpose: " + purposeId);
        retiredPlacesOfUseService.deleteRetiredPlaceOfUse(new BigDecimal(purposeId), new BigDecimal(retiredPlaceId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.TRS_LOCATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.POU_EXAMINATIONS_XREF),
            @Permission(verb = Constants.UPDATE, table = Constants.SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.POU_SURVEY_TYPE_XREF)
    })
    public ResponseEntity<Void> deletePlaceOfUse(Long purposeId, Long placeId) {
        LOGGER.info("Delete Place Of Use");
        placeOfUseService.deletePlaceOfUse(new BigDecimal(purposeId), new BigDecimal(placeId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.SUBDIVISION_CODES_TABLE),
    })
    public ResponseEntity<SubdivisionPageDto> getSubdivisionsForRetPou(Long retiredPlaceId, Long purposeId, Integer pageNumber, Integer pageSize, SubdivisionSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Search Subdivisions for Retired Place Of Use: " + retiredPlaceId + " of Purpose: " + purposeId);
        SubdivisionPageDto dto = retiredPouSubdivisionXrefService.getSubdivisionsForRetPou(new BigDecimal(retiredPlaceId), new BigDecimal(purposeId), pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.SUBDIVISION_CODES_TABLE)
    })
    public ResponseEntity<SubdivisionPageDto> getSubdivisionsForPlaceOfUse(Long placeId,
                                                                    Long purposeId,
                                                                    Integer pageNumber,
                                                                    Integer pageSize,
                                                                    SubdivisionSortColumn sortColumn,
                                                                    SortDirection sortDirection) {
        LOGGER.info("Get Subdivision");
        SubdivisionPageDto dto = subdivisionXrefService.getSubdivisionsForPlaceOfUse(new BigDecimal(placeId), new BigDecimal(purposeId), pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.INSERT, table = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE)
    })
    public ResponseEntity<SubdivisionDto> createSubdivisionForRetPou(Long retiredPlaceId, Long purposeId, SubdivisionCreationDto subdivisionCreationDto) {
        LOGGER.info("Create Subdivision for Retired Place Of Use: " + retiredPlaceId + " of Purpose: " + purposeId);
        SubdivisionDto dto = retiredPouSubdivisionXrefService.createSubdivisionForRetPou(new BigDecimal(retiredPlaceId), new BigDecimal(purposeId), subdivisionCreationDto);
        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE)
    })
    public ResponseEntity<SubdivisionDto> updateSubdivisionForRetPou(Long purposeId, Long retiredPlaceId, String code, SubdivisionCreationDto subdivisionCreationDto) {
        LOGGER.info("Update Subdivision for Retired Place Of Use: " + retiredPlaceId + " of Purpose: " + purposeId);
        SubdivisionDto dto = retiredPouSubdivisionXrefService.updateSubdivisionForRetPou(new BigDecimal(purposeId), new BigDecimal(retiredPlaceId), code, subdivisionCreationDto);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.SUBDIVISION_XREFS_TABLE)
    })
    public ResponseEntity<SubdivisionDto> createSubdivisionForPlaceOfUse(Long placeId,
                                                                        Long purposeId,
                                                                        SubdivisionCreationDto createDto) {
        LOGGER.info("Create Subdivision");
        SubdivisionDto dto = subdivisionXrefService.createSubdivisionForPlaceOfUse(new BigDecimal(placeId), new BigDecimal(purposeId), createDto);
        return new ResponseEntity<SubdivisionDto>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.SUBDIVISION_XREFS_TABLE)
    })
    public ResponseEntity<SubdivisionDto> updateSubdivisionForPlaceOfUse(Long purposeId,
                                                                  Long placeId,
                                                                  String code,
                                                                  SubdivisionCreationDto updateDto) {
        LOGGER.info("Update Subdivision");
        SubdivisionDto dto = subdivisionXrefService.updateSubdivisionForPlaceOfUse(new BigDecimal(purposeId), new BigDecimal(placeId), code, updateDto);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE)
    })
    public ResponseEntity<Void> deleteSubdivisionFromRetPou(Long purposeId, Long retiredPlaceId, String code) {
        LOGGER.info("Delete Subdivision from Retired Place Of Use: " + retiredPlaceId + " of Purpose: " + purposeId);
        retiredPouSubdivisionXrefService.deleteSubdivisionFromRetPou(new BigDecimal(purposeId), new BigDecimal(retiredPlaceId), code);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.SUBDIVISION_XREFS_TABLE)
    })
    public ResponseEntity<Void> deleteSubdivisionFromPlaceOfUse(Long purposeId, Long placeId, String code) {
        LOGGER.info("Delete Subdivision");
        subdivisionXrefService.deleteSubdivisionFromPlaceOfUse(new BigDecimal(purposeId), new BigDecimal(placeId), code);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.EXECUTE, table = Constants.COMMON_FUNCTIONS),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.RETIRED_PLACE_OF_USE_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE),
    })
    public ResponseEntity<Integer> retiredPousCopyPods(Long purposeId, Object body) {
        LOGGER.info("Copy POU's to Retired Place Of Use in Purpose: " + purposeId);
        Integer dto = retiredPouSubdivisionXrefService.retiredPousCopyPods(new BigDecimal(purposeId));
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<PeriodOfUseDto> createPeriodOfUse(Long purposeId, PeriodOfUseCreationDto createDto) {

        LOGGER.info("Create Period Of Use");
        PeriodOfUseDto dto = periodOfUseService.createPeriodOfUse(new BigDecimal(purposeId), createDto);
        return new ResponseEntity<PeriodOfUseDto>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.TRS_LOCATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.POU_EXAMINATIONS_XREF),
            @Permission(verb = Constants.UPDATE, table = Constants.SUBDIVISION_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.POU_SURVEY_TYPE_XREF)
    })
    public ResponseEntity<PlaceOfUseDto> createPlaceOfUse(Long purposeId, PlaceOfUseCreationDto createDto) {

        LOGGER.info("Create Place Of Use");
        PlaceOfUseDto dto = placeOfUseService.createPlaceOfUse(new BigDecimal(purposeId), createDto);
        return new ResponseEntity<PlaceOfUseDto>(dto, null, HttpStatus.CREATED);

    }
    
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.EXAMINATIONS_TABLE)
    })
    public ResponseEntity<ExaminationDetailDto> createExamination(Long purposeId, ExaminationCreationDto examinationCreationDto) {
        LOGGER.info("Create Examination");
        ExaminationDetailDto examinationDto = examinationsService.createExamination(new BigDecimal(purposeId), examinationCreationDto);
        return new ResponseEntity<ExaminationDetailDto>(examinationDto, null, HttpStatus.CREATED);
    }

}
