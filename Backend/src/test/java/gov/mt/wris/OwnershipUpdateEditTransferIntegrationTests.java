package gov.mt.wris;

import gov.mt.wris.controllers.OwnershipUpdateController;
import gov.mt.wris.dtos.*;
import gov.mt.wris.services.ApplicationService;
import gov.mt.wris.services.OwnershipUpdateService;
import gov.mt.wris.services.WaterRightService;
import org.apache.tomcat.jni.Local;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static gov.mt.wris.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class OwnershipUpdateEditTransferIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMVC;

    @Autowired
    OwnershipUpdateController controller;

    @Autowired
    OwnershipUpdateService service;


    @Test
    @Rollback
    public void testChangeOwnershipUpdateCanTransfer() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, ownerUpdateId;
        float sec;

        /* FAILS TEST A */
        ownerUpdateId = Long.valueOf(152711);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto testA = convertTo(result, OwnershipUpdateDto.class);
        assertThat(testA.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(testA.getCanTransfer().equals("N"));

        /* FAILS TEST B */
        ownerUpdateId = Long.valueOf(188851);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto testB = convertTo(result, OwnershipUpdateDto.class);
        assertThat(testB.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(testB.getCanTransfer().equals("N"));

        /* FAILS TEST C */
        ownerUpdateId = Long.valueOf(86694);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto testC = convertTo(result, OwnershipUpdateDto.class);
        assertThat(testC.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(testC.getCanTransfer().equals("N"));

        /* FAILS TEST D */
        ownerUpdateId = Long.valueOf(7200);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto testD = convertTo(result, OwnershipUpdateDto.class);
        assertThat(testD.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(testD.getCanTransfer().equals("N"));

        /* FAILS TEST E */
        ownerUpdateId = Long.valueOf(15130);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto testE = convertTo(result, OwnershipUpdateDto.class);
        assertThat(testE.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(testE.getCanTransfer().equals("N"));

        /* FAILS TEST F */
        ownerUpdateId = Long.valueOf(13588);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto testF = convertTo(result, OwnershipUpdateDto.class);
        assertThat(testF.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(testF.getCanTransfer().equals("N"));

        /* PASSES TEST F WITH TERMINATED WRs (has enough WRs with wrst_cd != 'TERM'' to pass test) */
        ownerUpdateId = Long.valueOf(2909);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto testFa = convertTo(result, OwnershipUpdateDto.class);
        assertThat(testFa.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(testFa.getCanTransfer().equals("Y"));

        /* CAN_TRANSFER = TRUE */
        ownerUpdateId = Long.valueOf(196894);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto testX = convertTo(result, OwnershipUpdateDto.class);
        assertThat(testX.getOwnershipUpdateId().equals(ownerUpdateId));
        assertThat(testX.getCanTransfer().equals("Y"));

    }

    @Test
    @Rollback
    public void testGetAssociateOwnershipUpdateBuyersAndSellers() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, ownerUpdateId;
        float sec;
        String pathResolver = "&path-solver=OWNERUPDATE";

        /* SELLERS & BUYERS TEST FOR 152711 */
        ownerUpdateId = Long.valueOf(152711);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers")
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        SellersForOwnershipUpdatePageDto testA = convertTo(result, SellersForOwnershipUpdatePageDto.class);
        assertThat(testA.getResults().get(0).getContactId()).isEqualTo(28042);
        assertThat(testA.getResults().get(0).getName()).isEqualTo("VOISE, ELLA B");
        assertThat(testA.getResults().get(0).getContractForDeedRle()).isEqualTo("Y");

        ownerUpdateId = Long.valueOf(152711);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers")
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto testB = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        assertThat(testB.getResults().get(0).getContactId()).isEqualTo(370040);
        assertThat(testB.getResults().get(0).getName()).isEqualTo("DUGGINS, TRAVIS");
        assertThat(testB.getResults().get(1).getContactId()).isEqualTo(370041);
        assertThat(testB.getResults().get(1).getName()).isEqualTo("DUGGINS, APRIL M");


        /* SELLERS & BUYERS TEST FOR 188851 */
        ownerUpdateId = Long.valueOf(188851);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers")
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        SellersForOwnershipUpdatePageDto testC = convertTo(result, SellersForOwnershipUpdatePageDto.class);
        assertThat(testC.getResults().size()).isEqualTo(0);

        ownerUpdateId = Long.valueOf(188851);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers")
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto testD = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        assertThat(testD.getResults().get(0).getContactId()).isEqualTo(92884);
        assertThat(testD.getResults().get(0).getName()).isEqualTo("MIKLOVICH, CARTER");
        assertThat(testD.getResults().get(0).getStartDate()).isEqualTo("2019-06-14");

        /* TESTS FOR SORT ORDER */
        String sort = "?sortDirection=ASC&sortColumn=NAME";
        ownerUpdateId = Long.valueOf(115972);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers" + sort)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto testE = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        assertThat(testE.getResults().get(0).getContactId()).isEqualTo(194645);
        assertThat(testE.getResults().get(0).getName()).isEqualTo("BOSTON, MICHAEL D");
        assertThat(testE.getResults().get(7).getContactId()).isEqualTo(333273);
        assertThat(testE.getResults().get(7).getName()).isEqualTo("WEPPLER, REBECCA C");

        sort = "?sortDirection=DESC&sortColumn=NAME";
        ownerUpdateId = Long.valueOf(115972);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers" + sort)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto testF = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        assertThat(testF.getResults().get(0).getContactId()).isEqualTo(333273);
        assertThat(testF.getResults().get(0).getName()).isEqualTo("WEPPLER, REBECCA C");
        assertThat(testF.getResults().get(7).getContactId()).isEqualTo(194645);
        assertThat(testF.getResults().get(7).getName()).isEqualTo("BOSTON, MICHAEL D");

        sort = "?sortDirection=ASC&sortColumn=CONTACTID";
        ownerUpdateId = Long.valueOf(115972);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers" + sort)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto testG = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        assertThat(testG.getResults().get(0).getContactId()).isEqualTo(193960);
        assertThat(testG.getResults().get(0).getName()).isEqualTo("BOYD, CHRISTOPHER M");
        assertThat(testG.getResults().get(7).getContactId()).isEqualTo(333273);
        assertThat(testG.getResults().get(7).getName()).isEqualTo("WEPPLER, REBECCA C");

        sort = "?sortDirection=DESC&sortColumn=CONTACTID";
        ownerUpdateId = Long.valueOf(115972);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers" + sort)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto testH = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        assertThat(testH.getResults().get(0).getContactId()).isEqualTo(333273);
        assertThat(testH.getResults().get(0).getName()).isEqualTo("WEPPLER, REBECCA C");
        assertThat(testH.getResults().get(7).getContactId()).isEqualTo(193960);
        assertThat(testH.getResults().get(7).getName()).isEqualTo("BOYD, CHRISTOPHER M");

        sort = "?sortDirection=ASC&sortColumn=STARTDATE";
        ownerUpdateId = Long.valueOf(115972);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers" + sort)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto testI = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        assertThat(testI.getResults().get(0).getContactId()).isEqualTo(194645);
        assertThat(testI.getResults().get(0).getName()).isEqualTo("BOSTON, MICHAEL D");
        assertThat(testI.getResults().get(7).getContactId()).isEqualTo(333273);
        assertThat(testI.getResults().get(7).getName()).isEqualTo("WEPPLER, REBECCA C");

    }

    @Test
    @Rollback
    public void testOwnershipUpdateSellersdDateIssue() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, ownerUpdateId;
        float sec;

        ownerUpdateId = Long.valueOf(42404);
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers")
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        SellersForOwnershipUpdatePageDto testA = convertTo(result, SellersForOwnershipUpdatePageDto.class);
        assertThat(testA.getResults().get(0).getContactId()).isEqualTo(87088);
        assertThat(testA.getResults().get(0).getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).isEqualTo("2006-10-11");

    }

    @Test
    public void testChangeOwnershipUpdateSeller() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        long start, end, ownerUpdateId, id;
        float sec;

        /* Retrieve test record */
        start = System.currentTimeMillis();
        ownerUpdateId = 2106;
        MvcResult result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SellersForOwnershipUpdatePageDto test1 = convertTo(result, SellersForOwnershipUpdatePageDto.class);
        assertThat(test1.getResults().get(0).getContractForDeedRle()).isEqualTo("N");

        /* Update test record */
        id = 8489;
        OwnershipUpdateSellerUpdateDto ousud = new OwnershipUpdateSellerUpdateDto();
        ousud.setOwnershipUpdateId(ownerUpdateId);
        ousud.setContractForDeedRle("Y");
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers/" + id)
                .content(getJson(ousud))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateSellerDto test2 = convertTo(result, OwnershipUpdateSellerDto.class);
        assertThat(test2.getContractForDeedRle()).isEqualTo("Y");

        /* Put test record back like we found it */
        ousud.setContractForDeedRle("N");
        result = mockMVC.perform(put("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers/" + id)
                .content(getJson(ousud))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateSellerDto test3 = convertTo(result, OwnershipUpdateSellerDto.class);
        assertThat(test3.getContractForDeedRle()).isEqualTo("N");

    }

    @Test
    public void testRemoveAndCreateBuyerReferenceToOwnershipUpdate() throws Exception {

        String token = getAccessToken();
        long start, end, ownerUpdateId, contactId, id;
        float sec;

        /* Test removing reference */
        start = System.currentTimeMillis();
        ownerUpdateId = 3689;
        contactId = 212413;
        id = 556129;
        MvcResult result = mockMVC.perform(delete("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers/" + id)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .param("contactId", String.valueOf(contactId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        /* Ensure reference is removed */
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto test2 = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        long finalContactId = contactId;
        test2.getResults().forEach(buyer -> {
            assertThat(buyer.getContactId()).isNotEqualTo(finalContactId);
        });

        /* Test create reference (back) */
        BuyerReferenceToOwnershipUpdateCreationDto brou = new BuyerReferenceToOwnershipUpdateCreationDto();
        List<Long> ids = new ArrayList<>();
        ids.add(contactId);
        brou.setContactIds(ids);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/buyers")
                .content(getJson(brou))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        BuyerForOwnershipUpdateResultDto test3 = convertTo(result, BuyerForOwnershipUpdateResultDto.class);
        assertThat(test3.getContactIds().contains(contactId));

    }

    @Test
    public void testRemoveAndCreateSellerReferenceToOwnershipUpdate() throws Exception {

        String token = getAccessToken();
        long start, end, ownerUpdateId, contactId, id;
        float sec;

        /* Test removing reference */
        start = System.currentTimeMillis();
        ownerUpdateId = 2078;
        contactId = 187369;
        id = 556130;
        MvcResult result = mockMVC.perform(delete("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers/" + id)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .param("contactId", String.valueOf(contactId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        /* Ensure reference is removed */
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SellersForOwnershipUpdatePageDto test2 = convertTo(result, SellersForOwnershipUpdatePageDto.class);
        long finalContactId = contactId;
        test2.getResults().forEach(seller -> {
            assertThat(seller.getContactId()).isNotEqualTo(finalContactId);
        });

        /* Test create reference (back) */
        SellerReferenceToOwnershipUpdateCreationDto brou = new SellerReferenceToOwnershipUpdateCreationDto();
        List<Long> ids = new ArrayList<>();
        ids.add(contactId);
        brou.setContactIds(ids);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/sellers")
                .content(getJson(brou))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SellerForOwnershipUpdateResultDto test3 = convertTo(result, SellerForOwnershipUpdateResultDto.class);
        assertThat(test3.getContactIds().contains(contactId));

    }

    @Test
    public void testRemoveAndCreateWaterRightReferenceToOwnershipUpdate() throws Exception {

        String token = getAccessToken();
        Message message;
        long start, end, ownerUpdateId, waterRightId;
        float sec;
        MvcResult result;

        /* Test removing reference */
        start = System.currentTimeMillis();
        ownerUpdateId = 5907;
        waterRightId = 40448;
        result = mockMVC.perform(delete("/api/v1/ownership-updates/" + ownerUpdateId + "/water-rights/" + waterRightId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .param("waterRightId", String.valueOf(waterRightId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        /* Ensure reference is removed */
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/water-rights")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateWaterRightPageDto test2 = convertTo(result, OwnershipUpdateWaterRightPageDto.class);
        long finalwaterRightId = waterRightId;
        test2.getResults().forEach(wr -> {
            assertThat(wr.getWaterRightId()).isNotEqualTo(finalwaterRightId);
        });

        /* Test create reference (back) */
        start = System.currentTimeMillis();
        WaterRightReferenceToOwnershipUpdateCreationDto wrou = new WaterRightReferenceToOwnershipUpdateCreationDto();
        List<Long> ids = new ArrayList<>();
        ids.add(waterRightId);
        wrou.setWaterRightIds(ids);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/water-rights")
                .content(getJson(wrou))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightReferenceToOwnershipUpdateResultDto test3 = convertTo(result, WaterRightReferenceToOwnershipUpdateResultDto.class);
        assertThat(test3.getWaterRightIds().contains(waterRightId));

        /* Test constraint violation adding reference for water right that doesn't exist */
        waterRightId = 989898;
        start = System.currentTimeMillis();
        WaterRightReferenceToOwnershipUpdateCreationDto wrou4 = new WaterRightReferenceToOwnershipUpdateCreationDto();
        List<Long> ids4 = new ArrayList<>();
        ids4.add(waterRightId);
        wrou4.setWaterRightIds(ids4);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/water-rights")
                .content(getJson(wrou4))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("One of the Water Rights does not exist");

    }

    @Test
    public void testGetWaterRightsByGeocode() throws Exception {

        String token = getAccessToken();
        long start, end, ownerUpdateId;
        float sec;
        MvcResult result;

        ownerUpdateId = 213827;
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/populate-by-geocodes")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PopulateByGeocodesPageDto test1 = convertTo(result, PopulateByGeocodesPageDto.class);
        assertThat(test1.getResults().get(0).getWaterRightId()).isEqualTo(96494);
        assertThat(test1.getResults().get(7).getWaterRightId()).isEqualTo(349374);

    }

    @Test
    public void testRemoveAndCreateApplicationReferenceToOwnershipUpdate() throws Exception {

        String token = getAccessToken();
        MvcResult result;
        Message message;
        long start, end, ownerUpdateId, applicationId;
        float sec;

        /* Test removing reference */
        start = System.currentTimeMillis();
        ownerUpdateId = 214134;
        applicationId = 30149944;
        result = mockMVC.perform(delete("/api/v1/ownership-updates/" + ownerUpdateId + "/applications/" + applicationId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .param("applicationId", String.valueOf(applicationId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        /* Ensure reference is removed */
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/applications")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateApplicationPageDto test2 = convertTo(result, OwnershipUpdateApplicationPageDto.class);
        long finalapplicationId = applicationId;
        test2.getResults().forEach(a -> {
            assertThat(a.getId()).isNotEqualTo(finalapplicationId);
        });

        /* Test create reference (back) */
        start = System.currentTimeMillis();
        ApplicationReferenceToOwnershipUpdateCreationDto arou = new ApplicationReferenceToOwnershipUpdateCreationDto();
        List<Long> ids = new ArrayList<>();
        ids.add(applicationId);
        arou.setApplicationIds(ids);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/applications")
                .content(getJson(arou))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ApplicationReferenceToOwnershipUpdateResultDto test3 = convertTo(result, ApplicationReferenceToOwnershipUpdateResultDto.class);
        assertThat(test3.getApplicationIds().contains(applicationId));

        /* Test constraint violation adding reference for water right that doesn't exist */
        applicationId = 98989898;
        start = System.currentTimeMillis();
        ApplicationReferenceToOwnershipUpdateCreationDto arou4 = new ApplicationReferenceToOwnershipUpdateCreationDto();
        List<Long> ids4 = new ArrayList<>();
        ids4.add(applicationId);
        arou4.setApplicationIds(ids4);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/applications")
                .content(getJson(arou4))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("One of the Applications does not exist");

    }

    @Test
    public void testTransferWaterRightsOwnership() throws Exception {

        transferWaterRightsOwnershipHappyPath(STANDARD_608_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(DI_641_608_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(EWR_642_608_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(SWR_643_608_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(DOR_608_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(CD_608_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(ADM_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(DI_641_COR_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(DI_641_OTH_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(EWR_642_COR_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(EWR_642_OTH_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(SWR_643_COR_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(EXEMPT_FILING_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(IMPLIED_CLAIM_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(PENDING_CORRECTION_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(PENDING_608_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(VER_TRANSACTION_TYPE);
        transferWaterRightsOwnershipHappyPath(WTC_608_TRANSACTION_TYPE);

    }

    private void transferWaterRightsOwnershipHappyPath(String ownerUpdateType) throws Exception {

        String token = getAccessToken();

        OwnershipUpdateSearchResultDto cou_dto_fwd = createOwnershipUpdateForward(token, ownerUpdateType);
        SellersForOwnershipUpdatePageDto sellers_fwd = getSellers(token, cou_dto_fwd);
        assertThat(sellers_fwd.getResults().get(1).getContactId().equals(159033L)); // FLADAGER, NOLA
        assertThat(sellers_fwd.getResults().get(0).getContactId().equals(159030L)); // FLADAGER, BRUCE
        BuyersForOwnershipUpdatePageDto buyers_fwd = getBuyers(token, cou_dto_fwd);
        assertThat(buyers_fwd.getResults().get(0).getContactId().equals(137115L)); // HERMAN, JOHN E
        assertThat(buyers_fwd.getResults().get(1).getContactId().equals(137133L)); // HERMAN, RAE ELLEN
        OwnershipUpdateWaterRightPageDto rights_fwd = getWaterRights(token, cou_dto_fwd);
        assertThat(rights_fwd.getResults().get(0).getWaterRightId().equals(485347L));
        TransferWaterRightsOwnershipResultDto fwd_dto = processTransfer(token, cou_dto_fwd.getOwnershipUpdateId());
        assertThat(fwd_dto.getMessages().contains("Ownership update successfully processed."));
        OwnershipUpdateDto fwd_result = getOwnershipUpdate(token, cou_dto_fwd.getOwnershipUpdateId());
        assertThat(fwd_result.getOwnershipUpdateId().equals(cou_dto_fwd.getOwnershipUpdateId()));
        // TODO ADDITIONAL TRANSFER FORWARD TESTS ?

        OwnershipUpdateSearchResultDto cou_dto_rev = createOwnershipUpdateReverse(token, ownerUpdateType);
        SellersForOwnershipUpdatePageDto sellers_rev = getSellers(token, cou_dto_rev);
        assertThat(sellers_rev.getResults().get(0).getContactId().equals(137115L)); // HERMAN, JOHN E
        assertThat(sellers_rev.getResults().get(1).getContactId().equals(137133L)); // HERMAN, RAE ELLEN
        BuyersForOwnershipUpdatePageDto buyers_rev = getBuyers(token, cou_dto_rev);
        assertThat(buyers_rev.getResults().get(1).getContactId().equals(159033L)); // FLADAGER, NOLA
        assertThat(buyers_rev.getResults().get(0).getContactId().equals(159030L)); // FLADAGER, BRUCE
        OwnershipUpdateWaterRightPageDto rights_rev = getWaterRights(token, cou_dto_rev);
        assertThat(rights_rev.getResults().get(0).getWaterRightId().equals(485347L));
        TransferWaterRightsOwnershipResultDto rev_dto = processTransfer(token, cou_dto_rev.getOwnershipUpdateId());
        assertThat(rev_dto.getMessages().contains("Ownership update successfully processed."));
        OwnershipUpdateDto rev_result = getOwnershipUpdate(token, cou_dto_rev.getOwnershipUpdateId());
        assertThat(rev_result.getOwnershipUpdateId().equals(cou_dto_rev.getOwnershipUpdateId()));
        // TODO ADDITIONAL TRANSFER REVERSE TESTS ?

    }

    private OwnershipUpdateSearchResultDto createOwnershipUpdateForward(String token, String ownerUpdateType) throws Exception {

        long start, end;
        float sec;

        OwnershipUpdateCreationDto ou_dto = new OwnershipUpdateCreationDto();
        ou_dto.setOwnershipUpdateType(ownerUpdateType);
        ou_dto.setPendingDORValidation(false);
        ou_dto.setReceivedAs608(true);
        ou_dto.setReceivedDate(LocalDate.now().minusMonths(1)); // Set it for last month
        List<Long> sellers = new ArrayList<>();
        sellers.add(159033L); // FLADAGER, NOLA
        sellers.add(159030L); // FLADAGER, BRUCE
        ou_dto.setSellers(sellers);
        List<Long> buyers = new ArrayList<>();
        buyers.add(137115L); // HERMAN, JOHN E
        buyers.add(137133L); // HERMAN, RAE ELLEN
        ou_dto.setBuyers(buyers);
        List<Long> rights = new ArrayList<>();
        rights.add(485347L); // WTR_ID = 30149284
        ou_dto.setWaterRights(rights);

        start = System.currentTimeMillis();
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                .content(getJson(ou_dto))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateSearchResultDto dto = convertTo(result, OwnershipUpdateSearchResultDto.class);
        return dto;

    }

    private OwnershipUpdateSearchResultDto createOwnershipUpdateReverse(String token, String ownerUpdateType) throws Exception {

        long start, end;
        float sec;

        OwnershipUpdateCreationDto ou_dto = new OwnershipUpdateCreationDto();
        ou_dto.setOwnershipUpdateType(ownerUpdateType);
        ou_dto.setPendingDORValidation(false);
        ou_dto.setReceivedAs608(true);
        ou_dto.setReceivedDate(LocalDate.now().minusWeeks(1)); // Set it for last week
        List<Long> sellers = new ArrayList<>();
        sellers.add(137115L); // HERMAN, JOHN E
        sellers.add(137133L); // HERMAN, RAE ELLEN
        ou_dto.setSellers(sellers);
        List<Long> buyers = new ArrayList<>();
        buyers.add(159033L); // FLADAGER, NOLA
        buyers.add(159030L); // FLADAGER, BRUCE
        ou_dto.setBuyers(buyers);
        List<Long> rights = new ArrayList<>();
        rights.add(485347L); // WTR_ID = 30149284
        ou_dto.setWaterRights(rights);

        start = System.currentTimeMillis();
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                .content(getJson(ou_dto))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateSearchResultDto dto = convertTo(result, OwnershipUpdateSearchResultDto.class);
        return dto;

    }

    private TransferWaterRightsOwnershipResultDto processTransfer(String token, Long ownerUpdateId) throws Exception {

        long start, end;
        float sec;

        start = System.currentTimeMillis();
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/transfer")
                .content(getJson(null))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        TransferWaterRightsOwnershipResultDto dto = convertTo(result, TransferWaterRightsOwnershipResultDto.class);
        return dto;

    }

    private OwnershipUpdateDto getOwnershipUpdate(String token, Long ownerUpdateId) throws Exception {

        long start, end;
        float sec;

        start = System.currentTimeMillis();
        MvcResult result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId)
                .header("Authorization", "Bearer " + token)
                .param("ownerUpdateId", String.valueOf(ownerUpdateId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** GET ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateDto dto = convertTo(result, OwnershipUpdateDto.class);
        return dto;

    }

    private SellersForOwnershipUpdatePageDto getSellers(String token, OwnershipUpdateSearchResultDto ou) throws Exception {

        long start, end;
        float sec;

        start = System.currentTimeMillis();
        MvcResult result = mockMVC.perform(get("/api/v1/ownership-updates/" + ou.getOwnershipUpdateId() + "/sellers")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SellersForOwnershipUpdatePageDto dto = convertTo(result, SellersForOwnershipUpdatePageDto.class);
        return dto;

    }

    private BuyersForOwnershipUpdatePageDto getBuyers(String token, OwnershipUpdateSearchResultDto ou) throws Exception {

        long start, end;
        float sec;

        start = System.currentTimeMillis();
        MvcResult result = mockMVC.perform(get("/api/v1/ownership-updates/" + ou.getOwnershipUpdateId() + "/buyers")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        BuyersForOwnershipUpdatePageDto dto = convertTo(result, BuyersForOwnershipUpdatePageDto.class);
        return dto;

    }

    private OwnershipUpdateWaterRightPageDto getWaterRights(String token, OwnershipUpdateSearchResultDto ou) throws Exception {

        long start, end;
        float sec;

        start = System.currentTimeMillis();
        MvcResult result = mockMVC.perform(get("/api/v1/ownership-updates/" + ou.getOwnershipUpdateId() + "/water-rights")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdateWaterRightPageDto dto = convertTo(result, OwnershipUpdateWaterRightPageDto.class);
        return dto;

    }

    @Test
    public void testSearchOwnershipUpdatesChangeApplications() throws Exception {

        String token = getAccessToken();
        String basin, applicationId;
        long start, end, ownerUpdateId;
        float sec;
        MvcResult result;

        ownerUpdateId = 1;
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/applications/active")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();

        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        OwnershipUpdatesChangeApplicationsPageDto testA = convertTo(result, OwnershipUpdatesChangeApplicationsPageDto.class);
        assertThat(testA.getResults().get(0).getApplicationId()).isEqualTo(900);
        assertThat(testA.getResults().get(0).getBasin()).isEqualTo("41I");

    }


    @Test
    public void testProcessTransferErrors() throws Exception {

        String token = getAccessToken();
        MvcResult result;
        Message message;
        long start, end, ownerUpdateId, applicationId;
        float sec;

        ownerUpdateId = 103747;
        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/transfer")
                .content(getJson(null))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("This 643 update has more than one Water Right. Only ONE Water Right is allowed to be processed by the 643 OWNERSHIP UPDATE.");

        /* Special test for 641/642s v1 apps deleting & inserting events */
        ownerUpdateId = 68327;
        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/transfer")
                .content(getJson(null))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        ownerUpdateId = 206614;
        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/transfer")
                .content(getJson(null))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("An owner has been specified that is not a current owner of a water right and no chain of title has been specified. No changes have been made.");

        ownerUpdateId = 0;
        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + ownerUpdateId + "/transfer")
                .content(getJson(null))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("This Ownership Update does not exist");

    }

    @Test
    public void testSearchActiveSellersOwnershipUpdate() throws Exception {

        String token = getAccessToken();
        long start, end, ownerUpdateId;
        float sec;
        MvcResult result;
        String page = "?pageSize=25";

        ownerUpdateId = 3;
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/customers/active" + page)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CustomerPageDto testA = convertTo(result, CustomerPageDto.class);
        assertThat(testA.getResults().contains(375260));
        assertThat(testA.getResults().contains(215371));
        assertThat(testA.getResults().contains(215372));


        String lastName = "NIELSON";
        ownerUpdateId = 3;
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/ownership-updates/" + ownerUpdateId + "/customers/active" + page)
                .header("Authorization", "Bearer " + token)
                .param("lastName", lastName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CustomerPageDto testB = convertTo(result, CustomerPageDto.class);
        assertThat(testB.getResults().size()).isEqualTo(2);
        assertThat(testB.getResults().get(0).getName()).isEqualTo("NIELSON, BUDDY A");
        assertThat(testB.getResults().get(1).getName()).isEqualTo("NIELSON, RENEE M");


    }


}
