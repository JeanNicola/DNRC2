package gov.mt.wris.services.Implementation;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.*;
import gov.mt.wris.repositories.*;
import gov.mt.wris.services.MasterStaffIndexesService;
import gov.mt.wris.services.OwnershipUpdateService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OwnershipUpdateServiceImpl implements OwnershipUpdateService {

    private static Logger LOGGER = LoggerFactory.getLogger(OwnershipUpdateServiceImpl.class);

    @Autowired
    private OwnershipUpdateRepository ownershipUpdateRepository;

    @Autowired
    private WaterRightOwnershipUpdateXrefRepository waterRightXrefRepo;

    @Autowired
    private CustomerXrefRepository customerRepo;

    @Autowired
    private ApplicationOwnershipUpdateXrefRepository applicationXrefRepo;

    @Autowired
    private ApplicationRepository applicationRepo;

    @Autowired
    private MasterStaffIndexesService masterStaffIndexesService;

    @Autowired
    MasterStaffIndexesRepository generalStaffRepo;

    @Autowired
    private OwnershipUpdateOfficeRepository officeRepo;

    @Autowired
    private OwnershipUpdateStaffRepository staffRepo;

    @Autowired
    private CustomerRepository customerContactRepository;

    @Autowired
    private WaterRightRepository waterRightRepository;

    @Autowired
    private PaymentRepository payRepo;

    @Autowired
    OwnerRepository ownRepository;

    @Override
    public OwnershipUpdateWaterRightPageDto getOwnershipUpdateWaterRights(int pagenumber, int pagesize, OwnershipUpdateWaterRightSortColumn sortColumn, SortDirection sortDirection, Long ownerUpdateId) {

        LOGGER.info("Getting a page of Ownership Update Water Rights");
        Pageable pageable = PageRequest.of(pagenumber -1, pagesize, getOwnershipUpdateWaterRightsSort(sortColumn, sortDirection));
        OwnershipUpdateWaterRightPageDto page = new OwnershipUpdateWaterRightPageDto();
        Page<WaterRighOwnshiptXref> resultPage = ownershipUpdateRepository.getOwnershipUpdateWaterRights(pageable, BigDecimal.valueOf(ownerUpdateId));

        page.setResults(resultPage.getContent().stream().map(model -> {
            return getWaterRightDto(model.getWaterRight());
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    private OwnershipUpdateWaterRightDto getWaterRightDto(WaterRight model) {

        OwnershipUpdateWaterRightDto dto = new OwnershipUpdateWaterRightDto();

        if (model.getBasin() != null) dto.setBasin(model.getBasin());
        if (model.getExt() != null) dto.setExt(model.getExt());
        if (model.getWaterRightType() != null) dto.setTypeDescription(model.getWaterRightType() != null ? model.getWaterRightType().getDescription() : null);
        if (model.getWaterRightNumber() != null) dto.setWaterRightNumber(model.getWaterRightNumber() != null ? model.getWaterRightNumber().longValue() : null);

        dto.setCompleteWaterRightNumber(Helpers.buildCompleteWaterRightNumber(model.getBasin(), model.getWaterRightNumber().toString(), model.getExt()));
        dto.setWaterRightId(model.getWaterRightId().longValue());
        dto.setStatusCode(model.getWaterRightStatusCode());
        WaterRightStatus status = model.getWaterRightStatus();
        if(status != null) {
            dto.setStatusDescription(status.getDescription());
        }

        dto.setTypeCode(model.getWaterRightTypeCode());
        WaterRightType type = model.getWaterRightType();
        if(type != null) {
            dto.setTypeDescription(type.getDescription());
        }
        dto.setDividedOwnship("Y".equals(model.getDividedOwnship()));
        dto.setSevered("Y".equals(model.getSevered()));

        Integer validGeocode = model.getValidity().getValidGeocode();
        dto.setValidGeocode(validGeocode != null && validGeocode == 1);

        return dto;

    }

    private Sort getOwnershipUpdateWaterRightsSort(OwnershipUpdateWaterRightSortColumn column, SortDirection direction) {

        Sort sortGroup;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary, tertiary;

        switch (column) {
            case BASIN:
            case TYPEDESCRIPTION:
            case EXT:
            case STATUSCODE:
            case COMPLETEWATERRIGHTNUMBER:
                primary = Sort.by(sortOrderDirection, getWaterRightSortColumn(OwnershipUpdateWaterRightSortColumn.BASIN));
                secondary = Sort.by(sortOrderDirection, getWaterRightSortColumn(OwnershipUpdateWaterRightSortColumn.WATERRIGHTNUMBER));
                tertiary = Sort.by(sortOrderDirection, getWaterRightSortColumn(OwnershipUpdateWaterRightSortColumn.EXT));
                sortGroup = primary.and(secondary).and(tertiary);
                break;
            case DIVIDEDOWNSHIP:
                primary = Sort.by(sortOrderDirection, getWaterRightSortColumn(column));
                secondary = Sort.by(Sort.Direction.ASC, getWaterRightSortColumn(OwnershipUpdateWaterRightSortColumn.WATERRIGHTNUMBER));
                sortGroup = primary.and(secondary);
                break;
            default:
                // WATERRIGHTNUMBER
                primary = Sort.by(sortOrderDirection, getWaterRightSortColumn(OwnershipUpdateWaterRightSortColumn.WATERRIGHTNUMBER));
                secondary = Sort.by(Sort.Direction.ASC, getWaterRightSortColumn(OwnershipUpdateWaterRightSortColumn.BASIN));
                sortGroup = primary.and(secondary);
        }

        return sortGroup;

    }

    private String getWaterRightSortColumn(OwnershipUpdateWaterRightSortColumn sortColumn) {
        if (sortColumn == OwnershipUpdateWaterRightSortColumn.BASIN)
            return "waterRight.basin";
        else if (sortColumn == OwnershipUpdateWaterRightSortColumn.EXT)
            return "waterRight.ext";
        else if (sortColumn == OwnershipUpdateWaterRightSortColumn.TYPEDESCRIPTION)
            return "waterRight.waterRightType.description";
        else if (sortColumn == OwnershipUpdateWaterRightSortColumn.DIVIDEDOWNSHIP)
            return "waterRight.dividedOwnship";
        else if (sortColumn == OwnershipUpdateWaterRightSortColumn.STATUSCODE)
            return "waterRight.waterRightStatusCode";
        else if (sortColumn == OwnershipUpdateWaterRightSortColumn.COMPLETEWATERRIGHTNUMBER)
            return "waterRight.waterRightStatusCode";
        else {
            return "waterRight.waterRightNumber";
        }
    }

    public OwnershipUpdateSearchResultDto createOwnershipUpdate(OwnershipUpdateCreationDto dto) {
        try {
            return _createOwnershipUpdate(dto);
        } catch (DataIntegrityViolationException e) {
            // check that basin code and application type code exist
            if(e.getCause() instanceof ConstraintViolationException &&
                    e.getCause().getCause() instanceof BatchUpdateException) {
                ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                String constraintMessage = sc.getMessage();
                if (constraintMessage.contains("WRD.WROX_WRGT_FK")) {
                    throw new DataIntegrityViolationException("One of the Water Rights does not exist");
                } else if (constraintMessage.contains("WRD.COUX_CUST_FK")) {
                    throw new DataIntegrityViolationException("Make sure all the Buyers and Sellers exist");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

    @Transactional
    private OwnershipUpdateSearchResultDto _createOwnershipUpdate(OwnershipUpdateCreationDto dto) {
        LOGGER.info("Creating a new Ownership Update");

        // check that at least one buyer, seller and water right exists
        if(dto.getBuyers().size() == 0 &&
                dto.getSellers().size() == 0 &&
                dto.getWaterRights().size() == 0
        ) {
            throw new ValidationException("At least one buyer, seller and water right is required");
        }

        if(dto.getReceivedDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Date must be on or before today");
        }
        
        // Check for duplicates in submitted data
        List<Long> buyersList = dto.getBuyers();
        Set<Long> buyersSet = new HashSet<Long>(buyersList);
        if(buyersList.size() > buyersSet.size()) {
            throw new DataConflictException("Duplicate Buyers are not allowed");
        }

        List<Long> sellersList = dto.getSellers();
        Set<Long> sellersSet = new HashSet<Long>(sellersList);
        if(sellersList.size() > sellersSet.size()) {
            throw new DataConflictException("Duplicate Sellers are not allowed");
        }

        List<Long> waterRightsList = dto.getWaterRights();
        Set<Long> waterRightsSet = new HashSet<Long>(waterRightsList);
        if (waterRightsList.size() > waterRightsSet.size()) {
            throw new DataConflictException("Duplicate Water Rights are not allowed");
        }

        OwnershipUpdate model = new OwnershipUpdate();
        model.setDateReceived(dto.getReceivedDate());
        model.setTrnType(dto.getOwnershipUpdateType());
        model.setPendingDor(dto.getPendingDORValidation() ? "Y" : "N");
        model.setReceivedAs608(dto.getReceivedAs608() ? "Y" : "N");

        for (Long buyerContactId : dto.getBuyers()) {
            CustomerXref customer = createOwnershipUpdateCustomerXref(buyerContactId, Constants.BUYER_ROLE);
            model.addCustomer(customer);
        }
        for (Long sellerContactId : dto.getSellers()) {
            CustomerXref customer = createOwnershipUpdateCustomerXref(sellerContactId, Constants.SELLER_ROLE);
            model.addCustomer(customer);
        }

        model.setWaterRights(dto.getWaterRights().stream().map(waterRightId -> {
            WaterRight w = new WaterRight();
            w.setWaterRightId(BigDecimal.valueOf(waterRightId));
            return w;
        }).collect(Collectors.toSet()));

        String directoryUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        // set the office and processors
        MasterStaffIndexes masterStaffIndex = masterStaffIndexesService.getLocationStaffInfo(directoryUserName);
        model.setOfficeId(masterStaffIndex.getOfficeId());
        model.setProcessorOfficeId(masterStaffIndex.getOfficeId());
        model.setProcessorStaffId(masterStaffIndex.getId());
        model.setFeeStatus("FULL");
        model.setFeeDue(BigDecimal.ZERO);

        //add first staff and office

        OwnershipUpdate createdUpdate = ownershipUpdateRepository.save(model);

        // add change applications
        ownershipUpdateRepository.addMissingChangeApplications(createdUpdate.getOwnerUpdateId());

        // add first office and staff
        OwnershipUpdateOffice office = new OwnershipUpdateOffice();
        office.setOwnershipUpdateId(createdUpdate.getOwnerUpdateId());
        office.setOfficeId(masterStaffIndex.getOfficeId());
        office.setReceivedDate(dto.getReceivedDate());
        officeRepo.save(office);

        OwnershipUpdateStaff staff = new OwnershipUpdateStaff();
        staff.setOwnershipUpdateId(createdUpdate.getOwnerUpdateId());
        staff.setStaffId(masterStaffIndex.getId());
        staff.setBeginDate(dto.getReceivedDate());
        staffRepo.save(staff);

        OwnershipUpdateSearchResultDto returnDto = getOwnershipUpdateDto(createdUpdate);

        return returnDto;
    }

    private CustomerXref createOwnershipUpdateCustomerXref(Long contactId, String role) {
        CustomerXref model = new CustomerXref();
        model.setCustomerId(BigDecimal.valueOf(contactId));
        model.setRole(role);
        if (Constants.SELLER_ROLE == role) {
            model.setConttForDeed("N");
        }
        return model;
    }

    private OwnershipUpdateSearchResultDto getOwnershipUpdateDto(OwnershipUpdate model) {
        OwnershipUpdateSearchResultDto dto = new OwnershipUpdateSearchResultDto();
        dto.setOwnershipUpdateId(model.getOwnerUpdateId().longValue());
        dto.setOwnershipUpdateType(model.getTrnType());
        if(model.getUpdateTypeValue() != null) dto.setOwnershipUpdateTypeValue(model.getUpdateTypeValue().getMeaning());
        dto.setReceivedDate(model.getDateReceived());
        return dto;
    }

    @Override
    public OwnershipUpdateApplicationPageDto getOwnershipUpdateApplications(int pagenumber, int pagesize, OwnershipUpdateApplicationSortColumn sortColumn, SortDirection sortDirection, Long ownerUpdateId) {

        LOGGER.info("Getting a page of Ownership Update Applications");
        Pageable pageable = PageRequest.of(pagenumber -1, pagesize, getOwnershipUpdateApplicationsSort(sortColumn, sortDirection));
        OwnershipUpdateApplicationPageDto page = new OwnershipUpdateApplicationPageDto();

        Page<ApplicationOwnshipXref> resultPage = ownershipUpdateRepository.getOwnershipUpdateApplications(pageable, BigDecimal.valueOf(ownerUpdateId));
        page.setResults(resultPage.getContent().stream().map(model -> {
            return getApplicationDto(model.getApplication());
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    private OwnershipUpdateApplicationDto getApplicationDto(Application model) {
        OwnershipUpdateApplicationDto newDto = new OwnershipUpdateApplicationDto();

        if (model.getId() != null) newDto.setId(model.getId().longValue());
        if (model.getBasin() != null) newDto.setBasin(model.getBasin());

        return newDto;
    }

    private Sort getOwnershipUpdateApplicationsSort(OwnershipUpdateApplicationSortColumn column, SortDirection direction) {

        Sort sortGroup;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;

        switch (column) {
            case BASIN:
                primary = Sort.by(sortOrderDirection, getApplicationSortColumn(column));
                secondary = Sort.by(Sort.Direction.ASC, getApplicationSortColumn(OwnershipUpdateApplicationSortColumn.ID));
                sortGroup = primary.and(secondary);
                break;
            default:
                // ID
                primary = Sort.by(sortOrderDirection, getApplicationSortColumn(OwnershipUpdateApplicationSortColumn.ID));
                secondary = Sort.by(Sort.Direction.ASC, getApplicationSortColumn(OwnershipUpdateApplicationSortColumn.BASIN));
                sortGroup = primary.and(secondary);
        }

        return sortGroup;

    }

    private String getApplicationSortColumn(OwnershipUpdateApplicationSortColumn sortColumn) {
        if (sortColumn == OwnershipUpdateApplicationSortColumn.BASIN) {
            return "application.basin";
        } else {
            return "application.id";
        }
    }

    @Override
    public OwnershipUpdatePageDto searchOwnershipUpdates(int pageNumber, int pageSize, OwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, String ownershipUpdateId, String ownershipUpdateType, String waterRightNumber, LocalDate dateReceived, LocalDate dateSale, LocalDate dateProcessed, LocalDate dateTerminated) {

        LOGGER.info("Getting a Page of Ownership Updates");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<OwnershipUpdateSearchResultDto> resultsPage =
                ownershipUpdateRepository.searchOwnershipUpdatesWithCounts(
                        pageable, sortColumn, sortDirection, ownershipUpdateId, ownershipUpdateType, waterRightNumber, dateReceived, dateSale, dateProcessed, dateTerminated
                );
        OwnershipUpdatePageDto page = new OwnershipUpdatePageDto();
        page.setResults(resultsPage.getContent());

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(ownershipUpdateId != null) {
            filters.put("ownershipUpdateId", ownershipUpdateId.toString());
        }
        if(ownershipUpdateType != null) {
            filters.put("ownershipUpdateType", ownershipUpdateType.toString());
        }
        if(waterRightNumber != null) {
            filters.put("waterRightNumber", waterRightNumber.toString());
        }
        if(dateReceived != null) {
            filters.put("dateReceived", dateReceived.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if(dateSale != null) {
            filters.put("dateSale", dateSale.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if(dateProcessed != null) {
            filters.put("dateProcessed", dateProcessed.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if(dateTerminated != null) {
            filters.put("dateTerminated", dateTerminated.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        page.setFilters(filters);

        return page;

    }

    @Override
    public OwnershipUpdatesForContactPageDto getOwnershipUpdatesForContact(Long contactId, int pageNumber, int pageSize, OwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Get Ownership Updates for a Contact");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<OwnershipUpdatesForContactSearchResultDto> resultsPage =
                ownershipUpdateRepository.getOwnershipUpdatesForContact(
                        pageable, sortColumn, sortDirection, contactId
                );
        OwnershipUpdatesForContactPageDto page = new OwnershipUpdatesForContactPageDto();
        page.setResults(resultsPage.getContent());

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    @Override
    public OwnershipUpdateBuyersPageDto searchOwnershipUpdateBuyers(int pageNumber, int pageSize, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId) {

        LOGGER.info("Search Ownership Update Buyers");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<OwnershipUpdateSellersAndBuyerSearchResultDto> resultsPage =
                ownershipUpdateRepository.searchOwnershipUpdateBuyersWithCounts(
                        pageable, sortColumn, sortDirection, lastName, firstName, contactId
                );
        OwnershipUpdateBuyersPageDto page = new OwnershipUpdateBuyersPageDto();
        page.setResults(resultsPage.getContent());

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(lastName != null) {
            filters.put("lastName", lastName);
        }
        if(firstName != null) {
            filters.put("firstName", firstName);
        }
        if(contactId != null) {
            filters.put("contactId", contactId);
        }
        page.setFilters(filters);

        return page;
    }

    @Override
    public OwnershipUpdateBuyersPageDto getOwnershipUpdateBuyers(Long ownershipUpdateId, int pageNumber, int pageSize, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting a page of Ownership Update Buyers");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<Customer> resultsPage = ownershipUpdateRepository.getOwnershipUpdateBuyers(
                pageable, sortColumn, sortDirection, ownershipUpdateId);
        OwnershipUpdateBuyersPageDto page = new OwnershipUpdateBuyersPageDto();

        page.setResults(resultsPage.getContent().stream().map(oub -> {
            return loadOwnershipUpdateBuyersSearchResultDto(oub);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private OwnershipUpdateSellersAndBuyerSearchResultDto loadOwnershipUpdateBuyersSearchResultDto(Customer model) {

        OwnershipUpdateSellersAndBuyerSearchResultDto dto = new OwnershipUpdateSellersAndBuyerSearchResultDto();
        dto.setContactId(model.getCustomerId().longValue());
        dto.setLastName(model.getLastName());
        dto.setFirstName(model.getFirstName());
        dto.setName(Helpers.buildName(
                model.getLastName(),
                model.getFirstName(),
                model.getMiddleInitial(),
                model.getSuffix())
        );
        dto.setCount(model.getCustomerXref().size());
        return dto;
    }

    @Override
    public OwnershipUpdateSellersPageDto searchOwnershipUpdateSellers(int pageNumber, int pageSize, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId) {

        LOGGER.info("Search Ownership Update Sellers");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<OwnershipUpdateSellersAndBuyerSearchResultDto> resultsPage =
                ownershipUpdateRepository.searchOwnershipUpdateSellersWithCounts(
                        pageable, sortColumn, sortDirection, lastName, firstName, contactId
                );
        OwnershipUpdateSellersPageDto page = new OwnershipUpdateSellersPageDto();
        page.setResults(resultsPage.getContent());

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(lastName != null) {
            filters.put("lastName", lastName);
        }
        if(firstName != null) {
            filters.put("firstName", firstName);
        }
        if(contactId != null) {
            filters.put("contactId", contactId);
        }
        page.setFilters(filters);

        return page;
    }

    @Override
    public OwnershipUpdateSellersPageDto getOwnershipUpdateSellers(Long ownershipUpdateId, int pageNumber, int pageSize, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Getting a page of Ownership Update Sellers");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<Customer> resultsPage = ownershipUpdateRepository.getOwnershipUpdateSellers(
                pageable, sortColumn, sortDirection, ownershipUpdateId);
        OwnershipUpdateSellersPageDto page = new OwnershipUpdateSellersPageDto();

        page.setResults(resultsPage.getContent().stream().map(ous -> {
            return loadOwnershipUpdateSellersSearchResultDto(ous);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    private OwnershipUpdateSellersAndBuyerSearchResultDto loadOwnershipUpdateSellersSearchResultDto(Customer model) {

        OwnershipUpdateSellersAndBuyerSearchResultDto dto = new OwnershipUpdateSellersAndBuyerSearchResultDto();
        dto.setContactId(model.getCustomerId().longValue());
        dto.setLastName(model.getLastName());
        dto.setFirstName(model.getFirstName());
        dto.setName(Helpers.buildName(
                model.getLastName(),
                model.getFirstName(),
                model.getMiddleInitial(),
                model.getSuffix())
        );
        dto.setCount(model.getCustomerXref().size());
        return dto;
    }

    @Override
    public OwnershipUpdateDto getOwnershipUpdate(BigDecimal ownershipUpdateId) {

        LOGGER.info("Get an Ownership Update");

        Optional<OwnershipUpdate> foundOwnershipUpdate = ownershipUpdateRepository.getOwnershipUpdate(ownershipUpdateId);
        if(!foundOwnershipUpdate.isPresent())
            throw new NotFoundException(String.format("Ownership Update id %s not found.", ownershipUpdateId));

        OwnershipUpdateDto dto = loadOwnershipUpdateDto(foundOwnershipUpdate.get());
        dto.setCanPrintDecreeReport(generalStaffRepo.hasRoles(Arrays.asList(Constants.PRINT_DECREE_REPORT)) > 0);
        /* Load 'header' level properties needed to enable ownership update transfer process */
        dto.setParentCount(ownershipUpdateRepository.getParentApplicationCountForOwnershipUpdate(ownershipUpdateId));
        dto.setChildCount(ownershipUpdateRepository.getChildApplicationCountForOwnershipUpdate(ownershipUpdateId));
        dto.setAllAppsInc(ownershipUpdateRepository.getAllApplicationsIncludedFlag(ownershipUpdateId));
        dto.setOtherOwnerUpdateId(ownershipUpdateRepository.getWaterRightSharedWithOtherOwnershipUpdateFlag(foundOwnershipUpdate.get()));
        return dto;

    }

    @Override
    public OwnershipUpdateDto changeOwnershipUpdate(BigDecimal ownershipUpdateId, OwnershipUpdateUpdateDto updateOwnershipUpdate) {

        LOGGER.info("Change an Ownership Update");

        OwnershipUpdate oldOwnershipUpdate = new OwnershipUpdate();
        Optional<OwnershipUpdate> foundOwnershipUpdate = ownershipUpdateRepository.findById(ownershipUpdateId);
        if(!foundOwnershipUpdate.isPresent())
            throw new NotFoundException(String.format("Ownership Update id %s not found.", ownershipUpdateId));

        oldOwnershipUpdate = foundOwnershipUpdate.get();
        oldOwnershipUpdate.setTrnType(updateOwnershipUpdate.getOwnershipUpdateType());
        oldOwnershipUpdate.setDateReceived(updateOwnershipUpdate.getDateReceived());
        oldOwnershipUpdate.setDateTerminated(updateOwnershipUpdate.getDateTerminated());
        oldOwnershipUpdate.setPendingDor(updateOwnershipUpdate.getPendingDor());
        oldOwnershipUpdate.setReceivedAs608(updateOwnershipUpdate.getReceivedAs608());
        oldOwnershipUpdate.setNotes(updateOwnershipUpdate.getNotes());

        oldOwnershipUpdate = ownershipUpdateRepository.saveAndFlush(oldOwnershipUpdate);

        Optional<OwnershipUpdate> afterSaveFoundOwnershipUpdate = ownershipUpdateRepository.findById(ownershipUpdateId);
        if(!afterSaveFoundOwnershipUpdate.isPresent())
            throw new NotFoundException(String.format("Ownership Update id %s not found.", ownershipUpdateId));

        return loadOwnershipUpdateDto(afterSaveFoundOwnershipUpdate.get());

    }

    private OwnershipUpdateDto loadOwnershipUpdateDto(OwnershipUpdate model) {

        OwnershipUpdateDto dto = new OwnershipUpdateDto();
        dto.setOwnershipUpdateId(model.getOwnerUpdateId().longValue());
        dto.setDateProcessed(model.getDateProcessed());
        dto.setDateReceived(model.getDateReceived());
        dto.setDateTerminated(model.getDateTerminated());
        dto.setOwnershipUpdateType(model.getTrnType());
        dto.setOwnershipUpdateTypeVal(model.getUpdateTypeValue().getMeaning());
        dto.setPendingDor(model.getPendingDor());
        if (model.getPendingDorVal()!= null) {
            dto.setPendingDorVal(model.getPendingDorVal().getMeaning());
        }
        dto.setReceivedAs608(model.getReceivedAs608());
        if (model.getReceivedAs608Val()!=null) {
            dto.setReceivedAs608Val(model.getReceivedAs608Val().getMeaning());
        }
        dto.setNotes(model.getNotes());
        dto.setCanTransfer((canTransfer(model)==true?"Y":"N"));
        dto.setCanTransferVal((dto.getCanTransfer().equals("Y")?"YES":"NO"));
        return dto;

    }

    private boolean canTransfer(OwnershipUpdate model) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate FEES_CHARGE_DATE = LocalDate.parse(Constants.DNRC_START_FEES_CHARGE_DATE, dtf); /* 09-30-2017 */

        /* TEST A */
        if ((model.getTrnType().equals(Constants.DOR_608_TRANSACTION_TYPE)) &&
                (model.getDateProcessed() == null) &&
                (model.getDateTerminated() == null) &&
                (model.getFeeStatus()!=null && !model.getFeeStatus().equals(Constants.DNRC_FEE_STATUS_FULL)) &&
                ("N".equals(model.getPendingDor()))) {
            LOGGER.debug(String.format("ownerUpdateId %s failed test A", model.getOwnerUpdateId()));
            return false;
        }

        /* TEST B */
        if ((model.getTrnType().equals(Constants.STANDARD_608_TRANSACTION_TYPE)) &&
                (model.getDateProcessed() == null) &&
                (model.getDateTerminated() == null) &&
                (model.getDateReceived().isAfter(FEES_CHARGE_DATE)) &&
                (model.getFeeStatus()!=null && !model.getFeeStatus().equals(Constants.DNRC_FEE_STATUS_FULL))) {
            LOGGER.debug(String.format("ownerUpdateId %s failed test B", model.getOwnerUpdateId()));
            return false;
        }

        /* TEST C */
        if ((model.getTrnType().equals(Constants.DOR_608_TRANSACTION_TYPE)) &&
                (model.getDateProcessed() == null) &&
                (model.getDateTerminated() == null) &&
                (model.getPendingDor() != null && model.getPendingDor().equals("Y"))) {
            LOGGER.debug(String.format("ownerUpdateId %s failed test C", model.getOwnerUpdateId()));
            return false;
        }

        /* TEST D */
        if (model.getDateProcessed() != null || model.getDateTerminated() != null) {
            LOGGER.debug(String.format("ownerUpdateId %s failed test D", model.getOwnerUpdateId()));
            return false;
        }

        /* TEST E */
        if (model.getTrnType().equals(Constants.ADM_TRANSACTION_TYPE)) {
            LOGGER.debug(String.format("ownerUpdateId %s failed test E", model.getOwnerUpdateId()));
            return false;
        }

        /* TEST F */
        if (!ownershipUpdateIdHasBuyersSellersAndWaterRights(model.getOwnerUpdateId())) {
            LOGGER.debug(String.format("ownerUpdateId %s failed test F", model.getOwnerUpdateId()));
            return false;
        }

        return true;

    }

    private boolean ownershipUpdateIdHasBuyersSellersAndWaterRights(BigDecimal ownerUpdateId) {

        boolean test = true;
        TreeMap<String, Integer> ownerCounts = ownershipUpdateRepository.getOwnershipUpdateCountsForTransfer(ownerUpdateId);
        Set s = ownerCounts.entrySet();
        Iterator i = s.iterator();
        while(i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            LOGGER.debug(String.format("ownerUpdateId %s KEY: %s, VALUE: %s", ownerUpdateId, e.getKey(), e.getValue()));
            if ((Integer)e.getValue() < 1) {
                test = false;
                break;
            }
        }
        return test;

    }


    @Override
    public BuyersForOwnershipUpdatePageDto getBuyersForOwnershipUpdate(Long ownershipUpdateId, int pageNumber, int pageSize, BuyersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Get a page of Associate Buyers for Ownership Update");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<CustomerXref> resultsPage = ownershipUpdateRepository.getBuyersForOwnershipUpdate(
                pageable, sortColumn, sortDirection, ownershipUpdateId);
        BuyersForOwnershipUpdatePageDto page = new BuyersForOwnershipUpdatePageDto();

        page.setResults(resultsPage.getContent().stream().map(coux -> {
            return loadAssociateBuyersOwnershipUpdateSearchResultDto(coux);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;

    }

    private BuyersForOwnershipUpdateSearchResultDto loadAssociateBuyersOwnershipUpdateSearchResultDto(CustomerXref model) {

        BuyersForOwnershipUpdateSearchResultDto dto = new BuyersForOwnershipUpdateSearchResultDto();
        dto.setId(model.getCouxIdSeq().longValue());
        dto.setContactId(model.getCustomer().getCustomerId().longValue());
        dto.setLastName(model.getCustomer().getLastName());
        dto.setFirstName(model.getCustomer().getFirstName());
        dto.setName(Helpers.buildName(
                model.getCustomer().getLastName(),
                model.getCustomer().getFirstName(),
                model.getCustomer().getMiddleInitial(),
                model.getCustomer().getSuffix())
        );
        if (model.getStrDate() != null)
            dto.setStartDate(model.getStrDate());
        return dto;

    }


    @Override
    public SellersForOwnershipUpdatePageDto getSellersForOwnershipUpdate(Long ownershipUpdateId, int pageNumber, int pageSize, SellersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Get a page of Associate Sellers for Ownership Update");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<CustomerXref> resultsPage = ownershipUpdateRepository.getSellersForOwnershipUpdate(
                pageable, sortColumn, sortDirection, ownershipUpdateId);
        SellersForOwnershipUpdatePageDto page = new SellersForOwnershipUpdatePageDto();

        page.setResults(resultsPage.getContent().stream().map(coux -> {
            return loadAssociateSellersOwnershipUpdateSearchResultDto(coux);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    private SellersForOwnershipUpdateSearchResultDto loadAssociateSellersOwnershipUpdateSearchResultDto(CustomerXref model) {

        SellersForOwnershipUpdateSearchResultDto dto = new SellersForOwnershipUpdateSearchResultDto();
        dto.setId(model.getCouxIdSeq().longValue());
        dto.setContactId(model.getCustomer().getCustomerId().longValue());
        dto.setLastName(model.getCustomer().getLastName());
        dto.setFirstName(model.getCustomer().getFirstName());
        dto.setName(Helpers.buildName(
                model.getCustomer().getLastName(),
                model.getCustomer().getFirstName(),
                model.getCustomer().getMiddleInitial(),
                model.getCustomer().getSuffix())
        );
        if (model.getConttForDeed() != null) {
            dto.setContractForDeedRle(model.getConttForDeed());
            dto.setContractForDeedRleVal(model.getConttForDeedValue().getMeaning());
        }
        if (model.getEndDate() != null)
            dto.setEndDate(model.getEndDate());
        return dto;

    }

    @Transactional
    public void deleteOwnershipUpdate(Long ownershipUpdateId) {
        LOGGER.info("deleting an Ownership Update");
        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);

        waterRightXrefRepo.deleteByOwnershipUpdateId(updateId);

        customerRepo.deleteByOwnershipUpdateId(updateId);

        applicationXrefRepo.deleteByOwnershipUpdateId(updateId);

        officeRepo.deleteByOwnershipUpdateId(updateId);

        staffRepo.deleteByOwnershipUpdateId(updateId);

        ownershipUpdateRepository.deleteById(updateId);
    }

    // File Location and Processor
    public ResponsibleOfficeDto getResponsibleOffice(Long ownerUpdateId) {
        LOGGER.info("Getting the Responsible Office for the Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findOwnershipUpdateWithOffice(updateId);
        if(!foundUpdate.isPresent()) {
            throw new NotFoundException("This Ownership Update does not exist");
        }
        OwnershipUpdate update = foundUpdate.get();

        ResponsibleOfficeDto dto = getResponsibleOfficeDto(update.getOffice());

        return dto;
    }

    private ResponsibleOfficeDto getResponsibleOfficeDto(Office office) {
        ResponsibleOfficeDto dto = new ResponsibleOfficeDto();
        if(office != null) {
            dto.setOffice(office.getDescription());
            dto.setOfficeId(office.getId().longValue());
        }
        return dto;
    }

    public ResponsibleOfficeDto editResponsibleOffice(Long ownerUpdateId, ResponsibleOfficeDto dto) {
        LOGGER.info("Changing the Responsible Office");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findOwnershipUpdateWithOffice(updateId);
        if(!foundUpdate.isPresent()) {
            throw new NotFoundException("This Ownership Update does not exist");
        }
        OwnershipUpdate update = foundUpdate.get();

        update.setOfficeId(BigDecimal.valueOf(dto.getOfficeId()));

        update = ownershipUpdateRepository.save(update);

        ResponsibleOfficeDto returnDto = getResponsibleOfficeDto(update.getOffice());

        return returnDto;
    }

    public ProcessorDto getProcessor(Long ownerUpdateId) {
        LOGGER.info("Getting the Processor for the Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findOwnershipUpdateWithProcessor(updateId);
        if(!foundUpdate.isPresent()) {
            throw new NotFoundException("This Ownership Update does not exist");
        }
        OwnershipUpdate update = foundUpdate.get();

        ProcessorDto dto = getProcessorDto(update);

        return dto;
    }

    private ProcessorDto getProcessorDto(OwnershipUpdate update) {
        ProcessorDto dto = new ProcessorDto();
        Office office = update.getProcessorOffice();
        if(office != null) dto.setOffice(office.getDescription());
        if(update.getProcessorOfficeId() != null) dto.setOfficeId(update.getProcessorOfficeId().longValue());
        MasterStaffIndexes staff = update.getProcessorStaff();
        if(staff != null) {
            String name = staff.getFirstName() + " " + staff.getLastName();
            dto.setStaff(name);
        }
        if(update.getProcessorStaffId() != null) dto.setStaffId(update.getProcessorStaffId().longValue());
        return dto;
    }

    public ProcessorDto editProcessor(Long ownerUpdateId, ProcessorDto dto) {
        LOGGER.info("Changing the Processor");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findOwnershipUpdateWithProcessor(updateId);
        if(!foundUpdate.isPresent()) {
            throw new NotFoundException("This Ownership Update does not exist");
        }
        OwnershipUpdate update = foundUpdate.get();

        update.setProcessorOfficeId(BigDecimal.valueOf(dto.getOfficeId()));
        update.setProcessorStaffId(BigDecimal.valueOf(dto.getStaffId()));

        update = ownershipUpdateRepository.save(update);

        ProcessorDto returnDto = getProcessorDto(update);

        return returnDto;
    }

    public OfficePageDto getOwnershipUpdateOffices(Long ownershipUpdateId,
                                                   int pageNumber,
                                                   int pageSize,
                                                   OfficeSortColumn sortDTOColumn,
                                                   DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting the offices for an Ownership Update");

        String sortColumn = getOfficeSortColumn(sortDTOColumn);
        Sort.Direction direction = (sortDirection == DescSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortColumn).and(Sort.by(Sort.Direction.DESC, "id")));

        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);
        Page<OwnershipUpdateOffice> resultPage = officeRepo.findOwnershipUpdateOffices(pageable, updateId);

        LocalDateTime earliestCreatedByDate = officeRepo.minCreatedDate(updateId);

        OfficePageDto page = new OfficePageDto();

        page.setResults(resultPage.getContent().stream().map(xref -> {
            return getOfficeDto(xref, earliestCreatedByDate);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());

        page.setTotalPages(resultPage.getTotalPages());
        page.setTotalElements(resultPage.getTotalElements());

        page.setSortColumn(sortDTOColumn);
        page.setSortDirection(sortDirection);

        LocalDate latestSentDate = officeRepo.getLatestSentDate(updateId);
        page.setLatestSentDate(latestSentDate);

        int activeOffices = officeRepo.countActiveOffices(updateId);
        page.setCanInsert(activeOffices == 0);

        return page;
    }

    private String getOfficeSortColumn(OfficeSortColumn sortColumn) {
        if (OfficeSortColumn.OFFICEDESCRIPTION == sortColumn)
            return "o.description";
        if (OfficeSortColumn.RECEIVEDDATE == sortColumn)
            return "receivedDate";
        return "sentDate";
    }

    private OfficeDto getOfficeDto(OwnershipUpdateOffice xref, LocalDateTime earliestCreatedByDate) {
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

    public StaffPageDto getOwnershipUpdateStaff(Long ownershipUpdateId,
                                                int pageNumber,
                                                int pageSize,
                                                StaffSortColumn sortDTOColumn,
                                                DescSortDirection sortDirection
    ) {
        LOGGER.info("Getting the staff for an Ownership Update");

        String sortColumn = getStaffSortColumn(sortDTOColumn);
        Sort.Direction direction = (sortDirection == DescSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortColumn).and(Sort.by(Sort.Direction.DESC, "id")));

        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);
        Page<OwnershipUpdateStaff> resultsPage = staffRepo.findOwnershipUpdateStaff(pageable, updateId);

        LocalDateTime earliestCreatedByDate = staffRepo.minCreatedDate(updateId);

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

        LocalDate latestEndDate = staffRepo.getLatestEndDate(updateId);
        page.setLatestEndDate(latestEndDate);

        int activeStaff = staffRepo.countActiveStaff(updateId);
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

    private StaffDto getStaffDto(OwnershipUpdateStaff xref, LocalDateTime earliestCreatedByDate) {
        StaffDto dto = new StaffDto();
        dto.setId(xref.getId().longValue());
        dto.setBeginDate(xref.getBeginDate());
        dto.setEndDate(xref.getEndDate());
        dto.setStaffId(xref.getStaffId().longValue());
        MasterStaffIndexes staff = xref.getStaff();
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

    public OfficeDto addOwnershipUpdateOffice(Long ownershipUpdateId, OfficeCreationDto dto) {
        LOGGER.info("Adding a new office");

        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This Ownership Update doesn't exist");
        }
        OwnershipUpdate ownershipUpdate = foundUpdate.get();

        if (dto.getReceivedDate() != null) {
            if(dto.getReceivedDate().isBefore(ownershipUpdate.getDateReceived())) {
                throw new ValidationException("The Received Date cannot be before the Ownership Update Date Received/Sale Date");
            }

            if(dto.getReceivedDate().isAfter(LocalDate.now())) {
                throw new ValidationException("Cannot enter a date after today");
            }

            LocalDate latestSentDate = officeRepo.getLatestSentDate(updateId);
            if (latestSentDate != null && dto.getReceivedDate().isBefore(latestSentDate)) {
                throw new ValidationException("The Received Date cannot be before the latest Office Sent Date on the ownership update");
            }
        }

        if(officeRepo.countActiveOffices(updateId) > 0) {
            throw new ValidationException("Add a sent date to all offices before adding one");
        }

        OwnershipUpdateOffice model = getOfficeForCreation(dto);
        model.setOwnershipUpdateId(updateId);
        model = officeRepo.save(model);
        return getOfficeDto(model, null);
    }

    private OwnershipUpdateOffice getOfficeForCreation(OfficeCreationDto office) {
        OwnershipUpdateOffice model = new OwnershipUpdateOffice();
        model.setReceivedDate(office.getReceivedDate());
        model.setOfficeId(BigDecimal.valueOf(office.getOfficeId()));
        return model;
    }

    public OfficeDto editOwnershipUpdateOffice(Long ownershipUpdateId, Long officeXrefId, OfficeDto dto) {
        LOGGER.info("Editing an office");

        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This ownership update doesn't exist");
        }
        OwnershipUpdate update = foundUpdate.get();

        BigDecimal xrefId = BigDecimal.valueOf(officeXrefId);
        Optional<OwnershipUpdateOffice> foundOffice = officeRepo.findById(xrefId);
        if(!foundOffice.isPresent()) {
            throw new ValidationException("This office isn't attached to this Ownership Update");
        }
        OwnershipUpdateOffice office = foundOffice.get();

        if (office.getReceivedDate() != null && !office.getReceivedDate().equals(dto.getReceivedDate())) {
            throw new DataConflictException("Cannot edit Office Received Date once set");
        }

        if (office.getSentDate() != null && !office.getSentDate().equals(dto.getSentDate())) {
            throw new DataConflictException("Cannot edit Office Sent Date once set");
        }

        if (office.getReceivedDate() == null && dto.getReceivedDate() != null) {
            if(dto.getReceivedDate().isBefore(update.getDateReceived())) {
                throw new ValidationException("The Received Date cannot be before the Ownership Update Date Received Date/Sale Date");
            }

            if(dto.getReceivedDate().isAfter(LocalDate.now())) {
                throw new ValidationException("Cannot enter a Received Date after today");
            }

            LocalDate latestSentDate = officeRepo.getLatestSentDate(updateId);
            if (latestSentDate != null && dto.getReceivedDate().isBefore(latestSentDate)) {
                throw new ValidationException("The Received Date cannot be before the latest Office Sent Date on the ownership update");
            }
        }

        if (office.getSentDate() == null && dto.getSentDate() != null) {
            if (dto.getReceivedDate() == null) {
                throw new ValidationException("The Sent Date cannot be set without an existing Received Date");
            }

            if(dto.getSentDate().isAfter(LocalDate.now())) {
                throw new ValidationException("Cannot enter a Sent Date after today");
            }

            if (dto.getSentDate().isBefore(dto.getReceivedDate())) {
                throw new ValidationException("The Sent Date cannot be before the Office Received Date");
            }
        }

        // removing an end date when there's already one without an end date is not allowed
        if(officeRepo.countActiveOffices(updateId) > 0 && office.getReceivedDate() != null && dto.getSentDate() == null) {
            throw new ValidationException("Add a sent date to all offices before removing an office");
        }

        office.setSentDate(dto.getSentDate());
        office.setReceivedDate(dto.getReceivedDate());
        office = officeRepo.save(office);
        return getOfficeDto(office, null);
    }

    public void deleteOwnershipUpdateOffice(Long ownershipUpdateId, Long officeXrefId) {
        LOGGER.info("Removing an attached office");

        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This ownership update doesn't exist");
        }

        BigDecimal xrefId = BigDecimal.valueOf(officeXrefId);
        Optional<OwnershipUpdateOffice> foundOffice = officeRepo.findById(xrefId);
        if(!foundOffice.isPresent()) {
            throw new ValidationException("This office isn't attached to this Ownership Update");
        }
        OwnershipUpdateOffice office = foundOffice.get();

        LocalDateTime earliestCreatedDate = officeRepo.minCreatedDate(updateId);
        if(earliestCreatedDate != null && !earliestCreatedDate.isBefore(office.getCreatedDate())) {
            throw new ValidationException("The System Generated office can not be deleted");
        }

        officeRepo.deleteById(xrefId);

        return;
    }

    public StaffDto addOwnershipUpdateStaff(Long ownershipUpdateId, StaffCreationDto dto) {
        LOGGER.info("Adding a new staff");

        if(dto.getBeginDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot enter a date after today");
        }

        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This ownership update doesn't exist");
        }
        OwnershipUpdate update = foundUpdate.get();

        if(dto.getBeginDate().isBefore(update.getDateReceived())) {
            throw new ValidationException("The Received Date cannot be before the Ownership Update Date Received/Sale Date");
        }

        LocalDate latestEndDate = staffRepo.getLatestEndDate(updateId);
        if (latestEndDate != null && dto.getBeginDate().isBefore(latestEndDate)) {
            throw new ValidationException("The Begin Date cannot be before the latest Staff End Date on the ownership update");
        }

        if(staffRepo.countActiveStaff(updateId) > 0) {
            throw new ValidationException("Add an end date to all staff before adding one");
        }

        OwnershipUpdateStaff model = getStaffForCreation(dto);
        model.setOwnershipUpdateId(updateId);
        model = staffRepo.save(model);
        return getStaffDto(model, null);
    }

    private OwnershipUpdateStaff getStaffForCreation(StaffCreationDto staff) {
        OwnershipUpdateStaff model = new OwnershipUpdateStaff();
        model.setBeginDate(staff.getBeginDate());
        model.setStaffId(BigDecimal.valueOf(staff.getStaffId()));
        return model;
    }

    public StaffDto editOwnershipUpdateStaff(Long ownershipUpdateId, Long staffXrefId, StaffDto dto) {
        LOGGER.info("Editing an Ownership Update's staff member");

        if(dto.getBeginDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot enter a Begin Date after today");
        }

        if(dto.getEndDate() != null && dto.getEndDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot enter an End Date after today");
        }

        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This ownership update doesn't exist");
        }
        OwnershipUpdate update = foundUpdate.get();

        BigDecimal xrefId = BigDecimal.valueOf(staffXrefId);
        Optional<OwnershipUpdateStaff> foundStaff = staffRepo.findById(xrefId);
        if(!foundStaff.isPresent()) {
            throw new ValidationException("This staff is not attached to this ownership update");
        }
        OwnershipUpdateStaff staff = foundStaff.get();

        if (!staff.getBeginDate().equals(dto.getBeginDate())) {
            throw new DataConflictException("Cannot modify the Begin Date on Ownership Update Staff");
        }

        if (staff.getEndDate() != null && !staff.getEndDate().equals(dto.getEndDate())) {
            throw new DataConflictException("Cannot modify the End Date on Ownership Update Staff once set");
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(staff.getBeginDate())) {
            throw new ValidationException("The End Date cannot be before the Staff Begin Date");
        }

        // removing an end date when there's already one without an end date is not allowed
        if(staffRepo.countActiveStaff(updateId) > 0 && staff.getEndDate() != null && dto.getEndDate() == null) {
            throw new ValidationException("Add a end date to all staff before removing one");
        }

        staff.setEndDate(dto.getEndDate());
        staff = staffRepo.save(staff);
        return getStaffDto(staff, null);
    }

    public void deleteOwnershipUpdateStaff(Long ownershipUpdateId, Long staffXrefId) {
        LOGGER.info("Removing an attached staff");

        BigDecimal updateId = BigDecimal.valueOf(ownershipUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This ownership update doesn't exist");
        }

        BigDecimal xrefId = BigDecimal.valueOf(staffXrefId);
        Optional<OwnershipUpdateStaff> foundStaff = staffRepo.findById(xrefId);
        if(!foundStaff.isPresent()) {
            throw new ValidationException("This staff is not attached to this application");
        }
        OwnershipUpdateStaff staff = foundStaff.get();

        LocalDateTime earliestCreatedDate = staffRepo.minCreatedDate(updateId);
        if(earliestCreatedDate != null && !earliestCreatedDate.isBefore(staff.getCreatedDate())) {
            throw new ValidationException("The System Added Staff Member can not be deleted");
        }

        staffRepo.deleteById(xrefId);

        return;
    }

    public OwnershipUpdateSellerDto changeOwnershipUpdateSeller(Long ownerUpdateId, Long sellerId, OwnershipUpdateSellerUpdateDto ownershipUpdateSellerUpdateDto) {

        LOGGER.info("Change an Ownership Update Seller");

        CustomerXref customerXref = new CustomerXref();
        Optional<CustomerXref> foundCustomerXref = customerRepo.findById(BigDecimal.valueOf(sellerId));
        if(!foundCustomerXref.isPresent()) {
            throw new ValidationException("This Seller isn't attached to this Ownership Update");
        }
        customerXref = foundCustomerXref.get();
        /* this is the only property changeOwnershipUpdateSeller() will update */
        customerXref.setConttForDeed(ownershipUpdateSellerUpdateDto.getContractForDeedRle());
        customerRepo.saveAndFlush(customerXref);

        OwnershipUpdateSellerDto dto = new OwnershipUpdateSellerDto();
        dto.setOwnershipUpdateId(customerXref.getOwnershipUpdate().getOwnerUpdateId().longValue());
        dto.setContactId(customerXref.getCustomerId().longValue());
        dto.setContractForDeedRle(customerXref.getConttForDeed());

        return dto;

    }

    @Transactional
    public SellerForOwnershipUpdateResultDto createSellerReferenceToOwnershipUpdate(Long ownerUpdateId, SellerReferenceToOwnershipUpdateCreationDto dto) {
        LOGGER.info("Add new Seller references to an Ownership Update");

        List<String> duplicates = Helpers.findDuplicates(dto.getContactIds()).stream().map(String::valueOf).collect(Collectors.toList());
        if(duplicates.size() > 0) {
            String duplicateError = String.join(",\n", duplicates);
            throw new ValidationException("The following Sellers were entered twice:\n" + duplicateError);
        }

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent())
            throw new ValidationException(String.format("This ownership update %s doesn't exist", updateId));
        OwnershipUpdate ownershipUpdate = foundUpdate.get();

        //check that all are actual contacts
        List<BigDecimal> contactIds = dto.getContactIds().stream().map(BigDecimal::valueOf).collect(Collectors.toList());
        List<BigDecimal> availableContacts = customerContactRepository.getCustomerId(contactIds);
        if(availableContacts.size() < contactIds.size()) {
            contactIds.removeAll(availableContacts);
            String failedContacts = contactIds.stream().map(BigDecimal::toString).collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Contacts do not exist:\n" + failedContacts);
        }

        //prevent duplicates
        List<BigDecimal> usedContacts = customerRepo.getCustomerIds(updateId, contactIds, Constants.SELLER_ROLE);
        if(!usedContacts.isEmpty()) {
            String failedContacts = usedContacts.stream().map(BigDecimal::toString).collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Contacts are duplicates: \n" + failedContacts);
        }

        List<CustomerXref> contacts = dto.getContactIds().stream()
            .map(contactId -> {
                CustomerXref xref = createOwnershipUpdateCustomerXref(contactId, Constants.SELLER_ROLE);
                xref.setOwnershipUpdate(ownershipUpdate);
                return xref;
            }).collect(Collectors.toList());
        
        customerRepo.saveAll(contacts);

        return new SellerForOwnershipUpdateResultDto().contactIds(dto.getContactIds());
    }

    @Transactional
    public BuyerForOwnershipUpdateResultDto createBuyerReferenceToOwnershipUpdate(Long ownerUpdateId, BuyerReferenceToOwnershipUpdateCreationDto dto) {
        LOGGER.info("Add new Buyer references to an Ownership Update");

        List<String> duplicates = Helpers.findDuplicates(dto.getContactIds()).stream().map(String::valueOf).collect(Collectors.toList());
        if(duplicates.size() > 0) {
            String duplicateError = String.join(",\n", duplicates);
            throw new ValidationException("The following Buyers were entered twice:\n" + duplicateError);
        }

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent())
            throw new ValidationException(String.format("This ownership update %s doesn't exist", updateId));
        OwnershipUpdate ownershipUpdate = foundUpdate.get();

        //check that all are actual contacts
        List<BigDecimal> contactIds = dto.getContactIds().stream().map(BigDecimal::valueOf).collect(Collectors.toList());
        List<BigDecimal> availableContacts = customerContactRepository.getCustomerId(contactIds);
        if(availableContacts.size() < contactIds.size()) {
            contactIds.removeAll(availableContacts);
            String failedContacts = contactIds.stream().map(BigDecimal::toString).collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Contacts do not exist:\n" + failedContacts);
        }

        //prevent duplicates
        List<BigDecimal> usedContacts = customerRepo.getCustomerIds(updateId, contactIds, Constants.BUYER_ROLE);
        if(!usedContacts.isEmpty()) {
            String failedContacts = usedContacts.stream().map(BigDecimal::toString).collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Contacts are duplicates: \n" + failedContacts);
        }

        List<CustomerXref> contacts = dto.getContactIds().stream()
            .map(contactId -> {
                CustomerXref xref = createOwnershipUpdateCustomerXref(contactId, Constants.BUYER_ROLE);
                xref.setOwnershipUpdate(ownershipUpdate);
                return xref;
            }).collect(Collectors.toList());
        
        customerRepo.saveAll(contacts);

        return new BuyerForOwnershipUpdateResultDto().contactIds(dto.getContactIds());
    }

    @Transactional
    public WaterRightReferenceToOwnershipUpdateResultDto createWaterRightReferenceToOwnershipUpdate(Long ownerUpdateId, WaterRightReferenceToOwnershipUpdateCreationDto dto) {
        LOGGER.info("Add new Water Right references to an Ownership Update");

        Set<Long> duplicates = Helpers.findDuplicates(dto.getWaterRightIds());
        if(!duplicates.isEmpty()) {
            String duplicateError = duplicates.stream().map(String::valueOf).collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Water Rights were entered twice:\n" + duplicateError);
        }

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent())
            throw new ValidationException(String.format("This ownership update %s does not exist", updateId));

        List<BigDecimal> waterRightIds = dto.getWaterRightIds().stream().map(BigDecimal::valueOf).collect(Collectors.toList());
        List<BigDecimal> availableWaterRights = waterRightRepository.getWaterRightIds(waterRightIds);
        if(availableWaterRights.size() < waterRightIds.size()) {
            throw new ValidationException("One of the Water Rights does not exist");
        }

        //prevent duplicates in the db
        List<BigDecimal> usedWaterRights = waterRightXrefRepo.getWaterRightIds(updateId, waterRightIds);
        if(!usedWaterRights.isEmpty()) {
            String failedContacts = usedWaterRights.stream().map(BigDecimal::toString).collect(Collectors.joining(",\n"));
            throw new ValidationException("The following Water Rights are duplicates: \n" + failedContacts);
        }

        List<WaterRighOwnshiptXref> xrefs = waterRightIds.stream()
            .map(waterRightId -> {
                WaterRighOwnshiptXref xref = new WaterRighOwnshiptXref();
                xref.setWaterRightId(waterRightId);
                xref.setOwnershipUpdateId(updateId);
                return xref;
            }).collect(Collectors.toList());
        
        waterRightXrefRepo.saveAll(xrefs);

        return new WaterRightReferenceToOwnershipUpdateResultDto().waterRightIds(dto.getWaterRightIds());
    }

    public void deleteSellerReferenceToOwnershipUpdate(Long ownerUpdateId, Long sellerId) {

        LOGGER.info("Remove Seller reference from Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This Ownership Update does not exist");
        }

        customerRepo.deleteById(BigDecimal.valueOf(sellerId));
    }

    @Transactional
    public void deleteAllSellersReferenceToOwnershipUpdate(Long ownerUpdateId) {

        LOGGER.info("Remove Sellers references from Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This Ownership Update does not exist");
        }

        Optional<List<CustomerXref>> foundCustomersXref = Optional.ofNullable(customerRepo.findCustomerXrefByOwnershipUpdateIdAndRole(updateId, Constants.SELLER_ROLE));
        if(!foundCustomersXref.isPresent()) {
            throw new ValidationException("This Ownership Update does not have any Seller attached");
        }

        customerRepo.deleteByCouxIdSeqIn(foundCustomersXref.get().stream().map(cust -> cust.getCouxIdSeq()).collect(Collectors.toList()));

    }

    @Transactional
    public void deleteAllAppsReferenceToOwnershipUpdate(Long ownerUpdateId) {

        LOGGER.info("Remove Applications references from Ownership Update");
        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This Ownership Update does not exist");
        }

        Optional<List<ApplicationOwnshipXref>> foundApplicationXref = Optional.ofNullable(applicationXrefRepo.findByOwnershipUpdateId(updateId));
        if(!foundApplicationXref.isPresent()) {
            throw new ValidationException("This Ownership Update does not have any Application attached");
        }

        applicationXrefRepo.deleteByOwnershipUpdateId(updateId);
    }

    public void deleteBuyerReferenceToOwnershipUpdate(Long ownerUpdateId, Long buyerId) {

        LOGGER.info("Remove Buyer reference from Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This ownership update doesn't exist");
        }

        customerRepo.deleteById(BigDecimal.valueOf(buyerId));
    }

    public void deleteWaterRightReferenceToOwnershipUpdate(Long ownerUpdateId, Long waterRightId) {

        LOGGER.info("Remove Water Right reference from Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This ownership update does not exist");
        }

        BigDecimal wtrId = BigDecimal.valueOf(waterRightId);
        Optional<WaterRighOwnshiptXref> foundWaterXref = Optional.ofNullable(waterRightXrefRepo.findWaterRighOwnshiptXrefByOwnershipUpdateIdAndWaterRightId(updateId, wtrId));
        if(!foundWaterXref.isPresent()) {
            throw new ValidationException("This Water Right is not attached to Ownership Update");
        }
        waterRightXrefRepo.deleteByOwnershipUpdateIdAndWaterRightId(updateId, wtrId);

    }

    public PopulateByGeocodesPageDto getWaterRightsByGeocode(Long ownerUpdateId, Integer pageNumber, Integer pageSize, PopulateByGeocodesSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Get Water Rights by Geocode");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<PopulateByGeocodesSearchResultDto> resultsPage =
                ownershipUpdateRepository.getWaterRightsByGeocode(
                        pageable, sortColumn, sortDirection, ownerUpdateId
                );

        PopulateByGeocodesPageDto page = new PopulateByGeocodesPageDto();
        page.setResults(resultsPage.getContent());

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

    @Override
    public PopulateBySellersPageDto getWaterRightsBySellers(Long ownerUpdateId, Integer pageNumber, Integer pageSize, PopulateBySellersSortColumn sortColumn, SortDirection sortDirection) {

        PopulateBySellersPageDto page = new PopulateBySellersPageDto();
        String sortColumnForPage = getCustomerWaterRightSortColumn(sortColumn);
        Sort.Direction direction = (sortDirection == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(BigDecimal.valueOf(ownerUpdateId));
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This ownership update does not exist");
        }
        OwnershipUpdate update = foundUpdate.get();

        Sort sort = Sort.by(direction, sortColumnForPage).and(Sort.by(Sort.Direction.ASC, "waterRightNumber"));
        if (sortColumn.equals(PopulateBySellersSortColumn.COMPLETEWATERRIGHTNUMBER)) {
            Sort primary = Sort.by(direction, getCustomerWaterRightSortColumn(PopulateBySellersSortColumn.BASIN));
            Sort secondary = Sort.by(direction, getCustomerWaterRightSortColumn(PopulateBySellersSortColumn.WATERRIGHTNUMBER));
            Sort tertiary = Sort.by(direction, getCustomerWaterRightSortColumn(PopulateBySellersSortColumn.EXT));
            sort = primary.and(secondary).and(tertiary);
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        List<CustomerXref> sellers = this.customerRepo.findCustomerXrefByOwnershipUpdateIdAndRole(BigDecimal.valueOf(ownerUpdateId), Constants.SELLER_ROLE);

        Page<WaterRight> resultPage = this.ownRepository.getCustomersWaterRights(pageable, sellers.stream().map(s -> s.getCustomerId()).collect(Collectors.toList()), update.getTrnType(), BigDecimal.valueOf(ownerUpdateId));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setResults(resultPage.getContent().stream().map(wr -> getPopulateBySellersPageDto(wr)).collect(Collectors.toList()));
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    public PopulateBySellersSearchResultDto getPopulateBySellersPageDto(WaterRight wr) {

        PopulateBySellersSearchResultDto dto = new PopulateBySellersSearchResultDto();

        dto.setCompleteWaterRightNumber(Helpers.buildCompleteWaterRightNumber(wr.getBasin(), wr.getWaterRightNumber().toString(), wr.getExt()));
        dto.setWaterRightId(wr.getWaterRightId().longValue());
        dto.setBasin(wr.getBasin());
        dto.setExt(wr.getExt());
        dto.setWaterRightNumber(wr.getWaterRightNumber().longValue());
        dto.setTypeDescription(wr.getWaterRightType() != null ? wr.getWaterRightType().getDescription() : null);

        return dto;

    }

    private String getCustomerWaterRightSortColumn(PopulateBySellersSortColumn sortDTOColumn) {
        if(PopulateBySellersSortColumn.WATERRIGHTID == sortDTOColumn)
            return "waterRightId";
        if(PopulateBySellersSortColumn.BASIN == sortDTOColumn)
            return "basin";
        if(PopulateBySellersSortColumn.WATERRIGHTNUMBER == sortDTOColumn)
            return "waterRightNumber";
        if(PopulateBySellersSortColumn.EXT == sortDTOColumn)
            return "ext";
        else if (PopulateBySellersSortColumn.TYPEDESCRIPTION == sortDTOColumn)
            return "description";
        return "waterRightId";
    }


    public ApplicationReferenceToOwnershipUpdateResultDto createApplicationReferenceToOwnershipUpdate(Long ownerUpdateId, ApplicationReferenceToOwnershipUpdateCreationDto dto) {

        LOGGER.info("Add new Application references to Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent())
            throw new ValidationException(String.format("This ownership update %s doesn't exist", updateId));

        List<Long> newReferences = new ArrayList<>();
        for (Long applicationId : dto.getApplicationIds()) {
            BigDecimal appId = BigDecimal.valueOf(applicationId);
            ApplicationOwnshipXref appXref = new ApplicationOwnshipXref();
            appXref.setApplicationId(appId);
            appXref.setOwnershipUpdateId(updateId);
            try { applicationXrefRepo.saveAndFlush(appXref); }
            catch (DataIntegrityViolationException e) {
                if(e.getCause() instanceof ConstraintViolationException &&
                        e.getCause().getCause() instanceof BatchUpdateException) {
                    ConstraintViolationException cn = (ConstraintViolationException) e.getCause();
                    BatchUpdateException sc = (BatchUpdateException) cn.getCause();
                    String constraintMessage = sc.getMessage();
                    if (constraintMessage.contains("WRD.APOX_APPL_FK")) {
                        throw new DataIntegrityViolationException("One of the Applications does not exist");
                    } else {
                        throw e;
                    }
                } else {
                    throw e;
                }
            }

            newReferences.add(applicationId);
        }

        ApplicationReferenceToOwnershipUpdateResultDto out = new ApplicationReferenceToOwnershipUpdateResultDto();
        out.setApplicationIds(newReferences);
        return out;
    }

    public void deleteApplicationReferenceToOwnershipUpdate(Long ownerUpdateId, Long applicationId) {

        LOGGER.info("Remove Application reference from Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> foundUpdate = ownershipUpdateRepository.findById(updateId);
        if(!foundUpdate.isPresent()) {
            throw new ValidationException("This Ownership Update does not exist");
        }

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<ApplicationOwnshipXref> foundApplicationXref = Optional.ofNullable(applicationXrefRepo.findApplicationOwnshipXrefByOwnershipUpdateIdAndApplicationId(updateId, appId));
        if(!foundApplicationXref.isPresent()) {
            throw new ValidationException("This Application is not attached to Ownership Update");
        }
        applicationXrefRepo.deleteByOwnershipUpdateIdAndApplicationId(updateId, appId);

    }

    @Override
    public OwnershipUpdateFeeSummaryDto searchOwnershipFeeSummary(Long ownerUpdateId) {

        LOGGER.info("Get Fee Summary");

        Optional<OwnershipUpdate> foundOwnershipUpdate = ownershipUpdateRepository.findById(BigDecimal.valueOf(ownerUpdateId));
        if(!foundOwnershipUpdate.isPresent())
            throw new NotFoundException(String.format("Ownership Update id %s not found.", ownerUpdateId));

        double amountPaid = payRepo.getFeesPaidForOwnershipUpdate(BigDecimal.valueOf(ownerUpdateId));
        return loadOwnershipUpdateFeeSummaryDto(foundOwnershipUpdate.get(), amountPaid);
    }

    private OwnershipUpdateFeeSummaryDto loadOwnershipUpdateFeeSummaryDto(OwnershipUpdate model, double amountPaid) {

        OwnershipUpdateFeeSummaryDto dto = new OwnershipUpdateFeeSummaryDto();

        if (model.getFeeDue() != null) dto.setFeeDue(model.getFeeDue().doubleValue());
        if (model.getFeeStatus() != null) dto.setFeeStatus(model.getFeeStatus());

        dto.setAmountPaid(amountPaid);
        if (model.getFeeDue() != null) dto.setTotalDue(model.getFeeDue().doubleValue() - amountPaid);

        return dto;
    }

    @Override
    public OwnershipUpdateFeeSummaryDto changeOwnershipUpdateFeeSummary(Long ownerUpdateId, OwnershipUpdateChangeFeeSummaryDto updatedDto) {

        OwnershipUpdate oldOwnershipUpdate = new OwnershipUpdate();
        Optional<OwnershipUpdate> foundOwnershipUpdate = ownershipUpdateRepository.findById(BigDecimal.valueOf(ownerUpdateId));
        if(!foundOwnershipUpdate.isPresent())
            throw new NotFoundException(String.format("Ownership Update id %s not found.", ownerUpdateId));

        oldOwnershipUpdate = foundOwnershipUpdate.get();

        if (!Arrays.asList("DOR 608", "608").contains(oldOwnershipUpdate.getTrnType())) {
            throw new ValidationException("The type has to be DOR OWNERSHIP UPDATE or OWNERSHIP UPDATE");
        }

        if (updatedDto.getFeeDue() != null) {
            oldOwnershipUpdate.setFeeDue(BigDecimal.valueOf(updatedDto.getFeeDue()));
        } else {
            oldOwnershipUpdate.setFeeDue(BigDecimal.ZERO);
        }

        double amountPaid = payRepo.getFeesPaidForOwnershipUpdate(BigDecimal.valueOf(ownerUpdateId));
        oldOwnershipUpdate = updateFeeStatusForOwnershipUpdate(oldOwnershipUpdate, amountPaid).get();
        oldOwnershipUpdate = ownershipUpdateRepository.saveAndFlush(oldOwnershipUpdate);

        return loadOwnershipUpdateFeeSummaryDto(oldOwnershipUpdate, amountPaid);
    }

    private Optional<OwnershipUpdate> updateFeeStatusForOwnershipUpdate(OwnershipUpdate ou, double feesPaid) {
        double totalDue = ou.getFeeDue().doubleValue() - feesPaid;

        if(totalDue <= 0 && !"FULL".equals(ou.getFeeStatus())) {
            ou.setFeeStatus("FULL");
        } else if (totalDue > 0 && feesPaid > 0 && !"PARTIAL".equals(ou.getFeeStatus())) {
            ou.setFeeStatus("PARTIAL");
        } else if (totalDue > 0 && feesPaid == 0) {
            ou.setFeeStatus("NONE");
        }

        return Optional.ofNullable(ou);
    }

    @Override
    public OwnershipUpdateFeeSummaryDto calculateOwnershipUpdateFeeDue(Long ownerUpdateId) {

        LOGGER.info("Calculating Ownership Update Fee Due for " + ownerUpdateId);

        BigDecimal feeDue = BigDecimal.ZERO;
        BigDecimal activeWaterRightsCount = this.ownershipUpdateRepository.getActiveOwnershipUpdateWaterRightsCount(BigDecimal.valueOf(ownerUpdateId));

        if (activeWaterRightsCount.intValue() > 0) {
            feeDue = BigDecimal.valueOf(50 + ((activeWaterRightsCount.intValue() - 1) * 10));
            if (feeDue.intValue() > 300) feeDue = BigDecimal.valueOf(300);
         } 

        OwnershipUpdateChangeFeeSummaryDto updateDto = new OwnershipUpdateChangeFeeSummaryDto();
        updateDto.setFeeDue(feeDue.doubleValue());

        return changeOwnershipUpdateFeeSummary(ownerUpdateId, updateDto);
    }

    @Override
    public OwnershipUpdateFeeLetterDto searchOwnershipFeeLetter(Long ownerUpdateId) {
        LOGGER.info("Get Fee Letter");

        Optional<OwnershipUpdate> foundOwnershipUpdate = ownershipUpdateRepository.findById(BigDecimal.valueOf(ownerUpdateId));
        if(!foundOwnershipUpdate.isPresent())
            throw new NotFoundException(String.format("Ownership Update id %s not found.", ownerUpdateId));

        return loadOwnershipUpdateFeeLetterDto(foundOwnershipUpdate.get());
    }

    @Override
    public OwnershipUpdateFeeLetterDto changeOwnershipUpdateFeeLetter(Long ownerUpdateId, OwnershipUpdateChangeFeeLetterDto updatedDto) {
        OwnershipUpdate oldOwnershipUpdate = new OwnershipUpdate();
        Optional<OwnershipUpdate> foundOwnershipUpdate = ownershipUpdateRepository.findById(BigDecimal.valueOf(ownerUpdateId));
        if(!foundOwnershipUpdate.isPresent())
            throw new NotFoundException(String.format("Ownership Update id %s not found.", ownerUpdateId));

        oldOwnershipUpdate = foundOwnershipUpdate.get();

        if (!Arrays.asList("DOR 608", "608").contains(oldOwnershipUpdate.getTrnType())) {
            throw new ValidationException("The type has to be DOR OWNERSHIP UPDATE or OWNERSHIP UPDATE");
        }

        oldOwnershipUpdate.setFeeDueSentDate(updatedDto.getDateSent());

        oldOwnershipUpdate = ownershipUpdateRepository.saveAndFlush(oldOwnershipUpdate);

        return loadOwnershipUpdateFeeLetterDto(oldOwnershipUpdate);
    }

    private OwnershipUpdateFeeLetterDto loadOwnershipUpdateFeeLetterDto(OwnershipUpdate model) {

        OwnershipUpdateFeeLetterDto dto = new OwnershipUpdateFeeLetterDto();

        if (model.getFeeDueSentDate() != null) dto.setDateSent(model.getFeeDueSentDate());

        return dto;
    }

    public TransferWaterRightsOwnershipResultDto transferWaterRightsOwnership(Long ownerUpdateId) {

        LOGGER.info("Transfer Water Rights for Ownership Update");

        BigDecimal updateId = BigDecimal.valueOf(ownerUpdateId);
        Optional<OwnershipUpdate> model = ownershipUpdateRepository.findById(updateId);
        if (!model.isPresent()) {
            throw new ValidationException("This Ownership Update does not exist");
        }
        OwnershipUpdate updateModel = model.get();
        TransferWaterRightsOwnershipResultDto dto = ownershipUpdateRepository.transferWaterRightsOwnership(updateModel);
        return dto;
    }

    @Override
    public OwnershipUpdatesChangeApplicationsPageDto searchOwnershipUpdatesChangeApplications(Long ownerUpdateId, Integer pageNumber, Integer pageSize, OwnershipUpdatesChangeApplicationsSortColumn sortColumn, SortDirection sortDirection, String applicationId, String basin) {

        LOGGER.info("List Change Applications for Ownership Update Water Rights");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize, getOwnershipUpdatesChangeApplicationsSort(sortColumn, sortDirection));
        Page<Application> resultsPage =
            applicationRepo.listChangeApplicationsForWaterRightsByOwnershipUpdate(pageable, applicationId, basin, new BigDecimal(ownerUpdateId));
        OwnershipUpdatesChangeApplicationsPageDto page = new OwnershipUpdatesChangeApplicationsPageDto();

        page.setResults(resultsPage.getContent().stream().map(record -> {
            return loadChangeApplicationsToDto(record);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultsPage.getTotalElements());
        page.setTotalPages(resultsPage.getTotalPages());

        Map<String, String> filters = new HashMap<String, String>();
        if (applicationId != null)
            filters.put("applicationId", applicationId);
        if (basin != null)
            filters.put("basin", basin);
        page.setFilters(filters);

        return page;

    }

    private OwnershipUpdatesChangeApplicationsSearchResultDto loadChangeApplicationsToDto(Application model) {

        OwnershipUpdatesChangeApplicationsSearchResultDto dto = new OwnershipUpdatesChangeApplicationsSearchResultDto();
        dto.setApplicationId(model.getId().longValue());
        dto.setBasin(model.getBasin());
        return dto;

    }

    private Sort getOwnershipUpdatesChangeApplicationsSort(OwnershipUpdatesChangeApplicationsSortColumn column, SortDirection direction) {

        Sort sortGroup;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;

        switch (column) {
            case BASIN:
                primary = Sort.by(sortOrderDirection, getOwnershipUpdatesChangeApplicationsSortColumn(column));
                secondary = Sort.by(Sort.Direction.ASC, getOwnershipUpdatesChangeApplicationsSortColumn(OwnershipUpdatesChangeApplicationsSortColumn.APPLICATIONID));
                sortGroup = primary.and(secondary);
                break;
            default: /* APPLICATIONID */
                primary = Sort.by(sortOrderDirection, getOwnershipUpdatesChangeApplicationsSortColumn(OwnershipUpdatesChangeApplicationsSortColumn.APPLICATIONID));
                secondary = Sort.by(Sort.Direction.ASC, getOwnershipUpdatesChangeApplicationsSortColumn(OwnershipUpdatesChangeApplicationsSortColumn.BASIN));
                sortGroup = primary.and(secondary);
        }
        return sortGroup;

    }

    private String getOwnershipUpdatesChangeApplicationsSortColumn(OwnershipUpdatesChangeApplicationsSortColumn sortColumn) {

        if (sortColumn == OwnershipUpdatesChangeApplicationsSortColumn.APPLICATIONID)
            return "id";
        else
            return "basin";

    }
}
