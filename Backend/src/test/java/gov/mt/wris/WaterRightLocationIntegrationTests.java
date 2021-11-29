package gov.mt.wris;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.OfficeCreationDto;
import gov.mt.wris.dtos.OfficeDto;
import gov.mt.wris.dtos.OfficePageDto;
import gov.mt.wris.dtos.StaffCreationDto;
import gov.mt.wris.dtos.StaffDto;
import gov.mt.wris.dtos.StaffPageDto;
import gov.mt.wris.dtos.WaterRightCreationDto;
import gov.mt.wris.dtos.WaterRightDto;
import gov.mt.wris.services.WaterRightService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WaterRightLocationIntegrationTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMVC;

    @Autowired
    WaterRightService service;

    @Test
    public void testCreateOffice() throws Exception {
        String token = getAccessToken();

        WaterRightCreationDto newWaterRight = new WaterRightCreationDto();
        newWaterRight.setTypeCode("STOC");
        newWaterRight.setBasin("38H");

        MvcResult result = mockMVC.perform(post("/api/v1/water-rights")
                                    .content(getJson(newWaterRight))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        WaterRightDto createdWaterRight = convertTo(result, WaterRightDto.class);

        assertThat(createdWaterRight.getWaterRightId()).isNotNull();

        Long id = createdWaterRight.getWaterRightId();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        OfficePageDto offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(1);
        assertThat(offices.getResults().get(0).getReceivedDate()).isEqualTo(LocalDate.now());

        OfficeDto office = offices.getResults().get(0);
        LocalDate sentDate = LocalDate.of(2021, 03, 10);
        office.setSentDate(sentDate);

        result = mockMVC.perform(put("/api/v1/water-rights/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        OfficeCreationDto newOffice = new OfficeCreationDto();
        newOffice.setOfficeId(4L);
        LocalDate receivedDate = LocalDate.now();
        newOffice.setReceivedDate(receivedDate);
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/locations")
                            .content(getJson(newOffice))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(2);

        // the default sorting should result in the first one being the new one
        Long officeId = offices.getResults().get(0).getId();
        mockMVC.perform(delete("/api/v1/water-rights/" + id + "/locations/" + officeId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        service.deleteWaterRight(id);
    }

    @Test
    public void testEndDateOthersFirst() throws Exception {
        String token = getAccessToken();

        WaterRightCreationDto newWaterRight = new WaterRightCreationDto();
        newWaterRight.setTypeCode("STOC");
        newWaterRight.setBasin("38H");

        MvcResult result = mockMVC.perform(post("/api/v1/water-rights")
                                    .content(getJson(newWaterRight))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        WaterRightDto createdWaterRight = convertTo(result, WaterRightDto.class);

        assertThat(createdWaterRight.getWaterRightId()).isNotNull();

        Long id = createdWaterRight.getWaterRightId();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        OfficePageDto offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(1);
        assertThat(offices.getResults().get(0).getReceivedDate()).isEqualTo(LocalDate.now());

        OfficeCreationDto newOffice = new OfficeCreationDto();
        newOffice.setOfficeId(4L);
        LocalDate receivedDate = LocalDate.now();
        newOffice.setReceivedDate(receivedDate);
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/locations")
                            .content(getJson(newOffice))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        service.deleteWaterRight(id);
    }

    @Test
    public void testCreateStaff() throws Exception {
        String token = getAccessToken();

        WaterRightCreationDto newWaterRight = new WaterRightCreationDto();
        newWaterRight.setTypeCode("STOC");
        newWaterRight.setBasin("38H");

        MvcResult result = mockMVC.perform(post("/api/v1/water-rights")
                                    .content(getJson(newWaterRight))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        WaterRightDto createdWaterRight = convertTo(result, WaterRightDto.class);

        assertThat(createdWaterRight.getWaterRightId()).isNotNull();

        Long id = createdWaterRight.getWaterRightId();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(LocalDate.now());

        StaffDto member = staff.getResults().get(0);
        member.setEndDate(LocalDate.now());

        result = mockMVC.perform(put("/api/v1/water-rights/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        StaffCreationDto newStaff = new StaffCreationDto();
        newStaff.setStaffId(526L);
        newStaff.setBeginDate(LocalDate.now());
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/staff")
                            .content(getJson(newStaff))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(2);
        // the default sorting should result in the first one being the new one
        Long staffId = staff.getResults().get(0).getId();
        mockMVC.perform(delete("/api/v1/water-rights/" + id + "/staff/" + staffId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        service.deleteWaterRight(id);
    }

    @Test
    public void testEndDateOtherStaffFirst() throws Exception {
        String token = getAccessToken();

        WaterRightCreationDto newWaterRight = new WaterRightCreationDto();
        newWaterRight.setTypeCode("STOC");
        newWaterRight.setBasin("38H");

        MvcResult result = mockMVC.perform(post("/api/v1/water-rights")
                                    .content(getJson(newWaterRight))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        WaterRightDto createdWaterRight = convertTo(result, WaterRightDto.class);

        assertThat(createdWaterRight.getWaterRightId()).isNotNull();

        Long id = createdWaterRight.getWaterRightId();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(LocalDate.now());

        StaffCreationDto newStaff = new StaffCreationDto();
        newStaff.setStaffId(526L);
        newStaff.setBeginDate(LocalDate.now());
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/staff")
                            .content(getJson(newStaff))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        service.deleteWaterRight(id);
    }

    @Test
    public void testPreventStaffDeleteOnSystemGenerated() throws Exception {
        String token = getAccessToken();

        WaterRightCreationDto newWaterRight = new WaterRightCreationDto();
        newWaterRight.setTypeCode("STOC");
        newWaterRight.setBasin("38H");

        MvcResult result = mockMVC.perform(post("/api/v1/water-rights")
                                    .content(getJson(newWaterRight))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        WaterRightDto createdWaterRight = convertTo(result, WaterRightDto.class);

        assertThat(createdWaterRight.getWaterRightId()).isNotNull();

        Long id = createdWaterRight.getWaterRightId();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(LocalDate.now());

        Long staffId = staff.getResults().get(0).getId();

        result = mockMVC.perform(delete("/api/v1/water-rights/" + id + "/staff/" + staffId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        service.deleteWaterRight(id);
    }
}
