/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST resource:mojiAerodromi
 * [mojiAerodromi/{icao}/prate]<br>
 * USAGE:
 * <pre>
        MojiAerodromiKlijent_2 client = new MojiAerodromiKlijent_2();
        Object response = client.XXX(...);
        // do whatever with response
        client.close();
 </pre>
 *
 * @author NWTiS_2
 */
public class MojiAerodromiKlijent_2 {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/ahip20_aplikacija_2/rest/";

    public MojiAerodromiKlijent_2(String icao) {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        String resourcePath = java.text.MessageFormat.format("mojiAerodromi/{0}/prate", new Object[]{icao});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    public void setResourcePath(String icao) {
        String resourcePath = java.text.MessageFormat.format("mojiAerodromi/{0}/prate", new Object[]{icao});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korisnik header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public <T> T dajKorisnikeKojiPrateIcao(Class<T> responseType, String korisnik, String lozinka) throws ClientErrorException {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).header("korisnik", korisnik).header("lozinka", lozinka).get(responseType);
    }

    public void close() {
        client.close();
    }
    
}
