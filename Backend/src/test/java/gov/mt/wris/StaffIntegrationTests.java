package gov.mt.wris;

import gov.mt.wris.dtos.AllStaffDto;
import gov.mt.wris.dtos.ApplicantsPageDto;
import gov.mt.wris.dtos.CaseCreationDto;
import gov.mt.wris.dtos.CaseDto;
import gov.mt.wris.dtos.CaseRegisterCreateUpdateDto;
import gov.mt.wris.dtos.CaseRegisterDetailDto;
import gov.mt.wris.dtos.CaseRegisterPageDto;
import gov.mt.wris.dtos.CaseSearchResultPageDto;
import gov.mt.wris.dtos.CaseUpdateDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.ObjectionsPageDto;
import gov.mt.wris.dtos.SearchStaffPageDto;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class StaffIntegrationTests extends BaseTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSearchStaff() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String lastName, firstName;
        float sec;
        String sort = "";

        // NAME, STAFFID
        sort = "?sortDirection=ASC&sortColumn=NAME";
        lastName = "JON%";
        firstName = "HON%";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/staff" + sort)
                .header("Authorization", "Bearer " + token)
                .param("lastName", lastName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        SearchStaffPageDto testA = convertTo(result, SearchStaffPageDto.class);
        assertThat(testA.getResults().get(0).getDnrcId()).isEqualTo("280");
        assertThat(testA.getResults().get(0).getCompleteName()).isEqualTo("JONES, CARISSA L");
        assertThat(testA.getResults().get(1).getDnrcId()).isEqualTo("348");
        assertThat(testA.getResults().get(1).getCompleteName()).isEqualTo("JONES, HON. BLAIR");
        assertThat(testA.getResults().get(2).getDnrcId()).isEqualTo("56");
        assertThat(testA.getResults().get(2).getCompleteName()).isEqualTo("JONES, STAN");

    }

    @Test
    public void testGetDistrictCourtStaff() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        long start, end;
        String districtCourt;
        float sec;

        districtCourt = "2";
        start = System.currentTimeMillis();
        result = mockMvc.perform(get("/api/v1/staff/district-court/" + districtCourt)
                .header("Authorization", "Bearer " + token)
                .param("districtCourt", districtCourt)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        AllStaffDto testA = convertTo(result, AllStaffDto.class);
        assertThat(testA.getResults().get(0).getStaffId()).isEqualTo(309);
        assertThat(testA.getResults().get(0).getName()).isEqualTo("KRUEGER, HON. KURT");
        assertThat(testA.getResults().get(1).getStaffId()).isEqualTo(308);
        assertThat(testA.getResults().get(1).getName()).isEqualTo("NEWMAN, HON. BRAD");

    }

}
