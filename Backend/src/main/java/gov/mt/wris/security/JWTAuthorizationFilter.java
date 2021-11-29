package gov.mt.wris.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * @author Cesar.Zamorano
 *
 */
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	/**
	 * Logger
	 */
	Logger LOGGER = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

	/**
	 * Constants
	 */
	private final String HEADER = "Authorization";
	private final String PREFIX = "Bearer ";
	private String secretKey;

	private AuthenticationEntryPoint restUnauthorizedHandler;
	private AccessDeniedHandler restAccessDeniedHandler;

	/**
	 * @param userDetailsService
	 * @param deniedHandler
	 * @param entryPoint
	 * @param properties
	 */
	public JWTAuthorizationFilter(UserDetailsService userDetailsService, AccessDeniedHandler deniedHandler,
			AuthenticationEntryPoint entryPoint, SecurityProperties properties) {
		this.restUnauthorizedHandler = entryPoint;
		this.restAccessDeniedHandler = deniedHandler;
		secretKey = properties.getSecretKey();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws AccessDeniedException, BadCredentialsException, ServletException, IOException {

		LOGGER.info("REST API Request started.");
		
		try {
			if (existsJWTToken(request, response)) {
				// All REST Services except login has to validate token
				String authToken = request.getHeader(HEADER).replace(PREFIX, "");
				// If has token, we validate it
				Claims claims = TokenUtils.validateToken(authToken, secretKey);
				LOGGER.debug("token validated!");
				setUpSpringAuthentication(claims);
			}
			chain.doFilter(request, response);

		} catch (IllegalArgumentException | UnsupportedJwtException | MalformedJwtException e) {
			LOGGER.debug(e.getMessage());
			SecurityContextHolder.clearContext();
			restAccessDeniedHandler.handle(request, response, new AccessDeniedException(e.getMessage()));
		} catch (UsernameNotFoundException | ExpiredJwtException e3) {
			LOGGER.debug(e3.getMessage());
			SecurityContextHolder.clearContext();
			restUnauthorizedHandler.commence(request, response, new BadCredentialsException(e3.getMessage()));
		}
		LOGGER.info("REST API Request complete.");
	}

	private boolean existsJWTToken(HttpServletRequest request, HttpServletResponse res) {
		LOGGER.debug("existsJWTToken");

		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
			return false;
		return true;
	}

	private void setUpSpringAuthentication(Claims claims) {
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				java.util.Collections.emptyList());
		SecurityContextHolder.getContext().setAuthentication(auth);

	}

}