/**
 * 
 */
package gov.mt.wris.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Cesar.Zamorano
 *
 */
public class TokenUtils {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtils.class);

	/**
	 * @param jwtToken
	 * @param secretKey
	 * @return
	 */
	public static Claims validateToken(String jwtToken, String secretKey) {
		LOGGER.debug("validateToken");

		Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS256.getJcaName());
		Jws<Claims> jwt = Jwts.parserBuilder().setSigningKey(hmacKey).build().parseClaimsJws(jwtToken);
		return jwt.getBody();
	}

	/**
	 * @param username
	 * @param issuedAt
	 * @param expiration
	 * @param secretKey
	 * @return
	 */
	public static String getJWTToken(String username, Date issuedAt, Date expiration, String secretKey) {
		Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS256.getJcaName());

		String token = Jwts.builder().setId(UUID.randomUUID().toString()).setSubject(username).setIssuedAt(issuedAt)
				.setExpiration(expiration).signWith(hmacKey, SignatureAlgorithm.HS256).compact();

		LOGGER.debug("Token generated");
		return token;
	}

}
