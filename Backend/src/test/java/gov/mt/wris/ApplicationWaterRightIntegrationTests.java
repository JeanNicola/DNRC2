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

import gov.mt.wris.dtos.ApplicationCreationDto;
import gov.mt.wris.dtos.ApplicationDto;
import gov.mt.wris.dtos.ApplicationWaterRightCreationDto;
import gov.mt.wris.dtos.ApplicationWaterRightDto;
import gov.mt.wris.dtos.ApplicationWaterRightsPageDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.PaymentDto;
import gov.mt.wris.services.ApplicationService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationWaterRightIntegrationTests extends BaseTestCase{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationService appService;

    @Test
    public void testCreateWaterRight() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("1973-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);
        Long applicationId = createdApp.getApplicationId();

        Long waterRightId = 487703L;
        Integer versionId = 1;
        ApplicationWaterRightCreationDto dto = new ApplicationWaterRightCreationDto();
        dto.setId(waterRightId);
        dto.setVersion(versionId);
        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/water-rights")
                            .content(getJson(dto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
        ApplicationWaterRightDto waterRight = convertTo(result, ApplicationWaterRightDto.class);

        assertThat(waterRight.getId()).isEqualTo(waterRightId);
        assertThat(waterRight.getVersion()).isEqualTo(versionId.longValue());

        appService.deleteApplication(createdApp.getApplicationId());
    }

    @Test
    public void testUpdate() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("1973-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);
        Long applicationId = createdApp.getApplicationId();

        Long waterRightId = 487703L;
        Integer versionId = 1;
        ApplicationWaterRightCreationDto dto = new ApplicationWaterRightCreationDto();
        dto.setId(waterRightId);
        dto.setVersion(versionId);
        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/water-rights")
                            .content(getJson(dto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
        ApplicationWaterRightDto waterRight = convertTo(result, ApplicationWaterRightDto.class);

        assertThat(waterRight.getId()).isEqualTo(waterRightId);
        assertThat(waterRight.getVersion()).isEqualTo(versionId.longValue());

        waterRight.setStatusCode("PEND");
        waterRight.setVersionStatusCode("PEND");
        result = mockMvc.perform(put("/api/v1/applications/" + applicationId + "/water-rights/" + waterRightId + "/" + versionId)
                            .content(getJson(waterRight))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        waterRight = convertTo(result, ApplicationWaterRightDto.class);

        assertThat(waterRight.getStatusCode()).isEqualTo("PEND");
        assertThat(waterRight.getVersionStatusCode()).isEqualTo("PEND");

        appService.deleteApplication(createdApp.getApplicationId());
    }

    @Test
    public void testDelete() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("1973-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);
        Long applicationId = createdApp.getApplicationId();

        Long waterRightId = 487703L;
        Integer versionId = 1;
        ApplicationWaterRightCreationDto dto = new ApplicationWaterRightCreationDto();
        dto.setId(waterRightId);
        dto.setVersion(versionId);
        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/water-rights")
                            .content(getJson(dto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
        ApplicationWaterRightDto waterRight = convertTo(result, ApplicationWaterRightDto.class);

        assertThat(waterRight.getId()).isEqualTo(waterRightId);
        assertThat(waterRight.getVersion()).isEqualTo(versionId.longValue());

        result = mockMvc.perform(delete("/api/v1/applications/" + applicationId + "/water-rights/" + waterRightId + "/" + versionId)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        appService.deleteApplication(createdApp.getApplicationId());
    }

    @Test
    public void testUpdateFailSameStatus() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("102");
        LocalDateTime date = getDate("1973-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);
        Long applicationId = createdApp.getApplicationId();

        Long waterRightId = 487703L;
        Integer versionId = 1;
        ApplicationWaterRightCreationDto dto = new ApplicationWaterRightCreationDto();
        dto.setId(waterRightId);
        dto.setVersion(versionId);
        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/water-rights")
                            .content(getJson(dto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
        ApplicationWaterRightDto waterRight = convertTo(result, ApplicationWaterRightDto.class);

        assertThat(waterRight.getId()).isEqualTo(waterRightId);
        assertThat(waterRight.getVersion()).isEqualTo(versionId.longValue());

        waterRight.setStatusCode("ACTV");
        waterRight.setVersionStatusCode("ACTV");
        result = mockMvc.perform(put("/api/v1/applications/" + applicationId + "/water-rights/" + waterRightId + "/" + versionId)
                            .content(getJson(waterRight))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isConflict())
                            .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Not allowed to edit this Version Status");

        appService.deleteApplication(createdApp.getApplicationId());
    }

    @Test
    public void testUpdateWrongAppType() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("102");
        LocalDateTime date = getDate("1973-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);
        Long applicationId = createdApp.getApplicationId();

        Long waterRightId = 487703L;
        Integer versionId = 1;
        ApplicationWaterRightCreationDto dto = new ApplicationWaterRightCreationDto();
        dto.setId(waterRightId);
        dto.setVersion(versionId);
        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/water-rights")
                            .content(getJson(dto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
        ApplicationWaterRightDto waterRight = convertTo(result, ApplicationWaterRightDto.class);

        assertThat(waterRight.getId()).isEqualTo(waterRightId);
        assertThat(waterRight.getVersion()).isEqualTo(versionId.longValue());

        waterRight.setStatusCode("ACTV");
        waterRight.setVersionStatusCode("PEND");
        result = mockMvc.perform(put("/api/v1/applications/" + applicationId + "/water-rights/" + waterRightId + "/" + versionId)
                            .content(getJson(waterRight))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isConflict())
                            .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("For this application, the statuses must be the same");

        appService.deleteApplication(createdApp.getApplicationId());
    }

    @Test
    public void testCreateDuplicate() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("1973-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);
        Long applicationId = createdApp.getApplicationId();

        Long waterRightId = 487703L;
        Integer versionId = 1;
        ApplicationWaterRightCreationDto dto = new ApplicationWaterRightCreationDto();
        dto.setId(waterRightId);
        dto.setVersion(versionId);
        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/water-rights")
                            .content(getJson(dto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
        ApplicationWaterRightDto waterRight = convertTo(result, ApplicationWaterRightDto.class);

        assertThat(waterRight.getId()).isEqualTo(waterRightId);
        assertThat(waterRight.getVersion()).isEqualTo(versionId.longValue());

        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/water-rights")
                            .content(getJson(dto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isConflict())
                            .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("This water right and version already exist on this application");

        appService.deleteApplication(createdApp.getApplicationId());
    }

    @Test
    public void testDeleteAutoComplete() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("1973-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);
        Long applicationId = createdApp.getApplicationId();

        PaymentDto newPayment = new PaymentDto();
        newPayment.setAmountPaid((double) 125);
        String tracking = "TESTDELETEWR";
        newPayment.setTrackingNumber(tracking);
        LocalDate paymentDate = getDate("2020-07-11T00:00:00").toLocalDate();
        newPayment.setDatePaid(paymentDate);
        newPayment.setOrigin("TLMS");

        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/payments")
                                .content(getJson(newPayment))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isCreated())
                                .andReturn();

        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/auto-complete")
                .content("{}")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        
        result = mockMvc.perform(get("/api/v1/applications/" + applicationId + "/water-rights")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        ApplicationWaterRightsPageDto page = convertTo(result, ApplicationWaterRightsPageDto.class);
        ApplicationWaterRightDto dto = page.getResults().get(0);

        assertThat(dto.getWaterRightNumber()).isEqualTo(applicationId);

        result = mockMvc.perform(delete("/api/v1/applications/" + applicationId + "/water-rights/" + dto.getId() + "/" + dto.getVersion())
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isConflict())
                            .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Cannot remove Water Rights generated via Auto-Complete");

        appService.deleteApplication(createdApp.getApplicationId());
    }

    @Test
    public void testUpdateWrongStatus() throws Exception {
        String token = getAccessToken();

        ApplicationCreationDto newApp = new ApplicationCreationDto();
        newApp.setBasin("43D");
        newApp.setApplicationTypeCode("602");
        LocalDateTime date = getDate("1973-07-11T10:55:00");
        newApp.setDateTimeReceived(date);
        newApp.setContactIds(Arrays.asList(387065L, 160576L));
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .content(getJson(newApp))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        ApplicationDto createdApp = convertTo(result, ApplicationDto.class);
        Long applicationId = createdApp.getApplicationId();

        Long waterRightId = 487703L;
        Integer versionId = 1;
        ApplicationWaterRightCreationDto dto = new ApplicationWaterRightCreationDto();
        dto.setId(waterRightId);
        dto.setVersion(versionId);
        result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/water-rights")
                            .content(getJson(dto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
        ApplicationWaterRightDto waterRight = convertTo(result, ApplicationWaterRightDto.class);

        assertThat(waterRight.getId()).isEqualTo(waterRightId);
        assertThat(waterRight.getVersion()).isEqualTo(versionId.longValue());

        waterRight.setStatusCode("DISS");
        waterRight.setVersionStatusCode("DISS");
        result = mockMvc.perform(put("/api/v1/applications/" + applicationId + "/water-rights/" + waterRightId + "/" + versionId)
                            .content(getJson(waterRight))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isConflict())
                            .andReturn();
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("DISS is not a valid Version Status Code for this Water Right version");

        appService.deleteApplication(createdApp.getApplicationId());
    }
}