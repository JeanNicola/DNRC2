package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.CaseApplicationXref;
import gov.mt.wris.models.CaseType;
import gov.mt.wris.models.CourtCase;
import gov.mt.wris.models.CourtCaseVersionXref;
import gov.mt.wris.models.Decree;
import gov.mt.wris.models.Event;
import gov.mt.wris.repositories.CaseApplicationXrefRepository;
import gov.mt.wris.repositories.CaseTypeRepository;
import gov.mt.wris.repositories.CourtCaseRepository;
import gov.mt.wris.repositories.CourtCaseVersionXrefRepository;
import gov.mt.wris.repositories.DecreeRepository;
import gov.mt.wris.repositories.EventRepository;
import gov.mt.wris.repositories.EventTypeRepository;
import gov.mt.wris.repositories.MasterStaffIndexesRepository;
import gov.mt.wris.services.ApplicantService;
import gov.mt.wris.services.CaseService;
import gov.mt.wris.services.ObjectionsService;
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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CaseServiceImpl  implements CaseService {

    private static Logger LOGGER = LoggerFactory.getLogger(CaseServiceImpl.class);

    private static List<String> unsupportedCaseTypes = Arrays.asList("OBJL", "ISRO", "MOTA", "OACT", "SPLT", "DCC", "AWC", "EXPN");

    @Autowired
    private CourtCaseVersionXrefRepository courtCaseVersionXrefRepository;

    @Autowired
    private CourtCaseRepository courtCaseRepository;

    @Autowired
    private MasterStaffIndexesRepository masterStaffIndexesRepository;

    @Autowired
    private DecreeRepository decreeRepository;

    @Autowired
    private CaseTypeRepository caseTypeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private ObjectionsService objectionsService;

    @Autowired
    private CaseApplicationXrefRepository caseApplicationXrefRepository;

    @Override
    public WaterRightVersionCasesPageDto getWaterRightVersionCases(int pageNumber, int pageSize, WaterRightVersionCasesSortColumn sortColumn, SortDirection sortDirection, BigDecimal waterRightId, BigDecimal versionId) {

        LOGGER.info("Get Water Right Version Court Cases");

        Sort sortDtoColumn = getWaterRightVersionCasesSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<CourtCaseVersionXref> results = courtCaseVersionXrefRepository.getWaterRightVersionCourtCases(request, waterRightId, versionId);

        WaterRightVersionCasesPageDto page = new WaterRightVersionCasesPageDto();
        page.setResults(results.getContent().stream().map(c -> {
            return getWaterRightVersionCaseDto(c);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getWaterRightVersionCasesSortColumn(WaterRightVersionCasesSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();

        switch (sortColumn) {
            case CASENUMBER:
                orders.add(new Sort.Order(direction, "caseId"));
                break;
            case DESCRIPTION:
                orders.add(new Sort.Order(direction, "courtCaseHearing.caseType.description"));
                break;
            case HEARINGDATE:
                /* Still researching where to find this property! */
                orders.add(new Sort.Order(direction, "courtCaseHearing.hearingDate"));
                break;
            case VERSIONNUMBER:
                orders.add(new Sort.Order(direction, "versionId"));
                break;
            case WATERCOURTCASE:
                orders.add(new Sort.Order(direction, "courtCaseHearing.caseNumber"));
                break;
            case STATUSDESCRIPTION:
                orders.add(new Sort.Order(direction, "courtCaseHearing.caseStatus.description"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "caseId"));
        return Sort.by(orders);

    }

    private WaterRightVersionCaseDto getWaterRightVersionCaseDto(CourtCaseVersionXref model) {

        WaterRightVersionCaseDto dto = new WaterRightVersionCaseDto();
        dto.setCaseNumber(model.getCaseId().longValue());
        dto.setWaterCourtCase(model.getCourtCaseHearing().getCaseNumber());
        dto.setDescription(model.getCourtCaseHearing().getCaseType().getDescription());
        if (model.getCourtCaseHearing().getCaseStatus() != null)
            dto.setStatusDescription(model.getCourtCaseHearing().getCaseStatus().getDescription());
        dto.setVersionNumber(model.getVersionId().longValue());
        if (model.getCourtCaseHearing().getHearingDate() != null)
            dto.setHearingDate(model.getCourtCaseHearing().getHearingDate().toLocalDate());
        return dto;

    }

    public CaseSearchResultPageDto searchCases(int pageNumber, int pageSize, CaseSearchSortColumn sortColumn, SortDirection sortDirection, String applicationId, String caseNumber, String caseTypeCode, String caseStatusCode, String waterCourtCaseNumber) {

        LOGGER.info("Search Cases and Hearings");

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<CourtCase> results = courtCaseRepository.searchCases(pageable, sortColumn, sortDirection, applicationId, caseNumber, caseTypeCode, caseStatusCode, waterCourtCaseNumber);

        CaseSearchResultPageDto page = new CaseSearchResultPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return caseSearchResultDtoLoader(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if (applicationId != null) filters.put("applicationId", applicationId);
        if (caseNumber != null) filters.put("caseNumber", caseNumber);
        if (caseTypeCode != null) filters.put("caseTypeCode", caseTypeCode);
        if (caseStatusCode != null) filters.put("caseStatusCode", caseStatusCode);
        if (waterCourtCaseNumber != null) filters.put("waterCourtCaseNumber", waterCourtCaseNumber);
        page.setFilters(filters);
        return page;

    }

    private CaseSearchResultDto caseSearchResultDtoLoader(CourtCase model) {

        CaseSearchResultDto dto = new CaseSearchResultDto();
        dto.setCaseNumber(model.getId().longValue());
        if (model.getCaseStatus() != null) {
            dto.setCaseStatus(model.getCaseStatus().getCode());
            dto.setCaseStatusDescription(model.getCaseStatus().getDescription());
        }
        dto.setCaseType(model.getCaseType().getCode());
        dto.setCaseTypeDescription(model.getCaseType().getDescription());
        if (model.getCaseApplicationXrefs() != null) {
            dto.setApplicationId(model.getCaseApplicationXrefs().getApplicationId().longValue());
            dto.setBasin(model.getCaseApplicationXrefs().getApplication().getBasin());
            if (model.getCaseApplicationXrefs().getApplication().getType() != null)
                dto.setCompleteApplicationType(
                    String.format("%s - %s",
                        model.getCaseApplicationXrefs().getApplication().getType().getCode(),
                        model.getCaseApplicationXrefs().getApplication().getType().getDescription()
                    )
                );
        }
        return dto;
    }


        public CaseDto createCase(CaseCreationDto createDto) {
        try {
            return createCaseTransaction(createDto);
        } catch (DataIntegrityViolationException e){
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.CAXR_APPL_FK")) {
                    throw new DataIntegrityViolationException("The application id " + createDto.getApplicationId() + " does not exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

    @Transactional
    private CaseDto createCaseTransaction(CaseCreationDto createDto) {

        LOGGER.info("Create Court Case");

        if (unsupportedCaseTypes.contains(createDto.getCaseType()))
            throw new ValidationException(String.format("Unsupported case type %s", createDto.getCaseType()));

        CourtCase newCase = new CourtCase();
        newCase.setCaseTypeCode(createDto.getCaseType());
        newCase.setCaseStatusCode(createDto.getCaseStatus());
        if (createDto.getRegionalOfficeId() != null) newCase.setOfficeId(new BigDecimal(createDto.getRegionalOfficeId()));
        if (createDto.getDecreeId() != null) newCase.setDecreeId(new BigDecimal(createDto.getDecreeId()));
        if (createDto.getProgramType().equals(Constants.CASE_TYPE_PROGRAM_WC)) {
            if (createDto.getWaterCourtCaseNumber() == null || createDto.getWaterCourtCaseNumber().isEmpty())
                throw new ValidationException("Water Court Case Type requires a Water Court Case Number");
            List<BigDecimal> idCheck = courtCaseRepository.waterCourtCaseNumberInUse(createDto.getWaterCourtCaseNumber(), BigDecimal.ZERO);
            if (idCheck != null && idCheck.size() > 0)
                throw new ValidationException(
                        String.format("Water Court Case Number is already in use for case id(s) %s",
                                idCheck.stream().map(n->String.valueOf(n)).collect(Collectors.joining(", ")))
                );
            newCase.setCaseNumber(createDto.getWaterCourtCaseNumber());
        }

        CourtCase caseResult = courtCaseRepository.saveAndFlush(newCase);
        CaseDto dto = new CaseDto();
        dto.setCaseNumber(caseResult.getId().longValue());
        dto.setCaseType(caseResult.getCaseTypeCode());

        if (createDto.getProgramType().equals(Constants.CASE_TYPE_PROGRAM_NA) && (createDto.getApplicationId()!=null && createDto.getApplicationId() > 0)) {
            List<BigDecimal> idCheck = courtCaseRepository.applicationNumberInUse(new BigDecimal(createDto.getApplicationId()), BigDecimal.ZERO);
            if (idCheck != null && idCheck.size() > 0)
                throw new ValidationException(
                        String.format("Application Number is already in use for case id(s) %s",
                                idCheck.stream().map(n->String.valueOf(n)).collect(Collectors.joining(", ")))
                );
            CaseApplicationXref caseAppResult;
            CaseApplicationXref xref = new CaseApplicationXref();
            xref.setCaseId(caseResult.getId());
            xref.setApplicationId(new BigDecimal(createDto.getApplicationId()));
            caseAppResult =  caseApplicationXrefRepository.saveAndFlush(xref);
            /** put new application id into outbound dto **/
            dto.setApplicationId(caseAppResult.getApplicationId().longValue());
        }

        return dto;

    }


    public CaseDto getCourtCase(Long caseId) {

        LOGGER.info("Get Court Case or Hearing");

        Optional<CourtCase> courtCase = courtCaseRepository.getCourtCase(new BigDecimal(caseId));
        if (!courtCase.isPresent())
            throw new NotFoundException(String.format("Case or hearing %s not found",caseId));
        CaseDto dto = caseDtoLoader(courtCase.get());
        if (dto.getDecreeIssueDate()!=null)
            dto.setHasOldDecreeIssuedDate(dto.getDecreeIssueDate().isBefore(LocalDate.of(2003, 1, 22)));
        else
            dto.setHasOldDecreeIssuedDate(false);
        return dto;

    }

    private CaseDto caseDtoLoader(CourtCase model) {

        CaseDto dto = new CaseDto();
        if (model.getDecreeId() != null) dto.setDecreeId(model.getDecreeId().longValue());
        dto.setCaseNumber(model.getId().longValue());
        dto.setCaseType(model.getCaseTypeCode());
        dto.setCaseTypeDescription(model.getCaseType().getDescription());
        dto.setProgramType(model.getCaseType().getProgram());
        dto.setCanPrintDecreeReport(masterStaffIndexesRepository.hasRoles(Arrays.asList(Constants.PRINT_DECREE_REPORT)) > 0);
        dto.setCanPrintAllWcReports(masterStaffIndexesRepository.hasRoles(Arrays.asList(Constants.PRINT_ALL_WC_REPORTS)) > 0);
        /* Program will be either 'NA' or 'WC' */
        if (model.getCaseType().getProgram().equals(Constants.CASE_TYPE_PROGRAM_NA)) {
            if (model.getCaseApplicationXrefs() != null && model.getCaseApplicationXrefs().getApplication()!=null) {
                dto.setApplicationId(model.getCaseApplicationXrefs().getApplicationId().longValue());
                dto.setBasin(model.getCaseApplicationXrefs().getApplication().getBasin());
                if (model.getCaseApplicationXrefs().getApplication().getType()!=null)
                    dto.setCompleteApplicationType(
                            String.format("%s %s",
                                    model.getCaseApplicationXrefs().getApplication().getType().getCode(),
                                    model.getCaseApplicationXrefs().getApplication().getType().getDescription()
                            )
                    );
            }
            if (model.getCourtCaseAssignToNa().getAssignedTo()!=null)
                dto.setAssignedTo(
                        String.format(
                                "%s, %s",
                                model.getCourtCaseAssignToNa().getAssignedTo().getLastName(),
                                model.getCourtCaseAssignToNa().getAssignedTo().getFirstName()
                        )
                );
        } else {

            if (model.getDecree()!=null) {
                dto.setDecreeId(Long.valueOf(model.getDecree().getId()));
                dto.setDecreeBasin(model.getDecree().getBasin());
                dto.setDecreeType(model.getDecree().getDecreeType().getCode());
                dto.setDecreeTypeDescription(model.getDecree().getDecreeType().getDescription());
            }

            if (model.getCourtCaseAssignToWc().getAssignedTo()!=null)
                dto.setAssignedTo(
                        String.format(
                                "%s, %s",
                                model.getCourtCaseAssignToWc().getAssignedTo().getLastName(),
                                model.getCourtCaseAssignToWc().getAssignedTo().getFirstName()
                        )
                );

        }
        if (model.getDecreeIssuedDate().getDecreeIssuedDate()!=null)
            dto.setDecreeIssueDate(model.getDecreeIssuedDate().getDecreeIssuedDate().toLocalDate());
        dto.setHasCaseAdminRole(masterStaffIndexesRepository.hasRoles(Arrays.asList(Constants.CASES_AND_OBJECTIONS_ADMIN_ROLE)) > 0);
        dto.setWaterCourtCaseNumber(model.getCaseNumber()!=null?model.getCaseNumber():null);
        if (model.getOfficeId()!=null) dto.setOfficeId(model.getOfficeId().longValue());
        if (model.getOffice()!=null) {
            dto.setOfficeId(model.getOffice().getId().longValue());
            dto.setOfficeDescription(model.getOffice().getDescription());
        }
        dto.setCaseStatus(model.getCaseStatusCode()!=null?model.getCaseStatusCode():null);
        if (model.getCaseStatus()!=null) dto.setCaseStatusDescription(model.getCaseStatus().getDescription());
        return dto;

    }

    public SearchBasinsResultPageDto searchBasins(int pagenumber, int pagesize, SearchBasinsSortColumn sortColumn, SortDirection sortDirection, String basin) {

        LOGGER.info("Search for Basins");

        Sort sortDtoColumn = getSearchBasinsSortColumn(sortColumn, sortDirection);
        Pageable pageable = PageRequest.of(pagenumber -1, pagesize, sortDtoColumn);

        String user = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Boolean isAdministrator = decreeRepository.isWaterCourtAdministrator(user) > 0;
        /* if isAdministrator is TRUE then DON'T limit list to basins where LOV_ITEM = 'Y', send 'N'; otherwise 'Y' :-? */
        Page<Decree> resultsPage = decreeRepository.searchDecreeBasins(pageable, basin, (isAdministrator?"N":"Y"));
        SearchBasinsResultPageDto page = new SearchBasinsResultPageDto();
        page.setResults(resultsPage.getContent().stream().map(row -> {
            SearchBasinsResultDto dto = new SearchBasinsResultDto();
            dto.setBasin(row.getBasin());
            dto.setDecreeId(Long.valueOf(row.getId()));
            dto.setDctpCode(row.getDecreeTypeCode());
            dto.setDctpCodeDescription(row.getDecreeType().getDescription());
            if (row.getDecreeIssuedDate()!=null)
                dto.setIssueDate(row.getDecreeIssuedDate().toLocalDate());
            return dto;
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());
        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private Sort getSearchBasinsSortColumn(SearchBasinsSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        if (sortColumn==SearchBasinsSortColumn.BASIN) {
            orders.add(new Sort.Order(direction, "basin"));
        } else if (sortColumn==SearchBasinsSortColumn.DECREEID) {
            orders.add(new Sort.Order(direction, "id"));
        } else if (sortColumn==SearchBasinsSortColumn.DCTPCODEDESCRIPTION) {
            orders.add(new Sort.Order(direction, "dt.description"));
        } else { /* SearchBasinsSortColumn.ISSUEDATE */
            orders.add(new Sort.Order(direction, "decreeIssuedDate"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "basin"));
        Sort fullSort = Sort.by(orders);
        return fullSort;

    }

    @Transactional
    @Modifying
    public CaseDto updateCourtCase(Long caseId, CaseUpdateDto updateDto) {

        LOGGER.info("Update Court Case");

        Optional<CourtCase> fndCase = courtCaseRepository.getCourtCase(new BigDecimal(caseId));
        if (!fndCase.isPresent())
            throw new NotFoundException(String.format("Case or hearing %s not found",caseId));
        CourtCase courtCase = fndCase.get();
        String program;
        if (updateDto.getCaseType()!=null && !updateDto.getCaseType().equals(courtCase.getCaseTypeCode())) {
            /* Case type changed, set program for new type */
            Optional<CaseType> fndType = caseTypeRepository.getCaseTypeByCode(updateDto.getCaseType());
            if (!fndType.isPresent())
                throw new NotFoundException(String.format("Case type %s not found", updateDto.getCaseType()));
            courtCase.setCaseTypeCode(updateDto.getCaseType());
            program = fndType.get().getProgram();
        } else {
            program = courtCase.getCaseType().getProgram();
        }

        if (Constants.CASE_TYPE_PROGRAM_WC.equals(program)) {
            caseApplicationXrefRepository.deleteByCaseId(new BigDecimal(caseId));
            if (updateDto.getWaterCourtCaseNumber() !=null && !updateDto.getWaterCourtCaseNumber().equals(courtCase.getCaseNumber())) {
                List<BigDecimal> idCheck = courtCaseRepository.waterCourtCaseNumberInUse(updateDto.getWaterCourtCaseNumber(), courtCase.getId());
                if (idCheck != null && idCheck.size() > 0)
                    throw new ValidationException(
                            String.format("Water Court Case Number is already in use for case id(s) %s",
                                    idCheck.stream().map(n->String.valueOf(n)).collect(Collectors.joining(", ")))
                    );
                courtCase.setCaseNumber(updateDto.getWaterCourtCaseNumber());
            }

            if(updateDto.getDecreeId()!=null) courtCase.setDecreeId(new BigDecimal(updateDto.getDecreeId()));

            if (updateDto.getDecreeId() != null) {
                courtCase.setDecreeId(new BigDecimal(updateDto.getDecreeId()));
            } else {
                courtCase.setDecreeId(null);
            }

        } else if (Constants.CASE_TYPE_PROGRAM_NA.equals(program)) {
            if (updateDto.getApplicationId() != null) {
                List<BigDecimal> idCheck = courtCaseRepository.applicationNumberInUse(new BigDecimal(updateDto.getApplicationId()), new BigDecimal(caseId));
                if (idCheck != null && idCheck.size() > 0)
                    throw new ValidationException(
                            String.format("Application Number is already in use for case id(s) %s",
                                    idCheck.stream().map(n->String.valueOf(n)).collect(Collectors.joining(", ")))
                    );
            }
            courtCase.setDecreeId(null);
            courtCase.setCaseNumber(null);
            if (updateDto.getApplicationId() == null || (courtCase.getCaseApplicationXrefs() != null && updateDto.getApplicationId().longValue() != courtCase.getCaseApplicationXrefs().getApplicationId().longValue())) {
                caseApplicationXrefRepository.deleteByCaseId(new BigDecimal(caseId));
            }
            if (updateDto.getApplicationId() != null && (courtCase.getCaseApplicationXrefs() == null || updateDto.getApplicationId().longValue() != courtCase.getCaseApplicationXrefs().getApplicationId().longValue())) {
                CaseApplicationXref xref = new CaseApplicationXref();
                xref.setCaseId(new BigDecimal(caseId));
                xref.setApplicationId(new BigDecimal(updateDto.getApplicationId()));
                caseApplicationXrefRepository.save(xref);
            }

        }
        /* WC & NA shared properties */
        courtCase.setOfficeId(updateDto.getOfficeId()!=null?new BigDecimal(updateDto.getOfficeId()):null);
        courtCase.setCaseStatusCode(updateDto.getCaseStatus()!=null?updateDto.getCaseStatus():null);
        courtCaseRepository.saveAndFlush(courtCase);
        if (updateDto.getCaseStatus() != null && updateDto.getCaseStatus().equals(Constants.CASE_STATUS_CLOSED))
            objectionsService.closeCaseObjections(new BigDecimal(caseId));

        CaseDto dto = caseDtoLoader(courtCase); /* Decree issued date is set by caseDtoLoader() */
        if (dto.getDecreeIssueDate()!=null)
            dto.setHasOldDecreeIssuedDate(dto.getDecreeIssueDate().isBefore(LocalDate.of(2003, 1, 22)));
        else
            dto.setHasOldDecreeIssuedDate(false);
        return dto;

    }

    public void deleteCaseHearing(BigDecimal caseId) {

        LOGGER.info("Deleting Court Case");

        Optional<CourtCase> fndCase = courtCaseRepository.getCourtCase(caseId);
        CourtCase courtCase = fndCase.get();
        caseApplicationXrefRepository.deleteByCaseId(caseId);
        courtCaseRepository.delete(courtCase);

    }

    public CaseRegisterPageDto getCaseEvents(int pageNumber, int pageSize, CaseRegisterSortColumn sortColumn, SortDirection sortDirection, BigDecimal caseId) {

        LOGGER.info("Get Register of Events for Court Case or Hearing");

        Sort sortDtoColumn = getCaseRegisterSortColumn(sortColumn, sortDirection);
        Pageable request = PageRequest.of(pageNumber - 1, pageSize, sortDtoColumn);
        Page<Event> results = eventRepository.findAllByCaseId(request, caseId);

        CaseRegisterPageDto page = new CaseRegisterPageDto();
        page.setResults(results.getContent().stream().map(o -> {
            return caseRegisterDetailDtoLoader(o);
        }).collect(Collectors.toList()));

        page.setCurrentPage(results.getNumber() + 1);
        page.setPageSize(results.getSize());
        page.setTotalPages(results.getTotalPages());
        page.setTotalElements(results.getTotalElements());
        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);
        return page;

    }

    private Sort getCaseRegisterSortColumn(CaseRegisterSortColumn sortColumn, SortDirection sortDirection) {

        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();

        switch (sortColumn) {
            case EVENTTYPEDESCRIPTION:
                orders.add(new Sort.Order(direction, "et.description"));
                break;
            case FILEDDATE:
                orders.add(new Sort.Order(direction, "eventDate"));
                break;
            case DUEDATE:
                orders.add(new Sort.Order(direction, "responseDueDate"));
                break;
            case COMMENTS:
                orders.add(new Sort.Order(direction, "eventComment"));
                break;
            case ENTEREDBY:
                orders.add(new Sort.Order(direction, "createdByName.lastName"));
                orders.add(new Sort.Order(direction, "createdByName.firstName"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "et.description"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "eventDate"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "responseDueDate"));
        return Sort.by(orders);

    }

    private CaseRegisterDetailDto caseRegisterDetailDtoLoader(Event model) {

        CaseRegisterDetailDto dto = new CaseRegisterDetailDto();
        dto.setEventId(model.getEventId().longValue());
        dto.setEventType(model.getEventTypeCode());
        if (model.getEventType()!=null) dto.setEventTypeDescription(model.getEventType().getDescription());
        dto.setFiledDate(model.getEventDate().toLocalDate());
        if (model.getResponseDueDate()!=null) dto.setDueDate(model.getResponseDueDate().toLocalDate());
        if (model.getCreatedByName()!=null)
            dto.setEnteredBy(Helpers.buildName(model.getCreatedByName().getLastName(), model.getCreatedByName().getFirstName()));
        if (model.getEventComment()!=null) dto.setComments(model.getEventComment());
        return dto;

    }

    public CaseRegisterDetailDto updateCaseEvent(Long caseId, Long eventId, CaseRegisterCreateUpdateDto updateDto) {

        LOGGER.info("Update Court Case or Hearing Event");

        Optional<Event> fndEvent = eventRepository.getByEventIdAndCaseId(new BigDecimal(eventId), new BigDecimal(caseId));
        if(!fndEvent.isPresent())
            throw new NotFoundException(String.format("Case or Hearing %s Event %s not found", caseId, eventId));
        Event oldEvent = fndEvent.get();

        /* Only case administrators can change event type, filed date, due date & comments */
        if (masterStaffIndexesRepository.hasRoles(Arrays.asList(Constants.CASES_AND_OBJECTIONS_ADMIN_ROLE)) > 0) {
            oldEvent.setEventDate(updateDto.getFiledDate().atStartOfDay());
            if (updateDto.getEventType()!=null && !updateDto.getEventType().equals(oldEvent.getEventTypeCode())) {
                /* Event type is changing so we need to go see if due date calculation is warranted */
                oldEvent.setEventTypeCode(updateDto.getEventType());
                int days = eventTypeRepository.getResponseDueDays(updateDto.getEventType());
                LocalDateTime dueDate = updateDto.getDueDate()!=null?updateDto.getDueDate().atStartOfDay():null;
                oldEvent.setResponseDueDate(days>0?updateDto.getFiledDate().plusDays(days).atStartOfDay():dueDate);
            } else {
                oldEvent.setResponseDueDate(updateDto.getDueDate()!=null?updateDto.getDueDate().atStartOfDay():null);
            }
            oldEvent.setEventComment(updateDto.getComments());
        }

        return caseRegisterDetailDtoLoader(eventRepository.saveAndFlush(oldEvent));

    }

    public CaseRegisterDetailDto createCaseEvent(Long caseId, CaseRegisterCreateUpdateDto createDto) {

        try {
            return createCaseEventTransaction(caseId, createDto);
        } catch (DataIntegrityViolationException e){
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.EVDT_CASE_FK")) {
                    throw new DataIntegrityViolationException("The case id " + caseId + " does not exist");
                } else if (constraintMessage.contains("WRD.EVDT_EVTP_FK")) {
                    throw new DataIntegrityViolationException("The event type " + createDto.getEventType() + " does not exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }

    }

    @Transactional
    private CaseRegisterDetailDto createCaseEventTransaction(Long caseId, CaseRegisterCreateUpdateDto createDto) {

        LOGGER.info("Create Court Case or Hearing Event");

        Optional<CourtCase> courtCase = courtCaseRepository.getCourtCase(new BigDecimal(caseId));
        if (!courtCase.isPresent())
            throw new NotFoundException(String.format("Case or Hearing %s not found",caseId));

        Event event = new Event();
        event.setCaseId(new BigDecimal(caseId));
        event.setCourtCase(courtCase.get());
        event.setEventTypeCode(createDto.getEventType());
        event.setEventDate(createDto.getFiledDate().atStartOfDay());

        int days = eventTypeRepository.getResponseDueDays(createDto.getEventType());
        LocalDateTime dueDate = createDto.getDueDate()!=null?createDto.getDueDate().atStartOfDay():null;
        event.setResponseDueDate(days>0?createDto.getFiledDate().plusDays(days).atStartOfDay():dueDate);
        event.setEventComment(createDto.getComments());

        return caseRegisterDetailDtoLoader(eventRepository.saveAndFlush(event));

    }

    public ApplicantsPageDto getCaseApplicationApplicants(int pagenumber, int pagesize, ApplicantSortColumn sortColumn, SortDirection sortDirection, Long caseId) {

        LOGGER.info("Get associated Application Applicants for Court Case or Hearing");

        Optional<CourtCase> fndCase = courtCaseRepository.getCourtCase(new BigDecimal(caseId));
        if (!fndCase.isPresent())
            throw new NotFoundException(String.format("Case or Hearing %s not found", caseId));

        Long applicationId = (fndCase.get().getCaseApplicationXrefs() != null && fndCase.get().getCaseApplicationXrefs().getApplication() != null ? fndCase.get().getCaseApplicationXrefs().getApplication().getId().longValue():0);

        return applicantService.getApplicants(applicationId, pagenumber, pagesize, sortColumn, sortDirection);

    }

    public ObjectionsPageDto getCaseApplicationObjections(int pagenumber, int pagesize, ObjectionSortColumn sortColumn, SortDirection sortDirection, Long caseId) {

        LOGGER.info("Get associated Application Objections for Court Case or Hearing");

        Optional<CourtCase> fndCase = courtCaseRepository.getCourtCase(new BigDecimal(caseId));
        if (!fndCase.isPresent())
            throw new NotFoundException(String.format("Case or Hearing %s not found",caseId));

        BigDecimal applicationId = (fndCase.get().getCaseApplicationXrefs() != null && fndCase.get().getCaseApplicationXrefs().getApplication() != null ? fndCase.get().getCaseApplicationXrefs().getApplication().getId():BigDecimal.ZERO);

        return objectionsService.getObjections(pagenumber, pagesize, sortColumn, sortDirection, applicationId);

    }

    public CaseCommentsDto getCaseComments(Long caseId) {

        LOGGER.info("Get Comments for Case or Hearing");

        Optional<CourtCase> fndCase = courtCaseRepository.getCourtCase(new BigDecimal(caseId));
        if (!fndCase.isPresent())
            throw new NotFoundException(String.format("Case or Hearing %s not found",caseId));
        return new CaseCommentsDto().comments(fndCase.get().getCaseComments());

    }

    public CaseCommentsDto updateCaseComments(Long caseId, CaseCommentsDto updateDto) {

        LOGGER.info("Update Comments for Case or Hearing");

        Optional<CourtCase> fndCase = courtCaseRepository.getCourtCase(new BigDecimal(caseId));
        if (!fndCase.isPresent())
            throw new NotFoundException(String.format("Case or Hearing %s not found",caseId));
        CourtCase oldCase = fndCase.get();
        oldCase.setCaseComments(updateDto.getComments()!=null?updateDto.getComments():null);
        return new CaseCommentsDto().comments((courtCaseRepository.saveAndFlush(oldCase)).getCaseComments());

    }

}
