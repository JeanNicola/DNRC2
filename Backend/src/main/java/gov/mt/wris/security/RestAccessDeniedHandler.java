package gov.mt.wris.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import gov.mt.wris.dtos.Message;
import gov.mt.wris.utils.GlobalExceptionHandler;

/**
 * @author Cesar.Zamorano
 *
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
	
	Logger LOGGER = LoggerFactory.getLogger(RestAccessDeniedHandler.class);
    
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessException)
			throws IOException {
		LOGGER.debug("REST Access Denied Handler");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Message content = GlobalExceptionHandler.staticBuildApiMessage(accessException, "User access is denied", null);

		// Convert the contant into a JSON stucture
		ObjectMapper mapper = new ObjectMapper();
		String body = mapper.writeValueAsString(content);
		response.getWriter().println(body);
	}
}
