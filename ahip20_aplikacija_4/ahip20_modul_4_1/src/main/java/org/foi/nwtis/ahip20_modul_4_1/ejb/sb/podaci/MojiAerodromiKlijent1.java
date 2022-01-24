/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;

/**
 * Jersey REST client generated for REST resource:mojiAerodromi
 * [mojiAerodromi/{korisnik}/prati]<br>
 * USAGE:
 * <pre>
 *        MojiAerodromiKlijent1 client = new MojiAerodromiKlijent1();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author NWTiS_2
 */
public class MojiAerodromiKlijent1 {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/ahip20_aplikacija_2/rest/";

    public MojiAerodromiKlijent1(String korisnik) {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        String resourcePath = java.text.MessageFormat.format("mojiAerodromi/{0}/prati", new Object[]{korisnik});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    public void setResourcePath(String korisnik) {
        String resourcePath = java.text.MessageFormat.format("mojiAerodromi/{0}/prati", new Object[]{korisnik});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korisnik header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public <T> T dajAerodromeKojePratiKorisnik(Class<T> responseType, String korisnik, String lozinka) throws ClientErrorException {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).header("korisnik", korisnik).header("lozinka", lozinka).get(responseType);
    }

    /**
     * @param responseType Class representing the response
     * @param icao query parameter
     * @return response object (instance of responseType class)@param korisnik header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public <T> T dodajIcaoKorisniku(Class<T> responseType, String icao, String korisnik, String lozinka) throws ClientErrorException {
        String[] queryParamNames = new String[]{"icao"};
        String[] queryParamValues = new String[]{icao};
        ;
        jakarta.ws.rs.core.Form form = getQueryOrFormParams(queryParamNames, queryParamValues);
        jakarta.ws.rs.core.MultivaluedMap<String, String> map = form.asMap();
        for (java.util.Map.Entry<String, java.util.List<String>> entry : map.entrySet()) {
            java.util.List<String> list = entry.getValue();
            String[] values = list.toArray(new String[list.size()]);
            webTarget = webTarget.queryParam(entry.getKey(), (Object[]) values);
        }
        return webTarget.request().header("korisnik", korisnik).header("lozinka", lozinka).post(null, responseType);
    }

    private Form getQueryOrFormParams(String[] paramNames, String[] paramValues) {
        Form form = new jakarta.ws.rs.core.Form();
        for (int i = 0; i < paramNames.length; i++) {
            if (paramValues[i] != null) {
                form = form.param(paramNames[i], paramValues[i]);
            }
        }
        return form;
    }

    public void close() {
        client.close();
    }
    
}