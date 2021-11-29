package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import gov.mt.wris.dtos.Message;
import gov.mt.wris.dtos.CaseStatusDto;
import gov.mt.wris.dtos.CaseStatusPageDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class CaseStatusIntegrationTests extends BaseTestCase {

	private final String SERVICE_URL = "/api/v1/case-status";

	@Test
	@Transactional
	@Rollback(true)
	public void testMethods() throws Exception {
		String token = getAccessToken();

		MvcResult result = mockMvc
				.perform(get(SERVICE_URL).header("Authorization", "Bearer " + token).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		CaseStatusPageDto casePageDto = convertTo(result, CaseStatusPageDto.class);

		// check if Test is created and fail out if so
		assertThat(casePageDto.getResults()).as("Make sure that a TEST case status hasn't already been created")
				.filteredOn(caseType -> "TEST".equals(caseType.getCode())).hasSize(0);

		Long startingTotalElements = casePageDto.getTotalElements();

		// Create Case
		CaseStatusDto newCase = new CaseStatusDto();
		newCase.setCode("TEST");
		newCase.setDescription("Test Case Status");

		mockMvc.perform(post(SERVICE_URL).content(getJson(newCase)).header("Authorization", "Bearer " + accessToken)
				.contentType("application/json")).andExpect(status().isCreated()).andReturn();

		// check with filter
		result = mockMvc.perform(get(SERVICE_URL).header("Authorization", "Bearer " + accessToken).param("code", "TEST")
				.contentType("application/json")).andExpect(status().isOk()).andReturn();
		casePageDto = convertTo(result, CaseStatusPageDto.class);

		assertThat(casePageDto.getResults()).as("Can't find the Test case status that was created").hasSize(1);
		assertThat(casePageDto.getResults().get(0).getCode()).as("The filtering on code isn't working properly")
				.isEqualTo("TEST");

		// Update Case
		newCase.setDescription("Updated Test Case Status");
		result = mockMvc
				.perform(put(SERVICE_URL + "/TEST").content(getJson(newCase))
						.header("Authorization", "Bearer " + accessToken).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();

		// check with filter
		result = mockMvc.perform(get(SERVICE_URL).header("Authorization", "Bearer " + accessToken).param("code", "TEST")
				.contentType("application/json")).andExpect(status().isOk()).andReturn();
		casePageDto = convertTo(result, CaseStatusPageDto.class);

		assertThat(casePageDto.getResults()).as("Can't find the Test case status that was updated").hasSize(1);
		assertThat(casePageDto.getResults().get(0).getCode()).as("The filtering on code isn't working properly")
				.isEqualTo("TEST");

		// Update Case with new Code
		newCase.setCode("TEST");
		result = mockMvc
				.perform(put(SERVICE_URL + "/TEST").content(getJson(newCase))
						.header("Authorization", "Bearer " + accessToken).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();

		// check with filter
		result = mockMvc.perform(get(SERVICE_URL).header("Authorization", "Bearer " + accessToken).param("code", "TEST")
				.contentType("application/json")).andExpect(status().isOk()).andReturn();
		casePageDto = convertTo(result, CaseStatusPageDto.class);

		assertThat(casePageDto.getResults()).as("Can't find the Test case status that was updated").hasSize(1);
		assertThat(casePageDto.getResults().get(0).getCode()).as("The filtering on code isn't working properly")
				.isEqualTo("TEST");

		// Delete
		mockMvc.perform(delete(SERVICE_URL + "/TEST").header("Authorization", "Bearer " + accessToken)
				.contentType("application/json")).andExpect(status().isNoContent()).andReturn();

		// check without filter
		result = mockMvc.perform(
				get(SERVICE_URL).header("Authorization", "Bearer " + accessToken).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		casePageDto = convertTo(result, CaseStatusPageDto.class);

		assertThat(casePageDto.getTotalElements()).isEqualTo(startingTotalElements);
	}

	@Test
	void testDuplicateId() throws Exception {
		String token = getAccessToken();
		CaseStatusDto newCase = new CaseStatusDto();
		newCase.setDescription("Test Case Status");

		// grab Case status that already exists
		MvcResult result = mockMvc
				.perform(get(SERVICE_URL).header("Authorization", "Bearer " + token).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		newCase.setCode(convertTo(result, CaseStatusPageDto.class).getResults().get(0).getCode());

		result = mockMvc.perform(post(SERVICE_URL).content(getJson(newCase))
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

		mockMvc.perform(delete(SERVICE_URL + "/TESV").header("Authorization", "Bearer " + token)
				.contentType("application/json")).andExpect(status().isNotFound());
	}

	// Change Case
	// Doesn't exist with that code
	@Test
	void testNonExistentChange() throws Exception {
		String token = getAccessToken();

		CaseStatusDto newCase = new CaseStatusDto();
		newCase.setDescription("Test Case Status");
		newCase.setCode("TESV");
		mockMvc.perform(put(SERVICE_URL + "/TESV").content(getJson(newCase)).header("Authorization", "Bearer " + token)
				.contentType("application/json")).andExpect(status().isNotFound());
	}
}