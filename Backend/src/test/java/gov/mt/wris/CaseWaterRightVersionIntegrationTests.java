package gov.mt.wris;

import gov.mt.wris.dtos.CaseWaterRightVersionObjectionsPageDto;
import gov.mt.wris.dtos.CaseWaterRightVersionReferenceDto;
import gov.mt.wris.dtos.CaseWaterRightVersionsPageDto;
import gov.mt.wris.dtos.EligibleWaterRightsPageDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.WaterRightsReferenceDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class CaseWaterRightVersionIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testGetCaseWaterRightVersions() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId;
        float sec;
        String sort = "";

        // COMPLETEWATERRIGHTNUMBER, WATERRIGHTTYPEDESCRIPTION, WATERRIGHTSTATUSDESCRIPTION, COMPLETEVERSION
        sort = "?sortDirection=ASC&sortColumn=COMPLETEWATERRIGHTNUMBER";
        caseId = "2218";
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
        CaseWaterRightVersionsPageDto testA = convertTo(result, CaseWaterRightVersionsPageDto.class);
        assertThat(testA.getResults().get(0).getWaterRightId()).isEqualTo(156657);
        assertThat(testA.getResults().get(0).getVersionId()).isEqualTo(1);
        assertThat(testA.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("41D 88463 00");
        assertThat(testA.getResults().get(0).getWaterRightType()).isEqualTo("STOC");
        assertThat(testA.getResults().get(0).getWaterRightTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testA.getResults().get(0).getWaterRightStatus()).isEqualTo("ACTV");
        assertThat(testA.getResults().get(0).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testA.getResults().get(0).getCompleteVersion()).isEqualTo("ORIGINAL RIGHT 1");
        assertThat(testA.getResults().get(10).getWaterRightId()).isEqualTo(165001);
        assertThat(testA.getResults().get(10).getVersionId()).isEqualTo(1);
        assertThat(testA.getResults().get(10).getCompleteWaterRightNumber()).isEqualTo("41D 93301 00");
        assertThat(testA.getResults().get(10).getWaterRightType()).isEqualTo("STOC");
        assertThat(testA.getResults().get(10).getWaterRightTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testA.getResults().get(10).getWaterRightStatus()).isEqualTo("ACTV");
        assertThat(testA.getResults().get(10).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testA.getResults().get(10).getCompleteVersion()).isEqualTo("ORIGINAL RIGHT 1");
    }

    @Test
    public void testGetCaseWaterRightVersionObjections() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String caseId, waterRightId, version;
        float sec;
        String sort = "";

        // OBJECTIONID, OBJECTIONTYPEDESCRIPTION, DATERECEIVED, LATE, OBJECTIONSTATUSDESCRIPTION
        sort = "?sortDirection=ASC&sortColumn=OBJECTIONID";
        caseId = "2372";
        waterRightId = "60244";
        version = "1";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/" + caseId + "/water-right/" + waterRightId + "/version/" + version + "/objections" + sort)
                .header("Authorization", "Bearer " + token)
                .param("caseId", caseId)
                .param("waterRightId", waterRightId)
                .param("version", version)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseWaterRightVersionObjectionsPageDto testA = convertTo(result, CaseWaterRightVersionObjectionsPageDto.class);
        assertThat(testA.getResults().get(0).getId()).isEqualTo(67211);
        assertThat(testA.getResults().get(0).getObjectionType()).isEqualTo("OTW");
        assertThat(testA.getResults().get(0).getLate()).isEqualTo("NO");
        assertThat(testA.getResults().get(0).getStatus()).isEqualTo("CLSD");
        assertThat(testA.getResults().get(0).getDateReceived()).isEqualTo("2014-05-05");
        assertThat(testA.getResults().get(0).getObjectionTypeDescription()).isEqualTo("OBJECTION TO WATER RIGHT");
        assertThat(testA.getResults().get(0).getObjectionStatusDescription()).isEqualTo("CLOSED");
        assertThat(testA.getResults().get(4).getId()).isEqualTo(67650);
        assertThat(testA.getResults().get(4).getObjectionType()).isEqualTo("OTW");
        assertThat(testA.getResults().get(4).getLate()).isEqualTo("YES");
        assertThat(testA.getResults().get(4).getStatus()).isEqualTo("CLSD");
        assertThat(testA.getResults().get(4).getDateReceived()).isEqualTo("2014-05-08");
        assertThat(testA.getResults().get(4).getObjectionTypeDescription()).isEqualTo("OBJECTION TO WATER RIGHT");
        assertThat(testA.getResults().get(4).getObjectionStatusDescription()).isEqualTo("CLOSED");

    }


    @Test
    public void testCreateAndDeleteCaseWaterRightVersionReference() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId;

        caseId = "4286";

        CaseWaterRightVersionReferenceDto ref1 = new CaseWaterRightVersionReferenceDto();
        WaterRightsReferenceDto dto1 = new WaterRightsReferenceDto();
        List<WaterRightsReferenceDto> list = new ArrayList<>();
        dto1.setWaterRightId(352L);
        dto1.setVersionId(1L);
        list.add(dto1);
        ref1.setWaterRightVersions(list);

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/water-right-versions")
                .content(getJson(ref1))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        Message msg1 = convertTo(result, Message.class);
        assertThat(msg1.getDeveloperMessage()).contains("Water right number is already in use for case id(s) 4193, 4378");

        List<WaterRightsReferenceDto> list2 = new ArrayList<>();
        dto1.setWaterRightId(3222L);
        dto1.setVersionId(1L);
        list2.add(dto1);
        WaterRightsReferenceDto dto2 = new WaterRightsReferenceDto();
        dto2.setWaterRightId(2425L);
        dto2.setVersionId(1L);
        list2.add(dto2);
        ref1.setWaterRightVersions(list2);

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/cases/" + caseId + "/water-right-versions")
                .content(getJson(ref1))
                .param("caseId", caseId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CaseWaterRightVersionReferenceDto test1 = convertTo(result, CaseWaterRightVersionReferenceDto.class);
        List<WaterRightsReferenceDto> dtos = test1.getWaterRightVersions().stream().map(o -> {
            WaterRightsReferenceDto dto = new WaterRightsReferenceDto();
            assertThat(o.getWaterRightId()==dto1.getWaterRightId() || o.getWaterRightId()==dto2.getWaterRightId());
            assertThat(o.getVersionId()==dto1.getVersionId() || o.getVersionId()==dto2.getVersionId());
            dto.setWaterRightId(o.getWaterRightId());
            dto.setVersionId(o.getVersionId());
            return dto;
        }).collect(Collectors.toList());

        for (WaterRightsReferenceDto o:dtos) {
            start = System.currentTimeMillis();
            mockMvc.perform(delete("/api/v1/cases/" + caseId + "/water-right/" + o.getWaterRightId() + "/version/" + o.getVersionId())
                    .param("caseId", caseId)
                    .param("waterRightId", o.getWaterRightId().toString())
                    .param("version", o.getVersionId().toString())
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json"))
                    .andExpect(status().isNoContent())
                    .andReturn();
            end = System.currentTimeMillis();
            sec = (end - start) / 1000F;
            System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        }

    }


    @Test
    public void testDeleteCaseWaterRightVersionReference() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MvcResult result = null;
        long start, end;
        float sec;
        String caseId, waterRightId, version;

        caseId = "4286";
        //waterRightId = "3222";
        waterRightId = "309436";
        version = "1";

        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/cases/" + caseId + "/water-right/" + waterRightId + "/version/" + version)
                .param("caseId", caseId)
                .param("waterRightId", waterRightId)
                .param("version", version)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testGetEligibleWaterRights() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String waterNumber, decreeId, basin;
        float sec;
        String sort = "";

        // WATERRIGHTNUMBER, EXT, VERSION, BASIN, WATERRIGHTSTATUSDESCRIPTION
        sort = "?sortDirection=ASC&sortColumn=WATERRIGHTNUMBER&pageSize=100&pageNumber=1";
        decreeId = "159";
        basin = "40E";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/decree/" + decreeId + "/eligible-water-rights/" + basin + sort)
                .header("Authorization", "Bearer " + token)
                .param("decreeId", decreeId)
                .param("basin", basin)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EligibleWaterRightsPageDto testA = convertTo(result, EligibleWaterRightsPageDto.class);
        assertThat(testA.getResults().get(0).getWaterRightId()).isEqualTo(1706L);
        assertThat(testA.getResults().get(0).getVersionId()).isEqualTo(1L);
        assertThat(testA.getResults().get(0).getWaterRightNumber()).isEqualTo(615L);
        assertThat(testA.getResults().get(0).getExt()).isEqualTo("00");
        assertThat(testA.getResults().get(0).getBasin()).isEqualTo("40E");
        assertThat(testA.getResults().get(0).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testA.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("40E 615 00");
        assertThat(testA.getResults().get(0).getCompleteVersion()).isEqualTo("ORIGINAL RIGHT - 1");
        assertThat(testA.getResults().get(99).getWaterRightId()).isEqualTo(23370L);
        assertThat(testA.getResults().get(99).getVersionId()).isEqualTo(2L);
        assertThat(testA.getResults().get(99).getWaterRightNumber()).isEqualTo(8648L);
        assertThat(testA.getResults().get(99).getExt()).isEqualTo("00");
        assertThat(testA.getResults().get(99).getBasin()).isEqualTo("40E");
        assertThat(testA.getResults().get(99).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testA.getResults().get(99).getCompleteWaterRightNumber()).isEqualTo("40E 8648 00");
        assertThat(testA.getResults().get(99).getCompleteVersion()).isEqualTo("REEXAMINED - 2");


        // WATERRIGHTNUMBER, EXT, VERSION, BASIN, WATERRIGHTSTATUSDESCRIPTION
        sort = "?sortDirection=ASC&sortColumn=WATERRIGHTNUMBER&pageSize=100&pageNumber=69";
        decreeId = "159";
        basin = "40E";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/cases/decree/" + decreeId + "/eligible-water-rights/" + basin + sort)
                .header("Authorization", "Bearer " + token)
                .param("decreeId", decreeId)
                .param("basin", basin)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EligibleWaterRightsPageDto testB = convertTo(result, EligibleWaterRightsPageDto.class);
        assertThat(testB.getResults().get(0).getWaterRightId()).isEqualTo(479085L);
        assertThat(testB.getResults().get(0).getVersionId()).isEqualTo(1L);
        assertThat(testB.getResults().get(0).getWaterRightNumber()).isEqualTo(30142929L);
        assertThat(testB.getResults().get(0).getExt()).isNull();
        assertThat(testB.getResults().get(0).getBasin()).isEqualTo("40E");
        assertThat(testB.getResults().get(0).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testB.getResults().get(0).getCompleteWaterRightNumber()).isNull();
        assertThat(testB.getResults().get(0).getCompleteVersion()).isEqualTo("ORIGINAL RIGHT - 1");
        assertThat(testB.getResults().get(30).getWaterRightId()).isEqualTo(483077L);
        assertThat(testB.getResults().get(30).getVersionId()).isEqualTo(1L);
        assertThat(testB.getResults().get(30).getWaterRightNumber()).isEqualTo(30147037L);
        assertThat(testB.getResults().get(30).getExt()).isNull();
        assertThat(testB.getResults().get(30).getBasin()).isEqualTo("40E");
        assertThat(testB.getResults().get(30).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testB.getResults().get(30).getCompleteWaterRightNumber()).isNull();
        assertThat(testB.getResults().get(30).getCompleteVersion()).isEqualTo("ORIGINAL RIGHT - 1");

    }


}
