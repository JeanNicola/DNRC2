package gov.mt.wris;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Cesar.Zamorano
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class SecurityIntegrationTests extends BaseTestCase {

	@Autowired
	ObjectMapper mapper;

	@Test
	public void testFailedLogin() throws Exception {
		String user = "user";
		String pwd = "pass";

		String json = new JSONObject().put("user", user).put("password", pwd).toString();
		String body = new String(java.util.Base64.getEncoder().encode(json.getBytes()));

		mockMvc.perform(post("/api/auth/login").contentType("application/json").content(body))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testSuccessfulLogin() throws Exception {
		// SecurityController OK is tested in BaseTestCase so it could be used by
		// another tests
		assertNotNull(this.getAccessToken());
	}

}
