package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.StaffAssignmentCreateDto;
import gov.mt.wris.dtos.StaffAssignmentDetailDto;
import gov.mt.wris.dtos.StaffAssignmentUpdateDto;
import gov.mt.wris.dtos.StaffAssignmentsPageDto;
import gov.mt.wris.dtos.StaffAssignmentsSortColumn;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.CaseAssignment;
import gov.mt.wris.repositories.CaseAssignmentRepository;
import gov.mt.wris.services.CaseAssignmentService;
import gov.mt.wris.utils.Helpers;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CaseAssignmentServiceImpl  implements CaseAssignmentService {

    private static Logger LOGGER = LoggerFactory.getLogger(CaseServiceImpl.class);
    private static String OnlyOneActivePersonException = "Only one active person per role is " +
        "allowed. Please delete this record, end date the previous person for this role, " +
        "save, and then add the record.";

    @Autowired
    private CaseAssignmentRepository caseAssignmentRepository;

    public StaffAssignmentsPageDto getStaffAssignments(int pageNumber, int pageSize, StaffAssignmentsSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId) {

        LOGGER.info("Get Staff assigned to Court Case or Hearing");

        Sort sortDtoColumn = getStaffAssignmentsSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<CaseAssignment> results = caseAssignmentRepository.findAllByCaseId(request, caseId);

        StaffAssignmentsPageDto page = new StaffAssignmentsPageDto();
        page.setResults(results.getContent().stream().map(c -> {
            return getStaffAssignmentDetailDto(c);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getStaffAssignmentsSortColumn(StaffAssignmentsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();

        switch (sortColumn) {
            case COMPLETENAME:
                orders.add(new Sort.Order(direction, "msi.lastName"));
                orders.add(new Sort.Order(direction, "msi.firstName"));
                orders.add(new Sort.Order(direction, "msi.midInitial"));
                break;
            case ASSIGNMENTTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "cat.description"));
                break;
            case BEGINDATE:
                orders.add(new Sort.Order(direction, "beginDate"));
                break;
            case ENDDATE:
                orders.add(new Sort.Order(direction, "endDate"));
                break;
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "msi.lastName"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "msi.firstName"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "msi.midInitial"));
        return Sort.by(orders);

    }

    private StaffAssignmentDetailDto getStaffAssignmentDetailDto(CaseAssignment model) {

        StaffAssignmentDetailDto dto = new StaffAssignmentDetailDto();
        dto.setAssignmentId(model.getAssignmentId().longValue());
        if (model.getDnrcId() != null) dto.setDnrcId(Long.valueOf(model.getDnrcId()));
        dto.setAssignmentType(model.getAssignmentTypeCode());
        dto.setAssignmentTypeDescription(model.getCaseAssignmentType()!=null?model.getCaseAssignmentType().getDescription():null);
        dto.setBeginDate(model.getBeginDate()!=null?model.getBeginDate():null);
        dto.setEndDate(model.getEndDate()!=null?model.getEndDate():null);
        if (model.getMasterStaffIndexes()!=null)
            dto.setCompleteName(
                Helpers.buildName(
                    model.getMasterStaffIndexes().getLastName(),
                    model.getMasterStaffIndexes().getFirstName(),
                    model.getMasterStaffIndexes().getMidInitial()
                )
            );
        return dto;

    }

    public StaffAssignmentDetailDto addStaffAssignment(Long caseId, StaffAssignmentCreateDto createDto) {

        try {
            return addStaffAssignmentTransaction(caseId, createDto);
        } catch (DataIntegrityViolationException e){
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.ASSN_CASE_FK")) {
                    throw new DataIntegrityViolationException("The case id " + caseId + " does not exist");
                } else if (constraintMessage.contains("WRD.ASSN_ASST_FK")) {
                    throw new DataIntegrityViolationException("The assignment type " + createDto.getAssignmentType() + " does not exist");
                } else if (constraintMessage.contains("WRD.ASSN_MSI_FK")) {
                    throw new DataIntegrityViolationException("The staff id " + createDto.getDnrcId() + " does not exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }

    }

    @Transactional
    private StaffAssignmentDetailDto addStaffAssignmentTransaction(Long caseId, StaffAssignmentCreateDto createDto) {

        LOGGER.info("Add Staff Assignment to Court Case or Hearing");

        if (caseAssignmentRepository.countCaseAssignmentByCaseIdAndAssignmentTypeCodeAndEndDateIsNull(new BigDecimal(caseId), createDto.getAssignmentType()) > 0)
            throw new DataIntegrityViolationException(OnlyOneActivePersonException);

        CaseAssignment ca = new CaseAssignment();
        ca.setCaseId(new BigDecimal(caseId));
        ca.setAssignmentTypeCode(createDto.getAssignmentType());
        ca.setDnrcId(createDto.getDnrcId());
        ca.setBeginDate(createDto.getBeginDate());
        ca.setEndDate(createDto.getEndDate()!=null?createDto.getEndDate():null);
        return getStaffAssignmentDetailDto(caseAssignmentRepository.saveAndFlush(ca));

    }

    public void deleteStaffAssignment(Long caseId, Long assignmentId) {

        LOGGER.info("Delete Staff Assignment for Court Case or Hearing");
        caseAssignmentRepository.deleteCaseAssignmentByCaseIdAndAssignmentId(new BigDecimal(caseId), new BigDecimal(assignmentId));

    }

    public StaffAssignmentDetailDto updateStaffAssignment(Long caseId, Long assignmentId, StaffAssignmentUpdateDto updateDto) {

        LOGGER.info("Update Staff Assignment for Court Case or Hearing");

        Optional<CaseAssignment> fndCase =  caseAssignmentRepository.findByCaseIdAndAssignmentId(new BigDecimal(caseId), new BigDecimal(assignmentId));
        if (!fndCase.isPresent())
            throw new NotFoundException(String.format("Case Id %s or Assignment Id %s not found.",caseId, assignmentId));
        CaseAssignment oldCase = fndCase.get();
        if (updateDto.getAssignmentType()!=null) {
            if (caseAssignmentRepository.countCaseAssignmentByCaseIdAndAssignmentTypeCodeAndAssignmentIdNotAndEndDateIsNull(new BigDecimal(caseId), updateDto.getAssignmentType(), new BigDecimal(assignmentId)) > 0)
                throw new DataIntegrityViolationException(OnlyOneActivePersonException);
            oldCase.setAssignmentTypeCode(updateDto.getAssignmentType());
        }
        oldCase.setDnrcId(updateDto.getDnrcId());
        oldCase.setBeginDate(updateDto.getBeginDate()!=null?updateDto.getBeginDate():null);
        oldCase.setEndDate(updateDto.getEndDate()!=null?updateDto.getEndDate():null);
        return getStaffAssignmentDetailDto(caseAssignmentRepository.saveAndFlush(oldCase));

    }

}
