package gov.mt.wris;

import gov.mt.wris.dtos.AllEventCodeDescDto;
import gov.mt.wris.dtos.ApplicantsPageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

import gov.mt.wris.controllers.EventTypesController;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.AllApplicationTypesDto;
import gov.mt.wris.dtos.AllCaseTypesDto;
import gov.mt.wris.dtos.AllDecreeTypesDto;
import gov.mt.wris.dtos.ApplicationTypeDto;
import gov.mt.wris.dtos.CaseTypeDto;
import gov.mt.wris.dtos.DecreeTypeDto;
import gov.mt.wris.dtos.EventTypeDto;
import gov.mt.wris.dtos.EventTypePageDto;
import gov.mt.wris.dtos.EventTypeScreenDto;
import gov.mt.wris.dtos.TypeXrefDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventTypesIntegrationTests extends BaseTestCase {
    private static Logger LOGGER = LoggerFactory.getLogger(EventTypesIntegrationTests.class);

    @Autowired
    private MockMvc mockMVC;

    @Autowired
    EventTypesController eventController;

    @PersistenceContext
    private EntityManager manager;

    @Test
    @Rollback(true)
    public void testGetEventTypes() throws Exception {

        String token = getAccessToken();

        MvcResult result =  mockMVC.perform(get("/api/v1/event-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        EventTypePageDto eventPageDto = convertTo(result, EventTypePageDto.class);

        //check if Test is created and fail out if so
        assertThat(eventPageDto.getResults())
            .as("Make sure that a TEST event type hasn't already been created")
            .filteredOn(eventType -> "TEST".equals(eventType.getCode()))
            .hasSize(0);

        Long startingTotalElements = eventPageDto.getTotalElements();

        // Create Case
        EventTypeDto newEvent = new EventTypeDto();
        newEvent.setCode("TEST");
        newEvent.setDescription("Test Case Type");

        mockMVC.perform(post("/api/v1/event-types")
                        .content(getJson(newEvent))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();

        //check with filter
        result =  mockMVC.perform(get("/api/v1/event-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("code", "TEST")
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        eventPageDto = convertTo(result, EventTypePageDto.class);

        assertThat(eventPageDto.getResults())
            .as("Can find the Test event Type that was created")
            .hasSize(1);
        assertThat(eventPageDto.getResults().get(0).getCode())
            .as("The filtering on code is working properly")
            .isEqualTo("TEST");

        // Update Case
        newEvent.setDescription("Updated Test Case Type");
        result =  mockMVC.perform(put("/api/v1/event-types/TEST")
                        .content(getJson(newEvent))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();

        // check with filter
        result =  mockMVC.perform(get("/api/v1/event-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("code", "TEST")
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        eventPageDto = convertTo(result, EventTypePageDto.class);

        assertThat(eventPageDto.getResults())
            .as("Can find the Test case Type that was updated")
            .hasSize(1);
        assertThat(eventPageDto.getResults().get(0).getCode())
            .as("The filtering on code is working properly")
            .isEqualTo("TEST");

        // Update Case with new Code, not allowed
        newEvent.setCode("TESV");
        result =  mockMVC.perform(put("/api/v1/event-types/TEST")
                        .content(getJson(newEvent))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();
        
        // Missing field
        newEvent = new EventTypeDto();
        newEvent.setCode("TEST");
        result =  mockMVC.perform(put("/api/v1/event-types/TESV")
                        .content(getJson(newEvent))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isBadRequest())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).contains("Missing or incorrect values were provided");

        // Check that it's ok that Response Due Days is missing
        newEvent = new EventTypeDto();
        newEvent.setCode("TEST");
        newEvent.setDescription("al;skdjfasdf");
        result =  mockMVC.perform(put("/api/v1/event-types/TEST")
                        .content(getJson(newEvent))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();

        // Delete
        mockMVC.perform(delete("/api/v1/event-types/TEST")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // check without filter
        result =  mockMVC.perform(get("/api/v1/event-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        eventPageDto = convertTo(result, EventTypePageDto.class);

        assertThat(eventPageDto.getTotalElements()).isEqualTo(startingTotalElements);
    }

    // Create Event, what happens when something with the
    // missing field
    @Test
    public void testMissingField() throws Exception {
        String token = getAccessToken();
        EventTypeDto newEvent = new EventTypeDto();
        newEvent.setCode("TEST");
        MvcResult result =  mockMVC.perform(post("/api/v1/event-types")
                        .content(getJson(newEvent))
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
        EventTypeDto newEvent = new EventTypeDto();
        newEvent.setDescription("Test Event Type");
        
        //grab Case Type that already exists
        MvcResult result =  mockMVC.perform(get("/api/v1/event-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        newEvent.setCode(convertTo(result, EventTypePageDto.class).getResults().get(0).getCode());
        
        result = mockMVC.perform(post("/api/v1/event-types")
                        .content(getJson(newEvent))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("An Event Type with Code " + newEvent.getCode() + " already exists");
    }

    // Delete Case
    // what happens if doesn't exist
    @Test void testNonexistentDelete() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(delete("/api/v1/event-types/TESV")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isNotFound())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Event Type with code TESV not found");
    }

    // Delete Event
    // what happens if we try to delete something that has Case Type xrefs
    @Test
    public void testCantDelete() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(delete("/api/v1/event-types/STL")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("STL has multiple application, case or decree types associated with it");
    }

    // Change Case
    // Doesn't exist with that code
    @Test void testNonexistentChange() throws Exception {
        String token = getAccessToken();

        EventTypeDto newEvent = new EventTypeDto();
        newEvent.setDescription("Test Event Type");
        newEvent.setCode("TESV");
        MvcResult result = mockMVC.perform(put("/api/v1/event-types/TESV")
                        .content(getJson(newEvent))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isNotFound())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("The Event Type with code TESV was not found");
    }

    // Case Xref Tests
    // Grab a random Event Types
    // then add a test case xref to it
    @Test
    // @Transactional
    @Rollback(true)
    public void testInsertDeleteCaseXref() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(get("/api/v1/event-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        EventTypePageDto eventPageDto = convertTo(result, EventTypePageDto.class);

        assertThat(eventPageDto.getTotalElements()).isGreaterThan(0L);

        String eventCode = eventPageDto.getResults().get(0).getCode();
        String caseCode = "SCHD";
        LOGGER.info("Code: "+eventCode);

        TypeXrefDto newCaseXref = new TypeXrefDto();
        newCaseXref.setCode(caseCode);

        result = mockMVC.perform(post("/api/v1/event-types/"+eventCode+"/case-types")
                        .content(getJson(newCaseXref))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();
        
        // check
        result = mockMVC.perform(get("/api/v1/event-types/"+eventCode + "/case-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        
        List<CaseTypeDto> dtos = convertTo(result, AllCaseTypesDto.class)
            .getResults()
            .stream()
            .filter(xref -> xref.getCode().equals(caseCode))
            .collect(Collectors.toList());
        
        assertThat(dtos).hasSize(1);

        mockMVC.perform(delete("/api/v1/event-types/"+eventCode+"/case-types/"+caseCode)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

        // check
        result = mockMVC.perform(get("/api/v1/event-types/"+eventCode + "/case-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        
        dtos = convertTo(result, AllCaseTypesDto.class)
            .getResults()
            .stream()
            .filter(xref -> xref.getCode().equals(caseCode))
            .collect(Collectors.toList());
        
        assertThat(dtos).hasSize(0);
    }

    // Decree Xref Tests
    // Grab a random Event Types
    // then add a test decree xref to it
    @Test
    // @Transactional
    @Rollback(true)
    public void testInsertDeleteDecreeXref() throws Exception {
        String token = getAccessToken();

        String eventCode = "OOS";
        String decreeCode = "PRLM";
        LOGGER.info("Code: "+eventCode);

        TypeXrefDto newDecreeXref = new TypeXrefDto();
        newDecreeXref.setCode(decreeCode);

        MvcResult result = mockMVC.perform(post("/api/v1/event-types/"+eventCode+"/decree-types")
                        .content(getJson(newDecreeXref))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();
        
        // check
        result = mockMVC.perform(get("/api/v1/event-types/"+eventCode + "/decree-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        
        List<DecreeTypeDto> dtos = convertTo(result, AllDecreeTypesDto.class)
            .getResults()
            .stream()
            .filter(xref -> xref.getCode().equals(decreeCode))
            .collect(Collectors.toList());
        
        assertThat(dtos).hasSize(1);

        mockMVC.perform(delete("/api/v1/event-types/"+eventCode+"/decree-types/"+decreeCode)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

        // check
        result = mockMVC.perform(get("/api/v1/event-types/"+eventCode + "/decree-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        
        dtos = convertTo(result, AllDecreeTypesDto.class)
            .getResults()
            .stream()
            .filter(xref -> xref.getCode().equals(decreeCode))
            .collect(Collectors.toList());
        
        assertThat(dtos).hasSize(0);
    }

    // Application Xref Tests
    // Grab a random Event Types
    // then add a test application xref to it
    @Test
    // @Transactional
    @Rollback(true)
    public void testInsertDeleteApplicationXref() throws Exception {
        String token = getAccessToken();

        String eventCode = "STL";
        String applicationCode = "606";
        LOGGER.info("Code: "+eventCode);

        TypeXrefDto newApplicationXref = new TypeXrefDto();
        newApplicationXref.setCode(applicationCode);

        MvcResult result = mockMVC.perform(post("/api/v1/event-types/"+eventCode+"/application-types")
                        .content(getJson(newApplicationXref))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();
        
        // check
        result = mockMVC.perform(get("/api/v1/event-types/"+eventCode + "/application-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        
        List<ApplicationTypeDto> dtos = convertTo(result, AllApplicationTypesDto.class)
            .getResults()
            .stream()
            .filter(xref -> xref.getCode().equals(applicationCode))
            .collect(Collectors.toList());
        
        assertThat(dtos).hasSize(1);

        mockMVC.perform(delete("/api/v1/event-types/"+eventCode+"/application-types/"+applicationCode)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

        // check
        result = mockMVC.perform(get("/api/v1/event-types/"+eventCode + "/case-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        
        List<CaseTypeDto> caseDtos = convertTo(result, AllCaseTypesDto.class)
            .getResults()
            .stream()
            .filter(xref -> xref.getCode().equals(applicationCode))
            .collect(Collectors.toList());
        
        assertThat(caseDtos).hasSize(0);
    }

    @Test
    @Rollback
    public void testDeleteWithOneSubType() throws Exception {
        String token = getAccessToken();

        EventTypeDto newEvent = new EventTypeDto();
        newEvent.setCode("TEST");
        newEvent.setDescription("Test Case Type");

        mockMVC.perform(post("/api/v1/event-types")
                        .content(getJson(newEvent))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();

        //check with filter
        MvcResult result =  mockMVC.perform(get("/api/v1/event-types")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("code", "TEST")
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        EventTypePageDto eventPageDto = convertTo(result, EventTypePageDto.class);

        assertThat(eventPageDto.getResults())
            .as("Can't find the Test event Type that was created")
            .hasSize(1);


        TypeXrefDto newApplicationXref = new TypeXrefDto();
        newApplicationXref.setCode("606");
        result = mockMVC.perform(post("/api/v1/event-types/TEST/application-types")
                        .content(getJson(newApplicationXref))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();
        // check
        result = mockMVC.perform(get("/api/v1/event-types/TEST/application-types")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        
        List<ApplicationTypeDto> dtos = convertTo(result, AllApplicationTypesDto.class)
            .getResults()
            .stream()
            .filter(xref -> xref.getCode().equals("606"))
            .collect(Collectors.toList());
        
        assertThat(dtos).hasSize(1);

        // check that delete works
        mockMVC.perform(delete("/api/v1/event-types/TEST")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isNoContent())
                        .andReturn();
    }

    @Test
    public void testGetEventTypesForCaseSchedule() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String typeCode = "SCHD";
        float sec;

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
        assertThat(testA.getResults().get(0).getCode()).isEqualTo("FLDV");
        assertThat(testA.getResults().get(0).getDescription()).isEqualTo("FIELD VISIT");
        assertThat(testA.getResults().get(1).getCode()).isEqualTo("MLIM");
        assertThat(testA.getResults().get(1).getDescription()).isEqualTo("FILING: MOTION IN LIMINE");
        assertThat(testA.getResults().get(2).getCode()).isEqualTo("HRSH");
        assertThat(testA.getResults().get(2).getDescription()).isEqualTo("HEARING SCHEDULED FOR");
        assertThat(testA.getResults().get(3).getCode()).isEqualTo("ORAH");
        assertThat(testA.getResults().get(3).getDescription()).isEqualTo("ORAL ARGUEMENT HEARING");
        assertThat(testA.getResults().get(4).getCode()).isEqualTo("OASH");
        assertThat(testA.getResults().get(4).getDescription()).isEqualTo("ORAL ARGUEMENT HEARING SCHEDULED FOR");
        assertThat(testA.getResults().get(5).getCode()).isEqualTo("PCSD");
        assertThat(testA.getResults().get(5).getDescription()).isEqualTo("PREHEARING CONFERENCE SCHEDULED");

    }

}