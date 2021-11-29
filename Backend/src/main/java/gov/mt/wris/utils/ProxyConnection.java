package gov.mt.wris.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import oracle.jdbc.OracleConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/*
 * Intercept the call to the database getConnection() to open the Oracle Proxy User connection.
 * It's done here so all database connection calls are opened as Oracle Proxys
 *
 */
@Aspect
@Configuration
public class ProxyConnection {
    private static Logger LOGGER = LoggerFactory.getLogger(ProxyConnection.class);

    // This is the wrapper around all calls to getConnection.
    @Around("execution(java.sql.Connection com.zaxxer.hikari.HikariDataSource.getConnection(..)) ")
    public Object openProxyConnection(ProceedingJoinPoint jp) throws Throwable {
        LOGGER.debug("Open Proxy Connection");

        // Since we are only interested in the database connection itself, don't do anything before the call, just pass it through
        Object result = jp.proceed(jp.getArgs());

        // If the result of the wrapped call is a Connection, then proceed with setting up the proxy; othwerwise just pass it through
        if (result instanceof Connection) {
            /*
             * Since the db pool initialization process also calls getConnection(), we need to seperate out "setup" and actual
             * user calls. This is done using the SecurityContext whcih gets set when an authenticated user called a REST API.
             */
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                String proxyUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                LOGGER.debug("Login User     : " + proxyUsername);

                /*
                 * Here is where the Oracle Proxy user is setup on the existing connection.
                 * 1. Unwrap the actual Oracle connection from the "pool" connection.
                 * 2. For some reason the connection may still have a previous Oracle Proxy session. If so, then close it.
                 * 3. Set the properties necessary for the Oracle Proxy user connection. The userid comes from the SecurityContext when the 
                 *    user authenticated with the REST API.
                 * 4. Open the Oracle Proxy user session.
                 */
                try {
                    OracleConnection oc = ((Connection) result).unwrap(OracleConnection.class);
                    LOGGER.debug("Connection User: " + oc.getUserName());

                    if(oc.isProxySession()) {
                        if(oc.getUserName().equals(proxyUsername)) {
                            LOGGER.debug("There was a proxy session for the user already open. Using that one.");
                            return result;
                        }
                        LOGGER.debug("Grabbed the wrong proxy session");
                        oc.close(OracleConnection.PROXY_SESSION);
                    }

                    Properties prop = new java.util.Properties();
                    prop.put(OracleConnection.PROXY_USER_NAME, proxyUsername);                    
                    oc.openProxySession(OracleConnection.PROXYTYPE_USER_NAME, prop);

                    LOGGER.debug("DB Proxy User  : " + oc.getUserName());
                } catch (SQLException e) {
                    // If an SQLException occurred, log the error then close the current connection the pass the error back.
                    // This is typically for an invalid proxy user. There may be other instances as well.
                    // Otherwise it would still be considered "open" and diminish the available pool connections.
                    LOGGER.error("*** ERROR ***");
                    LOGGER.error("User id: <" + proxyUsername + ">");
                    LOGGER.error(e.getErrorCode() + ": " + e.getMessage());
                    Connection c = (Connection) result;
                    c.close();
                    throw new SQLException(e);
                }
            }
            else {
                LOGGER.debug("No user login");
            }
        }
        else {
            LOGGER.debug("No Connection was returned");
        }

        return result;
    }
}