package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.MeasurementDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.VersionMeasurementReportDto;
import gov.mt.wris.services.VersionMeasurementService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class VersionMeasurementReportTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VersionMeasurementService measurementService;

    @Test
    public void testCreateReport() throws Exception {
        String token = getAccessToken();

        VersionMeasurementReportDto creationDto = new VersionMeasurementReportDto()
            .remarkCode("TY")
            .reportTypeCode("2")
            .effectiveDate(LocalDate.of(2010, 1, 24))
            .endDate(LocalDate.of(2010, 4, 24));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports")
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isCreated())
                                .andReturn();
        
        VersionMeasurementReportDto dto = convertTo(result, VersionMeasurementReportDto.class);

        assertThat(dto.getRemarkId()).isNotNull();
        assertThat(dto.getReportTypeCode()).isEqualTo("2");
        assertThat(dto.getEffectiveDate()).isEqualTo(LocalDate.of(2010, 1, 24));

        creationDto.reportTypeCode("4").effectiveDate(LocalDate.of(2010, 3, 24));

        result = mockMvc.perform(put("/api/v1/water-rights/403592/versions/1/measurement-reports/" + dto.getRemarkId())
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        dto = convertTo(result, VersionMeasurementReportDto.class);

        assertThat(dto.getReportTypeCode()).isEqualTo("4");
        assertThat(dto.getEffectiveDate()).isEqualTo(LocalDate.of(2010, 3, 24));

        mockMvc.perform(delete("/api/v1/water-rights/403592/versions/1/measurement-reports/" + dto.getRemarkId())
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();
    }

    @Test
    public void testNotEndDating() throws Exception {
        String token = getAccessToken();

        VersionMeasurementReportDto creationDto = new VersionMeasurementReportDto()
            .remarkCode("TY")
            .reportTypeCode("2")
            .effectiveDate(LocalDate.of(2010, 1, 24));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports")
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();
    }

    @Test
    public void testFutureEndDate() throws Exception {
        String token = getAccessToken();

        VersionMeasurementReportDto creationDto = new VersionMeasurementReportDto()
            .remarkCode("TY")
            .reportTypeCode("2")
            .effectiveDate(LocalDate.of(2010, 1, 24))
            .endDate(LocalDate.of(2099, 4, 24));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports")
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();
    }

    @Test
    public void testPrematureEndDate() throws Exception {
        String token = getAccessToken();

        VersionMeasurementReportDto creationDto = new VersionMeasurementReportDto()
            .remarkCode("TY")
            .reportTypeCode("2")
            .effectiveDate(LocalDate.of(2010, 1, 24))
            .endDate(LocalDate.of(1999, 4, 24));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports")
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();
    }

    @Test
    public void testRemovingEndDate() throws Exception {
        String token = getAccessToken();

        VersionMeasurementReportDto creationDto = new VersionMeasurementReportDto()
            .remarkCode("TY")
            .reportTypeCode("2")
            .effectiveDate(LocalDate.of(2010, 1, 24))
            .endDate(LocalDate.of(2010, 4, 24));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports")
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isCreated())
                                .andReturn();
        
        VersionMeasurementReportDto dto = convertTo(result, VersionMeasurementReportDto.class);

        assertThat(dto.getRemarkId()).isNotNull();
        assertThat(dto.getReportTypeCode()).isEqualTo("2");
        assertThat(dto.getEffectiveDate()).isEqualTo(LocalDate.of(2010, 1, 24));

        creationDto.endDate(null);

        result = mockMvc.perform(put("/api/v1/water-rights/403592/versions/1/measurement-reports/" + dto.getRemarkId())
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();
        
        measurementService.deleteMeasurementReportAndDescendants(dto.getRemarkId());
    }

    @Test
    public void testCreateMeasurement() throws Exception {
        String token = getAccessToken();

        VersionMeasurementReportDto creationDto = new VersionMeasurementReportDto()
            .remarkCode("TY")
            .reportTypeCode("2")
            .effectiveDate(LocalDate.of(2010, 1, 24))
            .endDate(LocalDate.of(2010, 4, 24));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports")
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isCreated())
                                .andReturn();
        
        VersionMeasurementReportDto dto = convertTo(result, VersionMeasurementReportDto.class);

        assertThat(dto.getRemarkId()).isNotNull();

        MeasurementDto measurementCreationDto = new MeasurementDto()
            .year(2019)
            .flowRate(2.9d)
            .unit("GPM");

        result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports/" + dto.getRemarkId() + "/measurements")
                                .content(getJson(measurementCreationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isCreated())
                                .andReturn();
        
        MeasurementDto measurementDto = convertTo(result, MeasurementDto.class);

        assertThat(measurementDto.getId()).isNotNull();
        assertThat(measurementDto.getFlowRate()).isEqualTo(2.9d);
        assertThat(measurementDto.getUnit()).isEqualTo("GPM");
        assertThat(measurementDto.getYear()).isEqualTo(2019);

        measurementCreationDto.setFlowRate(5.3d);

        result = mockMvc.perform(put("/api/v1/water-rights/403592/versions/1/measurement-reports/" + dto.getRemarkId() + "/measurements/" + measurementDto.getId())
                                .content(getJson(measurementCreationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        measurementDto = convertTo(result, MeasurementDto.class);
        
        assertThat(measurementDto.getFlowRate()).isEqualTo(5.3d);

        mockMvc.perform(delete("/api/v1/water-rights/403592/versions/1/measurement-reports/" + dto.getRemarkId() + "/measurements/" + measurementDto.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        measurementService.deleteMeasurementReportAndDescendants(dto.getRemarkId());
    }

    @Test
    public void testMeasurementFutureYear() throws Exception {
        String token = getAccessToken();

        VersionMeasurementReportDto creationDto = new VersionMeasurementReportDto()
            .remarkCode("TY")
            .reportTypeCode("2")
            .effectiveDate(LocalDate.of(2010, 1, 24))
            .endDate(LocalDate.of(2010, 4, 24));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports")
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isCreated())
                                .andReturn();
        
        VersionMeasurementReportDto dto = convertTo(result, VersionMeasurementReportDto.class);

        assertThat(dto.getRemarkId()).isNotNull();

        MeasurementDto measurementCreationDto = new MeasurementDto()
            .year(2099)
            .flowRate(2.9d)
            .unit("GPM");

        result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports/" + dto.getRemarkId() + "/measurements")
                                .content(getJson(measurementCreationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();

        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("The Year cannot be in the future");
    }

    @Test
    public void testMeasurementYearBeforeOperatingAuthority() throws Exception {
        String token = getAccessToken();

        VersionMeasurementReportDto creationDto = new VersionMeasurementReportDto()
            .remarkCode("TY")
            .reportTypeCode("2")
            .effectiveDate(LocalDate.of(2010, 1, 24))
            .endDate(LocalDate.of(2010, 4, 24));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports")
                                .content(getJson(creationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isCreated())
                                .andReturn();
        
        VersionMeasurementReportDto dto = convertTo(result, VersionMeasurementReportDto.class);

        assertThat(dto.getRemarkId()).isNotNull();

        MeasurementDto measurementCreationDto = new MeasurementDto()
            .year(1800)
            .flowRate(2.9d)
            .unit("GPM");

        result = mockMvc.perform(post("/api/v1/water-rights/403592/versions/1/measurement-reports/" + dto.getRemarkId() + "/measurements")
                                .content(getJson(measurementCreationDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();

        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("The Year cannot be before the Operating Authority Date");
    }
}
