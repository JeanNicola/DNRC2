package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.CountiesResponseDto;
import gov.mt.wris.dtos.CountyDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class CountiesIntegrationTests extends BaseTestCase {

	private final String SERVICE_URL = "/api/v1/counties";

	/**
	 * Test for GET method for Counties
	 */
	@Test
	public void testMethods() throws Exception {
		String token = getAccessToken();

		MvcResult result = mockMvc
				.perform(get(SERVICE_URL).header("Authorization", "Bearer " + token).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		CountiesResponseDto dtos = convertTo(result, CountiesResponseDto.class);
		assertThat(dtos.getResults()).as("Can't find the first county of the list").hasSizeGreaterThan(0);
	}

	@Test
	public void testGetDistrictCourtCounties() throws Exception {

		String token = getAccessToken();
		MvcResult result = null;
		long start, end;
		String districtCourt;
		float sec;

        districtCourt = "16";
		start = System.currentTimeMillis();
		result = mockMvc.perform(get("/api/v1/counties/district-court/" + districtCourt)
				.header("Authorization", "Bearer " + token)
				.param("districtCourt", districtCourt)
				.contentType("application/json"))
				.andExpect(status().isOk())
				.andReturn();
		end = System.currentTimeMillis();
		sec = (end - start) / 1000F;
		System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
		CountiesResponseDto testA = convertTo(result, CountiesResponseDto.class);
		assertThat(testA.getResults().get(0).getName()).isEqualTo("CARTER");
		assertThat(testA.getResults().get(6).getName()).isEqualTo("TREASURE");

	}

}