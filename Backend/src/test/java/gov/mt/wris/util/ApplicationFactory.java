package gov.mt.wris.util;

import gov.mt.wris.dtos.*;
import gov.mt.wris.models.Application;
import gov.mt.wris.repositories.ApplicationRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "appFactory")
public class ApplicationFactory {

    @Autowired
    TestClient client;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ApplicationRepository appRepo;

    public static ApplicationCreationDto dto() {
        return new ApplicationCreationDto()
            .basin("43D")
            .applicationTypeCode("600")
            .contactIds(new ArrayList<Long>(Arrays.asList(387065L)))
            .dateTimeReceived(LocalDateTime.of(2020, 1, 1, 10, 30));
    }

    public void refresh(ApplicationDto application) throws Exception {
        Optional<Application> entity = appRepo.getApplicationById(
            BigDecimal.valueOf(application.getApplicationId())
        );
        entityManager.refresh(entity.get());
    }

    public ApplicationDto create() throws Exception {
        ApplicationDto application = client
            .post("/api/v1/applications")
            .body(dto())
            .exchange()
            .parse(ApplicationDto.class);
        refresh(application);
        return application;
    }

    public ApplicationDto create(ApplicationCreationDto body) throws Exception {
        ApplicationDto application = client
            .post("/api/v1/applications")
            .body(body)
            .exchange()
            .parse(ApplicationDto.class);
        refresh(application);
        return application;
    }
}
