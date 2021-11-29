package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.DataSourcesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.services.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class DataSourceController implements DataSourcesApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(DataSourceController.class);

    @Autowired
    private DataSourceService dataSourceService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_XREF)
    })
    public ResponseEntity<DataSourceDto> getDataSourceDetails(Long pexmId) {
        LOGGER.info("Get Data Source");
        DataSourceDto dto = dataSourceService.getDataSourceDetails(new BigDecimal(pexmId));
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EXAMINATIONS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.POU_EXAMINATIONS_TABLE)
    })
    public ResponseEntity<DataSourceDto> updateDataSource(Long pexmId, DataSourceCreationDto dataSourceCreationDto) {
        LOGGER.info("Change Data Source");
        DataSourceDto dto = dataSourceService.updateDataSource(BigDecimal.valueOf(pexmId), dataSourceCreationDto);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EXAM_USGS_MAP_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.USGS_TABLE)
    })
    public ResponseEntity<UsgsPageDto> getUsgsQuadMaps(Long pexmId, Integer pageNumber, Integer pageSize, UsgsSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Search Usgs for Data Source: " + pexmId);
        UsgsPageDto dto = dataSourceService.getUsgsQuads(new BigDecimal(pexmId), pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.EXAM_USGS_MAP_XREF_TABLE)
    })
    public ResponseEntity<UsgsDto> createUsgsQuadMap(Long pexmId, UsgsCreationDto usgsCreationDto) {
        LOGGER.info("Create Usgs Quad Map for Data Source: " + pexmId);
        UsgsDto dto = dataSourceService.createUsgsQuadMap(new BigDecimal(pexmId), usgsCreationDto);
        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);
    }
    @PermissionsNeeded({
            @Permission(verb = Constants.DELETE, table = Constants.EXAM_USGS_MAP_XREF_TABLE)
    })
    public ResponseEntity<Void> deleteUsgsQuadMap(Long pexmId, Long utmpId) {
        LOGGER.info("Delete Usgs Quad Map");
        dataSourceService.deleteUsgsQuadMap(BigDecimal.valueOf(pexmId), BigDecimal.valueOf(utmpId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.AERIAL_TABLE),
    })
    public ResponseEntity<AerialPhotoPageDto> getAerialPhotos(Long pexmId, Integer pageNumber, Integer pageSize, AerialPhotoSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Search Aerial Photos for Data Source: " + pexmId);
        AerialPhotoPageDto dto = dataSourceService.getAerialPhotos(new BigDecimal(pexmId), pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.AERIAL_TABLE),
    })
    public ResponseEntity<AerialPhotoDto> createAerialPhoto(Long pexmId, AerialPhotoCreationDto aerialPhotoCreationDto) {
        LOGGER.info("Create Aerial Photo for Data Source: " + pexmId);
        AerialPhotoDto dto = dataSourceService.createAerialPhoto(new BigDecimal(pexmId), aerialPhotoCreationDto);
        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.AERIAL_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.AERIAL_TABLE),
    })
    public ResponseEntity<AerialPhotoDto> updateAerialPhoto(Long pexmId, Long aerialId, AerialPhotoCreationDto aerialPhotoCreationDto) {
        LOGGER.info("Change Aerial Photo");
        AerialPhotoDto dto = dataSourceService.updateAerialPhoto(BigDecimal.valueOf(pexmId), BigDecimal.valueOf(aerialId), aerialPhotoCreationDto);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.AERIAL_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.AERIAL_TABLE),
    })
    public ResponseEntity<Void> deleteAerialPhoto(Long pexmId, Long aerialId) {
        LOGGER.info("Delete Aerial Photo");
        dataSourceService.deleteAerialPhoto(BigDecimal.valueOf(pexmId), BigDecimal.valueOf(aerialId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_SURVEY_POU_EXAM_XREF),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RESOURCE_SURVEYS_TABLE)
    })
    public ResponseEntity<WaterResourceSurveyPageDto> getWaterSurveys(Long pexmId, Integer pageNumber, Integer pageSize, WaterResourceSurveySortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Search Water Resource Surveys for Data Source: " + pexmId);
        WaterResourceSurveyPageDto dto = dataSourceService.getWaterSurveys(new BigDecimal(pexmId), pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.WATER_SURVEY_POU_EXAM_XREF)
    })
    public ResponseEntity<WaterResourceSurveyDto> createWaterSourceSurvey(Long pexmId, WaterResourceSurveyCreationDto waterResourceSurveyCreationDto) {
        LOGGER.info("Create Water Resource Survey for Data Source: " + pexmId);
        WaterResourceSurveyDto dto = dataSourceService.createWaterSourceSurvey(new BigDecimal(pexmId), waterResourceSurveyCreationDto);
        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.DELETE, table = Constants.WATER_SURVEY_POU_EXAM_XREF)
    })
    public ResponseEntity<Void> deleteWaterResourceSurvey(Long pexmId, Long surveyId) {
        LOGGER.info("Delete Water Resource Survey");
        dataSourceService.deleteWaterResourceSurvey(BigDecimal.valueOf(pexmId), BigDecimal.valueOf(surveyId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.POU_EXAMINATIONS_XREF)
    })
    public ResponseEntity<Void> populateParcels(Long pexmId) {
        LOGGER.info("Populate Places Of Use in Data Source: " + pexmId);
        dataSourceService.populateParcels(new BigDecimal(pexmId));
        return new ResponseEntity<>(null, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_XREF)
    })
    public ResponseEntity<ParcelPageDto> getParcels(Long pexmId, Integer pageNumber, Integer pageSize, ParcelSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get Parcels of Data Source: " + pexmId);
        ParcelPageDto dto = dataSourceService.getParcels(new BigDecimal(pexmId), pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_XREF),
            @Permission(verb = Constants.UPDATE, table = Constants.POU_EXAMINATIONS_XREF)
    })
    public ResponseEntity<ParcelDto> updateParcel(Long pexmId, Long placeId, ParcelUpdateDto parcelUpdateDto) {
        LOGGER.info("Change Parcel");
        ParcelDto dto = dataSourceService.updateParcel(BigDecimal.valueOf(pexmId), BigDecimal.valueOf(placeId), parcelUpdateDto);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_XREF),
            @Permission(verb = Constants.DELETE, table = Constants.POU_EXAMINATIONS_XREF)
    })
    public ResponseEntity<Void> deleteParcel(Long pexmId, Long placeId) {
        LOGGER.info("Delete Parcel");
        dataSourceService.deleteParcel(BigDecimal.valueOf(pexmId), BigDecimal.valueOf(placeId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_SURVEY_POU_EXAM_XREF),
            @Permission(verb = Constants.DELETE, table = Constants.WATER_RESOURCE_SURVEYS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.COUNTIES_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.AERIAL_TABLE)
    })
    public ResponseEntity<AllReferencesDto> getExamInfoValues(Long pexmId) {
        LOGGER.info("Get Exam Info Values of Data Source");
        AllReferencesDto dto = dataSourceService.getExamInfoValues(BigDecimal.valueOf(pexmId));
        return ResponseEntity.ok(dto);
    }
}