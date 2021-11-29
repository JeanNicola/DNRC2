package gov.mt.wris.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.CustomersApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.ActiveCustomersRequestDto;
import gov.mt.wris.dtos.AddressCreationDto;
import gov.mt.wris.dtos.AddressDto;
import gov.mt.wris.dtos.AddressSearchPageDto;
import gov.mt.wris.dtos.AddressSortColumn;
import gov.mt.wris.dtos.AddressUpdateDto;
import gov.mt.wris.dtos.ApplicationSearchPageDto;
import gov.mt.wris.dtos.ApplicationSortColumn;
import gov.mt.wris.dtos.BuyerSellerOwnershipUpdatesForContactPageDto;
import gov.mt.wris.dtos.BuyerSellerOwnershipUpdatesForContactSortColumn;
import gov.mt.wris.dtos.CustomerContactCreationDto;
import gov.mt.wris.dtos.CustomerContactDto;
import gov.mt.wris.dtos.CustomerContactSearchPageDto;
import gov.mt.wris.dtos.CustomerContactUpdateDto;
import gov.mt.wris.dtos.CustomerContactsSortColumn;
import gov.mt.wris.dtos.CustomerOwnershipSortColumn;
import gov.mt.wris.dtos.CustomerOwnershipUpdatePageDto;
import gov.mt.wris.dtos.CustomerPageDto;
import gov.mt.wris.dtos.CustomerSortColumn;
import gov.mt.wris.dtos.CustomerWaterRightPageDto;
import gov.mt.wris.dtos.CustomerWaterRightSortColumn;
import gov.mt.wris.dtos.ElectronicContactsDto;
import gov.mt.wris.dtos.ElectronicContactsSearchPageDto;
import gov.mt.wris.dtos.ElectronicContactsSortColumn;
import gov.mt.wris.dtos.ElectronicContactsUpdateDto;
import gov.mt.wris.dtos.NotTheSameSearchPageDto;
import gov.mt.wris.dtos.NotTheSameSortColumn;
import gov.mt.wris.dtos.OwnershipUpdateRole;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.services.AddressService;
import gov.mt.wris.services.CustomerService;
import gov.mt.wris.services.ElectronicContactsService;

