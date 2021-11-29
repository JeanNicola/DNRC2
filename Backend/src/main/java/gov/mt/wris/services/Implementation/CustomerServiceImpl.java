package gov.mt.wris.services.Implementation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gov.mt.wris.dtos.ActiveCustomersRequestDto;
import gov.mt.wris.dtos.AddressDto;
import gov.mt.wris.dtos.ApplicationSearchPageDto;
import gov.mt.wris.dtos.ApplicationSearchResultDto;
import gov.mt.wris.dtos.ApplicationSortColumn;
import gov.mt.wris.dtos.BuyerSellerOwnershipUpdatesForContactPageDto;
import gov.mt.wris.dtos.BuyerSellerOwnershipUpdatesForContactSearchResultDto;
import gov.mt.wris.dtos.BuyerSellerOwnershipUpdatesForContactSortColumn;
import gov.mt.wris.dtos.CustomerContactCreationDto;
import gov.mt.wris.dtos.CustomerContactDto;
import gov.mt.wris.dtos.CustomerContactSearchPageDto;
import gov.mt.wris.dtos.CustomerContactSearchResultDto;
import gov.mt.wris.dtos.CustomerContactUpdateDto;
import gov.mt.wris.dtos.CustomerContactsSortColumn;
import gov.mt.wris.dtos.CustomerDto;
import gov.mt.wris.dtos.CustomerOwnershipSortColumn;
import gov.mt.wris.dtos.CustomerOwnershipUpdateDto;
import gov.mt.wris.dtos.CustomerOwnershipUpdatePageDto;
import gov.mt.wris.dtos.CustomerPageDto;
import gov.mt.wris.dtos.CustomerSortColumn;
import gov.mt.wris.dtos.CustomerWaterRightDto;
import gov.mt.wris.dtos.CustomerWaterRightPageDto;
import gov.mt.wris.dtos.CustomerWaterRightSortColumn;
import gov.mt.wris.dtos.NotTheSameSearchPageDto;
import gov.mt.wris.dtos.NotTheSameSearchResultDto;
import gov.mt.wris.dtos.NotTheSameSortColumn;
import gov.mt.wris.dtos.OwnershipUpdateRole;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Address;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.CustomerXref;
import gov.mt.wris.models.OwnershipUpdate;
import gov.mt.wris.models.WaterRight;
import gov.mt.wris.repositories.AddressRepository;
import gov.mt.wris.repositories.CustomerRepository;
import gov.mt.wris.repositories.CustomerXrefRepository;
import gov.mt.wris.repositories.NotTheSamesRepository;
import gov.mt.wris.repositories.ObjectionsRepository;
import gov.mt.wris.repositories.OwnerRepository;
import gov.mt.wris.repositories.OwnershipUpdateRepository;
import gov.mt.wris.services.CustomerService;
import gov.mt.wris.utils.Helpers;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerRepository custRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private NotTheSamesRepository notTheSamesRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ObjectionsRepository objectionsRepository;

    @Autowired
    private OwnershipUpdateRepository ownershipUpdateRepository;

    @Autowired
    private CustomerXrefRepository customerXrefRepository;

    @Override
    public CustomerPageDto searchCustomers(int pagenumber,
                                           int pagesize,
                                           CustomerSortColumn sortDTOColumn,
                                           SortDirection sortDirection,
                                           String contactId,
                                           String lastName,
                                           String firstName,
                                           String firstLastName) {
        LOGGER.info("Getting a Page of Customers");

        Pageable pageable = PageRequest.of(pagenumber -1, pagesize);

        Page<Customer> resultsPage = custRepository.searchCustomers(pageable, sortDTOColumn, sortDirection, contactId, lastName, firstName, firstLastName);

        CustomerPageDto custPage = new CustomerPageDto();

        custPage.setResults(resultsPage.getContent().stream().map(customer -> {
            return getCustomerDto(customer);
        }).collect(Collectors.toList()));

        custPage.setCurrentPage(resultsPage.getNumber() + 1);
        custPage.setPageSize(resultsPage.getSize());

        custPage.setTotalPages(resultsPage.getTotalPages());
        custPage.setTotalElements(resultsPage.getTotalElements());

        custPage.setSortColumn(sortDTOColumn);
        custPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(contactId != null) {
            filters.put("contactId", contactId);
        }
        if(lastName != null) {
            filters.put("lastName", lastName);
        }
        if(firstName != null) {
            filters.put("firstName", firstName);
        }
        if(firstLastName != null) {
            filters.put("name", firstLastName);
        }
        custPage.setFilters(filters);

        return custPage;
    }

    @Override
    public CustomerPageDto searchActiveSellersOwnershipUpdate(int pagenumber,
                                                        int pagesize,
                                                        CustomerSortColumn sortDTOColumn,
                                                        SortDirection sortDirection,
                                                        Long ownerUpdateId,
                                                        String contactId,
                                                        String lastName,
                                                        String firstName) {

        LOGGER.info("Getting a Page of Available Active Sellers for Ownership Update");

        Pageable pageable = PageRequest.of(pagenumber -1, pagesize);

        List<Long> waterRightIds = new ArrayList<Long>();
        Optional<OwnershipUpdate> ownershipUpdate = ownershipUpdateRepository.findById(BigDecimal.valueOf(ownerUpdateId));
        if (ownershipUpdate.isPresent()) {
            Set<WaterRight> waterRights =  ownershipUpdate.get().getWaterRights();
            waterRightIds = waterRights.stream().map(w -> w.getWaterRightId().longValue()).collect(Collectors.toList());
        }

        Page<Customer> resultsPage = custRepository.searchActiveSellersOwnershipUpdate(pageable, sortDTOColumn, sortDirection, waterRightIds, ownerUpdateId, contactId, lastName, firstName);

        CustomerPageDto custPage = new CustomerPageDto();

        custPage.setResults(resultsPage.getContent().stream().map(customer -> {
            return getCustomerDto(customer);
        }).collect(Collectors.toList()));

        custPage.setCurrentPage(resultsPage.getNumber() + 1);
        custPage.setPageSize(resultsPage.getSize());

        custPage.setTotalPages(resultsPage.getTotalPages());
        custPage.setTotalElements(resultsPage.getTotalElements());

        custPage.setSortColumn(sortDTOColumn);
        custPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(contactId != null) {
            filters.put("contactId", contactId);
        }
        if(lastName != null) {
            filters.put("lastName", lastName);
        }
        if(firstName != null) {
            filters.put("firstName", firstName);
        }
        custPage.setFilters(filters);

        return custPage;
    }

    @Override
    public CustomerPageDto searchCustomersByWaterRights(int pagenumber,
                                        int pagesize,
                                        CustomerSortColumn sortDTOColumn,
                                        SortDirection sortDirection,
                                        ActiveCustomersRequestDto activeCustomersRequestDto,
                                        String contactId,
                                        String lastName,
                                        String firstName) {
        LOGGER.info("Getting a Page of Active Owners");

        Pageable pageable = PageRequest.of(pagenumber -1, pagesize);

        List<Long> waterRightIds = new ArrayList<Long>();

        if (activeCustomersRequestDto.getWaterRights() != null) {
            waterRightIds = activeCustomersRequestDto.getWaterRights();
        } else if (activeCustomersRequestDto.getOwnershipUpdateId() != null) {
            Optional<OwnershipUpdate> ownershipUpdate = ownershipUpdateRepository.findById(BigDecimal.valueOf(activeCustomersRequestDto.getOwnershipUpdateId()));

            if (ownershipUpdate.isPresent()) {
                Set<WaterRight> waterRights =  ownershipUpdate.get().getWaterRights();
                waterRightIds = waterRights.stream().map(w -> w.getWaterRightId().longValue()).collect(Collectors.toList());
            }

        }

        Page<Customer> resultsPage = custRepository.searchCustomersByWaterRights(pageable, sortDTOColumn, sortDirection, waterRightIds, contactId, lastName, firstName);

        CustomerPageDto custPage = new CustomerPageDto();

        custPage.setResults(resultsPage.getContent().stream().map(customer -> {
            return getCustomerDto(customer);
        }).collect(Collectors.toList()));

        custPage.setCurrentPage(resultsPage.getNumber() + 1);
        custPage.setPageSize(resultsPage.getSize());

        custPage.setTotalPages(resultsPage.getTotalPages());
        custPage.setTotalElements(resultsPage.getTotalElements());

        custPage.setSortColumn(sortDTOColumn);
        custPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(contactId != null) {
            filters.put("contactId", contactId);
        }
        if(lastName != null) {
            filters.put("lastName", lastName);
        }
        if(firstName != null) {
            filters.put("firstName", firstName);
        }
        custPage.setFilters(filters);

        return custPage;
    }

    @Override
    public CustomerContactSearchPageDto searchCustomerContacts(int pagenumber,
                                                               int pagesize,
                                                               CustomerContactsSortColumn sortColumn,
                                                               SortDirection sortDirection,
                                                               String contactId,
                                                               String lastName,
                                                               String firstName,
                                                               String middleInitial,
                                                               String suffix,
                                                               String contactType,
                                                               String contactStatus) {

        LOGGER.info("Getting a Page of Customers");
        Pageable pageable = PageRequest.of(pagenumber -1, pagesize);

        Page<CustomerContactSearchResultDto> resultsPage = custRepository.searchCustomerContacts(
                pageable, sortColumn, sortDirection, contactId, lastName, firstName, middleInitial, suffix, contactType, contactStatus);
        CustomerContactSearchPageDto ccPage = new CustomerContactSearchPageDto();
        ccPage.setResults(resultsPage.getContent());

        ccPage.setCurrentPage(resultsPage.getNumber() + 1);
        ccPage.setPageSize(resultsPage.getSize());

        ccPage.setTotalPages(resultsPage.getTotalPages());
        ccPage.setTotalElements(resultsPage.getTotalElements());

        ccPage.setSortColumn(sortColumn);
        ccPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        if(contactId != null) {
            filters.put("contactId", contactId);
        }
        if(lastName != null) {
            filters.put("lastName", lastName);
        }
        if(firstName != null) {
            filters.put("firstName", firstName);
        }
        if(middleInitial != null) {
            filters.put("middleInitial", middleInitial);
        }
        if(suffix != null) {
            filters.put("suffix", suffix);
        }
        if(contactType != null) {
            filters.put("contactType", contactType);
        }
        if(contactStatus != null) {
            filters.put("contactStatus", contactStatus);
        }

        ccPage.setFilters(filters);
        return ccPage;

    }

    @Override
    public CustomerWaterRightPageDto getCustomerWaterRights(int pagenumber, int pagesize, CustomerWaterRightSortColumn sortColumn, SortDirection sortDirection, Long contactId) {

        Pageable pageable = PageRequest.of(pagenumber -1, pagesize, getCustomerWaterRightsSort(sortColumn, sortDirection));
        CustomerWaterRightPageDto page = new CustomerWaterRightPageDto();

        Page<WaterRight> resultPage = ownerRepository.getCustomerWaterRights(pageable, BigDecimal.valueOf(contactId));
        page.setResults(resultPage.getContent().stream().map(model -> {
            return getWaterRightDto(model);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortDirection(sortDirection);
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    private CustomerWaterRightDto getWaterRightDto(WaterRight model) {
        CustomerWaterRightDto newDto = new CustomerWaterRightDto();

        BigDecimal objectionsCount = objectionsRepository.countByWaterRightId(model.getWaterRightId());

        if (objectionsCount.longValue() > 0L) {
            newDto.setObjection("YES");
        } else {
            newDto.setObjection("NO");
        }

        newDto.setBasin(model.getBasin());
        newDto.setExt(model.getExt());
        newDto.setStatus(model.getWaterRightStatus() != null ? model.getWaterRightStatus().getDescription() : null);
        newDto.setTypeDescription(model.getWaterRightType() != null ? model.getWaterRightType().getDescription() : null);
        newDto.setWaterRightNumber(model.getWaterRightNumber() != null ? model.getWaterRightNumber().longValue() : null);
        newDto.setWaterRightId(model.getWaterRightId() != null ? model.getWaterRightId().longValue() : null);
        newDto.setConDistNo(model.getConDistNo() != null ? model.getConDistNo() : null);
        return newDto;
    }

    private Sort getCustomerWaterRightsSort(CustomerWaterRightSortColumn column, SortDirection direction) {

        Sort sortGroup;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;

        switch (column) {
            case BASIN:
            case TYPEDESCRIPTION:
            case EXT:
            case STATUS:
            case CONDISTNO:
                primary = Sort.by(sortOrderDirection, getWaterRightSortColumn(column));
                secondary = Sort.by(Sort.Direction.ASC, getWaterRightSortColumn(CustomerWaterRightSortColumn.WATERRIGHTNUMBER));
                sortGroup = primary.and(secondary);
                break;
            default:
                // WATERRIGHTNUMBER
                primary = Sort.by(sortOrderDirection, getWaterRightSortColumn(CustomerWaterRightSortColumn.WATERRIGHTNUMBER));
                sortGroup = primary;
        }

        return sortGroup;

    }

    private String getWaterRightSortColumn(CustomerWaterRightSortColumn sortColumn) {
        if (sortColumn == CustomerWaterRightSortColumn.BASIN)
            return "waterRight.basin";
        else if (sortColumn == CustomerWaterRightSortColumn.EXT)
            return "waterRight.ext";
        else if (sortColumn == CustomerWaterRightSortColumn.STATUS)
            return "waterRight.waterRightStatus.description";
        else if (sortColumn == CustomerWaterRightSortColumn.TYPEDESCRIPTION)
            return "waterRight.waterRightType.description";
        else if (sortColumn == CustomerWaterRightSortColumn.WATERRIGHTNUMBER)
            return "waterRight.waterRightNumber";
        else if (sortColumn == CustomerWaterRightSortColumn.CONDISTNO) {
            return "waterRight.conDistNo";
        } else {
            return "waterRight.waterRightNumber";
        }
    }

    @Override
    public ApplicationSearchPageDto getCustomerApplications(int pagenumber, int pagesize, ApplicationSortColumn sortColumn, SortDirection sortDirection, Long contactId) {

        Pageable pageable = PageRequest.of(pagenumber -1, pagesize, getCustomerApplicationsSort(sortColumn, sortDirection));
        ApplicationSearchPageDto page = new ApplicationSearchPageDto();

        Page<Application> resultPage = ownerRepository.getCustomerApplications(pageable, BigDecimal.valueOf(contactId));
        page.setResults(resultPage.getContent().stream().map(model -> {
            return getApplicationDto(model);
        }).collect(Collectors.toList()));

        page.setCurrentPage(resultPage.getNumber() + 1);
        page.setPageSize(resultPage.getSize());
        page.setSortColumn(sortColumn);
        page.setTotalElements(resultPage.getTotalElements());
        page.setTotalPages(resultPage.getTotalPages());

        return page;
    }

    private ApplicationSearchResultDto getApplicationDto(Application model) {
        ApplicationSearchResultDto newDto = new ApplicationSearchResultDto();

        BigDecimal objectionsCount = objectionsRepository.countByApplicationId(model.getId());

        if (objectionsCount.longValue() > 0L) {
            newDto.setObjection("YES");
        } else {
            newDto.setObjection("NO");
        }

        newDto.setApplicationId(model.getId().longValue());
        newDto.setBasin(model.getBasin());
        newDto.setApplicationTypeCode(model.getTypeCode());
        newDto.setApplicationTypeDescription(model.getType().getDescription());
        newDto.setDateTimeReceived(model.getDateTimeReceivedEvent().getEventDate());

        return newDto;
    }

    private Sort getCustomerApplicationsSort(ApplicationSortColumn column, SortDirection direction) {

        Sort sortGroup;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;

        switch (column) {
            case BASIN:
            case APPLICATIONTYPECODE:
            case APPLICATIONTYPEDESCRIPTION:
            case DATETIMERECEIVED:
                primary = Sort.by(sortOrderDirection, getApplicationSortColumn(column));
                secondary = Sort.by(Sort.Direction.ASC, getApplicationSortColumn(ApplicationSortColumn.APPLICATIONID));
                sortGroup = primary.and(secondary);
                break;
            default:
                // APPLICATIONID
                primary = Sort.by(sortOrderDirection, getApplicationSortColumn(ApplicationSortColumn.APPLICATIONID));
                sortGroup = primary;
        }

        return sortGroup;

    }

    private String getApplicationSortColumn(ApplicationSortColumn sortColumn) {

        if (sortColumn == ApplicationSortColumn.APPLICATIONID) {
            return "application.id";
        } else if (sortColumn == ApplicationSortColumn.APPLICATIONTYPECODE) {
            return "application.typeCode";
        } else if (sortColumn == ApplicationSortColumn.APPLICATIONTYPEDESCRIPTION) {
            return "application.typeCode";
        } else if (sortColumn == ApplicationSortColumn.DATETIMERECEIVED) {
            return "application.dateTimeReceivedEvent.eventDate";
        } else if (sortColumn == ApplicationSortColumn.BASIN) {
            return "application.basin";
        } else {
            return "application.id";
        }

    }

    @Override
    public CustomerContactDto getCustomerContact(Long contactId) {

        Optional<Customer> customer =  custRepository.getCustomersByCustomerId(BigDecimal.valueOf(contactId));
        if(!customer.isPresent()) {
            throw new NotFoundException("Contact with id " + contactId + " does not exist");
        }
        return getCustomerContactDto(customer);

    }

    @Transactional
    @Override
    public CustomerContactDto createCustomerContact(CustomerContactCreationDto newContact) {

        LOGGER.info("Create new Customer Contact");

        Customer customer = new Customer();
        customer.setContactType(newContact.getContactType());
        customer.setContactStatus(newContact.getContactStatus());
        customer.setLastName(newContact.getLastName());
        customer.setFirstName(newContact.getFirstName());
        customer.setMiddleInitial(newContact.getMiddleInitial());
        customer.setSuffix(newContact.getSuffix());
        customer.setAddresses(transformAddressDtoToAddress(newContact));

        Customer savedCustomer = custRepository.saveAndFlush(customer);
        Optional<Customer> foundCustomer =  custRepository.getCustomersByCustomerId(savedCustomer.getCustomerId());
        if(!foundCustomer.isPresent()) {
            throw new DataIntegrityViolationException("Contact with id " + savedCustomer.getCustomerId() + " after save was not found.");
        } else {
            /* save the addresses now that we have new cust_id_seq value for customer (contact) record */
            addressRepository
                    .saveAll(
                            initializeCustomerIdForAddresses(
                                    BigInteger.valueOf(savedCustomer.getCustomerId().longValue()),
                                    customer.getAddresses()
                            )
                    );
        }

        return getCustomerContactDto(foundCustomer);

    }

    @Transactional
    @Override
    public CustomerContactDto changeCustomerContact(Long contactId, CustomerContactUpdateDto updateContact) {

        LOGGER.info("Update existing Customer Contact");

        Customer oldCustomer = null;
        Optional<Customer> foundCustomer =  custRepository.getCustomersByCustomerId(BigDecimal.valueOf(contactId));
        if(!foundCustomer.isPresent()) {
            throw new NotFoundException(String.format("Customer contact id %s not found.", contactId));
        }
        oldCustomer = foundCustomer.get();

        oldCustomer.setContactType(updateContact.getContactType());
        oldCustomer.setContactStatus(updateContact.getContactStatus());
        oldCustomer.setLastName(updateContact.getLastName());
        oldCustomer.setFirstName(updateContact.getFirstName());
        oldCustomer.setMiddleInitial(updateContact.getMiddleInitial());
        oldCustomer.setSuffix(updateContact.getSuffix());
        List<Address> updatedAddresses = transformAddressDtoToAddress(updateContact);
        oldCustomer = custRepository.saveAndFlush(oldCustomer);
        if (updatedAddresses.size() > 0) {
            addressRepository.saveAll(updatedAddresses);
        }
        return getCustomerContactDto(oldCustomer);

    }

    @Override
    public NotTheSameSearchPageDto searchNotTheSame(int pagenumber,
                                                    int pagesize,
                                                    NotTheSameSortColumn sortColumn,
                                                    SortDirection sortDirection,
                                                    String contactId) {

        LOGGER.info("Searching for Not The Same");

        Pageable request = PageRequest.of(pagenumber-1, pagesize);
        Page<NotTheSameSearchResultDto> resultsPage = notTheSamesRepository.searchNotTheSamesByCustomerId(request, sortColumn, sortDirection, new BigDecimal(contactId.strip()));
        NotTheSameSearchPageDto ntsPage = new NotTheSameSearchPageDto();
        List<NotTheSameSearchResultDto> ntsList = resultsPage.getContent();
        ntsPage.setResults(ntsList);
        ntsPage.setCurrentPage(resultsPage.getNumber() + 1);
        ntsPage.setPageSize(resultsPage.getSize());
        ntsPage.setTotalPages(resultsPage.getTotalPages());
        ntsPage.setTotalElements(resultsPage.getTotalElements());
        ntsPage.setSortColumn(sortColumn);
        ntsPage.setSortDirection(sortDirection);

        Map<String, String> filters = new HashMap<String, String>();
        filters.put("contactId", contactId);
        ntsPage.setFilters(filters);

        return ntsPage;
    }

    @Override
    public CustomerOwnershipUpdatePageDto getCustomerContactOwnershipUpdates(int pagenumber, int pagesize, CustomerOwnershipSortColumn sortColumn, SortDirection sortDirection, Long contactId, OwnershipUpdateRole ownershipUpdateRole) {

        LOGGER.info("Get Customer Contact Ownership Updates");

        Pageable pageable = PageRequest.of(pagenumber -1, pagesize, getCustomerContactOwnershipUpdatesSort(sortColumn, sortDirection));
        CustomerOwnershipUpdatePageDto page = new CustomerOwnershipUpdatePageDto();

        Map<String, String> filters = new HashMap<String, String>();

        // If a role was passed in, use it in the query; otherwise get all records
        if (ownershipUpdateRole != null) {
            Page<CustomerXref> resultPage = custRepository.getCustomerOwnershipUpdatesByRole(pageable, BigDecimal.valueOf(contactId), ownershipUpdateRole.getValue());
                filters.put("ownershipUpdateRole", ownershipUpdateRole.toString());

            page.setResults(resultPage.getContent().stream().map(model -> {
                    return getCustomerOwnershipUpdateDto(model);
                }).collect(Collectors.toList()));
            page.setCurrentPage(resultPage.getNumber() + 1);
            page.setPageSize(resultPage.getSize());
            page.setSortColumn(sortColumn);
            page.setTotalElements(resultPage.getTotalElements());
            page.setTotalPages(resultPage.getTotalPages());
            page.setFilters(filters);

        } else {
            Page<OwnershipUpdate> resultPage = ownershipUpdateRepository.getCustomerOwnershipUpdates(pageable, BigDecimal.valueOf(contactId));
            page.setResults(resultPage.getContent().stream().map(model -> {
                return getCustomerOwnershipUpdateDto(model);
            }).collect(Collectors.toList()));
            page.setCurrentPage(resultPage.getNumber() + 1);
            page.setPageSize(resultPage.getSize());
            page.setSortColumn(sortColumn);
            page.setTotalElements(resultPage.getTotalElements());
            page.setTotalPages(resultPage.getTotalPages());
            page.setFilters(filters);
        }

        return page;
    }
    private Sort getCustomerContactOwnershipUpdatesSort(CustomerOwnershipSortColumn column, SortDirection direction) {

        Sort sortGroup;
        Sort.Direction sortOrderDirection = direction.getValue().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort primary, secondary;

        switch (column) {
            case UPDATETYPE:
            case DATERECEIVED:
            case DATEPROCESSED:
            case DATETERMINATED:
                primary = Sort.by(sortOrderDirection, getOwnershipUpdateSortColumn(column));
                secondary = Sort.by(Sort.Direction.ASC, getOwnershipUpdateSortColumn(CustomerOwnershipSortColumn.OWNERUPDATEID));
                sortGroup = primary.and(secondary);
                break;
            default:
                // OWNERUPDATEID
                primary = Sort.by(sortOrderDirection, getOwnershipUpdateSortColumn(CustomerOwnershipSortColumn.OWNERUPDATEID));
                sortGroup = primary;
        }

        return sortGroup;

    }

    private String getOwnershipUpdateSortColumn(CustomerOwnershipSortColumn sortColumn) {
        switch (sortColumn) {
            case OWNERUPDATEID:
                return "ownerUpdateId";
            case UPDATETYPE:
                return "updateTypeValue.meaning";
            case DATERECEIVED:
                return "dateReceived";
            case DATEPROCESSED:
                return "dateProcessed";
            case DATETERMINATED:
                return "dateTerminated";
            default:
                return "ownerUpdateId";
        }
    }

    private CustomerOwnershipUpdateDto getCustomerOwnershipUpdateDto(CustomerXref model) {

        CustomerOwnershipUpdateDto newDto = new CustomerOwnershipUpdateDto();
        OwnershipUpdate update = model.getOwnershipUpdate();

        if (model.getConttForDeed() != null) {
            newDto.setContractForDeed(model.getConttForDeedValue().getMeaning());
        } else {
            newDto.setContractForDeed("NO");
        }

        if (update.getUpdateTypeValue() != null) newDto.setUpdateType(update.getUpdateTypeValue().getMeaning());
        if (update.getOwnerUpdateId() != null) newDto.setOwnerUpdateId(update.getOwnerUpdateId().longValue());
        newDto.setDateReceived(update.getDateReceived());
        newDto.setDateProcessed(update.getDateProcessed());
        newDto.setDateTerminated(update.getDateTerminated());

        return newDto;
    }

    private CustomerOwnershipUpdateDto getCustomerOwnershipUpdateDto(OwnershipUpdate model) {

        CustomerOwnershipUpdateDto newDto = new CustomerOwnershipUpdateDto();

        if (model.getUpdateTypeValue() != null) newDto.setUpdateType(model.getUpdateTypeValue().getMeaning());
        if (model.getOwnerUpdateId() != null) newDto.setOwnerUpdateId(model.getOwnerUpdateId().longValue());
        newDto.setDateReceived(model.getDateReceived());
        newDto.setDateProcessed(model.getDateProcessed());
        newDto.setDateTerminated(model.getDateTerminated());
        newDto.setContractForDeed(customerXrefRepository.getFirstConttForDeedByOwnerUpdateId(model.getOwnerUpdateId()));

        return newDto;
    }

    private CustomerDto getCustomerDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setContactId(customer.getCustomerId().longValue());
        String name = Helpers.buildName(customer.getLastName(),
                customer.getFirstName(),
                customer.getMiddleInitial(),
                customer.getSuffix());
        dto.setName(name);
        String firstLastName = Helpers.buildFirstLastName(customer.getFirstName(),
                customer.getMiddleInitial(),
                customer.getLastName(),
                customer.getSuffix());
        dto.setFirstLastName(firstLastName);
        dto.setContactTypeDescription(customer.getContactTypeValue().getDescription());
        return dto;
    }

    private CustomerContactDto getCustomerContactDto(Optional<Customer> customer) {
        return getCustomerContactDto(customer.get());
    }

    private CustomerContactDto getCustomerContactDto(Customer customer) {

        CustomerContactDto dto = new CustomerContactDto();
        dto.setContactId(customer.getCustomerId().longValue());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setMiddleInitial(customer.getMiddleInitial());
        if (customer.getContactTypeValue()!=null)
            dto.setContactTypeValue(customer.getContactTypeValue().getDescription());
        dto.setContactType(customer.getContactType());
        if (customer.getSuffixValue()!=null)
            dto.setSuffixValue(customer.getSuffixValue().getMeaning());
        dto.setSuffix(customer.getSuffix());
        if (customer.getContactStatusValue()!=null)
            dto.setContactStatusValue(customer.getContactStatusValue().getMeaning());
        dto.setContactStatus(customer.getContactStatus());
        dto.setAddresses(getAddressDtos(customer));
        /* Not all customers have addresses */
        if(dto.getAddresses().size()>0) {
            dto.setAddress(dto.getAddresses().get(0).getAddressLine1());
        }
        dto.setName(Helpers.buildName(
                customer.getLastName(),
                customer.getFirstName(),
                customer.getMiddleInitial(),
                customer.getSuffix())
        );
        return dto;

    }

    private List<AddressDto> getAddressDtos(Customer customer) {

        List<AddressDto> dtos = new ArrayList<>();
        customer.getAddresses().forEach((a)-> {
            if(a.getAddressId()!=null) {
                AddressDto d = new AddressDto();
                d.setAddressId(Long.valueOf(a.getAddressId().toString()));
                d.setAddressLine1(a.getAddressLine1());
                d.setAddressLine2(a.getAddressLine2());
                d.setAddressLine3(a.getAddressLine3());
                if (a.getCustomerId() != null) d.setCustomerId(Long.valueOf(a.getCustomerId().toString()));
                d.setCreatedBy(a.getCreatedBy());
                if (a.getCreatedByName()!=null)
                    d.setCreatedByValue(a.getCreatedByName().getFirstName() + " " + a.getCreatedByName().getLastName());
                d.setCreatedDate(a.getDateCreated());
                d.setModifiedBy(a.getModifiedBy());
                if (a.getModifiedByName()!=null)
                    d.setModifiedByValue(Helpers.buildName(a.getModifiedByName().getLastName(), a.getModifiedByName().getFirstName()));
                d.setModifiedDate(a.getDateModified());
                d.setModReason(a.getModifiedReason());
                d.setPl4(a.getPlFour());
                if (a.getUnresolvedFlagValue()!=null)
                    d.setUnresolvedFlagValue(a.getUnresolvedFlagValue().getMeaning());
                d.setUnresolvedFlag(a.getUnresolvedFlag());
                if (a.getPrimaryMailValue()!=null)
                    d.setPrimaryMailValue(a.getPrimaryMailValue().getMeaning());
                d.setPrimaryMail(a.getPrimaryMail());
                if (a.getForeignAddressValue()!=null)
                    d.setForeignAddressValue(a.getForeignAddressValue().getMeaning());
                d.setForeignAddress(a.getForeignAddress());
                d.setForeignPostal(a.getForeignPostal());
                /* foreign address won't have zip id */
                if (a.getForeignAddress()==null || a.getForeignAddress().equals("N")) {
                    if (a.getZipCode()!=null) {
                        d.setZipCodeId(a.getZipCode().getZipCodeId().longValueExact());
                        d.setCityId(a.getZipCode().getCityIdSeq().longValueExact());
                        d.setZipCode(a.getZipCode().getZipCode());
                        d.setCityName(a.getZipCode().getCity().getCityName());
                        d.setStateCode(a.getZipCode().getCity().getState().getCode());
                        d.setStateName(a.getZipCode().getCity().getState().getName());
                    }
                }
                dtos.add(d);
            }

        });
        return dtos;

    }

    private List<Address> transformAddressDtoToAddress(CustomerContactCreationDto customer) {

        List<Address> addresses = new ArrayList<>();
        customer.getAddresses().forEach((a) -> {
            Address d = new Address();

            if (a.getCustomerId() != null) d.setCustomerId(BigInteger.valueOf(a.getCustomerId()));

            d.setAddressLine1(a.getAddressLine1());
            d.setAddressLine2(a.getAddressLine2());
            d.setAddressLine3(a.getAddressLine3());
            d.setForeignAddress(a.getForeignAddress());
            d.setForeignPostal(a.getForeignPostal());
            d.setPrimaryMail(a.getPrimaryMail());
            d.setModifiedReason(a.getModReason());
            d.setUnresolvedFlag(a.getUnresolvedFlag());
            d.setPlFour(a.getPl4());
            if (a.getZipCodeId() != null) d.setZipCodeId(BigInteger.valueOf(a.getZipCodeId()));

            addresses.add(d);
        });

        return addresses;

    }

    private List<Address> transformAddressDtoToAddress(CustomerContactUpdateDto customer) {

        List<Address> addresses = new ArrayList<>();
        if (customer.getAddresses() != null) {
            customer.getAddresses().forEach((a) -> {
                Address d = new Address();
                d.setCustomerId(BigInteger.valueOf(a.getCustomerId()));
                d.setAddressId(BigInteger.valueOf(a.getAddressId()));
                d.setAddressLine1(a.getAddressLine1());
                d.setAddressLine2(a.getAddressLine2());
                d.setAddressLine3(a.getAddressLine3());
                d.setForeignAddress(a.getForeignAddress());
                d.setForeignPostal(a.getForeignPostal());
                d.setPrimaryMail(a.getPrimaryMail());
                d.setModifiedReason(a.getModReason());
                d.setUnresolvedFlag(a.getUnresolvedFlag());
                d.setPlFour(a.getPl4());
                if (a.getZipCodeId() != null) d.setZipCodeId(BigInteger.valueOf(a.getZipCodeId()));

                addresses.add(d);
            });
        }

        return addresses;

    }

    private List<Address> initializeCustomerIdForAddresses(BigInteger customerId, List<Address> in) {
        List<Address> out = new ArrayList<>();
        in.forEach((a)-> {
            Address address = new Address();
            address = a;
            address.setCustomerId(customerId);
            out.add(address);
        });
        return out;
    }

    @Override
    public BuyerSellerOwnershipUpdatesForContactPageDto getBuyerSellerOwnershipUpdatesForContact(Long contactId, int pageNumber, int pageSize, BuyerSellerOwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection, OwnershipUpdateRole ownershipUpdateRole) {

        LOGGER.info("Get All Buyer or Seller Ownership Updates for a Contact");

        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<BuyerSellerOwnershipUpdatesForContactSearchResultDto> resultsPage =
                custRepository.getBuyerSellerOwnershipUpdatesForContact(
                        pageable, sortColumn, sortDirection, contactId, ownershipUpdateRole
                );
        BuyerSellerOwnershipUpdatesForContactPageDto page = new BuyerSellerOwnershipUpdatesForContactPageDto();
        page.setResults(resultsPage.getContent());
        page.setOwnershipUpdateRole(ownershipUpdateRole);

        page.setCurrentPage(resultsPage.getNumber() + 1);
        page.setPageSize(resultsPage.getSize());

        page.setTotalPages(resultsPage.getTotalPages());
        page.setTotalElements(resultsPage.getTotalElements());

        page.setSortColumn(sortColumn);
        page.setSortDirection(sortDirection);

        return page;
    }

}
