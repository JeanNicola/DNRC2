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
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class DataSourceIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUsgsQuadMaps() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String examinationId = "93125";
        Long utmpId = 5380L;

        DataSourceCreationDto dataSourceCreationDto = new DataSourceCreationDto();

        dataSourceCreationDto.setSourceType("USQ");

        // Create Data Source
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/examinations/" + examinationId + "/data-sources")
                .content(getJson(dataSourceCreationDto))
                .param("examinationId", String.valueOf(examinationId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DataSourceDto dataSourceDto = convertTo(result, DataSourceDto.class);

        // Test POST

        UsgsCreationDto usgsCreationDto = new UsgsCreationDto();

        usgsCreationDto.setUtmpId(utmpId);

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/usgs-quads")
                .content(getJson(usgsCreationDto))
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        UsgsDto testPOST = convertTo(result, UsgsDto.class);
        assertThat(testPOST.getPexmId()).isEqualTo(dataSourceDto.getPexmId());

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/usgs-quads")
                .header("Authorization", "Bearer " + token)
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;

        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        UsgsPageDto testGET = convertTo(result, UsgsPageDto.class);
        assertThat(testGET.getResults().get(0).getPexmId()).isEqualTo(dataSourceDto.getPexmId());
        assertThat(testGET.getResults().size()).isGreaterThan(0);

        // Test DELETE
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/usgs-quads/" + testPOST.getUtmpId())
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .param("utmpId", String.valueOf(utmpId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Delete Data Source
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/examinations/" + examinationId + "/data-sources/" + dataSourceDto.getPexmId().toString())
                .param("examinationId", String.valueOf(examinationId))
                .param("pexmId", dataSourceDto.getPexmId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testAerialPhotos() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String examinationId = "93125";

        // Create Data Source
        DataSourceCreationDto dataSourceCreationDto = new DataSourceCreationDto();

        dataSourceCreationDto.setSourceType("AER");

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/examinations/" + examinationId + "/data-sources")
                .content(getJson(dataSourceCreationDto))
                .param("examinationId", String.valueOf(examinationId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DataSourceDto dataSourceDto = convertTo(result, DataSourceDto.class);

        // Test POST
        AerialPhotoCreationDto aerialCreationDto = new AerialPhotoCreationDto();

        aerialCreationDto.setTypeCode("USGS");
        aerialCreationDto.setAerialPhotoNumber("11223");
        aerialCreationDto.setAerialPhotoDate("03/03/2020");

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/aerial-photos")
                .content(getJson(aerialCreationDto))
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        AerialPhotoDto testPOST = convertTo(result, AerialPhotoDto.class);
        assertThat(testPOST.getPexmId()).isEqualTo(dataSourceDto.getPexmId());
        assertThat(testPOST.getTypeCode()).isEqualTo("USGS");
        assertThat(testPOST.getAerialPhotoNumber()).isEqualTo("11223");
        assertThat(testPOST.getAerialPhotoDate()).isEqualTo("03/03/2020");

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/aerial-photos")
                .header("Authorization", "Bearer " + token)
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;

        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        AerialPhotoPageDto testGET = convertTo(result, AerialPhotoPageDto.class);
        assertThat(testGET.getResults().get(0).getPexmId()).isEqualTo(dataSourceDto.getPexmId());
        assertThat(testGET.getResults().size()).isGreaterThan(0);

        // Test PUT
        aerialCreationDto.setAerialPhotoNumber("4455");
        aerialCreationDto.setAerialPhotoDate("04/04/2021");

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/aerial-photos/" + testPOST.getAerialId())
                .content(getJson(aerialCreationDto))
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .param("aerialId", String.valueOf(testPOST.getAerialId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        AerialPhotoDto testPUT = convertTo(result, AerialPhotoDto.class);
        assertThat(testPUT.getTypeCode()).isEqualTo("USGS");
        assertThat(testPUT.getAerialPhotoNumber()).isEqualTo("4455");
        assertThat(testPUT.getAerialPhotoDate()).isEqualTo("04/04/2021");

        // Test DELETE
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/aerial-photos/" + testPOST.getAerialId())
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .param("aerialId", String.valueOf(testPOST.getAerialId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Delete Data Source
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/examinations/" + examinationId + "/data-sources/" + dataSourceDto.getPexmId().toString())
                .param("examinationId", String.valueOf(examinationId))
                .param("pexmId", dataSourceDto.getPexmId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testWaterResourceSurvey() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String examinationId = "93125";

        // Create Data Source
        DataSourceCreationDto dataSourceCreationDto = new DataSourceCreationDto();

        dataSourceCreationDto.setSourceType("WRS");

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/examinations/" + examinationId + "/data-sources")
                .content(getJson(dataSourceCreationDto))
                .param("examinationId", String.valueOf(examinationId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DataSourceDto dataSourceDto = convertTo(result, DataSourceDto.class);

        // Test POST
        WaterResourceSurveyCreationDto waterResourceSurveyCreationDtoDto = new WaterResourceSurveyCreationDto();

        waterResourceSurveyCreationDtoDto.setSurveyId(75L);

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/water-resource-surveys")
                .content(getJson(waterResourceSurveyCreationDtoDto))
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterResourceSurveyDto testPOST = convertTo(result, WaterResourceSurveyDto.class);
        assertThat(testPOST.getPexmId()).isEqualTo(dataSourceDto.getPexmId());
        assertThat(testPOST.getSurveyId()).isEqualTo(waterResourceSurveyCreationDtoDto.getSurveyId());

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/water-resource-surveys")
                .header("Authorization", "Bearer " + token)
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;

        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterResourceSurveyPageDto testGET = convertTo(result, WaterResourceSurveyPageDto.class);
        assertThat(testGET.getResults().get(0).getPexmId()).isEqualTo(dataSourceDto.getPexmId());
        assertThat(testGET.getResults().size()).isGreaterThan(0);

        // Test DELETE
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/water-resource-surveys/" + testPOST.getSurveyId())
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .param("surveyId", String.valueOf(testPOST.getSurveyId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Delete Data Source
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/examinations/" + examinationId + "/data-sources/" + dataSourceDto.getPexmId().toString())
                .param("examinationId", String.valueOf(examinationId))
                .param("pexmId", dataSourceDto.getPexmId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testParcels() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId = 676612L;
        float sec;
        String examinationId = "93125";

        LegalLandCreationDto lld = new LegalLandCreationDto();
        lld.setCountyId(36L);
        lld.setTownship(13L);
        lld.setTownshipDirection("N");
        lld.setRange(19L);
        lld.setRangeDirection("W");
        lld.setSection(9L);

        PlaceOfUseCreationDto pou = new PlaceOfUseCreationDto();
        pou.setAcreage(new BigDecimal(100));
        pou.setCountyId(49L);
        pou.setElementOrigin("CLAI");
        pou.setLegalLand(lld);
        pou.setLegalId(657883L);
        pou.setModifiedByThisChange("Y");

        start = System.currentTimeMillis();
        mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/places")
                .content(getJson(pou))
                .param("purposeId", String.valueOf(purposeId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Create Data Source

        DataSourceCreationDto dataSourceCreationDto = new DataSourceCreationDto();

        dataSourceCreationDto.setInvestigationDate(LocalDate.of(2021, 04, 05));
        dataSourceCreationDto.setSourceType("FLD");

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/examinations/" + examinationId + "/data-sources")
                .content(getJson(dataSourceCreationDto))
                .param("examinationId", String.valueOf(examinationId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DataSourceDto dataSourceDto = convertTo(result, DataSourceDto.class);

        // Populate Parcels

        start = System.currentTimeMillis();
        mockMvc.perform(post("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/populate-parcel-records")
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Test GET

        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/parcels")
                .header("Authorization", "Bearer " + token)
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ParcelPageDto testGET = convertTo(result, ParcelPageDto.class);
        assertThat(testGET.getResults().size()).isGreaterThan(0);

        // Test PUT

        ParcelUpdateDto updateDto =  new ParcelUpdateDto();

        updateDto.setExaminedAcreage(new BigDecimal(25));

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/parcels/" + testGET.getResults().get(0).getPlaceId())
                .content(getJson(updateDto))
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .param("placeId", String.valueOf(testGET.getResults().get(0).getPlaceId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ParcelDto testPUT = convertTo(result, ParcelDto.class);
        assertThat(testPUT.getExaminedAcreage().toString()).isEqualTo(updateDto.getExaminedAcreage().toString());

        // Test DELETE
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/data-sources/"+ dataSourceDto.getPexmId() +"/parcels/" + testGET.getResults().get(0).getPlaceId())
                .param("pexmId", String.valueOf(dataSourceDto.getPexmId()))
                .param("placeId", String.valueOf(testGET.getResults().get(0).getPlaceId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Delete Data Source
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/examinations/" + examinationId + "/data-sources/" + dataSourceDto.getPexmId().toString())
                .param("examinationId", String.valueOf(examinationId))
                .param("pexmId", dataSourceDto.getPexmId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Delete POU from Purpose

        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/purposes/" + purposeId + "/places/" + testGET.getResults().get(0).getPlaceId())
                .param("purposeId", String.valueOf(purposeId))
                .param("placeId", String.valueOf(testGET.getResults().get(0).getPlaceId()))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }



}
