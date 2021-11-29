package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import gov.mt.wris.dtos.EligibleWaterRightVersionPageDto;
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

import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.PeriodOfUseDto;
import gov.mt.wris.dtos.PlaceOfUseDto;
import gov.mt.wris.dtos.VersionCreationDto;
import gov.mt.wris.dtos.VersionDetailDto;
import gov.mt.wris.dtos.VersionDto;
import gov.mt.wris.dtos.VersionUpdateDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesPageDto;
import gov.mt.wris.dtos.WaterRightVersionCasesPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectionsElementsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectionsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectorsPageDto;
import gov.mt.wris.dtos.WaterRightVersionPageDto;
import gov.mt.wris.dtos.WaterRightVersionPurposeCreationDto;
import gov.mt.wris.services.WaterRightVersionService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class WaterRightVersionIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WaterRightVersionService versionService;

    @Test
    public void testCreateVersion() throws Exception {
        String token = getAccessToken();

        VersionCreationDto creationDto = new VersionCreationDto();
        creationDto.setVersionTypeCode("CHAU");

        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/4856/versions")
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isCreated())
                                        .andReturn();
        VersionDto createdVersion = convertTo(result, VersionDto.class);

        assertThat(createdVersion.getVersion()).isNotNull();

        versionService.deleteVersion(createdVersion.getVersion(), 4856L);
    }

    @Test
    @Rollback
    public void testSearchWaterRightsVersions() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId;
        String waterRightNumber, basin, ext;
        float sec;
        String sort = "";
