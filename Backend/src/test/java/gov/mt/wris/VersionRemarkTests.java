package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.RemarkDto;
import gov.mt.wris.dtos.RemarkUpdateDto;
import gov.mt.wris.dtos.RemarkVariableDto;
import gov.mt.wris.dtos.RemarkVariableUpdateDto;
import gov.mt.wris.dtos.RemarkVariablesPageDto;
import gov.mt.wris.dtos.VersionRemarkCreateDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class VersionRemarkTests extends BaseTestCase {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateRemark() throws Exception {
        String token = getAccessToken();

        VersionRemarkCreateDto createDto = new VersionRemarkCreateDto()
            .remarkCode("P272")
            .addedDate(LocalDate.of(2021, 1, 1));
        
        MvcResult result = mockMvc.perform(post("/api/v1/water-rights/487470/versions/1/remarks")
                            .content(getJson(createDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isCreated())
                            .andReturn();
            
        RemarkDto dto = convertTo(result, RemarkDto.class);

        assertThat(dto.getRemarkId()).isNotNull();
        
        result = mockMvc.perform(get("/api/v1/remarks/" + dto.getRemarkId() + "/variables")
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        
        RemarkVariablesPageDto variableDtos = convertTo(result, RemarkVariablesPageDto.class);

        assertThat(variableDtos.getResults().size()).isEqualTo(3);

        Long dataId = variableDtos.getResults().get(0).getDataId();

        LocalDate updateDate = LocalDate.of(2021, 2, 1);
        RemarkUpdateDto updateDto = new RemarkUpdateDto().addedDate(updateDate);

        result = mockMvc.perform(put("/api/v1/remarks/" + dto.getRemarkId())
                            .content(getJson(updateDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        
        dto = convertTo(result, RemarkDto.class);

        assertThat(dto.getAddedDate()).isEqualTo(updateDate);

        RemarkVariableUpdateDto variableUpdateDto = new RemarkVariableUpdateDto().variableText("TESTING");

        result = mockMvc.perform(put("/api/v1/remarks/" + dto.getRemarkId() + "/variables/" + dataId)
                            .content(getJson(variableUpdateDto))
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn();
        
        RemarkVariableDto variableDto = convertTo(result, RemarkVariableDto.class);

        assertThat(variableDto.getVariableText()).isEqualTo("TESTING");
        
        mockMvc.perform(delete("/api/v1/remarks/" + dto.getRemarkId())
                            .header("Authorization", "Bearer " + token)
                            .contentType("application/json"))
                            .andExpect(status().isNoContent())
                            .andReturn();
    }
}
