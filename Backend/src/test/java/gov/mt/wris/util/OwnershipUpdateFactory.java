package gov.mt.wris.util;

import gov.mt.wris.dtos.*;
import gov.mt.wris.repositories.OwnershipUpdateOfficeRepository;
import gov.mt.wris.repositories.OwnershipUpdateStaffRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component(value = "ownershipUpdateFactory")
public class OwnershipUpdateFactory {

    @Autowired
    TestClient client;

    @Autowired
    EntityManager entityManager;

    @Autowired
    OwnershipUpdateOfficeRepository ownershipUpdateOfficeRepo;

    @Autowired
    OwnershipUpdateStaffRepository ownershipUpdateStaffRepo;

    public static OwnershipUpdateCreationDto dto() {
        return new OwnershipUpdateCreationDto()
            .ownershipUpdateType("DOR 608")
            .pendingDORValidation(false)
            .receivedAs608(false)
            .receivedDate(LocalDate.of(2021, 1, 1))
            .buyers(Arrays.asList(229249L))
            .sellers(Arrays.asList(311952L))
            .waterRights(Arrays.asList(129023L, 192135L, 287512L));
    }

    public void refresh(OwnershipUpdateSearchResultDto ownershipUpdate)
        throws Exception {
        Pageable paging = PageRequest.of(0, 2);
        ownershipUpdateOfficeRepo
            .findOwnershipUpdateOffices(
                paging,
                BigDecimal.valueOf(ownershipUpdate.getOwnershipUpdateId())
            )
            .stream()
            .forEach(entity -> entityManager.refresh(entity));
        ownershipUpdateStaffRepo
            .findOwnershipUpdateStaff(paging, BigDecimal.valueOf(ownershipUpdate.getOwnershipUpdateId()))
            .stream()
            .forEach(entity -> entityManager.refresh(entity));
    }

    public OwnershipUpdateSearchResultDto create() throws Exception {
        OwnershipUpdateSearchResultDto update = client
            .post("/api/v1/ownership-updates")
            .body(dto())
            .exchange()
            .parse(OwnershipUpdateSearchResultDto.class);
        refresh(update);
        return update;
    }

    public OwnershipUpdateSearchResultDto create(
        OwnershipUpdateCreationDto body
    ) throws Exception {
        OwnershipUpdateSearchResultDto update = client
            .post("/api/v1/ownership-updates")
            .body(body)
            .exchange()
            .parse(OwnershipUpdateSearchResultDto.class);
        refresh(update);
        return update;
    }
}
