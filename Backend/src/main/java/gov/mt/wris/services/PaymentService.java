package gov.mt.wris.services;

import gov.mt.wris.dtos.*;
import gov.mt.wris.models.Application;

import java.math.BigDecimal;

public interface PaymentService {
    public PaymentsPageDto getPaymentPage(Integer pageNumber, Integer pageSize, PaymentSortColumn sortColumn, SortDirection sortDirection, Long applicationId);

    public OwnershipUpdatePaymentsPageDto getOwnershipUpdatePayments(Integer pageNumber, Integer pageSize, PaymentSortColumn sortColumn, SortDirection sortDirection, Long ownershipUpdateId);

    public PaymentDto createPayment(Long applicationId, PaymentDto paymentDto);

    public PaymentDto createOwnershipUpdatePayment(Long ownershipUpdateId, PaymentDto paymentDto);

    public PaymentDto updatePayment(Long applicationId, Long paymentId, PaymentDto paymentDto);

    public PaymentDto updateOwnershipUpdatePayment(Long ownershipUpdateId, Long paymentId, PaymentDto paymentDto);

    public void deletePayment(Long applicationId, Long paymentId);

    public void deleteOwnershipUpdatePayment(Long ownershipUpdateId, Long paymentId);

    public PaymentSummaryDto updatePaymentSummary(Long applicationId, PaymentSummaryDto summaryDto);

    public BigDecimal computeFilingFee(Application app);
    
	/**
	 * @param dto
	 * @return
	 */
	public PaymentDto toUpperCase(PaymentDto dto);
	public PaymentSummaryDto toUpperCase(PaymentSummaryDto dto);
}
