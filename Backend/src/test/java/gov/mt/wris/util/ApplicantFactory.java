package gov.mt.wris.util;

import gov.mt.wris.dtos.*;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicantFactory {

    @Autowired
    TestClient client;

    public static ApplicantDto dto() {
        return new ApplicantDto()
            .contactId(160476L)
            .beginDate(LocalDate.of(2021, 1, 1));
    }

    public ApplicantDto create(ApplicationDto application) throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/applicants",
                application.getApplicationId()
            )
            .body(dto())
            .exchange()
            .parse(ApplicantDto.class);
    }

    public ApplicantDto create(
        ApplicationDto application,
        ApplicantDto applicant
    ) throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/applicants",
                application.getApplicationId()
            )
            .body(applicant)
            .exchange()
            .parse(ApplicantDto.class);
    }
}
