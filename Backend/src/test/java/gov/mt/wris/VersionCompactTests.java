package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import gov.mt.wris.dtos.VersionCompactDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class VersionCompactTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateCompact() throws Exception {
        String token = getAccessToken();

        VersionCompactDto creationDto = new VersionCompactDto()
            .allocation(false)
            .blm(false)
            .exemptCompact(true)
            .subcompactId(6L);
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/487495/versions/1/compacts")
                                    .content(getJson(creationDto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();
        
        VersionCompactDto dto = convertTo(result, VersionCompactDto.class);

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getAllocation()).isFalse();
        assertThat(dto.getBlm()).isFalse();
        assertThat(dto.getExemptCompact()).isTrue();

        dto.allocation(true);
        dto.blm(true);
        dto.exemptCompact(false);

        result = mockMvc.perform(put("/api/v1/water-rights/487495/versions/1/compacts/" + dto.getId())
                                    .content(getJson(dto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isOk())
                                    .andReturn();

        dto = convertTo(result, VersionCompactDto.class);

        assertThat(dto.getAllocation()).isTrue();
        assertThat(dto.getBlm()).isTrue();
        assertThat(dto.getExemptCompact()).isFalse();

        mockMvc.perform(delete("/api/v1/water-rights/487495/versions/1/compacts/" + dto.getId())
                                    .content(getJson(dto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isNoContent());
    }

    @Test
    public void testPreventDuplicates() throws Exception {
        String token = getAccessToken();

        VersionCompactDto creationDto = new VersionCompactDto()
            .allocation(false)
            .blm(false)
            .exemptCompact(true)
            .subcompactId(6L);
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/487495/versions/1/compacts")
                                    .content(getJson(creationDto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isCreated())
                                    .andReturn();

        VersionCompactDto dto = convertTo(result, VersionCompactDto.class);

        result = mockMvc.perform(post("/api/v1/water-rights/487495/versions/1/compacts")
                                    .content(getJson(creationDto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isBadRequest())
                                    .andReturn();
        
        Message message = convertTo(result, Message.class);

        assertThat(message.getUserMessage()).isEqualTo("Cannot add the same Compact to a Water Right Version twice.");

        mockMvc.perform(delete("/api/v1/water-rights/487495/versions/1/compacts/" + dto.getId())
                                    .content(getJson(dto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isNoContent());
    }

    @Test
    public void testAllocationAndBLM() throws Exception {
        String token = getAccessToken();

        VersionCompactDto creationDto = new VersionCompactDto()
            .allocation(true)
            .blm(false)
            .exemptCompact(true)
            .subcompactId(6L);
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/487495/versions/1/compacts")
                                    .content(getJson(creationDto))
                                    .header("Authorization", "Bearer " + token)
                                    .contentType("application/json"))
                                    .andExpect(status().isBadRequest())
                                    .andReturn();
        
        Message dto = convertTo(result, Message.class);

        assertThat(dto.getUserMessage()).isEqualTo("When Exempt From Compact is checked, Version Affects Allocation and BLM Transbasin must be unchecked.");
    }
}
