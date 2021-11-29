package gov.mt.wris.controllers;
import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.ReferencesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.AllRemarkCodeReferencesDto;
import gov.mt.wris.dtos.CaseAllReferencesDto;
import gov.mt.wris.services.DiversionTypeService;
import gov.mt.wris.services.MinorTypeService;
import gov.mt.wris.services.ReferenceService;
import gov.mt.wris.services.RemarkCodeService;
import gov.mt.wris.services.ReportTypeService;
import gov.mt.wris.services.WaterRightTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ReferencesController implements ReferencesApiDelegate{
    private static Logger LOGGER = LoggerFactory.getLogger(ReferencesController.class);

    @Autowired
    private ReferenceService refService;

    @Autowired
    private WaterRightTypeService typeService;

    @Autowired
    private ReportTypeService reportTypeService;

    @Autowired
    private RemarkCodeService remarkCodeService;

    @Autowired
    private DiversionTypeService diversionTypeService;

    @Autowired
    private MinorTypeService minorTypeService;

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getCaseAssignmentTypePrograms() {
        LOGGER.info("Return all the Case Assignment Type Programs");

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.CASE_ASSIGNMENT_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getCaseTypePrograms() {
        LOGGER.info("Return all the Case Type Programs");

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.CASE_TYPE_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getApplicationPaymentOrigins() {
        LOGGER.info("Return all the Application Payment Origins");

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.PAYMENT_APPLICATION_ORIGIN_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getApplicationPaymentFeeStatuses() {
        LOGGER.info("Return all the Application Payment Fee Statuses");

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.PAYMENT_APPLICATION_FEE_STATUS_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getYesNo() {
        LOGGER.info("Return a list of Yes/No");

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.YES_NO_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getDirections() {

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.DIRECTION);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getCardinalDirections() {

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.CARDINAL_DIRECTION);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getContactStatus() {

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.CUSTOMER_STATUS_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getContactSuffix() {

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.CUSTOMER_SUFFIX_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TYPES_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getContactType() {

        AllReferencesDto references = refService.findDistinctContactType();
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getFlowRateUnits() {
        LOGGER.info("Getting a list of Flow Rate Units");

        AllReferencesDto references = refService.findAllProgramsByTable(Constants.FLOW_RATE_UNIT_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    public ResponseEntity<AllReferencesDto> getElectronicContactTypes() {
        AllReferencesDto references = refService.findElectronicContactTypes(Constants.ELECTRONIC_CONTACT);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getOwnershipTransfers() {
        LOGGER.info("Getting a list of Ownership Transfers");

        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.OWNERSHIP_TRANSFER_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getCreationWaterRightTypes() {
        LOGGER.info("Getting the Water Right Types for Creation");

        AllReferencesDto dto = typeService.getWaterRightCreationTypes();

        return ResponseEntity.ok(dto);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getContractForDeedRle() {
        LOGGER.info("Getting a list of Contract For Deed RLE types");

        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.CONTRACT_FOR_DEED_RLE_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE)
    })
    public ResponseEntity<AllReferencesDto> getWaterRightTypes() {
        LOGGER.info("Getting all the Water Right Types");

        AllReferencesDto all = typeService.getWaterRightTypes();

        return ResponseEntity.ok(all);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getVersionTypes() {
        LOGGER.info("Return all the Version Type");

        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.VERSION_TYPE_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getOwnerOrigins() {
        LOGGER.info("Return all the Owner Origins");

        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.OWNER_ORIGIN_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getRelationshipTypes() {
        LOGGER.info("Getting a list of Relationship Types");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.RELATED_RIGHT_TYPE_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.ELEMENT_TYPE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getRelatedElementTypes() {

        AllReferencesDto references = refService.findElementType();
        return ResponseEntity.ok(references);
    }

    @Override
    public ResponseEntity<AllReferencesDto> getReportUrl(String env) {
        AllReferencesDto reference = refService.getReportUrl(env);
        return ResponseEntity.ok(reference);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getReservoirOrigins() {
        LOGGER.info("Getting a list of Reservoir Origins");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.RESERVOIR_ORIGIN_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getAliquotParts() {
        LOGGER.info("Getting a list of Aliquot Parts");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.ALIQUOT_PARTS_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getRangeDirections() {
        LOGGER.info("Getting a list of Range Directions");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.RANGE_DIRECTION_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getTownshipDirections() {
        LOGGER.info("Getting a list of Township Directions");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.TOWNSHIP_DIRECTION_DOMAIN);
        return ResponseEntity.ok(references);
    }


    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_CODES_TABLE),
        @Permission(verb = Constants.SELECT, table = Constants.REMARK_CATEGORIES_TABLE)
    })
    public ResponseEntity<AllRemarkCodeReferencesDto> getMeasurementReportRemarkCodes() {
        LOGGER.info("Getting all the Measurement Report Remark Codes");

        AllRemarkCodeReferencesDto dto = remarkCodeService.getReportRemarkCodes();

        return ResponseEntity.ok(dto);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.CLIMATIC_AREAS_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getClimaticAreas() {

        AllReferencesDto references = refService.findAllClimaticAreas();
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.PURPOSE_TYPES_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getPurposeTypes() {

        AllReferencesDto references = refService.findAllPurposeTypes();
        return ResponseEntity.ok(references);
    }

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.IRRIGATION_TYPES_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getIrrigationTypes() {

        AllReferencesDto references = refService.findAllIrrigationTypes();
        return ResponseEntity.ok(references);
    }
    
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getLeaseYearValues() {
        LOGGER.info("Getting a list of Lease Year Values");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.LEASE_YEAR_VALUE_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REPORT_TYPE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getReportTypes() {
        LOGGER.info("Getting all the Report Types");

        AllReferencesDto dto = reportTypeService.getAllReportTypes();

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getPODOrigins() {
        LOGGER.info("Getting a list of POD Origins");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.POINT_OF_DIVERSION_ORIGIN_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getMajorTypes() {
        LOGGER.info("Getting a list of Major Types");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.MAJOR_TYPE_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getPhotoTypes() {
        LOGGER.info("Getting a list of Photo Types");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByLowValue(Constants.PHOTO_TYPES);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getDataSourceTypes() {
        LOGGER.info("Getting a list of Data Source Types");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.DATA_SOURCE_TYPE);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_TABLE)
    )
    public ResponseEntity<CaseAllReferencesDto> getCaseTypeValues(Integer supported) {
        LOGGER.info("Getting Case Types");
        CaseAllReferencesDto dto = refService.findCaseTypes(supported);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getSourceOrigins() {
        LOGGER.info("Getting a list of Source Origins");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.POINT_OF_DIVERSION_SOURCE_ORIGIN_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getPODTypes() {
        LOGGER.info("Getting a list of POD Types");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.POINT_OF_DIVERSION_TYPE_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.DIVERSION_TYPE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getDiversionTypes() {
        LOGGER.info("Getting a list of Diversion Types");
        AllReferencesDto dto = diversionTypeService.getAllDiversionTypes();
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.MINOR_TYPE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getMinorTypes() {
        LOGGER.info("Getting a list of Minor Types");
        AllReferencesDto dto = minorTypeService.getAllMinorTypes();
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.MINOR_TYPE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getReservoirTypes() {
        LOGGER.info("Getting a list of Reservoir Types");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.RESERVOIR_TYPE_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getOrigins() {
        LOGGER.info("Getting a list of Element Origin");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.ORIGIN_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
        @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getFlowRateDescriptions() {
        LOGGER.info("Getting a list of Flow Rate Descriptions");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByCode(Constants.VERSION_FLOW_RATE_DESCRIPTION_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getObjectionStatuses() {
        LOGGER.info("Getting a list of Objection Statuses");
        AllReferencesDto references = refService.findAllProgramsByTable(Constants.OBJECTIONS_STATUS_DOMAIN);
        return ResponseEntity.ok(references);
    }

    public ResponseEntity<AllReferencesDto> getMaxVolumeDescriptions() {
        return ResponseEntity.ok(refService.findAllMaxVolumeDescriptions());
    }

    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getAllObjectionStatus() {
        LOGGER.info("Get list of Objection Status");
        AllReferencesDto references = refService.findAllProgramsByTableOrderByMeaning(Constants.OBJECTIONS_STATUS_DOMAIN);
        return ResponseEntity.ok(references);
    }

    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_TABLE)
    )
    public ResponseEntity<AllReferencesDto> getAllObjectionTypes(Integer supported) {
        LOGGER.info("Get Objection Types");
        AllReferencesDto dto = refService.findObjectionTypes(supported);
        return ResponseEntity.ok(dto);
    }
}
