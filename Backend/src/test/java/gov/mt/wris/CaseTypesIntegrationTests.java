package gov.mt.wris;

import gov.mt.wris.dtos.AllEventCodeDescDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

import gov.mt.wris.controllers.CaseTypesController;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.CaseTypeDto;
import gov.mt.wris.dtos.CaseTypePageDto;
import gov.mt.wris.repositories.CaseTypeRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CaseTypesIntegrationTests extends BaseTestCase{
    @Autowired
    private MockMvc mockMVC;

    @Autowired
    CaseTypesController caseController;

    @Autowired
    CaseTypeRepository caseRepo;

    @Test
    @Transactional
    @Rollback(true)
    public void testGetCaseTypes() throws Exception {
        String token = getAccessToken();

        MvcResult result =  mockMVC.perform(get("/api/v1/case-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        CaseTypePageDto casePageDto = convertTo(result, CaseTypePageDto.class);

        //check if Test is created and fail out if so
        assertThat(casePageDto.getResults())
            .as("Make sure that a TEST case type hasn't already been created")
            .filteredOn(caseType -> "TEST".equals(caseType.getCode()))
            .hasSize(0);

        Long startingTotalElements = casePageDto.getTotalElements();

        // Create Case
        CaseTypeDto newCase = new CaseTypeDto();
        newCase.setCode("TEST");
        newCase.setDescription("Test Case Type");
        newCase.setProgram(CaseTypeDto.ProgramEnum.fromValue("WC"));

        mockMVC.perform(post("/api/v1/case-types")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();

        //check with filter
        result =  mockMVC.perform(get("/api/v1/case-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("code", "TEST")
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        casePageDto = convertTo(result, CaseTypePageDto.class);

        assertThat(casePageDto.getResults())
            .as("Can't find the Test case Type that was created")
            .hasSize(1);
        assertThat(casePageDto.getResults().get(0).getCode())
            .as("The filtering on code isn't working properly")
            .isEqualTo("TEST");

        // Update Case
        newCase.setDescription("Updated Test Case Type");
        result =  mockMVC.perform(put("/api/v1/case-types/TEST")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();

        // check with filter
        result =  mockMVC.perform(get("/api/v1/case-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("code", "TEST")
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        casePageDto = convertTo(result, CaseTypePageDto.class);

        assertThat(casePageDto.getResults())
            .as("Can't find the Test case Type that was updated")
            .hasSize(1);
        assertThat(casePageDto.getResults().get(0).getCode())
            .as("The filtering on code isn't working properly")
            .isEqualTo("TEST");

        // Update Case with new Code
        newCase.setCode("TESV");
        result =  mockMVC.perform(put("/api/v1/case-types/TEST")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();
        
        // Missing field
        newCase = new CaseTypeDto();
        newCase.setCode("TEST");
        newCase.setProgram(CaseTypeDto.ProgramEnum.fromValue("WC"));
        result =  mockMVC.perform(put("/api/v1/case-types/TEST")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isBadRequest())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).contains("Missing or incorrect values were provided");

        // Delete
        mockMVC.perform(delete("/api/v1/case-types/TEST")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // check without filter
        result =  mockMVC.perform(get("/api/v1/case-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        casePageDto = convertTo(result, CaseTypePageDto.class);

        assertThat(casePageDto.getTotalElements()).isEqualTo(startingTotalElements);
    }
    //Tests
    // Create Case, what happens when something with the
    // missing field
    @Test
    public void testMissingField() throws Exception {
        String token = getAccessToken();
        CaseTypeDto newCase = new CaseTypeDto();
        newCase.setCode("TEST");
        newCase.setProgram(CaseTypeDto.ProgramEnum.fromValue("WC"));
        MvcResult result =  mockMVC.perform(post("/api/v1/case-types")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isBadRequest())
                        .andReturn();
        
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).contains("Missing or incorrect values were provided");
    }
    // same id exists
    @Test void testDuplicateId() throws Exception {
        String token = getAccessToken();
        CaseTypeDto newCase = new CaseTypeDto();
        newCase.setDescription("Test Case Type");
        newCase.setProgram(CaseTypeDto.ProgramEnum.fromValue("WC"));
        
        //grab Case Type that already exists
        MvcResult result =  mockMVC.perform(get("/api/v1/case-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        newCase.setCode(convertTo(result, CaseTypePageDto.class).getResults().get(0).getCode());
        
        result = mockMVC.perform(post("/api/v1/case-types")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("A Case Type with this Code already exists");
    }

    // Delete Case
    // what happens if doesn't exist
    @Test void testNonexistentDelete() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(delete("/api/v1/case-types/TESV")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isNotFound())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Case Type with code TESV not found");
    }
    // Delete Case
    // what happens if we try to delete something that has Event type xrefs
    @Test void testCantDelete() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(delete("/api/v1/case-types/AWC")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("This Case Type is used in a Water Court Case");
    }

    // Change Case
    // Doesn't exist with that code
    @Test void testNonexistentChange() throws Exception {
        String token = getAccessToken();

        CaseTypeDto newCase = new CaseTypeDto();
        newCase.setDescription("Test Case Type");
        newCase.setProgram(CaseTypeDto.ProgramEnum.fromValue("WC"));
        newCase.setCode("TESV");
        MvcResult result = mockMVC.perform(put("/api/v1/case-types/TESV")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isNotFound())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("The Case Type with code TESV was not found");
    }

    // Create a Case Type with a too small code
    @Test
    public void testTooSmallCode() throws Exception {
        String token = getAccessToken();

        CaseTypeDto newCase = new CaseTypeDto();
        newCase.setCode("TS");
        newCase.setDescription("Test Case Type");
        newCase.setProgram(CaseTypeDto.ProgramEnum.fromValue("WC"));
        MvcResult result = mockMVC.perform(post("/api/v1/case-types")
                            .content(getJson(newCase))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).contains("Missing or incorrect values were provided");
    }

    @Test
    public void testGetCaseEventTypes() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String typeCode;
        float sec;

        typeCode = "AWC"; /* UNSUPPORTED CASE TYPE */
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/case-types/" + typeCode + "/event-types")
                .header("Authorization", "Bearer " + token)
                .param("typeCode", typeCode)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        AllEventCodeDescDto testA = convertTo(result, AllEventCodeDescDto.class);
        assertThat(testA.getResults().size()).isEqualTo(0);

        typeCode = "ADM"; /* SUPPORTED CASE TYPE */
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/case-types/" + typeCode + "/event-types")
                .header("Authorization", "Bearer " + token)
                .param("typeCode", typeCode)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        AllEventCodeDescDto testB = convertTo(result, AllEventCodeDescDto.class);
        assertThat(testB.getResults().get(0).getCode()).isEqualTo("ARFC");
        assertThat(testB.getResults().get(0).getDescription()).isEqualTo("APPLICATION REVIEW FORM COMPLETE");
        assertThat(testB.getResults().get(0).getResponseDueDays()).isEqualTo(0);
        assertThat(testB.getResults().get(62).getCode()).isEqualTo("ATWC");
        assertThat(testB.getResults().get(62).getDescription()).isEqualTo("WATER COURT-APPEALED TO WATER COURT");
        assertThat(testB.getResults().get(62).getResponseDueDays()).isEqualTo(0);

    }

}