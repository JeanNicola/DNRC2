package gov.mt.wris.util;

import gov.mt.wris.dtos.*;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "staffFactory")
public class StaffFactory {

    @Autowired
    TestClient client;

    public static StaffCreationDto dto() {
        return new StaffCreationDto()
            .staffId(526L)
            .beginDate(LocalDate.of(2021, 1, 1));
    }

    public StaffDto create(ApplicationDto application) throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/staff",
                application.getApplicationId()
            )
            .body(dto())
            .exchange()
            .parse(StaffDto.class);
    }

    public StaffDto create(ApplicationDto application, StaffCreationDto body)
        throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/staff",
                application.getApplicationId()
            )
            .body(body)
            .exchange()
            .parse(StaffDto.class);
    }

    public StaffDto create(OwnershipUpdateSearchResultDto ownershipUpdate)
        throws Exception {
        return client
            .post(
                "/api/v1/ownership-updates/{id}/staff",
                ownershipUpdate.getOwnershipUpdateId()
            )
            .body(dto())
            .exchange()
            .parse(StaffDto.class);
    }

    public StaffDto create(
        OwnershipUpdateSearchResultDto ownershipUpdate,
        StaffCreationDto body
    ) throws Exception {
        return client
            .post(
                "/api/v1/ownership-updates/{id}/staff",
                ownershipUpdate.getOwnershipUpdateId()
            )
            .body(body)
            .exchange()
            .parse(StaffDto.class);
    }
}
