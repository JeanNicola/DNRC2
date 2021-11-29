package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.dtos.RoleTypesResponseDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class RoleTypesIntegrationTests extends BaseTestCase {

	private final String SERVICE_URL = "/api/v1/role-types";

	/**
	 * Test for GET method for RoleTypes
	 */
	@Test
	public void testMethods() throws Exception {
		String token = getAccessToken();

		MvcResult result = mockMvc
				.perform(get(SERVICE_URL).header("Authorization", "Bearer " + token).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		RoleTypesResponseDto dtos = convertTo(result, RoleTypesResponseDto.class);
		assertThat(dtos.getResults()).as("Can't find the first county of the list").hasSizeGreaterThan(0);
	}

}