package gov.mt.wris.repositories;

import gov.mt.wris.dtos.CustomerContactSearchResultDto;
import gov.mt.wris.dtos.CustomerContactsSortColumn;
import gov.mt.wris.dtos.CustomerSortColumn;
import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import gov.mt.wris.models.Customer;

import java.util.List;

public interface CustomCustomerRepository {

    public Page<Customer> searchCustomers(Pageable pageable, CustomerSortColumn sortColumn, SortDirection sortDirection, String contactID, String lastName, String firstName, String firstLastName);

    public Page<Customer> searchCustomersByWaterRights(Pageable pageable, CustomerSortColumn sortColumn, SortDirection sortDirection, List<Long> waterRightIds, String contactID, String lastName, String firstName);

    public Page<Customer> searchActiveSellersOwnershipUpdate(Pageable pageable, CustomerSortColumn sortColumn, SortDirection sortDirection, List<Long> waterRightIds, Long ownerUpdateId, String contactID, String lastName, String firstName);

    public Page<CustomerContactSearchResultDto> searchCustomerContacts(Pageable pageable, CustomerContactsSortColumn sortColumn, SortDirection sortDirection, String contactID, String lastName, String firstName, String middleInitial, String suffix, String contactType, String contactStatus);

    public Page<BuyerSellerOwnershipUpdatesForContactSearchResultDto> getBuyerSellerOwnershipUpdatesForContact(Pageable pageable, BuyerSellerOwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection, Long contactId, OwnershipUpdateRole ownershipUpdateRole);

}
