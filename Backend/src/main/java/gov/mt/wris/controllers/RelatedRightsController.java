package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.RelatedRightsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.dtos.RelatedRightDto;
import gov.mt.wris.dtos.RelatedRightElementsPageDto;
import gov.mt.wris.dtos.RelatedRightElementsSortColumn;
import gov.mt.wris.dtos.RelatedRightSortColumn;
import gov.mt.wris.dtos.RelatedRightWaterRightPageDto;
import gov.mt.wris.dtos.RelatedRightWaterRightSortColumn;
import gov.mt.wris.dtos.RelatedRightsPageDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.UpdateRelatedRightDto;
import gov.mt.wris.services.RelatedRightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class RelatedRightsController implements RelatedRightsApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(RelatedRightsController.class);

    @Autowired
    RelatedRightService relatedRightService;

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_TABLE)
    })
    public ResponseEntity<RelatedRightsPageDto> searchRelatedRights(Integer pageNumber, Integer pageSize, RelatedRightSortColumn sortColumn, SortDirection sortDirection, String relatedRightId, String relationshipType, String waterRightNumber, String basin, String ext) {
        LOGGER.info("Search Related Right");
        return ResponseEntity.ok(relatedRightService.searchRelatedRights(pageNumber, pageSize, sortColumn, sortDirection, relatedRightId, relationshipType, waterRightNumber, basin, ext));
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_TABLE),
    })
    public ResponseEntity<RelatedRightDto> getRelatedRightDetails(Long relatedRightId) {
        LOGGER.info("Search for specific Related Right details");
        return ResponseEntity.ok(relatedRightService.getRelatedRightDetails(relatedRightId));
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_RELATED_RIGHT_VERS_XREFS),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE)
    })
    public ResponseEntity<RelatedRightWaterRightPageDto> getRelatedRightWaterRights(Long relatedRightId, Integer pageNumber, Integer pageSize, RelatedRightWaterRightSortColumn sortColumn, SortDirection sortDirection, String returnVersions) {
        LOGGER.info("Search Water Rights of a specific Related Right ");

        return ResponseEntity.ok(relatedRightService.getRelatedRightWaterRights(pageNumber, pageSize, sortColumn, sortDirection,relatedRightId, returnVersions));
    }

    @Override
    public ResponseEntity<RelatedRightDto> changeRelatedRight(Long relatedRightId, UpdateRelatedRightDto updateRelatedRightDto) {
        LOGGER.info("Change Related Right");
        RelatedRightDto relatedRightDto = relatedRightService.changeRelatedRight(BigDecimal.valueOf(relatedRightId), updateRelatedRightDto);
        return ResponseEntity.ok(relatedRightDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.SHARED_ELEMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_RELATED_RIGHT_VERS_XREFS),
            @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_TABLE)
    })
    @Override
    public ResponseEntity<Void> deleteRelatedRight(Long relatedRightId) {
        LOGGER.info("Delete Related Right");
        relatedRightService.deleteRelatedRight(BigDecimal.valueOf(relatedRightId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.SHARED_ELEMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.ELEMENT_TYPE_TABLE)
    })
    public ResponseEntity<RelatedRightElementsPageDto> getRelatedRightElements(Long relatedRightId,
                                                                               Integer pageNumber,
                                                                               Integer pageSize,
                                                                               RelatedRightElementsSortColumn sortColumn,
                                                                               SortDirection sortDirection) {

        LOGGER.info("Get Shared Elements for an Related Right");
        RelatedRightElementsPageDto dto = relatedRightService.getRelatedRightElements(pageNumber, pageSize, sortColumn, sortDirection, relatedRightId);
        return ResponseEntity.ok(dto);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.SHARED_ELEMENT_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.SHARED_ELEMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.ELEMENT_TYPE_TABLE)
    })
    public ResponseEntity<RelatedRightElementsSearchResultDto> createRelatedRightElement(Long relatedRightId,
                                                                                         RelatedRightElementCreationDto dto) {

        LOGGER.info("Create Shared Element for Related Right");
        RelatedRightElementsSearchResultDto element = relatedRightService.createRelatedRightElement(relatedRightId, dto);
        return new ResponseEntity<>(element, null, HttpStatus.CREATED);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.SHARED_ELEMENT_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.SHARED_ELEMENT_TABLE)
    })
    public ResponseEntity<Void> deleteRelatedRightElement(Long relatedRightId, String elementType) {

        LOGGER.info("Delete Shared Element for Related Right");
        relatedRightService.deleteRelatedRightElement(relatedRightId, elementType);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_VERSIONS_XREFS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.RELATED_RIGHT_VERSIONS_XREFS_TABLE)
    })
    public ResponseEntity<WaterRightReferenceToRelatedRightSearchResultDto> createWaterRightReferenceToRelatedRight(Long relatedRightId, WaterRightReferenceToRelatedRightCreationDto dtoIn) {

        LOGGER.info("Adding Water Right references to Related Right");
        WaterRightReferenceToRelatedRightSearchResultDto dto = relatedRightService.createWaterRightReferenceToRelatedRight(relatedRightId, dtoIn);
        return new ResponseEntity<WaterRightReferenceToRelatedRightSearchResultDto>(dto, null, HttpStatus.CREATED);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_VERSIONS_XREFS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.RELATED_RIGHT_VERSIONS_XREFS_TABLE)
    })
    public ResponseEntity<Void> deleteWaterRightReferenceToRelatedRight(Long relatedRightId, Long waterRightId, Long versionId) {

        LOGGER.info("Delete Water Right reference for Related Right");
        relatedRightService.deleteWaterRightReferenceToRelatedRight(relatedRightId, waterRightId, versionId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE)
    })
    public ResponseEntity<WaterRightsVersionsPageDto> searchWaterRightsVersions(Long relatedRightId, Integer pageNumber, Integer pageSize, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection, String basin, String waterNumber, String ext) {

        LOGGER.info("Search Water Rights Versions");
        WaterRightsVersionsPageDto page = relatedRightService.searchWaterRightsVersions(relatedRightId, pageNumber, pageSize, sortColumn, sortDirection, basin, waterNumber, ext);
        return ResponseEntity.ok(page);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.RELATED_RIGHT_TABLE)
    })
    public ResponseEntity<RelatedRightCreationResultDto> createRelatedRight(RelatedRightCreationDto newDto) {

        LOGGER.info("Create Related Right");
        RelatedRightCreationResultDto dto = relatedRightService.createRelatedRight(newDto);
        return new ResponseEntity<RelatedRightCreationResultDto>(dto, null, HttpStatus.CREATED);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE)
    })
    public ResponseEntity<WaterRightsVersionsPageDto> searchWaterRightsVersionsAll(Integer pageNumber, Integer pageSize, WaterRightVersionsForRelatedRightSortColumn sortColumn, SortDirection sortDirection, String basin, String waterNumber, String ext) {

        LOGGER.info("Search all Water Rights Versions");
        WaterRightsVersionsPageDto page = relatedRightService.searchWaterRightsVersionsAll(pageNumber, pageSize, sortColumn, sortDirection, basin, waterNumber, ext);
        return ResponseEntity.ok(page);

    }

}
