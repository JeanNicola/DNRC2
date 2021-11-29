package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.controllers.ApplicationsController;
import gov.mt.wris.dtos.ApplicationCreationDto;
import gov.mt.wris.dtos.ApplicationDto;
import gov.mt.wris.dtos.OfficeDto;
import gov.mt.wris.dtos.OfficeCreationDto;
import gov.mt.wris.dtos.OfficePageDto;
import gov.mt.wris.dtos.StaffCreationDto;
import gov.mt.wris.dtos.StaffDto;
import gov.mt.wris.dtos.StaffPageDto;
import gov.mt.wris.services.ApplicationService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationLocationProcessorIntegrationTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMVC;

    @Autowired
    ApplicationsController appController;

    @Autowired
    private ApplicationService appService;

    @Test
    public void testCreateOffice() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("2020-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMVC.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);

        assertThat(createdApp.getApplicationId()).isNotNull();

        Long id = createdApp.getApplicationId();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        OfficePageDto offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(1);
        assertThat(offices.getResults().get(0).getReceivedDate()).isEqualTo(date.toLocalDate());

        OfficeDto office = offices.getResults().get(0);
        LocalDate sentDate = LocalDate.of(2021, 03, 10);
        office.setSentDate(sentDate);

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        OfficeCreationDto newOffice = new OfficeCreationDto();
        newOffice.setOfficeId(4L);
        result = mockMVC.perform(post("/api/v1/applications/" + id + "/locations")
                            .content(getJson(newOffice))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(2);

        // the default sorting should result in the first one being the new one
        Long officeId = offices.getResults().get(0).getId();
        mockMVC.perform(delete("/api/v1/applications/" + id + "/locations/" + officeId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        appService.deleteApplication(id);
    }

    @Test
    public void testEditOffice() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("2020-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMVC.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);

        assertThat(createdApp.getApplicationId()).isNotNull();

        Long id = createdApp.getApplicationId();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        OfficePageDto offices = convertTo(result, OfficePageDto.class);

        OfficeDto office = offices.getResults().get(0);

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        LocalDate receivedDate = office.getReceivedDate();
        office.setReceivedDate(LocalDate.of(2020, 12, 31));

        // Cannot edit received date once set
        result = mockMVC.perform(put("/api/v1/applications/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        office.setReceivedDate(receivedDate);
        office.setSentDate(LocalDate.of(2050, 12, 31));

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        office.setSentDate(LocalDate.of(1999, 12, 31));

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        office.setSentDate(LocalDate.of(2020, 12, 31));

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        office.setSentDate(LocalDate.of(2021, 1, 1));

        // Cannot edit sent date once set
        result = mockMVC.perform(put("/api/v1/applications/" + id + "/locations/" + office.getId())
                            .content(getJson(office))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        appService.deleteApplication(id);
    }

    @Test
    public void testEndDateOthersFirst() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("2020-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMVC.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);

        assertThat(createdApp.getApplicationId()).isNotNull();

        Long id = createdApp.getApplicationId();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        OfficePageDto offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(1);
        assertThat(offices.getResults().get(0).getReceivedDate()).isEqualTo(date.toLocalDate());

        OfficeCreationDto newOffice = new OfficeCreationDto();
        newOffice.setOfficeId(4L);
        LocalDate receivedDate = LocalDate.of(2021, 03, 11);
        newOffice.setReceivedDate(receivedDate);
        result = mockMVC.perform(post("/api/v1/applications/" + id + "/locations")
                            .content(getJson(newOffice))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        appService.deleteApplication(id);
    }

    @Test
    public void testPreventDeleteOnSystemGenerated() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("2020-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMVC.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);

        assertThat(createdApp.getApplicationId()).isNotNull();

        Long id = createdApp.getApplicationId();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/locations")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        OfficePageDto offices = convertTo(result, OfficePageDto.class);

        assertThat(offices.getResults().size()).isEqualTo(1);
        assertThat(offices.getResults().get(0).getReceivedDate()).isEqualTo(date.toLocalDate());

        Long officeId = offices.getResults().get(0).getId();

        result = mockMVC.perform(delete("/api/v1/applications/" + id + "/locations/" + officeId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        appService.deleteApplication(id);
    }
    
    // File Location Staff Tests
    @Test
    public void testCreateStaff() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("2020-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMVC.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);

        assertThat(createdApp.getApplicationId()).isNotNull();

        Long id = createdApp.getApplicationId();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(date.toLocalDate());

        StaffDto member = staff.getResults().get(0);
        LocalDate endDate = LocalDate.of(2021, 03, 10);
        member.setEndDate(endDate);

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        StaffCreationDto newStaff = new StaffCreationDto();
        newStaff.setStaffId(526L);
        LocalDate beginDate = LocalDate.of(2021, 03, 11);
        newStaff.setBeginDate(beginDate);
        result = mockMVC.perform(post("/api/v1/applications/" + id + "/staff")
                            .content(getJson(newStaff))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(2);
        // the default sorting should result in the first one being the new one
        Long staffId = staff.getResults().get(0).getId();
        mockMVC.perform(delete("/api/v1/applications/" + id + "/staff/" + staffId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        appService.deleteApplication(id);
    }

    @Test
    public void testEditStaff() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("2020-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMVC.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);

        Long id = createdApp.getApplicationId();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        StaffPageDto staff = convertTo(result, StaffPageDto.class);
        StaffDto member = staff.getResults().get(0);

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        LocalDate beginDate = member.getBeginDate();
        member.setBeginDate(LocalDate.of(2020, 12, 31));

        // Cannot edit begin date once set
        result = mockMVC.perform(put("/api/v1/applications/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        member.setBeginDate(beginDate);
        member.setEndDate(LocalDate.of(2050, 12, 31));

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        member.setEndDate(LocalDate.of(1999, 12, 31));

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        member.setEndDate(LocalDate.of(2020, 12, 31));

        result = mockMVC.perform(put("/api/v1/applications/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();

        member.setEndDate(LocalDate.of(2021, 1, 1));

        // Cannot edit end date once set
        result = mockMVC.perform(put("/api/v1/applications/" + id + "/staff/" + member.getId())
                            .content(getJson(member))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        appService.deleteApplication(id);
    }

    @Test
    public void testEndDateOtherStaffFirst() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("2020-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMVC.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);

        assertThat(createdApp.getApplicationId()).isNotNull();

        Long id = createdApp.getApplicationId();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(date.toLocalDate());

        StaffCreationDto newStaff = new StaffCreationDto();
        newStaff.setStaffId(526L);
        LocalDate beginDate = LocalDate.of(2021, 03, 11);
        newStaff.setBeginDate(beginDate);
        result = mockMVC.perform(post("/api/v1/applications/" + id + "/staff")
                            .content(getJson(newStaff))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        appService.deleteApplication(id);
    }

    @Test
    public void testPreventStaffDeleteOnSystemGenerated() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("2020-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMVC.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);

        assertThat(createdApp.getApplicationId()).isNotNull();

        Long id = createdApp.getApplicationId();

        result = mockMVC.perform(get("/api/v1/applications/" + id + "/staff")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        StaffPageDto staff = convertTo(result, StaffPageDto.class);

        assertThat(staff.getResults().size()).isEqualTo(1);
        assertThat(staff.getResults().get(0).getBeginDate()).isEqualTo(date.toLocalDate());

        Long staffId = staff.getResults().get(0).getId();

        result = mockMVC.perform(delete("/api/v1/applications/" + id + "/staff/" + staffId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isBadRequest())
                            .andReturn();

        appService.deleteApplication(id);
    }

}
