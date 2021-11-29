package gov.mt.wris;

import static gov.mt.wris.util.ApplicantFactory.dto;

import gov.mt.wris.dtos.*;
import gov.mt.wris.util.ApplicantFactory;
import gov.mt.wris.util.ApplicationFactory;
import gov.mt.wris.util.TestClient;
import gov.mt.wris.util.TestEnvironment;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestEnvironment
public class ApplicantDateTests {

    @Autowired
    TestClient client;

    @Autowired
    ApplicantFactory applicantFactory;

    @Autowired
    ApplicationFactory appFactory;

    @Test
    public void createApplicant_withoutBeginDate_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/applicants",
                application.getApplicationId()
            )
            .body(dto().beginDate(null))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createApplicant_withoutEndDate_isCreated() throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/applicants",
                application.getApplicationId()
            )
            .body(dto().endDate(null))
            .exchange()
            .expectStatus()
            .isCreated();
    }

    @Test
    public void createApplicant_whenEndDateIsBeforeBeginDate_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/applicants",
                application.getApplicationId()
            )
            .body(dto().endDate(LocalDate.of(2020, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createApplicant_whenEndDateIsInTheFuture_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();

        client
            .post(
                "/api/v1/applications/{id}/applicants",
                application.getApplicationId()
            )
            .body(dto().endDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void changeApplicant_whenReplacingNullEndDate_isOk()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}",
                application.getApplicationId(),
                applicant.getOwnerId()
            )
            .body(dto().endDate(LocalDate.of(2021, 1, 1)))
            .exchange()
            .expectStatus()
            .isOk();
    }

    @Test
    public void changeApplicant_whenEditingNonNullEndDate_isConflict()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(
            application,
            dto().endDate(LocalDate.of(2021, 1, 1))
        );

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}",
                application.getApplicationId(),
                applicant.getOwnerId()
            )
            .body(dto().endDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isConflict();
    }

    @Test
    public void changeApplicant_whenNewEndDateIsInTheFuture_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}",
                application.getApplicationId(),
                applicant.getOwnerId()
            )
            .body(dto().endDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void changeApplicant_whenNewEndDateIsBeforeBeginDate_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}",
                application.getApplicationId(),
                applicant.getOwnerId()
            )
            .body(dto().endDate(LocalDate.of(2020, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void changeApplicant_whenNewEndDateIsBeforeApplicationReceivedDate_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(
            application,
            dto().beginDate(LocalDate.of(2019, 1, 1))
        );

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}",
                application.getApplicationId(),
                applicant.getOwnerId()
            )
            .body(
                dto()
                    .beginDate(LocalDate.of(2019, 1, 1))
                    .endDate(LocalDate.of(2019, 1, 1))
            )
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createApplicant_whenAddingDuplicateOfApplicantWithEndDate_isCreated() throws Exception {
        ApplicationDto application = appFactory.create();
        applicantFactory.create(
            application,
            dto().endDate(LocalDate.of(2021, 1, 1))
        );

        client
            .post("/api/v1/applications/{id}/applicants", application.getApplicationId())
            .body(dto().beginDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isCreated();
    }
}
