package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.mt.wris.dtos.EligibleApplicationsSearchPageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.controllers.ApplicationsController;
import gov.mt.wris.dtos.CategoriesPageDto;
import gov.mt.wris.dtos.ObjectionsPageDto;
import gov.mt.wris.dtos.ObjectorsPageDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationObjectionsIntegrationTests extends BaseTestCase{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ApplicationsController appController;

    @Test
    @Rollback
    public void testObjectionsCategoriesAndObjectors() throws Exception {
        String token = getAccessToken();

        String SERVICE_URL = "/api/v1/applications/14307200";

        MvcResult result = mockMvc.perform(get(SERVICE_URL + "/objections").header("Authorization", "Bearer " + token)
                .contentType("application/json")).andExpect(status().isOk()).andReturn();
        ObjectionsPageDto dtos = convertTo(result, ObjectionsPageDto.class);
        assertThat(dtos.getResults().getDetails()).as("Can't find any objection.").hasSizeGreaterThan(0);

        Long objectionId = dtos.getResults().getDetails().get(0).getId();

        result = mockMvc
                .perform(get(SERVICE_URL + "/objections/" + objectionId + "/objectors")
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andExpect(status().isOk()).andReturn();
        ObjectorsPageDto dtos2 = convertTo(result, ObjectorsPageDto.class);
        assertThat(dtos2.getResults()).as("Can't find any objector.").hasSizeGreaterThan(0);

        result = mockMvc
                .perform(get(SERVICE_URL + "/objections/" + objectionId + "/criteria")
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andExpect(status().isOk()).andReturn();
        CategoriesPageDto dtos3 = convertTo(result, CategoriesPageDto.class);
        assertThat(dtos3.getResults()).as("Can't find any category.").hasSizeGreaterThan(0);
    }


    @Test
    public void testGetEligibleApplications() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String applicationId;
        float sec;
        String sort = "";

        // APPLICATIONID, APPLICATIONTYPEDESCRIPTION
        sort = "?sortDirection=ASC&sortColumn=APPLICATIONID&pageNumber=1";
        applicationId = "16%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/applications/eligible-applications" + sort)
                .header("Authorization", "Bearer " + token)
                .param("applicationId", applicationId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EligibleApplicationsSearchPageDto testA = convertTo(result, EligibleApplicationsSearchPageDto.class);
        assertThat(testA.getResults().get(0).getApplicationId()).isEqualTo(1600L);
        assertThat(testA.getResults().get(0).getApplicationTypeDescription()).isEqualTo("APPLICATION FOR BENEFICIAL WATER USE PERMIT");
        assertThat(testA.getResults().get(24).getApplicationId()).isEqualTo(164700L);
        assertThat(testA.getResults().get(24).getApplicationTypeDescription()).isEqualTo("APPLICATION FOR BENEFICIAL WATER USE PERMIT");

        sort = "?sortDirection=ASC&sortColumn=APPLICATIONID&pageNumber=13";
        applicationId = "16%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/applications/eligible-applications" + sort)
                .header("Authorization", "Bearer " + token)
                .param("applicationId", applicationId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EligibleApplicationsSearchPageDto testB = convertTo(result, EligibleApplicationsSearchPageDto.class);
        assertThat(testB.getResults().get(0).getApplicationId()).isEqualTo(16827100L);
        assertThat(testB.getResults().get(0).getApplicationTypeDescription()).isEqualTo("APPLICATION TO CHANGE A WATER RIGHT");
        assertThat(testB.getResults().get(7).getApplicationId()).isEqualTo(16996300L);
        assertThat(testB.getResults().get(7).getApplicationTypeDescription()).isEqualTo("APPLICATION TO CHANGE A WATER RIGHT");

    }

}
