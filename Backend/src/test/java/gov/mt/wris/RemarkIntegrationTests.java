package gov.mt.wris;

import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.RemarkCodeSearchPageDto;
import gov.mt.wris.dtos.VersionCreationDto;
import gov.mt.wris.dtos.VersionDetailDto;
import gov.mt.wris.dtos.VersionDto;
import gov.mt.wris.dtos.VersionUpdateDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesDto;
import gov.mt.wris.dtos.WaterRightVersionApplicationReferencesPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectionsElementsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectionsPageDto;
import gov.mt.wris.dtos.WaterRightVersionObjectorsPageDto;
import gov.mt.wris.dtos.WaterRightVersionPageDto;
import gov.mt.wris.services.RemarkCodeService;
import gov.mt.wris.services.WaterRightVersionService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class RemarkIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RemarkCodeService remarkCodeService;

    @Test
    @Rollback
    public void testSearchRemarkCodes() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String remarkCode, waterRightId;
        float sec;

        remarkCode = "C1%";
        waterRightId = "28029";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/remarks/all")
                .header("Authorization", "Bearer " + token)
                .param("remarkCode", remarkCode)
                .param("waterRightId", waterRightId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        RemarkCodeSearchPageDto testA = convertTo(result, RemarkCodeSearchPageDto.class);
        assertThat(testA.getResults().get(0).getRemarkCode()).isEqualTo("C119");
        assertThat(testA.getResults().get(0).getRemarkCategoryDescription()).isEqualTo("CONVEYANCE FACILITY INFORMATION");
        assertThat(testA.getResults().get(0).getRemarkTypeDescription()).isEqualTo("INFORMATION");
        assertThat(testA.getResults().get(0).getElementTypeDescription()).isEqualTo("POINT OF DIVERSION");

    }


}
