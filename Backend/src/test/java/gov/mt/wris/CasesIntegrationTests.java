package gov.mt.wris;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.*;
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
public class CasesIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSearchCases() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String applicationId, caseNumber, caseTypeCode, caseStatusCode, waterCourtCaseNumber;
        float sec;
        String sort = "";

        sort = "?sortDirection=ASC&sortColumn=COMPLETEAPPLICATIONTYPE";
        applicationId = "30106785";
        caseNumber = "11888";
        caseTypeCode = "ADM";
        waterCourtCaseNumber = "%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases" + sort)
                .header("Authorization", "Bearer " + token)
                .param("applicationId", applicationId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseSearchResultPageDto testA = convertTo(result, CaseSearchResultPageDto.class);
        assertThat(testA.getResults().size()).isGreaterThan(0);
        assertThat(testA.getResults().get(0).getApplicationId()).isEqualTo(30106785);
        assertThat(testA.getResults().get(0).getCaseNumber()).isEqualTo(11888);
        assertThat(testA.getResults().get(0).getCaseType()).isEqualTo("ADM");
        assertThat(testA.getResults().get(0).getCaseTypeDescription()).isEqualTo("CONTESTED CASE HEARING");
        assertThat(testA.getResults().get(0).getBasin()).isEqualTo("76G");
        assertThat(testA.getResults().get(0).getCompleteApplicationType()).isEqualTo("606 - APPLICATION TO CHANGE A WATER RIGHT");
        assertThat(testA.getResults().get(0).getCaseStatus()).isEqualTo("CONT");
        assertThat(testA.getResults().get(0).getCaseStatusDescription()).isEqualTo("CONTINUED");
        assertThat(testA.getResults().get(1).getApplicationId()).isEqualTo(30106785);
        assertThat(testA.getResults().get(1).getCaseNumber()).isEqualTo(14231);
        assertThat(testA.getResults().get(1).getCaseType()).isEqualTo("ADM");
        assertThat(testA.getResults().get(1).getCaseTypeDescription()).isEqualTo("CONTESTED CASE HEARING");
        assertThat(testA.getResults().get(1).getBasin()).isEqualTo("76G");
        assertThat(testA.getResults().get(1).getCompleteApplicationType()).isEqualTo("606 - APPLICATION TO CHANGE A WATER RIGHT");
        assertThat(testA.getResults().get(1).getCaseStatus()).isEqualTo("CLSD");
        assertThat(testA.getResults().get(1).getCaseStatusDescription()).isEqualTo("CLOSED");


        sort = "?sortDirection=ASC&sortColumn=COMPLETEAPPLICATIONTYPE";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases" + sort)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseSearchResultPageDto testB = convertTo(result, CaseSearchResultPageDto.class);
        assertThat(testB.getTotalElements()).isGreaterThan(18000);


        sort = "?sortDirection=ASC&sortColumn=APPLICATIONID";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases" + sort)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseSearchResultPageDto testC = convertTo(result, CaseSearchResultPageDto.class);
        assertThat(testC.getResults().get(0).getApplicationId()).isEqualTo(100);
        assertThat(testC.getResults().get(1).getApplicationId()).isEqualTo(200);
        assertThat(testC.getResults().get(2).getApplicationId()).isEqualTo(300);
        assertThat(testC.getResults().get(3).getApplicationId()).isEqualTo(400);
        assertThat(testC.getResults().get(4).getApplicationId()).isEqualTo(1100);
        assertThat(testC.getResults().get(5).getApplicationId()).isEqualTo(1100);
        assertThat(testC.getResults().get(6).getApplicationId()).isEqualTo(1300);
        assertThat(testC.getResults().get(7).getApplicationId()).isEqualTo(12800);
        assertThat(testC.getResults().get(8).getApplicationId()).isEqualTo(628200);
        assertThat(testC.getResults().get(9).getApplicationId()).isEqualTo(2918100);

    }

    @Test
    public void testCreateCourtCase() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;

        CaseCreationDto ccd0 = new CaseCreationDto();
        ccd0.setCaseType("BCA");
        ccd0.setProgramType("NA");
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases")
                .content(getJson(ccd0))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseDto testA = convertTo(result, CaseDto.class);
        assertThat(testA.getCaseNumber()).isGreaterThan(0);
        assertThat(testA.getCaseType()).isEqualTo(ccd0.getCaseType());

        CaseCreationDto ccd1 = new CaseCreationDto();
        ccd1.setCaseType("ADM");
        ccd1.setProgramType("NA");
        ccd1.setApplicationId(9898989898L);
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases")
                .content(getJson(ccd1))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message msg3 = convertTo(result, Message.class);
        assertThat(msg3.getDeveloperMessage()).isEqualTo("DataIntegrityViolationException: The application id 9898989898 does not exist");

        CaseCreationDto ccd4 = new CaseCreationDto();
        ccd4.setCaseType("WCC");
        ccd4.setProgramType("WC");
        ccd4.setWaterCourtCaseNumber("40M-135");
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases")
                .content(getJson(ccd4))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message msg4 = convertTo(result, Message.class);
        assertThat(msg4.getDeveloperMessage()).isEqualTo("ValidationException: Water Court Case Number is already in use for case id(s) 210");

        CaseCreationDto ccd5 = new CaseCreationDto();
        ccd5.setCaseType("SCH");
        ccd5.setProgramType("NA");
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases")
                .content(getJson(ccd5))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseDto testC = convertTo(result, CaseDto.class);
        assertThat(testC.getCaseNumber()).isGreaterThan(0);
        assertThat(testC.getCaseType()).isEqualTo(ccd5.getCaseType());

    }

    @Test
    public void testGetCourtCase() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;

        caseId = "11540";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseDto testA = convertTo(result, CaseDto.class);
        assertThat(testA.getCaseNumber()).isEqualTo(11540);
        assertThat(testA.getApplicationId()).isEqualTo(30111620);
        assertThat(testA.getCaseType()).isEqualTo("ARMR");
        assertThat(testA.getOfficeDescription()).isEqualTo("ADJUDICATION OFFICE");
        assertThat(testA.getCompleteApplicationType()).isEqualTo("630 PETITION FOR CONTROLLED GROUNDWATER AREA");
        assertThat(testA.getBasin()).isEqualTo("76LJ");
        assertThat(testA.getCaseStatus()).isEqualTo("HRHD");
        assertThat(testA.getCaseStatusDescription()).isEqualTo("HEARING HELD");
        assertThat(testA.getAssignedTo()).isEqualTo("FERCH, JAMES");
        assertThat(testA.getHasOldDecreeIssuedDate()).isEqualTo(true);
        assertThat(testA.getProgramType()).isEqualTo("NA");

        caseId = "18272";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseDto testA1 = convertTo(result, CaseDto.class);
        assertThat(testA1.getCaseNumber()).isEqualTo(18272);
        assertThat(testA1.getApplicationId()).isNull();
        assertThat(testA1.getCaseType()).isEqualTo("WCC");
        assertThat(testA1.getOfficeDescription()).isNull();
        assertThat(testA1.getCaseStatus()).isNull();
        assertThat(testA1.getCaseStatusDescription()).isNull();
        assertThat(testA1.getAssignedTo()).isNull();
        assertThat(testA1.getHasOldDecreeIssuedDate()).isEqualTo(false);
        assertThat(testA1.getProgramType()).isEqualTo("WC");

        caseId = "1625";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseDto testB = convertTo(result, CaseDto.class);
        assertThat(testB.getCaseNumber()).isEqualTo(1625);
        assertThat(testB.getWaterCourtCaseNumber()).isEqualTo("76F-54");
        assertThat(testB.getCaseType()).isEqualTo("OBJL");
        assertThat(testB.getCaseTypeDescription()).isEqualTo("OBJECTION LIST");
        assertThat(testB.getOfficeId()).isEqualTo(2);
        assertThat(testB.getOfficeDescription()).isEqualTo("ADJUDICATION OFFICE");
        assertThat(testB.getDecreeBasin()).isEqualTo("76F");
        assertThat(testB.getDecreeType()).isEqualTo("PRLM");
        assertThat(testB.getDecreeTypeDescription()).isEqualTo("PRELIMINARY");
        assertThat(testB.getCaseStatus()).isEqualTo("CLSD");
        assertThat(testB.getCaseStatusDescription()).isEqualTo("CLOSED");
        assertThat(testB.getAssignedTo()).isEqualTo("MCFADDEN, HUGH");
        assertThat(testB.getHasOldDecreeIssuedDate()).isEqualTo(false);
        assertThat(testB.getProgramType()).isEqualTo("WC");
    }

    @Test
    public void testUpdateCourtCase() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId;

        CaseUpdateDto cud0 = new CaseUpdateDto();
        cud0.setCaseType("WCC");
        cud0.setDecreeId(37L);
        cud0.setWaterCourtCaseNumber("XYZ-123-NEWESTTEST4");
        cud0.setCaseStatus("OPEN");
        caseId = "11541";

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId)
                .content(getJson(cud0))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseDto test1 = convertTo(result, CaseDto.class);
        assertThat(test1.getCaseNumber()).isGreaterThan(0);

        CaseUpdateDto cud1 = new CaseUpdateDto();
        cud1.setCaseType("WCC");
        cud1.setDecreeId(37L);
        cud1.setWaterCourtCaseNumber("40M-66");
        cud1.setCaseStatus("OPEN");
        caseId = "11541";

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId)
                .content(getJson(cud1))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message msg1 = convertTo(result, Message.class);
        assertThat(msg1.getDeveloperMessage()).isEqualTo("ValidationException: Water Court Case Number is already in use for case id(s) 55");

        CaseUpdateDto cud2 = new CaseUpdateDto();
        cud2.setCaseType("ARMR");
        cud2.setOfficeId(2L);
        cud2.setCaseStatus("HRHD");
        caseId = "11541";

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId)
                .content(getJson(cud2))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseDto test0 = convertTo(result, CaseDto.class);
        assertThat(test0.getCaseNumber()).isGreaterThan(0);

    }

    @Test
    public void testUpdateCourtCaseCloseObjections() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId, sort;

        caseId = "9415";

        CaseUpdateDto cud0 = new CaseUpdateDto();
        cud0.setCaseStatus("CLSD");
        cud0.setCaseType("OBJL");

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId)
                .content(getJson(cud0))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseDto testA = convertTo(result, CaseDto.class);
        assertThat(testA.getCaseStatus()).isEqualTo(cud0.getCaseStatus());


        sort = "?sortDirection=ASC&sortColumn=COMPLETEWATERRIGHTNUMBER";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/water-right-versions")
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseWaterRightVersionsPageDto testB = convertTo(result, CaseWaterRightVersionsPageDto.class);


        for (CaseWaterRightVersionDetailDto o:testB.getResults()) {
            start = System.currentTimeMillis();
            result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/water-right/" + o.getWaterRightId() + "/version/" + o.getVersionId() + "/objections")
                    .header("Authorization", "Bearer " + token)
                    .param("caseId", caseId)
                    .param("waterRightId", o.getWaterRightId().toString())
                    .param("version", o.getVersionId().toString())
                    .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andReturn();
            end = System.currentTimeMillis();
            sec = (end - start) / 1000F;
            System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
            CaseWaterRightVersionObjectionsPageDto testC = convertTo(result, CaseWaterRightVersionObjectionsPageDto.class);
            for (WaterRightVersionObjectionsDto dto : testC.getResults()) {
                assertThat(dto.getStatus()).isEqualTo(Constants.CASE_STATUS_CLOSED);
            }
        }

    }

    @Test
    public void testGetCaseEvents() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;
        String sort = "";

        // EVENTTYPEDESCRIPTION, FILEDDATE, DUEDATE, ENTEREDBY
        sort = "?sortDirection=ASC&sortColumn=FILEDDATE";
        caseId = "4256";
        start = System.currentTimeMillis();

        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/register" + sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseRegisterPageDto testA = convertTo(result, CaseRegisterPageDto.class);
        assertThat(testA.getResults().get(0).getEventId()).isEqualTo(444837L);
        assertThat(testA.getResults().get(0).getEventType()).isEqualTo("CCSF");
        assertThat(testA.getResults().get(0).getEventTypeDescription()).isEqualTo("ORDER: CONSOLIDATING CLAIMS AND SETTING FILING DEADLINE");
        assertThat(testA.getResults().get(0).getFiledDate()).isEqualTo("2014-05-06");
        assertThat(testA.getResults().get(0).getDueDate()).isEqualTo("2014-07-28");
        assertThat(testA.getResults().get(0).getEnteredBy()).isEqualTo("HEISER, VICKI");
        assertThat(testA.getResults().get(0).getComments()).isNull();
        assertThat(testA.getResults().get(4).getEventId()).isEqualTo(456524L);
        assertThat(testA.getResults().get(4).getEventType()).isEqualTo("AMDR");
        assertThat(testA.getResults().get(4).getEventTypeDescription()).isEqualTo("ORDER: ADOPTING MASTER REPORT");
        assertThat(testA.getResults().get(4).getFiledDate()).isEqualTo("2014-10-17");
        assertThat(testA.getResults().get(4).getDueDate()).isNull();
        assertThat(testA.getResults().get(4).getEnteredBy()).isEqualTo("LAWSON, DIANE");
        assertThat(testA.getResults().get(4).getComments()).isEqualTo("ORDER ADOPTING MASTER'S REPORT");

    }

    @Test
    public void testUpdateCaseEvent() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId, eventId;

        caseId = "4286";
        eventId = "455714";

        CaseRegisterCreateUpdateDto crcud1 = new CaseRegisterCreateUpdateDto();
        crcud1.setEventType("FOBJ");
        crcud1.setComments("NOTICE OF UNCONDITIONAL WITHDRAWAL (ATTY CHEATUM & HOWE)");
        crcud1.setFiledDate(LocalDate.parse("2021-10-01", dtf));
        //crcud1.setDueDate();
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/register/" + eventId)
                .content(getJson(crcud1))
                .param("caseId", caseId)
                .param("eventId", eventId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseRegisterDetailDto test1 = convertTo(result, CaseRegisterDetailDto.class);
        assertThat(test1.getEventType()).isEqualTo("FOBJ");
        assertThat(test1.getEventTypeDescription()).isEqualTo("FILING: OTHER");
        assertThat(test1.getFiledDate()).isEqualTo("2021-10-01");
        assertThat(test1.getDueDate()).isEqualTo("2022-03-30");
        assertThat(test1.getEnteredBy()).isEqualTo("HEISER, VICKI");
        assertThat(test1.getComments()).isEqualTo(crcud1.getComments());


        CaseRegisterCreateUpdateDto crcud2 = new CaseRegisterCreateUpdateDto();
        crcud2.setEventType("FOTR");
        crcud2.setComments("NOTICE OF UNCONDITIONAL WITHDRAWAL (ATTY MILLER)");
        crcud2.setFiledDate(LocalDate.parse("2014-10-01", dtf));
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/register/" + eventId)
                .content(getJson(crcud2))
                .param("caseId", caseId)
                .param("eventId", eventId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseRegisterDetailDto test2 = convertTo(result, CaseRegisterDetailDto.class);
        assertThat(test2.getEventType()).isEqualTo("FOTR");
        assertThat(test2.getEventTypeDescription()).isEqualTo("NOTICE TO FILE OBJECTIONS");
        assertThat(test2.getFiledDate()).isEqualTo("2014-10-01");
        assertThat(test2.getDueDate()).isNull();
        assertThat(test2.getEnteredBy()).isEqualTo("HEISER, VICKI");
        assertThat(test2.getComments()).isEqualTo(crcud2.getComments());

    }

    @Test
    public void testCreateCaseEvent() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId;

        caseId = "4286";

        CaseRegisterCreateUpdateDto crcud1 = new CaseRegisterCreateUpdateDto();
        crcud1.setEventType("FOBJ");
        crcud1.setComments("CASE EVENT CREATE TEST - THIS CASE TYPE HAS 180 'DUE DAYS' THAT SHOULD BE ADDED TO FILED DATE ENTERED.");
        crcud1.setFiledDate(LocalDate.parse("2021-10-01", dtf));
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/register")
                .content(getJson(crcud1))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseRegisterDetailDto test1 = convertTo(result, CaseRegisterDetailDto.class);
        assertThat(test1.getEventType()).isEqualTo("FOBJ");
        assertThat(test1.getFiledDate()).isEqualTo("2021-10-01");
        assertThat(test1.getDueDate()).isEqualTo("2022-03-30");
        assertThat(test1.getComments()).isEqualTo(crcud1.getComments());

        CaseRegisterCreateUpdateDto crcud2 = new CaseRegisterCreateUpdateDto();
        crcud2.setEventType("FOTR");
        crcud2.setComments("CASE EVENT CREATE TEST - THIS CASE TYPE HAS 0 'DUE DAYS', ENTERED DUE DATE SHOULD BE USED.");
        crcud2.setFiledDate(LocalDate.parse("2021-10-01", dtf));
        crcud2.setDueDate(LocalDate.parse("2021-10-15", dtf));
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/register")
                .content(getJson(crcud2))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseRegisterDetailDto test2 = convertTo(result, CaseRegisterDetailDto.class);
        assertThat(test2.getEventType()).isEqualTo("FOTR");
        assertThat(test2.getFiledDate()).isEqualTo("2021-10-01");
        assertThat(test2.getDueDate()).isEqualTo("2021-10-15");
        assertThat(test2.getComments()).isEqualTo(crcud2.getComments());

    }

    @Test
    @Rollback
    public void testDeleteCaseEvent() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId;

        /** Create test event **/
        caseId = "4286";
        CaseRegisterCreateUpdateDto crcud1 = new CaseRegisterCreateUpdateDto();
        crcud1.setEventType("FOBJ");
        crcud1.setComments("TESTING CASE EVENT DELETE.");
        crcud1.setFiledDate(LocalDate.parse("2021-10-01", dtf));
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/register")
                .content(getJson(crcud1))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseRegisterDetailDto test1 = convertTo(result, CaseRegisterDetailDto.class);

        /** Delete the test event we just created **/
        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/register/" + test1.getEventId())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testGetCaseApplicationApplicants() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;
        String sort = "";

        // CONTACTID, FULLNAME, SUFFIX, BEGINDATE, ENDDATE
        sort = "?sortDirection=ASC&sortColumn=CONTACTID";

        caseId = "13855";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/applicants" + sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ApplicantsPageDto testA = convertTo(result, ApplicantsPageDto.class);
        assertThat(testA.getResults().get(0).getContactId()).isEqualTo(29266);
        assertThat(testA.getResults().get(0).getFirstName()).isEqualTo("DWIGHT");
        assertThat(testA.getResults().get(0).getLastName()).isEqualTo("VANNATTA");
        assertThat(testA.getResults().get(1).getContactId()).isEqualTo(368500);
        assertThat(testA.getResults().get(1).getFirstName()).isEqualTo("SHEILA");
        assertThat(testA.getResults().get(1).getLastName()).isEqualTo("VANNATTA");

        caseId = "4256";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/applicants" + sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ApplicantsPageDto testB = convertTo(result, ApplicantsPageDto.class);
        assertThat(testB.getResults().size()).isEqualTo(0);

    }

    @Test
    public void testGetCaseApplicationObjections() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;
        String sort = "";

        //ID, OBJECTIONTYPE, DATERECEIVED, LATE, STATUS
        sort = "?sortDirection=ASC&sortColumn=ID";

        caseId = "4256";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/objections" + sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsPageDto testA = convertTo(result, ObjectionsPageDto.class);
        assertThat(testA.getResults().getDetails().size()).isEqualTo(0);

        caseId = "14551";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/objections" + sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsPageDto testB = convertTo(result, ObjectionsPageDto.class);
        assertThat(testB.getResults().getDetails().get(0).getId()).isEqualTo(88393);
        assertThat(testB.getResults().getDetails().get(0).getObjectionType()).isEqualTo("OBJECTION TO APPLICATION");
        assertThat(testB.getResults().getDetails().get(0).getDateReceived()).isEqualTo("2019-11-27");

    }

    @Test
    public void testGetCaseComments() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;

        caseId = "11540";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/comments")
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseCommentsDto testA = convertTo(result, CaseCommentsDto.class);
        assertThat(testA.getComments()).isEqualTo("CGWA - PROPOSAL NOTICE PUBLISHED 09/07/18; HEARING HELD 10/16/18; ADOPTION NOTICE PUBLISHED 12/07/18\nCDF ADDED COMMENT\nPLUS ONE MORE LINE");

    }

    @Test
    public void testUpdateCaseComments() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId, comments, test;
        float sec;

        comments = "CGWA - PROPOSAL NOTICE PUBLISHED 09/07/18; HEARING HELD 10/16/18; ADOPTION NOTICE PUBLISHED 12/07/18";
        test = "\nCDF ADDED COMMENT\nPLUS ONE MORE LINE";

        caseId = "11540";

        CaseCommentsDto dtoA = new CaseCommentsDto();
        dtoA.setComments(comments);
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/comments")
                .content(getJson(dtoA))
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseCommentsDto testA = convertTo(result, CaseCommentsDto.class);
        assertThat(testA.getComments()).isEqualTo(comments);

        CaseCommentsDto dtoB = new CaseCommentsDto();
        dtoB.setComments(comments + test);
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/comments")
                .content(getJson(dtoB))
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseCommentsDto testB = convertTo(result, CaseCommentsDto.class);
        assertThat(testB.getComments()).isEqualTo(comments + test);

    }


}
