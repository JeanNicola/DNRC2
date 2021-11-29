package gov.mt.wris.services;

import gov.mt.wris.dtos.ActiveCustomersRequestDto;
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
import gov.mt.wris.dtos.NotTheSameSearchPageDto;
import gov.mt.wris.dtos.NotTheSameSortColumn;
import gov.mt.wris.dtos.OwnershipUpdateRole;
import gov.mt.wris.dtos.SortDirection;

public interface CustomerService {
    public CustomerPageDto searchCustomers(int pagenumber, int pagesize, CustomerSortColumn sortColumn, SortDirection sortDirection, String contactID, String lastName, String firstName, String name);

    public CustomerPageDto searchCustomersByWaterRights(int pagenumber, int pagesize, CustomerSortColumn sortColumn, SortDirection sortDirection, ActiveCustomersRequestDto activeCustomersRequestDto, String contactID, String lastName, String firstName);

    public CustomerPageDto searchActiveSellersOwnershipUpdate(int pagenumber, int pagesize, CustomerSortColumn sortDTOColumn, SortDirection sortDirection, Long ownerUpdateId, String contactId, String lastName, String firstName);

    public CustomerContactSearchPageDto searchCustomerContacts(int pagenumber, int pagesize, CustomerContactsSortColumn sortColumn, SortDirection sortDirection, String contactId, String lastName, String firstName, String middleInitial, String suffix, String contactType, String contactStatus);

    public CustomerContactDto getCustomerContact(Long contactId);

    public CustomerContactDto createCustomerContact(CustomerContactCreationDto newContact);

    public CustomerContactDto changeCustomerContact(Long contactId, CustomerContactUpdateDto contact);

    public NotTheSameSearchPageDto searchNotTheSame(int pagenumber, int pagesize, NotTheSameSortColumn sortColumn, SortDirection sortDirection, String contactId);

    public CustomerOwnershipUpdatePageDto getCustomerContactOwnershipUpdates(int pagenumber, int pagesize, CustomerOwnershipSortColumn sortColumn, SortDirection sortDirection, Long contactId, OwnershipUpdateRole ownershipUpdateRole);

    public CustomerWaterRightPageDto getCustomerWaterRights(int pagenumber, int pagesize, CustomerWaterRightSortColumn sortColumn, SortDirection sortDirection, Long contactId);

    public ApplicationSearchPageDto getCustomerApplications(int pagenumber, int pagesize, ApplicationSortColumn sortColumn, SortDirection sortDirection, Long contactId);

    public BuyerSellerOwnershipUpdatesForContactPageDto getBuyerSellerOwnershipUpdatesForContact(Long contactId, int pageNumber, int pageSize, BuyerSellerOwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection, OwnershipUpdateRole ownershipUpdateRole);
}
