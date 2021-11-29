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

import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.PeriodOfDiversionDto;
import gov.mt.wris.dtos.PeriodOfDiversionPageDto;
import gov.mt.wris.dtos.PodAddressUpdateDto;
import gov.mt.wris.dtos.PodCreationDto;
import gov.mt.wris.dtos.PodDetailsDto;
import gov.mt.wris.dtos.PodDetailsDtoResults;
import gov.mt.wris.dtos.PodDetailsUpdateDto;
import gov.mt.wris.dtos.PodDto;
import gov.mt.wris.dtos.PodEnforcementDto;
import gov.mt.wris.dtos.PodEnforcementsPageDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class VersionPodTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMvc;

    // Main Tests
    @Test
    public void testPODCud() throws Exception {
        String token = getAccessToken();

        // Pod Creation
        PodCreationDto creationDto = new PodCreationDto()
            .section(17L)
            .township(34L)
            .townshipDirection("N")
            .range(48L)
            .rangeDirection("E")
            .countyId(14L)
            .sourceOriginCode("ISSU")
            .unnamedTributary(true)
            .sourceId(92838L)
            .majorTypeCode("G")
            .meansOfDiversionCode("IN")
            .podTypeCode("SECD")
            .podOriginCode("ISSU");
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/486971/versions/1/pods")
                            .content(getJson(creationDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
        
        PodDto savedDto = convertTo(result, PodDto.class);

        assertThat(savedDto.getPodId()).isNotNull();

        Long id = savedDto.getPodId();

        // Pod Details update
        PodDetailsUpdateDto updateDto = new PodDetailsUpdateDto()
            .section(15L)
            .township(9L)
            .townshipDirection("S")
            .range(62L)
            .rangeDirection("E")
            .countyId(10L)
            .podOriginCode("ISSU")
            .meansOfDiversionCode("IN")
            .podTypeCode("SECD");

        mockMvc.perform(put("/api/v1/water-rights/486971/versions/1/pods/" + id + "/details")
                            .content(getJson(updateDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/pods/" + id)
                            .content(getJson(updateDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        PodDetailsDto dto = convertTo(result, PodDetailsDto.class);
        PodDetailsDtoResults results = dto.getResults();

        assertThat(results.getLegalLandDescription()).isEqualTo("15 9 S 62 E CARTER MT");

        // Address Creation, Update and Deletion

        // Address Creation
        PodAddressUpdateDto addressUpdateDto = new PodAddressUpdateDto()
            .addressLine("TESTING")
            .zipCodeId(30300L);

        mockMvc.perform(put("/api/v1/water-rights/486971/versions/1/pods/" + id + "/address")
                            .content(getJson(addressUpdateDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();
        result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/pods/" + id)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        dto = convertTo(result, PodDetailsDto.class);
        results = dto.getResults();

        assertThat(results.getFullAddress()).isEqualTo("TESTING\nRICHARDSON TEXAS 75081");

        // Update address
        addressUpdateDto = new PodAddressUpdateDto()
            .addressLine("TEST")
            .zipCodeId(30300L);

        mockMvc.perform(put("/api/v1/water-rights/486971/versions/1/pods/" + id + "/address")
                            .content(getJson(addressUpdateDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/pods/" + id)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        dto = convertTo(result, PodDetailsDto.class);
        results = dto.getResults();

        assertThat(results.getFullAddress()).isEqualTo("TEST\nRICHARDSON TEXAS 75081");

        // Delete address
        addressUpdateDto = new PodAddressUpdateDto();

        mockMvc.perform(put("/api/v1/water-rights/486971/versions/1/pods/" + id + "/address")
                            .content(getJson(addressUpdateDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/pods/" + id)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        dto = convertTo(result, PodDetailsDto.class);
        results = dto.getResults();

        assertThat(results.getFullAddress()).isNull();

        // delete, cleanup
        mockMvc.perform(delete("/api/v1/water-rights/486971/versions/1/pods/" + id)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();
    }

    // Period of Diversion tests
    @Test
    public void testCreatePeriod() throws Exception {
        String token = getAccessToken();

        LocalDate beginDate = LocalDate.of(2021, 10, 1);
        LocalDate endDate = LocalDate.of(2021, 11, 1);
        PeriodOfDiversionDto dto = new PeriodOfDiversionDto()
            .beginDate(beginDate)
            .endDate(endDate)
            .diversionOriginCode("ISSU")
            .flowRate(3.5d);

        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/410907/versions/1/pods/962183/periods")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isCreated())
                                        .andReturn();
        PeriodOfDiversionDto period = convertTo(result, PeriodOfDiversionDto.class);

        Long id = period.getPeriodId();

        result = mockMvc.perform(get("/api/v1/water-rights/410907/versions/1/pods/962183/periods")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        PeriodOfDiversionPageDto page = convertTo(result, PeriodOfDiversionPageDto.class);

        period = page.getResults().stream().filter(e -> id.equals(e.getPeriodId())).findFirst().get();

        assertThat(period.getBeginDate()).isEqualTo(beginDate);
        assertThat(period.getEndDate()).isEqualTo(endDate);
        assertThat(period.getDiversionOriginCode()).isEqualTo("ISSU");
        assertThat(period.getFlowRate()).isEqualTo(3.5d);
        
        dto.setFlowRate(8.5d);

        mockMvc.perform(put("/api/v1/water-rights/410907/versions/1/pods/962183/periods/" + id)
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();

        result = mockMvc.perform(get("/api/v1/water-rights/410907/versions/1/pods/962183/periods")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        page = convertTo(result, PeriodOfDiversionPageDto.class);

        period = page.getResults().stream().filter(e -> id.equals(e.getPeriodId())).findFirst().get();

        assertThat(period.getBeginDate()).isEqualTo(beginDate);
        assertThat(period.getEndDate()).isEqualTo(endDate);
        assertThat(period.getDiversionOriginCode()).isEqualTo("ISSU");
        assertThat(period.getFlowRate()).isEqualTo(8.5d);

        mockMvc.perform(delete("/api/v1/water-rights/410907/versions/1/pods/962183/periods/" + id)
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();
    }

    @Test
    public void testWrongCompact() throws Exception {
        String token = getAccessToken();

        LocalDate beginDate = LocalDate.of(2021, 10, 1);
        LocalDate endDate = LocalDate.of(2021, 11, 1);
        PeriodOfDiversionDto dto = new PeriodOfDiversionDto()
            .beginDate(beginDate)
            .endDate(endDate)
            .diversionOriginCode("CMPT")
            .flowRate(3.5d);

        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/410907/versions/1/pods/962183/periods")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isBadRequest())
                                        .andReturn();
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("Compacted may only be used as an element of origin when the Water Right Type is Compacted");
    }

    // Enforcement tests
    @Test
    public void testCreateEnforcement() throws Exception {
        String token = getAccessToken();

        PodEnforcementDto dto = new PodEnforcementDto()
            .areaId("E003")
            .enforcementNumber("200")
            .comments("TESTING");

        mockMvc.perform(post("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isCreated())
                                        .andReturn();

        MvcResult result = mockMvc.perform(get("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        PodEnforcementsPageDto page = convertTo(result, PodEnforcementsPageDto.class);

        PodEnforcementDto enforcement = page.getResults().stream().filter(e -> e.getAreaId().equals("E003") && e.getEnforcementNumber().equals("200")).findFirst().get();

        assertThat(enforcement.getComments()).isEqualTo("TESTING");
        
        dto.setComments("TESTING2");

        mockMvc.perform(put("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements/E003/200")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();

        result = mockMvc.perform(get("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        page = convertTo(result, PodEnforcementsPageDto.class);

        enforcement = page.getResults().stream().filter(e -> e.getAreaId().equals("E003") && e.getEnforcementNumber().equals("200")).findFirst().get();

        assertThat(enforcement.getComments()).isEqualTo("TESTING2");

        dto.setAreaId("E004");

        mockMvc.perform(put("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements/E003/200")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();

        result = mockMvc.perform(get("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        page = convertTo(result, PodEnforcementsPageDto.class);

        enforcement = page.getResults().stream().filter(e -> e.getAreaId().equals("E004") && e.getEnforcementNumber().equals("200")).findFirst().get();

        assertThat(enforcement.getComments()).isEqualTo("TESTING2");

        mockMvc.perform(delete("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements/E004/200")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();
    }

    @Test
    public void testCreateDuplicate() throws Exception {
        String token = getAccessToken();

        PodEnforcementDto dto = new PodEnforcementDto()
            .areaId("E002")
            .enforcementNumber("200")
            .comments("TESTING");

        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isBadRequest())
                                        .andReturn();

        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("Cannot have two Enforcements with the same Enf Area and Enf #");
    }

    @Test
    public void testUpdateToDuplicateEnforcement() throws Exception {
        String token = getAccessToken();

        PodEnforcementDto dto = new PodEnforcementDto()
            .areaId("E003")
            .enforcementNumber("200")
            .comments("TESTING");

        mockMvc.perform(post("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isCreated())
                                        .andReturn();
        dto.setAreaId("E002");

        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isBadRequest())
                                        .andReturn();
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("Cannot have two Enforcements with the same Enf Area and Enf #");

        mockMvc.perform(delete("/api/v1/water-rights/410907/versions/1/pods/962183/enforcements/E003/200")
                                        .content(getJson(dto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();
    }
}
