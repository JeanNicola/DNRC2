package gov.mt.wris.services;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.StaffAssignmentCreateDto;
import gov.mt.wris.dtos.StaffAssignmentDetailDto;
import gov.mt.wris.dtos.StaffAssignmentUpdateDto;
import gov.mt.wris.dtos.StaffAssignmentsPageDto;
import gov.mt.wris.dtos.StaffAssignmentsSortColumn;

import java.math.BigDecimal;

public interface CaseAssignmentService {

    public StaffAssignmentsPageDto getStaffAssignments(int pagenumber, int pagesize, StaffAssignmentsSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId);

    public StaffAssignmentDetailDto addStaffAssignment(Long caseId, StaffAssignmentCreateDto createDto);

    public void deleteStaffAssignment(Long caseId, Long assignmentId);

    public StaffAssignmentDetailDto updateStaffAssignment(Long caseId, Long assignmentId, StaffAssignmentUpdateDto updateDto);

}