@Controller
public class CustomersController implements CustomersApiDelegate {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomersController.class);

    @Autowired
    private CustomerService custService;

    @Autowired
    private ElectronicContactsService electronicContactsService;

    @Autowired
    private AddressService addressService;

    @Override
    @PermissionsNeeded(
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE)
    )
    public ResponseEntity<CustomerPageDto> searchCustomers(Integer pageNumber,
                                                           Integer pageSize,
                                                           CustomerSortColumn sortColumn,
                                                           SortDirection sortDirection,
                                                           String contactId,
                                                           String lastName,
                                                           String firstName,
                                                           String firstLastName) {
        LOGGER.info("Searching for Customers");
        CustomerPageDto custPage = custService.searchCustomers(pageNumber, pageSize, sortColumn, sortDirection, contactId, lastName, firstName, firstLastName);

        return ResponseEntity.ok(custPage);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE)
    })
    public ResponseEntity<CustomerPageDto> searchActiveCustomersByWaterRights(ActiveCustomersRequestDto activeCustomersRequestDto,
                                                        Integer pageNumber,
                                                        Integer pageSize,
                                                        CustomerSortColumn sortColumn,
                                                        SortDirection sortDirection,
                                                        String contactId,
                                                        String lastName,
                                                        String firstName) {
        LOGGER.info("Searching for Active Owners");
        CustomerPageDto custPage = custService.searchCustomersByWaterRights(pageNumber, pageSize, sortColumn, sortDirection, activeCustomersRequestDto, contactId, lastName, firstName);

        return ResponseEntity.ok(custPage);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<CustomerContactSearchPageDto> searchCustomerContacts(Integer pageNumber,
                                                                               Integer pageSize,
                                                                               CustomerContactsSortColumn sortColumn,
                                                                               SortDirection sortDirection,
                                                                               String contactId,
                                                                               String lastName,
                                                                               String firstName,
                                                                               String middleInitial,
                                                                               String suffix,
                                                                               String contactType,
                                                                               String contactStatus) {

        LOGGER.info("Search for Customer Contacts");
        CustomerContactSearchPageDto contactsPage = custService.searchCustomerContacts(pageNumber, pageSize, sortColumn, sortDirection,
                contactId, lastName, firstName, middleInitial, suffix, contactType, contactStatus);
        return ResponseEntity.ok(contactsPage);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<AddressSearchPageDto> searchAddresses(Long customerId,
                                                                Integer pageNumber,
                                                                Integer pageSize,
                                                                AddressSortColumn sortColumn,
                                                                SortDirection sortDirection) {

        LOGGER.info("Search Addresses");
        AddressSearchPageDto page = addressService.searchAddresses(pageNumber, pageSize, sortColumn, sortDirection, customerId);
        return ResponseEntity.ok(page);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<CustomerContactDto> getCustomerContact(Long contactId) {

        LOGGER.info("Get specific Customer Contact");
        CustomerContactDto contact = custService.getCustomerContact(contactId);
        return ResponseEntity.ok(contact);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<CustomerContactDto> createCustomerContact(CustomerContactCreationDto newContact) {

        LOGGER.info("Creating a new Customer Contact");
        CustomerContactDto contact = custService.createCustomerContact(newContact);
        return new ResponseEntity<CustomerContactDto>(contact, null, HttpStatus.CREATED);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<CustomerContactDto> changeCustomerContact(Long contactId, CustomerContactUpdateDto contact) {

        LOGGER.info("Update existing Customer Contact");
        CustomerContactDto updatedContact = custService.changeCustomerContact(contactId, contact);
        return ResponseEntity.ok(updatedContact);

    }

    @Override
    public ResponseEntity<CustomerWaterRightPageDto> getCustomerContactWaterRights(Long contactId, Integer pageNumber, Integer pageSize, CustomerWaterRightSortColumn sortColumn, SortDirection sortDirection) {
        return ResponseEntity.ok(custService.getCustomerWaterRights(pageNumber, pageSize, sortColumn, sortDirection, contactId));
    }

    @Override
    public ResponseEntity<ApplicationSearchPageDto> getCustomerContactApplications(Long contactId, Integer pageNumber, Integer pageSize, ApplicationSortColumn sortColumn, SortDirection sortDirection) {
        return ResponseEntity.ok(custService.getCustomerApplications(pageNumber, pageSize, sortColumn, sortDirection, contactId));
    }

    @Override
    public ResponseEntity<CustomerOwnershipUpdatePageDto> getCustomerContactOwnershipUpdates(Long contactId, Integer pageNumber, Integer pageSize, CustomerOwnershipSortColumn sortColumn, SortDirection sortDirection, OwnershipUpdateRole ownershipUpdateRole) {
        return ResponseEntity.ok(custService.getCustomerContactOwnershipUpdates(pageNumber, pageSize, sortColumn, sortDirection, contactId, ownershipUpdateRole));
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ELECTRONIC_CONTACTS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.ELECTRONIC_CONTACTS_TABLE)
    })
    public ResponseEntity<ElectronicContactsSearchPageDto> searchElectronicContacts(Long customerId,
                                                                                    Integer pageNumber,
                                                                                    Integer pageSize,
                                                                                    ElectronicContactsSortColumn sortColumn,
                                                                                    SortDirection sortDirection) {

        LOGGER.info("Search for Electronic Contacts");
        ElectronicContactsSearchPageDto contactsPage =
                electronicContactsService.searchElectronicContacts(pageNumber, pageSize, sortColumn, sortDirection, customerId);
        return ResponseEntity.ok(contactsPage);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ELECTRONIC_CONTACTS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.ELECTRONIC_CONTACTS_TABLE)
    })
    public ResponseEntity<ElectronicContactsDto> createElectronicContact(Long customerId, ElectronicContactsUpdateDto contact) {

        LOGGER.info("Creating an Electronic Contact");
        ElectronicContactsDto newContact =
                electronicContactsService.createElectronicContact(contact);
        return new ResponseEntity<>(newContact, null, HttpStatus.CREATED);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ELECTRONIC_CONTACTS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.ELECTRONIC_CONTACTS_TABLE)
    })
    public ResponseEntity<ElectronicContactsDto> changeElectronicContact(Long customerId,
                                                                         Long electronicId,
                                                                         ElectronicContactsUpdateDto electronicContactsUpdateDto) {
        LOGGER.info("Update existing Electronic Contact");
        ElectronicContactsDto updatedContact = electronicContactsService.changeElectronicContact(electronicId, electronicContactsUpdateDto);
        return ResponseEntity.ok(updatedContact);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ELECTRONIC_CONTACTS_TABLE)
    })
    public ResponseEntity<ElectronicContactsDto> getElectronicContact(Long customerId, Long electronicId) {

        LOGGER.info("Get specific Electronic Contact");
        ElectronicContactsDto contact = electronicContactsService.getElectronicContact(electronicId);
        return ResponseEntity.ok(contact);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ELECTRONIC_CONTACTS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.ELECTRONIC_CONTACTS_TABLE)
    })
    public ResponseEntity<Void> deleteElectronicContact(Long customerId, Long electronicId) {

        LOGGER.info("Deleting specific Electronic Contact");
        electronicContactsService.deleteElectronicContact(customerId, electronicId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.NOT_THE_SAME_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.NOT_THE_SAMES_TABLE)
    })
    public ResponseEntity<NotTheSameSearchPageDto> searchNotTheSame(Long contactId,
                                                                    Integer pageNumber,
                                                                    Integer pageSize,
                                                                    NotTheSameSortColumn sortColumn,
                                                                    SortDirection sortDirection) {
        LOGGER.info("Search Not The Same Contacts");
        NotTheSameSearchPageDto page = custService.searchNotTheSame(pageNumber, pageSize, sortColumn, sortDirection, contactId.toString());
        return ResponseEntity.ok(page);

    }


    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<AddressDto> createAddress(Long customerId, AddressCreationDto addressCreationDto) {

        LOGGER.info("Create Address");
        AddressDto address = addressService.createAddress(customerId, addressCreationDto);
        return new ResponseEntity<>(address, null, HttpStatus.CREATED);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<AddressDto> changeAddress(Long customerId, Long addressId, AddressUpdateDto addressUpdateDto) {

        LOGGER.info("Change Address");
        AddressDto address = addressService.changeAddress(customerId, addressId, addressUpdateDto);
        return ResponseEntity.ok(address);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<AddressDto> getAddress(Long customerId, Long addressId) {

        LOGGER.info("Get an Address");
        AddressDto address = addressService.getAddress(customerId, addressId);
        return ResponseEntity.ok(address);

    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.ADDRESS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.ADDRESS_TABLE)
    })
    public ResponseEntity<Void> deleteAddress(Long customerId, Long addressId) {

        LOGGER.info("Delete Address");
        addressService.deleteAddress(customerId, addressId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);

    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<BuyerSellerOwnershipUpdatesForContactPageDto> getBuyerSellerOwnershipUpdatesForContact(Long contactId, Integer pageNumber, Integer pageSize, BuyerSellerOwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection, OwnershipUpdateRole ownershipUpdateRole) {
        LOGGER.info("Get Buyer or Seller Ownership Updates for a Contact");
        return ResponseEntity.ok(custService.getBuyerSellerOwnershipUpdatesForContact(contactId, pageNumber, pageSize, sortColumn, sortDirection, ownershipUpdateRole));
    }

}
