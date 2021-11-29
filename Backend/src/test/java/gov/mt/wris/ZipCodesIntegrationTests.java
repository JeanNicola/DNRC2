package gov.mt.wris;

import javax.transaction.Transactional;

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

import java.util.List;

import static org.assertj.core.api.Assertions.*;

import gov.mt.wris.controllers.ZipCodesController;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.ZipCodeDto;
import gov.mt.wris.dtos.ZipCodePageDto;
import gov.mt.wris.models.City;
import gov.mt.wris.repositories.CityRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ZipCodesIntegrationTests extends BaseTestCase{
    @Autowired
    private MockMvc mockMVC;

    @Autowired
    ZipCodesController zipCodesController;

    @Autowired
    CityRepository cityRepository;

    @Test
    @Transactional
    @Rollback
    public void testGetZipCodes() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(get("/api/v1/zip-codes")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        ZipCodePageDto zipCodePageDto = convertTo(result, ZipCodePageDto.class);

        Long startingTotalElements = zipCodePageDto.getTotalElements();

        // Create Zip Code and City
        ZipCodeDto newZip = new ZipCodeDto();
        String zipCode = "00234";
        newZip.setZipCode(zipCode);
        newZip.setCityName("TESTING");
        newZip.setStateCode("WA");

        mockMVC.perform(post("/api/v1/zip-codes")
                        .content(getJson(newZip))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();
        
        // check with filter
        result = mockMVC.perform(get("/api/v1/zip-codes")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("zipCode", zipCode)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        zipCodePageDto = convertTo(result, ZipCodePageDto.class);

        assertThat(zipCodePageDto.getResults())
            .as("Can't find the test Zip Code that was created")
            .hasSize(1);
        assertThat(zipCodePageDto.getResults().get(0).getZipCode())
            .as("The filtering on Zip Code isn't working properly")
            .isEqualTo(zipCode);
        
        long zipId = zipCodePageDto.getResults().get(0).getId();
        
        // Update Zip Code
        newZip.setCityName("PORTSMOUTH");
        newZip.setStateCode("NH");
        result = mockMVC.perform(put("/api/v1/zip-codes/"+zipId)
                        .content(getJson(newZip))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        
        // check with filter
        result = mockMVC.perform(get("/api/v1/zip-codes")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("zipCode", zipCode)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        zipCodePageDto = convertTo(result, ZipCodePageDto.class);

        assertThat(zipCodePageDto.getResults())
            .as("Can't find the test Zip Code that was updated")
            .hasSize(1);
        assertThat(zipCodePageDto.getResults().get(0).getZipCode())
            .as("The filtering on zip code isn't working properly")
            .isEqualTo(zipCode);
        
        // check that city has been deleted
        List<City> oldCity = cityRepository.findByCityNameAndStateCode("TESTING", "WA");
        assertThat(oldCity)
            .as("The old city wasn't deleted")
            .hasSize(0);
        
        // try adding a duplicate
        mockMVC.perform(post("/api/v1/zip-codes")
                        .content(getJson(newZip))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isConflict())
                        .andReturn();
        
        newZip = new ZipCodeDto();
        newZip.setCityName("PORTSMOUTH");
        newZip.setStateCode("NH");
        result = mockMVC.perform(put("/api/v1/zip-codes/"+zipId)
                        .content(getJson(newZip))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isBadRequest())
                        .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).contains("Missing or incorrect values were provided");

        // Delete
        mockMVC.perform(delete("/api/v1/zip-codes/"+zipId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isNoContent());
        
        // check with filter
        result = mockMVC.perform(get("/api/v1/zip-codes")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType("application/json"))
                        .andExpect(status().isOk())
                        .andReturn();
        zipCodePageDto = convertTo(result, ZipCodePageDto.class);

        assertThat(zipCodePageDto.getTotalElements()).isEqualTo(startingTotalElements);
    }

    // Create Case, what happens when something has
    // a missing field
    @Test
    public void testMissingField() throws Exception {
        String token = getAccessToken();
        ZipCodeDto newZip = new ZipCodeDto();
        newZip.setZipCode("TESTS");
        newZip.setStateCode("NH");
        MvcResult result = mockMvc.perform(post("/api/v1/zip-codes")
                            .content(getJson(newZip))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();
        
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).contains("Missing or incorrect values were provided");
    }

    // Delete Case
    // what happens if it doesn't exist
    @Test
    public void testNonExistentDelete() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(delete("/api/v1/zip-codes/500000")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNotFound())
                            .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("This Zip Code could not be found");
    }

    // Delete Case
    // that has Address
    @Test
    public void testDeleteWithAddress() throws Exception {
        String token = getAccessToken();

        MvcResult result = mockMVC.perform(delete("/api/v1/zip-codes/22615")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isConflict())
                            .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("This Zip Code has Addresses associated with it");
    }
}