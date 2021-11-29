package gov.mt.wris;

import gov.mt.wris.dtos.CopyDiversionToPeriodResultsDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.PeriodOfUseDto;
import gov.mt.wris.dtos.PeriodOfUseUpdateDto;
import gov.mt.wris.dtos.PeriodsOfUsePageDto;
import gov.mt.wris.dtos.PurposeDetailDto;
import gov.mt.wris.dtos.WaterRightVersionPurposeCreationDto;
import gov.mt.wris.services.PurposeService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class PeriodOfUseIntegrationTests  extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurposeService purposeService;

    @Test
    @Rollback
    public void testCopyPeriodOfDiversion() throws Exception {

        String token = getAccessToken();
        Message message;
        MvcResult result = null;
        long start, end, waterRightId, versionId;
        String purposeId;
        float sec;

        WaterRightVersionPurposeCreationDto purpose = new WaterRightVersionPurposeCreationDto();
        purpose.setPurposeCode("CM");
        purpose.setHousehold(1);
        purpose.setClimaticCode("4");
        purpose.setPurposeOrigin("CLAI");
        waterRightId = 1476L;
        versionId = 1L;

        start = System.currentTimeMillis();
        result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/water-rights/"+waterRightId+"/versions/"+versionId+"/purposes")
                .header("Authorization", "Bearer " + token)
                .content(getJson(purpose))
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionId", String.valueOf(versionId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PurposeDetailDto testA = convertTo(result, PurposeDetailDto.class);

        start = System.currentTimeMillis();
        result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/purposes/" + testA.getPurposeId() + "/diversions/copy")
                        .content(getJson(null))
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                        .andExpect(status().isCreated())
                        .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CopyDiversionToPeriodResultsDto testB = convertTo(result, CopyDiversionToPeriodResultsDto.class);
        assertThat(testB.getPeriods().get(0).getPurposeId()).isEqualTo(testA.getPurposeId());
        assertThat(testB.getPeriods().get(0).getBeginDate()).isEqualTo("2021-01-01");
        assertThat(testB.getPeriods().get(0).getEndDate()).isEqualTo("2021-12-31");
        assertThat(testB.getPeriods().get(0).getElementOrigin()).isEqualTo("ISSU");
        assertThat(testB.getPeriods().get(0).getElementOriginDescription()).isEqualTo("AS ISSUED");

        purposeId = "668636";
        start = System.currentTimeMillis();
        result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/purposes/" + purposeId + "/diversions/copy")
                .content(getJson(null))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("No Period of Use records can exist to perform the copy.");

    }

    @Test
    @Rollback
    public void testGetPeriodOfUse() throws Exception {

        String token = getAccessToken();
        Message message;
        MvcResult result = null;
        long start, end;
        Long periodId;
        float sec;

        periodId = 1821355L;
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/periods/" + periodId)
                .header("Authorization", "Bearer " + token)
                .param("periodId", String.valueOf(periodId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PeriodOfUseDto testA = convertTo(result, PeriodOfUseDto.class);
        assertThat(testA.getPurposeId()).isEqualTo(668679);
        assertThat(testA.getPeriodId()).isEqualTo(1821355);
        assertThat(testA.getElementOrigin()).isEqualTo("CLAI");
        assertThat(testA.getElementOriginDescription()).isEqualTo("CLAIMED");
        assertThat(testA.getWaterRightId()).isEqualTo(317527);
        assertThat(testA.getVersionId()).isEqualTo(1);

    }

    @Test
    @Rollback
    public void testUpdatePeriodOfUse() throws Exception {

        String token = getAccessToken();
        Message message;
        MvcResult result = null;
        long start, end;
        String periodId;
        float sec;

        PeriodOfUseUpdateDto update = new PeriodOfUseUpdateDto();

        periodId = "1821382";
        update.setLeaseYear("20");
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/periods/" + periodId)
                .header("Authorization", "Bearer " + token)
                .content(getJson(update))
                .param("periodId", String.valueOf(periodId))
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Invalid Lease Year '20', value must be '1ST' or '2ND'.");

        update.setLeaseYear("1ST");
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/periods/" + periodId)
                .header("Authorization", "Bearer " + token)
                .content(getJson(update))
                .param("periodId", String.valueOf(periodId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PeriodOfUseDto testA = convertTo(result, PeriodOfUseDto.class);
        assertThat(testA.getPurposeId()).isEqualTo(668757);
        assertThat(testA.getPeriodId()).isEqualTo(1821382);
        assertThat(testA.getLeaseYear()).isEqualTo("1ST");

    }

    @Test
    @Rollback
    public void testDeletePeriodOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Long periodId = 523221L;

        result = mockMvc.perform(delete("/api/v1/periods/" + periodId)
                .header("Authorization", "Bearer " + token)
                .param("periodId", String.valueOf(periodId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

        result = mockMvc.perform(get("/api/v1/periods/" + periodId)
                .header("Authorization", "Bearer " + token)
                .param("periodId", String.valueOf(periodId))
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();

    }

}