/*
        sort = "?sortDirection=ASC&sortColumn=WATERRIGHTTYPEDESCRIPTION";
        waterRightNumber = "67%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testA = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testA.getResults().get(0).getWaterRightTypeDescription()).isEqualTo("CONVERTED TERMINATES");
        assertThat(testA.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("41F 671 00");
        assertThat(testA.getResults().get(24).getWaterRightTypeDescription()).isEqualTo("CONVERTED TERMINATES");
        assertThat(testA.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("43D 67190 00");


        sort = "?sortDirection=ASC&sortColumn=VERSIONSTATUSDESCRIPTION&pageSize=25&pageNumber=1";
        waterRightNumber = "67%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testB = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testB.getResults().get(0).getVersionStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testB.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("42J 67 00");
        assertThat(testB.getResults().get(24).getVersionStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testB.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("40R 675 00");

        sort = "?sortDirection=DESC&sortColumn=VERSIONSTATUSDESCRIPTION&pageSize=25&pageNumber=1";
        waterRightNumber = "67%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testC = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testC.getResults().get(0).getVersionStatusDescription()).isEqualTo("WITHDRAWN");
        assertThat(testC.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("41H 6736 00");
        assertThat(testC.getResults().get(24).getVersionStatusDescription()).isEqualTo("WITHDRAWN");
        assertThat(testC.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("40J 67914 00");


        sort = "?sortDirection=ASC&sortColumn=VERSION&pageSize=25&pageNumber=1";
        waterRightNumber = "67%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testD = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testD.getResults().get(0).getVersion()).isEqualTo(1);
        assertThat(testD.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("42J 67 00");
        assertThat(testD.getResults().get(24).getVersion()).isEqualTo(1);
        assertThat(testD.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("40R 675 00");

        sort = "?sortDirection=DESC&sortColumn=VERSION&pageSize=25&pageNumber=1";
        waterRightNumber = "67%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testE = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testE.getResults().get(0).getVersion()).isEqualTo(3);
        assertThat(testE.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("41H 6701 00");
        assertThat(testE.getResults().get(24).getVersion()).isEqualTo(3);
        assertThat(testE.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("40K 67654 00");


        sort = "?sortDirection=ASC&sortColumn=OPERATINGAUTHORITY&pageSize=25&pageNumber=1";
        waterRightNumber = "67%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testF = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testF.getResults().get(0).getOperatingAuthority()).isEqualTo("1919-12-31");
        assertThat(testF.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("76H 67575 00");
        assertThat(testF.getResults().get(24).getOperatingAuthority()).isEqualTo("1973-07-01");
        assertThat(testF.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("41I 671 00");

        sort = "?sortDirection=DESC&sortColumn=OPERATINGAUTHORITY&pageSize=25&pageNumber=1";
        waterRightNumber = "67%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testG = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testG.getResults().get(0).getOperatingAuthority()).isNull();
        assertThat(testG.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("43D 67 00");
        assertThat(testG.getResults().get(24).getOperatingAuthority()).isNull();
        assertThat(testG.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("76G 67030 00");


        sort = "?sortDirection=ASC&sortColumn=COMPLETEWATERRIGHTNUMBER";
        waterRightNumber = "670";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testH = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testH.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("41U 670 00");
        assertThat(testH.getResults().get(3).getCompleteWaterRightNumber()).isEqualTo("76H 670 00");

        sort = "?sortDirection=ASC&sortColumn=VERSIONTYPEDESCRIPTION";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testI = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testI.getResults().get(0).getVersionTypeDescription()).isEqualTo("ORIGINAL RIGHT");
        assertThat(testI.getResults().get(3).getVersionTypeDescription()).isEqualTo("REEXAMINED");
*/

        sort = "?sortDirection=ASC&sortColumn=WATERRIGHTNUMBER";
        waterRightNumber = "301%";
        basin = "40J";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-right-versions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionPageDto testJ = convertTo(result, WaterRightVersionPageDto.class);
        assertThat(testJ.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("42I 301 00");
        assertThat(testJ.getResults().get(0).getWaterRightId()).isEqualTo(827L);
        assertThat(testJ.getResults().get(0).getVersion()).isEqualTo(1L);
        assertThat(testJ.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("76F 3015 00");
        assertThat(testJ.getResults().get(24).getWaterRightId()).isEqualTo(8160L);
        assertThat(testJ.getResults().get(24).getVersion()).isEqualTo(2L);

    }

    @Test
    public void getWaterRightVersionDetail_withExistingWaterRightVersion_isNotEmpty() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long waterRightId, versionNumber;
        float sec;

        waterRightId = 1898L; /* returns 1 */
        versionNumber = 1L;
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber)
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", waterRightId.toString())
                .param("versionNumber", versionNumber.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        VersionDetailDto testA = convertTo(result, VersionDetailDto.class);
        assertThat(testA.getIsVersionLocked()).isEqualTo(true);
        assertThat(testA.getIsEditableIfDecreed()).isEqualTo(true);
        assertThat(testA.getWaterRightTypeCode()).isEqualTo("STOC");

    }

    @Test
    public void testUpdateWaterRightVersion() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long waterRightId, versionNumber;
        float sec;

        waterRightId = 4856L;
        VersionCreationDto dto1 = new VersionCreationDto();
        dto1.setVersionTypeCode("CHAU");
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/water-rights/" + waterRightId + "/versions")
                .content(getJson(dto1))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        VersionDto test1 = convertTo(result, VersionDto.class);
        assertThat(test1.getVersion()).isNotNull();
        assertThat(test1.getVersionTypeCode()).isEqualTo("CHAU");

        versionNumber = test1.getVersion();
        VersionUpdateDto dto2 = new VersionUpdateDto();
        dto2.setStandardsUpdated(true);
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber)
                .content(getJson(dto2))
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", waterRightId.toString())
                .param("versionNumber", versionNumber.toString())
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        VersionUpdateDto dto3 = new VersionUpdateDto();
        dto3.setStandardsUpdated(false);
        dto3.setVersionTypeCode("REXM");
        dto3.setVersionStatusCode("TERM");
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber)
                .content(getJson(dto3))
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", waterRightId.toString())
                .param("versionNumber", versionNumber.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        result = mockMvc.perform(get("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber)
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", waterRightId.toString())
                .param("versionNumber", versionNumber.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        VersionDetailDto test3 = convertTo(result, VersionDetailDto.class);
        assertThat(test3.getVersionTypeCode()).isEqualTo("REXM");
        assertThat(test3.getVersionStatusCode()).isEqualTo("TERM");

        versionService.deleteVersion(versionNumber, waterRightId);

    }

    @Test
    public void testAddAndDeleteApplicationReferenceToWaterRightVersion() throws Exception {

        String token = getAccessToken();
        long start, end, waterRightId, versionNumber, applicationId;
        float sec;
        Message message;
        MvcResult result;

        waterRightId = 3;
        versionNumber = 1;
        applicationId = 30152355;
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber + "/applications/" + applicationId)
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionNumber", String.valueOf(versionNumber))
                .param("applicationId", String.valueOf(applicationId))
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Unable to create application reference, reference with water right id 3, version number 1 and application id 30152355 currently exists.");

        waterRightId = 3;
        versionNumber = 1;
        applicationId = 30152596;
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber + "/applications/" + applicationId)
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionNumber", String.valueOf(versionNumber))
                .param("applicationId", String.valueOf(applicationId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionApplicationReferencesDto test1 = convertTo(result, WaterRightVersionApplicationReferencesDto.class);
        assertThat(test1.getApplicationId()).isNotNull();

        waterRightId = 3;
        versionNumber = 1;
        applicationId = 30152596;
        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber + "/applications/" + applicationId)
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionNumber", String.valueOf(versionNumber))
                .param("applicationId", String.valueOf(applicationId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
    }


    @Test
    public void testGetApplicationReferenceToWaterRightVersion() throws Exception {

        String token = getAccessToken();
        long start, end, waterRightId, versionNumber, applicationId;
        float sec;
        Message message;
        MvcResult result;

        waterRightId = 3;
        versionNumber = 1;
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber + "/applications/")
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionNumber", String.valueOf(versionNumber))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionApplicationReferencesPageDto test1 = convertTo(result, WaterRightVersionApplicationReferencesPageDto.class);
        assertThat(test1.getResults().get(0).getApplicationId()).isEqualTo(100);
        assertThat(test1.getResults().get(11).getApplicationId()).isEqualTo(30153558);
    }

    @Test
    public void testGetWaterRightVersionObjectors() throws Exception {

        String token = getAccessToken();
        long start, end, waterRightId, versionNumber, objectionId;
        float sec;
        String sort = "?sortDirection=DESC&sortColumn=NAME";
        MvcResult result;

        waterRightId = 241972;
        versionNumber = 1;
        objectionId = 44143;
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber + "/objections/" + objectionId + "/objectors" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionNumber", String.valueOf(versionNumber))
                .param("objectionId", String.valueOf(objectionId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionObjectorsPageDto test1 = convertTo(result, WaterRightVersionObjectorsPageDto.class);
        assertThat(test1.getResults().get(0).getContactId()).isEqualTo(2014);
        assertThat(test1.getResults().get(0).getName()).isEqualTo("USA (DEPT OF INTERIOR BUREAU OF LAND MGMT)");

    }

    @Test
    public void testGetWaterRightVersionCases() throws Exception {

        String token = getAccessToken();
        long start, end, waterRightId, versionNumber, applicationId;
        float sec;

        MvcResult result;

        waterRightId = 199802;
        versionNumber = 1;
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber + "/cases/")
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionNumber", String.valueOf(versionNumber))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** CREATE ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionCasesPageDto test1 = convertTo(result, WaterRightVersionCasesPageDto.class);
        assertThat(test1.getResults().get(0).getCaseNumber()).isEqualTo(3617);
        assertThat(test1.getResults().get(0).getWaterCourtCase()).isEqualTo("41O-84");
        assertThat(test1.getResults().get(0).getHearingDate()).isEqualTo("2012-12-10");

    }

    @Test
    public void testGetWaterRightVersionObjections() throws Exception {

        String token = getAccessToken();
        long start, end, waterRightId, versionNumber, applicationId;
        float sec;
        String sort = "?sortDirection=ASC&sortColumn=ID";
        MvcResult result;

        waterRightId = 240814;
        versionNumber = 1;
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber + "/objections/" + sort)
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionNumber", String.valueOf(versionNumber))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionObjectionsPageDto test1 = convertTo(result, WaterRightVersionObjectionsPageDto.class);
        assertThat(test1.getResults().get(0).getId()).isEqualTo(44122);
        assertThat(test1.getResults().get(0).getObjectionStatusDescription()).isEqualTo("CLOSED");
        assertThat(test1.getResults().get(9).getId()).isEqualTo(45442);
        assertThat(test1.getResults().get(9).getObjectionStatusDescription()).isEqualTo("CLOSED");

    }

    @Test
    public void testGetWaterRightVersionObjectionElements() throws Exception {

        String token = getAccessToken();
        long start, end, waterRightId, versionNumber, objectionId;
        float sec;
        MvcResult result;

        waterRightId = 241972;
        versionNumber = 1;
        objectionId = 44143;
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/water-rights/" + waterRightId + "/versions/" + versionNumber + "/objections/"+ objectionId + "/elements")
                .header("Authorization", "Bearer " + token)
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionNumber", String.valueOf(versionNumber))
                .param("objectionId", String.valueOf(objectionId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightVersionObjectionsElementsPageDto test1 = convertTo(result, WaterRightVersionObjectionsElementsPageDto.class);
        assertThat(test1.getResults().get(0).getElementType()).isEqualTo("PLOU");
        assertThat(test1.getResults().get(0).getElementObjectionId()).isEqualTo(6958);
        assertThat(test1.getResults().get(0).getElementTypeDescription()).isEqualTo("PLACE OF USE");
        assertThat(test1.getResults().get(0).getElementObjectionRemark()).isEqualTo("NOT IN THE TETON RIVER BASIN ACCORDING TO THE ABSTRACT INCLUDED IN THE DECREE");
        assertThat(test1.getResults().get(1).getElementType()).isEqualTo("PTOD");
        assertThat(test1.getResults().get(1).getElementObjectionId()).isEqualTo(6959);
        assertThat(test1.getResults().get(1).getElementTypeDescription()).isEqualTo("POINT OF DIVERSION");
        assertThat(test1.getResults().get(1).getElementObjectionRemark()).isEqualTo("NOT IN THE TETON RIVER BASIN ACCORDING TO THE ABSTRACT INCLUDED IN THE DECREE");


    }

    @Test
    public void testCreateWaterRightVersionPurpose() throws Exception {

        String token = getAccessToken();
        WaterRightVersionPurposeCreationDto creationDto = new WaterRightVersionPurposeCreationDto();
        List<PlaceOfUseDto> places = new ArrayList<>();
        PlaceOfUseDto place = new PlaceOfUseDto();

        List<PeriodOfUseDto> periods = new ArrayList<>();
        PeriodOfUseDto period = new PeriodOfUseDto();


        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/4856/versions")
                .content(getJson(creationDto))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        VersionDto createdVersion = convertTo(result, VersionDto.class);

        assertThat(createdVersion.getVersion()).isNotNull();

        versionService.deleteVersion(createdVersion.getVersion(), 4856L);
    }

    @Test
    public void testGetEligibleWaterRightVersions() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String basin, waterNumber;
        float sec;
        String sort = "";

        // WATERRIGHTNUMBER, BASIN, EXT, WATERRIGHTTYPEDESCRIPTION, WATERRIGHTSTATUSDESCRIPTION
        sort = "?sortDirection=ASC&sortColumn=WATERRIGHTNUMBER";
        basin = "40E";
        waterNumber = "1%";
        start = System.currentTimeMillis();

        result = mockMvc.perform(get("/api/v1/water-rights/" + basin + "/eligible-water-rights" + sort)
                .header("Authorization", "Bearer " + token)
                .param("basin", basin)
                .param("waterNumber", waterNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EligibleWaterRightVersionPageDto testA = convertTo(result, EligibleWaterRightVersionPageDto.class);
        assertThat(testA.getResults().get(0).getWaterRightId()).isEqualTo(291982L);
        assertThat(testA.getResults().get(0).getWaterRightNumber()).isEqualTo(1551L);
        assertThat(testA.getResults().get(0).getBasin()).isEqualTo("40E");
        assertThat(testA.getResults().get(0).getExt()).isEqualTo("00");
        assertThat(testA.getResults().get(0).getWaterRightTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testA.getResults().get(0).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testA.getResults().get(0).getVersion()).isEqualTo(1L);
        assertThat(testA.getResults().get(24).getWaterRightId()).isEqualTo(31565L);
        assertThat(testA.getResults().get(24).getWaterRightNumber()).isEqualTo(12727L);
        assertThat(testA.getResults().get(24).getBasin()).isEqualTo("40E");
        assertThat(testA.getResults().get(24).getExt()).isEqualTo("00");
        assertThat(testA.getResults().get(24).getWaterRightTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testA.getResults().get(24).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testA.getResults().get(24).getVersion()).isEqualTo(2L);

        sort = "?sortDirection=ASC&sortColumn=WATERRIGHTNUMBER&pageNumber=120";
        result = mockMvc.perform(get("/api/v1/water-rights/" + basin + "/eligible-water-rights" + sort)
                .header("Authorization", "Bearer " + token)
                .param("basin", basin)
                .param("waterNumber", waterNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        EligibleWaterRightVersionPageDto testB = convertTo(result, EligibleWaterRightVersionPageDto.class);
        assertThat(testB.getResults().get(0).getWaterRightId()).isEqualTo(268967L);
        assertThat(testB.getResults().get(0).getWaterRightNumber()).isEqualTo(189344L);
        assertThat(testB.getResults().get(0).getBasin()).isEqualTo("40E");
        assertThat(testB.getResults().get(0).getExt()).isEqualTo("00");
        assertThat(testB.getResults().get(0).getWaterRightTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testB.getResults().get(0).getWaterRightStatusDescription()).isEqualTo("DISMISSED");
        assertThat(testB.getResults().get(0).getVersion()).isEqualTo(1L);
        assertThat(testB.getResults().get(11).getWaterRightId()).isEqualTo(269357L);
        assertThat(testB.getResults().get(11).getWaterRightNumber()).isEqualTo(189795L);
        assertThat(testB.getResults().get(11).getBasin()).isEqualTo("40E");
        assertThat(testB.getResults().get(11).getExt()).isEqualTo("00");
        assertThat(testB.getResults().get(11).getWaterRightTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testB.getResults().get(11).getWaterRightStatusDescription()).isEqualTo("ACTIVE");
        assertThat(testB.getResults().get(11).getVersion()).isEqualTo(2L);

    }

}
