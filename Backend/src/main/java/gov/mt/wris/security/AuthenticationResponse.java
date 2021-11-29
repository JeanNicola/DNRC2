package gov.mt.wris.security;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * The Response object for login method in Security.
 * 
 * @author Cesar.Zamorano
 *
 */
@Getter
@Setter
public class AuthenticationResponse {

	private String accessToken;

	private UserData userData;

	private Date expirationDate;
}
