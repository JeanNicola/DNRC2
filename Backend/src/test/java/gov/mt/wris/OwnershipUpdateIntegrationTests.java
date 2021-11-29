package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.controllers.OwnershipUpdateController;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.OwnershipUpdateBuyersPageDto;
import gov.mt.wris.dtos.OwnershipUpdateChangeFeeLetterDto;
import gov.mt.wris.dtos.OwnershipUpdateChangeFeeSummaryDto;
import gov.mt.wris.dtos.OwnershipUpdateCreationDto;
import gov.mt.wris.dtos.OwnershipUpdateDto;
import gov.mt.wris.dtos.OwnershipUpdateFeeLetterDto;
import gov.mt.wris.dtos.OwnershipUpdateFeeSummaryDto;
import gov.mt.wris.dtos.OwnershipUpdatePageDto;
import gov.mt.wris.dtos.OwnershipUpdatePaymentsPageDto;
import gov.mt.wris.dtos.OwnershipUpdateSearchResultDto;
import gov.mt.wris.dtos.OwnershipUpdateSellersPageDto;
import gov.mt.wris.dtos.OwnershipUpdateUpdateDto;
import gov.mt.wris.dtos.OwnershipUpdateWaterRightPageDto;
import gov.mt.wris.dtos.OwnershipUpdatesForContactPageDto;
import gov.mt.wris.dtos.PaymentDto;
import gov.mt.wris.services.OwnershipUpdateService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class OwnershipUpdateIntegrationTests  extends BaseTestCase {

    @Autowired
    private MockMvc mockMVC;

    @Autowired
    OwnershipUpdateController controller;

    @Autowired
    OwnershipUpdateService service;

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATEID";
        String ownershipUpdateId = "215097";
        String ownershipUpdateType = "IMP";
        //ownershipUpdateId = "1";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .param("ownershipUpdateId", ownershipUpdateId)
                .param("ownershipUpdateType", ownershipUpdateType)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdatePageDto page = convertTo(result, OwnershipUpdatePageDto.class);
        assertThat(page.getResults().get(0).getWaterRightCount().equals(3));

    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesSort() throws Exception {

        String token = getAccessToken();

        long start, end;
        float sec;
        MvcResult result = null;
        Message message = null;

        String sort = "?sortDirection=DESC&sortColumn=OWNERSHIPUPDATETYPEVALUE";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatePageDto page = convertTo(result, OwnershipUpdatePageDto.class);
        // There is bad data in the database - some OwnershipUpdateTypeValues are null
        if(page.getResults().get(0).getOwnershipUpdateTypeValue() != null) {
            assertThat(page.getResults().get(0).getOwnershipUpdateTypeValue().equals("WATER COURT ORDER UPDATE"));
        } else {
            assertThat(page.getResults().get(0).getOwnershipUpdateType().equals("643 OTH"));
        }


        sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATETYPEVALUE";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatePageDto page2 = convertTo(result, OwnershipUpdatePageDto.class);
        assertThat(page2.getResults().get(0).getOwnershipUpdateTypeValue().equals("641 CORRECTION"));


    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesVariousDates() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        Long start, end;
        float sec;
        String sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATEID";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        LocalDate dateReceived = LocalDate.parse("02-22-2021", formatter2);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .param("dateReceived", dateReceived.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatePageDto page1 = convertTo(result, OwnershipUpdatePageDto.class);
        assertThat(page1.getResults()).hasSize(19);

        LocalDate dateSale = LocalDate.parse("22-02-2021", formatter);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .param("dateSale", dateSale.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatePageDto page2 = convertTo(result, OwnershipUpdatePageDto.class);
        assertThat(page2.getResults()).hasSize(5);

        LocalDate dateProcessed = LocalDate.parse("22-02-2021", formatter);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .param("dateProcessed", dateProcessed.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatePageDto page3 = convertTo(result, OwnershipUpdatePageDto.class);
        assertThat(page3.getResults()).hasSize(5);

        LocalDate dateTerminated = LocalDate.parse("22-02-2021", formatter);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .param("dateTerminated", dateTerminated.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatePageDto page4 = convertTo(result, OwnershipUpdatePageDto.class);
        assertThat(page4.getResults()).hasSize(1);

    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesOwnershipUpdateIdSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATEID";
        String ownershipUpdateId = "215097";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .param("ownershipUpdateId", ownershipUpdateId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatePageDto page = convertTo(result, OwnershipUpdatePageDto.class);
        assertThat(page.getResults()).hasSize(1);

    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesWaterRightNumber() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATEID";
        String waterRightNumber = "2";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdatePageDto page = convertTo(result, OwnershipUpdatePageDto.class);
        assertThat(page.getResults()).hasSize(11);

    }

    @Test
    @Rollback
    public void testGetOwnershipUpdatesForContactSuccess() throws Exception {

        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        Message message = null;

        String sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATEID";
        String contactId = "46339";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + contactId + "/contacts" + sort)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatesForContactPageDto page1 = convertTo(result, OwnershipUpdatesForContactPageDto.class);
        assertThat(page1.getResults()).hasSize(2);

        sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATETYPEVALUE";
        contactId = "145863";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + contactId + "/contacts" + sort)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatesForContactPageDto page2 = convertTo(result, OwnershipUpdatesForContactPageDto.class);
        assertThat(page2.getResults()).hasSize(2);
        assertThat(page2.getResults().get(0).getOwnershipUpdateType().equals("CORRECTION"));
        assertThat(page2.getResults().get(0).getOwnershipUpdateType().equals("OWNERSHIP UPDATE"));

        sort = "?sortDirection=DESC&sortColumn=OWNERSHIPUPDATETYPEVALUE";
        contactId = "145863";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + contactId + "/contacts" + sort)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatesForContactPageDto page3 = convertTo(result, OwnershipUpdatesForContactPageDto.class);
        assertThat(page3.getResults()).hasSize(2);
        assertThat(page3.getResults().get(0).getOwnershipUpdateType().equals("OWNERSHIP UPDATE"));
        assertThat(page3.getResults().get(0).getOwnershipUpdateType().equals("CORRECTION"));

    }

    @Test
    @Rollback
    public void testGetOwnershipUpdatesBuyersSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=ASC&sortColumn=CONTACTID";
        String ownershipUpdateId = "215097";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/buyers/" + ownershipUpdateId + sort)
                .header("Authorization", "Bearer " + token)
                .param("ownershipUpdateId", ownershipUpdateId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdateBuyersPageDto page = convertTo(result, OwnershipUpdateBuyersPageDto.class);
        assertThat(page.getResults()).hasSize(1);

    }

    @Test
    @Rollback
    public void testGetOwnershipUpdatesSellersSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=ASC&sortColumn=CONTACTID";
        String ownershipUpdateId = "215097";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/sellers/" + ownershipUpdateId + sort)
                .header("Authorization", "Bearer " + token)
                //.param("lastName", lastName)
                .param("ownershipUpdateId", ownershipUpdateId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdateSellersPageDto page = convertTo(result, OwnershipUpdateSellersPageDto.class);
        assertThat(page.getResults()).hasSize(2);

    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesBuyersSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=DESC&sortColumn=CONTACTID";
        String contactId = "250044";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/buyers" + sort)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdateBuyersPageDto page = convertTo(result, OwnershipUpdateBuyersPageDto.class);
        assertThat(page.getResults()).hasSize(1);

    }

    @Test
    @Rollback
    public void bugfix_SearchOwnershipUpdatesBuyers_DTFP_1107() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=DESC&sortColumn=CONTACTID";
        String page = "?pageSize=25&pageNumber=1";
        String lastName = "ZIGNEGO";
        long start, end;
        float sec;

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/buyers" + page)
                .header("Authorization", "Bearer " + token)
                .param("lastName", lastName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdateBuyersPageDto test1 = convertTo(result, OwnershipUpdateBuyersPageDto.class);
        assertThat(test1.getResults().get(0).getContactId().equals(214819));
        assertThat(test1.getResults().get(0).getName().equals("ZIGNEGO, J LEE"));
        assertThat(test1.getResults().get(0).getLastName().equals("ZIGNEGO"));
        assertThat(test1.getResults().get(0).getFirstName().equals("J LEE"));

        // BUGFIX for pendingDor flag
        Long ownerUpdateId = 215278L;
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdateDto test2 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test2.getOwnershipUpdateId()).isEqualTo(215278L);

    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesSellersSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=DESC&sortColumn=CONTACTID";
        String lastName = "FE%";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/sellers/" + sort)
                .header("Authorization", "Bearer " + token)
                .param("lastName", lastName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdateSellersPageDto page = convertTo(result, OwnershipUpdateSellersPageDto.class);
        assertThat(page.getResults()).hasSize(25);
        assertThat(page.getResults().get(0).getContactId().equals(416294));
        assertThat(page.getResults().get(24).getContactId().equals(371792));

    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesSellersFirstName() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=DESC&sortColumn=CONTACTID";
        String firstName = "JAM%";
        String lastName = "FE%";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/sellers/" + sort)
                .header("Authorization", "Bearer " + token)
                .param("firstName", firstName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        OwnershipUpdateSellersPageDto page = convertTo(result, OwnershipUpdateSellersPageDto.class);
        assertThat(page.getResults()).hasSize(25);

    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesSellersPaging() throws Exception {

        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=ASC&sortColumn=CONTACTID";
        String page = "&pageSize=25&pageNumber=7190";
        String firstName = "JAM%";
        String lastName = "FE%";
        String contactId = "22%";

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/sellers/" + sort + page)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateSellersPageDto test1 = convertTo(result, OwnershipUpdateSellersPageDto.class);
        assertThat(test1.getResults()).hasSize(25);

        page = "&pageSize=25&pageNumber=2";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/sellers/" + sort + page)
                .header("Authorization", "Bearer " + token)
                .param("lastName", lastName)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateSellersPageDto test2 = convertTo(result, OwnershipUpdateSellersPageDto.class);
        assertThat(test2.getResults()).hasSize(5);

    }

    @Test
    @Rollback
    public void testSearchOwnershipUpdatesBuyerSellerIssue() throws Exception {

        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=DESC&sortColumn=CONTACTID";
        String contactId = "330189";

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/sellers" + sort)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateBuyersPageDto sPage = convertTo(result, OwnershipUpdateBuyersPageDto.class);
        assertThat(sPage.getResults()).hasSize(1);

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/buyers" + sort)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateBuyersPageDto bPage = convertTo(result, OwnershipUpdateBuyersPageDto.class);
        assertThat(bPage.getResults()).hasSize(1);

    }

    @Test
    public void testCreateOwnershipUpdate() throws Exception {
        String token = getAccessToken();

        OwnershipUpdateCreationDto newUpdate = new OwnershipUpdateCreationDto();
        newUpdate.setOwnershipUpdateType("DOR 608");
        newUpdate.setPendingDORValidation(false);
        newUpdate.setReceivedAs608(false);
        LocalDate receivedDate = getDate("2021-01-01T00:00:00").toLocalDate();
        newUpdate.setReceivedDate(receivedDate);
        newUpdate.setBuyers(Arrays.asList(229249L));
        newUpdate.setSellers(Arrays.asList(311952L));
        newUpdate.setWaterRights(Arrays.asList(129023L, 192135L, 287512L));
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                .content(getJson(newUpdate))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        OwnershipUpdateSearchResultDto dto = convertTo(result, OwnershipUpdateSearchResultDto.class);

        service.deleteOwnershipUpdate(dto.getOwnershipUpdateId());
    }

    @Test
    @Rollback
    public void testChangeOwnershipUpdate() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, ownerUpdateId;
        float sec;
        String testNote = "\nTHIS IS A MULTI-LINE COMMENT.\nANOTHER LINE OF COMMENTS.\nONE MORE LINE OF COMMENTS.";

        /* Test getting an Ownership Update */
        ownerUpdateId = Long.valueOf(7400);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test1 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test1.getOwnershipUpdateId().equals(ownerUpdateId));

        /* Test an Ownership Update update */
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newProcessedDate = LocalDate.parse("2003-08-11", dtf);
        OwnershipUpdateUpdateDto ouud1 = new OwnershipUpdateUpdateDto();
        ouud1.setOwnershipUpdateId(test1.getOwnershipUpdateId());
        ouud1.setOwnershipUpdateType(test1.getOwnershipUpdateType());
        ouud1.setDateProcessed(newProcessedDate);
        ouud1.setDateReceived(test1.getDateReceived());
        ouud1.setDateTerminated(null);
        ouud1.setPendingDor("Y");
        ouud1.setReceivedAs608(test1.getReceivedAs608());
        ouud1.setNotes(test1.getNotes() + "\n" + testNote);
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId)
                .content(getJson(ouud1))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(test1.getOwnershipUpdateId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test2 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test2.getOwnershipUpdateId().equals(ownerUpdateId));

        /* Retrieve the Ownership Update we just updated and check it has new values... */
        ownerUpdateId = Long.valueOf(7400);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test3 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test3.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(test3.getDateProcessed().equals(newProcessedDate));
        assertThat(test3.getPendingDor().equals(ouud1.getPendingDor()));
        assertThat(test3.getNotes()).isEqualTo(ouud1.getNotes());

        /* Put everything back the way we found it... */
        OwnershipUpdateUpdateDto ouud2 = new OwnershipUpdateUpdateDto();
        ouud2 = ouud1;
        ouud2.setDateProcessed(test1.getDateProcessed());
        ouud2.setPendingDor(test1.getPendingDor());
        ouud2.setReceivedAs608(test1.getReceivedAs608());
        ouud2.setDateTerminated(test1.getDateTerminated());
        ouud2.setNotes(test1.getNotes());
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId)
                .content(getJson(ouud1))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(test1.getOwnershipUpdateId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test4 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test4.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(test4.getDateProcessed()).isEqualTo(test1.getDateProcessed());
        assertThat(test4.getPendingDor()).isEqualTo(test1.getPendingDor());
        assertThat(test4.getNotes()).isEqualTo(test1.getNotes());

    }

    @Test
    @Rollback
    public void testChangeOwnershipUpdateNotesSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, ownerUpdateId;
        float sec;
        String testNote = "\nTHIS IS A MULTI-LINE COMMENT.\nANOTHER LINE OF COMMENTS.\nONE MORE LINE OF COMMENTS.";

        /* Test getting an Ownership Update */
        ownerUpdateId = Long.valueOf(7400);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test1 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test1.getOwnershipUpdateId().equals(ownerUpdateId));

        /* Test updating Ownership Update Notes */
        OwnershipUpdateUpdateDto ouud1 = new OwnershipUpdateUpdateDto();
        ouud1.setOwnershipUpdateId(test1.getOwnershipUpdateId());
        ouud1.setOwnershipUpdateType(test1.getOwnershipUpdateType());
        ouud1.setDateProcessed(test1.getDateProcessed());
        ouud1.setDateReceived(test1.getDateReceived());
        ouud1.setDateTerminated(test1.getDateTerminated());
        ouud1.setPendingDor(test1.getPendingDor());
        ouud1.setReceivedAs608(test1.getReceivedAs608());
        ouud1.setDateSale(test1.getDateSale());
        ouud1.setNotes(test1.getNotes() + "\n" + testNote);

        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId)
                .content(getJson(ouud1))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(test1.getOwnershipUpdateId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test2 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test2.getOwnershipUpdateId().equals(ownerUpdateId));

        /* Retrieve the Ownership Update we just updated and check it has new values... */
        ownerUpdateId = Long.valueOf(7400);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test3 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test3.getNotes()).isEqualTo(ouud1.getNotes());

        /* Put everything back the way we found it... */
        OwnershipUpdateUpdateDto ouud2 = new OwnershipUpdateUpdateDto();
        ouud2 = ouud1;
        ouud2.setNotes(test1.getNotes());
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId)
                .content(getJson(ouud1))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(test1.getOwnershipUpdateId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test4 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test4.getNotes()).isEqualTo(test1.getNotes());

    }

    @Test
    @Rollback
    public void testChangeOwnershipUpdateNotesAndRequiredProperties() throws Exception {

        String token = getAccessToken();
        Message message;
        MvcResult result = null;
        long start, end, ownerUpdateId;
        float sec;
        String testNote = "\nTHIS IS A MULTI-LINE COMMENT.\nANOTHER LINE OF COMMENTS.\nONE MORE LINE OF COMMENTS.";

        /* Get an Ownership Update */
        ownerUpdateId = Long.valueOf(213838);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto test1 = convertTo(result, OwnershipUpdateDto.class);
        assertThat(test1.getOwnershipUpdateId().equals(ownerUpdateId));

        /* Test missing Date Received */
        OwnershipUpdateUpdateDto ouud1 = new OwnershipUpdateUpdateDto();
        ouud1.setOwnershipUpdateId(test1.getOwnershipUpdateId());
        ouud1.setOwnershipUpdateType(test1.getOwnershipUpdateType());
        ouud1.setNotes(test1.getNotes() + "\n" + testNote);
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId)
                .content(getJson(ouud1))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(test1.getOwnershipUpdateId()))
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Date Received is missing");

        /* Test missing Ownership Update Id */
        OwnershipUpdateUpdateDto ouud2 = new OwnershipUpdateUpdateDto();
        ouud2.setOwnershipUpdateType(test1.getOwnershipUpdateType());
        ouud2.setNotes(test1.getNotes() + "\n" + testNote);
        ouud2.setDateReceived(test1.getDateReceived());
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId)
                .content(getJson(ouud2))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(test1.getOwnershipUpdateId()))
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Ownership Update Id is missing");

        /* Test missing Ownership Update Type */
        OwnershipUpdateUpdateDto ouud3 = new OwnershipUpdateUpdateDto();
        ouud3.setOwnershipUpdateId(test1.getOwnershipUpdateId());
        ouud3.setNotes(test1.getNotes() + "\n" + testNote);
        ouud3.setDateReceived(test1.getDateReceived());
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId)
                .content(getJson(ouud3))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(test1.getOwnershipUpdateId()))
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Ownership Update Type is missing");

    }

    private String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    @Test
    @Rollback
    public void testSearchFeeSummaryForOwnershipUpdate() throws Exception {
        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        String ownershipUpdtateId = "215163";

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownershipUpdtateId + "/fee-summary")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateFeeSummaryDto feeSummary = convertTo(result, OwnershipUpdateFeeSummaryDto.class);
        assertThat(feeSummary.getFeeStatus()).isNotNull();
    }

    @Test
    @Rollback
    public void testChangeOwnershipUpdateFeeSummary() throws Exception {

        String token = getAccessToken();
        OwnershipUpdateFeeSummaryDto feeSummaryDto;
        MvcResult result = null;
        long start, end;
        float sec;
        String ownershipUpdateId = "215163";
        Double feeDueTestValue1 = 70.00;
        Double feeDueTestValue2 = 0.00;

        OwnershipUpdateChangeFeeSummaryDto feeDueDto = new OwnershipUpdateChangeFeeSummaryDto();
        feeDueDto.setFeeDue(feeDueTestValue1);

        /* UPDATE feeDue USING feeDueTestValue1 */
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownershipUpdateId + "/fee-summary")
                .content(getJson(feeDueDto))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownershipUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        feeSummaryDto = convertTo(result, OwnershipUpdateFeeSummaryDto.class);
        assertThat(feeSummaryDto.getFeeDue()).isEqualTo(feeDueTestValue1);
        if (feeSummaryDto.getAmountPaid() == 0.00) {
            assertThat(feeSummaryDto.getFeeStatus()).isEqualTo("NONE");
        } else if (feeSummaryDto.getAmountPaid() > 0.00 && feeSummaryDto.getAmountPaid() < feeDueTestValue1) {
            assertThat(feeSummaryDto.getFeeStatus()).isEqualTo("PARTIAL");
        } else {
            assertThat(feeSummaryDto.getFeeStatus()).isEqualTo("FULL");
        }

        /* UPDATE feeDue USING feeDueTestValue2 */
        feeDueDto.setFeeDue(feeDueTestValue2);
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownershipUpdateId + "/fee-summary")
                .content(getJson(feeDueDto))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownershipUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        feeSummaryDto = convertTo(result, OwnershipUpdateFeeSummaryDto.class);
        assertThat(feeSummaryDto.getFeeDue()).isEqualTo(feeDueTestValue2);
        assertThat(feeSummaryDto.getFeeStatus()).isEqualTo("FULL");
    }

    @Test
    @Rollback
    public void testCRUDPaymentOperations() throws Exception {

        String token = getAccessToken();

        // setup
        OwnershipUpdateCreationDto newOu = new OwnershipUpdateCreationDto();
        newOu.setOwnershipUpdateType("DOR 608");
        LocalDate date = LocalDate.parse("1973-07-11", DateTimeFormatter.ISO_DATE);
        newOu.setReceivedDate(date);
        newOu.setWaterRights(Arrays.asList(1L, 2L));
        newOu.setPendingDORValidation(false);
        newOu.setReceivedAs608(false);

        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                .content(getJson(newOu))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        OwnershipUpdateSearchResultDto createdOu = convertTo(result, OwnershipUpdateSearchResultDto.class);

        assertThat(createdOu.getOwnershipUpdateId()).isNotNull();
        long oupId = createdOu.getOwnershipUpdateId();

        // Add Payment
        double amountPaid = 100;
        PaymentDto newPayment = new PaymentDto();
        newPayment.setAmountPaid(amountPaid);
        newPayment.setTrackingNumber("Integration0");
        LocalDate paymentDate = getDate("2020-07-11T00:00:00").toLocalDate();
        newPayment.setDatePaid(paymentDate);
        newPayment.setOrigin("TLMS");

        result = mockMVC.perform(post("/api/v1/ownership-updates/" + oupId + "/payments")
                .content(getJson(newPayment))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        PaymentDto payment = convertTo(result, PaymentDto.class);
        long paymentId = payment.getPaymentId();

        // Get Payment
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + oupId + "/payments")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        OwnershipUpdatePaymentsPageDto payments = convertTo(result, OwnershipUpdatePaymentsPageDto.class);
        assertThat(payments.getResults()).isNotNull();
        assertThat(payments.getResults().size()).isEqualTo(1);
        assertThat(payments.getResults().get(0).getAmountPaid()).isEqualTo(amountPaid);

        // Update Payment
        amountPaid += 50;
        newPayment.setAmountPaid(amountPaid);
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + oupId + "/payments/" + paymentId)
                .content(getJson(newPayment))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        PaymentDto updatedpayment = convertTo(result, PaymentDto.class);
        assertThat(updatedpayment.getPaymentId()).isNotNull();
        assertThat(updatedpayment.getAmountPaid()).isEqualTo(amountPaid);

        // Delete Payment
        result = mockMVC.perform(delete("/api/v1/ownership-updates/" + oupId + "/payments/" + paymentId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

        // Get Payment
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + oupId + "/payments")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        payments = convertTo(result, OwnershipUpdatePaymentsPageDto.class);
        assertThat(payments.getResults().size()).isEqualTo(0);

        // Delete Ownership Update
        service.deleteOwnershipUpdate(oupId);
    }

    @Test
    @Rollback
    public void testSearchFeeLetterInformationForOwnershipUpdate() throws Exception {
        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        String ownershipUpdtateId = "27";

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownershipUpdtateId + "/fee-letter")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateFeeLetterDto feeLetterInfo = convertTo(result, OwnershipUpdateFeeLetterDto.class);
        assertThat(feeLetterInfo.getDateSent()).isNotNull();
    }

    @Test
    @Rollback
    public void testChangeOwnershipUpdateFeeLetterInformation() throws Exception {

        String token = getAccessToken();
        OwnershipUpdateFeeLetterDto feeLetterDto;
        MvcResult result = null;
        long start, end;
        float sec;
        String ownershipUpdateId = "27";
        String sentDateTestValue1 = "2001-11-17";
        String sentDateTestValue2 = "2001-11-18";

        OwnershipUpdateChangeFeeLetterDto feeLetterChangeDto = new OwnershipUpdateChangeFeeLetterDto();
        feeLetterChangeDto.setDateSent(LocalDate.of(2001, 11, 17));

        /* UPDATE dateSent to 2001-11-17 */
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownershipUpdateId + "/fee-letter")
                .content(getJson(feeLetterChangeDto))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownershipUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        feeLetterDto = convertTo(result, OwnershipUpdateFeeLetterDto.class);
        assertThat(feeLetterDto.getDateSent().toString()).isEqualTo(sentDateTestValue1);

        /* UPDATE dateSent to 2001-11-18 */
        feeLetterChangeDto.setDateSent(LocalDate.of(2001, 11, 18));
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownershipUpdateId + "/fee-letter")
                .content(getJson(feeLetterChangeDto))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownershipUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        feeLetterDto = convertTo(result, OwnershipUpdateFeeLetterDto.class);
        assertThat(feeLetterDto.getDateSent().toString()).isEqualTo(sentDateTestValue2);
    }

    @Test
    @Rollback
    public void testDuplicateTrackingNumber() throws Exception {

        String token = getAccessToken();

        // setup
        OwnershipUpdateCreationDto newOu = new OwnershipUpdateCreationDto();
        newOu.setOwnershipUpdateType("DOR 608");
        LocalDate date = LocalDate.parse("1973-07-11", DateTimeFormatter.ISO_DATE);
        newOu.setReceivedDate(date);
        newOu.setWaterRights(Arrays.asList(1L, 2L));
        newOu.setPendingDORValidation(false);
        newOu.setReceivedAs608(false);

        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                .content(getJson(newOu))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        OwnershipUpdateSearchResultDto createdOu = convertTo(result, OwnershipUpdateSearchResultDto.class);

        assertThat(createdOu.getOwnershipUpdateId()).isNotNull();
        long oupId = createdOu.getOwnershipUpdateId();

        // add payment
        PaymentDto newPayment = new PaymentDto();
        newPayment.setAmountPaid((double) 125);
        String tracking = "HEL0904699";
        newPayment.setTrackingNumber(tracking);
        LocalDate paymentDate = getDate("2020-07-11T00:00:00").toLocalDate();
        newPayment.setDatePaid(paymentDate);
        newPayment.setOrigin("TLMS");

        result = mockMVC.perform(post("/api/v1/ownership-updates/" + oupId + "/payments")
                .content(getJson(newPayment))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        Message payMessage = convertTo(result, Message.class);

        assertThat(payMessage.getUserMessage()).isEqualTo("Tracking Number " + tracking + " is already being used");

        // Delete Ownership Update
        service.deleteOwnershipUpdate(oupId);
    }

    @Test
    @Rollback
    public void testCalculateFeeDueForOwnershipUpdate() throws Exception {
        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        String ownershipUpdtateId = "215163";

        /* VERIFY 215163 ONLY HAS ONE WATER RIGHT */
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownershipUpdtateId + "/water-rights")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateWaterRightPageDto page = convertTo(result, OwnershipUpdateWaterRightPageDto.class);
        assertThat(page.getResults()).hasSize(1);

        /* VERIFY 215163 HAS A feeDue of 50.00 */
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownershipUpdtateId + "/calculate-fee-due")
                .content(getJson(null))
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownershipUpdtateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateFeeSummaryDto feeSummary = convertTo(result, OwnershipUpdateFeeSummaryDto.class);
        assertThat(feeSummary.getFeeDue()).isEqualTo(50.00);
    }

    @Test
    @Rollback
    public void testGetOwnershipUpdateWaterRightsSort() throws Exception {
        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        String ownerUpdtateId = "213838";
        String sort = "?sortDirection=ASC&sortColumn=STATUSCODE";

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdtateId + "/water-rights" + sort)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateWaterRightPageDto testA = convertTo(result, OwnershipUpdateWaterRightPageDto.class);
        assertThat(testA.getResults().get(0).getWaterRightId()).isEqualTo(5);
        assertThat(testA.getResults().get(0).getValidGeocode()).isEqualTo(false);
        assertThat(testA.getResults().get(0).getStatusCode()).isEqualTo("ACTV");
        assertThat(testA.getResults().get(1).getWaterRightId()).isEqualTo(2);
        assertThat(testA.getResults().get(1).getValidGeocode()).isEqualTo(false);
        assertThat(testA.getResults().get(1).getStatusCode()).isEqualTo("TERM");

        ownerUpdtateId = "61116";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdtateId + "/water-rights" + sort)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateWaterRightPageDto testB = convertTo(result, OwnershipUpdateWaterRightPageDto.class);
        assertThat(testB.getResults().get(0).getWaterRightId()).isEqualTo(1);
        assertThat(testB.getResults().get(0).getValidGeocode()).isEqualTo(true);
        assertThat(testB.getResults().get(0).getStatusCode()).isEqualTo("ACTV");
        assertThat(testB.getResults().get(10).getWaterRightId()).isEqualTo(195299);
        assertThat(testB.getResults().get(10).getValidGeocode()).isEqualTo(false);
        assertThat(testB.getResults().get(10).getStatusCode()).isEqualTo("ACTV");

    }

}
