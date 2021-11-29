package gov.mt.wris.services.Implementation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.commons.math3.util.Pair;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gov.mt.wris.annot.PermissionsNeeded;
import gov.mt.wris.exceptions.HelpDeskNeededException;
import gov.mt.wris.services.EndpointService;

@Service
public class EndpointServiceImpl implements EndpointService {
    private static Logger LOGGER = LoggerFactory.getLogger(EndpointServiceImpl.class);

    @Autowired
    private ListableBeanFactory listableBeanFactory;

    @PersistenceContext
    private EntityManager manager;

    private List<Method> getDelegateMethods(Object cont) {
        Method hasDelegate = null;
        Class<?> delegateClass;
        try {
            hasDelegate = cont.getClass().getMethod("getDelegate", (Class<?>[]) null);
            delegateClass = hasDelegate.invoke(cont).getClass();
            return Arrays.asList(delegateClass.getMethods());
        } catch (NoSuchMethodException | InvocationTargetException | SecurityException | IllegalAccessException e ) {
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, List<String>> getEndpoints() {
        /*
            Three steps determine which endpoint methods a user can access
            1. Get the list of permissions used by the implemented endpioint methods from the application REST controllers
                a. Use this list to create an SQL WHERE clause to limit the query to only these tables and permissions
                b. Query the database to get the set of tables/permissions granted to the user
            2. For each permission used by the implemented endpoint methods on the application REST controllers,
                verify the user has that permission on that table. If the user has all required permissions
                save the method. If a method does not have any required permissions, save it as well since
                anyone can access that method. If the user is missing any required permission, the user cannot access that 
                endpoint.
            3. For each REST API method on the REST Controllers, get the API mapping (GET, POST, PUT, DELETE) and the endpoint path.
                If the API method is in the list of methods created in Step #2, save the API mapping and the path
            4. Return the list of endpoints and API mappings the user can access
        */

        Map<String, List<String>> endpoints = new HashMap<String, List<String>>();

        // part 1
        Map<String, Object> controllers = listableBeanFactory.getBeansWithAnnotation(Controller.class);

        Map<String, Set<String>> permissionsUsed = new HashMap();
        for(Map.Entry<String, ?> cont : controllers.entrySet()) {
            for(Method m : getDelegateMethods(cont.getValue())) {
                PermissionsNeeded permissions = m.getAnnotation(PermissionsNeeded.class);
                if(permissions == null) continue;

                for(int index=0; index < permissions.value().length; index++) {
                    String verb = permissions.value()[index].verb();
                    String table = permissions.value()[index].table();
                    if(permissionsUsed.containsKey(verb)) {
                        permissionsUsed.get(verb).add(table);
                    } else {
                        permissionsUsed.put(verb, new HashSet(Arrays.asList(table)));
                    }
                }
            }
        }

        // part 1a
        // Add extra conditions to check if values are used
        String basicQuery = "SELECT DISTINCT t.PRIVILEGE,\n" +
                                            "t.TABLE_NAME\n" +
                            "FROM USER_ROLE_PRIVS r\n" +
                            "JOIN DBA_TAB_PRIVS t\n" +
                            "ON r.GRANTED_ROLE = t.GRANTEE\n" + 
                            "WHERE t.OWNER = 'WRD'";

        
        String whereCondition = permissionsUsed.entrySet().stream().map(entry -> 
            "(t.PRIVILEGE = '" + entry.getKey() + "' AND t.TABLE_NAME IN ('" + entry.getValue().stream().collect(Collectors.joining("', '")) + "'))"
        ).collect(Collectors.joining("\n OR "));
        basicQuery = basicQuery + "\nAND (\n" + whereCondition + "\n)"; 

        // part 1b
        List<Object[]> results;
        try {
            results = manager.createNativeQuery(basicQuery).getResultList();
        } catch(PersistenceException e) {
            // Check to ensure the cause was invalid configuration.If so throw new error;
            // Otherwise re-throw the origianl error
            // check that basin code and application type code exist
            if(e.getCause() instanceof SQLGrammarException) {
                SQLGrammarException gv = (SQLGrammarException) e.getCause();
                if (gv.getErrorCode() == 942){
                    LOGGER.debug("*** User is not  configured properly in the database ***\n" + e);
                    throw new HelpDeskNeededException("Cannot query DNR priviliges. " +
                        "Database permissions are set up incorrectly for your userid.\n" +
                        "Please open a ticket with the Help Desk to get this resolved.");
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }

        List<Pair<String, String>> privileges = new ArrayList<>();
        
        /*
         * Get the tables and privleges granted to the user from the database
         * E.g.:
         * PRIVILEGE    TABLE_NAME
         * ---------    ---------------
         * UPDATE	    WRD_EVENT_DATES
         * SELECT	    WRD_ADDRESSES
         */
        LOGGER.trace("From the database:");
        for(Object[] priv : results) {
            LOGGER.trace("\t" + (String) priv[0] + ":" + (String) priv[1]);
            privileges.add(new Pair<>((String) priv[0], (String) priv[1]));
        }
        LOGGER.trace("********************************");


        // part 2
        LOGGER.trace("Method Table permissions:");
        for(Map.Entry<String, ?> cont : controllers.entrySet()) {

            List<String> methods = new ArrayList<String>();
            // look through all of our delegate controllers methods
            for(Method m : getDelegateMethods(cont.getValue())) {
                PermissionsNeeded permissions = m.getAnnotation(PermissionsNeeded.class);
                // if the method doesn't have annotations, let everyone access it
                // But if it does, check for each permission
                Boolean hasPermission = true;
                if(permissions != null) {
                    for(int index=0; index < permissions.value().length; index++) {
                        String verb = permissions.value()[index].verb();
                        String table = permissions.value()[index].table();
                        Pair<String, String> priv = new Pair<>(verb, table);
                        
                        // If the user does not have a privilege then ignore the method
                        if(!privileges.contains(priv)) {
                            hasPermission = false;
                            break;
                        }
                        LOGGER.trace("\t" + m.getName() + ": " + priv);
                    }
                }
                if (hasPermission) methods.add(m.getName());
            }

            // part 3
            // Look through the interfaces of each controller and its superclass for request mappings
            // And then check if the user has all the permissions
            LOGGER.trace("Getting Super Classes");
            for(Class<?> i: cont.getValue().getClass().getSuperclass().getInterfaces()) {
                for(Method m : i.getMethods()) {
                    // if the current method is not in the list of methods, move on to next method
                    if (!methods.contains(m.getName())) {
                        continue;
                    }
                    
                    // Set endpoint permissions based on the @GetMapping, @PostMapping, @PutMapping, and @DeleteMapping annotations
                    endpoints = setEndpoints(endpoints, m);
                }
            }

            LOGGER.trace("Getting Classes");
            for(Class<?> i: cont.getValue().getClass().getInterfaces()) {
                for(Method m : i.getMethods()) {
                    // if the current method is not in the list of methods, move on to next method
                    if (!methods.contains(m.getName())) {
                        continue;
                    }
                    
                    // Set endpoint permissions based on the @GetMapping, @PostMapping, @PutMapping, and @DeleteMapping annotations
                    endpoints = setEndpoints(endpoints, m);
                }
            }
        }

        // part 4
        return endpoints;
    }

    private Map<String, List<String>> setEndpoints(Map<String, List<String>> endpoints, Method m) {    
        // get the annotations for the method. Look for the @GetMapping, @PostMapping, @PutMapping, and @DeleteMapping annotations
        String end = null;
        String method = null;

        // Get all of the annotations on the method then if they are one of the 4 HTTP mappings, save the path (value)
        // and set the method. Then save the method to the endpoints map.
        for(Annotation a: m.getAnnotations()) {
            if(a instanceof GetMapping) {
                GetMapping g = (GetMapping) a;
                end = g.value()[0];
                method = "GET";
                break;
            } else if(a instanceof PostMapping) {
                PostMapping p = (PostMapping) a;
                end = p.value()[0];
                method = "POST";
                break;
            } else if(a instanceof PutMapping) {
                PutMapping pu = (PutMapping) a;
                end = pu.value()[0];
                method = "PUT";
                break;
            } else if(a instanceof DeleteMapping) {
                DeleteMapping d = (DeleteMapping) a;
                end = d.value()[0];
                method = "DELETE";
                break;
            } else if(a instanceof RequestMapping) {
                RequestMapping r = (RequestMapping) a;
                end = r.value()[0];
                method = r.method()[0].name();
                break;
            }
            else {
                continue;
            }
        }

        // If the endpoint for the Annotation does not exist in the map, add it
        if(endpoints.get(end) == null) {
            endpoints.put(end, new ArrayList<>());
        }

        endpoints.get(end).add(method);
        LOGGER.trace("\t" + method + ":\t" + end);
        return endpoints;
    }
}
