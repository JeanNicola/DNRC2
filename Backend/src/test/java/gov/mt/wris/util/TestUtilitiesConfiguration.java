package gov.mt.wris.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.mt.wris.security.AuthenticationResponse;
import gov.mt.wris.security.SecurityProperties;
import java.util.Base64;
import java.util.Optional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestConfiguration
public class TestUtilitiesConfiguration {

    private static Optional<String> accessToken = Optional.empty();

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    SecurityProperties properties;

    @Bean
    public String token() throws Exception {
        if (accessToken.isPresent()) {
            return accessToken.get();
        }

        MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();

        String user = properties.getTestingUser();
        String password = properties.getTestingPassword();

        String json = new JSONObject()
            .put("user", user)
            .put("password", password)
            .toString();

        String body = new String(Base64.getEncoder().encode(json.getBytes()));

        String result = mvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andReturn()
            .getResponse()
            .getContentAsString();

        accessToken =
            Optional.of(
                mapper
                    .readValue(result, AuthenticationResponse.class)
                    .getAccessToken()
            );

        return accessToken.get();
    }
}
