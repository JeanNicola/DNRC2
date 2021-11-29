package gov.mt.wris;

import gov.mt.wris.dtos.ApplicantsPageDto;
import gov.mt.wris.dtos.CaseCommentsDto;
import gov.mt.wris.dtos.CaseCreationDto;
import gov.mt.wris.dtos.CaseDto;
import gov.mt.wris.dtos.CaseRegisterCreateUpdateDto;
import gov.mt.wris.dtos.CaseRegisterDetailDto;
import gov.mt.wris.dtos.CaseRegisterPageDto;
import gov.mt.wris.dtos.CaseSearchResultPageDto;
import gov.mt.wris.dtos.CaseUpdateDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.ObjectionsPageDto;
import gov.mt.wris.dtos.ScheduleEventCreateDto;
import gov.mt.wris.dtos.ScheduleEventDetailDto;
import gov.mt.wris.dtos.ScheduleEventUpdateDto;
import gov.mt.wris.dtos.ScheduleEventsPageDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class CaseScheduleIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetScheduleEvents() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;

        caseId = "18321";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/schedule")
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ScheduleEventsPageDto testA = convertTo(result, ScheduleEventsPageDto.class);
        assertThat(testA.getResults().get(1).getScheduleId()).isEqualTo(450);
        assertThat(testA.getResults().get(1).getEventType()).isEqualTo("MLIM");
        assertThat(testA.getResults().get(1).getEventTypeDescription()).isEqualTo("FILING: MOTION IN LIMINE");
        assertThat(testA.getResults().get(1).getEventStatus()).isEqualTo("THIS IS STATUS");
        assertThat(testA.getResults().get(1).getEventDate()).isEqualTo("2021-10-20");
        assertThat(testA.getResults().get(1).getEventBeginTime()).isEqualTo("2021-10-22T00:00");
        assertThat(testA.getResults().get(1).getNotes()).isNull();

    }

    @Test
    public void testCreateScheduleEvent() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId;

        caseId = "18321";
        ScheduleEventCreateDto secd1 = new ScheduleEventCreateDto();
        secd1.setEventType("HRSH");
        secd1.setEventDate(LocalDate.parse("2021-10-20", dtf1));
        secd1.setEventBeginTime(LocalDateTime.parse("2021-10-20 14:30", dtf2));
        secd1.setEventStatus("SHOW CAUSE HEARING");
        secd1.setNotes("TESTING 1234");

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/schedule")
                .content(getJson(secd1))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ScheduleEventDetailDto test1 = convertTo(result, ScheduleEventDetailDto.class);
        assertThat(test1.getEventType()).isEqualTo(secd1.getEventType());
        assertThat(test1.getEventStatus()).isEqualTo(secd1.getEventStatus());
        assertThat(test1.getEventDate()).isEqualTo(secd1.getEventDate());
        assertThat(test1.getEventBeginTime()).isEqualTo(secd1.getEventBeginTime());
        assertThat(test1.getNotes()).isEqualTo(secd1.getNotes());

        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/schedule/" + test1.getScheduleId())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testUpdateScheduleEvent() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId;

        caseId = "18321";
        ScheduleEventCreateDto secd1 = new ScheduleEventCreateDto();
        secd1.setEventType("HRSH");
        secd1.setEventDate(LocalDate.parse("2021-10-20", dtf1));
        secd1.setEventBeginTime(LocalDateTime.parse("2021-10-20 14:30", dtf2));
        secd1.setEventStatus("SHOW CAUSE HEARING");
        secd1.setNotes("TESTING 1234");
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/schedule")
                .content(getJson(secd1))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ScheduleEventDetailDto test1 = convertTo(result, ScheduleEventDetailDto.class);


        ScheduleEventUpdateDto secd2 = new ScheduleEventUpdateDto();
        secd2.setEventType("HRSH");
        secd2.setEventDate(LocalDate.parse("2021-11-01", dtf1));
        secd2.setEventBeginTime(LocalDateTime.parse("2021-11-20 16:45", dtf2));
        secd2.setEventStatus("SHOW ME HEARING");
        secd2.setNotes("TESTING 1234 45678");
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/schedule/" + test1.getScheduleId())
                .content(getJson(secd2))
                .param("caseId", caseId)
                .param("scheduleId", test1.getScheduleId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ScheduleEventDetailDto test2 = convertTo(result, ScheduleEventDetailDto.class);
        assertThat(test2.getEventType()).isEqualTo(secd2.getEventType());
        assertThat(test2.getEventStatus()).isEqualTo(secd2.getEventStatus());
        assertThat(test2.getEventDate()).isEqualTo(secd2.getEventDate());
        assertThat(test2.getEventBeginTime()).isEqualTo(secd2.getEventBeginTime());
        assertThat(test2.getNotes()).isEqualTo(secd2.getNotes());


        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/schedule/" + test1.getScheduleId())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

}
