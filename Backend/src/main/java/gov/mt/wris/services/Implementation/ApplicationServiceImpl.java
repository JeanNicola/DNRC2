package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.ApplicationCreationDto;
import gov.mt.wris.dtos.ApplicationDto;
import gov.mt.wris.dtos.ApplicationOwnerSearchPageDto;
import gov.mt.wris.dtos.ApplicationOwnerSearchResultDto;
import gov.mt.wris.dtos.ApplicationOwnerSortColumn;
import gov.mt.wris.dtos.ApplicationRepSearchPageDto;
import gov.mt.wris.dtos.ApplicationRepSearchResultDto;
import gov.mt.wris.dtos.ApplicationRepSortColumn;
import gov.mt.wris.dtos.ApplicationSearchPageDto;
import gov.mt.wris.dtos.ApplicationSearchResultDto;
import gov.mt.wris.dtos.ApplicationSortColumn;
import gov.mt.wris.dtos.ApplicationUpdateDto;
import gov.mt.wris.dtos.ApplicationWaterRightsSummaryDto;
import gov.mt.wris.dtos.ChangeDto;
import gov.mt.wris.dtos.DescSortDirection;
import gov.mt.wris.dtos.OfficeCreationDto;
import gov.mt.wris.dtos.OfficeDto;
import gov.mt.wris.dtos.OfficePageDto;
import gov.mt.wris.dtos.OfficeSortColumn;
import gov.mt.wris.dtos.OwnerApplicationRepListDto;
import gov.mt.wris.dtos.OwnerApplicationRepPageDto;
import gov.mt.wris.dtos.OwnerApplicationSortColumn;
import gov.mt.wris.dtos.ProcessorDto;
import gov.mt.wris.dtos.RelatedApplicationDto;
import gov.mt.wris.dtos.RelatedApplicationPageDto;
import gov.mt.wris.dtos.RelatedApplicationSortColumn;
import gov.mt.wris.dtos.RepApplicationOwnerListDto;
import gov.mt.wris.dtos.RepApplicationOwnerPageDto;
import gov.mt.wris.dtos.RepApplicationSortColumn;
import gov.mt.wris.dtos.ResponsibleOfficeDto;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.StaffCreationDto;
import gov.mt.wris.dtos.StaffDto;
import gov.mt.wris.dtos.StaffPageDto;
import gov.mt.wris.dtos.StaffSortColumn;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.ApplicationType;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.Event;
import gov.mt.wris.models.EventType;
import gov.mt.wris.models.MasterStaffIndexes;
import gov.mt.wris.models.Office;
import gov.mt.wris.models.OfficeApplicationXref;
import gov.mt.wris.models.Owner;
import gov.mt.wris.models.Reference;
import gov.mt.wris.models.StaffApplicationXref;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.ApplicationTypeRepository;
import gov.mt.wris.repositories.CustomerRepository;
import gov.mt.wris.repositories.EventRepository;
import gov.mt.wris.repositories.EventTypeRepository;
import gov.mt.wris.repositories.OfficeApplicationXrefRepository;
import gov.mt.wris.repositories.OwnerRepository;
import gov.mt.wris.repositories.PaymentRepository;
import gov.mt.wris.repositories.StaffApplicationXrefRepository;
import gov.mt.wris.repositories.VersionApplicationXrefRepository;
import gov.mt.wris.repositories.WaterRightGeocodeRepository;
import gov.mt.wris.repositories.WaterRightVersionRepository;
import gov.mt.wris.services.ApplicationService;
import gov.mt.wris.services.MasterStaffIndexesService;
import gov.mt.wris.services.ReferenceService;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Autowired
    private ApplicationRepository appRepository;

    @Autowired
    private OwnerRepository ownRepository;

    @Autowired
    private CustomerRepository custRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;
    
    @Autowired
    private MasterStaffIndexesService masterStaffIndexesService;
    
    @Autowired
    private StaffApplicationXrefRepository staffRepo;
    
    @Autowired
    private OfficeApplicationXrefRepository officeRepo;

    @Autowired
    private ApplicationTypeRepository appTypeRepo;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WaterRightVersionRepository waterRepo;

    @Autowired
    private WaterRightGeocodeRepository waterRightGeocodeRepository;

    @Autowired
    private VersionApplicationXrefRepository versionXrefRepo;

    @Autowired
    private ReferenceService referenceService;

    @Override
    public ApplicationSearchPageDto getApplications(int pagenumber,
                                                int pagesize,
                                                ApplicationSortColumn sortDTOColumn,
                                                DescSortDirection sortDirection,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode) {
        LOGGER.info("Getting a Page of Applications");
        // pagination by default uses 0 to start, shift it so we can use the same number to display on the frontend
		Pageable request = PageRequest.of(pagenumber - 1, pagesize);

        Page<ApplicationSearchResultDto> resultsPage = appRepository.getApplications(request, sortDTOColumn, sortDirection,
                                                                        basin, applicationId, applicationTypeCode);

        ApplicationSearchPageDto appPage = new ApplicationSearchPageDto();

        appPage.setResults(resultsPage.getContent());

        appPage.setCurrentPage(resultsPage.getNumber() + 1);
        appPage.setPageSize(resultsPage.getSize());

        appPage.setTotalPages(resultsPage.getTotalPages());
        appPage.setTotalElements(resultsPage.getTotalElements());
        
        appPage.setSortColumn(sortDTOColumn);
        appPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(basin != null) {
            filters.put("basin", basin);
        }
        if(applicationId != null) {
            filters.put("applicationId", applicationId);
        }
        if(applicationTypeCode != null) {
            filters.put("applicationTypeCode", applicationTypeCode);
        }
        appPage.setFilters(filters);

        return appPage;
    }

    @Override
    public ApplicationOwnerSearchPageDto getApplicationsByOwners(int pagenumber,
                                                int pagesize,
                                                ApplicationOwnerSortColumn sortColumn,
                                                DescSortDirection sortDirection,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode,
                                                String ownerContactId,
                                                String ownerLastName,
                                                String ownerFirstName,
                                                String repContactId,
                                                String repLastName,
                                                String repFirstName) {
        LOGGER.info("Getting a Page of Application Owners");
        // pagination by default uses 0 to start, shift it so we can use the same number to display on the frontend
		Pageable request = PageRequest.of(pagenumber - 1, pagesize);
		
        Page<ApplicationOwnerSearchResultDto> resultsPage = appRepository.getApplicationsByOwners(request, sortColumn, sortDirection,
                                                                        basin, applicationId, applicationTypeCode,
                                                                        ownerContactId, ownerLastName, ownerFirstName, repContactId, repLastName, repFirstName);

        ApplicationOwnerSearchPageDto appPage = new ApplicationOwnerSearchPageDto();

        appPage.setResults(resultsPage.getContent());

        appPage.setCurrentPage(resultsPage.getNumber() + 1);
        appPage.setPageSize(resultsPage.getSize());

        appPage.setTotalPages(resultsPage.getTotalPages());
        appPage.setTotalElements(resultsPage.getTotalElements());
        
        appPage.setSortColumn(sortColumn);
        appPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(basin != null) {
            filters.put("basin", basin);
        }
        if(applicationId != null) {
            filters.put("applicationId", applicationId);
        }
        if(applicationTypeCode != null) {
            filters.put("applicationTypeCode", applicationTypeCode);
        }
        if(ownerContactId != null) {
            filters.put("ownerContactId", ownerContactId);
        }
        if(ownerLastName != null) {
            filters.put("ownerLastName", ownerLastName);
        }
        if(ownerFirstName != null) {
            filters.put("ownerFirstName", ownerFirstName);
        }
        if(repContactId != null) {
            filters.put("repContactId", repContactId);
        }
        if(repLastName != null) {
            filters.put("repLastName", repLastName);
        }
        if(repFirstName != null) {
            filters.put("repFirstName", repFirstName);
        }
        appPage.setFilters(filters);

        return appPage;
    }

    @Override
    public OwnerApplicationRepPageDto getOwnersApplications(int pagenumber,
                                                int pagesize,
                                                OwnerApplicationSortColumn sortDTOColumn,
                                                DescSortDirection sortDirection,
                                                long contactID,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode,
                                                String repContactId,
                                                String repLastName,
                                                String repFirstName) {
        LOGGER.info("Getting a Page of an Owners Applications");
        // pagination by default uses 0 to start, shift it so we can use the same number to display on the frontend
		Pageable request = PageRequest.of(pagenumber - 1, pagesize);
		
        Page<OwnerApplicationRepListDto> resultsPage = appRepository.getOwnersApplications(request, sortDTOColumn, sortDirection, contactID,
                                                                                            basin, applicationId, applicationTypeCode,
                                                                                            repContactId, repLastName, repFirstName);

        OwnerApplicationRepPageDto appPage = new OwnerApplicationRepPageDto();

        appPage.setResults(resultsPage.getContent());

        appPage.setCurrentPage(resultsPage.getNumber() + 1);
        appPage.setPageSize(resultsPage.getSize());

        appPage.setTotalPages(resultsPage.getTotalPages());
        appPage.setTotalElements(resultsPage.getTotalElements());
        
        appPage.setSortColumn(sortDTOColumn);
        appPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(basin != null) {
            filters.put("basin", basin);
        }
        if(applicationId != null) {
            filters.put("applicationId", applicationId);
        }
        if(applicationTypeCode != null) {
            filters.put("applicationTypeCode", applicationTypeCode);
        }
        if(repLastName != null) {
            filters.put("repLastName", repLastName);
        }
        if(repFirstName != null) {
            filters.put("repFirstName", repFirstName);
        }
        appPage.setFilters(filters);

        return appPage;
    }

    @Override
    public ApplicationRepSearchPageDto getApplicationsByReps(int pagenumber,
                                                int pagesize,
                                                ApplicationRepSortColumn sortColumn,
                                                DescSortDirection sortDirection,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode,
                                                String repContactId,
                                                String repLastName,
                                                String repFirstName) {
        LOGGER.info("Getting a Page of Application Representatives");
        // pagination by default uses 0 to start, shift it so we can use the same number to display on the frontend
		Pageable request = PageRequest.of(pagenumber - 1, pagesize);
		
        Page<ApplicationRepSearchResultDto> resultsPage = appRepository.getApplicationsByRepresentatives(request, sortColumn, sortDirection,
                                                                        basin, applicationId, applicationTypeCode,
                                                                        repContactId, repLastName, repFirstName);

        ApplicationRepSearchPageDto appPage = new ApplicationRepSearchPageDto();

        appPage.setResults(resultsPage.getContent());

        appPage.setCurrentPage(resultsPage.getNumber() + 1);
        appPage.setPageSize(resultsPage.getSize());

        appPage.setTotalPages(resultsPage.getTotalPages());
        appPage.setTotalElements(resultsPage.getTotalElements());
        
        appPage.setSortColumn(sortColumn);
        appPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(basin != null) {
            filters.put("basin", basin);
        }
        if(applicationId != null) {
            filters.put("applicationId", applicationId);
        }
        if(applicationTypeCode != null) {
            filters.put("applicationTypeCode", applicationTypeCode);
        }
        if(repContactId != null) {
            filters.put("repContactId", repContactId);
        }
        if(repLastName != null) {
            filters.put("repLastName", repLastName);
        }
        if(repFirstName != null) {
            filters.put("repFirstName", repFirstName);
        }
        appPage.setFilters(filters);

        return appPage;
    }

    @Override
    public RepApplicationOwnerPageDto getRepsApplications(int pagenumber,
                                                int pagesize,
                                                RepApplicationSortColumn sortDTOColumn,
                                                DescSortDirection sortDirection,
                                                long repContactID,
                                                String basin,
                                                String applicationId,
                                                String applicationTypeCode) {
        LOGGER.info("Getting a Page of a Representative's Applications");
        // pagination by default uses 0 to start, shift it so we can use the same number to display on the frontend
		Pageable request = PageRequest.of(pagenumber - 1, pagesize);
		
        Page<RepApplicationOwnerListDto> resultsPage = appRepository.getRepsApplications(request, sortDTOColumn, sortDirection, repContactID,
                                                                                            basin, applicationId, applicationTypeCode);

        RepApplicationOwnerPageDto appPage = new RepApplicationOwnerPageDto();

        appPage.setResults(resultsPage.getContent());

        appPage.setCurrentPage(resultsPage.getNumber() + 1);
        appPage.setPageSize(resultsPage.getSize());

        appPage.setTotalPages(resultsPage.getTotalPages());
        appPage.setTotalElements(resultsPage.getTotalElements());
        
        appPage.setSortColumn(sortDTOColumn);
        appPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(basin != null) {
            filters.put("basin", basin);
        }
        if(applicationId != null) {
            filters.put("applicationId", applicationId);
        }
        if(applicationTypeCode != null) {
            filters.put("applicationTypeCode", applicationTypeCode);
        }
        appPage.setFilters(filters);

        return appPage;
    }

    public ApplicationDto getApplication(Long id) {
        BigDecimal appId = BigDecimal.valueOf(id);
        Optional<Application> foundApp = appRepository.findApplicationsWithType(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("Application #" + id + " not found");
        }

        ApplicationDto app = getApplicationDto(foundApp.get());
        Optional<Event> issued = eventRepository.findByAppIdAndEventTypeCode(BigDecimal.valueOf(id), "ISSU");
        Optional<Event> reissued = eventRepository.findByAppIdAndEventTypeCode(BigDecimal.valueOf(id), "RISS");
        Boolean hasGeocode = waterRightGeocodeRepository.existsByApplicationId(BigDecimal.valueOf(id));
        app.setIssued(issued.map(event -> event.getEventDate()).orElse(null));
        app.setReissued(reissued.map(event -> event.getEventDate()).orElse(null));
        app.setHasGeocode(hasGeocode);
        app.setCaseReport("Y".equalsIgnoreCase(foundApp.get().getType().getCaseReport()));

        return app;
    }

    public ApplicationDto createApplication(ApplicationCreationDto newApplication) {
        try {
            return _createApplication(newApplication);
        } catch (DataIntegrityViolationException e){
            // check that basin code and application type code exist
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.APPL_BOCA_FK")) {
                    throw new DataIntegrityViolationException("The basin code " + newApplication.getBasin() + " does not exist");
                } else if (constraintMessage.contains("WRD.APPL_APTP_FK")) {
                    throw new DataIntegrityViolationException("The Application Type Code " + newApplication.getApplicationTypeCode() + " does not exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

    @Transactional
    private ApplicationDto _createApplication(ApplicationCreationDto newApplication) {
        LOGGER.info("Creating a new Application");
        // prevent 607, 608, 617, 618, 626, 627, 650 & 651 from being
        // used as Application Types
        if(Constants.DISALLOWED_APPLICATION_TYPES.contains(newApplication.getApplicationTypeCode())) {
            throw new DataIntegrityViolationException("Type "+newApplication.getApplicationTypeCode() + " not allowed for new Applications");
        }

        if (newApplication.getDateTimeReceived().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Date and Time received must be today or earlier");
        }

        // check for at least one applicant
        if(newApplication.getContactIds() == null ||
            newApplication.getContactIds().size() == 0) {
            throw new DataIntegrityViolationException("At least one Applicant is required to create a new Application");
        }

        // check that there aren't any applicant duplicates
        List<Long> applicantList = newApplication.getContactIds();
        Set<Long> applicantSet = new HashSet<Long>(applicantList);
        if(applicantList.size() > applicantSet.size()) {
            throw new DataConflictException("Cannot add an applicant more than once");
        }

        if (newApplication.getDateTimeReceived().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Date and Time received must be today or earlier");
        }

        // create application
        Application appModel = getApplicationModel(newApplication);
        String directoryUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		// set the office
		MasterStaffIndexes masterStaffIndex = masterStaffIndexesService.getLocationStaffInfo(directoryUserName);
		appModel.setOfficeId(masterStaffIndex.getOfficeId());

		// add Applicants
        List<Owner> newApplicants = newApplication.getContactIds().stream()
        .map(contactId -> 
            createApplicant(contactId, newApplication.getDateTimeReceived(), null)
        ).collect(Collectors.toList());
        appModel.setApplicants(newApplicants);

        // add event based on type
        // New code was added to hardcode the PHAM event to 180 days as that is what occurs in the original Oracle Forms code.
        // The new code to use the WRD_EVENT_TYPES table has been commented out in case the business later changes their requirements.
        
        // String eventTypeCode =
        //     newApplication.getApplicationTypeCode().equals("600P") ||
        //     newApplication.getApplicationTypeCode().equals("606P")
        //         ? "PAMH"
        //         : "FRMR";

        // EventType eventType = eventTypeRepository.findById(eventTypeCode).get();
        // Event event = createEvent(
        //     newApplication.getDateTimeReceived(),
        //     eventTypeCode,
        //     eventType.getDueDays() != null
        //         ? newApplication.getDateTimeReceived().plusDays(eventType.getDueDays())
        //         : null
        // );
        
        String eventTypeCode;
        LocalDateTime eventDueDate;
        if (newApplication.getApplicationTypeCode().equals("600P") ||
        newApplication.getApplicationTypeCode().equals("606P")) {
            eventTypeCode = "PAMH";
            eventDueDate = newApplication.getDateTimeReceived().plusDays(180);
        } else {
            eventTypeCode = "FRMR";
            eventDueDate = null;
        }

        Event event = createEvent(
            newApplication.getDateTimeReceived(),
            eventTypeCode,
            eventDueDate
        );


        appModel.setEvents(Arrays.asList(event));

        // payments tab setup
        Optional<ApplicationType> appType = appTypeRepo.findById(newApplication.getApplicationTypeCode());
        if(!appType.isPresent()) {
            throw new NotFoundException("Application Type " + newApplication.getApplicationTypeCode() + " doesn't exist");
        }
        if(appType.get().getFilingFee() != null && appType.get().getFilingFee().compareTo(BigDecimal.valueOf(0)) > 0) {
            appModel.setFilingFee(appType.get().getFilingFee());
            appModel.setFeeStatus("NONE");
        } else {
            appModel.setFilingFee(BigDecimal.valueOf(0));
            appModel.setFeeStatus("FULL");
        }
        appModel.setFeeDiscount("N");
        appModel.setFeeWaived("N");
        appModel.setFeeOther("N");
        appModel.setFeeCGWA("N");

        // Set default for Non Filed Water Project
        appModel.setNonFiledWaterProject("N");

        // set the initial processor office and staff
        appModel.setProcessorStaffId(masterStaffIndex.getId());
        appModel.setProcessorOfficeId(masterStaffIndex.getOfficeId());

        Application createdApp = appRepository.save(appModel);

        ApplicationDto createdDto = getApplicationDto(createdApp);

        // transactional prevents the get request for behaving appropriately
        // need to attach the date time time to the dto directly
        if(!newApplication.getApplicationTypeCode().equals("600P") &&
            !newApplication.getApplicationTypeCode().equals("606P")) {
                createdDto.setDateTimeReceived(newApplication.getDateTimeReceived());
            }
        
        StaffApplicationXref staffRef = new StaffApplicationXref();
        staffRef.setApplicationId(appModel.getId());
        staffRef.setMasterStaffIndexId(masterStaffIndex.getId());
        staffRef.setBeginDate(newApplication.getDateTimeReceived().toLocalDate());
        staffRepo.save(staffRef);
        OfficeApplicationXref officeRef = new OfficeApplicationXref();
        officeRef.setApplicationId(appModel.getId());
        officeRef.setOfficeId(masterStaffIndex.getOfficeId());
        officeRef.setReceivedDate(newApplication.getDateTimeReceived().toLocalDate());
        officeRepo.save(officeRef);
        
        return createdDto;
    }

    public ApplicationDto updateApplication(Long id, ApplicationUpdateDto newApplication) {
        try {
            return _updateApplication(id, newApplication);
        } catch (DataIntegrityViolationException e){
            // check that basin code and application type code exist
            if(e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.APPL_BOCA_FK")) {
                    throw new DataIntegrityViolationException("The basin code " + newApplication.getBasin() + " does not exist");
                } else if (constraintMessage.contains("WRD.APPL_APTP_FK")) {
                    throw new DataIntegrityViolationException("The Application Type Code " + newApplication.getApplicationTypeCode() + " does not exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

    @Transactional
    private ApplicationDto _updateApplication(Long id, ApplicationUpdateDto newApp) {
        LOGGER.info("Updating an Application: " + id);
        // prevent 607, 608, 617, 618, 626, 627, 650 & 651 from being
        // used as Application Types
        if(Constants.DISALLOWED_APPLICATION_TYPES.contains(newApp.getApplicationTypeCode())) {
            throw new DataIntegrityViolationException("Type "+newApp.getApplicationTypeCode() + " not allowed for new Applications");
        }

        if (newApp.getDateTimeReceived().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Date and Time Received must be today or earlier");
        }

        Optional<Application> foundApplication = appRepository.findById(BigDecimal.valueOf(id));
        Application oldApplication = null;

        if(!foundApplication.isPresent()) {
            throw new NotFoundException("The Application " + id + " doesn't exist");
        }

        // Update
        oldApplication = foundApplication.get();
        oldApplication.setBasin(newApp.getBasin());

        String oldTypeCode = oldApplication.getTypeCode();
        String newTypeCode = newApp.getApplicationTypeCode();

        // can only turn a P type into its corresponding non-P type
        if((oldTypeCode.equals("600P") && !(newTypeCode.equals("600") || newTypeCode.equals("600P")) ) || 
            (oldTypeCode.equals("606P") && !(newTypeCode.equals("606") || newTypeCode.equals("606P")) ) ) {
            throw new DataConflictException("Cannot turn a " + oldTypeCode
                                    + " application into a " + newTypeCode);
        }

        // can't turn a non-P type into a P-type
        List<String> pList = new ArrayList<String>();
        pList.add("606P");
        pList.add("600P");
        if(!pList.contains(oldTypeCode) &&
            pList.contains(newTypeCode)) {
            throw new DataConflictException("Cannot turn a " + oldTypeCode
                                    + " application into a " + newTypeCode);
        }

        LocalDateTime oldDate = oldApplication.getEvents().stream()
            .filter(event -> event.getEventTypeCode().equals(pList.contains(oldTypeCode) ? "PAMH" : "FRMR"))
            .collect(Collectors.toList()).get(0).getEventDate();
        // add the form received event if changing from P to a non-P type
        if (!newTypeCode.equals(oldTypeCode) &&
            pList.contains(oldTypeCode)) {
            if (newApp.getDateTimeReceived().isAfter(LocalDateTime.now())) {
                throw new ValidationException("Date and Time Received must be today or earlier");
            }

            Event newEvent = createEvent(newApp.getDateTimeReceived(), "FRMR", null);
            oldApplication.addEvent(newEvent);
        } else if(!pList.contains(oldTypeCode) && !newApp.getDateTimeReceived().equals(oldDate)) {
            throw new DataConflictException("Can only change the Date Time Received when changing from a P application type to a non-P application type.");
        }
        oldApplication.setTypeCode(newApp.getApplicationTypeCode());

        // payments tab setup
        if(!newTypeCode.equals(oldTypeCode)) {
            Optional<ApplicationType> appType = appTypeRepo.findById(newTypeCode);
            if(!appType.isPresent()) {
                throw new NotFoundException("Application Type " + newTypeCode + " doesn't exist");
            }
            if(appType.get().getFilingFee() != null && appType.get().getFilingFee().compareTo(BigDecimal.valueOf(0)) > 0) {
                oldApplication.setFilingFee(appType.get().getFilingFee());
                oldApplication.setFeeStatus("NONE");
            } else {
                oldApplication.setFilingFee(BigDecimal.valueOf(0));
                oldApplication.setFeeStatus("FULL");
            }
            // check that all the properties are correct
            if("Y".equals(oldApplication.getFeeWaived())) {
                oldApplication.setFilingFee(BigDecimal.valueOf(0));
                oldApplication.setFeeOther("N");
                oldApplication.setFeeDiscount("N");
                oldApplication.setFeeCGWA("N");
            } else if("Y".equals(oldApplication.getFeeOther())) {
                oldApplication.setFilingFee(appType.get().getFeeOther());
                oldApplication.setFeeWaived("N");
                oldApplication.setFeeDiscount("N");
                oldApplication.setFeeCGWA("N");
            } else if("Y".equals(oldApplication.getFeeCGWA())) {
                oldApplication.setFilingFee(appType.get().getFeeCGWA());
                oldApplication.setFeeWaived("N");
                oldApplication.setFeeOther("N");
                oldApplication.setFeeDiscount("N");
            } else if("Y".equals(oldApplication.getFeeDiscount())) {
                // Need to check for null values. If any nulls, use BigDecimal 0
                BigDecimal getFilingFee = appType.get().getFilingFee();
                BigDecimal getFeeDiscount = appType.get().getFeeDiscount();

                getFilingFee = getFilingFee == null ? BigDecimal.ZERO : getFilingFee;
                getFilingFee = getFeeDiscount == null ? BigDecimal.ZERO : getFeeDiscount;
                
                oldApplication.setFilingFee(getFilingFee.subtract(getFilingFee));
                oldApplication.setFeeWaived("N");
                oldApplication.setFeeOther("N");
                oldApplication.setFeeCGWA("N");
            }
            //update form status
            double feesPaid = paymentRepository.getFeesPaid(BigDecimal.valueOf(id));
            double totalDue = oldApplication.getFilingFee().doubleValue() - feesPaid;
            if(totalDue > 0 && feesPaid <= 0) {
                oldApplication.setFeeStatus("NONE");
            }
            if(totalDue > 0 && feesPaid > 0) {
                oldApplication.setFeeStatus("PARTIAL");
            }
            if(totalDue <= 0) {
                oldApplication.setFeeStatus("FULL");
            }
        }

        // Non Filed Water Project
        oldApplication.setNonFiledWaterProject("N");
        
		String directoryUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		MasterStaffIndexes masterStaffIndex = masterStaffIndexesService.getLocationStaffInfo(directoryUserName);
		oldApplication.setOfficeId(masterStaffIndex.getOfficeId());

        oldApplication = appRepository.save(oldApplication);

        ApplicationDto updatedApp = getApplicationDto(oldApplication);

        // transactional prevent the get request for behaving appropriately
        // so need to set the datetime received for the dto
        if(!newApp.getApplicationTypeCode().equals("600P") &&
            !newApp.getApplicationTypeCode().equals("606P")) {
                updatedApp.setDateTimeReceived(newApp.getDateTimeReceived());
            }

        return updatedApp;
    }    

    private ApplicationDto getApplicationDto(Application model) {
        ApplicationDto dto = new ApplicationDto();
        dto.setApplicationId(model.getId().longValue());
        dto.setApplicationTypeCode(model.getTypeCode());
        dto.setDateTimeReceived(model.getDateTimeReceivedEvent() != null ? model.getDateTimeReceivedEvent().getEventDate() : null);
        dto.setBasin(model.getBasin());
        dto.setFeeStatus(model.getFeeStatus());
        if(model.getType() != null) {
            dto.setApplicationTypeDescription(model.getType().getDescription());
            dto.setFilingFee(( model.getType().getFilingFee() != null) ?  model.getType().getFilingFee().floatValue() : 0);
            dto.setHasAutoCompleteCode(model.getType().getAutoCompleteType() != null ? true : false);
        }
        return dto;
    }

    // purely for testing
    @Transactional
    public void deleteApplication(Long applicationId) {
        LOGGER.info("Deleting an Application");

        BigDecimal appId = BigDecimal.valueOf(applicationId);

        staffRepo.deleteByApplicationId(appId);

        officeRepo.deleteByApplicationId(appId);

        eventRepository.deleteByApplicationId(appId);

        ownRepository.deleteByApplicationId(appId);

        paymentRepository.deleteByApplicationId(appId);

        appRepository.deleteById(appId);

        versionXrefRepo.deleteByApplicationId(appId);
    }
    
    private Owner createApplicant(Long contactId, LocalDateTime beginDate, LocalDateTime endDate) {
        Optional<Customer> foundCustomer = custRepository.findById(BigDecimal.valueOf(contactId));
        if(!foundCustomer.isPresent()) {
            throw new DataIntegrityViolationException("No one exists with the Contact Id " + contactId);
        }
        Owner newApplicant = new Owner();
        // newApplicant.setApplication(newApp);
        newApplicant.setCustomerId(BigDecimal.valueOf(contactId));
        newApplicant.setCustomer(foundCustomer.get());
        newApplicant.setBeginDate(beginDate.toLocalDate());
        if(endDate != null) newApplicant.setEndDate(endDate.toLocalDate());
        return newApplicant;
    }

    private Event createEvent(LocalDateTime startingDate, String eventTypeCode, LocalDateTime responseDueDate) {
        Event newEvent = new Event();
        // newEvent.setApplication(newApp);
        newEvent.setEventDate(startingDate);
        newEvent.setEventTypeCode(eventTypeCode);
        newEvent.setResponseDueDate((responseDueDate!=null)? responseDueDate : null);
        return newEvent;
    }

    private Application getApplicationModel(ApplicationCreationDto newApp) {
        Application app = new Application();
        app.setBasin(newApp.getBasin());
        app.setTypeCode(newApp.getApplicationTypeCode());
        return app;
    }

    @Override
    public RelatedApplicationPageDto findRelatedApplications(String applicationId, Integer pageNumber, Integer pageSize, RelatedApplicationSortColumn sortColumn, SortDirection sortDirection) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(
                sortDirection.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, getEntitySortColumn(sortColumn))
        );

        Page<Application> resultPage = appRepository.findApplicationsByRegardingId(pageable, new BigDecimal(applicationId));
        RelatedApplicationPageDto page = new RelatedApplicationPageDto();

        page.setResults(resultPage.getContent().stream().map(record -> {
            return getRelatedApplicationDto(record);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());
        return page;
    }

    /**
     * @param dtoColumn
     * @return
     */
    private String getEntitySortColumn(RelatedApplicationSortColumn dtoColumn) {
        if (dtoColumn == RelatedApplicationSortColumn.APPLICATIONID)
            return "id";
        if (dtoColumn == RelatedApplicationSortColumn.TYPEDESCRIPTION)
            return "b.description";
        if (dtoColumn == RelatedApplicationSortColumn.DATERECEIVED)
            return "e.eventDate";
        return "id";
    }

    private RelatedApplicationDto getRelatedApplicationDto(Application model) {
        RelatedApplicationDto newDto = new RelatedApplicationDto();
        newDto.setApplicationId(model.getId().longValue());
        newDto.setTypeDescription(model.getType().getDescription());
        newDto.setTypeCode(model.getTypeCode());
        newDto.setDateReceived(model.getDateTimeReceivedEvent().getEventDate().toLocalDate());
        return newDto;
    }

    /*
        Application Water Right Summary
    */
    public ApplicationWaterRightsSummaryDto getWaterRightSummary(Long applicationId) {
        LOGGER.info("Getting Application Water Right Summary");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.getApplicationById(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("Application with id " + applicationId + "does not exist");
        }

        ApplicationWaterRightsSummaryDto dto = getWaterRightSummaryDto(foundApp.get());

        return dto;
    }

    private ApplicationWaterRightsSummaryDto getWaterRightSummaryDto(Application model) {
        ApplicationWaterRightsSummaryDto result = new ApplicationWaterRightsSummaryDto();
        BigDecimal acres = model.getMaxAcres();
        if(acres != null) result.setAcres(acres.doubleValue());
        BigDecimal volume = model.getMaxVolume();
        if(volume != null) result.setVolume(volume.doubleValue());
        BigDecimal flowRate = model.getMaxFlowRate();
        if(flowRate != null) result.setMaxFlowRate(flowRate.doubleValue());
        Reference flowRateUnit = model.getFlowRateReference();
        if(flowRateUnit != null) result.setFlowRateUnit(flowRateUnit.getMeaning());
        result.setNonFiledWaterProject("Y".equals(model.getNonFiledWaterProject()));
        result.setCanPressNonFiledWaterProject(canPressNonFiledWaterProject(model));
        return result;
    }

    public ApplicationWaterRightsSummaryDto editWaterRightSummaryDto(Long applicationId, ApplicationWaterRightsSummaryDto summaryDto) {
        try {
            return _editWaterRightSummaryDto(applicationId, summaryDto);
        } catch (DataIntegrityViolationException e) {
            // check that the statuses are correct
            if(
                e.getCause() instanceof ConstraintViolationException &&
                e.getCause().getCause() instanceof BatchUpdateException
            ) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if(constraintMessage.contains("AVCON_981414954_FLW_R_002")) {
                    throw new DataIntegrityViolationException("Enter a valid flow rate unit");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

    @Transactional
    private ApplicationWaterRightsSummaryDto _editWaterRightSummaryDto(Long applicationId, ApplicationWaterRightsSummaryDto summaryDto) {
        LOGGER.info("Editing the Water Right summary");

        if(summaryDto.getMaxFlowRate() != null &&
            summaryDto.getFlowRateUnit() == null
        ) {
            throw new ValidationException("A Flow Rate Unit is required with a Maximum Flow Rate");
        }
        if(summaryDto.getMaxFlowRate() == null &&
            summaryDto.getFlowRateUnit() != null
        ) {
            throw new ValidationException("A Maximum Flow Rate is required with a Flow Rate Unit");
        }

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.getApplicationById(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("Application with id " + applicationId + "does not exist");
        }
        Application app = foundApp.get();

        String nonFiledYesNo = (summaryDto.getNonFiledWaterProject() != null && summaryDto.getNonFiledWaterProject()) ? "Y" : "N";
        boolean changedNonFiled = !nonFiledYesNo.equals(app.getNonFiledWaterProject());
        // throw an error if we're trying to check the box when we're not allowed to
        if( summaryDto.getNonFiledWaterProject() != null &&
            changedNonFiled &&
            !canPressNonFiledWaterProject(app)
        ) {
            throw new ValidationException("Not allowed to change Non-Filed Water Project");
        }
        if(summaryDto.getNonFiledWaterProject() != null) {
            app.setNonFiledWaterProject(nonFiledYesNo);
        }

        List<String> allowed = Arrays.asList("105", "606", "604", "626", "627", "634", "644", "650");
        if(allowed.contains(app.getTypeCode())) {
            if(summaryDto.getVolume() == null) {
                app.setMaxVolume(null);
            } else {
                app.setMaxVolume(BigDecimal.valueOf(summaryDto.getVolume()));
            }
            if(summaryDto.getAcres() == null) {
                app.setMaxAcres(null);
            } else {
                app.setMaxAcres(BigDecimal.valueOf(summaryDto.getAcres()));

            }
            if(summaryDto.getMaxFlowRate() != null &&
                summaryDto.getFlowRateUnit() != null
            ) {
                Double flowRate = summaryDto.getMaxFlowRate();
                String flowUnit = summaryDto.getFlowRateUnit();
                if(flowRate < 1 && "CFS".equals(flowUnit)) {
                    flowRate = flowRate * 448.8f;
                    flowUnit = "GPM";
                } else if(flowRate > 448.8 && "GPM".equals(flowUnit)) {
                    flowRate = flowRate / 448.8f;
                    flowUnit = "CFS";
                }
                app.setMaxFlowRate(BigDecimal.valueOf(flowRate));
                app.setFlowRateUnit(flowUnit);
            } else {
                // using null since BigDecimal is required
                // and the validation up top catches the other cases
                app.setMaxFlowRate(null);
                app.setFlowRateUnit(null);
            }
        } else if (
            (app.getMaxVolume().doubleValue() != summaryDto.getVolume())||
            (app.getMaxAcres().doubleValue()  != summaryDto.getAcres()) ||
            (app.getMaxFlowRate().doubleValue() != summaryDto.getMaxFlowRate()) ||
            (!app.getFlowRateUnit().equals(summaryDto.getFlowRateUnit()))
        ) {
            throw new ValidationException("Not allowed to change any field for this application type");
        }
        appRepository.save(app);

        ApplicationWaterRightsSummaryDto dto = getWaterRightSummaryDto(app);

        return dto;
    }

    private boolean canPressNonFiledWaterProject(Application app) {
        if(!app.getTypeCode().equals("606") && !app.getTypeCode().equals("634")) {
            return false;
        }

        if(app.getBasin() != null &&
            (app.getNonFiledWaterProject() == null || app.getNonFiledWaterProject().equals("Y")) &&
            waterRepo.countByApplicationId(app.getId()) > 0
        ) {
            return false;
        }

        return true;
    }

    @Transactional
    public ChangeDto updateChange(String applicationId, ChangeDto changeDto) {
        LOGGER.info("Updating Change Description: " + applicationId);

        Optional<Application> foundChange = appRepository.findById(new BigDecimal(applicationId));

        if (!foundChange.isPresent())
            throw new NotFoundException("The Application with Id " + applicationId + " was not found");

        Application foundChangeEntity = foundChange.get();
        List<String> applicationTypes = new ArrayList<String>(Arrays.asList("606", "604", "626", "650", "105"));

        if (foundChangeEntity.getTypeCode().equals("634") || foundChangeEntity.getTypeCode().equals("644")) {
            foundChangeEntity.setDistance(changeDto.getDistance());
            foundChangeEntity.setDirection(changeDto.getDirection());
            foundChangeEntity.setAdditionalInfo(changeDto.getAdditionalInformation() != null ? changeDto.getAdditionalInformation().toUpperCase() : null);
        } else if (foundChangeEntity.getTypeCode().equals("635")) {
            foundChangeEntity.setChangeDesc(changeDto.getChangeDescription() != null ? changeDto.getChangeDescription().toUpperCase() : null);
            foundChangeEntity.setAdditionalInfo(changeDto.getAdditionalInformation() != null ? changeDto.getAdditionalInformation().toUpperCase() : null);
        } else if (applicationTypes.contains(foundChangeEntity.getTypeCode())) {
            foundChangeEntity.setChangeDesc(changeDto.getChangeDescription() != null ? changeDto.getChangeDescription().toUpperCase() : null);
            foundChangeEntity.setPastUse(changeDto.getPastUse() != null ? changeDto.getPastUse().toUpperCase() : null);
            foundChangeEntity.setAdditionalInfo(changeDto.getAdditionalInformation() != null ? changeDto.getAdditionalInformation().toUpperCase() : null);
        } else {
            throw new ValidationException("Unable to update. The application type must be '606', '604', '626', '650', '105', '635', '634' or '644'");
        }

        Application newChange = appRepository.save(foundChangeEntity);

        ChangeDto newChangeDto = _getChangeDto(newChange);
        return newChangeDto;
    }

    public ChangeDto getChange(String applicationId) {
        ChangeDto changeDto = null;
        Optional<Application> change = appRepository.findById(new BigDecimal(applicationId));
        if (change.isPresent()) {
            changeDto = _getChangeDto(change.get());
        }

        return changeDto;
    }

    private ChangeDto _getChangeDto(Application change) {
        List<String> applicationTypes = new ArrayList<String>(Arrays.asList("606", "604", "626", "650", "105", "635", "634", "644"));

        ChangeDto changeDto = new ChangeDto();
        if (applicationTypes.contains(change.getTypeCode())) {
            if (change.getTypeCode().equals("644") || change.getTypeCode().equals("634")) {
                changeDto.setDistance(change.getDistance());
                changeDto.setDirection(change.getDirection());
                changeDto.setAdditionalInformation(change.getAdditionalInfo());
                changeDto.setDirectionName(getDirectionLookupByTypeCode(change.getTypeCode()).get(change.getDirection()));
            } else if (change.getTypeCode().equals("635")) {
                changeDto.setChangeDescription(change.getChangeDesc());
                changeDto.setAdditionalInformation(change.getAdditionalInfo());
            } else {
                changeDto.setChangeDescription(change.getChangeDesc());
                changeDto.setAdditionalInformation(change.getAdditionalInfo());
                changeDto.setPastUse(change.getPastUse());
            }
        }
        return changeDto;
    }

    private Map<String, String> getDirectionLookupByTypeCode(String typeCode) {
        Map<String, String> lookups = new HashMap<>();
        AllReferencesDto allReferencesDto = null;
        if (typeCode.equals("644")) {
            allReferencesDto = referenceService.findAllProgramsByTable("DIRECTION");
        } else if (typeCode.equals("634")) {
            allReferencesDto = referenceService.findAllProgramsByTable("C_DIRECTION");
        }
        allReferencesDto.getResults().forEach((k) -> lookups.put(k.getValue(), k.getDescription()));
        return lookups;
    }

    // File Location and Processor
    public ResponsibleOfficeDto getResponsibleOffice(Long applicationId) {
        LOGGER.info("Getting the Responsible Office for the application");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findApplicationsWithOffice(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("This Application does not exist");
        }
        Application app = foundApp.get();

        ResponsibleOfficeDto dto = getResponsibleOfficeDto(app.getOffice());

        return dto;
    }

    private ResponsibleOfficeDto getResponsibleOfficeDto(Office office) {
        ResponsibleOfficeDto dto = new ResponsibleOfficeDto();
        dto.setOffice(office.getDescription());
        dto.setOfficeId(office.getId().longValue());
        return dto;
    }

    public ResponsibleOfficeDto editResponsibleOffice(Long applicationId, ResponsibleOfficeDto dto) {
        LOGGER.info("Changing the responsible Office");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findApplicationsWithOffice(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("This Application does not exist");
        }
        Application app = foundApp.get();

        app.setOfficeId(BigDecimal.valueOf(dto.getOfficeId()));

        app = appRepository.save(app);

        ResponsibleOfficeDto returnDto = getResponsibleOfficeDto(app.getOffice());

        return returnDto;
    }

    public ProcessorDto getProcessor(Long applicationId) {
        LOGGER.info("Getting the Processor for the application");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findApplicationsWithProcessor(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("This Application does not exist");
        }
        Application app = foundApp.get();

        ProcessorDto dto = getProcessorDto(app);

        return dto;
    }

    private ProcessorDto getProcessorDto(Application app) {
        ProcessorDto dto = new ProcessorDto();
        Office office = app.getProcessorOffice();
        if(office != null) dto.setOffice(office.getDescription());
        if(app.getProcessorOfficeId() != null) dto.setOfficeId(app.getProcessorOfficeId().longValue());
        MasterStaffIndexes staff = app.getProcessorStaff();
        if(staff != null) {
            String name = staff.getFirstName() + " " + staff.getLastName();
            dto.setStaff(name);
        }
        if(app.getProcessorStaffId() != null) dto.setStaffId(app.getProcessorStaffId().longValue());
        return dto;
    }

    public ProcessorDto editProcessor(Long applicationId, ProcessorDto dto) {
        LOGGER.info("Changing the Processor");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findApplicationsWithOffice(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("This Application does not exist");
        }
        Application app = foundApp.get();

        app.setProcessorOfficeId(BigDecimal.valueOf(dto.getOfficeId()));
        app.setProcessorStaffId(BigDecimal.valueOf(dto.getStaffId()));

        app = appRepository.save(app);

        ProcessorDto returnDto = getProcessorDto(app);

        return returnDto;
    }

    public OfficePageDto getApplicationsOffices(Long applicationId,
        int pageNumber,
        int pageSize,
        OfficeSortColumn sortDTOColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting the offices for an application");

        String sortColumn = getOfficeSortColumn(sortDTOColumn);
        Sort.Direction direction = (sortDirection == DescSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortColumn).and(Sort.by(Sort.Direction.DESC, "id")));

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Page<OfficeApplicationXref> resultsPage = officeRepo.findApplicationsOffices(pageable, appId);

        LocalDateTime earliestCreatedByDate = officeRepo.minCreatedDate(appId);

        OfficePageDto page = new OfficePageDto();

        page.setResults(resultsPage.getContent().stream().map(xref -> {
            return getOfficeDto(xref, earliestCreatedByDate);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortDTOColumn);
        page.setSortDirection(sortDirection);

        LocalDate latestSentDate = officeRepo.getLatestSentDate(appId);
        page.setLatestSentDate(latestSentDate);

        int activeOffices = officeRepo.countActiveOffices(appId);
        page.setCanInsert(activeOffices == 0);

        return page;
    }

    private String getOfficeSortColumn(OfficeSortColumn sortColumn) {
        if (OfficeSortColumn.OFFICEDESCRIPTION == sortColumn)
            return "o.description";
        if (OfficeSortColumn.RECEIVEDDATE == sortColumn)
            return "receivedDate";
        if (OfficeSortColumn.SENTDATE == sortColumn)
            return "sentDate";
        return "sentDate";
    }

    private OfficeDto getOfficeDto(OfficeApplicationXref xref, LocalDateTime earliestCreatedByDate) {
        OfficeDto dto = new OfficeDto();
        if(xref.getOffice() != null) dto.setOfficeDescription(xref.getOffice().getDescription());
        dto.setId(xref.getId().longValue());
        dto.setOfficeId(xref.getOfficeId().longValue());
        dto.setReceivedDate(xref.getReceivedDate());
        dto.setSentDate(xref.getSentDate());
        if(earliestCreatedByDate != null) {
            dto.setIsSystemGenerated(!earliestCreatedByDate.isBefore(xref.getCreatedDate()));
        }
        return dto;
    }

    public StaffPageDto getApplicationsStaff(Long applicationId,
        int pageNumber,
        int pageSize,
        StaffSortColumn sortDTOColumn,
        DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting the staff for an application");

        String sortColumn = getStaffSortColumn(sortDTOColumn);
        Sort.Direction direction = (sortDirection == DescSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortColumn).and(Sort.by(Sort.Direction.DESC, "id")));

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Page<StaffApplicationXref> resultsPage = staffRepo.findApplicationsStaff(pageable, appId);

        LocalDateTime earliestCreatedByDate = staffRepo.minCreatedDate(appId);

        StaffPageDto page = new StaffPageDto();

        page.setResults(resultsPage.getContent().stream().map(xref -> {
            return getStaffDto(xref, earliestCreatedByDate);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortDTOColumn);
        page.setSortDirection(sortDirection);

        LocalDate latestEndDate = staffRepo.getLatestEndDate(appId);
        page.setLatestEndDate(latestEndDate);

        int activeStaff = staffRepo.countActiveStaff(appId);
        page.setCanInsert(activeStaff == 0);

        return page;
    }

    private String getStaffSortColumn(StaffSortColumn sortDTOColumn) {
        if(StaffSortColumn.STAFFDESCRIPTION == sortDTOColumn)
            return "s.description";
        if(StaffSortColumn.BEGINDATE == sortDTOColumn)
            return "beginDate";
        return "endDate";
    }

    private StaffDto getStaffDto(StaffApplicationXref xref, LocalDateTime earliestCreatedByDate) {
        StaffDto dto = new StaffDto();
        dto.setId(xref.getId().longValue());
        dto.setBeginDate(xref.getBeginDate());
        dto.setEndDate(xref.getEndDate());
        dto.setStaffId(xref.getMasterStaffIndexId().longValue());
        MasterStaffIndexes staff = xref.getMasterStaffIndex();
        if(staff != null) {
            String firstName = staff.getFirstName() != null ? staff.getFirstName() + " ":"";
            String name = firstName + staff.getLastName();
            dto.setName(name);
        }
        if(earliestCreatedByDate != null) {
            dto.setIsSystemGenerated(!earliestCreatedByDate.isBefore(xref.getCreatedDate()));
        }
        return dto;
    }

    public OfficeDto addApplicationOffice(Long applicationId, OfficeCreationDto dto) {
        LOGGER.info("Adding a new office to an Application");

        if(dto.getReceivedDate() != null && dto.getReceivedDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot enter a date after today");
        }

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findById(appId);
        if(!foundApp.isPresent()) {
            throw new ValidationException("This application doesn't exist");
        }
        Application app = foundApp.get();

        // required for the next check
        if(app.getDateTimeReceivedEvent() == null){
            throw new ValidationException("This application must be received before adding an office");
        }

        if (dto.getReceivedDate() != null) {
            if (dto.getReceivedDate().isBefore(app.getDateTimeReceivedEvent().getEventDate().toLocalDate())) {
                throw new ValidationException("The Received Date cannot be before the Date/Time Received of the application");
            }

            LocalDate latestSentDate = officeRepo.getLatestSentDate(appId);
            if (latestSentDate != null && dto.getReceivedDate().isBefore(latestSentDate)) {
                throw new ValidationException("The Received Date cannot be before the latest Office Sent Date on the application");
            }
        }

        if(officeRepo.countActiveOffices(appId) > 0) {
            throw new ValidationException("Add a sent date to all offices before adding one");
        }

        OfficeApplicationXref model = getOfficeForCreation(dto);
        model.setApplicationId(appId);
        model = officeRepo.save(model);
        return getOfficeDto(model, null);
    }

    private OfficeApplicationXref getOfficeForCreation(OfficeCreationDto office) {
        OfficeApplicationXref model = new OfficeApplicationXref();
        model.setReceivedDate(office.getReceivedDate());
        model.setOfficeId(BigDecimal.valueOf(office.getOfficeId()));
        return model;
    }

    public OfficeDto editApplicationOffice(Long applicationId, Long officeXrefId, OfficeDto dto) {
        LOGGER.info("Editing an office");

        if (dto.getReceivedDate() != null && dto.getReceivedDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Office Date/Time Received cannot be after today");
        }

        if(dto.getSentDate() != null && dto.getSentDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Office Sent Date cannot be after today");
        }

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findById(appId);
        if(!foundApp.isPresent()) {
            throw new ValidationException("This application doesn't exist");
        }
        Application app = foundApp.get();

        BigDecimal xrefId = BigDecimal.valueOf(officeXrefId);
        Optional<OfficeApplicationXref> foundOffice = officeRepo.findById(xrefId);
        if(!foundOffice.isPresent()) {
            throw new ValidationException("This office isn't attached to this application");
        }
        OfficeApplicationXref office = foundOffice.get();

        if(app.getDateTimeReceivedEvent() == null){
            throw new ValidationException("This application must be received before editing an office");
        }

        if (office.getReceivedDate() != null && !office.getReceivedDate().equals(dto.getReceivedDate())) {
            throw new ValidationException("Cannot edit Office Received Date once set");
        }

        if (office.getSentDate() != null && !office.getSentDate().equals(dto.getSentDate())) {
            throw new ValidationException("Cannot edit Office Sent Date once set");
        }

        if (dto.getReceivedDate() == null && dto.getSentDate() != null) {
            throw new ValidationException("Office Sent Date cannot be set before the Office Received Date");
        }

        if (office.getReceivedDate() == null && dto.getReceivedDate() != null) {
            LocalDate latestSentDate = officeRepo.getLatestSentDate(xrefId);
            if (latestSentDate != null && dto.getReceivedDate().isBefore(latestSentDate)) {
                throw new ValidationException("The Received Date cannot be before the latest Office Sent Date on the application");
            }
        }

        office.setReceivedDate(dto.getReceivedDate());
        office.setSentDate(dto.getSentDate());
        office = officeRepo.save(office);
        return getOfficeDto(office, null);
    }

    public void deleteApplicationOffice(Long applicationId, Long officeXrefId) {
        LOGGER.info("Removing an attached office");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findById(appId);
        if(!foundApp.isPresent()) {
            throw new ValidationException("This application doesn't exist");
        }
        Application app = foundApp.get();

        BigDecimal xrefId = BigDecimal.valueOf(officeXrefId);
        Optional<OfficeApplicationXref> foundOffice = officeRepo.findById(xrefId);
        if(!foundOffice.isPresent()) {
            throw new ValidationException("This office isn't attached to this application");
        }
        OfficeApplicationXref office = foundOffice.get();

        LocalDateTime earliestCreatedDate = officeRepo.minCreatedDate(appId);
        if(earliestCreatedDate != null && !earliestCreatedDate.isBefore(office.getCreatedDate())) {
            throw new ValidationException("The System Generated office can not be deleted");
        }

        officeRepo.deleteById(xrefId);

        return;
    }

    public StaffDto editApplicationStaff(Long applicationId, Long staffXrefId, StaffDto dto) {
        LOGGER.info("Editing an staff");

        if(dto.getEndDate() != null && dto.getEndDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Staff End Date cannot be after today");
        }

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findById(appId);
        if(!foundApp.isPresent()) {
            throw new ValidationException("This application doesn't exist");
        }
        Application app = foundApp.get();

        BigDecimal xrefId = BigDecimal.valueOf(staffXrefId);
        Optional<StaffApplicationXref> foundStaff = staffRepo.findById(xrefId);
        if(!foundStaff.isPresent()) {
            throw new ValidationException("This staff is not attached to this application");
        }
        StaffApplicationXref staff = foundStaff.get();

        if(app.getDateTimeReceivedEvent() == null){
            throw new ValidationException("This application must be received before editing an office");
        }

        if (!staff.getBeginDate().equals(dto.getBeginDate())) {
            throw new ValidationException("Cannot edit Staff Begin Date");
        }

        if (staff.getEndDate() != null && !staff.getEndDate().equals(dto.getEndDate())) {
            throw new ValidationException("Cannot edit Staff End Date once set");
        }

        staff.setEndDate(dto.getEndDate());
        staff = staffRepo.save(staff);
        return getStaffDto(staff, null);
    }

    public StaffDto addApplicationStaff(Long applicationId, StaffCreationDto dto) {
        LOGGER.info("Adding a new staff");

        if(dto.getBeginDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot enter a date after today");
        }

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findById(appId);
        if(!foundApp.isPresent()) {
            throw new ValidationException("This application doesn't exist");
        }
        Application app = foundApp.get();

        // required for the next check
        if(app.getDateTimeReceivedEvent() == null){
            throw new ValidationException("This application must be received before adding a staff member");
        }

        if(staffRepo.countActiveStaff(appId) > 0) {
            throw new ValidationException("Add an end date to all staff before adding one");
        }

        LocalDate latestEndDate = staffRepo.getLatestEndDate(appId);
        if (latestEndDate != null && dto.getBeginDate().isBefore(latestEndDate)) {
            throw new ValidationException("The Begin Date cannot be before the latest Staff End Date on the application");
        }

        StaffApplicationXref model = getStaffForCreation(dto);
        model.setApplicationId(appId);
        model = staffRepo.save(model);
        return getStaffDto(model, null);
    }

    private StaffApplicationXref getStaffForCreation(StaffCreationDto staff) {
        StaffApplicationXref model = new StaffApplicationXref();
        model.setBeginDate(staff.getBeginDate());
        model.setMasterStaffIndexId(BigDecimal.valueOf(staff.getStaffId()));
        return model;
    }

    public void deleteApplicationStaff(Long applicationId, Long staffXrefId) {
        LOGGER.info("Removing an attached staff");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepository.findById(appId);
        if(!foundApp.isPresent()) {
            throw new ValidationException("This application doesn't exist");
        }
        Application app = foundApp.get();

        BigDecimal xrefId = BigDecimal.valueOf(staffXrefId);
        Optional<StaffApplicationXref> foundStaff = staffRepo.findById(xrefId);
        if(!foundStaff.isPresent()) {
            throw new ValidationException("This staff isn't attached to this application");
        }
        StaffApplicationXref staff = foundStaff.get();

        LocalDateTime earliestCreatedDate = staffRepo.minCreatedDate(appId);
        if(earliestCreatedDate != null && !earliestCreatedDate.isBefore(staff.getCreatedDate())) {
            throw new ValidationException("The System Generated staff can not be deleted");
        }

        staffRepo.deleteById(xrefId);

        return;
    }

}
