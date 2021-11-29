package gov.mt.wris;

import gov.mt.wris.dtos.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class EnforcementIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSearchEnforcements() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String enforcementArea, enforcementName, enforcementNumber, basin, waterNumber;
        float sec;
        String sort = "";

        sort = "?sortDirection=ASC&sortColumn=ENFORCEMENTNAME";
        enforcementArea = "E009";
        enforcementName = "%CREEK";
        enforcementNumber = "%";
        basin = "43A";
        waterNumber = "%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/enforcements" + sort)
                .header("Authorization", "Bearer " + token)
                .param("enforcementArea", enforcementArea)
                .param("enforcementName", enforcementName)
                .param("basin", basin)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EnforcementsSearchPageDto testA = convertTo(result, EnforcementsSearchPageDto.class);
        assertThat(testA.getResults().get(0).getEnforcementArea()).isEqualTo("E009");
        assertThat(testA.getResults().get(0).getEnforcementName()).isEqualTo("43A ROCK CREEK");
        assertThat(testA.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("43A 192328 00");
        assertThat(testA.getResults().get(24).getEnforcementArea()).isEqualTo("E009");
        assertThat(testA.getResults().get(24).getEnforcementName()).isEqualTo("43A ROCK CREEK");
        assertThat(testA.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("43A 191907 00");

    }

    @Test
    public void testGetEnforcementPods() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String areaId;
        float sec;
        String sort = "";

        sort = "?sortDirection=ASC&sortColumn=PODNUMBER";
        areaId = "E009";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/enforcements/" + areaId + "/pods" + sort)
                .header("Authorization", "Bearer " + token)
                .param("areaId", areaId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EnforcementPodPageDto testA = convertTo(result, EnforcementPodPageDto.class);
        assertThat(testA.getResults().get(0).getPodId()).isEqualTo(630018);
        assertThat(testA.getResults().get(0).getPodNumber()).isEqualTo(2);
        assertThat(testA.getResults().get(0).getMajorTypeDescription()).isEqualTo("SURFACE");
        assertThat(testA.getResults().get(0).getLegalLandDescription()).isEqualTo("W2 SW 11 1 N 9 E PARK MT");
        assertThat(testA.getResults().get(0).getMeansOfDiversionDescription()).isEqualTo("PUMP");
        assertThat(testA.getResults().get(0).getEnforcementNumber()).isEqualTo("001P");
        assertThat(testA.getResults().get(0).getDitchName()).isNull();
        assertThat(testA.getResults().get(0).getDitchLegalLandDescription()).isNull();
        assertThat(testA.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("43A 192328 00");
        assertThat(testA.getResults().get(0).getCompleteVersion()).isEqualTo("POST DECREE 2 ACTIVE");

    }

    @Test
    public void testGetEnforcement() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String areaId;
        float sec;
        areaId = "E009";

        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/enforcements/" + areaId)
                .header("Authorization", "Bearer " + token)
                .param("areaId", areaId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EnforcementDto testA = convertTo(result, EnforcementDto.class);
        assertThat(testA.getAreaId()).isEqualTo(areaId);
        assertThat(testA.getName()).isEqualTo("43A ROCK CREEK");

    }

    @Test
    public void testUpdateEnforcementArea() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String areaId;
        float sec;
        areaId = "E009";

        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/enforcements/" + areaId)
                .header("Authorization", "Bearer " + token)
                .param("areaId", areaId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EnforcementDto oldArea = convertTo(result, EnforcementDto.class);

        EnforcementDto dto = new EnforcementDto();
        dto.setAreaId(areaId);
        dto.setName("TEST01");
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/enforcements/" + areaId)
                .header("Authorization", "Bearer " + token)
                .content(getJson(dto))
                .param("areaId", areaId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EnforcementDto updatedDto = convertTo(result, EnforcementDto.class);
        assertThat(updatedDto.getAreaId()).isEqualTo(areaId);
        assertThat(updatedDto.getName()).isEqualTo("TEST01");

        dto.setName(oldArea.getName());
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/enforcements/" + areaId)
                .header("Authorization", "Bearer " + token)
                .content(getJson(dto))
                .param("areaId", areaId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EnforcementDto resetDto = convertTo(result, EnforcementDto.class);
        assertThat(resetDto.getAreaId()).isEqualTo(areaId);
        assertThat(resetDto.getName()).isEqualTo(oldArea.getName());

    }

}
