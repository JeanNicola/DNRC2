package gov.mt.wris.services;

import gov.mt.wris.dtos.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface OwnershipUpdateService {

    public OwnershipUpdateWaterRightPageDto getOwnershipUpdateWaterRights(int pagenumber, int pagesize, OwnershipUpdateWaterRightSortColumn sortColumn, SortDirection sortDirection, Long ownerUpdateId);

    public OwnershipUpdateApplicationPageDto getOwnershipUpdateApplications(int pagenumber, int pagesize, OwnershipUpdateApplicationSortColumn sortColumn, SortDirection sortDirection, Long ownerUpdateId);

    public OwnershipUpdatePageDto searchOwnershipUpdates(int pageNumber, int pageSize, OwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, String ownershipUpdateId, String ownershipUpdateType, String waterRightNumber, LocalDate dateReceived, LocalDate dateSale, LocalDate dateProcessed, LocalDate dateTerminated);

    public OwnershipUpdateSearchResultDto createOwnershipUpdate(OwnershipUpdateCreationDto dto);

    public OwnershipUpdatesForContactPageDto getOwnershipUpdatesForContact(Long contactId, int pageNumber, int pageSize, OwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection);

    public OwnershipUpdateBuyersPageDto searchOwnershipUpdateBuyers(int pageNumber, int pageSize, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId);

    public OwnershipUpdateBuyersPageDto getOwnershipUpdateBuyers(Long ownershipUpdateId, int pageNumber, int pageSize, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection);

    public OwnershipUpdateSellersPageDto searchOwnershipUpdateSellers(int pageNumber, int pageSize, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId);

    public OwnershipUpdateSellersPageDto getOwnershipUpdateSellers(Long ownershipUpdateId, int pageNumber, int pageSize, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection);

    public OwnershipUpdateDto getOwnershipUpdate(BigDecimal ownershipUpdateId);

    public OwnershipUpdateDto changeOwnershipUpdate(BigDecimal ownershipUpdateId, OwnershipUpdateUpdateDto updateOwnershipUpdate);

    public BuyersForOwnershipUpdatePageDto getBuyersForOwnershipUpdate(Long ownershipUpdateId, int pageNumber, int pageSize, BuyersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection);

    public SellersForOwnershipUpdatePageDto getSellersForOwnershipUpdate(Long ownershipUpdateId, int pageNumber, int pageSize, SellersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection);

    public void deleteOwnershipUpdate(Long ownershipUpdateId);

    public ResponsibleOfficeDto getResponsibleOffice(Long ownerUpdateId);
    public ResponsibleOfficeDto editResponsibleOffice(Long ownerUpdateId, ResponsibleOfficeDto dto);

    public ProcessorDto getProcessor(Long ownerUpdateId);
    public ProcessorDto editProcessor(Long ownerUpdateId, ProcessorDto dto);

    public OfficePageDto getOwnershipUpdateOffices(Long ownershipUpdateId, int pageNumber, int pageSize, OfficeSortColumn sortDTOColumn, DescSortDirection sortDirection);
    public StaffPageDto getOwnershipUpdateStaff(Long ownershipUpdateId, int pageNumber, int pageSize, StaffSortColumn sortDTOColumn, DescSortDirection sortDirection);

    public OfficeDto addOwnershipUpdateOffice(Long ownershipUpdateId, OfficeCreationDto dto);
    public OfficeDto editOwnershipUpdateOffice(Long ownershipUpdateId, Long officeXrefId, OfficeDto dto);
    public void deleteOwnershipUpdateOffice(Long ownershipUpdateId, Long officeXrefId);

    public StaffDto addOwnershipUpdateStaff(Long ownershipUpdateId, StaffCreationDto dto);
    public StaffDto editOwnershipUpdateStaff(Long ownershipUpdateId, Long staffXrefId, StaffDto dto);
    public void deleteOwnershipUpdateStaff(Long ownershipUpdateId, Long staffXrefId);

    public OwnershipUpdateSellerDto changeOwnershipUpdateSeller(Long ownerUpdateId, Long sellerId, OwnershipUpdateSellerUpdateDto ownershipUpdateSellerUpdateDto);
    public SellerForOwnershipUpdateResultDto createSellerReferenceToOwnershipUpdate(Long ownerUpdateId, SellerReferenceToOwnershipUpdateCreationDto sellerReferenceToOwnershipUpdateCreationDto);
    public BuyerForOwnershipUpdateResultDto createBuyerReferenceToOwnershipUpdate(Long ownerUpdateId, BuyerReferenceToOwnershipUpdateCreationDto buyerReferenceToOwnershipUpdateCreationDto);
    public WaterRightReferenceToOwnershipUpdateResultDto createWaterRightReferenceToOwnershipUpdate(Long ownerUpdateId, WaterRightReferenceToOwnershipUpdateCreationDto waterRightReferenceToOwnershipUpdateCreationDto);
    public void deleteSellerReferenceToOwnershipUpdate(Long ownerUpdateId, Long sellerId);
    public void deleteAllSellersReferenceToOwnershipUpdate(Long ownerUpdateId);
    public void deleteAllAppsReferenceToOwnershipUpdate(Long ownerUpdateId);
    public void deleteBuyerReferenceToOwnershipUpdate(Long ownerUpdateId, Long buyerId);
    public void deleteWaterRightReferenceToOwnershipUpdate(Long ownerUpdateId, Long waterRightId);

    public PopulateByGeocodesPageDto getWaterRightsByGeocode(Long ownerUpdateId, Integer pageNumber, Integer pageSize, PopulateByGeocodesSortColumn sortColumn, SortDirection sortDirection);
    public PopulateBySellersPageDto getWaterRightsBySellers(Long ownerUpdateId, Integer pageNumber, Integer pageSize, PopulateBySellersSortColumn sortColumn, SortDirection sortDirection);
    public ApplicationReferenceToOwnershipUpdateResultDto createApplicationReferenceToOwnershipUpdate(Long ownerUpdateId, ApplicationReferenceToOwnershipUpdateCreationDto applicationReferenceToOwnershipUpdateCreationDto);
    public void deleteApplicationReferenceToOwnershipUpdate(Long ownerUpdateId, Long applicationId);

    public TransferWaterRightsOwnershipResultDto transferWaterRightsOwnership(Long ownerUpdateId);

    public OwnershipUpdateFeeSummaryDto searchOwnershipFeeSummary(Long ownerUpdateId);
    public OwnershipUpdateFeeLetterDto searchOwnershipFeeLetter(Long ownerUpdateId);
    public OwnershipUpdateFeeSummaryDto calculateOwnershipUpdateFeeDue(Long ownerUpdateId);
    public OwnershipUpdateFeeSummaryDto changeOwnershipUpdateFeeSummary(Long ownerUpdateId, OwnershipUpdateChangeFeeSummaryDto dto);
    public OwnershipUpdateFeeLetterDto changeOwnershipUpdateFeeLetter(Long ownerUpdateId, OwnershipUpdateChangeFeeLetterDto dto);

    public OwnershipUpdatesChangeApplicationsPageDto searchOwnershipUpdatesChangeApplications(Long ownerUpdateId, Integer pageNumber, Integer pageSize, OwnershipUpdatesChangeApplicationsSortColumn sortColumn, SortDirection sortDirection, String applicationId, String basin);

}
