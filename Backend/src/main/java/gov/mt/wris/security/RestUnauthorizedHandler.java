package gov.mt.wris.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import gov.mt.wris.dtos.Message;
import gov.mt.wris.utils.GlobalExceptionHandler;

/**
 * @author Cesar.Zamorano
 *
 */
@Component
public class RestUnauthorizedHandler implements AuthenticationEntryPoint {
	
	Logger LOGGER = LoggerFactory.getLogger(RestAccessDeniedHandler.class);
    
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
		throws IOException, ServletException {
		LOGGER.debug("REST Unauthorized Access Handler");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Message content = GlobalExceptionHandler.staticBuildApiMessage(authException, "User request is unauthorized", authException.getMessage());

		// Convert the contant into a JSON stucture
		ObjectMapper mapper = new ObjectMapper();
		String body = mapper.writeValueAsString(content);
		response.getWriter().println(body);
	}

}
