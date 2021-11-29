package gov.mt.wris;

import static gov.mt.wris.util.StaffFactory.dto;

import gov.mt.wris.dtos.*;
import gov.mt.wris.util.ApplicationFactory;
import gov.mt.wris.util.OwnershipUpdateFactory;
import gov.mt.wris.util.StaffFactory;
import gov.mt.wris.util.TestClient;
import gov.mt.wris.util.TestEnvironment;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestEnvironment
public class StaffDateTests {

    @Autowired
    TestClient client;

    @Autowired
    ApplicationFactory appFactory;

    @Autowired
    OwnershipUpdateFactory ownershipUpdateFactory;

    @Autowired
    StaffFactory staffFactory;

    private void setEndDateOnCurrentStaff(
        OwnershipUpdateSearchResultDto update
    ) throws Exception {
        StaffDto member = getCurrentStaff(update);

        client
            .put(
                "/api/v1/ownership-updates/{id}/staff/{member}",
                update.getOwnershipUpdateId(),
                member.getId()
            )
            .body(member.endDate(LocalDate.of(2021, 1, 1)))
            .exchange();
    }

    private StaffDto getCurrentStaff(OwnershipUpdateSearchResultDto update)
        throws Exception {
        return client
            .get(
                "/api/v1/ownership-updates/{id}/staff",
                update.getOwnershipUpdateId()
            )
            .exchange()
            .parse(StaffPageDto.class)
            .getResults()
            .get(0);
    }

    @Test
    public void addOwnershipUpdateStaff_whenPreviousStaffSentDateIsNull_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();

        client
            .post(
                "/api/v1/ownership-updates/{id}/staff",
                update.getOwnershipUpdateId()
            )
            .body(dto())
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void addOwnershipUpdateStaff_withoutBeginDate_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setEndDateOnCurrentStaff(update);

        client
            .post(
                "/api/v1/ownership-updates/{id}/staff",
                update.getOwnershipUpdateId()
            )
            .body(dto().beginDate(null))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void addOwnershipUpdateStaff_whenBeginDateIsInTheFuture_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setEndDateOnCurrentStaff(update);

        client
            .post(
                "/api/v1/ownership-updates/{id}/staff",
                update.getOwnershipUpdateId()
            )
            .body(dto().beginDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateStaff_whenModifyingBeginDate_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        StaffDto member = getCurrentStaff(update);

        client
            .put(
                "/api/v1/ownership-updates/{id}/staff/{member}",
                update.getOwnershipUpdateId(),
                member.getId()
            )
            .body(dto().beginDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateStaff_whenAddingEndDateBeforeBeginDate_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setEndDateOnCurrentStaff(update);
        StaffDto member = staffFactory.create(
            update,
            dto().beginDate(LocalDate.of(2021, 1, 5))
        );

        client
            .put(
                "/api/v1/ownership-updates/{id}/staff/{member}",
                update.getOwnershipUpdateId(),
                member.getId()
            )
            .body(member.endDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateStaff_whenAddingEndDateInTheFuture_isBadRequest()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        StaffDto member = getCurrentStaff(update);

        client
            .put(
                "/api/v1/ownership-updates/{id}/staff/{member}",
                update.getOwnershipUpdateId(),
                member.getId()
            )
            .body(member.endDate(LocalDate.of(2100, 1, 1)))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void editOwnershipUpdateStaff_whenModifyingNonNullEndDate_isConflict()
        throws Exception {
        OwnershipUpdateSearchResultDto update = ownershipUpdateFactory.create();
        setEndDateOnCurrentStaff(update);
        StaffDto member = getCurrentStaff(update);

        client
            .put(
                "/api/v1/ownership-updates/{id}/staff/{member}",
                update.getOwnershipUpdateId(),
                member.getId()
            )
            .body(member.endDate(LocalDate.of(2021, 1, 2)))
            .exchange()
            .expectStatus()
            .isConflict();
    }
}
