package gov.mt.wris.services.Implementation;

import gov.mt.wris.dtos.*;
import gov.mt.wris.exceptions.DataConflictException;
import gov.mt.wris.exceptions.DataUsedElsewhereException;
import gov.mt.wris.exceptions.NotFoundException;
import gov.mt.wris.models.Application;
import gov.mt.wris.models.Event;
import gov.mt.wris.models.OwnershipUpdate;
import gov.mt.wris.models.Payment;
import gov.mt.wris.repositories.ApplicationRepository;
import gov.mt.wris.repositories.EventRepository;
import gov.mt.wris.repositories.OwnershipUpdateRepository;
import gov.mt.wris.repositories.PaymentRepository;
import gov.mt.wris.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class PaymentServiceImpl implements PaymentService{
    private static Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentRepository payRepo;

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private OwnershipUpdateRepository ouRepo;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    ApplicationAutoCompleteServiceImpl autoCompleteService;


    private Optional<Application> checkApplicationExists(BigDecimal appId) throws NotFoundException {
        Optional<Application> foundApp = appRepo.findById(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("The Application id " + appId.longValue() + " was not found");
        }
        return foundApp;
    }

    private Optional<OwnershipUpdate> checkOwnershipUpdateExists(BigDecimal ownershipUpdateId) throws NotFoundException {
        Optional<OwnershipUpdate> ouResult = ouRepo.findById(ownershipUpdateId);
        if(!ouResult.isPresent()) {
            throw new NotFoundException("Ownership Update " + ownershipUpdateId + " not found");
        }
        return ouResult;
    }

    private Optional<Payment> checkPaymentExists(BigDecimal paymentId) throws NotFoundException {
        Optional<Payment> foundPayment = payRepo.findById(paymentId);
        if(!foundPayment.isPresent()) {
            throw new NotFoundException("This Payment doesn't exist");
        }
        return foundPayment;
    }

    private void validatePaymentDto(PaymentDto paymentDto, Boolean isCreate, Payment payment) throws DataUsedElsewhereException {
        if (paymentDto.getDatePaid().isAfter(LocalDate.now())) {
            throw new ValidationException("Date Paid cannot be after today");
        }
        if (isCreate) {
            int trackingNoCount = payRepo.countByTrackingNo(paymentDto.getTrackingNumber());
            if(paymentDto.getOrigin().equals("TLMS") && trackingNoCount > 0) {
                throw new DataUsedElsewhereException("Tracking Number " + paymentDto.getTrackingNumber() + " is already being used");
            }
        } else {
            boolean sameTrackingNumber = payment.getTrackingNo().equals(paymentDto.getTrackingNumber());
            int trackingNoCount = payRepo.countByTrackingNo(paymentDto.getTrackingNumber());
            // If we changing the tracking number, the new one will need to not exist
            // Otherwise, the same one will need to only exist once, it already exists
            boolean isNotUnique = ( !sameTrackingNumber && trackingNoCount > 0) || (sameTrackingNumber && trackingNoCount > 1);
            if(paymentDto.getOrigin().equals("TLMS") && isNotUnique) {
                throw new DataUsedElsewhereException("Tracking Number " + paymentDto.getTrackingNumber() + " is already being used");
            }
        }
    }

    @Override
    public PaymentsPageDto getPaymentPage(Integer pageNumber, Integer pageSize, PaymentSortColumn sortColumn, SortDirection sortDirection, Long applicationId) {
        LOGGER.info("Get a Page of Payments");

        String sortDtoColumn = getPaymentSortColumn(sortColumn);

        PaymentsPageDtoResults paymentResults = new PaymentsPageDtoResults();

        PaymentSummaryDto paymentSummary = new PaymentSummaryDto();
        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = checkApplicationExists(appId);

        /* Determine if app can be auto completed... */
        boolean canAutoComplete = autoCompleteService.canAutoComplete(appId);
        paymentSummary.setCanAutoComplete(canAutoComplete);

        Application app = foundApp.get();
        paymentSummary.setFeeStatus(app.getFeeStatus());
        if(app.getFeeStatusReference() != null) paymentSummary.setFeeStatusDescription(app.getFeeStatusReference().getMeaning());

        paymentSummary.setFeeDiscount(app.getFeeDiscount());
        if(app.getFeeDiscountReference() != null)
            paymentSummary.setFeeDiscountDescription(app.getFeeDiscountReference().getMeaning());


        paymentSummary.setFeeWaived(app.getFeeWaived());
        if(app.getFeeWaivedReference() != null)
            paymentSummary.setFeeWaivedDescription(app.getFeeWaivedReference().getMeaning());

        paymentSummary.setFeeOther(app.getFeeOther());
        if(app.getFeeOtherReference() != null)
            paymentSummary.setFeeOtherDescription(app.getFeeOtherReference().getMeaning());

        paymentSummary.setFeeCGWA(app.getFeeCGWA());
        if(app.getFeeCGWAReference() != null)
            paymentSummary.setFeeCGWADescription(app.getFeeCGWAReference().getMeaning());

        if (app.getType() != null) {
            if (app.getType().getFeeCGWA() != null) paymentSummary.setAppFeeCGWA(app.getType().getFeeCGWA().doubleValue());
            if (app.getType().getFeeOther() != null) paymentSummary.setAppFeeOther(app.getType().getFeeOther().doubleValue());
            if (app.getType().getFeeDiscount() != null) paymentSummary.setAppFeeDiscount(app.getType().getFeeDiscount().doubleValue());
        }

        double amountPaid = payRepo.getFeesPaid(appId);
        paymentSummary.setAmountPaid(amountPaid);

        double filingFee = app.getFilingFee() != null ? app.getFilingFee().doubleValue() : 0;
        paymentSummary.setFeeDue(filingFee);
        paymentSummary.setTotalDue(filingFee - amountPaid);

        paymentResults.setSummary(paymentSummary);

        Sort.Direction sortDtoDirection = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber -1, pageSize, Sort.by(sortDtoDirection,sortDtoColumn).and(Sort.by(Sort.Direction.DESC, "trackingNo")));
        Page<Payment> resultPage = payRepo.findByApplicationId(pageable, appId);

        paymentResults.setDetails(resultPage.getContent().stream().map(payment -> {
            return getPaymentDetailDto(payment);
        }).collect(Collectors.toList()));

        PaymentsPageDto paymentPage = new PaymentsPageDto();
        paymentPage.setResults(paymentResults);

        paymentPage.setCurrentPage(resultPage.getNumber() + 1);
        paymentPage.setPageSize(resultPage.getSize());

        paymentPage.setTotalPages(resultPage.getTotalPages());
        paymentPage.setTotalElements(resultPage.getTotalElements());

        paymentPage.setSortColumn(sortColumn);
        paymentPage.setSortDirection(sortDirection);

        return paymentPage;
    }

    @Override
    public OwnershipUpdatePaymentsPageDto getOwnershipUpdatePayments(Integer pageNumber, Integer pageSize, PaymentSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId) {

        LOGGER.info("Get a Page of Ownership Update Payments");

        String sortDtoColumn = getPaymentSortColumn(sortColumn);
        OwnershipUpdatePaymentsPageDto paymentResults = new OwnershipUpdatePaymentsPageDto();

        Optional<OwnershipUpdate> ouResult = checkOwnershipUpdateExists(BigDecimal.valueOf(ownershipUpdateId));
        OwnershipUpdate ou = ouResult.get();

        Sort.Direction sortDtoDirection = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber -1, pageSize, Sort.by(sortDtoDirection,sortDtoColumn).and(Sort.by(Sort.Direction.DESC, "trackingNo")));
        Page<Payment> resultPage = payRepo.findByOwnershipUpdateId(pageable, BigDecimal.valueOf(ownershipUpdateId));

        paymentResults.setResults(resultPage.getContent().stream().map(payment -> {
            return getPaymentDetailDto(payment);
        }).collect(Collectors.toList()));

        paymentResults.setCurrentPage(resultPage.getNumber() + 1);
        paymentResults.setPageSize(resultPage.getSize());

        paymentResults.setTotalPages(resultPage.getTotalPages());
        paymentResults.setTotalElements(resultPage.getTotalElements());

        paymentResults.setSortColumn(sortColumn);
        paymentResults.setSortDirection(sortDirection);

        return paymentResults;
    }



    @Transactional
    @Override
    public PaymentSummaryDto updatePaymentSummary(Long applicationId, PaymentSummaryDto summaryDto) {
        LOGGER.info("Updating the Payment Summary");

        if("Y".equals(summaryDto.getFeeWaived()) && summaryDto.getFeeWaivedReason() == null) {
            throw new ValidationException("A Reason is required when waiving the fee");
        }

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = appRepo.findApplicationsWithType(appId);
        if(!foundApp.isPresent()) {
            throw new NotFoundException("The Application id " + applicationId + " was not found");
        }
        Application app = foundApp.get();

        // make sure only one is Yes
        List<String> feeModifiers = Arrays.asList(summaryDto.getFeeCGWA() != null ? summaryDto.getFeeCGWA() : app.getFeeCGWA(),
                                                    summaryDto.getFeeWaived() != null ? summaryDto.getFeeWaived() : app.getFeeWaived(),
                                                    summaryDto.getFeeOther() != null ? summaryDto.getFeeOther() : app.getFeeOther(),
                                                    summaryDto.getFeeDiscount() != null ? summaryDto.getFeeDiscount() : app.getFeeDiscount());
        int feeOptionCount = feeModifiers.stream().map(yesno -> {
            return (yesno.equals("Y")) ? 1 : 0;
        }).collect(Collectors.summingInt(Integer::intValue));
        
        if(feeOptionCount > 1) {
            throw new ValidationException("Can only set one option to Yes at a time");
        }

        if("Y".equals(summaryDto.getFeeDiscount()) && (app.getType().getFeeDiscount() == null || app.getType().getFeeDiscount().equals(BigDecimal.ZERO))) {
            throw new ValidationException("Cannot discount the fee for this Application Type");
        }
        if("Y".equals(summaryDto.getFeeOther()) && (app.getType().getFeeOther() == null || app.getType().getFeeOther().equals(BigDecimal.ZERO))) {
            throw new ValidationException("Cannot use Fee Other for this Application Type");
        }
        if("Y".equals(summaryDto.getFeeCGWA()) && (app.getType().getFeeCGWA() == null || app.getType().getFeeCGWA().equals(BigDecimal.ZERO))) {
            throw new ValidationException("Cannot use Fee CGWA for this Application Type");
        }

        if(summaryDto.getFeeWaived() != null) app.setFeeWaived(summaryDto.getFeeWaived());
        if(summaryDto.getFeeCGWA() != null) app.setFeeCGWA(summaryDto.getFeeCGWA());
        if(summaryDto.getFeeOther() != null) app.setFeeOther(summaryDto.getFeeOther());
        if(summaryDto.getFeeDiscount() != null) app.setFeeDiscount(summaryDto.getFeeDiscount());
        
        BigDecimal filingFee = computeFilingFee(app);

        app.setFilingFee(filingFee);

        //update form status
        double feesPaid = payRepo.getFeesPaid(appId);
        Optional<Application> updatedApp = updateFeeStatus(app, feesPaid);
        if(updatedApp.isPresent()){
            appRepo.save(updatedApp.get());
        }
        if("Y".equals(summaryDto.getFeeWaived()) && summaryDto.getFeeWaivedReason() != null) {
            Optional<Event> foundFRMR = appRepo.getFormReceivedEvent(appId);
            if(!foundFRMR.isPresent()) {
                throw new DataConflictException("Application forms must have been received to waive the fee");
            }
            Event frmr = foundFRMR.get();
            String oldComment = frmr.getEventComment();
            if(oldComment != null && oldComment.length() > 0) {
                frmr.setEventComment(oldComment + "; " + summaryDto.getFeeWaivedReason());
            } else {
                frmr.setEventComment(summaryDto.getFeeWaivedReason());
            }
            eventRepo.save(frmr);
        }

        double totalDue = app.getFilingFee().doubleValue() - feesPaid;
        PaymentSummaryDto dto = new PaymentSummaryDto();
        dto.setFeeStatus(app.getFeeStatus());
        dto.setFeeDue(filingFee.doubleValue());
        dto.setTotalDue(totalDue);
        dto.setAmountPaid(feesPaid);
        dto.setFeeCGWA(summaryDto.getFeeCGWA());
        dto.setFeeDiscount(summaryDto.getFeeDiscount());
        dto.setFeeOther(summaryDto.getFeeOther());
        dto.setFeeWaived(summaryDto.getFeeWaived());

        return dto;
    }

    public BigDecimal computeFilingFee(Application app) {
        BigDecimal filingFee = app.getType().getFilingFee();

        if("Y".equals(app.getFeeWaived())) {
            filingFee = BigDecimal.valueOf(0);
        } else if("Y".equals(app.getFeeCGWA())) {
            filingFee = app.getType().getFeeCGWA();
        } else if("Y".equals(app.getFeeOther())) {
            filingFee = app.getType().getFeeOther();
        } else if("Y".equals(app.getFeeDiscount())) {
            if (app.getType().getFeeDiscount() != null) filingFee = filingFee.subtract(app.getType().getFeeDiscount());
        }

        if (filingFee == null) filingFee = BigDecimal.ZERO;

        BigDecimal eventFees = appRepo.getFeesFromEvents(app.getId());
        if(eventFees != null) filingFee = filingFee.add(eventFees);

        return filingFee;
    }

    @Transactional
    @Override
    public PaymentDto createPayment(Long applicationId, PaymentDto paymentDto) {
        LOGGER.info("Creating a new Payment");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = checkApplicationExists(appId);
        Application app = foundApp.get();

        if (paymentDto.getDatePaid() == null) {
            paymentDto.setDatePaid(app.getDateTimeReceivedEvent().getEventDate().toLocalDate());
        }

        validatePaymentDto(paymentDto, true, null);

        //update fee status
        double feesPaid = payRepo.getFeesPaid(appId) + paymentDto.getAmountPaid();
        Optional<Application> updatedApp = updateFeeStatus(app, feesPaid);
        if(updatedApp.isPresent()){
            appRepo.save(updatedApp.get());
        }

        Payment newPayment = getPayment(paymentDto);
        newPayment.setApplicationId(BigDecimal.valueOf(applicationId));

        newPayment = payRepo.save(newPayment);

        return getPaymentDetailDto(newPayment);
    }

    @Override
    public PaymentDto createOwnershipUpdatePayment(Long ownershipUpdateId, PaymentDto paymentDto) {
        LOGGER.info("Creating a new Payment");

        Optional<OwnershipUpdate> ouResult = checkOwnershipUpdateExists(BigDecimal.valueOf(ownershipUpdateId));
        OwnershipUpdate ou = ouResult.get();

        validatePaymentDto(paymentDto, true, null);

        double feesPaid = payRepo.getFeesPaidForOwnershipUpdate(BigDecimal.valueOf(ownershipUpdateId)) + paymentDto.getAmountPaid();
        Optional<OwnershipUpdate> updatedOu = updateFeeStatusForOwnershipUpdate(ou, feesPaid);
        if(updatedOu.isPresent()){
            ouRepo.save(updatedOu.get());
        }

        Payment newPayment = getPayment(paymentDto);
        newPayment.setOwnershipUpdateId(BigDecimal.valueOf(ownershipUpdateId));

        newPayment = payRepo.save(newPayment);

        return getPaymentDetailDto(newPayment);

    }


    private Optional<OwnershipUpdate> updateFeeStatusForOwnershipUpdate(OwnershipUpdate ou, double feesPaid) {

        double totalDue = 0;

        if (ou.getFeeDue() == null) {
            totalDue = 0 - feesPaid;
        } else {
            totalDue = ou.getFeeDue().doubleValue() - feesPaid;
        }

        if(totalDue <= 0 && !"FULL".equals(ou.getFeeStatus())) {
            ou.setFeeStatus("FULL");
        } else if (totalDue > 0 && feesPaid > 0 && !"PARTIAL".equals(ou.getFeeStatus())) {
            ou.setFeeStatus("PARTIAL");
        } else if (totalDue > 0 && feesPaid == 0) {
            ou.setFeeStatus("NONE");
        }

        return Optional.ofNullable(ou);

    }

    @Transactional
    @Override
    public PaymentDto updatePayment(Long applicationId, Long paymentId, PaymentDto paymentDto) {
        LOGGER.info("Updating a Payment");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = checkApplicationExists(appId);
        Application app = foundApp.get();

        Optional<Payment> foundPayment = checkPaymentExists(BigDecimal.valueOf(paymentId));
        Payment payment = foundPayment.get();

        validatePaymentDto(paymentDto, false, payment);

        //update fee status
        double feesPaid = payRepo.getFeesPaid(appId) + paymentDto.getAmountPaid() - payment.getAmount().doubleValue();
        Optional<Application> updatedApp = updateFeeStatus(app, feesPaid);
        if(updatedApp.isPresent()){
            appRepo.save(updatedApp.get());
        }

        Payment newPayment = updatePayment(paymentDto, payment);
        newPayment.setApplicationId(BigDecimal.valueOf(applicationId));

        newPayment = payRepo.save(newPayment);

        return getPaymentDetailDto(newPayment);
    }

    @Override
    public PaymentDto updateOwnershipUpdatePayment(Long ownershipUpdateId, Long paymentId, PaymentDto paymentDto) {
        LOGGER.info("Updating a Payment");

        Optional<OwnershipUpdate> ouResult = checkOwnershipUpdateExists(BigDecimal.valueOf(ownershipUpdateId));
        OwnershipUpdate ou = ouResult.get();

        Optional<Payment> foundPayment = checkPaymentExists(BigDecimal.valueOf(paymentId));
        Payment payment = foundPayment.get();

        validatePaymentDto(paymentDto, false, payment);

        double feesPaid = payRepo.getFeesPaidForOwnershipUpdate(BigDecimal.valueOf(ownershipUpdateId)) + paymentDto.getAmountPaid() - payment.getAmount().doubleValue();
        Optional<OwnershipUpdate> updatedOu = updateFeeStatusForOwnershipUpdate(ou, feesPaid);
        if(updatedOu.isPresent()){
            ouRepo.save(updatedOu.get());
        }

        Payment newPayment = updatePayment(paymentDto, payment);
        newPayment.setOwnershipUpdateId(BigDecimal.valueOf(ownershipUpdateId));

        newPayment = payRepo.save(newPayment);

        return getPaymentDetailDto(newPayment);
    }

    @Override
    @Transactional
    public void deletePayment(Long applicationId, Long paymentId) {
        LOGGER.info("Deleting a Payment");

        BigDecimal appId = BigDecimal.valueOf(applicationId);
        Optional<Application> foundApp = checkApplicationExists(appId);
        Application app = foundApp.get();

        Optional<Payment> foundPayment = checkPaymentExists(BigDecimal.valueOf(paymentId));
        Payment payment = foundPayment.get();

        //update fee status
        double feesPaid = payRepo.getFeesPaid(appId) - payment.getAmount().doubleValue();
        Optional<Application> updatedApp = updateFeeStatus(app, feesPaid);
        if(updatedApp.isPresent()){
            appRepo.save(updatedApp.get());
        }

        payRepo.delete(payment);
    }

    @Override
    public void deleteOwnershipUpdatePayment(Long ownershipUpdateId, Long paymentId) {

        LOGGER.info("Deleting a Payment");

        Optional<OwnershipUpdate> ouResult = checkOwnershipUpdateExists(BigDecimal.valueOf(ownershipUpdateId));
        OwnershipUpdate ou = ouResult.get();

        Optional<Payment> foundPayment = checkPaymentExists(BigDecimal.valueOf(paymentId));
        Payment payment = foundPayment.get();

        double feesPaid = payRepo.getFeesPaidForOwnershipUpdate(BigDecimal.valueOf(ownershipUpdateId)) - payment.getAmount().doubleValue();
        Optional<OwnershipUpdate> updatedOu = updateFeeStatusForOwnershipUpdate(ou, feesPaid);
        if(updatedOu.isPresent()){
            ouRepo.save(updatedOu.get());
        }

        payRepo.delete(payment);

    }

    // return the application if it needs to be updated
    private Optional<Application> updateFeeStatus(Application app, double feesPaid) {
        double totalDue = app.getFilingFee().doubleValue() - feesPaid;

        if(totalDue > 0 && feesPaid <= 0 && !app.getFeeStatus().equals("NONE")) {
            app.setFeeStatus("NONE");
        }
        if(totalDue > 0 && feesPaid > 0 && !app.getFeeStatus().equals("PARTIAL")) {
            app.setFeeStatus("PARTIAL");
        }
        if(totalDue <= 0 && !app.getFeeStatus().equals("FULL")) {
            app.setFeeStatus("FULL");
        }
        return Optional.ofNullable(app);
    }

    private Payment updatePayment(PaymentDto dto, Payment model) {
        model.setAmount(BigDecimal.valueOf(dto.getAmountPaid()));
        model.setDate(dto.getDatePaid());
        model.setOrigin(dto.getOrigin());
        model.setTrackingNo(dto.getTrackingNumber());
        return model;
    }

    private Payment getPayment(PaymentDto dto) {
        Payment model = new Payment();
        model.setAmount(BigDecimal.valueOf(dto.getAmountPaid()));
        model.setDate(dto.getDatePaid());
        model.setOrigin(dto.getOrigin());
        model.setTrackingNo(dto.getTrackingNumber());
        return model;
    }

    private PaymentDto getPaymentDetailDto(Payment model) {
        PaymentDto dto = new PaymentDto();
        double amountPaid = (model.getAmount() != null) ? model.getAmount().doubleValue() : 0;
        dto.setAmountPaid(amountPaid);
        dto.setDatePaid(model.getDate());
        dto.setOrigin(model.getOrigin());
        if(model.getOriginReference() != null) dto.setOriginDescription(model.getOriginReference().getMeaning());
        dto.setTrackingNumber(model.getTrackingNo());
        dto.setPaymentId(model.getId().longValue());
        return dto;
    }

    private String getPaymentSortColumn(PaymentSortColumn sortColumn) {
        if(sortColumn == PaymentSortColumn.AMOUNTPAID) {
            return "amount";
        } else if(sortColumn == PaymentSortColumn.DATEPAID) {
            return "date";
        } else if(sortColumn == PaymentSortColumn.ORIGIN) {
            return "origin";
        } else {
            return "trackingNo";
        }
    }

    public PaymentDto toUpperCase(PaymentDto dto) {
        PaymentDto newDto = new PaymentDto();
        newDto.setPaymentId(dto.getPaymentId());
        newDto.setTrackingNumber((dto.getTrackingNumber() != null) ? dto.getTrackingNumber().toUpperCase() : null);
        newDto.setOrigin((dto.getOrigin() != null) ? dto.getOrigin().toUpperCase() : null);
        newDto.setDatePaid(dto.getDatePaid());
        newDto.setAmountPaid(dto.getAmountPaid());
        return newDto;
    }

    public PaymentSummaryDto toUpperCase(PaymentSummaryDto dto) {
        if(dto.getFeeWaivedReason() != null) {
            dto.setFeeWaivedReason(dto.getFeeWaivedReason().toUpperCase());
        }
        return dto;
    }
}
