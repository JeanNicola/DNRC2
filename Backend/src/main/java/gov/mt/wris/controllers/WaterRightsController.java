package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.WaterRightsApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class WaterRightsController implements WaterRightsApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(WaterRightsController.class);

    @Autowired
    private WaterRightService waterService;

    @Autowired
    private WaterRightOwnerService ownerService;

    @Autowired
    private WaterRightGeocodeService geocodeService;

    @Autowired
    private WaterRightVersionService versionService;

    @Autowired
    private WaterRightFileLocationService locationService;

    @Autowired
    private WaterRightOwnershipUpdateService updateService;

    @Autowired
    private VersionApplicationService versionApplicationService;

    @Autowired
    private CaseService caseService;

    @Autowired
    private VersionReservoirService versionReservoirService;

    @Autowired
    private VersionPodService podService;

    @Autowired
    private ObjectorsService objectorsService;

    @Autowired
    private ObjectionsService objectionsService;

    @Autowired
    private ElementObjectionService elementObjectionService;

    @Autowired
    private PurposeService purposeService;

    @Autowired
    private VersionMeasurementService measurementService;

    @Autowired
    private VersionRemarkService versionRemarkService;

    @Autowired
    private VersionCompactService versionCompactService;

    @Autowired
    private VersionHistoricalService versionHistoricalService;

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
    })
    public ResponseEntity<WaterRightPageDto> searchWaterRights(Integer pageNumber,
        Integer pageSize,
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
        LOGGER.info("Searching for Water Rights");

        WaterRightPageDto dto = waterService.searchWaterRights(pageNumber, pageSize, sortColumn, sortDirection, basin, waterRightNumber, ext, typeCode, statusCode, subBasin, waterReservationId, conservationDistrictNumber, countActiveChangeAuthorizationVersions);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<WaterRightVersionSearchPageDto> searchWaterRightByVersions(Integer pageNumber,
        Integer pageSize,
        WaterRightVersionSearchSortColumn sortColumn,
        DescSortDirection sortDirection,
        String waterRightNumber,
        String version,
        String versionType
    ) {
        LOGGER.info("Searching for Water Rights By Version");

        WaterRightVersionSearchPageDto dto = waterService.searchWaterRightsByVersions(pageNumber, pageSize, sortColumn, sortDirection, waterRightNumber, version, versionType);

        return ResponseEntity.ok(dto);
    }

    @Override
    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STAFF_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_OFFICE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<WaterRightDto> createWaterRight(WaterRightCreationDto creationDto) {
        LOGGER.info("Creating a new Water Right");

        WaterRightDto dto = waterService.createWaterRight(creationDto);

        return new ResponseEntity<WaterRightDto>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE)
    })
    public ResponseEntity<WaterRightViewDto> getWaterRight(Long waterRightId) {
        LOGGER.info("Getting a Water Right");

        WaterRightViewDto dto = waterService.getWaterRight(waterRightId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_TABLE)
    })
    public ResponseEntity<WaterRightViewDto> updateWaterRight(Long waterRightId, WaterRightUpdateDto updateDto) {
        LOGGER.info("Updating a Water Right");

        WaterRightViewDto dto = waterService.updateWaterRight(waterRightId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE)
    })
    public ResponseEntity<ChildRightPageDto> getChildRights(Long waterRightId,
        Integer pageNumber,
        Integer pageSize,
        ChildRightSortColumn sortColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Child Rights");

        ChildRightPageDto dto = waterService.getChildRights(pageNumber, pageSize, sortColumn, sortDirection, waterRightId);

        return ResponseEntity.ok(dto);
    }

    // Versions
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_VERSIONS_XREFS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.RELATED_RIGHT_TABLE)
    })
    public ResponseEntity<RelatedRightsPageDto> getWaterRightVersionRelatedRights(
        Long waterRightId,
        Long versionId,
        Integer pageNumber,
        Integer pageSize,
        RelatedRightSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of related rights belonging to a water right version");

        RelatedRightsPageDto dto = versionService.getVersionRelatedRights(pageNumber, pageSize, sortColumn, sortDirection, waterRightId, versionId);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE)
    })
    public ResponseEntity<VersionPageDto> getWaterRightVersions(Long waterRightId,
        Integer pageNumber,
        Integer pageSize,
        WaterRightVersionSortColumn sortColumn, 
        SortDirection sortDirection,
        String version,
        String versionType
    ) {
        LOGGER.info("Getting a page of versions belonging to a water right");

        VersionPageDto dto = versionService.getVersions(pageNumber, pageSize, sortColumn, sortDirection, waterRightId, version, versionType);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.VERSIONS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE)
    })
    public ResponseEntity<VersionDto> createWaterRightVersion(Long waterRightId, VersionCreationDto creationDto) {
        LOGGER.info("Creating a new Water Right Version");

        VersionDto dto = versionService.createWaterRightVersion(waterRightId, creationDto);

        return new ResponseEntity<VersionDto>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.VERSIONS_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSIONS_TABLE)
    })
    public ResponseEntity<VersionDto> createFirstVersion(Long waterRightId, FirstVersionCreationDto creationDto) {
        LOGGER.info("Creating the first Water Right Version");

        VersionDto dto = versionService.createFirstVersion(waterRightId, creationDto);

        return new ResponseEntity<VersionDto>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.EXECUTE, table = Constants.STANDARD_PROCEDURES)
    })
    public ResponseEntity<Void> applyVersionStandards(Long waterRightId) {
        LOGGER.info("Running standards for the Water Right Version");

        versionService.applyVersionStandards(waterRightId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionDto> editVersionStandards(Long waterRightId, Long versionNumber, VersionUpdateDto versionUpdateDto) {
        LOGGER.info("Editing a Water Right Version's Standards");

        VersionDto dto = versionService.updateVersionStandards(waterRightId, versionNumber, versionUpdateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionDto> editWaterRightVersion(Long waterRightId, Long versionNumber, VersionUpdateDto updateDto) {
        LOGGER.info("Editing a Water Right Version");

        VersionDto dto = versionService.updateWaterRightVersion(waterRightId, versionNumber, updateDto);

        return ResponseEntity.ok(dto);
    }

    // Owners
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<WaterRightOwnerPageDto> getWaterRightOwners(Long waterRightId,
        Integer pageNumber,
        Integer pageSize,
        WaterRightOwnerSortColumn sortColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Water Right Owners");

        WaterRightOwnerPageDto dto = ownerService.getWaterRightOwners(pageNumber, pageSize, sortColumn, sortDirection, waterRightId);
        
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.OWNER_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.DECREE_VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.DECREE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<WaterRightOwnerDto> editWaterRightOwner(Long waterRightId, Long ownerId, Long contactId, WaterRightOwnerUpdateDto updateDto) {
        LOGGER.info("Updating a Water Right Owner");

        WaterRightOwnerDto dto = ownerService.updateWaterRightOwner(waterRightId, ownerId, contactId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<WaterRightRepresentativePageDto> getWaterRightRepresentatives(Long waterRightId,
        Long ownerId,
        Long contactId,
        Integer pageNumber,
        Integer pageSize,
        WaterRightRepresentativeSortColumn sortColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Water Right Representatives");

        WaterRightRepresentativePageDto dto = ownerService.getWaterRightRepresentatives(pageNumber, pageSize, sortColumn, sortDirection, waterRightId, ownerId, contactId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<WaterRightRepresentativeDto> addWaterRightRepresentative(Long waterRightId,
        Long ownerId,
        Long contactId,
        WaterRightRepresentativeDto creationDto
    ) {
        LOGGER.info("Creating a Water Right Owner Representative");

        WaterRightRepresentativeDto dto = ownerService.addWaterRightRepresentative(waterRightId, ownerId, contactId, creationDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.REPRESENTATIVE_TABLE)
    })
    public ResponseEntity<WaterRightRepresentativeDto> editWaterRightRepresentative(Long waterRightId,
        Long ownerId,
        Long contactId,
        Long representativeId,
        WaterRightRepresentativeUpdateDto updateDto
    ) {
        LOGGER.info("Updating a Water Right Owner Representative");

        WaterRightRepresentativeDto dto = ownerService.editWaterRightRepresentative(waterRightId, ownerId, contactId, representativeId, updateDto);

        return ResponseEntity.ok(dto);
    }


    // File Location
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<ResponsibleOfficeDto> getWaterRightResponsibleOffice(Long waterRightId) {
        LOGGER.info("Getting the Water Right's responsible office"); 

        ResponsibleOfficeDto dto = locationService.getResponsibleOffice(waterRightId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<ResponsibleOfficeDto> editWaterRightResponsibleOffice(Long waterRightId, ResponsibleOfficeDto updateDto) {
        LOGGER.info("Changing the Water Right's responsible office"); 

        ResponsibleOfficeDto dto = locationService.editResponsibleOffice(waterRightId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_OFFICE_TABLE)
    })
    public ResponseEntity<OfficePageDto> getWaterRightOffices(Long waterRightId, Integer pageNumber, Integer pageSize, OfficeSortColumn sortColumn, DescSortDirection sortDirection) {
        LOGGER.info("Getting a page of Offices belonging to a Water Right");

        OfficePageDto dto = locationService.getWaterRightsOffices(waterRightId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_OFFICE_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.WATER_RIGHT_OFFICE_TABLE)
    })
    public ResponseEntity<OfficeDto> addWaterRightOffice(Long waterRightId, OfficeCreationDto dto) {
        LOGGER.info("Adding a new office to a Water Right");

        OfficeDto returnDto = locationService.addWaterRightOffice(waterRightId, dto);

        return new ResponseEntity<OfficeDto>(returnDto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_OFFICE_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_OFFICE_TABLE)
    })
    public ResponseEntity<OfficeDto> editWaterRightOffice(Long waterRightId, Long officeXrefId, OfficeDto dto) {
        LOGGER.info("Editing an Water Right's office");

        OfficeDto returnDto = locationService.editWaterRightOffice(waterRightId, officeXrefId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_OFFICE_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.WATER_RIGHT_OFFICE_TABLE)
    })
    public ResponseEntity<Void> deleteWaterRightOffice(Long waterRightId, Long officeXrefId) {
        LOGGER.info("Removing an office from a Water Right");

        locationService.deleteWaterRightOffice(waterRightId, officeXrefId);

        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STAFF_TABLE)
    })
    public ResponseEntity<StaffPageDto> getWaterRightStaff(Long waterRightId, Integer pageNumber, Integer pageSize, StaffSortColumn sortColumn, DescSortDirection sortDirection) {
        LOGGER.info("Getting a page of Offices belonging to a Water Right");

        StaffPageDto dto = locationService.getWaterRightStaff(waterRightId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STAFF_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.WATER_RIGHT_STAFF_TABLE)
    })
    public ResponseEntity<StaffDto> addWaterRightStaff(Long waterRightId, StaffCreationDto dto) {
        LOGGER.info("Adding a new staff member to a Water Right");

        StaffDto returnDto = locationService.addWaterRightStaff(waterRightId, dto);

        return new ResponseEntity<StaffDto>(returnDto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STAFF_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_STAFF_TABLE)
    })
    public ResponseEntity<StaffDto> editWaterRightStaff(Long waterRightId, Long officeXrefId, StaffDto dto) {
        LOGGER.info("Editing a Water Right's staff member");

        StaffDto returnDto = locationService.editWaterRightStaff(waterRightId, officeXrefId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STAFF_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.WATER_RIGHT_STAFF_TABLE)
    })
    public ResponseEntity<Void> deleteWaterRightStaff(Long waterRightId, Long staffXrefId) {
        LOGGER.info("Removing a staff member from an Water Right");

        locationService.deleteWaterRightStaff(waterRightId, staffXrefId);

        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    // Ownership Updates
    public ResponseEntity<WaterRightOwnershipUpdatePageDto> getWaterRightOwnershipUpdates(Long waterRightId,
        Integer pageNumber,
        Integer pageSize,
        WaterRightOwnershipSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Ownership Updates attached to a Water Right");

        WaterRightOwnershipUpdatePageDto pageDto = updateService.getWaterRightOwnershipUpdates(pageNumber, pageSize, sortColumn, sortDirection, waterRightId);

        return ResponseEntity.ok(pageDto);
    }

    // Geocodes
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.GEOCODE_TABLE)
    })
    public ResponseEntity<WaterRightGeocodePageDto> getWaterRightGeocodes(Long waterRightId, Integer pageNumber, Integer pageSize, WaterRightGeocodeSortColumn sortColumn, DescSortDirection sortDirection) {
        LOGGER.info("Getting a page of Water Right Geocodes");

        WaterRightGeocodePageDto dto = geocodeService.getWaterRightGeocodes(pageNumber, pageSize, sortColumn, sortDirection, waterRightId);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
    })
    public ResponseEntity<Void> addWaterRightGeocodes(Long waterRightId, WaterRightGeocodesCreationDto dto) {
        LOGGER.info("Adding Geocodes to a Water Right");

        geocodeService.addWaterRightGeocode(waterRightId, dto);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE)
    })
    public ResponseEntity<WaterRightGeocodeDto> editWaterRightGeocode(Long waterRightId, Long xrefId, WaterRightGeocodeDto dto) {
        LOGGER.info("Editing a Geocode attached to a Water Right");

        dto = geocodeService.editGeocode(waterRightId, xrefId, dto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
    })
    public ResponseEntity<Void> deleteWaterRightGeocode(Long waterRightId, Long xrefId) {
        LOGGER.info("Removing a Geocode from a Water Right");

        geocodeService.deleteGeocode(waterRightId, xrefId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
    })
    public ResponseEntity<Void> deleteInvalidWaterRightGeocodes(Long waterRightId) {
        LOGGER.info("Removing an Invalid Geocode from a Water Right");

        geocodeService.deleteInvalidGeocodes(waterRightId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
    })
    public ResponseEntity<Void> unresolveWaterRightGeocodes(Long waterRightId) {
        LOGGER.info("Unresolve Geocode from a Water Right");

        geocodeService.unresolveWaterRightGeocodes(waterRightId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
    })
    public ResponseEntity<Void> severWaterRightGeocodes(Long waterRightId) {
        LOGGER.info("Sever Geocode from a Water Right");

        geocodeService.severWaterRightGeocodes(waterRightId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    // Versions
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE)
    })
    public ResponseEntity<VersionDetailDto> getWaterRightVersionDetail(Long waterRightId, Long versionNumber) {

        LOGGER.info("Get Water Right Version detail");
        VersionDetailDto dto = versionService.getWaterRightVersionDetail(waterRightId, versionNumber);
        return ResponseEntity.ok(dto);

    }


    // Version Applications
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_APPLICATION_XREFS_TABLE)
    })
    public ResponseEntity<WaterRightVersionApplicationReferencesPageDto> getWaterRightVersionApplicationReferences(Long waterRightId,
                                                                                                                   Long versionNumber,
                                                                                                                   Integer pageNumber,
                                                                                                                   Integer pageSize,
                                                                                                                   WaterRightVersionApplicationReferencesSortColumn sortColumn,
                                                                                                                   SortDirection sortDirection) {
        LOGGER.info("Get Application references for Water Right Version");
        WaterRightVersionApplicationReferencesPageDto dto = versionApplicationService.getWaterRightVersionApplicationReferences(pageNumber, pageSize, sortColumn, sortDirection, waterRightId, versionNumber);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_APPLICATION_XREFS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.VERSION_APPLICATION_XREFS_TABLE)
    })
    public ResponseEntity<WaterRightVersionApplicationReferencesDto> addApplicationReferenceToWaterRightVersion(Long waterRightId,
                                                                                                         Long versionNumber,
                                                                                                         Long applicationId,
                                                                                                         Object body) {
        LOGGER.info("Add Application reference to a Water Right Version");
        WaterRightVersionApplicationReferencesDto dto = versionApplicationService.addApplicationReferenceToWaterRightVersion(waterRightId, versionNumber, applicationId);
        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTORS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<WaterRightVersionObjectorsPageDto> getWaterRightVersionObjectors(Long waterRightId,
                                                                                           Long versionNumber,
                                                                                           Long objectionId,
                                                                                           Integer pageNumber,
                                                                                           Integer pageSize,
                                                                                           WaterRightVersionObjectorsSortColumn sortColumn,
                                                                                           SortDirection sortDirection) {
        LOGGER.info("Get page of Water Right Version Objectors");
        WaterRightVersionObjectorsPageDto dto = objectorsService.getWaterRightVersionObjectors(new BigDecimal(waterRightId), new BigDecimal(versionNumber), new BigDecimal(objectionId), pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_APPLICATION_XREFS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.VERSION_APPLICATION_XREFS_TABLE)
    })
    public ResponseEntity<Void> deleteApplicationReferenceToWaterRightVersion(Long waterRightId,
                                                                       Long versionNumber,
                                                                       Long applicationId) {

        LOGGER.info("Delete Application reference to a Water Right Version");
        versionApplicationService.deleteApplicationReferenceToWaterRightVersion(waterRightId, versionNumber, applicationId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);

    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.DECREE_VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.DECREE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.DECREE_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<WaterRightVersionDecreesPageDto> getWaterRightVersionDecrees(
        Long waterRightId,
        Long versionNumber,
        Integer pageNumber,
        Integer pageSize,
        WaterRightVersionDecreeSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Water Right Version Decrees");

        WaterRightVersionDecreesPageDto dto =
            versionService.getWaterRightVersionDecrees(
                waterRightId,
                versionNumber,
                pageNumber,
                pageSize,
                sortColumn,
                sortDirection
            );

        return ResponseEntity.ok(dto);
    }

    // Version Reservoirs
    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.RESERVOIR_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.TRS_LOCATION_TABLE)
    })
    public ResponseEntity<WaterRightVersionReservoirsPageDto> getWaterRightVersionReservoirs(Long waterRightId,
        Long versionNumber,
        Integer pageNumber,
        Integer pageSize,
        WaterRightVersionReservoirsSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a page of Reservoirs that belong to a Version");

        WaterRightVersionReservoirsPageDto dto = versionReservoirService.getVersionReservoirs(pageNumber, pageSize, sortColumn, sortDirection, waterRightId, versionNumber);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.EXECUTE, table = Constants.COMMON_FUNCTIONS),
        @Permission(verb = Constants.INSERT, table = Constants.RESERVOIR_TABLE)
    })
    public ResponseEntity<Void> addVersionReservoir(Long waterRightId, Long versionNumber, ReservoirCreationDto dto) {
        LOGGER.info("Adding a Reservoir to a Water Right Version");

        versionReservoirService.addVersionReservoir(waterRightId, versionNumber, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.EXECUTE, table = Constants.COMMON_FUNCTIONS),
        @Permission(verb = Constants.SELECT, table = Constants.RESERVOIR_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.RESERVOIR_TABLE)
    })
    public ResponseEntity<Void> updateVersionReservoir(Long waterRightId, Long versionNumber, Long reservoirId, ReservoirCreationDto dto) {
        LOGGER.info("Updating a Reservoir attached to a Water Right Version");

        versionReservoirService.updateReservoir(waterRightId, versionNumber, reservoirId, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.RESERVOIR_TABLE)
    })
    public ResponseEntity<Void> deleteVersionReservoir(Long waterRightId, Long versionNumber, Long reservoirId) {
        LOGGER.info("Deleting a Reservoir from a Water Right Version");

        versionReservoirService.deleteReservoir(waterRightId, versionNumber, reservoirId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE)
    })
    public ResponseEntity<AllPodsDto> getAllVersionPODs(Long waterRightId, Long versionNumber) {
        LOGGER.info("Getting all the Points of Diversion associated with a Water Right Version");

        AllPodsDto dto = podService.getAllPods(waterRightId, versionNumber);

        return ResponseEntity.ok(dto);
    }

    // Version Objections
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.COURT_CASE_VERSION_XREF_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.WATER_COURT_CASE_TYPES_TABLE)
    })
    public ResponseEntity<WaterRightVersionCasesPageDto> getWaterRightVersionCases(Long waterRightId,
                                                                            Long versionNumber,
                                                                            Integer pageNumber,
                                                                            Integer pageSize,
                                                                            WaterRightVersionCasesSortColumn sortColumn,
                                                                            SortDirection sortDirection) {
        LOGGER.info("Get Water Right Version Court Cases");
        WaterRightVersionCasesPageDto dto = caseService.getWaterRightVersionCases(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(waterRightId), new BigDecimal(versionNumber));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<WaterRightVersionObjectionsPageDto> getWaterRightVersionObjections(Long waterRightId,
                                                                                      Long versionNumber,
                                                                                      Integer pageNumber,
                                                                                      Integer pageSize,
                                                                                      WaterRightVersionObjectionsSortColumn sortColumn,
                                                                                      SortDirection sortDirection) {
        LOGGER.info("Get water right version objections");
        WaterRightVersionObjectionsPageDto dto = objectionsService.getWaterRightVersionObjections(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(waterRightId), new BigDecimal(versionNumber));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ELEMENT_OBJECTION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.ELEMENT_TYPE_TABLE)
    })
    public ResponseEntity<WaterRightVersionObjectionsElementsPageDto> getWaterRightVersionObjectionsElements(Long waterRightId,
                                                                                                             Long versionNumber,
                                                                                                             Long objectionId,
                                                                                                             Integer pageNumber,
                                                                                                             Integer pageSize,
                                                                                                             WaterRightVersionObjectionsElementsSortColumn sortColumn,
                                                                                                             SortDirection sortDirection) {
        LOGGER.info("Get water right version element objections");
        WaterRightVersionObjectionsElementsPageDto dto = elementObjectionService.getWaterRightVersionObjectionElements(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(waterRightId), new BigDecimal(versionNumber), new BigDecimal(objectionId));
        return ResponseEntity.ok(dto);

    }


    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PURPOSES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CLIMATIC_AREAS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.IRRIGATION_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSE_IRRIGATION_XREF_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PURPOSE_IRRIGATION_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PLACE_OF_USES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<PurposeDetailDto> createWaterRightVersionPurpose(Long waterRightId,
                                                                           Long versionNumber,
                                                                           WaterRightVersionPurposeCreationDto creationDto) {
        LOGGER.info("Create new Water Right Version Purpose");
        PurposeDetailDto dto = purposeService.createPurpose(new BigDecimal(waterRightId), new BigDecimal(versionNumber), creationDto);
        return new ResponseEntity<PurposeDetailDto>(dto, null, HttpStatus.CREATED);

    }


    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_REMARKS_TABLE)
    )
    public ResponseEntity<VersionMeasurementReportsPageDto> getVersionMeasurementReports(Long waterRightId,
        Long versionNumber,
        Integer pageNumber,
        Integer pageSize,
        VersionMeasurementReportSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Get Measurement Reports for a Water Right Version");

        VersionMeasurementReportsPageDto dto = measurementService.getMeasurementReports(waterRightId, versionNumber, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.VERSION_REMARKS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_REMARKS_TABLE)
    })
    public ResponseEntity<VersionMeasurementReportDto> createMeasurementReport(Long waterRightId,
        Long versionNumber,
        VersionMeasurementReportDto creationDto
    ) {
        LOGGER.info("Creating a new Measurement Report");

        VersionMeasurementReportDto dto = measurementService.createMeasurementReport(waterRightId, versionNumber, creationDto);
        
        return new ResponseEntity<>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.VERSION_REMARKS_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_REMARKS_TABLE)
    })
    public ResponseEntity<VersionMeasurementReportDto> updateMeasurementReport(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        VersionMeasurementReportDto creationDto
    ) {
        LOGGER.info("Updating a Measurement Report");

        VersionMeasurementReportDto dto = measurementService.updateMeasurementReport(waterRightId, versionNumber, remarkId, creationDto);
        
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.DELETE, table = Constants.VERSION_REMARKS_TABLE)
    )
    public ResponseEntity<Void> deleteMeasurementReport(Long waterRightId,
        Long versionNumber,
        Long remarkId
    ) {
        LOGGER.info("Deleting a Measurement Report");

        measurementService.deleteMeasurementReport(remarkId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_ELEMENTS_TABLE)
    )
    public ResponseEntity<CommentsPageDto> getVersionMeasurementComments(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        Integer pageNumber,
        Integer pageSize,
        SortDirection sortDirection
    ) {
        LOGGER.info("Get Comments for a Measurement Report");

        CommentsPageDto dto = measurementService.getComments(remarkId, pageNumber, pageSize, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_ELEMENTS_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.REMARK_ELEMENTS_TABLE)
    })
    public ResponseEntity<CommentDto> updateVersionMeasurementComment(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        Long dataId,
        CommentDto updateDto
    ) {
        LOGGER.info("Updating a Measurment Report Comment");

        CommentDto dto = measurementService.updateComment(remarkId, dataId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_REMARK_MEASUREMENT_TABLE)
    )
    public ResponseEntity<MeasurementsPageDto> getVersionMeasurements(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        Integer pageNumber,
        Integer pageSize,
        MeasurementSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Get Measurements for a Measurement Report");

        MeasurementsPageDto dto = measurementService.getMeasurements(remarkId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.VERSION_REMARK_MEASUREMENT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE)
    })
    public ResponseEntity<MeasurementDto> createMeasurement(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        MeasurementDto createDto
    ) {
        LOGGER.info("Create a Measurement for a Measurement Report");

        MeasurementDto dto = measurementService.createMeasurement(waterRightId, versionNumber, remarkId, createDto);

        return new ResponseEntity(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_REMARK_MEASUREMENT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_REMARK_MEASUREMENT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE)
    })
    public ResponseEntity<MeasurementDto> updateMeasurement(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        Long measurementId,
        MeasurementDto updateDto
    ) {
        LOGGER.info("Updating a Measurement for a Measurement Report");

        MeasurementDto dto = measurementService.updateMeasurement(waterRightId, versionNumber, remarkId, measurementId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.DELETE, table = Constants.VERSION_REMARK_MEASUREMENT_TABLE)
    )
    public ResponseEntity<Void> deleteMeasurement(Long waterRightId,
        Long versionNumber,
        Long remarkId,
        Long measurementId
    ) {
        LOGGER.info("Deleting a Measurement from a Measurement Report");

        measurementService.deleteMeasurement(measurementId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_REMARKS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_CODES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_CATEGORIES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<VersionRemarksPageDto> getVersionRemarks(Long waterRightId,
        Long versionNumber,
        Integer pageNumber,
        Integer pageSize,
        VersionRemarksSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Get a page of Remarks that belong to a Water Right Version");

        VersionRemarksPageDto dto = versionRemarkService.getVersionRemarks(waterRightId, versionNumber, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_REMARKS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_CODES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_VARIABLE_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.VERSION_REMARKS_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.REMARK_ELEMENTS_TABLE)
    })
    public ResponseEntity<RemarkDto> createVersionRemark(Long waterRightId,
        Long versionNumber,
        VersionRemarkCreateDto createDto
    ) {
        LOGGER.info("Create a new Remark for a Version");

        RemarkDto dto = versionRemarkService.createRemark(waterRightId, versionNumber, createDto);

        return new ResponseEntity(dto, null, HttpStatus.CREATED);
    }


    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_COMPACT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.SUBCOMPACT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.COMPACT_TABLE)
    })
    public ResponseEntity<VersionCompactsPageDto> getVersionCompacts(Long waterRightId,
        Long versionNumber,
        Integer pageNumber,
        Integer pageSize,
        VersionCompactSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting the compacts attached to a Water Right Version");

        VersionCompactsPageDto dto = versionCompactService.getVersionCompacts(pageNumber, pageSize, sortColumn, sortDirection, waterRightId, versionNumber);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.VERSION_COMPACT_TABLE)
    })
    public ResponseEntity<VersionCompactDto> addVersionCompact(Long waterRightId, Long versionNumber, VersionCompactDto creationDto) {
        LOGGER.info("Adding a new Compact to a Version");

        VersionCompactDto dto = versionCompactService.createVersionCompact(waterRightId, versionNumber, creationDto);

        return new ResponseEntity(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_COMPACT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.SUBCOMPACT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.COMPACT_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_COMPACT_TABLE)
    })
    public ResponseEntity<VersionCompactDto> updateVersionCompact(Long waterRightId, Long versionNumber, Long compactId, VersionCompactDto creationDto) {
        LOGGER.info("Updating a Compact attached to a Version");

        VersionCompactDto dto = versionCompactService.updateVersionCompact(waterRightId, versionNumber, compactId, creationDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.VERSION_COMPACT_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_COMPACT_TABLE)
    })
    public ResponseEntity<Void> deleteVersionCompact(Long waterRightId, Long versionNumber, Long compactId) {
        LOGGER.info("Deleting a Compact attached to a Version");

        versionCompactService.deleteVersionCompact(compactId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.TRS_LOCATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.DITCH_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.DIVERSION_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MEANS_OF_DIVERSION_TABLE)
    })
    public ResponseEntity<VersionPodPageDto> getVersionPODs(Long waterRightId,
        Long versionNumber,
        Integer pageNumber,
        Integer pageSize,
        VersionPodSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting a Page of Point of Diversions that belong to a Water Right Version");

        VersionPodPageDto dto = podService.getVersionPods(waterRightId, versionNumber, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.EXECUTE, table = Constants.COMMON_FUNCTIONS)
    })
    public ResponseEntity<PodDto> createVersionPod(Long waterRightId,
        Long versionNumber,
        PodCreationDto creationDto
    ) {
        LOGGER.info("Creating a Point of Diversion");

        PodDto dto = podService.createVersionPod(waterRightId, versionNumber, creationDto);

        return new ResponseEntity(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.POINT_OF_DIVERSION_TABLE),
    })
    public ResponseEntity<PodDto> copyVersionPod(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodCopyDto copyDto
    ) {
        LOGGER.info("Copying a Point of Diversion");
        PodDto dto = podService.copyVersionPod(waterRightId, versionNumber, podId, copyDto);
        return new ResponseEntity(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.POINT_OF_DIVERSION_TABLE)
    })
    public ResponseEntity<Void> deletePod(Long waterRightId,
        Long versionNumber,
        Long podId
    ) {
        LOGGER.info("Deleting a Point of Diversion");

        podService.deletePod(waterRightId, versionNumber, podId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<FlowRateSummaryDto> getFlowRateSummary(Long waterRightId, Long versionNumber) {
        LOGGER.info("Getting the Flow Rate Summary of a Water Right Version");

        FlowRateSummaryDto dto = podService.getFlowRateSummary(waterRightId, versionNumber);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<FlowRateSummaryDto> editFlowRateSummary(Long waterRightId,
        Long versionNumber,
        FlowRateSummaryDtoResults updateDto
    ) {
        LOGGER.info("Getting the Flow Rate Summary of a Water Right Version");

        FlowRateSummaryDto dto = podService.updateFlowRateSummary(waterRightId, versionNumber, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MEANS_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.DITCH_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.DIVERSION_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.LEGAL_LAND_DESCRIPTION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.TRS_LOCATION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.SOURCE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.SOURCE_NAME_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MINOR_TYPE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.SUBDIVISION_CODES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.ZIP_CODE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.CITY_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.STATE_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
    })
    public ResponseEntity<PodDetailsDto> getPodDetails(Long waterRightId, Long versionNumber, Long podId) {
        LOGGER.info("Getting more information about a specific POD");

        PodDetailsDto dto = podService.getPodDetails(waterRightId, versionNumber, podId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.POINT_OF_DIVERSION_TABLE)
    })
    public ResponseEntity<Void> updateWellData(Long waterRightId,
        Long versionNumber,
        Long podId,
        WellDataUpdateDto updateDto
    ) {
        LOGGER.info("Updating the Well Data of a POD");

        podService.updateWellData(waterRightId, versionNumber, podId, updateDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.UPDATE, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.EXECUTE, table = Constants.COMMON_FUNCTIONS)
    })
    public ResponseEntity<Void> updatePodDetails(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodDetailsUpdateDto updateDto
    ) {
        LOGGER.info("Updating the POD Details");

        podService.updatePodDetails(waterRightId, versionNumber, podId, updateDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.ADDRESS_TABLE),
        @Permission(verb = Constants.DELETE, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<Void> updatePodAddress(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodAddressUpdateDto updateDto
    ) {
        LOGGER.info("Updating the POD Address");

        podService.updatePodAddress(waterRightId, versionNumber, podId, updateDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.POINT_OF_DIVERSION_TABLE)
    })
    public ResponseEntity<Void> editSubdivisionInfo(Long waterRightId,
        Long versionNumber,
        Long podId,
        SubdivisionUpdateDto updateDto
    ) {
        LOGGER.info("Updating the Subdivision Info of a POD");

        podService.updateSubdivision(waterRightId, versionNumber, podId, updateDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.POINT_OF_DIVERSION_TABLE)
    })
    public ResponseEntity<Void> updatePodSource(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodSourceUpdateDto updateDto
    ) {
        LOGGER.info("Updating a Point of Diversion's source");

        podService.updatePodSource(waterRightId, versionNumber, podId, updateDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
    })
    public ResponseEntity<PeriodOfDiversionPageDto> getPeriodOfDiversions(Long waterRightId,
        Long versionNumber,
        Long podId,
        Integer pageNumber,
        Integer pageSize,
        PeriodOfDiversionSortColumn sortColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting the Period of Diversions attached to a POD");

        PeriodOfDiversionPageDto dto = podService.getPeriodOfDiversions(waterRightId, versionNumber, podId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
        @Permission(verb = Constants.INSERT, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<PeriodOfDiversionDto> addPeriodOfDiversion(Long waterRightId,
        Long versionNumber,
        Long podId,
        PeriodOfDiversionDto creationDto
    ) {
        LOGGER.info("Creating a Period of Diversion");

        PeriodOfDiversionDto dto = podService.addPeriodOfDiversion(waterRightId, versionNumber, podId, creationDto);

        return new ResponseEntity(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.PERIOD_OF_USES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<PeriodOfDiversionDto> updatePeriodOfDiversion(Long waterRightId,
        Long versionNumber,
        Long podId,
        Long periodId,
        PeriodOfDiversionDto updateDto
    ) {
        LOGGER.info("Updating a Period of Diversion");

        PeriodOfDiversionDto dto = podService.updatePeriodOfDiversion(waterRightId, versionNumber, podId, periodId, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.PERIOD_OF_USES_TABLE)
    })
    public ResponseEntity<Void> deletePeriodOfDiversion(Long waterRightId,
        Long versionNumber,
        Long podId,
        Long periodId
    ) {
        LOGGER.info("Deleting a Period of Diversion");

        podService.deletePeriodOfDiversion(waterRightId, versionNumber, podId, periodId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE)
    })
    public ResponseEntity<PodEnforcementsPageDto> getEnforcements(Long waterRightId,
        Long versionNumber,
        Long podId,
        Integer pageNumber,
        Integer pageSize,
        EnforcementSortColumn sortColumn,
        SortDirection sortDirection
    ) {
        LOGGER.info("Getting the Enforcements attached to a POD");

        PodEnforcementsPageDto dto = podService.getEnforcements(waterRightId, versionNumber, podId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.INSERT, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE)
    })
    public ResponseEntity<PodEnforcementDto> addEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        PodEnforcementDto creationDto
    ) {
        LOGGER.info("Adding an enforcement to a POD");

        PodEnforcementDto dto = podService.addEnforcement(waterRightId, versionNumber, podId, creationDto);

        return new ResponseEntity(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE)
    })
    public ResponseEntity<PodEnforcementDto> updateEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        String areaId,
        String enforcementNumber,
        PodEnforcementDto updateDto
    ) {
        LOGGER.info("Updating an enforcement attached to a POD");

        PodEnforcementDto dto = podService.updateEnforcement(waterRightId, versionNumber, podId, areaId, enforcementNumber, updateDto);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.DELETE, table = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE)
    })
    public ResponseEntity<Void> deleteEnforcement(Long waterRightId,
        Long versionNumber,
        Long podId,
        String areaId,
        String enforcementNumber
    ) {
        LOGGER.info("Removing an enforcement from a POD");

        podService.deleteEnforcement(waterRightId, versionNumber, podId, areaId, enforcementNumber);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionVolumeDto> getVersionVolume(Long waterRightId, Long versionNumber) {
        LOGGER.info("Getting the volume information on a Version");

        VersionVolumeDto dto = versionService.getVersionVolume(waterRightId, versionNumber);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionHistoricalWithReferencesDto> getVersionHistorical(
        Long waterRightId,
        Long versionNumber
    ) {
        LOGGER.info("Getting the Historical data on a Version");
        VersionHistoricalWithReferencesDto dto = versionHistoricalService.getHistorical(waterRightId, versionNumber);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionHistoricalDto> updateVersionHistoricalPriorityDate(
        Long waterRightId,
        Long versionNumber,
        VersionHistoricalPriorityDateDto update
    ) {
        LOGGER.info("Updating the Historical Priority Date on a Version");

        VersionHistoricalDto dto = versionHistoricalService.updatePriorityDate(
            waterRightId,
            versionNumber,
            update
        );

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionVolumeDto> updateVersionVolume(
        Long waterRightId,
        Long versionNumber,
        VersionVolumeDto update
    ) {
        LOGGER.info("Updating the volume information on a Version");

        VersionVolumeDto dto = versionService.updateVersionVolume(waterRightId, versionNumber, update);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionHistoricalDto> updateVersionHistoricalClaimFiling(
        Long waterRightId,
        Long versionNumber,
        VersionHistoricalClaimFilingDto update
    ) {
        LOGGER.info("Updating the Historical Claim Filing data on a Version");

        VersionHistoricalDto dto = versionHistoricalService.updateClaimFiling(
            waterRightId,
            versionNumber,
            update
        );

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionAcreageDto> getVersionAcreage(Long waterRightId, Long versionNumber) {
        LOGGER.info("Getting the acreage information on a Version");

        VersionAcreageDto dto = versionService.getVersionAcreage(waterRightId, versionNumber);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionHistoricalDto> updateVersionHistoricalCourthouseFiling(
        Long waterRightId,
        Long versionNumber,
        VersionHistoricalCourthouseFilingDto update
    ) {
        LOGGER.info("Updating the Historical Courthouse Filing data on a Version");

        VersionHistoricalDto dto = versionHistoricalService.updateCourthouseFiling(
            waterRightId,
            versionNumber,
            update
        );

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionAcreageDto> updateVersionAcreage(
        Long waterRightId,
        Long versionNumber,
        VersionAcreageDto update
    ) {
        LOGGER.info("Updating the acreage information on a Version");

        VersionAcreageDto dto = versionService.updateVersionAcreage(waterRightId, versionNumber, update);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.VERSION_TABLE)
    })
    public ResponseEntity<VersionHistoricalDto> updateVersionHistoricalChanges(
        Long waterRightId,
        Long versionNumber,
        VersionHistoricalChangesDto update
    ) {
        LOGGER.info("Updating the Historical Data for Changes on a Version");

        VersionHistoricalDto dto = versionHistoricalService.updateChanges(
            waterRightId,
            versionNumber,
            update
        );

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_TABLE),
    })
    public ResponseEntity<EligibleWaterRightVersionPageDto> getEligibleWaterRightVersions(String basin, Integer pageNumber, Integer pageSize, EligibleWaterRightVersionSortColumn sortColumn, SortDirection sortDirection, String waterNumber) {
        LOGGER.info("Get List of eligible Water Right Versions for Objection or Counter Objection");
        EligibleWaterRightVersionPageDto dto = versionService.getEligibleWaterRightVersions(pageNumber, pageSize, sortColumn, sortDirection, basin, waterNumber);
        return ResponseEntity.ok(dto);
    }
}
