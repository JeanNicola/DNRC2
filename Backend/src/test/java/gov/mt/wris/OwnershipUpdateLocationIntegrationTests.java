package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.OfficeCreationDto;
import gov.mt.wris.dtos.OfficeDto;
import gov.mt.wris.dtos.OfficePageDto;
import gov.mt.wris.dtos.OwnershipUpdateCreationDto;
import gov.mt.wris.dtos.OwnershipUpdateSearchResultDto;
import gov.mt.wris.dtos.StaffCreationDto;
import gov.mt.wris.dtos.StaffDto;
import gov.mt.wris.dtos.StaffPageDto;
import gov.mt.wris.services.OwnershipUpdateService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OwnershipUpdateLocationIntegrationTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMVC;

    @Autowired
    OwnershipUpdateService service;

    @Test
    public void testCreateOffice() throws Exception {
        String token = getAccessToken();

        OwnershipUpdateCreationDto newUpdate = new OwnershipUpdateCreationDto();
        newUpdate.setOwnershipUpdateType("DOR 608");
        newUpdate.setPendingDORValidation(false);
        newUpdate.setReceivedAs608(false);
        LocalDate receivedDate = getDate("2021-01-01T00:00:00").toLocalDate();
        newUpdate.setReceivedDate(receivedDate);
        newUpdate.setBuyers(Arrays.asList(229249L));
        newUpdate.setSellers(Arrays.asList(311952L));
        newUpdate.setWaterRights(Arrays.asList(129023L, 192135L, 287512L));
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                                    .content(getJson(newUpdate))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        OwnershipUpdateSearchResultDto dto = convertTo(result, OwnershipUpdateSearchResultDto.class);

        assertThat(dto.getOwnershipUpdateId()).isNotNull();

        Long id = dto.getOwnershipUpdateId();

        result = mockMVC.perform(get("/api/v1/ownership-updates/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        OfficePageDto offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(1);
        assertThat(offices.getResults().get(0).getReceivedDate()).isEqualTo(receivedDate);

        OfficeDto office = offices.getResults().get(0);
        LocalDate sentDate = LocalDate.of(2021, 03, 10);
        office.setSentDate(sentDate);

        result = mockMVC.perform(put("/api/v1/ownership-updates/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        OfficeCreationDto newOffice = new OfficeCreationDto();
        newOffice.setOfficeId(4L);
        receivedDate = LocalDate.of(2021, 03, 11);
        newOffice.setReceivedDate(receivedDate);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + id + "/locations")
                            .content(getJson(newOffice))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();

        result = mockMVC.perform(get("/api/v1/ownership-updates/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(2);

        // the default sorting should result in the first one being the new one
        Long officeId = offices.getResults().get(0).getId();
        mockMVC.perform(delete("/api/v1/ownership-updates/" + id + "/locations/" + officeId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        service.deleteOwnershipUpdate(dto.getOwnershipUpdateId());
    }

    @Test
    public void testEndDateOthersFirst() throws Exception {
        String token = getAccessToken();

        OwnershipUpdateCreationDto newUpdate = new OwnershipUpdateCreationDto();
        newUpdate.setOwnershipUpdateType("DOR 608");
        newUpdate.setPendingDORValidation(false);
        newUpdate.setReceivedAs608(false);
        LocalDate receivedDate = getDate("2021-01-01T00:00:00").toLocalDate();
        newUpdate.setReceivedDate(receivedDate);
        newUpdate.setBuyers(Arrays.asList(229249L));
        newUpdate.setSellers(Arrays.asList(311952L));
        newUpdate.setWaterRights(Arrays.asList(129023L, 192135L, 287512L));
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                                    .content(getJson(newUpdate))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        OwnershipUpdateSearchResultDto dto = convertTo(result, OwnershipUpdateSearchResultDto.class);

        assertThat(dto.getOwnershipUpdateId()).isNotNull();

        Long id = dto.getOwnershipUpdateId();

        result = mockMVC.perform(get("/api/v1/ownership-updates/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        OfficePageDto offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(1);
        assertThat(offices.getResults().get(0).getReceivedDate()).isEqualTo(receivedDate);

        OfficeCreationDto newOffice = new OfficeCreationDto();
        newOffice.setOfficeId(4L);
        receivedDate = LocalDate.of(2021, 03, 11);
        newOffice.setReceivedDate(receivedDate);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + id + "/locations")
                            .content(getJson(newOffice))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        service.deleteOwnershipUpdate(dto.getOwnershipUpdateId());
    }

    @Test
    public void testCreateStaff() throws Exception {
        String token = getAccessToken();

        OwnershipUpdateCreationDto newUpdate = new OwnershipUpdateCreationDto();
        newUpdate.setOwnershipUpdateType("DOR 608");
        newUpdate.setPendingDORValidation(false);
        newUpdate.setReceivedAs608(false);
        LocalDate receivedDate = getDate("2021-01-01T00:00:00").toLocalDate();
        newUpdate.setReceivedDate(receivedDate);
        newUpdate.setBuyers(Arrays.asList(229249L));
        newUpdate.setSellers(Arrays.asList(311952L));
        newUpdate.setWaterRights(Arrays.asList(129023L, 192135L, 287512L));
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                                    .content(getJson(newUpdate))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        OwnershipUpdateSearchResultDto dto = convertTo(result, OwnershipUpdateSearchResultDto.class);

        assertThat(dto.getOwnershipUpdateId()).isNotNull();

        Long id = dto.getOwnershipUpdateId();

        result = mockMVC.perform(get("/api/v1/ownership-updates/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(receivedDate);

        StaffDto member = staff.getResults().get(0);
        LocalDate endDate = LocalDate.of(2021, 03, 10);
        member.setEndDate(endDate);

        result = mockMVC.perform(put("/api/v1/ownership-updates/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        StaffCreationDto newStaff = new StaffCreationDto();
        newStaff.setStaffId(526L);
        LocalDate beginDate = LocalDate.of(2021, 03, 11);
        newStaff.setBeginDate(beginDate);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + id + "/staff")
                            .content(getJson(newStaff))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();

        result = mockMVC.perform(get("/api/v1/ownership-updates/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(2);
        // the default sorting should result in the first one being the new one
        Long staffId = staff.getResults().get(0).getId();
        mockMVC.perform(delete("/api/v1/ownership-updates/" + id + "/staff/" + staffId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        service.deleteOwnershipUpdate(dto.getOwnershipUpdateId());
    }

    @Test
    public void testEndDateOtherStaffFirst() throws Exception {
        String token = getAccessToken();

        OwnershipUpdateCreationDto newUpdate = new OwnershipUpdateCreationDto();
        newUpdate.setOwnershipUpdateType("DOR 608");
        newUpdate.setPendingDORValidation(false);
        newUpdate.setReceivedAs608(false);
        LocalDate receivedDate = getDate("2021-01-01T00:00:00").toLocalDate();
        newUpdate.setReceivedDate(receivedDate);
        newUpdate.setBuyers(Arrays.asList(229249L));
        newUpdate.setSellers(Arrays.asList(311952L));
        newUpdate.setWaterRights(Arrays.asList(129023L, 192135L, 287512L));
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                                    .content(getJson(newUpdate))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        OwnershipUpdateSearchResultDto dto = convertTo(result, OwnershipUpdateSearchResultDto.class);

        assertThat(dto.getOwnershipUpdateId()).isNotNull();

        Long id = dto.getOwnershipUpdateId();

        result = mockMVC.perform(get("/api/v1/ownership-updates/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(receivedDate);

        StaffCreationDto newStaff = new StaffCreationDto();
        newStaff.setStaffId(526L);
        LocalDate beginDate = LocalDate.of(2021, 03, 11);
        newStaff.setBeginDate(beginDate);
        result = mockMVC.perform(post("/api/v1/ownership-updates/" + id + "/staff")
                            .content(getJson(newStaff))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        service.deleteOwnershipUpdate(dto.getOwnershipUpdateId());
    }

    @Test
    public void testPreventStaffDeleteOnSystemGenerated() throws Exception {
        String token = getAccessToken();

        OwnershipUpdateCreationDto newUpdate = new OwnershipUpdateCreationDto();
        newUpdate.setOwnershipUpdateType("DOR 608");
        newUpdate.setPendingDORValidation(false);
        newUpdate.setReceivedAs608(false);
        LocalDate receivedDate = getDate("2021-01-01T00:00:00").toLocalDate();
        newUpdate.setReceivedDate(receivedDate);
        newUpdate.setBuyers(Arrays.asList(229249L));
        newUpdate.setSellers(Arrays.asList(311952L));
        newUpdate.setWaterRights(Arrays.asList(129023L, 192135L, 287512L));
        MvcResult result = mockMVC.perform(post("/api/v1/ownership-updates")
                                    .content(getJson(newUpdate))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        OwnershipUpdateSearchResultDto dto = convertTo(result, OwnershipUpdateSearchResultDto.class);

        assertThat(dto.getOwnershipUpdateId()).isNotNull();

        Long id = dto.getOwnershipUpdateId();

        result = mockMVC.perform(get("/api/v1/ownership-updates/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(receivedDate);

        Long staffId = staff.getResults().get(0).getId();

        result = mockMVC.perform(delete("/api/v1/ownership-updates/" + id + "/staff/" + staffId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        service.deleteOwnershipUpdate(dto.getOwnershipUpdateId());
    }
}