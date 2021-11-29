package gov.mt.wris;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import gov.mt.wris.security.AuthenticationResponse;
import gov.mt.wris.security.SecurityProperties;

public class BaseTestCase {
	
	protected static String accessToken = null;

	@Autowired
	SecurityProperties properties;
	
	@Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;
	
	@BeforeEach
	public void initMocks() {
		SecurityContextHolder.clearContext();
		MockitoAnnotations.initMocks(this);
	}
	
	@AfterEach
	public void close() {
		SecurityContextHolder.clearContext();
	}

    public <T> T convertTo(MvcResult jsonResult, Class<T> C) throws Exception{
        String json = jsonResult.getResponse().getContentAsString();
        return mapper.readValue(json, C);
    }

	public <T> List<T> convertToArray(MvcResult jsonResult, Class<T> type) throws Exception {
		String json = jsonResult.getResponse().getContentAsString();
		CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
		List<T> sources = mapper.readValue(json, listType);

		return sources;
	}
    
    public String getJson(Object dto){
        try {
            final String jsonContent = mapper.writeValueAsString(dto);
            return jsonContent;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDateTime getDate(String datetime) {
        return LocalDateTime.parse(datetime, DateTimeFormatter.ISO_DATE_TIME);
    }
    
    public String getAccessToken() throws Exception{
    	String user = properties.getTestingUser();
		String pwd = properties.getTestingPassword();
		
		assertNotNull(user,"You need to set a testing user in application.properties");
		
		String json = new JSONObject().put("user", user).put("password", pwd).toString();
		String body = new String(java.util.Base64.getEncoder().encode(json.getBytes()));
    	
        if(accessToken == null) {
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType("application/json")
                                .content(body))
                                .andReturn();
            accessToken = convertTo(loginResult, AuthenticationResponse.class).getAccessToken();
        }
        
        return accessToken;
    }
}