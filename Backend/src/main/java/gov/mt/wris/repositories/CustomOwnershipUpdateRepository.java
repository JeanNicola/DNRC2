package gov.mt.wris.repositories;

import gov.mt.wris.dtos.*;
import gov.mt.wris.models.Customer;
import gov.mt.wris.models.CustomerXref;
import gov.mt.wris.models.OwnershipUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

public interface CustomOwnershipUpdateRepository {

    public Page<OwnershipUpdateSearchResultDto> searchOwnershipUpdatesWithCounts(Pageable pageable, OwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, String ownershipUpdateId, String ownershipUpdateType, String waterRightNumber, LocalDate dateReceived, LocalDate dateSale, LocalDate dateProcessed, LocalDate dateTerminated);

    public Page<OwnershipUpdatesForContactSearchResultDto> getOwnershipUpdatesForContact(Pageable pageable, OwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection, Long contactId);

    public Page<Customer> getOwnershipUpdateBuyers(Pageable pageable, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId);

    public Page<OwnershipUpdateSellersAndBuyerSearchResultDto> searchOwnershipUpdateBuyersWithCounts(Pageable pageable, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId);

    public Page<Customer> getOwnershipUpdateSellers(Pageable pageable, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId);

    public Page<OwnershipUpdateSellersAndBuyerSearchResultDto> searchOwnershipUpdateSellersWithCounts(Pageable pageable, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId);

    public TreeMap<String, Integer> getOwnershipUpdateCountsForTransfer(BigDecimal ownerUpdateId);

    public Page<CustomerXref> getBuyersForOwnershipUpdate(Pageable pageable, BuyersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId);

    public Page<CustomerXref> getSellersForOwnershipUpdate(Pageable pageable, SellersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId);

    public Page<PopulateByGeocodesSearchResultDto> getWaterRightsByGeocode(Pageable pageable, PopulateByGeocodesSortColumn sortColumn, SortDirection sortDirection, Long ownerUpdateId);

    public TransferWaterRightsOwnershipResultDto transferWaterRightsOwnership(OwnershipUpdate model);

    public Integer getParentApplicationCountForOwnershipUpdate(BigDecimal ownerUpdateId);
    public Integer getChildApplicationCountForOwnershipUpdate(BigDecimal ownerUpdateId);
    public boolean getAllApplicationsIncludedFlag(BigDecimal ownerUpdateId);
    public boolean getWaterRightSharedWithOtherOwnershipUpdateFlag(OwnershipUpdate model);
}
