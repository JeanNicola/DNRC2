package gov.mt.wris;

import static gov.mt.wris.util.RepresentativeFactory.dto;

import gov.mt.wris.dtos.*;
import gov.mt.wris.util.ApplicantFactory;
import gov.mt.wris.util.ApplicationFactory;
import gov.mt.wris.util.RepresentativeFactory;
import gov.mt.wris.util.TestClient;
import gov.mt.wris.util.TestEnvironment;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestEnvironment
public class RepresentativeDateTests {

    @Autowired
    TestClient client;

    @Autowired
    ApplicantFactory applicantFactory;

    @Autowired
    ApplicationFactory appFactory;

    @Autowired
    RepresentativeFactory representativeFactory;

    @Test
    public void createRepresentative_withoutBeginDate_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);

        client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(dto().beginDate(null))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createRepresentative_whenBeginDateIsInTheFuture_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);

        client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(dto().beginDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createRepresentative_whenBeginDateIsBeforeApplicantBeginDate_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(
            application,
            ApplicantFactory.dto().beginDate(LocalDate.of(2021, 1, 5))
        );

        client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(dto().beginDate(LocalDate.of(2021, 1, 4)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createRepresentative_whenEndDateIsBeforeApplicantBeginDate_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);

        client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(dto().endDate(LocalDate.of(2020, 12, 31)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void createRepresentative_whenEndDateIsInTheFuture_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);

        client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(dto().endDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void changeRepresentative_whenNewEndDateIsBeforeApplicantBeginDate_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);
        RepresentativeDto representative = representativeFactory.create(
            application,
            applicant
        );

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives/{rep}",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId(),
                representative.getRepresentativeId()
            )
            .body(dto().endDate(LocalDate.of(2020, 12, 31)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void changeRepresentative_whenNewEndDateIsInTheFuture_isBadRequest()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);
        RepresentativeDto representative = representativeFactory.create(
            application,
            applicant
        );

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives/{rep}",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId(),
                representative.getRepresentativeId()
            )
            .body(dto().endDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void changeRepresentative_whenReplacingNullEndDate_isOk()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);
        RepresentativeDto representative = representativeFactory.create(
            application,
            applicant
        );

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives/{rep}",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId(),
                representative.getRepresentativeId()
            )
            .body(dto().endDate(LocalDate.of(2021, 1, 1)))
            .exchange()
            .expectStatus()
            .isOk();
    }

    @Test
    public void changeRepresentative_whenEditingNonNullEndDate_isConflict()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);
        RepresentativeDto representative = representativeFactory.create(
            application,
            applicant,
            dto().endDate(LocalDate.of(2021, 1, 1))
        );

        client
            .put(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives/{rep}",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId(),
                representative.getRepresentativeId()
            )
            .body(dto().endDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isConflict();
    }

    @Test
    public void createRepresentative_whenAddingDuplicateOfRepresentativeWithEndDate_isCreated()
        throws Exception {
        ApplicationDto application = appFactory.create();
        ApplicantDto applicant = applicantFactory.create(application);
        representativeFactory.create(
            application,
            applicant,
            dto().endDate(LocalDate.of(2021, 1, 1))
        );

        client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(dto())
            .exchange()
            .expectStatus()
            .isCreated();
    }
}
