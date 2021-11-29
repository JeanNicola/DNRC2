package gov.mt.wris;

import gov.mt.wris.dtos.CaseCreationDto;
import gov.mt.wris.dtos.CaseDto;
import gov.mt.wris.dtos.CaseSearchResultPageDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.SearchBasinsResultPageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class DecreesIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSearchBasins() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String basin;
        float sec;
        String sort = "";

        // BASIN, DECREEID, DCTPCODEDESCRIPTION, ISSUEDATE
        sort = "?sortDirection=ASC&sortColumn=BASIN";
        basin = "%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/decrees/basins" + sort)
                .header("Authorization", "Bearer " + token)
                .param("basin", basin)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SearchBasinsResultPageDto testA = convertTo(result, SearchBasinsResultPageDto.class);
        assertThat(testA.getResults().get(0).getBasin()).isEqualTo("38H");
        assertThat(testA.getResults().get(0).getDecreeId()).isEqualTo(1);
        assertThat(testA.getResults().get(0).getDctpCode()).isEqualTo("FINL");
        assertThat(testA.getResults().get(0).getDctpCodeDescription()).isEqualTo("FINAL");
        assertThat(testA.getResults().get(0).getIssueDate()).isEqualTo("1984-03-27");
        assertThat(testA.getResults().get(24).getBasin()).isEqualTo("40M");
        assertThat(testA.getResults().get(24).getDecreeId()).isEqualTo(99);
        assertThat(testA.getResults().get(24).getDctpCode()).isEqualTo("PRLM");
        assertThat(testA.getResults().get(24).getDctpCodeDescription()).isEqualTo("PRELIMINARY");
        assertThat(testA.getResults().get(24).getIssueDate()).isEqualTo("2009-03-20");

        // BASIN, DECREEID, DCTPCODEDESCRIPTION, ISSUEDATE
        sort = "?sortDirection=ASC&sortColumn=BASIN&pageNumber=5";
        basin = "%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/decrees/basins" + sort)
                .header("Authorization", "Bearer " + token)
                .param("basin", basin)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SearchBasinsResultPageDto testB = convertTo(result, SearchBasinsResultPageDto.class);
        assertThat(testB.getResults().get(0).getBasin()).isEqualTo("BRMC");
        assertThat(testB.getResults().get(0).getDecreeId()).isEqualTo(120);
        assertThat(testB.getResults().get(0).getDctpCode()).isEqualTo("PRLM");
        assertThat(testB.getResults().get(0).getDctpCodeDescription()).isEqualTo("PRELIMINARY");
        assertThat(testB.getResults().get(0).getIssueDate()).isEqualTo("2011-09-30");
        assertThat(testB.getResults().get(10).getBasin()).isEqualTo("USFS");
        assertThat(testB.getResults().get(10).getDecreeId()).isEqualTo(102);
        assertThat(testB.getResults().get(10).getDctpCode()).isEqualTo("PRLM");
        assertThat(testB.getResults().get(10).getDctpCodeDescription()).isEqualTo("PRELIMINARY");
        assertThat(testB.getResults().get(10).getIssueDate()).isEqualTo("2008-05-19");

    }

}
