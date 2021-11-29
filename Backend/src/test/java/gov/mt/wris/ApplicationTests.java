package gov.mt.wris;

import static gov.mt.wris.util.ApplicationFactory.dto;
import static org.junit.jupiter.api.Assertions.*;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
import gov.mt.wris.util.ApplicantFactory;
import gov.mt.wris.util.ApplicationFactory;
import gov.mt.wris.util.EventsFactory;
import gov.mt.wris.util.PaymentFactory;
import gov.mt.wris.util.RepresentativeFactory;
import gov.mt.wris.util.TestClient;
import gov.mt.wris.util.TestEnvironment;
import gov.mt.wris.util.ThrowableConsumer;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestEnvironment
public class ApplicationTests {

    @Autowired
    TestClient client;

    @Autowired
    ApplicationFactory appFactory;

    @Autowired
    EventsFactory eventsFactory;

    @Autowired
    PaymentFactory paymentFactory;

    private ApplicantDto getFirstApplicantOnApplication(
        ApplicationDto application
    ) throws Exception {
        return client
            .get(
                "/api/v1/applications/{id}/applicants",
                application.getApplicationId()
            )
            .exchange()
            .parse(ApplicantsPageDto.class)
            .getResults()
            .get(0);
    }

    private PaymentSummaryDto updatePaymentSummaryOnApplication(
        ApplicationDto application,
        PaymentSummaryDto summary
    ) throws Exception {
        return client
            .put(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(summary)
            .exchange()
            .parse(PaymentSummaryDto.class);
    }

    @Test
    public void createApplication_withRequiredData_isCreated()
        throws Exception {
        client
            .post("/api/v1/applications")
            .body(dto())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ApplicationDto.class);
    }

    @Test
    public void createApplication_whenCreated_hasEvents() throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .get(
                "/api/v1/applications/{id}/events",
                application.getApplicationId()
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(EventsPageDto.class)
            .with(
                EventsPageDto.class,
                events -> assertFalse(events.getResults().isEmpty())
            );
    }

