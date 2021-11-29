package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.ExaminationsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.services.ExaminationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class ExaminationsController implements ExaminationsApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(PurposesController.class);

    @Autowired
    private ExaminationService examinationsService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<ExaminationsSearchPageDto> searchExaminations(String basin, String waterRightNumber, String waterRightType, Integer pageNumber, Integer pageSize, ExaminationsSortColumn sortColumn, SortDirection sortDirection, String versionType, String version) {
        LOGGER.info("Search Examinations");
        ExaminationsSearchPageDto dto = examinationsService.searchExaminations(pageNumber, pageSize, sortColumn, sortDirection, basin, waterRightNumber, waterRightType, versionType, version);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<ExaminationDetailDto> getExamination(Long examinationId) {
        LOGGER.info("Get Examination");
        ExaminationDetailDto dto = examinationsService.getExamination(examinationId);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EXAMINATIONS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.EXAMINATIONS_TABLE)
    })
    public ResponseEntity<ExaminationDetailDto> updateExamination(Long examinationId, ExaminationCreationDto examinationCreationDto) {
        LOGGER.info("Change Examination");
        ExaminationDetailDto examinationDto = examinationsService.updateExamination(BigDecimal.valueOf(examinationId), examinationCreationDto);
        return ResponseEntity.ok(examinationDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_XREF)
    })
    public ResponseEntity<DataSourcePageDto> getExaminationDataSources(Long examinationId, Integer pageNumber, Integer pageSize, DataSourceSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Search Data Sources for Examination: " + examinationId);
        DataSourcePageDto dto = examinationsService.getExaminationDataSources(new BigDecimal(examinationId), pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EXAMINATIONS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.POU_EXAMINATIONS_TABLE)
    })
    public ResponseEntity<DataSourceDto> createDataSourceForExamination(Long examinationId, DataSourceCreationDto dataSourceCreationDto) {
        LOGGER.info("Create Data Source for Examination: " + examinationId);
        DataSourceDto dto = examinationsService.createDataSourceForExamination(new BigDecimal(examinationId), dataSourceCreationDto);
        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.POU_EXAMINATIONS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.POU_EXAMINATIONS_TABLE)
    })
    public ResponseEntity<Void> deleteDataSource(Long examinationId, Long pexmId) {
        LOGGER.info("Delete Data Source");
        examinationsService.deleteDataSource(BigDecimal.valueOf(examinationId), BigDecimal.valueOf(pexmId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }
}
