package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.CountiesResponseDto;
import gov.mt.wris.dtos.CountyDto;
import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.SubdivisionCodeDto;
import gov.mt.wris.dtos.SubdivisionCodePageDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class SubdivisionCodesIntegrationTests extends BaseTestCase {

	private final String SUB_SERVICE_URL = "/api/v1/subdivision-codes";

	private final String COUNTY_SERVICE_URL = "/api/v1/counties";

	@Test
	@Rollback(true)
	public void testMethods() throws Exception {
		String token = getAccessToken();

		MvcResult result = mockMvc
				.perform(
						get(SUB_SERVICE_URL).header("Authorization", "Bearer " + token).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		SubdivisionCodePageDto subCodeDTO = convertTo(result, SubdivisionCodePageDto.class);

		// check if Test is created and fail out if so
		assertThat(subCodeDTO.getResults()).as("Make sure that a TEST subdivision code hasn't already been created")
				.filteredOn(caseType -> "TEST".equals(caseType.getCode())).hasSize(0);

		// Calling Counties service
		result = mockMvc.perform(
				get(COUNTY_SERVICE_URL).header("Authorization", "Bearer " + token).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		CountiesResponseDto dtos = convertTo(result, CountiesResponseDto.class);

		assertThat(!dtos.getResults().isEmpty()).as("Calling Counties Service to get the first.");

		CountyDto countyDTO = dtos.getResults().get(0);
		Long startingTotalElements = subCodeDTO.getTotalElements();
		SubdivisionCodeDto dto = subCodeDTO.getResults().get(0);
		BigDecimal countyId = countyDTO.getId();
		String first2 = countyDTO.getStateCountyNumber();
		String countyName = countyDTO.getName();

		// Create Case
		SubdivisionCodeDto newSubcode = new SubdivisionCodeDto();
		newSubcode.setCode(first2 + "TES");
		newSubcode.setDnrcName("Test Subdivision Codes");
		newSubcode.setDorName("DorName");
		newSubcode.setCountyId(countyId);

		mockMvc.perform(post(SUB_SERVICE_URL).content(getJson(newSubcode))
				.header("Authorization", "Bearer " + accessToken).contentType("application/json"))
				.andExpect(status().isCreated()).andReturn();

		// check with filter
		result = mockMvc
				.perform(get(SUB_SERVICE_URL).header("Authorization", "Bearer " + accessToken)
						.param("code", first2 + "TES").contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		subCodeDTO = convertTo(result, SubdivisionCodePageDto.class);

		assertThat(subCodeDTO.getResults()).as("Can't find the Test subdivision code that was created").hasSize(1);
		dto = subCodeDTO.getResults().get(0);
		assertThat(dto.getCode()).as("The filtering on code isn't working properly").isEqualTo(first2 + "TES");

		// Update Case
		newSubcode.setDnrcName("Updated Test Subdivision Code");
		result = mockMvc
				.perform(put(SUB_SERVICE_URL + "/" + first2 + "TES").content(getJson(newSubcode))
						.header("Authorization", "Bearer " + accessToken).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();

		// check with filter
		result = mockMvc
				.perform(get(SUB_SERVICE_URL).header("Authorization", "Bearer " + accessToken)
						.param("code", first2 + "TES").contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		subCodeDTO = convertTo(result, SubdivisionCodePageDto.class);

		assertThat(subCodeDTO.getResults()).as("Can't find the Test subdivision code that was updated").hasSize(1);
		dto = subCodeDTO.getResults().get(0);
		assertThat(dto.getCode()).as("The filtering on code isn't working properly").isEqualTo(first2 + "TES");
		assertThat(dto.getCountyName()).as("The County Name isn't working properly").isEqualTo(countyName);

		// Update Case with new Code
		newSubcode.setCode(first2 + "TES");
		result = mockMvc
				.perform(put(SUB_SERVICE_URL + "/" + first2 + "TES").content(getJson(newSubcode))
						.header("Authorization", "Bearer " + accessToken).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();

		// check with filter
		result = mockMvc
				.perform(get(SUB_SERVICE_URL).header("Authorization", "Bearer " + accessToken)
						.param("code", first2 + "TES").contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		subCodeDTO = convertTo(result, SubdivisionCodePageDto.class);

		assertThat(subCodeDTO.getResults()).as("Can't find the Test subdivision code that was updated").hasSize(1);
		dto = subCodeDTO.getResults().get(0);
		assertThat(dto.getCountyName()).as("The County Name isn't working properly").isEqualTo(countyName);

		// Delete
		mockMvc.perform(delete(SUB_SERVICE_URL + "/" + first2 + "TES").header("Authorization", "Bearer " + accessToken)
				.contentType("application/json")).andExpect(status().isNoContent()).andReturn();

		// check without filter
		result = mockMvc.perform(
				get(SUB_SERVICE_URL).header("Authorization", "Bearer " + accessToken).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		subCodeDTO = convertTo(result, SubdivisionCodePageDto.class);

		assertThat(subCodeDTO.getTotalElements()).isEqualTo(startingTotalElements);
	}

	@Test
	void testDuplicateId() throws Exception {
		String token = getAccessToken();
		SubdivisionCodeDto newSubcode = new SubdivisionCodeDto();
		newSubcode.setCountyId(new BigDecimal(31));
		newSubcode.setDnrcName("Test Dnrc");
		newSubcode.setDorName("DorName");

		// grab Subdivision Codes that already exists
		MvcResult result = mockMvc
				.perform(
						get(SUB_SERVICE_URL).header("Authorization", "Bearer " + token).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		newSubcode.setCode(convertTo(result, SubdivisionCodePageDto.class).getResults().get(0).getCode());

		result = mockMvc
				.perform(post(SUB_SERVICE_URL).content(getJson(newSubcode))
						.header("Authorization", "Bearer " + accessToken).contentType("application/json"))
				.andExpect(status().isConflict()).andReturn();
		Message message = convertTo(result, Message.class);
		assertThat(message.getUserMessage().toLowerCase()).contains("already exists");
	}

	// Delete Case
	// what happens if doesn't exist
	@Test
	void testNonExistentDelete() throws Exception {
		String token = getAccessToken();

		mockMvc.perform(delete(SUB_SERVICE_URL + "/TESV").header("Authorization", "Bearer " + token)
				.contentType("application/json")).andExpect(status().isNotFound());
	}

	// Change Case
	// Doesn't exist with that code
	@Test
	void testNonExistentChange() throws Exception {
		String token = getAccessToken();

		SubdivisionCodeDto newSubcode = new SubdivisionCodeDto();
		newSubcode.setDnrcName("Test Dnrc");
		newSubcode.setCountyId(new BigDecimal(31));
		newSubcode.setDorName("DorName");
		newSubcode.setCode("TEST");
		mockMvc.perform(put(SUB_SERVICE_URL + "/TEST").content(getJson(newSubcode))
				.header("Authorization", "Bearer " + token).contentType("application/json"))
				.andExpect(status().isNotFound());
	}
}