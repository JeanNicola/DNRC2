package gov.mt.wris.security;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.mt.wris.dtos.Message;
import gov.mt.wris.exceptions.HelpDeskNeededException;
import gov.mt.wris.services.MasterStaffIndexesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "login", description = "the login API")
@RestController
public class SecurityController {

	/**
	 * Logger
	 */
	Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);

	/**
	 * Constants
	 */
	private static final String LOGIN_PATH = "/api/auth/login";
	private static final String UPDATE_TOKEN_PATH = "/api/auth/update-token";
	private static final String BODY_FORMAT_ERROR = "Invalid request body format.";

	/**
	 * Injections
	 */
	private SecurityProperties properties;
	private AuthenticationManager authenticationManager;
	private MasterStaffIndexesService masterStaffIndexesService;

	/**
	 * @param authenticationManager
	 */
	@Autowired
	public SecurityController(AuthenticationManager authenticationManager, SecurityProperties properties,
			MasterStaffIndexesService masterStaffIndexesService) {
		this.authenticationManager = authenticationManager;
		this.properties = properties;
		this.masterStaffIndexesService = masterStaffIndexesService;
	}

	/**
	 * @param payload
	 * @return
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "user login", nickname = "login", notes = "", response = AuthenticationResponse.class, tags={  })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "valid user login", response = AuthenticationResponse.class),
			@ApiResponse(code = 401, message = "user not authorized", response = Message.class) })
	@RequestMapping(value = LOGIN_PATH,
			produces = { "application/json" }, 
			consumes = { "application/json" },
			method = RequestMethod.POST)
	public ResponseEntity<AuthenticationResponse> login(@RequestBody String payload) {
		LOGGER.debug("login()");

		try {
			String decodedJson = new String(Base64.getDecoder().decode(payload.getBytes()));
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> userInfo = mapper.readValue(decodedJson, Map.class);
			String username = userInfo.get("user").toUpperCase();
			String pwd = userInfo.get("password");

			// Only display the last 4 digits of the password in the log (only 1 digit if
			// less than one)
			Integer len = 0;
			if (pwd.length() > 0) {
				len = (pwd.length() < 4 ? 1 : 4);
			}

			LOGGER.debug("-- User    : <" + username + ">");
			LOGGER.debug("-- Password: <********" + pwd.substring(pwd.length() - len) + ">");

			AuthenticationResponse resp = doLogin(username, pwd);
			return new ResponseEntity<AuthenticationResponse>(resp, null, HttpStatus.OK);

		} catch (IllegalArgumentException | JsonProcessingException e) {
			LOGGER.debug("*** Illegal Argument | JsonProcessingException ERROR ***\n" + e);
			throw new BadCredentialsException(BODY_FORMAT_ERROR);
		} catch (BadCredentialsException e) {
			LOGGER.debug("*** ERROR ***\n" + e);
			throw new BadCredentialsException(e.getMessage());
		} catch (HelpDeskNeededException e) {
			LOGGER.debug("*** ERROR ***\n" + e);
			throw new HelpDeskNeededException(e.getMessage());
		}
	}

	/**
	 * @return
	 * @throws JSONException
	 */
	@ApiOperation(value = "user login session refresh", nickname = "session refresh", notes = "", response = AuthenticationResponse.class, tags={  })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "session refreshed", response = AuthenticationResponse.class),
			@ApiResponse(code = 401, message = "user not authorized", response = Message.class) })
	@RequestMapping(value = UPDATE_TOKEN_PATH,
			produces = { "application/json" }, 
			consumes = { "application/json" },
			method = RequestMethod.GET)
	public ResponseEntity<AuthenticationResponse> updateToken() {
		LOGGER.debug("updateToken()");

		try {
			AuthenticationResponse resp = new AuthenticationResponse();

			Date issuedAt = new Date(System.currentTimeMillis());
			Date expiration = new Date(System.currentTimeMillis() + properties.getTokenExpirationOffset());

			resp.setAccessToken(TokenUtils.getJWTToken(SecurityContextHolder.getContext().getAuthentication().getName(),
					issuedAt, expiration, properties.getSecretKey()));
			resp.setExpirationDate(expiration);
			return new ResponseEntity<AuthenticationResponse>(resp, null, HttpStatus.OK);

		} catch (IllegalArgumentException e) {
			LOGGER.debug("*** Illegal Argument | JSONException ERROR ***\n" + e);
			throw new BadCredentialsException(BODY_FORMAT_ERROR);
		} catch (Exception e) {
			LOGGER.debug("*** ERROR ***\n" + e);
			throw new BadCredentialsException(e.getMessage());
		}
	}

	private AuthenticationResponse doLogin(String username, String pwd) {

		LOGGER.debug("doLogin: Debug=" + properties.getDebugMode());

		AuthenticationResponse resp = new AuthenticationResponse();

		if (Boolean.TRUE.equals(properties.getDebugMode())) {
			LOGGER.debug("Authenticating local user - no LDAP");
				Authentication auth = new UsernamePasswordAuthenticationToken(username, null);
				SecurityContextHolder.getContext().setAuthentication(auth);
		} else {
			LOGGER.debug("Getting user from remote LDAP server");
			Authentication auth = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, pwd));
			SecurityContextHolder.getContext().setAuthentication(auth);
			LOGGER.debug("User is authenticated");
		}
		LOGGER.debug("User: " + username + ", at: " + new Date(System.currentTimeMillis()));

		Date issuedAt = new Date(System.currentTimeMillis());
		Date expiration = new Date(System.currentTimeMillis() + properties.getTokenExpirationOffset());

		resp.setAccessToken(TokenUtils.getJWTToken(username, issuedAt, expiration, properties.getSecretKey()));
		resp.setExpirationDate(expiration);
		resp.setUserData(getUserData(username));
		return resp;
	}

	private UserData getUserData(String directoryUser) {
		try {
			return masterStaffIndexesService.getUserDataByDirectoryUserName(directoryUser);
		} catch (JpaSystemException ex) {
			LOGGER.debug("Access to database failed in ProxyConnection. Reason:" + ex.getMessage());
			throw new HelpDeskNeededException(
					"Database permissions are not set up for your userid. Please open a ticket with the Help Desk to get this resolved.");
		}
	}
}