    @Test
    public void createApplication_whenTypeRequiresPayment_hasFeeDue()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("602")
        );

        client
            .get(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PaymentsPageDto.class)
            .with(
                PaymentsPageDto.class,
                payments -> {
                    assertEquals(
                        125.0,
                        payments.getResults().getSummary().getFeeDue()
                    );
                    assertEquals(
                        "NONE",
                        payments.getResults().getSummary().getFeeStatus()
                    );
                }
            );
    }

    @Test
    public void createApplication_whenGivenDisallowedType_isConflict()
        throws Exception {
        Constants.DISALLOWED_APPLICATION_TYPES.forEach(
            (ThrowableConsumer<String>) type ->
                client
                    .post("/api/v1/applications")
                    .body(
                        new ApplicationCreationDto()
                            .basin("43D")
                            .applicationTypeCode(type)
                            .contactIds(Arrays.asList(387065L, 160476L))
                            .dateTimeReceived(
                                LocalDateTime.of(2021, 1, 1, 10, 30)
                            )
                    )
                    .exchange()
                    .expectStatus()
                    .isConflict()
        );
    }

    @Test
    public void createApplication_withoutApplicationTypeCode_isBadRequest()
        throws Exception {
        client
            .post("/api/v1/applications")
            .body(
                new ApplicationCreationDto()
                    .basin("43D")
                    .contactIds(Arrays.asList(387065L, 160476L))
                    .dateTimeReceived(LocalDateTime.of(2021, 1, 1, 10, 30))
            )
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createApplication_withoutContactIds_isConflict()
        throws Exception {
        client
            .post("/api/v1/applications")
            .body(
                new ApplicationCreationDto()
                    .basin("43D")
                    .applicationTypeCode("600")
                    .dateTimeReceived(LocalDateTime.of(2021, 1, 1, 10, 30))
            )
            .exchange()
            .expectStatus()
            .isConflict();
    }

    @Test
    public void createApplication_withoutDateTimeReceived_isBadRequest()
        throws Exception {
        client
            .post("/api/v1/applications")
            .body(
                new ApplicationCreationDto()
                    .applicationTypeCode("600")
                    .contactIds(Arrays.asList(387065L, 160476L))
            )
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void changeApplication_whenConvertingPTypeToNonPType_isOk()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606P")
        );

        client
            .put("/api/v1/applications/{id}", application.getApplicationId())
            .body(
                new ApplicationUpdateDto()
                    .applicationTypeCode("606")
                    .basin(application.getBasin())
                    .dateTimeReceived(LocalDateTime.of(2021, 1, 1, 10, 30))
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ApplicationDto.class);
    }

    @Test
    public void changeApplication_whenConvertingPTypeToNonMatchingNonPType_isConflict()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606P")
        );

        client
            .put("/api/v1/applications/{id}", application.getApplicationId())
            .body(
                new ApplicationUpdateDto()
                    .applicationTypeCode("602")
                    .basin(application.getBasin())
                    .dateTimeReceived(LocalDateTime.of(2021, 1, 1, 10, 30))
            )
            .exchange()
            .expectStatus()
            .isConflict();
    }

    @Test
    public void changeApplication_whenConvertingNonPTypeToPType_isConflict()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606")
        );

        client
            .put("/api/v1/applications/{id}", application.getApplicationId())
            .body(
                new ApplicationUpdateDto()
                    .applicationTypeCode("606P")
                    .basin(application.getBasin())
                    .dateTimeReceived(LocalDateTime.of(2021, 1, 1, 10, 30))
            )
            .exchange()
            .expectStatus()
            .isConflict();
    }

    @Test
    public void changeApplication_whenDateTimeReceivedIsInTheFuture_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606P")
        );

        client
            .put("/api/v1/applications/{id}", application.getApplicationId())
            .body(
                new ApplicationUpdateDto()
                    .applicationTypeCode("606")
                    .basin(application.getBasin())
                    .dateTimeReceived(LocalDateTime.of(2051, 1, 1, 10, 30))
            )
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createApplicant_withRequiredData_isCreated() throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/applicants",
                application.getApplicationId()
            )
            .body(ApplicantFactory.dto())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ApplicantDto.class);
    }

    @Test
    public void deleteApplicant_whenOnPTypeAndWithOtherApplicants_isNoContent()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto()
                .applicationTypeCode("606P")
                .contactIds(Arrays.asList(387065L, 160476L))
        );
        ApplicantDto applicant = getFirstApplicantOnApplication(application);

        client
            .delete(
                "/api/v1/applications/{id}/applicants/{owner}",
                application.getApplicationId(),
                applicant.getOwnerId()
            )
            .exchange()
            .expectStatus()
            .isNoContent()
            .expectBody(Void.class);
    }

    @Test
    public void createRepresentative_withRequiredData_isCreated()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = getFirstApplicantOnApplication(application);

        client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(RepresentativeFactory.dto())
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RepresentativeDto.class);
    }

    @Test
    public void createEvent_withRequiredData_isCreated() throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/events",
                application.getApplicationId()
            )
            .body(EventsFactory.dto().event("MODR"))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EventsDto.class);
    }

    @Test
    public void createEvent_whenAddingReinAndTdorOnType606_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606")
        );
        eventsFactory.create(application, EventsFactory.dto().event("TDOR"));

        client
            .post(
                "/api/v1/applications/{id}/events",
                application.getApplicationId()
            )
            .body(EventsFactory.dto().event("REIN"))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void updateChange_onType634_isOk() throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("634")
        );

        client
            .put(
                "/api/v1/applications/{id}/change",
                application.getApplicationId()
            )
            .body(new ChangeDto())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ChangeDto.class);
    }

    @Test
    public void updateChange_onType635_isOk() throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("635")
        );

        client
            .put(
                "/api/v1/applications/{id}/change",
                application.getApplicationId()
            )
            .body(new ChangeDto())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ChangeDto.class);
    }

    @Test
    public void updateChange_onOtherType_isOk() throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606")
        );

        client
            .put(
                "/api/v1/applications/{id}/change",
                application.getApplicationId()
            )
            .body(new ChangeDto())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ChangeDto.class);
    }

    @Test
    public void updateChange_whenProvidedDescription_convertsTextToUppercase()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606")
        );

        client
            .put(
                "/api/v1/applications/{id}/change",
                application.getApplicationId()
            )
            .body(
                new ChangeDto()
                    .changeDescription("change description")
                    .pastUse("past use")
                    .additionalInformation("additional information")
            )
            .exchange()
            .with(
                ChangeDto.class,
                change -> {
                    assertEquals(
                        "CHANGE DESCRIPTION",
                        change.getChangeDescription()
                    );
                    assertEquals("PAST USE", change.getPastUse());
                    assertEquals(
                        "ADDITIONAL INFORMATION",
                        change.getAdditionalInformation()
                    );
                }
            );
    }

    @Test
    public void createPayment_withRequiredData_isCreated() throws Exception {
        ApplicationDto application = appFactory.create(
            dto()
                .applicationTypeCode("602")
                .dateTimeReceived(LocalDateTime.of(2020, 1, 1, 10, 30))
        );

        client
            .post(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(PaymentFactory.dto().amountPaid(350.0))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PaymentDto.class);
    }

    @Test
    public void createPayment_whenInsufficient_setsFeeStatusToPartial()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("602")
        );
        paymentFactory.create(application, PaymentFactory.dto().amountPaid(75.0));

        client
            .get(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .exchange()
            .with(
                PaymentsPageDto.class,
                payments -> {
                    PaymentSummaryDto summary = payments
                        .getResults()
                        .getSummary();
                    assertEquals(75.0, summary.getAmountPaid());
                    assertEquals(50.0, summary.getTotalDue());
                    assertEquals("PARTIAL", summary.getFeeStatus());
                }
            );
    }

    @Test
    public void createPayment_whenSufficient_setsFeeStatusToFull()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("602")
        );
        paymentFactory.create(application, PaymentFactory.dto().amountPaid(125.0));

        client
            .get(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .exchange()
            .with(
                PaymentsPageDto.class,
                payments ->
                    assertEquals(
                        "FULL",
                        payments.getResults().getSummary().getFeeStatus()
                    )
            );
    }

    @Test
    public void createPayment_whenDuplicateTrackingNumber_isConflict()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("602")
        );
        paymentFactory.create(application, PaymentFactory.dto().amountPaid(15.0));

        client
            .post(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(PaymentFactory.dto().amountPaid(30.0))
            .exchange()
            .expectStatus()
            .isConflict();
    }

    @Test
    public void getAllRelatedApplications_withExistingApplication_isNotEmpty()
        throws Exception {
        client
            .get("/api/v1/applications/{id}/related-applications", 8750100)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(RelatedApplicationPageDto.class)
            .with(
                RelatedApplicationPageDto.class,
                page -> assertFalse(page.getResults().isEmpty())
            );
    }

    @Test
    public void getNotices_withExistingApplication_isNotEmpty()
        throws Exception {
        client
            .get("/api/v1/applications/{id}/notices", 3235699)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ApplicationMailingJobPageDto.class)
            .with(
                ApplicationMailingJobPageDto.class,
                page -> assertFalse(page.getResults().isEmpty())
            );
    }

    @Test
    public void getWaterRightNotifications_withExistingApplication_isNotEmpty()
        throws Exception {
        client
            .get(
                "/api/v1/applications/{id}/notices/{notice}/water-right-notifications",
                3235699,
                469
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(WaterRightNotificationPageDto.class)
            .with(
                WaterRightNotificationPageDto.class,
                page -> assertFalse(page.getResults().isEmpty())
            );
    }

    @Test
    public void getOtherNotifications_withExistingApplication_isNotEmpty()
        throws Exception {
        client
            .get(
                "/api/v1/applications/{id}/notices/{notice}/other-notifications",
                3235699,
                469
            )
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(OtherNotificationPageDto.class)
            .with(
                OtherNotificationPageDto.class,
                page -> assertFalse(page.getResults().isEmpty())
            );
    }

    @Test
    public void createEvent_whenEventHasFee_increasesFeeDue() throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606")
        );
        eventsFactory.create(application, EventsFactory.dto().event("MODR"));

        client
            .get(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .exchange()
            .with(
                PaymentsPageDto.class,
                payments ->
                    assertEquals(
                        1300.0,
                        payments.getResults().getSummary().getFeeDue()
                    )
            );
    }

    @Test
    public void deleteEvent_whenEventHasFee_decreasesFeeDue() throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606")
        );
        EventsDto event = eventsFactory.create(application, EventsFactory.dto().event("MODR"));

        client
            .delete(
                "/api/v1/applications/{id}/events/{event}",
                application.getApplicationId(),
                event.getEventId()
            )
            .exchange()
            .expectStatus()
            .isNoContent();

        client
            .get(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .exchange()
            .with(
                PaymentsPageDto.class,
                payments ->
                    assertEquals(
                        900.0,
                        payments.getResults().getSummary().getFeeDue()
                    )
            );
    }

    @Test
    public void updatePaymentSummary_whenWaivingAnApplicationWithEventFees_eventFeeIsNotWaived()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606")
        );
        eventsFactory.create(application, EventsFactory.dto().event("RERD"));

        PaymentSummaryDto summary = client
            .get(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .exchange()
            .parse(PaymentsPageDto.class)
            .getResults()
            .getSummary()
            .feeWaived("Y")
            .feeWaivedReason("The applicant asked very politely");

        client
            .put(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(summary)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PaymentSummaryDto.class)
            .with(
                PaymentSummaryDto.class,
                result -> assertEquals(200.0, result.getFeeDue())
            );
    }

    @Test
    public void updatePaymentSummary_whenWaivingAnApplicationMultipleTimes_savesReasonHistoryInEventComments()
        throws Exception {
        ApplicationDto application = appFactory.create(
            dto().applicationTypeCode("606")
        );
        PaymentSummaryDto summary = new PaymentSummaryDto()
            .feeCGWA("N")
            .feeOther("N")
            .feeDiscount("N");

        updatePaymentSummaryOnApplication(
            application,
            summary.feeWaived("Y").feeWaivedReason("First")
        );

        updatePaymentSummaryOnApplication(application, summary.feeWaived("N"));

        updatePaymentSummaryOnApplication(
            application,
            summary.feeWaived("Y").feeWaivedReason("Second")
        );

        client
            .get(
                "/api/v1/applications/{id}/events",
                application.getApplicationId()
            )
            .exchange()
            .with(
                EventsPageDto.class,
                page ->
                    assertEquals(
                        "FIRST; SECOND",
                        page.getResults().get(0).getComments()
                    )
            );
    }
}
