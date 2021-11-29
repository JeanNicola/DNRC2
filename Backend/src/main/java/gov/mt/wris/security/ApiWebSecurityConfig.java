package gov.mt.wris.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author Cesar.Zamorano
 *
 */
@Configuration
@EnableWebSecurity
public class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {
	/**
	 *  Logger
	 */
	private Logger LOGGER = LoggerFactory.getLogger(ApiWebSecurityConfig.class);
	
	@Autowired
	private RestAccessDeniedHandler deniedHandler;

	@Autowired
	private RestUnauthorizedHandler entryPoint;
	
	@Autowired
	private SecurityProperties properties;

	private static final String[] SWAGGER_WHITELIST = {
		"/swagger-ui/**",
		"/swagger-resources/**",
		"/v2/api-docs"
	};

	@Bean
	public HttpFirewall allowPercentHttpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedPercent(true);
		firewall.setAllowBackSlash(true);
		firewall.setAllowSemicolon(true);
		firewall.setAllowUrlEncodedDoubleSlash(true);
		firewall.setAllowUrlEncodedPeriod(true);
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}

	@Bean
	public JWTAuthorizationFilter filter() {
		LOGGER.debug("In JWTAuthorizationFilter");

		return new JWTAuthorizationFilter(userDetailsService()
				,deniedHandler,entryPoint, properties);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.debug("In http configure");
	
		http
			.csrf().disable()
			.cors().and()
			.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
				.antMatchers(HttpMethod.GET, SWAGGER_WHITELIST).permitAll()
				.anyRequest().fullyAuthenticated()
				.and()
			.exceptionHandling()
				.accessDeniedHandler(deniedHandler)
				.authenticationEntryPoint(entryPoint)
				.and()
			.addFilterBefore(filter(), UsernamePasswordAuthenticationFilter.class)
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		LOGGER.debug("In corsConfuguration");
	
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConf = new CorsConfiguration().applyPermitDefaultValues();
		corsConf.addAllowedMethod("PUT");
		corsConf.addAllowedMethod("DELETE");
		source.registerCorsConfiguration("/**", corsConf);
		return source;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		LOGGER.debug("In LDAP configure");

		auth.ldapAuthentication()
			.userSearchFilter("(sAMAccountName={0})")
			.userDnPatterns("sAMAccountName={0}")
			.contextSource(getLdapContext());
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		LOGGER.debug("In AuthenticationManagerBean");

		return super.authenticationManagerBean();
	}

	@Bean
	public LdapContextSource getLdapContext() {
		LOGGER.debug("In getLdapContext");
		
		System.setProperty("javax.net.ssl.trustStore", properties.getTruststore());
		System.setProperty("javax.net.ssl.trustStorePassword", properties.getTruststorePassword());

		LdapContextSource source = new DefaultSpringSecurityContextSource(properties.getDirectoryUrl());
		source.setUserDn(properties.getUserDn()); 
		source.setPassword(properties.getPassword());
		source.setAnonymousReadOnly(false);
		source.setPooled(true);
		source.afterPropertiesSet();
		return source;
	}
	
	@Bean(name = BeanIds.USER_DETAILS_SERVICE)
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		LOGGER.debug("In userDetailsServiceBean");
		
		return super.userDetailsServiceBean();
	}
	
}