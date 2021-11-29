package gov.mt.wris.util;

import gov.mt.wris.dtos.*;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventsFactory {

    @Autowired
    TestClient client;

    public static EventsDto dto() {
        return new EventsDto()
            .event("FRMR")
            .dateTime(LocalDateTime.of(2021, 1, 1, 10, 30));
    }

    public EventsDto create(ApplicationDto application) throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/events",
                application.getApplicationId()
            )
            .body(dto())
            .exchange()
            .parse(EventsDto.class);
    }

    public EventsDto create(ApplicationDto application, EventsDto event)
        throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/events",
                application.getApplicationId()
            )
            .body(event)
            .exchange()
            .parse(EventsDto.class);
    }
}
