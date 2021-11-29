package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.CasesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.CaseCreationDto;
import gov.mt.wris.dtos.CaseDto;
import gov.mt.wris.dtos.CaseRegisterCreateUpdateDto;
import gov.mt.wris.dtos.CaseRegisterDetailDto;
import gov.mt.wris.dtos.CaseRegisterPageDto;
import gov.mt.wris.dtos.CaseRegisterSortColumn;
import gov.mt.wris.dtos.CaseSearchResultPageDto;
import gov.mt.wris.dtos.CaseSearchSortColumn;
import gov.mt.wris.dtos.CaseUpdateDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.*;
import gov.mt.wris.services.CaseAssignmentService;
import gov.mt.wris.services.CaseScheduleService;
import gov.mt.wris.services.CaseService;
import gov.mt.wris.services.CaseWaterRightVersionService;
import gov.mt.wris.services.DistrictCourtCaseService;
import gov.mt.wris.services.EventService;
import gov.mt.wris.services.ObjectionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class CasesController implements CasesApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(CasesController.class);

    @Autowired
    private CaseService caseService;

    @Autowired
    private CaseAssignmentService caseAssignmentService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CaseScheduleService caseScheduleService;

    @Autowired
    private DistrictCourtCaseService districtCourtCaseService;

    @Autowired
    private CaseWaterRightVersionService caseWaterRightVersionService;

    @Autowired
    private ObjectionsService objectionsService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_APPLICATION_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_STATUS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TYPE_TABLE)
    })
    public ResponseEntity<CaseSearchResultPageDto> searchCases(Integer pageNumber,
                                                        Integer pageSize,
                                                        CaseSearchSortColumn sortColumn,
                                                        SortDirection sortDirection,
                                                        String applicationId,
                                                        String caseNumber,
                                                        String caseTypeCode,
                                                        String caseStatusCode,
                                                        String waterCourtCaseNumber) {
        LOGGER.info("Search Cases and Hearings");
        CaseSearchResultPageDto dto = caseService.searchCases(pageNumber, pageSize, sortColumn, sortDirection, applicationId, caseNumber, caseTypeCode, caseStatusCode, waterCourtCaseNumber);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_APPLICATION_XREF_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.CASE_APPLICATION_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.WATER_COURT_CASE_TYPES_TABLE)
    })
    public ResponseEntity<CaseDto> createCourtCase(CaseCreationDto creationDto) {

        LOGGER.info("Create Court Case or Hearing");
        CaseDto dto = caseService.createCase(creationDto);
        return new ResponseEntity<CaseDto>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE)
    })
    public ResponseEntity<CaseDto> getCourtCase(Long caseId) {

        LOGGER.info("Get Court Case or Hearing");
        CaseDto dto = caseService.getCourtCase(caseId);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_TABLE)
    })
    public ResponseEntity<CaseDto> updateCourtCase(Long caseId, CaseUpdateDto updateDto) {

        LOGGER.info("Update Court Case or Hearing");
        CaseDto dto = caseService.updateCourtCase(caseId, updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
    })
    public ResponseEntity<Void> deleteCaseHearing(Long caseId) {
        LOGGER.info("Delete Court Case");
        caseService.deleteCaseHearing(BigDecimal.valueOf(caseId));
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

        @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<CaseRegisterPageDto> getCaseEvents(Long caseId,
                                                             Integer pageNumber,
                                                             Integer pageSize,
                                                             CaseRegisterSortColumn sortColumn,
                                                             SortDirection sortDirection) {
        LOGGER.info("Get Register of Events for Court Case or Hearing");
        CaseRegisterPageDto dto = caseService.getCaseEvents(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(caseId));
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_TABLE)
    })
    public ResponseEntity<CaseRegisterDetailDto> updateCaseEvent(Long caseId, Long eventId, CaseRegisterCreateUpdateDto updateDto) {

        LOGGER.info("Update Court Case or Hearing Event");
        CaseRegisterDetailDto dto = caseService.updateCaseEvent(caseId, eventId, updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_TYPE_TABLE)
    })
    public ResponseEntity<CaseRegisterDetailDto> createCaseEvent(Long caseId, CaseRegisterCreateUpdateDto createDto) {

        LOGGER.info("Create Court Case or Hearing Event");
        CaseRegisterDetailDto dto = caseService.createCaseEvent(caseId, createDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.DELETE, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<Void> deleteCaseEvent(Long caseId, Long eventId) {

        LOGGER.info("Delete Event from Case Register");
        eventService.deleteCaseRegisterEvent(new BigDecimal(caseId), new BigDecimal(eventId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE)
    })
    public ResponseEntity<ApplicantsPageDto> getCaseApplicationApplicants(Long caseId,
                                                                   Integer pageNumber,
                                                                   Integer pageSize,
                                                                   ApplicantSortColumn sortColumn,
                                                                   SortDirection sortDirection) {

        LOGGER.info("Get associated Application Applicants for Court Case or Hearing");
        ApplicantsPageDto dto = caseService.getCaseApplicationApplicants(pageNumber, pageSize, sortColumn, sortDirection, caseId);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_APPLICATION_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_STATUS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<ObjectionsPageDto> getCaseApplicationObjections(Long caseId,
                                                                   Integer pageNumber,
                                                                   Integer pageSize,
                                                                   ObjectionSortColumn sortColumn,
                                                                   SortDirection sortDirection) {

        LOGGER.info("Get associated Application Objections for Court Case or Hearing");
        ObjectionsPageDto dto = caseService.getCaseApplicationObjections(pageNumber, pageSize, sortColumn, sortDirection, caseId);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_ASSIGNMENTS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CASE_ASSIGNMENT_TYPES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE)
    })
    public ResponseEntity<StaffAssignmentsPageDto> getStaffAssignments(Long caseId,
                                                                       Integer pageNumber,
                                                                       Integer pageSize,
                                                                       StaffAssignmentsSortColumn sortColumn,
                                                                       SortDirection sortDirection) {

        LOGGER.info("Get Staff assigned to Court Case or Hearing");
        StaffAssignmentsPageDto dto = caseAssignmentService.getStaffAssignments(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(caseId));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_ASSIGNMENTS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.CASE_ASSIGNMENTS_TABLE)
    })
    public ResponseEntity<StaffAssignmentDetailDto> addStaffAssignment(Long caseId, StaffAssignmentCreateDto createDto) {

        LOGGER.info("Add Staff Assignment to Court Case or Hearing");
        StaffAssignmentDetailDto dto = caseAssignmentService.addStaffAssignment(caseId, createDto);
        return new ResponseEntity<StaffAssignmentDetailDto>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_ASSIGNMENTS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.CASE_ASSIGNMENTS_TABLE)
    })
    public ResponseEntity<Void> deleteStaffAssignment(Long caseId, Long assignmentId) {

        LOGGER.info("Delete Staff Assignment for Court Case or Hearing");
        caseAssignmentService.deleteStaffAssignment(caseId, assignmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_ASSIGNMENTS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.CASE_ASSIGNMENTS_TABLE)
    })
    public ResponseEntity<StaffAssignmentDetailDto> updateStaffAssignment(Long caseId,
                                                                          Long assignmentId,
                                                                          StaffAssignmentUpdateDto updateDto) {
        LOGGER.info("Update Staff Assignment for Court Case or Hearing");
        StaffAssignmentDetailDto dto = caseAssignmentService.updateStaffAssignment(caseId, assignmentId, updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE)
    })
    public ResponseEntity<CaseCommentsDto> getCaseComments(Long caseId) {

        LOGGER.info("Get Comments for Case or Hearing");
        CaseCommentsDto dto = caseService.getCaseComments(caseId);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_COURT_CASE_TYPES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WATER_COURT_CASE_TYPES_TABLE)
    })
    public ResponseEntity<CaseCommentsDto> updateCaseComments(Long caseId, CaseCommentsDto updateDto) {

        LOGGER.info("Update Comments for Case or Hearing");
        CaseCommentsDto dto = caseService.updateCaseComments(caseId, updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_SCHEDULE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE)
    })
    public ResponseEntity<ScheduleEventsPageDto> getScheduleEvents(Long caseId,
                                                                   Integer pageNumber,
                                                                   Integer pageSize,
                                                                   ScheduleEventsSortColumn sortColumn,
                                                                   SortDirection sortDirection) {

        LOGGER.info("Get Schedule of Events for Court Case or Hearing");
        ScheduleEventsPageDto dto = caseScheduleService.getScheduleEvents(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(caseId));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_SCHEDULE_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.CASE_SCHEDULE_TABLE)
    })
    public ResponseEntity<ScheduleEventDetailDto> createScheduleEvent(Long caseId, ScheduleEventCreateDto createDto) {

        LOGGER.info("Create Schedule Event for Court Case or Hearing");
        ScheduleEventDetailDto dto = caseScheduleService.createScheduleEvent(caseId, createDto);
        return new ResponseEntity<ScheduleEventDetailDto>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_SCHEDULE_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.CASE_SCHEDULE_TABLE)
    })
    public ResponseEntity<ScheduleEventDetailDto> updateScheduleEvent(Long caseId,
                                                                      Long scheduleId,
                                                                      ScheduleEventUpdateDto updateDto) {

        LOGGER.info("Update Schedule Event for Court Case or Hearing");
        ScheduleEventDetailDto dto = caseScheduleService.updateScheduleEvent(caseId, scheduleId, updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CASE_SCHEDULE_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.CASE_SCHEDULE_TABLE)
    })
    public ResponseEntity<Void> deleteScheduleEvent(Long caseId, Long scheduleId) {

        LOGGER.info("Delete Schedule Event for Court Case or Hearing");
        caseScheduleService.deleteScheduleEvent(caseId, scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DISTRICT_COURT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.COUNTIES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<DistrictCourtsPageDto> getDistrictCourts(Long caseId,
                                                                   Integer pageNumber,
                                                                   Integer pageSize,
                                                                   DistrictCourtsSortColumn sortColumn,
                                                                   SortDirection sortDirection) {

        LOGGER.info("Get District Court details for Court Case or Hearing");
        DistrictCourtsPageDto dto = districtCourtCaseService.getDistrictCourts(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(caseId));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DISTRICT_COURT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<DistrictCourtEventsPageDto> getDistrictCourtEvents(Long caseId,
                                                                   Long districtId,
                                                                   Integer pageNumber,
                                                                   Integer pageSize,
                                                                   DistrictCourtEventsSortColumn sortColumn,
                                                                   SortDirection sortDirection) {

        LOGGER.info("Get District Court Events for Court Case or Hearing");
        DistrictCourtEventsPageDto dto = districtCourtCaseService.getDistrictCourtEvents(pageNumber, pageSize, sortColumn, sortDirection, new BigDecimal(caseId), new BigDecimal(districtId));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DISTRICT_COURT_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.DISTRICT_COURT_TABLE)
    })
    public ResponseEntity<DistrictCourtDetailDto> createDistrictCourt(Long caseId, DistrictCourtCreateDto createDto) {

        LOGGER.info("Create District Court for Court Case or Hearing");
        DistrictCourtDetailDto dto = districtCourtCaseService.createDistrictCourt(caseId, createDto);
        return new ResponseEntity<DistrictCourtDetailDto>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DISTRICT_COURT_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.DISTRICT_COURT_TABLE)
    })
    public ResponseEntity<DistrictCourtDetailDto> updateDistrictCourt(Long caseId,
                                                                      Long districtId,
                                                                      DistrictCourtUpdateDto updateDto) {

        LOGGER.info("Update District Court for Court Case or Hearing");
        DistrictCourtDetailDto dto = districtCourtCaseService.updateDistrictCourt(caseId, districtId, updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DISTRICT_COURT_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.DISTRICT_COURT_TABLE)
    })
    public ResponseEntity<Void> deleteDistrictCourt(Long caseId, Long districtId) {

        LOGGER.info("Delete District Court for Court Case or Hearing");
        districtCourtCaseService.deleteDistrictCourt(caseId, districtId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DISTRICT_COURT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<DistrictCourtEventDetailDto> createDistrictCourtEvent(Long caseId,
                                                                                Long districtId,
                                                                                DistrictCourtEventCreateDto createDto) {

        LOGGER.info("Create District Court Event for Court Case or Hearing");
        DistrictCourtEventDetailDto dto = districtCourtCaseService.createDistrictCourtEvent(caseId, districtId, createDto);
        return new ResponseEntity<DistrictCourtEventDetailDto>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DISTRICT_COURT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<DistrictCourtEventDetailDto> updateDistrictCourtEvent(Long caseId,
                                                                                Long districtId,
                                                                                Long eventDateId,
                                                                                DistrictCourtEventUpdateDto updateDto) {

        LOGGER.info("Update District Court Event for Court Case or Hearing");
        DistrictCourtEventDetailDto dto = districtCourtCaseService.updateDistrictCourtEvent(caseId, districtId, eventDateId, updateDto);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.DISTRICT_COURT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.EVENT_TABLE)
    })
    public ResponseEntity<Void> deleteDistrictCourtEvent(Long caseId, Long districtId, Long eventDateId) {

        LOGGER.info("Delete District Court Event for Court Case or Hearing");
        districtCourtCaseService.deleteDistrictCourtEvent(caseId, districtId, eventDateId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.COURT_CASE_VERSION_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE)
    })
    public ResponseEntity<CaseWaterRightVersionsPageDto> getCaseWaterRightVersions(Long caseId,
                                                                                   Integer pageNumber,
                                                                                   Integer pageSize,
                                                                                   CaseWaterRightVersionsSortColumn sortColumn,
                                                                                   SortDirection sortDirection) {

        LOGGER.info("Get Case Water Right Versions");
        CaseWaterRightVersionsPageDto dto = caseWaterRightVersionService.getCaseWaterRightVersions(pageNumber, pageSize, sortColumn, sortDirection, caseId);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<CaseWaterRightVersionObjectionsPageDto> getCaseWaterRightVersionObjections(Long caseId,
                                                                                                     Long waterRightId,
                                                                                                     Long version,
                                                                                                     Integer pageNumber,
                                                                                                     Integer pageSize,
                                                                                                     CaseWaterRightVersionObjectionsSortColumn sortColumn,
                                                                                                     SortDirection sortDirection
                                                                                                     ) {

        LOGGER.info("Get Case Water Right Version Objections");
        CaseWaterRightVersionObjectionsPageDto dto = caseWaterRightVersionService.getCaseWaterRightVersionObjections(pageNumber, pageSize, sortColumn, sortDirection, caseId, waterRightId, Long.valueOf(version));
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.COURT_CASE_VERSION_XREF_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.COURT_CASE_VERSION_XREF_TABLE)
    })
    public ResponseEntity<CaseWaterRightVersionReferenceDto> createCaseWaterRightVersionReference(Long caseId, CaseWaterRightVersionReferenceDto createDto) {

        LOGGER.info("Create Court Case Water Right Version Reference");
        CaseWaterRightVersionReferenceDto dto = caseWaterRightVersionService.createCaseWaterRightVersionReference(caseId, createDto);
        return new ResponseEntity<CaseWaterRightVersionReferenceDto>(dto, null, HttpStatus.CREATED);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.COURT_CASE_VERSION_XREF_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.COURT_CASE_VERSION_XREF_TABLE)
    })
    public ResponseEntity<Void> deleteCaseWaterRightVersionReference(Long caseId, Long waterRightId, Long version) {

        LOGGER.info("Delete District Court for Court Case or Hearing");
        caseWaterRightVersionService.deleteCaseWaterRightVersionReference(caseId, waterRightId, Long.valueOf(version));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.DECREE_VERSION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.REFERENCE_TABLE)
    })
    public ResponseEntity<EligibleWaterRightsPageDto> getEligibleWaterRights(Long decreeId,
                                                                             String basin,
                                                                             Integer pageNumber,
                                                                             Integer pageSize,
                                                                             EligibleWaterRightsSortColumn sortColumn,
                                                                             SortDirection sortDirection,
                                                                             String waterNumber) {

        LOGGER.info("Get list of eligible Water Rights for Court Case or Hearing");
        EligibleWaterRightsPageDto dto = caseWaterRightVersionService.getEligibleWaterRights(pageNumber, pageSize, sortColumn, sortDirection, waterNumber, String.valueOf(decreeId), basin);
        return ResponseEntity.ok(dto);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.OBJECTIONS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.OBJECTIONS_TABLE)
    })
    public ResponseEntity<ObjectionDto> updateObjection(Long objectionId, ObjectionUpdateDto objectionUpdateDto) {
        LOGGER.info("Update Objection");
        ObjectionDto dto = objectionsService.updateObjection(new BigDecimal(objectionId), objectionUpdateDto);
        return ResponseEntity.ok(dto);
    }
}
