package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.WaterRightCreationDto;
import gov.mt.wris.dtos.WaterRightDto;
import gov.mt.wris.dtos.WaterRightGeocodeDto;
import gov.mt.wris.dtos.WaterRightGeocodeNewDto;
import gov.mt.wris.dtos.WaterRightGeocodePageDto;
import gov.mt.wris.dtos.WaterRightGeocodesCreationDto;
import gov.mt.wris.services.WaterRightService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WaterRightGeocodeIntegrationTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMVC;

    @Autowired
    WaterRightService service;

    @Test
    public void testCreateGeocode() throws Exception {
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

        Long id = createdWaterRight.getWaterRightId();

        WaterRightGeocodesCreationDto newGeocodes = new WaterRightGeocodesCreationDto();
        WaterRightGeocodeNewDto newGeo = new WaterRightGeocodeNewDto();
        String geocodeId = "99999999999999999";
        newGeo.setGeocodeId(geocodeId);
        LocalDate newDate = LocalDate.of(2021, 01, 01);
        newGeo.setBeginDate(newDate);
        newGeocodes.setNewGeocodes(Arrays.asList(newGeo));

        // basic create
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/geocodes")
                                .content(getJson(newGeocodes))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/geocodes")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        WaterRightGeocodePageDto createdGeocodes = convertTo(result, WaterRightGeocodePageDto.class);
        
        assertThat(createdGeocodes.getAllSevered()).isFalse();
        assertThat(createdGeocodes.getAllUnresolved()).isFalse();
        assertThat(createdGeocodes.getResults().size()).isEqualTo(1);
        assertThat(createdGeocodes.getResults().get(0).getBeginDate()).isEqualTo(newDate);
        assertThat(createdGeocodes.getResults().get(0).getGeocodeId()).isEqualTo(geocodeId);

        // basic unresolve button
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/geocodes/unresolve")
                                .content(getJson(newGeocodes))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/geocodes")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        createdGeocodes = convertTo(result, WaterRightGeocodePageDto.class);
        
        assertThat(createdGeocodes.getAllSevered()).isFalse();
        assertThat(createdGeocodes.getAllUnresolved()).isTrue();

        // sever button
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/geocodes/sever")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/geocodes")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        createdGeocodes = convertTo(result, WaterRightGeocodePageDto.class);
        
        assertThat(createdGeocodes.getAllSevered()).isTrue();
        assertThat(createdGeocodes.getAllUnresolved()).isTrue();

        // edit Geocode
        WaterRightGeocodeDto updateDto = createdGeocodes.getResults().get(0);
        LocalDate updateDate = LocalDate.of(2021, 02, 02);
        updateDto.setEndDate(updateDate);
        updateDto.setSever(false);
        updateDto.setUnresolved(false);
        updateDto.setComments("Hello World");

        result = mockMVC.perform(put("/api/v1/water-rights/" + id + "/geocodes/" + updateDto.getXrefId())
                                .content(getJson(updateDto))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andReturn();
        
        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/geocodes")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        createdGeocodes = convertTo(result, WaterRightGeocodePageDto.class);

        assertThat(createdGeocodes.getAllSevered()).isFalse();
        assertThat(createdGeocodes.getAllUnresolved()).isFalse();
        assertThat(createdGeocodes.getResults().get(0).getEndDate()).isEqualTo(updateDate);
        assertThat(createdGeocodes.getResults().get(0).getComments()).isEqualTo("Hello World");

        // delete Geocode
        result = mockMVC.perform(delete("/api/v1/water-rights/" + id + "/geocodes/" + updateDto.getXrefId())
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();
        
        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/geocodes")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        createdGeocodes = convertTo(result, WaterRightGeocodePageDto.class);

        assertThat(createdGeocodes.getResults().size()).isEqualTo(0);

        service.deleteWaterRight(id);
    }

    @Test
    public void testDeleteAll() throws Exception {
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

        Long id = createdWaterRight.getWaterRightId();

        WaterRightGeocodesCreationDto newGeocodes = new WaterRightGeocodesCreationDto();
        WaterRightGeocodeNewDto newGeo = new WaterRightGeocodeNewDto();
        String geocodeId = "99999999999999999";
        newGeo.setGeocodeId(geocodeId);
        LocalDate newDate = LocalDate.of(2021, 01, 01);
        newGeo.setBeginDate(newDate);
        WaterRightGeocodeNewDto newGeo2 = new WaterRightGeocodeNewDto();
        String geocodeId2 = "88888888888999999";
        newGeo2.setGeocodeId(geocodeId2);
        newGeo2.setBeginDate(newDate);
        newGeocodes.setNewGeocodes(Arrays.asList(newGeo, newGeo2));

        // basic create
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/geocodes")
                                .content(getJson(newGeocodes))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/geocodes")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        WaterRightGeocodePageDto createdGeocodes = convertTo(result, WaterRightGeocodePageDto.class);
        
        assertThat(createdGeocodes.getAllSevered()).isFalse();
        assertThat(createdGeocodes.getAllUnresolved()).isFalse();
        assertThat(createdGeocodes.getResults().size()).isEqualTo(2);
        assertThat(createdGeocodes.getResults().get(0).getBeginDate()).isEqualTo(newDate);
        assertThat(createdGeocodes.getResults().get(0).getGeocodeId()).isEqualTo(geocodeId);
        assertThat(createdGeocodes.getResults().get(1).getBeginDate()).isEqualTo(newDate);
        assertThat(createdGeocodes.getResults().get(1).getGeocodeId()).isEqualTo(geocodeId2);

        // delete all Geocodes
        result = mockMVC.perform(delete("/api/v1/water-rights/" + id + "/geocodes")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();
        
        result = mockMVC.perform(get("/api/v1/water-rights/" + id + "/geocodes")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        createdGeocodes = convertTo(result, WaterRightGeocodePageDto.class);

        assertThat(createdGeocodes.getResults().size()).isEqualTo(0);

        service.deleteWaterRight(id);
    }

    @Test
    public void testDoubleCreate() throws Exception {
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

        Long id = createdWaterRight.getWaterRightId();

        WaterRightGeocodesCreationDto newGeocodes = new WaterRightGeocodesCreationDto();
        WaterRightGeocodeNewDto newGeo = new WaterRightGeocodeNewDto();
        String geocodeId = "99999999999999999";
        newGeo.setGeocodeId(geocodeId);
        LocalDate newDate = LocalDate.of(2021, 01, 01);
        newGeo.setBeginDate(newDate);
        WaterRightGeocodeNewDto newGeo2 = new WaterRightGeocodeNewDto();
        newGeo2.setGeocodeId(geocodeId);
        newGeo2.setBeginDate(newDate);
        newGeocodes.setNewGeocodes(Arrays.asList(newGeo, newGeo2));

        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/geocodes")
                                .content(getJson(newGeocodes))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("The following Geocodes were entered at least twice: 99-9999-99-9-99-99-9999");
        
        service.deleteWaterRight(id);
    }

    @Test
    public void testDuplicateCreate() throws Exception {
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

        Long id = createdWaterRight.getWaterRightId();

        WaterRightGeocodesCreationDto newGeocodes = new WaterRightGeocodesCreationDto();
        WaterRightGeocodeNewDto newGeo = new WaterRightGeocodeNewDto();
        String geocodeId = "99999999999999999";
        newGeo.setGeocodeId(geocodeId);
        LocalDate newDate = LocalDate.of(2021, 01, 01);
        newGeo.setBeginDate(newDate);
        newGeocodes.setNewGeocodes(Arrays.asList(newGeo));

        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/geocodes")
                                .content(getJson(newGeocodes))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isNoContent())
                                .andReturn();

        newGeocodes = new WaterRightGeocodesCreationDto();
        newGeo = new WaterRightGeocodeNewDto();
        newGeo.setGeocodeId(geocodeId);
        newGeo.setBeginDate(newDate);
        newGeocodes.setNewGeocodes(Arrays.asList(newGeo));
        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/geocodes")
                                .content(getJson(newGeocodes))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("The following Geocodes already exist: 99-9999-99-9-99-99-9999");
        
        service.deleteWaterRight(id);
    }

    @Test
    public void testBadGeocode() throws Exception {
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

        Long id = createdWaterRight.getWaterRightId();

        WaterRightGeocodesCreationDto newGeocodes = new WaterRightGeocodesCreationDto();
        WaterRightGeocodeNewDto newGeo = new WaterRightGeocodeNewDto();
        String geocodeId = "99999999999999";
        newGeo.setGeocodeId(geocodeId);
        LocalDate newDate = LocalDate.of(2021, 01, 01);
        newGeo.setBeginDate(newDate);
        newGeocodes.setNewGeocodes(Arrays.asList(newGeo));

        result = mockMVC.perform(post("/api/v1/water-rights/" + id + "/geocodes")
                                .content(getJson(newGeocodes))
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                                .andExpect(status().isBadRequest())
                                .andReturn();

        service.deleteWaterRight(id);
    }
}
