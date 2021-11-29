package gov.mt.wris;

import gov.mt.wris.dtos.CaseRegisterCreateUpdateDto;
import gov.mt.wris.dtos.CaseRegisterDetailDto;
import gov.mt.wris.dtos.DistrictCourtCreateDto;
import gov.mt.wris.dtos.DistrictCourtDetailDto;
import gov.mt.wris.dtos.DistrictCourtEventCreateDto;
import gov.mt.wris.dtos.DistrictCourtEventDetailDto;
import gov.mt.wris.dtos.DistrictCourtEventUpdateDto;
import gov.mt.wris.dtos.DistrictCourtEventsPageDto;
import gov.mt.wris.dtos.DistrictCourtUpdateDto;
import gov.mt.wris.dtos.DistrictCourtsPageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class CasesDistrictCourtIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDistrictCourts() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;
        String sort = "";

        // CAUSENUMBER, DISTRICTCOURTNUMBER, COMPLETENAME, COUNTYNAME, SUPREMECOURTCAUSENUMBER
        sort = "?sortDirection=ASC&sortColumn=CAUSENUMBER";
        caseId = "18357";
        start = System.currentTimeMillis();

        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/district-court" + sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DistrictCourtsPageDto testA = convertTo(result, DistrictCourtsPageDto.class);
        assertThat(testA.getResults().get(0).getDistrictId()).isEqualTo(20);
        assertThat(testA.getResults().get(0).getCauseNumber()).isEqualTo("DDV 2018-151");
        assertThat(testA.getResults().get(0).getDistrictCourtNumber()).isEqualTo(1);
        assertThat(testA.getResults().get(0).getDnrcId()).isEqualTo(304);
        assertThat(testA.getResults().get(0).getCompleteName()).isEqualTo("MCCARTER, HON. DOROTHY");
        assertThat(testA.getResults().get(0).getCountyId()).isEqualTo(8);
        assertThat(testA.getResults().get(0).getCountyName()).isEqualTo("BROADWATER");
        assertThat(testA.getResults().get(0).getSupremeCourtCauseNumber()).isEqualTo("DA 19-0484");
        assertThat(testA.getResults().get(1).getDistrictId()).isEqualTo(21);
        assertThat(testA.getResults().get(1).getCauseNumber()).isEqualTo("DDV 2018-201");
        assertThat(testA.getResults().get(1).getDistrictCourtNumber()).isEqualTo(2);
        assertThat(testA.getResults().get(1).getDnrcId()).isEqualTo(308);
        assertThat(testA.getResults().get(1).getCompleteName()).isEqualTo("NEWMAN, HON. BRAD");
        assertThat(testA.getResults().get(1).getCountyId()).isEqualTo(51);
        assertThat(testA.getResults().get(1).getCountyName()).isEqualTo("SILVER BOW");
        assertThat(testA.getResults().get(1).getSupremeCourtCauseNumber()).isEqualTo("DA 23-2193");
        assertThat(testA.getResults().get(2).getDistrictId()).isEqualTo(22);
        assertThat(testA.getResults().get(2).getCauseNumber()).isEqualTo("XYZ 2021-777");
        assertThat(testA.getResults().get(2).getDistrictCourtNumber()).isEqualTo(2);
        assertThat(testA.getResults().get(2).getDnrcId()).isEqualTo(308);
        assertThat(testA.getResults().get(1).getCompleteName()).isEqualTo("NEWMAN, HON. BRAD");
        assertThat(testA.getResults().get(2).getCountyId()).isEqualTo(51);
        assertThat(testA.getResults().get(2).getCountyName()).isEqualTo("SILVER BOW");
        assertThat(testA.getResults().get(2).getSupremeCourtCauseNumber()).isNull();

    }

    @Test
    public void testGetDistrictCourtEvents() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId, districtId;
        float sec;
        String sort = "";

        // EVENTTYPEDESCRIPTION, EVENTDATE
        sort = "?sortDirection=ASC&sortColumn=EVENTTYPEDESCRIPTION";
        caseId = "9235";
        districtId = "7";
        start = System.currentTimeMillis();

        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/district-court/" + districtId + "/events"+ sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .param("caseId", districtId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DistrictCourtEventsPageDto testA = convertTo(result, DistrictCourtEventsPageDto.class);
        assertThat(testA.getResults().get(0).getEventDateId()).isEqualTo(543914);
        assertThat(testA.getResults().get(0).getEventType()).isEqualTo("CARD");
        assertThat(testA.getResults().get(0).getEventTypeDescription()).isEqualTo("CERTIFIED ADMINISTRATIVE RECORD TO DISTRICT COURT");
        assertThat(testA.getResults().get(0).getEventDate()).isEqualTo("2018-03-02");
        assertThat(testA.getResults().get(0).getComments()).isNull();
        assertThat(testA.getResults().get(3).getEventDateId()).isEqualTo(583936);
        assertThat(testA.getResults().get(3).getEventType()).isEqualTo("SCAP");
        assertThat(testA.getResults().get(3).getEventTypeDescription()).isEqualTo("SUPREME COURT-APPEALED TO SUPREME COURT");
        assertThat(testA.getResults().get(3).getEventDate()).isEqualTo("2019-08-22");
        assertThat(testA.getResults().get(3).getComments()).isEqualTo("DA 19-0484");

    }

    @Test
    public void testCreateUpdateDeleteDistrictCourt() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId, districtId, eventDateId, judgeId;

        // minimal create
        caseId = "9235";
        DistrictCourtCreateDto dcd1 = new DistrictCourtCreateDto();
        dcd1.setCauseNumber("DVD 2001-CDROM FLPY");
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/district-court")
                .content(getJson(dcd1))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DistrictCourtDetailDto test1 = convertTo(result, DistrictCourtDetailDto.class);
        assertThat(test1.getCauseNumber()).isEqualTo(dcd1.getCauseNumber());

        judgeId = "309";
        DistrictCourtUpdateDto dud1 = new DistrictCourtUpdateDto();
        dud1.setCauseNumber("DVD 2001-CDROM FLPY");
        dud1.setDistrictCourtNumber(2);
        dud1.setDnrcId(Long.valueOf(judgeId));
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/district-court/" + test1.getDistrictId())
                .content(getJson(dud1))
                .param("caseId", caseId)
                .param("districtId", test1.getDistrictId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DistrictCourtDetailDto test2 = convertTo(result, DistrictCourtDetailDto.class);
        assertThat(test2.getDnrcId()).isEqualTo(Long.valueOf(judgeId));

        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/district-court/" + test1.getDistrictId())
                .param("caseId", caseId)
                .param("districtId", test1.getDistrictId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testDeleteDistrictCourt() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId, districtId, eventDateId, judgeId;

        caseId = "9235";
        districtId = "36";
        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/district-court/" + districtId)
                .param("caseId", caseId)
                .param("districtId", districtId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testCreateUpdateDeleteDistrictCourtEvent() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId, districtId, eventDateId, judgeId;

        // minimal create
        caseId = "9235";
        districtId = "7";
        DistrictCourtEventCreateDto dcec1 = new DistrictCourtEventCreateDto();
        dcec1.setEventType("CARD");
        dcec1.setEventDate(LocalDate.parse("2021-10-01", dtf));

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/district-court/" + districtId + "/events")
                .content(getJson(dcec1))
                .param("caseId", caseId)
                .param("districtId", districtId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DistrictCourtEventDetailDto test1 = convertTo(result, DistrictCourtEventDetailDto.class);
        assertThat(test1.getEventType()).isEqualTo(dcec1.getEventType());
        assertThat(test1.getEventDate()).isEqualTo("2021-10-01");
        assertThat(test1.getComments()).isNull();

        DistrictCourtEventUpdateDto dceu1 = new DistrictCourtEventUpdateDto();
        dceu1.setComments("TESTING 123");
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/cases/" + caseId + "/district-court/" + districtId + "/events/" + test1.getEventDateId())
                .content(getJson(dceu1))
                .param("caseId", caseId)
                .param("districtId", districtId)
                .param("eventDateId", test1.getEventDateId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        DistrictCourtEventDetailDto test2 = convertTo(result, DistrictCourtEventDetailDto.class);
        assertThat(test2.getComments()).isEqualTo(dceu1.getComments());

        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/cases/" + caseId + "/district-court/" + districtId + "/events/" + test1.getEventDateId())
                .param("caseId", caseId)
                .param("districtId", districtId)
                .param("eventDateId", test1.getEventDateId().toString())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }


}
