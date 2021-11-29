package gov.mt.wris;

import gov.mt.wris.dtos.ApplicantsPageDto;
import gov.mt.wris.dtos.CaseCreationDto;
import gov.mt.wris.dtos.CaseDto;
import gov.mt.wris.dtos.CaseRegisterCreateUpdateDto;
import gov.mt.wris.dtos.CaseRegisterDetailDto;
import gov.mt.wris.dtos.CaseRegisterPageDto;
import gov.mt.wris.dtos.CaseSearchResultPageDto;
import gov.mt.wris.dtos.CaseUpdateDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.ObjectionsPageDto;
import gov.mt.wris.dtos.StaffAssignmentCreateDto;
import gov.mt.wris.dtos.StaffAssignmentDetailDto;
import gov.mt.wris.dtos.StaffAssignmentUpdateDto;
import gov.mt.wris.dtos.StaffAssignmentsPageDto;
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
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class CaseAssignmentsIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetCaseAssignments() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;
        String sort = "";

        //COMPLETENAME, ASSIGNMENTDESCRIPTION, BEGINDATE, ENDDATE
        sort = "?sortDirection=ASC&sortColumn=COMPLETENAME";

        caseId = "4256";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/assignments" + sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        StaffAssignmentsPageDto testA = convertTo(result, StaffAssignmentsPageDto.class);
        assertThat(testA.getResults().get(0).getAssignmentId()).isEqualTo(8591);
        assertThat(testA.getResults().get(0).getDnrcId()).isEqualTo("390");
        assertThat(testA.getResults().get(0).getCompleteName()).isEqualTo("CRISTIANI, JULIE");
        assertThat(testA.getResults().get(0).getAssignmentType()).isEqualTo("WCCK");
        assertThat(testA.getResults().get(0).getAssignmentTypeDescription()).isEqualTo("WATER COURT CLERK");
        assertThat(testA.getResults().get(0).getBeginDate()).isEqualTo("2014-05-06");
        assertThat(testA.getResults().get(0).getEndDate()).isNull();
        assertThat(testA.getResults().get(1).getAssignmentId()).isEqualTo(8590);
        assertThat(testA.getResults().get(1).getDnrcId()).isEqualTo("173");
        assertThat(testA.getResults().get(1).getCompleteName()).isEqualTo("STERN, ANIKA");
        assertThat(testA.getResults().get(1).getAssignmentType()).isEqualTo("WCMR");
        assertThat(testA.getResults().get(1).getAssignmentTypeDescription()).isEqualTo("WATER COURT MASTER");
        assertThat(testA.getResults().get(1).getBeginDate()).isEqualTo("2014-05-06");
        assertThat(testA.getResults().get(1).getEndDate()).isNull();

    }

    @Test
    public void testAddCaseAssignments() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId;

        caseId = "4286";
        StaffAssignmentCreateDto sac = new StaffAssignmentCreateDto();
        sac.setAssignmentType("WCCK");
        sac.setDnrcId("386");
        sac.setBeginDate(LocalDate.parse("2021-10-01", dtf));

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/assignments")
                .content(getJson(sac))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        StaffAssignmentDetailDto test1 = convertTo(result, StaffAssignmentDetailDto.class);
        assertThat(test1.getAssignmentId()).isGreaterThan(0);
        assertThat(test1.getAssignmentType()).isEqualTo("WCCK");

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/assignments")
                .content(getJson(sac))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).contains("Only one active person per role is allowed. Please delete this record, end date the previous person for this role, save, and then add the record.");

        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/assignments/" + test1.getAssignmentId())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testDeleteCaseAssignments() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId;

        caseId = "4286";
        StaffAssignmentCreateDto sac = new StaffAssignmentCreateDto();
        sac.setAssignmentType("WCMR");
        sac.setDnrcId("386");
        sac.setBeginDate(LocalDate.parse("2021-10-01", dtf));

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/assignments")
                .content(getJson(sac))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        StaffAssignmentDetailDto test1 = convertTo(result, StaffAssignmentDetailDto.class);

        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/assignments/" + test1.getAssignmentId())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testUpdateCaseAssignments() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId, assignmentId;

        caseId = "4286";
        StaffAssignmentCreateDto sac = new StaffAssignmentCreateDto();
        sac.setAssignmentType("WCMR");
        sac.setDnrcId("386");
        sac.setBeginDate(LocalDate.parse("2021-10-01", dtf));
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/assignments")
                .content(getJson(sac))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        StaffAssignmentDetailDto test1 = convertTo(result, StaffAssignmentDetailDto.class);


        StaffAssignmentUpdateDto sau1 = new StaffAssignmentUpdateDto();
        sau1.setAssignmentType("WCCK");
        sau1.setBeginDate(LocalDate.parse("2021-10-01", dtf));
        sau1.setEndDate(null);
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/assignments/" + test1.getAssignmentId())
                .content(getJson(sau1))
                .param("caseId", caseId)
                .param("assignmentId", test1.getAssignmentId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        StaffAssignmentDetailDto test2 = convertTo(result, StaffAssignmentDetailDto.class);
        assertThat(test2.getAssignmentType()).isEqualTo(sau1.getAssignmentType());
        assertThat(test2.getBeginDate()).isEqualTo(sau1.getBeginDate());
        assertThat(test2.getEndDate()).isNull();


        StaffAssignmentUpdateDto sau2 = new StaffAssignmentUpdateDto();
        sau1.setAssignmentType("WCCK");
        sau1.setBeginDate(LocalDate.parse("2021-10-21", dtf));
        sau1.setEndDate(LocalDate.parse("2021-10-30", dtf));
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/assignments/" + test1.getAssignmentId())
                .content(getJson(sau1))
                .param("caseId", caseId)
                .param("assignmentId", test1.getAssignmentId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).contains("Only one active person per role is allowed. Please delete this record, end date the previous person for this role, save, and then add the record.");


        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/assignments/" + test1.getAssignmentId())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

}
