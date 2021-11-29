package gov.mt.wris.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Cesar.Zamorano
 *
 */
@ConfigurationProperties(prefix = "wris.security")
public class SecurityProperties {

	private String secretKey;

	private long tokenExpirationOffset;

	private Boolean debugMode;

	private String truststore;

	private String directoryUrl;

	private String userDn;

	private String password;

	private String truststorePassword;
	
	private String testingUser;
	
	private String testingPassword;

	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @return the tokenExpirationOffset
	 */
	public long getTokenExpirationOffset() {
		return tokenExpirationOffset;
	}

	/**
	 * @param tokenExpirationOffset the tokenExpirationOffset to set
	 */
	public void setTokenExpirationOffset(long tokenExpirationOffset) {
		this.tokenExpirationOffset = tokenExpirationOffset;
	}

	/**
	 * @return the debugMode
	 */
	public Boolean getDebugMode() {
		return debugMode;
	}

	/**
	 * @param debugMode the debugMode to set
	 */
	public void setDebugMode(Boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * @return the truststore
	 */
	public String getTruststore() {
		return truststore;
	}

	/**
	 * @param truststore the truststore to set
	 */
	public void setTruststore(String truststore) {
		this.truststore = truststore;
	}

	/**
	 * @return the directoryUrl
	 */
	public String getDirectoryUrl() {
		return directoryUrl;
	}

	/**
	 * @param directoryUrl the directoryUrl to set
	 */
	public void setDirectoryUrl(String directoryUrl) {
		this.directoryUrl = directoryUrl;
	}

	/**
	 * @return the userD
	 */
	public String getUserDn() {
		return userDn;
	}

	/**
	 * @param userD the userDn to set
	 */
	public void setUserDn(String userDn) {
		this.userDn = userDn;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the truststorePassword
	 */
	public String getTruststorePassword() {
		return truststorePassword;
	}

	/**
	 * @param truststorePassword the truststorePassword to set
	 */
	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

	/**
	 * @return the testingUser
	 */
	public String getTestingUser() {
		return testingUser;
	}

	/**
	 * @param testingUser the testingUser to set
	 */
	public void setTestingUser(String testingUser) {
		this.testingUser = testingUser;
	}

	/**
	 * @return the testingPassword
	 */
	public String getTestingPassword() {
		return testingPassword;
	}

	/**
	 * @param testingPassword the testingPassword to set
	 */
	public void setTestingPassword(String testingPassword) {
		this.testingPassword = testingPassword;
	}

}
