package gov.mt.wris;

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

import gov.mt.wris.controllers.CaseAssignmentTypesController;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.CaseAssignmentTypeDto;
import gov.mt.wris.dtos.CaseAssignmentTypePageDto;
import gov.mt.wris.repositories.CaseAssignmentTypeRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CaseAssignmentTypesIntegrationTests extends BaseTestCase{
    @Autowired
    private MockMvc mockMVC;

    @Autowired
    CaseAssignmentTypesController caseController;

    @Autowired
    CaseAssignmentTypeRepository caseRepo;

    @Test
    @Transactional
    @Rollback(true)
    public void testGetUser() throws Exception {
        String token = getAccessToken();

        MvcResult result =  mockMVC.perform(get("/api/v1/case-assignment-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        CaseAssignmentTypePageDto casePageDto = convertTo(result, CaseAssignmentTypePageDto.class);

        //check if Test is created and fail out if so
        assertThat(casePageDto.getResults())
            .as("Make sure that a TEST case assignment type hasn't already been created")
            .filteredOn(caseType -> "TEST".equals(caseType.getCode()))
            .hasSize(0);

        Long startingTotalElements = casePageDto.getTotalElements();

        // Create Case
        CaseAssignmentTypeDto newCase = new CaseAssignmentTypeDto();
        newCase.setCode("TEST");
        newCase.setAssignmentType("Test Case Assignment Type");
        newCase.setProgram(CaseAssignmentTypeDto.ProgramEnum.fromValue("WC"));

        mockMVC.perform(post("/api/v1/case-assignment-types")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();

        //check with filter
        result =  mockMVC.perform(get("/api/v1/case-assignment-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("code", "TEST")
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        casePageDto = convertTo(result, CaseAssignmentTypePageDto.class);

        assertThat(casePageDto.getResults())
            .as("Can't find the Test case assignment Type that was created")
            .hasSize(1);
        assertThat(casePageDto.getResults().get(0).getCode())
            .as("The filtering on code isn't working properly")
            .isEqualTo("TEST");

        // Update Case
        newCase.setAssignmentType("Updated Test Case Assign Type");
        result =  mockMVC.perform(put("/api/v1/case-assignment-types/TEST")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();

        // check with filter
        result =  mockMVC.perform(get("/api/v1/case-assignment-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("code", "TEST")
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        casePageDto = convertTo(result, CaseAssignmentTypePageDto.class);

        assertThat(casePageDto.getResults())
            .as("Can't find the Test case assignment Type that was updated")
            .hasSize(1);
        assertThat(casePageDto.getResults().get(0).getCode())
            .as("The filtering on code isn't working properly")
            .isEqualTo("TEST");

        // Update Case with new Code
        newCase.setCode("TESV");
        result =  mockMVC.perform(put("/api/v1/case-assignment-types/TEST")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();

        // Missing field
        newCase = new CaseAssignmentTypeDto();
        newCase.setCode("TEST");
        newCase.setProgram(CaseAssignmentTypeDto.ProgramEnum.fromValue("BTH"));
        result =  mockMVC.perform(put("/api/v1/case-assignment-types/TEST")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isBadRequest())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).contains("Missing or incorrect values were provided");

        // Delete
        mockMVC.perform(delete("/api/v1/case-assignment-types/TEST")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // check without filter
        result =  mockMVC.perform(get("/api/v1/case-assignment-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        casePageDto = convertTo(result, CaseAssignmentTypePageDto.class);

        assertThat(casePageDto.getTotalElements()).isEqualTo(startingTotalElements);
    }
    //Tests
    // Create Case, what happens when something with the
    // missing field
    @Test
    public void testMissingField() throws Exception {
        String token = getAccessToken();
        CaseAssignmentTypeDto newCase = new CaseAssignmentTypeDto();
        newCase.setCode("TEST");
        newCase.setProgram(CaseAssignmentTypeDto.ProgramEnum.fromValue("WC"));
        MvcResult result =  mockMVC.perform(post("/api/v1/case-assignment-types")
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
        CaseAssignmentTypeDto newCase = new CaseAssignmentTypeDto();
        newCase.setAssignmentType("Test Case Assignment Type");
        newCase.setProgram(CaseAssignmentTypeDto.ProgramEnum.fromValue("WC"));
        
        //grab Case Assignment Type that already exists
        MvcResult result =  mockMVC.perform(get("/api/v1/case-assignment-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        newCase.setCode(convertTo(result, CaseAssignmentTypePageDto.class).getResults().get(0).getCode());
        
        result = mockMVC.perform(post("/api/v1/case-assignment-types")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("A Case Assignment Type with this Code already exists");
    }

    // Delete Case
    // what happens if doesn't exist
    @Test void testNonexistentDelete() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(delete("/api/v1/case-assignment-types/TESV")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isNotFound())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Case Assignment Type with code TESV not found");
    }

    // Change Case
    // Doesn't exist with that code
    @Test void testNonexistentChange() throws Exception {
        String token = getAccessToken();

        CaseAssignmentTypeDto newCase = new CaseAssignmentTypeDto();
        newCase.setAssignmentType("Test Case Assignment Type");
        newCase.setProgram(CaseAssignmentTypeDto.ProgramEnum.fromValue("WC"));
        newCase.setCode("TEST");
        MvcResult result = mockMVC.perform(put("/api/v1/case-assignment-types/TEST")
                        .content(getJson(newCase))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isNotFound())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("The Case Assignment Type with code TEST was not found");
    }
}