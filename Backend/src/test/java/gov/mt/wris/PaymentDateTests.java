package gov.mt.wris;

import static gov.mt.wris.util.PaymentFactory.dto;

import gov.mt.wris.dtos.*;
import gov.mt.wris.util.ApplicationFactory;
import gov.mt.wris.util.PaymentFactory;
import gov.mt.wris.util.TestClient;
import gov.mt.wris.util.TestEnvironment;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestEnvironment
public class PaymentDateTests {

    @Autowired
    TestClient client;

    @Autowired
    ApplicationFactory appFactory;

    @Autowired
    PaymentFactory paymentFactory;

    @Test
    public void createPayment_withoutDatePaid_isBadRequest() throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(dto().datePaid(null))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createPayment_whenDatePaidIsInTheFuture_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(dto().datePaid(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createPayment_whenDatePaidIsBeforeApplicationReceivedDate_isCreated()
        throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(dto().datePaid(LocalDate.of(2020, 1, 1)))
            .exchange()
            .expectStatus()
            .isCreated();
    }

    @Test
    public void updatePayment_whenDatePaidIsInTheFuture_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        PaymentDto payment = paymentFactory.create(application);

        client
            .put(
                "/api/v1/applications/{id}/payments/{payment}",
                application.getApplicationId(),
                payment.getPaymentId()
            )
            .body(dto().datePaid(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void updatePayment_whenDatePaidIsBeforeApplicationReceivedDate_isOk()
        throws Exception {
        ApplicationDto application = appFactory.create();
        PaymentDto payment = paymentFactory.create(application);

        client
            .put(
                "/api/v1/applications/{id}/payments/{payment}",
                application.getApplicationId(),
                payment.getPaymentId()
            )
            .body(dto().datePaid(LocalDate.of(2020, 1, 1)))
            .exchange()
            .expectStatus()
            .isOk();
    }
}
