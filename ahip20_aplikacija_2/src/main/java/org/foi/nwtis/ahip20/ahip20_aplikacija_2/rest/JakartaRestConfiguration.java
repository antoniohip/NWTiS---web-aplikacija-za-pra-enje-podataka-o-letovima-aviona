
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */
@ApplicationPath("rest")
public class JakartaRestConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> klase = new HashSet<>();
        klase.add(KorisniciResource.class);
        klase.add(AerodromiResource.class);
        klase.add(MojiAerodromiResource.class);
        klase.add(DnevnikResource.class);
        return klase;
    }
    
}