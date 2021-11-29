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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.JobPartiesPageDto;
import gov.mt.wris.dtos.JobPartyByOfficeCreationDto;
import gov.mt.wris.dtos.JobPartyCreationDto;
import gov.mt.wris.dtos.JobWaterRightCreationDto;
import gov.mt.wris.dtos.JobWaterRightPageDto;
import gov.mt.wris.dtos.MailingJobCreationDto;
import gov.mt.wris.dtos.MailingJobDto;
import gov.mt.wris.dtos.MailingJobUpdateDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class MailingJobTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testMailingJobCUD() throws Exception {
        String token = getAccessToken();

        MailingJobCreationDto creationDto = new MailingJobCreationDto()
            .applicationId(3020000L);
        
        MvcResult result = mockMvc.perform(post("/api/v1/mailing-jobs")
                                    .content(getJson(creationDto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        
        MailingJobDto dto = convertTo(result, MailingJobDto.class);

        assertThat(dto.getMailingJobNumber()).isNotNull();
        assertThat(dto.getMailingJobHeader()).isEqualTo("PN-43Q-3020000");

        Long id = dto.getMailingJobNumber();

        String header = "PN-43I-302000asd";
        MailingJobUpdateDto updateDto = new MailingJobUpdateDto()
            .applicationId(3020000L)
            .mailingJobHeader(header);

        result = mockMvc.perform(put("/api/v1/mailing-jobs/" + id)
                                .content(getJson(updateDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        dto = convertTo(result, MailingJobDto.class);

        assertThat(dto.getMailingJobHeader()).isEqualTo(header);

        mockMvc.perform(post("/api/v1/mailing-jobs/" + id)
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();

        result = mockMvc.perform(get("/api/v1/mailing-jobs/" + id)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        dto = convertTo(result, MailingJobDto.class);

        assertThat(dto.getApplicationId()).isEqualTo(3020000L);
        assertThat(dto.getApplicationTypeDescription()).isEqualTo("602 - NOTICE OF COMPLETION OF GROUNDWATER DEVELOPMENT");
        assertThat(dto.getMailingJobHeader()).isEqualTo(header);
        assertThat(dto.getGeneratedDate()).isEqualTo(LocalDate.now());

        mockMvc.perform(delete("/api/v1/mailing-jobs/" + id)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();
    }

    @Test
    public void testMailingJobWaterRight() throws Exception {
        String token = getAccessToken();

        MailingJobCreationDto creationDto = new MailingJobCreationDto()
            .applicationId(3020000L);
        
        MvcResult result = mockMvc.perform(post("/api/v1/mailing-jobs")
                                    .content(getJson(creationDto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        MailingJobDto dto = convertTo(result, MailingJobDto.class);
        assertThat(dto.getMailingJobNumber()).isNotNull();
        Long id = dto.getMailingJobNumber();

        JobWaterRightCreationDto creationDto2 = new JobWaterRightCreationDto()
            .waterRightId(3L);

        mockMvc.perform(post("/api/v1/mailing-jobs/" + id + "/water-rights")
                                    .content(getJson(creationDto2))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isNoContent())
                                    .andReturn();

        result = mockMvc.perform(get("/api/v1/mailing-jobs/" + id + "/water-rights")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        JobWaterRightPageDto pageDto = convertTo(result, JobWaterRightPageDto.class);

        assertThat(pageDto.getTotalElements()).isEqualTo(1L);
        assertThat(pageDto.getResults().get(0).getWaterRightId()).isEqualTo(3L);

        mockMvc.perform(delete("/api/v1/mailing-jobs/" + id + "/water-rights/3")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        result = mockMvc.perform(get("/api/v1/mailing-jobs/" + id + "/water-rights")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        pageDto = convertTo(result, JobWaterRightPageDto.class);

        assertThat(pageDto.getTotalElements()).isEqualTo(0L);

        mockMvc.perform(delete("/api/v1/mailing-jobs/" + id)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();
    }

    @Test
    public void testMailingJobInterestedParties() throws Exception {
        String token = getAccessToken();

        MailingJobCreationDto creationDto = new MailingJobCreationDto()
            .applicationId(3020000L);
        
        MvcResult result = mockMvc.perform(post("/api/v1/mailing-jobs")
                                    .content(getJson(creationDto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        MailingJobDto dto = convertTo(result, MailingJobDto.class);
        assertThat(dto.getMailingJobNumber()).isNotNull();
        Long id = dto.getMailingJobNumber();

        JobPartyCreationDto creationDto2 = new JobPartyCreationDto()
            .contactId(77987L);

        mockMvc.perform(post("/api/v1/mailing-jobs/" + id + "/parties")
                                    .content(getJson(creationDto2))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isNoContent())
                                    .andReturn();

        result = mockMvc.perform(get("/api/v1/mailing-jobs/" + id + "/parties")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        JobPartiesPageDto pageDto = convertTo(result, JobPartiesPageDto.class);

        assertThat(pageDto.getTotalElements()).isEqualTo(1L);
        assertThat(pageDto.getResults().get(0).getContactId()).isEqualTo(77987L);

        mockMvc.perform(delete("/api/v1/mailing-jobs/" + id + "/parties/77987")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        result = mockMvc.perform(get("/api/v1/mailing-jobs/" + id + "/parties")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        pageDto = convertTo(result, JobPartiesPageDto.class);

        assertThat(pageDto.getTotalElements()).isEqualTo(0L);

        JobPartyByOfficeCreationDto creationDto3 = new JobPartyByOfficeCreationDto()
            .contactIds(Arrays.asList(216232L))
            .includeAll(false);

        mockMvc.perform(post("/api/v1/mailing-jobs/" + id + "/parties-by-office/10")
                                    .content(getJson(creationDto3))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isNoContent())
                                    .andReturn();

        result = mockMvc.perform(get("/api/v1/mailing-jobs/" + id + "/parties")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        pageDto = convertTo(result, JobPartiesPageDto.class);

        assertThat(pageDto.getTotalElements()).isEqualTo(1L);

        mockMvc.perform(delete("/api/v1/mailing-jobs/" + id + "/parties/216232")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        creationDto3 = new JobPartyByOfficeCreationDto()
            .contactIds(Arrays.asList(216232L, 236071L))
            .includeAll(true);

        mockMvc.perform(post("/api/v1/mailing-jobs/" + id + "/parties-by-office/10")
                                    .content(getJson(creationDto3))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isNoContent())
                                    .andReturn();

        result = mockMvc.perform(get("/api/v1/mailing-jobs/" + id + "/parties")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        pageDto = convertTo(result, JobPartiesPageDto.class);

        assertThat(pageDto.getTotalElements()).isEqualTo(48L);

        mockMvc.perform(delete("/api/v1/mailing-jobs/" + id)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();
    }
}
