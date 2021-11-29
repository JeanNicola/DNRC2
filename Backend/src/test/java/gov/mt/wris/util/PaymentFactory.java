package gov.mt.wris.util;

import gov.mt.wris.dtos.*;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentFactory {

    @Autowired
    TestClient client;

    public static PaymentDto dto() {
        return new PaymentDto()
            .amountPaid(0.0)
            .trackingNumber("TrackingNumber")
            .datePaid(LocalDate.of(2021, 1, 1))
            .origin("TLMS");
    }

    public PaymentDto create(ApplicationDto application) throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(dto())
            .exchange()
            .parse(PaymentDto.class);
    }

    public PaymentDto create(ApplicationDto application, PaymentDto payment)
        throws Exception {
        return client
            .post(
                "/api/v1/applications/{id}/payments",
                application.getApplicationId()
            )
            .body(payment)
            .exchange()
            .parse(PaymentDto.class);
    }
}
