package gov.mt.wris.util;

import gov.mt.wris.dtos.*;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepresentativeFactory {

    @Autowired
    TestClient client;

    public static RepresentativeDto dto() {
        return new RepresentativeDto()
            .beginDate(LocalDate.of(2021, 1, 1))
            .contactId(219465L)
            .roleTypeCode("CSLT");
    }

    public RepresentativeDto create(
        ApplicationDto application,
        ApplicantDto applicant
    ) throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(dto())
            .exchange()
            .parse(RepresentativeDto.class);
    }

    public RepresentativeDto create(
        ApplicationDto application,
        ApplicantDto applicant,
        RepresentativeDto representative
    ) throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/applicants/{owner}/{contact}/representatives",
                application.getApplicationId(),
                applicant.getOwnerId(),
                applicant.getContactId()
            )
            .body(representative)
            .exchange()
            .parse(RepresentativeDto.class);
    }
}
