package gov.mt.wris;

import static gov.mt.wris.util.OfficeFactory.dto;

import gov.mt.wris.dtos.*;
import gov.mt.wris.util.OfficeFactory;
import gov.mt.wris.util.OwnershipUpdateFactory;
import gov.mt.wris.util.TestClient;
import gov.mt.wris.util.TestEnvironment;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestEnvironment
public class OfficeDateTests {

    @Autowired
    TestClient client;

    @Autowired
    OwnershipUpdateFactory ownershipUpdateFactory;

    @Autowired
    OfficeFactory officeFactory;

    private void setSentDateOnCurrentOffice(
        OwnershipUpdateSearchResultDto update
    ) throws Exception {
        OfficeDto office = getCurrentOffice(update);

        client
            .put(
                "/api/v1/ownership-updates/{id}/locations/{office}",
                update.getOwnershipUpdateId(),
                office.getId()
            )
            .body(office.sentDate(LocalDate.of(2021, 1, 1)))
            .exchange();
    }

    private OfficeDto getCurrentOffice(OwnershipUpdateSearchResultDto update)
        throws Exception {
        return client
            .get(
                "/api/v1/ownership-updates/{id}/locations",
                update.getOwnershipUpdateId()
            )
            .exchange()
            .parse(OfficePageDto.class)
            .getResults()
            .get(0);
    }

    @Test
    public void addOwnershipUpdateOffice_whenPreviousOfficeSentDateIsNull_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();

        client
            .post(
                "/api/v1/ownership-updates/{id}/locations",
                update.getOwnershipUpdateId()
            )
            .body(dto())
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void addOwnershipUpdateOffice_withoutReceivedDate_isCreated()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setSentDateOnCurrentOffice(update);

        client
            .post(
                "/api/v1/ownership-updates/{id}/locations",
                update.getOwnershipUpdateId()
            )
            .body(dto().receivedDate(null))
            .exchange()
            .expectStatus()
            .isCreated();
    }

    @Test
    public void addOwnershipUpdateOffice_whenReceivedDateIsInTheFuture_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setSentDateOnCurrentOffice(update);

        client
            .post(
                "/api/v1/ownership-updates/{id}/locations",
                update.getOwnershipUpdateId()
            )
            .body(dto().receivedDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateOffice_whenModifyingReceivedDate_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        OfficeDto office = getCurrentOffice(update);

        client
            .put(
                "/api/v1/ownership-updates/{id}/locations/{office}",
                update.getOwnershipUpdateId(),
                office.getId()
            )
            .body(dto().receivedDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateOffice_whenAddingSentDateBeforeReceivedDate_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setSentDateOnCurrentOffice(update);
        OfficeDto office = officeFactory.create(
            update,
            dto().receivedDate(LocalDate.of(2021, 1, 5))
        );

        client
            .put(
                "/api/v1/ownership-updates/{id}/locations/{office}",
                update.getOwnershipUpdateId(),
                office.getId()
            )
            .body(office.sentDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateOffice_whenAddingSentDateWhenReceivedDateIsNull_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setSentDateOnCurrentOffice(update);
        OfficeDto office = officeFactory.create(
            update,
            dto().receivedDate(null)
        );

        client
            .put(
                "/api/v1/ownership-updates/{id}/locations/{office}",
                update.getOwnershipUpdateId(),
                office.getId()
            )
            .body(office.sentDate(LocalDate.of(2021, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateOffice_whenAddingSentDateInTheFuture_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        OfficeDto office = getCurrentOffice(update);

        client
            .put(
                "/api/v1/ownership-updates/{id}/locations/{office}",
                update.getOwnershipUpdateId(),
                office.getId()
            )
            .body(office.sentDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateOffice_whenModifyingNonNullSentDate_isConflict()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setSentDateOnCurrentOffice(update);
        OfficeDto office = getCurrentOffice(update);

        client
            .put(
                "/api/v1/ownership-updates/{id}/locations/{office}",
                update.getOwnershipUpdateId(),
                office.getId()
            )
            .body(office.sentDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isConflict();
    }
}
