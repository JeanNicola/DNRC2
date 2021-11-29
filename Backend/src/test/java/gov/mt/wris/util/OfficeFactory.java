package gov.mt.wris.util;

import gov.mt.wris.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "officeFactory")
public class OfficeFactory {

    @Autowired
    TestClient client;

    public static OfficeCreationDto dto() {
        return new OfficeCreationDto().officeId(4L);
    }

    public OfficeDto create(ApplicationDto application) throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/locations",
                application.getApplicationId()
            )
            .body(dto())
            .exchange()
            .parse(OfficeDto.class);
    }

    public OfficeDto create(ApplicationDto application, OfficeCreationDto body)
        throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/locations",
                application.getApplicationId()
            )
            .body(body)
            .exchange()
            .parse(OfficeDto.class);
    }

    public OfficeDto create(OwnershipUpdateSearchResultDto ownershipUpdate)
        throws Exception {
        return client
            .post(
                "/api/v1/ownership-updates/{id}/locations",
                ownershipUpdate.getOwnershipUpdateId()
            )
            .body(dto())
            .exchange()
            .parse(OfficeDto.class);
    }

    public OfficeDto create(
        OwnershipUpdateSearchResultDto ownershipUpdate,
        OfficeCreationDto body
    ) throws Exception {
        return client
            .post(
                "/api/v1/ownership-updates/{id}/locations",
                ownershipUpdate.getOwnershipUpdateId()
            )
            .body(body)
            .exchange()
            .parse(OfficeDto.class);
    }
}
