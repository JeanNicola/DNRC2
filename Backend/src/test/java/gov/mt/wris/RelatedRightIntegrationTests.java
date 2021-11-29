package gov.mt.wris;

import gov.mt.wris.controllers.RelatedRightsController;
import gov.mt.wris.dtos.*;
import gov.mt.wris.dtos.RelatedRightDto;
import gov.mt.wris.dtos.RelatedRightElementsPageDto;
import gov.mt.wris.dtos.RelatedRightWaterRightPageDto;
import gov.mt.wris.dtos.RelatedRightsPageDto;
import gov.mt.wris.dtos.UpdateRelatedRightDto;
import gov.mt.wris.services.RelatedRightService;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class RelatedRightIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMVC;

    @Autowired
    RelatedRightsController controller;

    @Autowired
    RelatedRightService service;

    @Test
    @Rollback
    public void testSearchRelatedRightsSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        long start, end;
        float sec;

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        RelatedRightsPageDto page = convertTo(result, RelatedRightsPageDto.class);
        assertThat(page.getResults().size()).isGreaterThan(0);
    }

    @Test
    @Rollback
    public void testGetRelatedRightDetails() throws Exception {

        String token = getAccessToken();

        Long relatedRightId = 11250L;
        MvcResult result = null;
        long start, end;
        float sec;

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/" + relatedRightId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        RelatedRightDto dto = convertTo(result, RelatedRightDto.class);
        assertThat(dto.getRelatedRightId()).isEqualTo(relatedRightId);
    }

    @Test
    @Rollback
    public void testGetRelatedRightWaterRights() throws Exception {

        String token = getAccessToken();

        Long relatedRightId = 11250L;
        MvcResult result = null;
        long start, end;
        float sec;

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/" + relatedRightId + "/water-rights")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        RelatedRightWaterRightPageDto page = convertTo(result, RelatedRightWaterRightPageDto.class);
        assertThat(page.getResults().size()).isGreaterThan(0);
    }

    @Test
    @Rollback
    public void testChangeRelatedRight() throws Exception {

        String token = getAccessToken();
        Long relatedRightId = 52903L;
        MvcResult result = null;
        RelatedRightDto dto;
        long start, end;
        float sec;

        String firstType = "SUPL";
        String secondType = "MULT";
        UpdateRelatedRightDto updateRelatedRightDto = new UpdateRelatedRightDto();
        updateRelatedRightDto.relationshipType(firstType);

        /* UPDATE Related Right using firstType */
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/related-rights/" + relatedRightId)
                .content(getJson(updateRelatedRightDto))
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(updateRelatedRightDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        dto = convertTo(result, RelatedRightDto.class);
        assertThat(dto.getRelationshipType()).isEqualTo(firstType);


        /* UPDATE Related Right using secondType */
        updateRelatedRightDto.setRelationshipType(secondType);
        start = System.currentTimeMillis();
        result = mockMVC.perform(put("/api/v1/related-rights/" + relatedRightId)
                .content(getJson(updateRelatedRightDto))
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(updateRelatedRightDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        dto = convertTo(result, RelatedRightDto.class);
        assertThat(dto.getRelationshipType()).isEqualTo(secondType);

    }

    @Test
    @Rollback
    public void testGetRelatedRightElements() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId;
        float sec;

        relatedRightId = 31775L;
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/" + relatedRightId + "/related-elements")
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(relatedRightId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        RelatedRightElementsPageDto testA = convertTo(result, RelatedRightElementsPageDto.class);
        assertThat(testA.getResults().get(0).getRelatedRightId()).isEqualTo(31775);
        assertThat(testA.getResults().get(0).getElementType()).isEqualTo("FLRT");
        assertThat(testA.getResults().get(0).getElementTypeValue()).isEqualTo("FLOW RATE");
        assertThat(testA.getResults().get(1).getElementType()).isEqualTo("MOD");
        assertThat(testA.getResults().get(1).getElementTypeValue()).isEqualTo("MEANS OF DIVERSION");
        assertThat(testA.getResults().get(2).getElementType()).isEqualTo("RESR");
        assertThat(testA.getResults().get(2).getElementTypeValue()).isEqualTo("RESERVOIR");

    }

    @Test
    @Rollback
    public void testCreateRelatedRightElementSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId;
        float sec;

        relatedRightId = 44136L;
        String sharedElement = "IRRT";
        RelatedRightElementCreationDto new_element = new RelatedRightElementCreationDto();
        new_element.setRelatedRightId(relatedRightId);
        new_element.setElementType(sharedElement);

        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/related-rights/" + relatedRightId + "/related-elements")
                .content(getJson(new_element))
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(relatedRightId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        RelatedRightElementsSearchResultDto testA = convertTo(result, RelatedRightElementsSearchResultDto.class);
        assertThat(testA.getRelatedRightId()).isEqualTo(relatedRightId);
        assertThat(testA.getElementType()).isEqualTo(sharedElement);

        service.deleteRelatedRightElement(relatedRightId, sharedElement);

    }

    @Test
    @Rollback
    public void testCreateRelatedRightElementError() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId;
        float sec;

        relatedRightId = 44136L;
        String sharedElement = "WR";
        RelatedRightElementCreationDto new_element = new RelatedRightElementCreationDto();
        new_element.setRelatedRightId(relatedRightId);
        new_element.setElementType(sharedElement);

        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/related-rights/" + relatedRightId + "/related-elements")
                .content(getJson(new_element))
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(relatedRightId))
                .contentType("application/json"))
                .andExpect(status().isConflict())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Related right element for related right id " + relatedRightId + " of type " + sharedElement + " can not be added more than once.");

    }

    @Test
    @Rollback
    public void testDeleteRelatedRight() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId;
        float sec;

        relatedRightId = 33099;
        start = System.currentTimeMillis();
        result = mockMVC.perform(delete("/api/v1/related-rights/" + relatedRightId)
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(relatedRightId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    @Rollback
    public void testDeleteRelatedRightElement() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId;
        float sec;

        relatedRightId = 44136L;
        String sharedElement = "IRRT";
        RelatedRightElementCreationDto new_element = new RelatedRightElementCreationDto();
        new_element.setRelatedRightId(relatedRightId);
        new_element.setElementType(sharedElement);

        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/related-rights/" + relatedRightId + "/related-elements")
                .content(getJson(new_element))
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(relatedRightId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        start = System.currentTimeMillis();
        result = mockMVC.perform(delete("/api/v1/related-rights/" + relatedRightId + "/related-elements/" + sharedElement)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    @Rollback
    public void testCreateAndDeleteWaterRightReferenceToRelatedRightSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId, goodWaterRightId, badWaterRightId, goodVersionId, badVersionId;
        float sec;

        relatedRightId = 44136L;
        goodWaterRightId = 116L;
        goodVersionId = 2L;
        badWaterRightId = 9898989L;
        badVersionId = 9898989L;
        WaterRightReferenceToRelatedRightCreationDto dtoA = new WaterRightReferenceToRelatedRightCreationDto();
        List<WaterRightsReferenceDto> refsA = new ArrayList<>();
        WaterRightsReferenceDto one = new WaterRightsReferenceDto();
        one.setWaterRightId(goodWaterRightId);
        one.setVersionId(goodVersionId);
        refsA.add(one);
        WaterRightsReferenceDto two = new WaterRightsReferenceDto();
        two.setWaterRightId(117L);
        two.setVersionId(1L);
        refsA.add(two);
        dtoA.setWaterRights(refsA);

        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/related-rights/" + relatedRightId + "/water-rights")
                .content(getJson(dtoA))
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(relatedRightId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightReferenceToRelatedRightSearchResultDto testA = convertTo(result, WaterRightReferenceToRelatedRightSearchResultDto.class);
        assertThat(testA.getWaterRights().contains(goodWaterRightId));

        relatedRightId = 9898989L;
        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/related-rights/" + relatedRightId + "/water-rights")
                .content(getJson(dtoA))
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(relatedRightId))
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message testB = convertTo(result, Message.class);
        assertThat(testB.getUserMessage()).isEqualTo("Invalid Related Right Id " + relatedRightId);

        WaterRightReferenceToRelatedRightCreationDto dtoB = new WaterRightReferenceToRelatedRightCreationDto();
        List<WaterRightsReferenceDto> refsB = new ArrayList<>();
        one = new WaterRightsReferenceDto();
        one.setWaterRightId(badWaterRightId);
        one.setVersionId(goodVersionId);
        refsB.add(one);
        two = new WaterRightsReferenceDto();
        two.setWaterRightId(117L);
        two.setVersionId(1L);
        refsB.add(two);
        dtoB.setWaterRights(refsB);
        relatedRightId = 44136L;
        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/related-rights/" + relatedRightId + "/water-rights")
                .content(getJson(dtoB))
                .header("Authorization", "Bearer " + token)
                .param("relatedRightId", String.valueOf(relatedRightId))
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message testC = convertTo(result, Message.class);
        assertThat(testC.getUserMessage()).isEqualTo("Invalid Version or Water Right while creating references to Related Right Id " + relatedRightId);

        start = System.currentTimeMillis();
        result = mockMVC.perform(delete("/api/v1/related-rights/" + relatedRightId + "/water-rights/" + goodWaterRightId + "/" + goodVersionId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        start = System.currentTimeMillis();
        result = mockMVC.perform(delete("/api/v1/related-rights/" + relatedRightId + "/water-rights/" + two.getWaterRightId() + "/" + two.getVersionId())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    @Rollback
    public void testSearchWaterRightsVersions() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId;
        String waterRightNumber, basin, ext;
        float sec;

        relatedRightId = 44136L;
        waterRightNumber = "67%";
        basin = "42%";
        ext = "00";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/" + relatedRightId + "/water-rights-versions")
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .param("basin", basin)
                .param("ext", ext)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightsVersionsPageDto testA = convertTo(result, WaterRightsVersionsPageDto.class);
        assertThat(testA.getResults().get(0).getBasin()).isEqualTo("42B");
        assertThat(testA.getResults().get(0).getWaterRightNumber()).isEqualTo(67171);
        assertThat(testA.getResults().get(0).getVersionId()).isEqualTo(1);
        assertThat(testA.getResults().get(0).getWaterRightId()).isEqualTo(124852);

        waterRightNumber = "670";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/" + relatedRightId + "/water-rights-versions")
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightsVersionsPageDto testB = convertTo(result, WaterRightsVersionsPageDto.class);
        assertThat(testB.getResults().get(0).getBasin()).isEqualTo("41U");
        assertThat(testB.getResults().get(0).getWaterRightNumber()).isEqualTo(670);
        assertThat(testB.getResults().get(0).getVersionId()).isEqualTo(1);
        assertThat(testB.getResults().get(0).getWaterRightId()).isEqualTo(1879);
        assertThat(testB.getResults().get(0).getTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testB.getResults().get(0).getVersionType()).isEqualTo("ORIG");

    }

    @Test
    @Rollback
    public void testCreateRelatedRight() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId, goodWaterRightId, goodVersionId;
        float sec;
        String type;

        goodWaterRightId = 116L;
        goodVersionId = 2L;
        type = "ASSO";
        RelatedRightCreationDto dtoA = new RelatedRightCreationDto();
        dtoA.setRelationshipType(type);
        List<WaterRightsReferenceDto> refsA = new ArrayList<>();
        WaterRightsReferenceDto one = new WaterRightsReferenceDto();
        one.setWaterRightId(goodWaterRightId);
        one.setVersionId(goodVersionId);
        refsA.add(one);
        WaterRightsReferenceDto two = new WaterRightsReferenceDto();
        two.setWaterRightId(117L);
        two.setVersionId(1L);
        refsA.add(two);
        dtoA.setWaterRights(refsA);

        start = System.currentTimeMillis();
        result = mockMVC.perform(post("/api/v1/related-rights/")
                .content(getJson(dtoA))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        RelatedRightCreationResultDto testA = convertTo(result, RelatedRightCreationResultDto.class);
        assertThat(testA.getWaterRights().contains(goodWaterRightId));

        relatedRightId = testA.getRelatedRightId();

        start = System.currentTimeMillis();
        result = mockMVC.perform(delete("/api/v1/related-rights/" + relatedRightId + "/water-rights/" + goodWaterRightId + "/" + goodVersionId)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        start = System.currentTimeMillis();
        result = mockMVC.perform(delete("/api/v1/related-rights/" + relatedRightId + "/water-rights/" + two.getWaterRightId() + "/" + two.getVersionId())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
    }

    @Test
    public void testSearchWaterRightsVersionsSort() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, relatedRightId;
        String waterRightNumber, basin, ext;
        float sec;

        relatedRightId = 44136L;
        waterRightNumber = "670";
        String sortA = "?sortDirection=ASC&sortColumn=TYPEDESCRIPTION";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/" + relatedRightId + "/water-rights-versions" + sortA)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightsVersionsPageDto testA = convertTo(result, WaterRightsVersionsPageDto.class);
        assertThat(testA.getResults().get(0).getTypeDescription()).isEqualTo("GROUND WATER CERTIFICATE");
        assertThat(testA.getResults().get(1).getTypeDescription()).isEqualTo("POWDER RIVER DECLARATION");
        assertThat(testA.getResults().get(2).getTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testA.getResults().get(3).getTypeDescription()).isEqualTo("STATEMENT OF CLAIM");

        String sortB = "?sortDirection=DESC&sortColumn=TYPEDESCRIPTION";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/" + relatedRightId + "/water-rights-versions" + sortB)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightsVersionsPageDto testB = convertTo(result, WaterRightsVersionsPageDto.class);
        assertThat(testB.getResults().get(0).getTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testB.getResults().get(1).getTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testB.getResults().get(2).getTypeDescription()).isEqualTo("POWDER RIVER DECLARATION");
        assertThat(testB.getResults().get(3).getTypeDescription()).isEqualTo("GROUND WATER CERTIFICATE");

        String sortC = "?sortDirection=DESC&sortColumn=COMPLETEWATERRIGHTNUMBER";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/" + relatedRightId + "/water-rights-versions" + sortB)
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightsVersionsPageDto testC = convertTo(result, WaterRightsVersionsPageDto.class);
        assertThat(testC.getResults().get(0).getTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testC.getResults().get(1).getTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testC.getResults().get(2).getTypeDescription()).isEqualTo("POWDER RIVER DECLARATION");
        assertThat(testC.getResults().get(3).getTypeDescription()).isEqualTo("GROUND WATER CERTIFICATE");

    }

    @Test
    @Rollback
    public void testSearchWaterRightsVersionsAll() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String waterRightNumber, basin, ext;
        float sec;

        waterRightNumber = "67%";
        basin = "42%";
        ext = "00";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/water-rights-versions")
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .param("basin", basin)
                .param("ext", ext)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightsVersionsPageDto testA = convertTo(result, WaterRightsVersionsPageDto.class);
        assertThat(testA.getResults().get(0).getBasin()).isEqualTo("42B");
        assertThat(testA.getResults().get(0).getWaterRightNumber()).isEqualTo(67171);
        assertThat(testA.getResults().get(0).getVersionId()).isEqualTo(1);
        assertThat(testA.getResults().get(0).getWaterRightId()).isEqualTo(124852);

        waterRightNumber = "670";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/related-rights/water-rights-versions")
                .header("Authorization", "Bearer " + token)
                .param("waterRightNumber", waterRightNumber)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        WaterRightsVersionsPageDto testB = convertTo(result, WaterRightsVersionsPageDto.class);
        assertThat(testB.getResults().get(0).getBasin()).isEqualTo("41U");
        assertThat(testB.getResults().get(0).getWaterRightNumber()).isEqualTo(670);
        assertThat(testB.getResults().get(0).getVersionId()).isEqualTo(1);
        assertThat(testB.getResults().get(0).getWaterRightId()).isEqualTo(1879);
        assertThat(testB.getResults().get(0).getTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testB.getResults().get(0).getVersionType()).isEqualTo("ORIG");

    }

}
