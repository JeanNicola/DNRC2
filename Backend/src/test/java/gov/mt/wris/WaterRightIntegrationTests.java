package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.mt.wris.dtos.PeriodOfUseCreationDto;
import gov.mt.wris.dtos.PlaceOfUseCreationDto;
import gov.mt.wris.dtos.PurposeDetailDto;
import gov.mt.wris.dtos.WaterRightUpdateDto;
import gov.mt.wris.dtos.WaterRightVersionPurposeCreationDto;
import gov.mt.wris.dtos.WaterRightViewDto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.WaterRightCreationDto;
import gov.mt.wris.dtos.WaterRightDto;
import gov.mt.wris.services.WaterRightService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class WaterRightIntegrationTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMVC;

    @Autowired
    private WaterRightService waterService;

    @Test
    public void testCreateWaterRight() throws Exception {
        String token = getAccessToken();

        WaterRightCreationDto newWaterRight = new WaterRightCreationDto();
        newWaterRight.setTypeCode("STOC");
        newWaterRight.setBasin("38H");

        MvcResult result = mockMVC.perform(post("/api/v1/water-rights")
                                    .content(getJson(newWaterRight))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        WaterRightDto createdWaterRight = convertTo(result, WaterRightDto.class);

        assertThat(createdWaterRight.getWaterRightId()).isNotNull();

        waterService.deleteWaterRight(createdWaterRight.getWaterRightId().longValue());
    }

    @Test
    public void testChangeWaterRight() throws Exception {

        String token = getAccessToken();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        long start, end;
        float sec;

        start = System.currentTimeMillis();
        WaterRightCreationDto nwr = new WaterRightCreationDto();
        nwr.setTypeCode("STOC");
        nwr.setBasin("38H");
        MvcResult result = mockMVC.perform(post("/api/v1/water-rights")
                .content(getJson(nwr))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** CREATE ELAPSED TIME: " + sec + " seconds ****");
        WaterRightDto test1 = convertTo(result, WaterRightDto.class);
        assertThat(test1.getWaterRightId()).isNotNull();

        long wtr_resv = 178100;
        String dist_no = "YE-0102";
        LocalDateTime con_dt = LocalDateTime.parse("22-02-2021", dtf);
        start = System.currentTimeMillis();
        WaterRightUpdateDto uwr = new WaterRightUpdateDto();
        uwr.setDividedOwnership(true);
        uwr.setWaterReservationId(wtr_resv);
        uwr.setConservationDistrictNumber(dist_no);
        uwr.setConservationDistrictDate(con_dt);
        uwr.setSevered(false);
        uwr.setBasin("38H");
        uwr.setTypeCode("STOC");
        result = mockMVC.perform(put("/api/v1/water-rights/" + test1.getWaterRightId())
                .content(getJson(uwr))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** PUT ELAPSED TIME: " + sec + " seconds ****");
        WaterRightViewDto test2 = convertTo(result, WaterRightViewDto.class);
        assertThat(test2.getWaterRightId()).isEqualTo(test1.getWaterRightId());
        assertThat(test2.getDividedOwnership()).isEqualTo(uwr.getDividedOwnership());
        assertThat(test2.getWaterReservationId()).isEqualTo(uwr.getWaterReservationId());
        assertThat(test2.getConservationDistrictNumber()).isEqualTo(uwr.getConservationDistrictNumber());
        assertThat(test2.getConservationDistrictDate()).isEqualTo(uwr.getConservationDistrictDate());

        waterService.deleteWaterRight(test1.getWaterRightId().longValue());
    }

    @Test
    public void testCreateWaterRightVersionPurpose() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        float sec;
        DateTimeFormatter fmat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        Long waterRightId = 317527L;
        Long versionId = 1L;

        List<PlaceOfUseCreationDto> places = new ArrayList<>();
        PlaceOfUseCreationDto place1 = new PlaceOfUseCreationDto();
        place1.setElementOrigin("CLAI");
        place1.setCountyId(5L);
        place1.setLegalId(557707L);
        place1.setAcreage(new BigDecimal(100));
        places.add(place1);

        List<PeriodOfUseCreationDto> periods = new ArrayList<>();
        PeriodOfUseCreationDto period1 = new PeriodOfUseCreationDto();
        period1.setElementOrigin("CLAI");
        period1.setBeginDate(LocalDate.parse("08-10-2021", fmat));
        period1.setEndDate(LocalDate.parse("08-10-2022", fmat));
        period1.setWaterRightId(waterRightId);
        period1.setVersionId(versionId);
        periods.add(period1);

        WaterRightVersionPurposeCreationDto purpose = new WaterRightVersionPurposeCreationDto();
        purpose.setPurposeCode("IR");
        //purpose.setIrrigationCode("");
        purpose.setHousehold(1);
        purpose.setClimaticCode("4");
        purpose.setPurposeOrigin("CLAI");
        purpose.setPlacesOfUse(places);
        purpose.setPeriodsOfUse(periods);

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
        assertThat(testA.getPurposeId()).isGreaterThan(0);

    }

}
