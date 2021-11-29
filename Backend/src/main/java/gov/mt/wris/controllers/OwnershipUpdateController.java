package gov.mt.wris.controllers;

import gov.mt.wris.annot.Permission;
import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.api.OwnershipUpdatesApiDelegate;
import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.FieldValidationException;
import gov.mt.wris.services.CustomerService;
import gov.mt.wris.services.OwnershipUpdateService;
import gov.mt.wris.services.PaymentService;
import gov.mt.wris.services.WaterRightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Controller
public class OwnershipUpdateController implements OwnershipUpdatesApiDelegate {

    private static Logger LOGGER = LoggerFactory.getLogger(OwnershipUpdateController.class);

    @Autowired
    private WaterRightService waterRightService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OwnershipUpdateService ownershipUpdateService;

    @Autowired
    private CustomerService customerService;

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdateWaterRightPageDto> getOwnershipUpdateWaterRights(Long ownerUpdateId, Integer pageNumber, Integer pageSize, OwnershipUpdateWaterRightSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get Ownership Update Water Rights");
        return ResponseEntity.ok(ownershipUpdateService.getOwnershipUpdateWaterRights(pageNumber, pageSize, sortColumn, sortDirection, ownerUpdateId));
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdateApplicationPageDto> getOwnershipUpdateApplications(Long ownerUpdateId, Integer pageNumber, Integer pageSize, OwnershipUpdateApplicationSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get Ownership Update Applications");
        return ResponseEntity.ok(ownershipUpdateService.getOwnershipUpdateApplications(pageNumber, pageSize, sortColumn, sortDirection, ownerUpdateId));
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<OwnershipUpdatePageDto> searchOwnershipUpdates(Integer pageNumber, Integer pageSize, OwnershipUpdateSortColumn sortColumn, SortDirection sortDirection, String ownershipUpdateId, String ownershipUpdateType, String waterRightNumber, LocalDate dateReceived, LocalDate dateSale, LocalDate dateProcessed, LocalDate dateTerminated) {
        LOGGER.info("Search Ownership Update");
        return ResponseEntity.ok(ownershipUpdateService.searchOwnershipUpdates(pageNumber, pageSize, sortColumn, sortDirection, ownershipUpdateId, ownershipUpdateType, waterRightNumber, dateReceived, dateSale, dateProcessed, dateTerminated));
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.INSERT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_VERSION_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE)
    })
    public ResponseEntity<OwnershipUpdateSearchResultDto> createOwnershipUpdate(OwnershipUpdateCreationDto dto) {
        LOGGER.info("Creating an Ownership Update");

        OwnershipUpdateSearchResultDto returnDto = ownershipUpdateService.createOwnershipUpdate(dto);

        return new ResponseEntity<OwnershipUpdateSearchResultDto>(returnDto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdatesForContactPageDto> getOwnershipUpdatesForContact(Long contactId, Integer pageNumber, Integer pageSize, OwnershipUpdatesForContactSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get Ownership Updates for a Contact");
        return ResponseEntity.ok(ownershipUpdateService.getOwnershipUpdatesForContact(contactId, pageNumber, pageSize, sortColumn, sortDirection));
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdateBuyersPageDto> searchOwnershipUpdateBuyers(Integer pageNumber, Integer pageSize, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId) {
        LOGGER.info("Search Ownership Update Buyers");
        return ResponseEntity.ok(ownershipUpdateService.searchOwnershipUpdateBuyers(pageNumber, pageSize, sortColumn, sortDirection, lastName, firstName, contactId));
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdateBuyersPageDto> getOwnershipUpdateBuyers(Long ownershipUpdateId, Integer pageNumber, Integer pageSize, OwnershipUpdateBuyersSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get Ownership Update Buyers");
        return ResponseEntity.ok(ownershipUpdateService.getOwnershipUpdateBuyers(ownershipUpdateId, pageNumber, pageSize, sortColumn, sortDirection));
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdateSellersPageDto> searchOwnershipUpdateSellers(Integer pageNumber, Integer pageSize, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection, String lastName, String firstName, String contactId) {
        LOGGER.info("Search Ownership Update Sellers");
        return ResponseEntity.ok(ownershipUpdateService.searchOwnershipUpdateSellers(pageNumber, pageSize, sortColumn, sortDirection, lastName, firstName, contactId));
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdateSellersPageDto> getOwnershipUpdateSellers(Long ownershipUpdateId, Integer pageNumber, Integer pageSize, OwnershipUpdateSellersSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get Ownership Update Sellers");
        return ResponseEntity.ok(ownershipUpdateService.getOwnershipUpdateSellers(ownershipUpdateId, pageNumber, pageSize, sortColumn, sortDirection));
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdateDto> getOwnershipUpdate(Long ownershipUpdateId) {
        LOGGER.info("Change Ownership Update");
        OwnershipUpdateDto ownershipUpdate = ownershipUpdateService.getOwnershipUpdate(BigDecimal.valueOf(ownershipUpdateId));
        return ResponseEntity.ok(ownershipUpdate);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE)
    })
    @Override
    public ResponseEntity<OwnershipUpdateDto> changeOwnershipUpdate(Long ownershipUpdateId, OwnershipUpdateUpdateDto changeOwnershipUpdate) {
        LOGGER.info("Change Ownership Update");
        OwnershipUpdateDto ownershipUpdate = ownershipUpdateService.changeOwnershipUpdate(BigDecimal.valueOf(ownershipUpdateId), changeOwnershipUpdate);
        return ResponseEntity.ok(ownershipUpdate);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE)
    })
    public ResponseEntity<OwnershipUpdateSellerDto> changeOwnershipUpdateSeller(Long ownerUpdateId, Long sellerId, OwnershipUpdateSellerUpdateDto ownershipUpdateSellerUpdateDto) {
        LOGGER.info("Change Ownership Update Seller");
        OwnershipUpdateSellerDto ous = ownershipUpdateService.changeOwnershipUpdateSeller(ownerUpdateId, sellerId, ownershipUpdateSellerUpdateDto);
        return ResponseEntity.ok(ous);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<BuyersForOwnershipUpdatePageDto> getBuyersForOwnershipUpdate(Long ownerUpdateId, Integer pageNumber, Integer pageSize, BuyersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection) {

        LOGGER.info("Get Associate Buyers for Ownership Update");
        BuyersForOwnershipUpdatePageDto dto = ownershipUpdateService.getBuyersForOwnershipUpdate(ownerUpdateId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }


    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<ResponsibleOfficeDto> getOwnershipUpdateResponsibleOffice(Long ownershipUpdateId) {
        LOGGER.info("Getting the Ownership Update's responsible office");

        ResponsibleOfficeDto dto = ownershipUpdateService.getResponsibleOffice(ownershipUpdateId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<SellersForOwnershipUpdatePageDto> getSellersForOwnershipUpdate(Long ownerUpdateId, Integer pageNumber, Integer pageSize, SellersForOwnershipUpdateSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get Associate Sellers for Ownership Update");
        SellersForOwnershipUpdatePageDto dto = ownershipUpdateService.getSellersForOwnershipUpdate(ownerUpdateId, pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }


    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE)
    })
    public ResponseEntity<ResponsibleOfficeDto> editOwnershipUpdateResponsibleOffice(Long ownershipUpdateId, ResponsibleOfficeDto dto) {
        LOGGER.info("Getting the Application's responsible office");

        ResponsibleOfficeDto returnDto = ownershipUpdateService.editResponsibleOffice(ownershipUpdateId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<ProcessorDto> getOwnershipUpdateProcessor(Long ownershipUpdateId) {
        LOGGER.info("Getting the Ownership Update's Processor");

        ProcessorDto dto = ownershipUpdateService.getProcessor(ownershipUpdateId);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE)
    })
    public ResponseEntity<ProcessorDto> editOwnershipUpdateProcessor(Long ownershipUpdateId, ProcessorDto dto) {
        LOGGER.info("Editing the Ownership Update's Processor");

        ProcessorDto returnDto = ownershipUpdateService.editProcessor(ownershipUpdateId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE)
    })
    public ResponseEntity<OfficePageDto> getOwnershipUpdateOffices(Long ownershipUpdateId, Integer pageNumber, Integer pageSize, OfficeSortColumn sortColumn, DescSortDirection sortDirection) {
        LOGGER.info("Getting a page of Offices belonging to an Ownership Update");

        OfficePageDto dto = ownershipUpdateService.getOwnershipUpdateOffices(ownershipUpdateId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNERSHIP_UPDATE_STAFF_XREFS_TABLE)
    })
    public ResponseEntity<StaffPageDto> getOwnershipUpdateStaff(Long ownershipUpdateId, Integer pageNumber, Integer pageSize, StaffSortColumn sortColumn, DescSortDirection sortDirection) {
        LOGGER.info("Getting a page of Offices belonging to an Ownership Update");

        StaffPageDto dto = ownershipUpdateService.getOwnershipUpdateStaff(ownershipUpdateId, pageNumber, pageSize, sortColumn, sortDirection);

        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE)
    })
    public ResponseEntity<OfficeDto> addOwnershipUpdateOffice(Long ownershipUpdateId, OfficeCreationDto dto) {
        LOGGER.info("Adding a new office to an Ownership UPdate");

        OfficeDto returnDto = ownershipUpdateService.addOwnershipUpdateOffice(ownershipUpdateId, dto);

        return new ResponseEntity<OfficeDto>(returnDto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE)
    })
    public ResponseEntity<OfficeDto> editOwnershipUpdateOffice(Long ownershipUpdateId, Long officeXrefId, OfficeDto dto) {
        LOGGER.info("Editing an Ownership Updates's office");

        OfficeDto returnDto = ownershipUpdateService.editOwnershipUpdateOffice(ownershipUpdateId, officeXrefId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OFFICES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE)
    })
    public ResponseEntity<Void> deleteOwnershipUpdateOffice(Long ownershipUpdateId, Long officeXrefId) {
        LOGGER.info("Removing an office from an Ownership Update");

        ownershipUpdateService.deleteOwnershipUpdateOffice(ownershipUpdateId, officeXrefId);

        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNERSHIP_UPDATE_STAFF_XREFS_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.OWNERSHIP_UPDATE_STAFF_XREFS_TABLE)
    })
    public ResponseEntity<StaffDto> addOwnershipUpdateStaff(Long ownershipUpdateId, StaffCreationDto dto) {
        LOGGER.info("Adding a new staff member to an Ownership Update");

        StaffDto returnDto = ownershipUpdateService.addOwnershipUpdateStaff(ownershipUpdateId, dto);

        return new ResponseEntity<StaffDto>(returnDto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNERSHIP_UPDATE_STAFF_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.OWNERSHIP_UPDATE_STAFF_XREFS_TABLE)
    })
    public ResponseEntity<StaffDto> editOwnershipUpdateStaff(Long ownershipUpdateId, Long officeXrefId, StaffDto dto) {
        LOGGER.info("Editing a Ownership Update's staff member");

        StaffDto returnDto = ownershipUpdateService.editOwnershipUpdateStaff(ownershipUpdateId, officeXrefId, dto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.MASTER_STAFF_INDEXES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNERSHIP_UPDATE_STAFF_XREFS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.OWNERSHIP_UPDATE_STAFF_XREFS_TABLE)
    })
    public ResponseEntity<Void> deleteOwnershipUpdateStaff(Long ownershipUpdateId, Long staffXrefId) {
        LOGGER.info("Removing a staff member from an Ownership Update");

        ownershipUpdateService.deleteOwnershipUpdateStaff(ownershipUpdateId, staffXrefId);

        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }
    

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE) })
    public ResponseEntity<OwnershipUpdatePaymentsPageDto> getOwnershipUpdatePayments(Long ownershipUpdateId, Integer pageNumber, Integer pageSize, PaymentSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get a page of the Payments Tab");
        OwnershipUpdatePaymentsPageDto paymentsDto = paymentService.getOwnershipUpdatePayments(pageNumber, pageSize, sortColumn, sortDirection, ownershipUpdateId);
        return ResponseEntity.ok(paymentsDto);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.INSERT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE) })
    public ResponseEntity<PaymentDto> createOwnershipUpdatePayment(Long ownershipUpdateId, PaymentDto paymentDto) {
        LOGGER.info("Add a new Payment");

        // convert everything to upper case
        paymentDto = paymentService.toUpperCase(paymentDto);

        if (paymentDto.getDatePaid() == null) {
            paymentDto.setDatePaid(LocalDate.now());
        } else if (paymentDto.getDatePaid().isAfter(LocalDate.now())) {
            throw new FieldValidationException("Date Paid is after today",
                    "Need to prevent Users from entering a Date after today for Date Paid", Arrays.asList("datePaid"));
        }

        PaymentDto newPayment = paymentService.createOwnershipUpdatePayment(ownershipUpdateId, paymentDto);

        return new ResponseEntity<PaymentDto>(newPayment, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.PAYMENT_TABLE) })
    public ResponseEntity<PaymentDto> updateOwnershipUpdatePayment(Long ownershipUpdateId, Long paymentId, PaymentDto paymentDto) {
        LOGGER.info("Update a Payment");

        // convert everything to upper case
        paymentDto = paymentService.toUpperCase(paymentDto);

        if (paymentDto.getDatePaid().isAfter(LocalDate.now())) {
            throw new FieldValidationException("Date Paid is after today",
                    "Need to prevent Users from entering a Date after today for Date Paid", Arrays.asList("datePaid"));
        }
        PaymentDto newPayment = paymentService.updateOwnershipUpdatePayment(ownershipUpdateId, paymentId, paymentDto);

        return ResponseEntity.ok(newPayment);
    }

    @PermissionsNeeded({ @Permission(verb = Constants.DELETE, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.PAYMENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE) })
    public ResponseEntity<Void> deleteOwnershipUpdatePayment(Long ownershipUpdateId, Long paymentId) {
        LOGGER.info("Delete a Payment");
        paymentService.deleteOwnershipUpdatePayment(ownershipUpdateId, paymentId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }


    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<SellerForOwnershipUpdateResultDto> createSellerReferenceToOwnershipUpdate(Long ownerUpdateId, SellerReferenceToOwnershipUpdateCreationDto sellerReferenceToOwnershipUpdateCreationDto) {
        LOGGER.info("Adding Seller references to an Ownership Update");
        SellerForOwnershipUpdateResultDto dto = ownershipUpdateService.createSellerReferenceToOwnershipUpdate(ownerUpdateId, sellerReferenceToOwnershipUpdateCreationDto);
        return new ResponseEntity<SellerForOwnershipUpdateResultDto>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<BuyerForOwnershipUpdateResultDto> createBuyerReferenceToOwnershipUpdate(Long ownerUpdateId, BuyerReferenceToOwnershipUpdateCreationDto buyerReferenceToOwnershipUpdateCreationDto) {
        LOGGER.info("Adding Buyer references to an Ownership Update");
        BuyerForOwnershipUpdateResultDto dto = ownershipUpdateService.createBuyerReferenceToOwnershipUpdate(ownerUpdateId, buyerReferenceToOwnershipUpdateCreationDto);
        return new ResponseEntity<BuyerForOwnershipUpdateResultDto>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<WaterRightReferenceToOwnershipUpdateResultDto> createWaterRightReferenceToOwnershipUpdate(Long ownerUpdateId, WaterRightReferenceToOwnershipUpdateCreationDto waterRightReferenceToOwnershipUpdateCreationDto) {
        LOGGER.info("Adding Water Right references to an Ownership Update");
        WaterRightReferenceToOwnershipUpdateResultDto dto = ownershipUpdateService.createWaterRightReferenceToOwnershipUpdate(ownerUpdateId, waterRightReferenceToOwnershipUpdateCreationDto);
        return new ResponseEntity<WaterRightReferenceToOwnershipUpdateResultDto>(dto, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<Void>  deleteBuyerReferenceToOwnershipUpdate(Long ownerUpdateId, Long buyerId) {
        LOGGER.info("Remove Buyer reference from Ownership Update");
        ownershipUpdateService.deleteBuyerReferenceToOwnershipUpdate(ownerUpdateId, buyerId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<Void>  deleteSellerReferenceToOwnershipUpdate(Long ownerUpdateId, Long sellerId) {
        LOGGER.info("Remove Seller reference from Ownership Update");
        ownershipUpdateService.deleteSellerReferenceToOwnershipUpdate(ownerUpdateId, sellerId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<Void> deleteAllSellers(Long ownerUpdateId) {
        LOGGER.info("Remove all Sellers references from Ownership Update");
        ownershipUpdateService.deleteAllSellersReferenceToOwnershipUpdate(ownerUpdateId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<Void>  deleteWaterRightReferenceToOwnershipUpdate(Long ownerUpdateId, Long waterRightId) {
        LOGGER.info("Remove Water Right reference from Ownership Update");
        ownershipUpdateService.deleteWaterRightReferenceToOwnershipUpdate(ownerUpdateId, waterRightId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TYPE_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_STATUS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.STATUS_TYPE_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_GEOCODE_XREF_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE)
    })
    public ResponseEntity<PopulateByGeocodesPageDto> getWaterRightsByGeocode(Long ownerUpdateId,
                                                                      Integer pageNumber,
                                                                      Integer pageSize,
                                                                      PopulateByGeocodesSortColumn sortColumn,
                                                                      SortDirection sortDirection) {
        LOGGER.info("Get Water Rights by Geocode");
        PopulateByGeocodesPageDto dto = ownershipUpdateService.getWaterRightsByGeocode(ownerUpdateId, pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<ApplicationReferenceToOwnershipUpdateResultDto> createApplicationReferenceToOwnershipUpdate(Long ownerUpdateId, ApplicationReferenceToOwnershipUpdateCreationDto dto) {
        LOGGER.info("Adding Application references to an Ownership Update");
        ApplicationReferenceToOwnershipUpdateResultDto dto1 = ownershipUpdateService.createApplicationReferenceToOwnershipUpdate(ownerUpdateId, dto);
        return new ResponseEntity<ApplicationReferenceToOwnershipUpdateResultDto>(dto1, null, HttpStatus.CREATED);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<Void>  deleteApplicationReferenceToOwnershipUpdate(Long ownerUpdateId, Long applicationId) {
        LOGGER.info("Remove Application reference from Ownership Update");
        ownershipUpdateService.deleteApplicationReferenceToOwnershipUpdate(ownerUpdateId, applicationId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<OwnershipUpdateFeeSummaryDto> searchOwnershipFeeSummary(Long ownerUpdateId) {
        LOGGER.info("Searching Fee Summary information for Ownership Update");
        OwnershipUpdateFeeSummaryDto returnDto = ownershipUpdateService.searchOwnershipFeeSummary(ownerUpdateId);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<OwnershipUpdateFeeSummaryDto> changeFeeDue(Long ownerUpdateId, OwnershipUpdateChangeFeeSummaryDto ownershipUpdateChangeFeeSummaryDto) {
        LOGGER.info("Updating Fee Due");
        OwnershipUpdateFeeSummaryDto returnDto = ownershipUpdateService.changeOwnershipUpdateFeeSummary(ownerUpdateId, ownershipUpdateChangeFeeSummaryDto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
        @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
        @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<OwnershipUpdateFeeSummaryDto> calculateOwnershipUpdateFeeDue(Long ownerUpdateId) {
        LOGGER.info("Calculating Fee Due");
        OwnershipUpdateFeeSummaryDto returnDto = ownershipUpdateService.calculateOwnershipUpdateFeeDue(ownerUpdateId);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<OwnershipUpdateFeeLetterDto> searchOwnershipFeeLetter(Long ownerUpdateId) {
        LOGGER.info("Searching Fee Letter information for Ownership Update");
        OwnershipUpdateFeeLetterDto returnDto = ownershipUpdateService.searchOwnershipFeeLetter(ownerUpdateId);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE)
    })
    public ResponseEntity<OwnershipUpdateFeeLetterDto> changeFeeLetter(Long ownerUpdateId, OwnershipUpdateChangeFeeLetterDto ownershipUpdateChangeFeeLetterDto) {
        LOGGER.info("Updating Ownership Update Fee Letter information");
        OwnershipUpdateFeeLetterDto returnDto = ownershipUpdateService.changeOwnershipUpdateFeeLetter(ownerUpdateId, ownershipUpdateChangeFeeLetterDto);

        return ResponseEntity.ok(returnDto);
    }

    @Override
    public ResponseEntity<WaterRightUpdateDividedOwnershipDto> changeWaterRight(Long ownerUpdateId, Long waterRightId, WaterRightUpdateDividedOwnershipDto waterRightUpdateDividedOwnershipDto) {
        LOGGER.info("Updating Water Right Divided Ownership");
        WaterRightUpdateDividedOwnershipDto returnDto = waterRightService.updateWaterRightDividedOwnership(ownerUpdateId, waterRightId, waterRightUpdateDividedOwnershipDto);

        return ResponseEntity.ok(returnDto);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.EVENT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.WATER_RIGHT_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSIONS_TABLE),
            @Permission(verb = Constants.UPDATE, table = Constants.VERSIONS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.VERSION_APPLICATION_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.APPLICATION_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_OWNERSHIP_UPDATES_TABLE),
            @Permission(verb = Constants.EXECUTE, table = Constants.COMMON_FUNCTIONS)
    })
    public ResponseEntity<TransferWaterRightsOwnershipResultDto> transferWaterRightsOwnership(Long ownerUpdateId, Object body) {
        LOGGER.info("Transfer Water Rights for Ownership Update");
        TransferWaterRightsOwnershipResultDto dto = ownershipUpdateService.transferWaterRightsOwnership(ownerUpdateId);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<PopulateBySellersPageDto> getWaterRightsBySellers(Long ownerUpdateId, Integer pageNumber, Integer pageSize, PopulateBySellersSortColumn sortColumn, SortDirection sortDirection) {
        LOGGER.info("Get Water Rights for Ownership Update by Sellers");
        PopulateBySellersPageDto dto = ownershipUpdateService.getWaterRightsBySellers(ownerUpdateId, pageNumber, pageSize, sortColumn, sortDirection);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Void> deleteAllApps(Long ownerUpdateId) {
        LOGGER.info("Remove all Applications references from Ownership Update");
        ownershipUpdateService.deleteAllAppsReferenceToOwnershipUpdate(ownerUpdateId);
        return new ResponseEntity<Void>(null, null, HttpStatus.NO_CONTENT);
    }

    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.DELETE, table = Constants.APPLICATION_TABLE)
    })
    public ResponseEntity<OwnershipUpdatesChangeApplicationsPageDto> searchOwnershipUpdatesChangeApplications(Long ownerUpdateId,
                                                                                                              Integer pageNumber,
                                                                                                              Integer pageSize,
                                                                                                              OwnershipUpdatesChangeApplicationsSortColumn sortColumn,
                                                                                                              SortDirection sortDirection,
                                                                                                              String applicationId,
                                                                                                              String basin) {
        LOGGER.info("List Change Applications for Ownership Update Water Rights");
        OwnershipUpdatesChangeApplicationsPageDto page = ownershipUpdateService.searchOwnershipUpdatesChangeApplications(ownerUpdateId, pageNumber, pageSize, sortColumn, sortDirection, applicationId, basin);
        return ResponseEntity.ok(page);
    }

    @Override
    @PermissionsNeeded({
            @Permission(verb = Constants.SELECT, table = Constants.CUSTOMER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.OWNER_TABLE),
            @Permission(verb = Constants.SELECT, table = Constants.WATER_RIGHT_TABLE)
    })
    public ResponseEntity<CustomerPageDto> searchActiveSellersOwnershipUpdate(Long ownerUpdateId,
                                                                       Integer pageNumber,
                                                                       Integer pageSize,
                                                                       CustomerSortColumn sortColumn,
                                                                       SortDirection sortDirection,
                                                                       String contactId,
                                                                       String lastName,
                                                                       String firstName) {

        LOGGER.info("List Available Active Sellers for Ownership Update");
        CustomerPageDto page = customerService.searchActiveSellersOwnershipUpdate(pageNumber, pageSize, sortColumn, sortDirection, ownerUpdateId, contactId, lastName, firstName);
        return ResponseEntity.ok(page);

    }

}
