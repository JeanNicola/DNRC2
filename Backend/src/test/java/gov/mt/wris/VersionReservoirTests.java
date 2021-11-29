package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.ReservoirCreationDto;
import gov.mt.wris.dtos.WaterRightVersionReservoirsDto;
import gov.mt.wris.dtos.WaterRightVersionReservoirsPageDto;
import gov.mt.wris.services.VersionReservoirService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class VersionReservoirTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VersionReservoirService reservoirService;

    @Test
    public void testCreateReservoirWithNewLegalLandDescription() throws Exception {
        String token = getAccessToken();

        ReservoirCreationDto creationDto = new ReservoirCreationDto()
            .podId(960781L)
            .reservoirName("TESTING")
            .reservoirTypeCode("ON")
            .governmentLot(2L)
            .section(1L)
            .countyId(9L)
            .township(3L)
            .townshipDirection("S")
            .range(23L)
            .rangeDirection("E")
            .reservoirOriginCode("ISSU");
        
        mockMvc.perform(post("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();

        MvcResult result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        
        WaterRightVersionReservoirsPageDto pageDto = convertTo(result, WaterRightVersionReservoirsPageDto.class);

        List<WaterRightVersionReservoirsDto> matchingReservoirs = pageDto.getResults().stream().filter(res -> "TESTING".equals(res.getReservoirName())).collect(Collectors.toList());

        assertThat(matchingReservoirs.size()).isEqualTo(1);

        WaterRightVersionReservoirsDto dto = matchingReservoirs.get(0);

        assertThat(dto.getReservoirId()).isNotNull();
        assertThat(dto.getCompleteLegalLandDescription()).isEqualTo("Govt Lot 2 1 3 S 23 E CARBON MT");

        reservoirService.deleteReservoir(486971L, 1L, dto.getReservoirId());
    }

    @Test
    public void testCreateReservoirWithOldLegalLandDescription() throws Exception {
        String token = getAccessToken();

        ReservoirCreationDto creationDto = new ReservoirCreationDto()
            .podId(960781L)
            .reservoirName("TESTING")
            .countyId(9L)
            .reservoirTypeCode("ON")
            .legalLandDescriptionId(2128053L)
            .reservoirOriginCode("ISSU");

        mockMvc.perform(post("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();

        MvcResult result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        
        WaterRightVersionReservoirsPageDto pageDto = convertTo(result, WaterRightVersionReservoirsPageDto.class);

        List<WaterRightVersionReservoirsDto> matchingReservoirs = pageDto.getResults().stream().filter(res -> "TESTING".equals(res.getReservoirName())).collect(Collectors.toList());

        assertThat(matchingReservoirs.size()).isEqualTo(1);

        WaterRightVersionReservoirsDto dto = matchingReservoirs.get(0);

        assertThat(dto.getReservoirId()).isNotNull();
        assertThat(dto.getCompleteLegalLandDescription()).isEqualTo("E2 E2 SW 1 3 S 23 E CARBON MT");

        reservoirService.deleteReservoir(486971L, 1L, dto.getReservoirId());
    }

    @Test
    public void testBadLegalLandDescription() throws Exception {
        String token = getAccessToken();

        ReservoirCreationDto creationDto = new ReservoirCreationDto()
            .podId(960781L)
            .reservoirName("TESTING")
            .reservoirTypeCode("ON")
            .governmentLot(2L)
            .section(1L)
            .countyId(9L)
            .townshipDirection("S")
            .range(23L)
            .rangeDirection("E")
            .reservoirOriginCode("ISSU");
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isBadRequest())
                                        .andReturn();
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("When setting any Legal Land Description fields, Sec, Twp, N/S, Rge, E/W, County and State are all required.");
    }

    @Test
    public void testBadUpdateLegalLandDescriptionId() throws Exception {
        String token = getAccessToken();

        ReservoirCreationDto creationDto = new ReservoirCreationDto()
            .podId(960781L)
            .reservoirName("TESTING")
            .countyId(9L)
            .reservoirTypeCode("ON")
            .legalLandDescriptionId(2128053L)
            .reservoirOriginCode("ISSU");

        mockMvc.perform(post("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();
        MvcResult result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        WaterRightVersionReservoirsPageDto pageDto = convertTo(result, WaterRightVersionReservoirsPageDto.class);

        List<WaterRightVersionReservoirsDto> matchingReservoirs = pageDto.getResults().stream().filter(res -> "TESTING".equals(res.getReservoirName())).collect(Collectors.toList());

        assertThat(matchingReservoirs.size()).isEqualTo(1);

        WaterRightVersionReservoirsDto dto = matchingReservoirs.get(0);

        assertThat(dto.getReservoirId()).isNotNull();

        creationDto.setLegalLandDescriptionId(1747848L);

        result = mockMvc.perform(put("/api/v1/water-rights/486971/versions/1/reservoirs/" + dto.getReservoirId())
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isBadRequest())
                                        .andReturn();
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("Cannot use the POD ID to change the legal land description");

        reservoirService.deleteReservoir(486971L, 1L, dto.getReservoirId());
    }

    @Test
    public void testBadUpdateDeleteLegalLandDescription() throws Exception {
        String token = getAccessToken();

        ReservoirCreationDto creationDto = new ReservoirCreationDto()
            .podId(960781L)
            .reservoirName("TESTING")
            .countyId(9L)
            .reservoirTypeCode("ON")
            .legalLandDescriptionId(2128053L)
            .reservoirOriginCode("ISSU");

        mockMvc.perform(post("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();
        MvcResult result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        WaterRightVersionReservoirsPageDto pageDto = convertTo(result, WaterRightVersionReservoirsPageDto.class);

        List<WaterRightVersionReservoirsDto> matchingReservoirs = pageDto.getResults().stream().filter(res -> "TESTING".equals(res.getReservoirName())).collect(Collectors.toList());

        assertThat(matchingReservoirs.size()).isEqualTo(1);

        WaterRightVersionReservoirsDto dto = matchingReservoirs.get(0);

        assertThat(dto.getReservoirId()).isNotNull();

        creationDto.setLegalLandDescriptionId(null);

        result = mockMvc.perform(put("/api/v1/water-rights/486971/versions/1/reservoirs/" + dto.getReservoirId())
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isBadRequest())
                                        .andReturn();
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("Cannot delete the Legal Land Description, only updates are allowed");

        reservoirService.deleteReservoir(486971L, 1L, dto.getReservoirId());
    }

    @Test
    public void testReservoirLifecycle() throws Exception {
        String token = getAccessToken();

        ReservoirCreationDto creationDto = new ReservoirCreationDto()
            .podId(960781L)
            .reservoirName("TESTING")
            .countyId(9L)
            .reservoirTypeCode("ON")
            .legalLandDescriptionId(2128053L)
            .reservoirOriginCode("ISSU");

        mockMvc.perform(post("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();
        MvcResult result = mockMvc.perform(get("/api/v1/water-rights/486971/versions/1/reservoirs")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isOk())
                                        .andReturn();
        WaterRightVersionReservoirsPageDto pageDto = convertTo(result, WaterRightVersionReservoirsPageDto.class);

        List<WaterRightVersionReservoirsDto> matchingReservoirs = pageDto.getResults().stream().filter(res -> "TESTING".equals(res.getReservoirName())).collect(Collectors.toList());

        assertThat(matchingReservoirs.size()).isEqualTo(1);

        WaterRightVersionReservoirsDto dto = matchingReservoirs.get(0);

        assertThat(dto.getReservoirId()).isNotNull();

        creationDto.setReservoirOriginCode("AMEN");
        creationDto.setReservoirTypeCode("ON");

        mockMvc.perform(put("/api/v1/water-rights/486971/versions/1/reservoirs/" + dto.getReservoirId())
                                        .content(getJson(creationDto))
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();
        
        mockMvc.perform(delete("/api/v1/water-rights/486971/versions/1/reservoirs/" + dto.getReservoirId())
                                        .header("Authorization", "Bearer " + token)
                                        .contentType("application/json"))
                                        .andExpect(status().isNoContent())
                                        .andReturn();
    }
}
