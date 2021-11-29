package gov.mt.wris;


import gov.mt.wris.constants.Constants;
import gov.mt.wris.dtos.ElementDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.ObjectionCreationDto;
import gov.mt.wris.dtos.ObjectionsSearchResultDto;
import gov.mt.wris.dtos.ObjectionsSearchResultPageDto;
import gov.mt.wris.dtos.ObjectorDto;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class ObjectionsIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSearchCases() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String objectionId, objectionType, filedDate, objectionLate, objectionStatus, basin;
        float sec;
        String sort = "";

        // OBJECTIONID, OBJECTIONTYPEDESCRIPTION, FILEDDATE, OBJECTIONSTATUSDESCRIPTION, COMPLETEBASIN, COMPLETEWATERRIGHTNUMBER
        sort = "?sortDirection=ASC&sortColumn=OBJECTIONID";
        objectionId = "84%";
        objectionType = "%";
        filedDate = "%";
        objectionLate = "%";
        objectionStatus = "%";
        basin = "%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/objections" + sort)
                .header("Authorization", "Bearer " + token)
                .param("objectionId", objectionId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsSearchResultPageDto testA = convertTo(result, ObjectionsSearchResultPageDto.class);
        assertThat(testA.getResults().get(0).getObjectionId()).isEqualTo(84L);
        assertThat(testA.getResults().get(0).getObjectionType()).isEqualTo("OTA");
        assertThat(testA.getResults().get(0).getObjectionTypeDescription()).isEqualTo("OBJECTION TO APPLICATION");
        assertThat(testA.getResults().get(0).getFiledDate()).isEqualTo("2002-03-29");
        assertThat(testA.getResults().get(0).getObjectionStatus()).isNull();
        assertThat(testA.getResults().get(0).getObjectionStatusDescription()).isNull();
        assertThat(testA.getResults().get(0).getBasin()).isNull();
        assertThat(testA.getResults().get(0).getCompleteBasin()).isNull();
        assertThat(testA.getResults().get(0).getCompleteWaterRightNumber()).isNull();
        assertThat(testA.getResults().get(24).getObjectionId()).isEqualTo(8413L);
        assertThat(testA.getResults().get(24).getObjectionType()).isEqualTo("OTW");
        assertThat(testA.getResults().get(24).getObjectionTypeDescription()).isEqualTo("OBJECTION TO WATER RIGHT");
        assertThat(testA.getResults().get(24).getFiledDate()).isEqualTo("1985-11-18");
        assertThat(testA.getResults().get(24).getObjectionStatus()).isNull();
        assertThat(testA.getResults().get(24).getObjectionStatusDescription()).isNull();
        assertThat(testA.getResults().get(24).getBasin()).isNull();
        assertThat(testA.getResults().get(24).getCompleteBasin()).isNull();
        assertThat(testA.getResults().get(24).getCompleteWaterRightNumber()).isEqualTo("40N 76129 00");

        sort = "?sortDirection=ASC&sortColumn=OBJECTIONID&pageNumber=39";
        objectionId = "84%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/objections" + sort)
                .header("Authorization", "Bearer " + token)
                .param("objectionId", objectionId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsSearchResultPageDto testB = convertTo(result, ObjectionsSearchResultPageDto.class);
        assertThat(testB.getResults().get(0).getObjectionId()).isEqualTo(84976L);
        assertThat(testB.getResults().get(0).getObjectionType()).isEqualTo("OTW");
        assertThat(testB.getResults().get(0).getObjectionTypeDescription()).isEqualTo("OBJECTION TO WATER RIGHT");
        assertThat(testB.getResults().get(0).getFiledDate()).isEqualTo("2019-02-11");
        assertThat(testB.getResults().get(0).getObjectionStatus()).isEqualTo("OPEN");
        assertThat(testB.getResults().get(0).getObjectionStatusDescription()).isEqualTo("OPEN");
        assertThat(testB.getResults().get(0).getBasin()).isEqualTo("41G");
        assertThat(testB.getResults().get(0).getCompleteBasin()).isEqualTo("41G PRELIMINARY 2018-02-15");
        assertThat(testB.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("41G 211896 00");
        assertThat(testB.getResults().get(23).getObjectionId()).isEqualTo(84999L);
        assertThat(testB.getResults().get(23).getObjectionType()).isEqualTo("OTW");
        assertThat(testB.getResults().get(23).getObjectionTypeDescription()).isEqualTo("OBJECTION TO WATER RIGHT");
        assertThat(testB.getResults().get(23).getFiledDate()).isEqualTo("2019-02-11");
        assertThat(testB.getResults().get(23).getObjectionStatus()).isEqualTo("CLSD");
        assertThat(testB.getResults().get(23).getObjectionStatusDescription()).isEqualTo("CLOSED");
        assertThat(testB.getResults().get(23).getBasin()).isEqualTo("41G");
        assertThat(testB.getResults().get(23).getCompleteBasin()).isEqualTo("41G PRELIMINARY 2018-02-15");
        assertThat(testB.getResults().get(23).getCompleteWaterRightNumber()).isEqualTo("41G 95725 00");

        sort = "?sortDirection=ASC&sortColumn=OBJECTIONID&pageNumber=1";
        objectionId = "%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/objections" + sort)
                .header("Authorization", "Bearer " + token)
                .param("objectionId", objectionId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsSearchResultPageDto testC = convertTo(result, ObjectionsSearchResultPageDto.class);
        assertThat(testC.getResults().get(0).getObjectionId()).isEqualTo(6L);
        assertThat(testC.getResults().get(0).getObjectionType()).isEqualTo("OTA");
        assertThat(testC.getResults().get(0).getObjectionTypeDescription()).isEqualTo("OBJECTION TO APPLICATION");
        assertThat(testC.getResults().get(0).getFiledDate()).isEqualTo("2002-02-27");
        assertThat(testC.getResults().get(0).getObjectionStatus()).isEqualTo("CLSD");
        assertThat(testC.getResults().get(0).getObjectionStatusDescription()).isEqualTo("CLOSED");
        assertThat(testC.getResults().get(0).getBasin()).isNull();
        assertThat(testC.getResults().get(0).getCompleteBasin()).isNull();
        assertThat(testC.getResults().get(0).getCompleteWaterRightNumber()).isNull();
        assertThat(testC.getResults().get(24).getObjectionId()).isEqualTo(32L);
        assertThat(testC.getResults().get(24).getObjectionType()).isEqualTo("OTA");
        assertThat(testC.getResults().get(24).getObjectionTypeDescription()).isEqualTo("OBJECTION TO APPLICATION");
        assertThat(testC.getResults().get(24).getFiledDate()).isEqualTo("2002-03-04");
        assertThat(testC.getResults().get(24).getObjectionStatus()).isNull();
        assertThat(testC.getResults().get(24).getObjectionStatusDescription()).isNull();
        assertThat(testC.getResults().get(24).getBasin()).isNull();
        assertThat(testC.getResults().get(24).getCompleteBasin()).isNull();
        assertThat(testC.getResults().get(24).getCompleteWaterRightNumber()).isNull();

        sort = "?sortDirection=ASC&sortColumn=OBJECTIONID&pageNumber=3521";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/objections" + sort)
                .header("Authorization", "Bearer " + token)
                .param("objectionId", objectionId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsSearchResultPageDto testD = convertTo(result, ObjectionsSearchResultPageDto.class);
        assertThat(testD.getResults().get(0).getObjectionId()).isEqualTo(92200L);
        assertThat(testD.getResults().get(0).getObjectionType()).isEqualTo("COB");
        assertThat(testD.getResults().get(0).getObjectionTypeDescription()).isEqualTo("COUNTER OBJECTION");
        assertThat(testD.getResults().get(0).getFiledDate()).isEqualTo("2021-11-09");
        assertThat(testD.getResults().get(0).getObjectionStatus()).isNull();
        assertThat(testD.getResults().get(0).getObjectionStatusDescription()).isNull();
        assertThat(testD.getResults().get(0).getBasin()).isNull();
        assertThat(testD.getResults().get(0).getCompleteBasin()).isNull();
        assertThat(testD.getResults().get(0).getCompleteWaterRightNumber()).isEqualTo("42J 1809 00");
        assertThat(testD.getResults().get(20).getObjectionId()).isEqualTo(92221L);
        assertThat(testD.getResults().get(20).getObjectionType()).isEqualTo("OTD");
        assertThat(testD.getResults().get(20).getObjectionTypeDescription()).isEqualTo("OBJECTION TO DECREE");
        assertThat(testD.getResults().get(20).getFiledDate()).isEqualTo("2021-11-18");
        assertThat(testD.getResults().get(20).getObjectionStatus()).isEqualTo("OPEN");
        assertThat(testD.getResults().get(20).getObjectionStatusDescription()).isEqualTo("OPEN");
        assertThat(testD.getResults().get(20).getBasin()).isEqualTo("39G");
        assertThat(testD.getResults().get(20).getCompleteBasin()).isEqualTo("39G PRELIMINARY 2018-12-19");
        assertThat(testD.getResults().get(20).getCompleteWaterRightNumber()).isNull();
    }

    @Test
    public void testCreateObjections() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end, waterRightId, versionId, decreeId, applicationId;
        float sec;

        /**
         * OBJECTIONS_TYPE_COUNTER_OBJECTION
         * OBJECTIONS_TYPE_INTENT_TO_APPEAR
         * OBJECTIONS_TYPE_OBJECTION_TO_RIGHT
         **/
        waterRightId = 4896;
        versionId = 1;
        decreeId = 131;
        LocalDate filedDate1 = LocalDate.of(2021, 11, 9);
        ObjectionCreationDto ocd1 = new ObjectionCreationDto();
        ocd1.setObjectionType(Constants.OBJECTIONS_TYPE_COUNTER_OBJECTION);
        ocd1.setFiledDate(filedDate1);
        ocd1.setWaterRightId(waterRightId);
        ocd1.setVersionId(versionId);
        ocd1.setDecreeId(decreeId);
        List<ObjectorDto> objectors1 = new ArrayList<>();
        ObjectorDto obj1 = new ObjectorDto();
        obj1.setContactId(1645L);
        obj1.setEndDate(LocalDate.of(2022, 11, 9));
        obj1.setRepresentativeCount(1L);
        objectors1.add(obj1);
        ocd1.setObjectors(objectors1);

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/objections")
                .content(getJson(ocd1))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsSearchResultDto test1 = convertTo(result, ObjectionsSearchResultDto.class);
        assertThat(test1.getObjectionId()).isGreaterThan(92200L);
        assertThat(test1.getObjectionType()).isEqualTo(ocd1.getObjectionType());
        assertThat(test1.getFiledDate()).isEqualTo(ocd1.getFiledDate());
        assertThat(test1.getObjectionStatus()).isEqualTo(Constants.OBJECTIONS_STATUS_DEFAULT);

        /**
         * OBJECTIONS_TYPE_ON_MOTION
         **/
        waterRightId = 4809;
        versionId = 1;
        decreeId = 125;
        LocalDate filedDate2 = LocalDate.of(2021, 11, 9);
        ObjectionCreationDto ocd2 = new ObjectionCreationDto();
        ocd2.setObjectionType(Constants.OBJECTIONS_TYPE_ON_MOTION);
        ocd2.setFiledDate(filedDate2);
        ocd2.setWaterRightId(waterRightId);
        ocd2.setVersionId(versionId);
        ocd2.setDecreeId(decreeId);
        List<ElementDto> elements2 = new ArrayList<>();
        ElementDto elm2 = new ElementDto();
        elm2.setElementType("VOL");
        elm2.setElementObjectionRemark("TESTING OBJECTION ELEMENTS");
        elements2.add(elm2);
        ocd2.setElements(elements2);

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/objections")
                .content(getJson(ocd2))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsSearchResultDto test2 = convertTo(result, ObjectionsSearchResultDto.class);
        assertThat(test2.getObjectionId()).isGreaterThan(92200L);
        assertThat(test2.getObjectionType()).isEqualTo(ocd2.getObjectionType());
        assertThat(test2.getFiledDate()).isEqualTo(ocd2.getFiledDate());
        assertThat(test2.getObjectionStatus()).isEqualTo(Constants.OBJECTIONS_STATUS_DEFAULT);

        /**
         * OBJECTIONS_TYPE_OBJECTION_TO_DECREE
         **/
        decreeId = 135;
        LocalDate filedDate3 = LocalDate.of(2021, 11, 9);
        ObjectionCreationDto ocd3 = new ObjectionCreationDto();
        ocd3.setObjectionType(Constants.OBJECTIONS_TYPE_OBJECTION_TO_DECREE);
        ocd3.setFiledDate(filedDate3);
        ocd3.setDecreeId(decreeId);
        List<ElementDto> elements3 = new ArrayList<>();
        ElementDto elm3 = new ElementDto();
        elm3.setElementType("VOL");
        elm3.setElementObjectionRemark("TESTING OBJECTION ELEMENTS #2");
        elements3.add(elm3);
        ocd3.setElements(elements3);
        List<ObjectorDto> objectors3 = new ArrayList<>();
        ObjectorDto obj3 = new ObjectorDto();
        obj3.setContactId(1650L);
        obj3.setEndDate(LocalDate.of(2022, 11, 9));
        obj3.setRepresentativeCount(1L);
        objectors3.add(obj3);
        ocd3.setObjectors(objectors3);

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/objections")
                .content(getJson(ocd3))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsSearchResultDto test3 = convertTo(result, ObjectionsSearchResultDto.class);
        assertThat(test3.getObjectionId()).isGreaterThan(92200L);
        assertThat(test3.getObjectionType()).isEqualTo(ocd3.getObjectionType());
        assertThat(test3.getFiledDate()).isEqualTo(ocd3.getFiledDate());
        assertThat(test3.getObjectionStatus()).isEqualTo(Constants.OBJECTIONS_STATUS_DEFAULT);

        /**
         * OBJECTIONS_TYPE_OBJECTION_TO_APPLICATION
         **/
        applicationId = 8847399;
        LocalDate filedDate4 = LocalDate.of(2021, 11, 9);
        ObjectionCreationDto ocd4 = new ObjectionCreationDto();
        ocd4.setObjectionType(Constants.OBJECTIONS_TYPE_OBJECTION_TO_APPLICATION);
        ocd4.setFiledDate(filedDate4);
        ocd4.setApplicationId(applicationId);
        List<ObjectorDto> objectors4 = new ArrayList<>();
        ObjectorDto obj4 = new ObjectorDto();
        obj4.setContactId(1646L);
        obj4.setEndDate(LocalDate.of(2022, 11, 9));
        obj4.setRepresentativeCount(1L);
        objectors4.add(obj4);
        ocd4.setObjectors(objectors4);

        start = System.currentTimeMillis();
        result = mockMvc.perform(post("/api/v1/objections")
                .content(getJson(ocd4))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        ObjectionsSearchResultDto test4 = convertTo(result, ObjectionsSearchResultDto.class);
        assertThat(test4.getObjectionId()).isGreaterThan(92200L);
        assertThat(test4.getObjectionType()).isEqualTo(ocd4.getObjectionType());
        assertThat(test4.getFiledDate()).isEqualTo(ocd4.getFiledDate());
        assertThat(test4.getObjectionStatus()).isEqualTo(Constants.OBJECTIONS_STATUS_DEFAULT);

    }

}
