package gov.mt.wris;

import gov.mt.wris.dtos.DataSourceCreationDto;
import gov.mt.wris.dtos.DataSourceDto;
import gov.mt.wris.dtos.DataSourcePageDto;
import gov.mt.wris.dtos.ExaminationCreationDto;
import gov.mt.wris.dtos.ExaminationDetailDto;
import gov.mt.wris.dtos.ExaminationsSearchPageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class ExaminationIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSearchExaminations() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String waterRightNumber, basin, waterRightType;

        basin = "76LJ";
        waterRightNumber = "665";
        waterRightType = "STOC";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/examinations")
                .header("Authorization", "Bearer " + token)
                .param("basin", basin)
                .param("waterRightNumber", waterRightNumber)
                .param("waterRightType", waterRightType)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;

        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ExaminationsSearchPageDto testA = convertTo(result, ExaminationsSearchPageDto.class);
        assertThat(testA.getResults().get(0).getPurposeId()).isEqualTo(2327);
        assertThat(testA.getResults().size()).isEqualTo(1);
    }

    @Test
    public void testGETSingleExamination() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        Long examinationId = 93125L;

        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/examinations/" + examinationId)
                .param("examinationId", String.valueOf(examinationId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ExaminationDetailDto dto = convertTo(result, ExaminationDetailDto.class);
        assertThat(dto.getExaminationId()).isEqualTo(examinationId);
        assertThat(dto.getCompleteWaterRightNumber()).isEqualTo("42J 678 02");

    }

    @Test
    public void testUpdateExamination() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long examinationId = 93125L;
        float sec;

        ExaminationCreationDto updateDto = new ExaminationCreationDto();

        updateDto.setBeginDate(LocalDate.of(2020, 3, 3));
        updateDto.setEndDate(LocalDate.of(2020, 4, 4));

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/examinations/" + examinationId)
                .content(getJson(updateDto))
                .param("examinationId", String.valueOf(examinationId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ExaminationDetailDto dto = convertTo(result, ExaminationDetailDto.class);
        assertThat(dto.getBeginDate().toString()).isEqualTo(updateDto.getBeginDate().toString());
        assertThat(dto.getEndDate().toString()).isEqualTo(updateDto.getEndDate().toString());

        updateDto.setBeginDate(LocalDate.of(2020, 9, 2));
        updateDto.setEndDate(LocalDate.of(2020, 10, 2));

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/examinations/" + examinationId)
                .content(getJson(updateDto))
                .param("examinationId", String.valueOf(examinationId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        dto = convertTo(result, ExaminationDetailDto.class);
        assertThat(dto.getBeginDate().toString()).isEqualTo(updateDto.getBeginDate().toString());
        assertThat(dto.getEndDate().toString()).isEqualTo(updateDto.getEndDate().toString());

    }

    @Test
    public void testDataSource() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String examinationId = "93125";

        DataSourceCreationDto dataSourceCreationDto = new DataSourceCreationDto();

        dataSourceCreationDto.setInvestigationDate(LocalDate.of(2021, 04, 05));
        dataSourceCreationDto.setSourceType("FLD");

        // Test POST
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
        DataSourceDto dto = convertTo(result, DataSourceDto.class);
        assertThat(dto.getSourceType()).isEqualTo("FLD");
        assertThat(dto.getInvestigationDate().toString()).isEqualTo(LocalDate.of(2021, 04, 05).toString());

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/examinations/" + examinationId + "/data-sources")
                .header("Authorization", "Bearer " + token)
                .param("examinationId", examinationId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;

        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DataSourcePageDto testGET = convertTo(result, DataSourcePageDto.class);
        assertThat(testGET.getResults().get(0).getExaminationId().toString()).isEqualTo(examinationId);
        assertThat(testGET.getResults().size()).isGreaterThan(0);

        // Test PUT
        dataSourceCreationDto.setInvestigationDate(LocalDate.of(2020, 4, 9));
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/examinations/" + examinationId + "/data-sources/" + dto.getPexmId().toString())
                .content(getJson(dataSourceCreationDto))
                .param("examinationId", String.valueOf(examinationId))
                .param("pexmId", dto.getPexmId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DataSourceDto testPUT = convertTo(result, DataSourceDto.class);
        assertThat(testPUT.getSourceType()).isEqualTo("FLD");
        assertThat(testPUT.getInvestigationDate().toString()).isEqualTo(LocalDate.of(2020, 4, 9).toString());


        // Test DELETE
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/examinations/" + examinationId + "/data-sources/" + dto.getPexmId().toString())
                .param("examinationId", String.valueOf(examinationId))
                .param("pexmId", dto.getPexmId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
    }


}
