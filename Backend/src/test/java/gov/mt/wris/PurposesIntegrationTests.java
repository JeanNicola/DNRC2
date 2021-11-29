package gov.mt.wris;

import gov.mt.wris.dtos.*;
import gov.mt.wris.models.PeriodOfUse;
import gov.mt.wris.models.PurposeVolumeCalculation;
import gov.mt.wris.repositories.PeriodOfUseRepository;
import gov.mt.wris.services.PurposeService;
import gov.mt.wris.services.PurposeVolumeCalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class PurposesIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurposeService purposeService;

    @Autowired
    PeriodOfUseRepository periodOfUseRepository;

    @Autowired
    private PurposeVolumeCalculationService calculationService;

    static List<String> required = new ArrayList<>(Arrays.asList(
            "Township is missing",
            "Range is missing",
            "County Id is missing",
            "Range Direction is missing",
            "Township Direction is missing",
            "Section is missing"));

    @Test
    public void testSearchWaterRightsVersions() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String waterRightNumber, basin, waterRightType;
        float sec;
        String sort = "";

        sort = "?sortDirection=ASC&sortColumn=COMPLETEWATERRIGHTNUMBER";
        basin = "%";
        waterRightNumber = "%";
        waterRightType = "%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes" + sort)
                .header("Authorization", "Bearer " + token)
                .param("basin", basin)
                .param("waterRightNumber", waterRightNumber)
                .param("waterRightType", waterRightType)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PurposesSearchPageDto testA = convertTo(result, PurposesSearchPageDto.class);
        assertThat(testA.getResults().get(0).getPurposeId()).isEqualTo(1230);
        assertThat(testA.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("38H 355 09");
        assertThat(testA.getResults().get(0).getWaterRightTypeCode()).isEqualTo("PRDL");
        assertThat(testA.getResults().get(0).getWaterRightTypeDescription()).isEqualTo("POWDER RIVER DECLARATION");
        assertThat(testA.getResults().get(0).getCompleteWaterRightVersion()).isEqualTo("CHANGE AUTHORIZATION (SPLIT) 1 TERMINATED");
        assertThat(testA.getResults().get(0).getPurposeCode()).isEqualTo("IR");
        assertThat(testA.getResults().get(0).getPurposeDescription()).isEqualTo("IRRIGATION");
        assertThat(testA.getResults().get(0).getElementOrigin()).isEqualTo("ISSU");
        assertThat(testA.getResults().get(0).getElementOriginDescription()).isEqualTo("AS ISSUED");
        assertThat(testA.getResults().get(24).getPurposeId()).isEqualTo(49419);
        assertThat(testA.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("38H 17656 00");
        assertThat(testA.getResults().get(24).getWaterRightTypeCode()).isEqualTo("STOC");
        assertThat(testA.getResults().get(24).getWaterRightTypeDescription()).isEqualTo("STATEMENT OF CLAIM");
        assertThat(testA.getResults().get(24).getCompleteWaterRightVersion()).isEqualTo("ORIGINAL RIGHT 1 ACTIVE");
        assertThat(testA.getResults().get(24).getPurposeCode()).isEqualTo("ST");
        assertThat(testA.getResults().get(24).getPurposeDescription()).isEqualTo("STOCK");
        assertThat(testA.getResults().get(24).getElementOrigin()).isEqualTo("DECR");
        assertThat(testA.getResults().get(24).getElementOriginDescription()).isEqualTo("DECREED");

    }

    @Test
    public void testGetPurposeSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, purposeId;
        float sec;

        purposeId = 54894;
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PurposeDetailDto testA = convertTo(result, PurposeDetailDto.class);
        assertThat(testA.getPurposeCode()).isEqualTo("IR");
        assertThat(testA.getPurposeCodeDescription()).isEqualTo("IRRIGATION");
        assertThat(testA.getCompleteWaterRightNumber()).isEqualTo("43D 20221 00");

    }

    @Test
    public void testRetiredPlaceOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, purposeId;
        float sec;

        purposeId = 668617;

        PlaceOfUseCreationDto newRetPou = new PlaceOfUseCreationDto();
        LegalLandCreationDto newLLD = new LegalLandCreationDto();

        // Create Legal Land Description
        newLLD.setDescription160("NE");
        newLLD.setDescription320("SW");
        newLLD.setSection(2L);
        newLLD.setTownship(10L);
        newLLD.setTownshipDirection("N");
        newLLD.setRange(1L);
        newLLD.setRangeDirection("W");
        newLLD.setCountyId(29L);

        // Create Retired Place Of Use
        newRetPou.setLegalLand(newLLD);
        newRetPou.setAcreage(new BigDecimal(2));
        newRetPou.setElementOrigin("MRLE");

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/retired-places")
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlacesOfUsePageDto testA = convertTo(result, PlacesOfUsePageDto.class);
        assertThat(testA.getResults().size()).isGreaterThan(0);

        Integer currentRetPouSize = testA.getResults().size();

        // Test POST
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/retired-places?sort=false")
                .content(getJson(newRetPou))
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlaceOfUseDto testB = convertTo(result, PlaceOfUseDto.class);
        assertThat(testB.getAcreage()).isEqualTo(new BigDecimal(2));

        newRetPou.setAcreage(new BigDecimal(3));

        // Test PUT
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/purposes/" + purposeId + "/retired-places/" + testB.getPlaceId())
                .header("Authorization", "Bearer " + token)
                .content(getJson(newRetPou))
                .param("purposeId", String.valueOf(purposeId))
                .param("retiredPlaceId", String.valueOf(testB.getPlaceId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlaceOfUseDto testC = convertTo(result, PlaceOfUseDto.class);
        assertThat(testC.getAcreage()).isEqualTo(new BigDecimal(3));

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/retired-places")
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlacesOfUsePageDto testD = convertTo(result, PlacesOfUsePageDto.class);
        assertThat(testD.getResults().size()).isEqualTo(currentRetPouSize + 1);

        // Test DELETE
        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/purposes/" + purposeId + "/retired-places/" + testB.getPlaceId())
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .param("retiredPlaceId", String.valueOf(testB.getPlaceId()))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/retired-places")
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlacesOfUsePageDto testE = convertTo(result, PlacesOfUsePageDto.class);
        assertThat(testE.getResults().size()).isEqualTo(currentRetPouSize);
    }

    @Test
    public void testRetPouSubdivisions() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, purposeId;
        float sec;
        String code = "04ALP";
        String dnrcName = "ALPINE LAKESHORE TRACTS";

        purposeId = 668617;

        PlaceOfUseCreationDto newRetPou = new PlaceOfUseCreationDto();
        LegalLandCreationDto newLLD = new LegalLandCreationDto();

        // Create Legal Land Description
        newLLD.setDescription160("NE");
        newLLD.setDescription320("SW");
        newLLD.setSection(2L);
        newLLD.setTownship(10L);
        newLLD.setTownshipDirection("N");
        newLLD.setRange(1L);
        newLLD.setRangeDirection("W");
        newLLD.setCountyId(29L);

        // Create Retired Place Of Use
        newRetPou.setLegalLand(newLLD);
        newRetPou.setAcreage(new BigDecimal(2));
        newRetPou.setElementOrigin("MRLE");

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/retired-places?sort=false")
                .content(getJson(newRetPou))
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlaceOfUseDto retiredPlaceOfUse = convertTo(result, PlaceOfUseDto.class);

        // Create Subdivision
        SubdivisionCreationDto newSubdivision = new SubdivisionCreationDto();

        newSubdivision.setBlk("Block");
        newSubdivision.setLot("Lot");
        newSubdivision.setCode(code);

        // Test POST
        start = System.currentTimeMillis();
        mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/retired-places/" + retiredPlaceOfUse.getPlaceId() + "/subdivisions")
                .content(getJson(newSubdivision))
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .param("retiredPlaceId", String.valueOf(retiredPlaceOfUse.getPlaceId()))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/retired-places/" + retiredPlaceOfUse.getPlaceId() + "/subdivisions")
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .param("retiredPlaceId", String.valueOf(retiredPlaceOfUse.getPlaceId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SubdivisionPageDto testA = convertTo(result, SubdivisionPageDto.class);
        assertThat(testA.getResults().size()).isGreaterThan(0);
        assertThat(testA.getResults().get(0).getCode()).isEqualTo(code);
        assertThat(testA.getResults().get(0).getDnrcName()).isEqualTo(dnrcName);

        // Test PUT
        String newBlockValue = "BLOCK 2";
        newSubdivision.setCode(null);
        newSubdivision.setBlk(newBlockValue);

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/purposes/" + purposeId + "/retired-places/" + retiredPlaceOfUse.getPlaceId() + "/subdivisions/" + code)
                .header("Authorization", "Bearer " + token)
                .content(getJson(newSubdivision))
                .param("purposeId", String.valueOf(purposeId))
                .param("retiredPlaceId", String.valueOf(retiredPlaceOfUse.getPlaceId()))
                .param("code", code)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SubdivisionDto testB = convertTo(result, SubdivisionDto.class);
        assertThat(testB.getBlk()).isEqualTo(newBlockValue);

        // Test DELETE
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/purposes/" + purposeId + "/retired-places/" + retiredPlaceOfUse.getPlaceId() + "/subdivisions/" + code)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .param("retiredPlaceId", String.valueOf(retiredPlaceOfUse.getPlaceId()))
                .param("code", code)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Test GET
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/retired-places/" + retiredPlaceOfUse.getPlaceId() + "/subdivisions")
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .param("retiredPlaceId", String.valueOf(retiredPlaceOfUse.getPlaceId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SubdivisionPageDto testC = convertTo(result, SubdivisionPageDto.class);
        assertThat(testC.getResults().size()).isEqualTo(0);

        // Delete Retired Place Of Use
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/purposes/" + purposeId + "/retired-places/" + testB.getPlaceId())
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .param("retiredPlaceId", String.valueOf(testB.getPlaceId()))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testUpdatePurpose() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        DateTimeFormatter fmat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        Long purposeId = 317527L;

        PurposeUpdateDto purpose = new PurposeUpdateDto();
        purpose.setPurposeCode("IR");
        //purpose.setIrrigationCode("");
        purpose.setHousehold(1);
        purpose.setClimaticCode("4");
        purpose.setPurposeOrigin("CLAI");

        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/purposes/"+purposeId)
                .header("Authorization", "Bearer " + token)
                .content(getJson(purpose))
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PurposeDetailDto testA = convertTo(result, PurposeDetailDto.class);
        assertThat(testA.getPurposeCode()).isEqualTo("IR");
        assertThat(testA.getPurposeCodeDescription()).isEqualTo("IRRIGATION");
        assertThat(testA.getCompleteWaterRightNumber()).isEqualTo("42M 163824 00");

    }

    @Test
    public void testPurposeVolumeCalculationService() throws Exception {

        String token = getAccessToken();
        String purposeType, clarCode;
        int household;
        BigDecimal purposeId = new BigDecimal(668787L);
        BigDecimal animalUnits = new BigDecimal(1L);
        List<PeriodOfUse> periods =  periodOfUseRepository.findDistinctAllByPurposeId(purposeId);

        purposeType = "DM";
        clarCode = "";
        household = 1;
        PurposeVolumeCalculation test1 = calculationService.calculateVolume(
                purposeId,
                purposeType,
                household,
                clarCode,
                animalUnits,
                periods);
        assertThat(test1.getMessages().size()).isEqualTo(0);
        assertThat(test1.getVolume()).isEqualTo("0.09");

        purposeType = "MD";
        clarCode = "";
        household = 4;
        PurposeVolumeCalculation test2 = calculationService.calculateVolume(
                purposeId,
                purposeType,
                household,
                clarCode,
                animalUnits,
                periods);
        assertThat(test2.getMessages().size()).isEqualTo(0);
        assertThat(test2.getVolume()).isEqualTo("0.36");

        purposeType = "LG";
        clarCode = "";
        household = 4;
        PurposeVolumeCalculation test3 = calculationService.calculateVolume(
                purposeId,
                purposeType,
                household,
                clarCode,
                animalUnits,
                periods);
        assertThat(test3.getMessages().size()).isEqualTo(0);
        assertThat(test3.getVolume()).isEqualTo("25.00");

        purposeType = "IR";
        clarCode = "3";
        household = 1;
        PurposeVolumeCalculation test4 = calculationService.calculateVolume(
                purposeId,
                purposeType,
                household,
                clarCode,
                animalUnits,
                periods);
        assertThat(test4.getMessages().size()).isEqualTo(0);
        assertThat(test4.getVolume()).isEqualTo("18.00");

    }

    @Test
    public void testGetPlacesOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String purposeId;
        float sec;
        String sort = "";

        purposeId = "668757";
        sort = "?sortDirection=ASC&sortColumn=MODIFIEDBYTHISCHANGEDESCRIPTION";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/places" + sort)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", purposeId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlacesOfUsePageDto testA = convertTo(result, PlacesOfUsePageDto.class);
        assertThat(testA.getResults().get(0).getPurposeId()).isEqualTo(668757);
        assertThat(testA.getResults().get(0).getElementOrigin()).isEqualTo("CLAI");
        assertThat(testA.getResults().get(0).getElementOriginDescription()).isEqualTo("CLAIMED");
        assertThat(testA.getResults().get(0).getPlaceId()).isEqualTo(1);
        assertThat(testA.getResults().get(0).getLegalId()).isEqualTo(557707);
        assertThat(testA.getResults().get(0).getCompleteLegalLandDescription()).isEqualTo("6 5 N 40 E BEAVERHEAD MT");
        assertThat(testA.getResults().get(0).getModifiedByThisChange()).isEqualTo("Y");
        assertThat(testA.getResults().get(0).getModifiedByThisChangeDescription()).isEqualTo("YES");

    }

    @Test
    public void testCreatePlaceOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId;
        float sec;

        LegalLandCreationDto cl_A = new LegalLandCreationDto();
        cl_A.setDescription40("NE");
        cl_A.setDescription160("SE");
        cl_A.setGovernmentLot(3L);

        PlaceOfUseCreationDto cp_A = new PlaceOfUseCreationDto();
        cp_A.setAcreage(new BigDecimal(100));
        cp_A.setCountyId(49L);
        cp_A.setElementOrigin("CLAI");
        cp_A.setLegalLand(cl_A);
        cp_A.setLegalId(547291L);
        cp_A.setModifiedByThisChange("Y");

        /* Missing section, township, township direction, range, range direction and county */
        purposeId = 668755L;
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/places")
                .content(getJson(cp_A))
                .param("purposeId", String.valueOf(purposeId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        Message message = convertTo(result, Message.class);
        assertThat(required.stream().anyMatch(message.getUserMessage()::contains)).isEqualTo(true);

        LegalLandCreationDto cl_B = new LegalLandCreationDto();
        cl_B.setCountyId(49L);
        cl_B.setDescription40("NE");
        cl_B.setDescription160("SE");
        cl_B.setTownship(25L);
        cl_B.setTownshipDirection("N");
        cl_B.setRange(32L);
        cl_B.setRangeDirection("W");
        cl_B.setSection(1L);

        PlaceOfUseCreationDto cp_B = new PlaceOfUseCreationDto();
        cp_B.setAcreage(new BigDecimal(100));
        cp_B.setCountyId(49L);
        cp_B.setElementOrigin("CLAI");
        cp_B.setLegalLand(cl_A);
        cp_B.setLegalId(547291L);
        cp_B.setModifiedByThisChange("Y");

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/places")
                .content(getJson(cp_B))
                .param("purposeId", String.valueOf(purposeId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlaceOfUseDto newPer = convertTo(result, PlaceOfUseDto.class);
        assertThat(newPer.getPurposeId()).isEqualTo(668755L);
        assertThat(newPer.getElementOrigin()).isEqualTo("CLAI");

    }

    @Test
    public void testDeletePlaceOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId, placeId;
        float sec;

        purposeId = 2715L;
        placeId = 1L;
        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/purposes/" + purposeId + "/places/" + placeId)
                .param("purposeId", String.valueOf(purposeId))
                .param("placeId", String.valueOf(placeId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testGetPeriodOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String purposeId;
        float sec;
        String sort = "";

        purposeId = "668757";
        sort = "?sortDirection=ASC&sortColumn=BEGINDATE";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/periods" + sort)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", purposeId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PeriodsOfUsePageDto testA = convertTo(result, PeriodsOfUsePageDto.class);
        assertThat(testA.getResults().get(0).getPurposeId()).isEqualTo(668757);
        assertThat(testA.getResults().get(0).getPeriodId()).isEqualTo(1821382);
        assertThat(testA.getResults().get(0).getElementOrigin()).isEqualTo("CLAI");
        assertThat(testA.getResults().get(0).getElementOriginDescription()).isEqualTo("CLAIMED");
        assertThat(testA.getResults().get(0).getWaterRightId()).isEqualTo(317527);
        assertThat(testA.getResults().get(0).getVersionId()).isEqualTo(1);

        purposeId = "612752";
        sort = "?sortDirection=ASC&sortColumn=LEASEYEAR";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/periods" + sort)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", purposeId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PeriodsOfUsePageDto testC = convertTo(result, PeriodsOfUsePageDto.class);
        assertThat(testC.getResults().get(0).getPeriodId()).isEqualTo(1651455);
        assertThat(testC.getResults().get(0).getElementOrigin()).isEqualTo("ISSU");
        assertThat(testC.getResults().get(0).getElementOriginDescription()).isEqualTo("AS ISSUED");
        assertThat(testC.getResults().get(0).getWaterRightId()).isNull();
        assertThat(testC.getResults().get(0).getVersionId()).isNull();
        assertThat(testC.getResults().get(0).getLeaseYear()).isEqualTo("1ST");
        assertThat(testC.getResults().get(1).getPeriodId()).isEqualTo(1651456);
        assertThat(testC.getResults().get(1).getElementOrigin()).isEqualTo("ISSU");
        assertThat(testC.getResults().get(1).getElementOriginDescription()).isEqualTo("AS ISSUED");
        assertThat(testC.getResults().get(1).getWaterRightId()).isNull();
        assertThat(testC.getResults().get(1).getVersionId()).isNull();
        assertThat(testC.getResults().get(1).getLeaseYear()).isEqualTo("1ST");
        assertThat(testC.getResults().get(2).getPeriodId()).isEqualTo(1651457);
        assertThat(testC.getResults().get(2).getElementOrigin()).isEqualTo("ISSU");
        assertThat(testC.getResults().get(2).getElementOriginDescription()).isEqualTo("AS ISSUED");
        assertThat(testC.getResults().get(2).getWaterRightId()).isNull();
        assertThat(testC.getResults().get(2).getVersionId()).isNull();
        assertThat(testC.getResults().get(2).getLeaseYear()).isEqualTo("2ND");
        assertThat(testC.getResults().get(3).getPeriodId()).isEqualTo(1651458);
        assertThat(testC.getResults().get(3).getElementOrigin()).isEqualTo("ISSU");
        assertThat(testC.getResults().get(3).getElementOriginDescription()).isEqualTo("AS ISSUED");
        assertThat(testC.getResults().get(3).getWaterRightId()).isNull();
        assertThat(testC.getResults().get(3).getVersionId()).isNull();
        assertThat(testC.getResults().get(3).getLeaseYear()).isEqualTo("2ND");

    }

    @Test
    public void testGetSubdivisionsForPlaceOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId, placeId;
        float sec;
        String sort = "";

        purposeId = 51623L;
        placeId = 1L;
        sort = "?sortDirection=ASC&sortColumn=BLK";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/places/" + placeId + "/subdivisions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .param("placeId", String.valueOf(placeId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SubdivisionPageDto testA = convertTo(result, SubdivisionPageDto.class);
        assertThat(testA.getResults().get(0).getPurposeId()).isEqualTo(51623);
        assertThat(testA.getResults().get(0).getCode()).isEqualTo("03PRI");
        assertThat(testA.getResults().get(0).getDorName()).isEqualTo("PRIMROSE SUBD");
        assertThat(testA.getResults().get(0).getDnrcName()).isEqualTo("PRIMROSE SUBD");
        assertThat(testA.getResults().get(0).getBlk()).isEqualTo("5");
        assertThat(testA.getResults().get(0).getLot()).isEqualTo("17");

        purposeId = 668955L;
        placeId = 1L;
        sort = "?sortDirection=ASC&sortColumn=DNRCNAME";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId + "/places/" + placeId + "/subdivisions" + sort)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .param("placeId", String.valueOf(placeId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SubdivisionPageDto testB = convertTo(result, SubdivisionPageDto.class);
        assertThat(testB.getResults().get(0).getPurposeId()).isEqualTo(668955);
        assertThat(testB.getResults().get(0).getCode()).isEqualTo("03RBR");
        assertThat(testB.getResults().get(0).getDorName()).isEqualTo(" RAINBOW RIDGE SUBDY");
        assertThat(testB.getResults().get(0).getDnrcName()).isEqualTo(" RAINBOW RIDGE SUBD");
        assertThat(testB.getResults().get(1).getPurposeId()).isEqualTo(668955);
        assertThat(testB.getResults().get(1).getCode()).isEqualTo("03AAW");
        assertThat(testB.getResults().get(1).getDorName()).isEqualTo("A AND W SUBD");
        assertThat(testB.getResults().get(1).getDnrcName()).isEqualTo("A AND W SUBD");

    }

    @Test
    public void testCreateSubdivisionForPlaceOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId, placeId;
        float sec;

        SubdivisionCreationDto create = new SubdivisionCreationDto();
        create.setBlk("9");
        create.setLot("1");
        create.setCode("03CCA");

        purposeId = 51642L;
        placeId = 1L;
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/places/" + placeId + "/subdivisions")
                .content(getJson(create))
                .param("purposeId", String.valueOf(purposeId))
                .param("placeId", String.valueOf(placeId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SubdivisionDto newSub = convertTo(result, SubdivisionDto.class);
        assertThat(newSub.getBlk()).isEqualTo("9");
        assertThat(newSub.getLot()).isEqualTo("1");

    }

    @Test
    public void testUpdateSubdivisionForPlaceOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId, placeId;
        float sec;
        String code;

        SubdivisionCreationDto update = new SubdivisionCreationDto();
        update.setBlk("14");
        update.setLot("1");

        purposeId = 51642L;
        placeId = 1L;
        code = "03CCA";
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/purposes/" + purposeId + "/places/" + placeId + "/subdivisions/" + code)
                .content(getJson(update))
                .param("purposeId", String.valueOf(purposeId))
                .param("placeId", String.valueOf(placeId))
                .param("code", code)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SubdivisionDto updateSub = convertTo(result, SubdivisionDto.class);
        assertThat(updateSub.getBlk()).isEqualTo("14");
        assertThat(updateSub.getLot()).isEqualTo("1");

    }

    @Test
    public void testDeleteSubdivisionFromPlaceOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId, placeId;
        float sec;
        String code;

        code = "01KIM";
        SubdivisionCreationDto create = new SubdivisionCreationDto();
        create.setBlk("15");
        create.setLot("1");
        create.setCode(code);

        purposeId = 51642L;
        placeId = 1L;
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/places/" + placeId + "/subdivisions")
                .content(getJson(create))
                .param("purposeId", String.valueOf(purposeId))
                .param("placeId", String.valueOf(placeId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SubdivisionDto newSub = convertTo(result, SubdivisionDto.class);

        start = System.currentTimeMillis();
        result = mockMvc.perform(delete("/api/v1/purposes/" + purposeId + "/places/" + placeId + "/subdivisions/" + newSub.getCode())
                .param("purposeId", String.valueOf(purposeId))
                .param("placeId", String.valueOf(placeId))
                .param("code", code)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testCreatePeriodOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId;
        float sec;

        PeriodOfUseCreationDto create = new PeriodOfUseCreationDto();
        create.setWaterRightId(317527L);
        create.setVersionId(1L);
        create.setElementOrigin("CLAI");
        create.setBeginDate(LocalDate.of(2020, 12, 31));
        create.setEndDate(LocalDate.of(2021, 02, 01));

        purposeId = 668755L;
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/purposes/" + purposeId + "/periods")
                .content(getJson(create))
                .param("purposeId", String.valueOf(purposeId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PeriodOfUseDto newPer = convertTo(result, PeriodOfUseDto.class);
        assertThat(newPer.getPurposeId()).isEqualTo(668755L);
        assertThat(newPer.getElementOrigin()).isEqualTo("CLAI");

    }

    @Test
    public void testUpdatePlaceOfUse() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        Long purposeId, placeId;
        float sec;

        LegalLandCreationDto cl = new LegalLandCreationDto();
        cl.setCountyId(49L);
        cl.setDescription40("NE");
        cl.setDescription160("SE");
        //cl.setGovernmentLot(3L);
        cl.setTownship(25L);
        cl.setTownshipDirection("N");
        cl.setRange(32L);
        cl.setRangeDirection("W");
        cl.setSection(1L);

        PlaceOfUseCreationDto up = new PlaceOfUseCreationDto();
        up.setModifiedByThisChange("N");
        up.setElementOrigin("CLAI");
        up.setLegalLand(cl);
        up.setLegalId(547291L);

        purposeId = 668757L;
        placeId = 1L;
        start = System.currentTimeMillis();
        result = mockMvc.perform(put("/api/v1/purposes/" + purposeId + "/places/" + placeId)
                .content(getJson(up))
                .param("purposeId", String.valueOf(purposeId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PlaceOfUseDto newPer = convertTo(result, PlaceOfUseDto.class);
        assertThat(newPer.getPurposeId()).isEqualTo(668755L);
        assertThat(newPer.getElementOrigin()).isEqualTo("CLAI");

    }

    @Test
    public void testCreateExamination() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;

        Long waterRightId = 1859L;
        Long versionId = 1L;

        WaterRightVersionPurposeCreationDto purposeCreationDto = new WaterRightVersionPurposeCreationDto();
        purposeCreationDto.setPurposeCode("IR");
        purposeCreationDto.setClimaticCode("4");
        purposeCreationDto.setPurposeOrigin("CLAI");

        // Create Purpose
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/water-rights/"+waterRightId+"/versions/"+versionId+"/purposes")
                .header("Authorization", "Bearer " + token)
                .content(getJson(purposeCreationDto))
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionId", String.valueOf(versionId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        PurposeDetailDto purpose = convertTo(result, PurposeDetailDto.class);

        long purposeId = purpose.getPurposeId();

        ExaminationCreationDto createDto = new ExaminationCreationDto();

        createDto.setBeginDate(LocalDate.of(2020, 3, 3));
        createDto.setEndDate(LocalDate.of(2020, 4, 4));
        createDto.setDnrcId(237L);

        // Create Examination
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/purposes/"+purposeId+ "/examinations")
                .header("Authorization", "Bearer " + token)
                .content(getJson(createDto))
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        ExaminationDetailDto dto = convertTo(result, ExaminationDetailDto.class);
        assertThat(dto.getBeginDate().toString()).isEqualTo(createDto.getBeginDate().toString());
        assertThat(dto.getEndDate().toString()).isEqualTo(createDto.getEndDate().toString());

        // Delete Purpose
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/purposes/" + purposeId)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

    @Test
    public void testDeletePurpose() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        Long waterRightId = 317527L;
        Long versionId = 1L;

        List<PlaceOfUseCreationDto> places = new ArrayList<>();
        List<PeriodOfUseCreationDto> periods = new ArrayList<>();

        WaterRightVersionPurposeCreationDto purpose = new WaterRightVersionPurposeCreationDto();
        purpose.setPurposeCode("IR");
        purpose.setHousehold(1);
        purpose.setClimaticCode("4");
        purpose.setPurposeOrigin("CLAI");
        purpose.setPlacesOfUse(places);
        purpose.setPeriodsOfUse(periods);

        // Test POST
        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/water-rights/"+waterRightId+"/versions/"+versionId+"/purposes")
                .header("Authorization", "Bearer " + token)
                .content(getJson(purpose))
                .param("waterRightId", String.valueOf(waterRightId))
                .param("versionId", String.valueOf(versionId))
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PurposeDetailDto testA = convertTo(result, PurposeDetailDto.class);

        // Test GET
        long purposeId = testA.getPurposeId();

        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/purposes/" + purposeId)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        PurposeDetailDto testB = convertTo(result, PurposeDetailDto.class);
        assertThat(testB.getPurposeCode()).isEqualTo("IR");
        assertThat(testB.getPurposeCodeDescription()).isEqualTo("IRRIGATION");
        assertThat(testB.getCompleteWaterRightNumber()).isEqualTo("42J 9 00");

        // Test DELETE
        start = System.currentTimeMillis();
        mockMvc.perform(delete("/api/v1/purposes/" + purposeId)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        // Test GET
        start = System.currentTimeMillis();
        mockMvc.perform(get("/api/v1/purposes/" + purposeId)
                .header("Authorization", "Bearer " + token)
                .param("purposeId", String.valueOf(purposeId))
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

    }

}
