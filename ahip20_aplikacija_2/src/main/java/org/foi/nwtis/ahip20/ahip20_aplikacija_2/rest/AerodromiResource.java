/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.rest;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.AirplanesDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.AirportDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.Meteo;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.MeteoDAO;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.podaci.Odgovor;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author
 */
@Path("aerodromi")
public class AerodromiResource {

    @Inject
    ServletContext context;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrome(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("naziv") String naziv,
            @QueryParam("drzava") String drzava) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {

            AirportDAO adao = new AirportDAO();
            if (naziv == null) {
                naziv = "";
            }
            if (drzava == null) {
                drzava = "";
            }
            List<Aerodrom> aerodromi = adao.dohvatiSveAerodrome(naziv, drzava, pbp);

            return Response
                    .status(Response.Status.OK)
                    .entity(aerodromi)
                    .build();
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }

    }

    @Path("{icao}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrom(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            AirportDAO adao = new AirportDAO();

            Aerodrom a = adao.dohvatiAerodrom(icao, pbp);
            if (a != null) {
                return Response
                        .status(Response.Status.OK)
                        .entity(a)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Ne postoji aerodrom za trazeni ICAO")
                        .build();
            }
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }
    }

    @Path("{icao}/letovi")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajBrojLetovaZaIcao(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            AirportDAO adao = new AirportDAO();

            Aerodrom a = adao.dohvatiAerodrom(icao, pbp);
            if (a != null) {
                AirplanesDAO ardao = new AirplanesDAO();
                int broj = ardao.dohvatiLetoveZaIcao(icao, pbp);
                String brLetova = String.valueOf(broj);
                return Response
                        .status(Response.Status.OK)
                        .entity("Broj letova za " + icao + ": " + brLetova)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Ne postoji aerodrom za trazeni ICAO")
                        .build();
            }
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }

    }

    @Path("{icao}/letovi/{dan}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajLetoveZaIcao(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao,
            @PathParam("dan") String dan) throws ParseException {
        
        Date datum=new SimpleDateFormat("yyyy-MM-dd").parse(dan);
        long poc=datum.getTime();
        long kraj=poc+86400000;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            AirportDAO adao = new AirportDAO();

            Aerodrom a = adao.dohvatiAerodrom(icao, pbp);
            if (a != null) {
                AirplanesDAO ardao = new AirplanesDAO();
                int broj = ardao.dohvatiLetoveZaIcao(icao, pbp);
                List<AvionLeti> al=ardao.dohvatiSveLetoveZaIcao(icao, poc, kraj, pbp);
                if(al.size()==0){
                    return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nisu preuzeti letovi za ovaj dan...")
                        .build();
                }else{
                    return Response
                        .status(Response.Status.OK)
                        .entity(al)
                        .build();
                }
                
                
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Ne postoji aerodrom za trazeni ICAO")
                        .build();
            }
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }

    }
    
    @Path("{icao}/meteoDan/{dan}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajVrijemeDan(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao,
            @PathParam("dan") String dan) throws ParseException {
        
        Date datum=new SimpleDateFormat("yyyy-MM-dd").parse(dan);
        long poc=datum.getTime();
        long kraj=poc+86400000;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            AirportDAO adao = new AirportDAO();

            Aerodrom a = adao.dohvatiAerodrom(icao, pbp);
            if (a != null) {
                MeteoDAO metdao = new MeteoDAO();
                
                List<Meteo> vrijeme=metdao.dohvatiVrijemeZaDan(icao, poc, kraj, pbp);
                if(vrijeme!=null && vrijeme.size()!=0){
                    return Response
                        .status(Response.Status.OK)
                        .entity(vrijeme)
                        .build();
                }else{
                    return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nema vremena za taj aerodrom na ovaj dan")
                        .build();
                }
                
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Ne postoji aerodrom za trazeni ICAO")
                        .build();
            }
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }

    }
    
     @Path("{icao}/meteoVrijeme/{vrijeme}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajOdredenoVrijeme(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao,
            @PathParam("vrijeme") String sVrijeme) throws ParseException {
        
        long vrijeme = Long.parseLong(sVrijeme);
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
     
        if (authOdg.startsWith("OK")) {
            AirportDAO adao = new AirportDAO();

            Aerodrom a = adao.dohvatiAerodrom(icao, pbp);
            if (a != null) {
                MeteoDAO metdao = new MeteoDAO();
                
                Meteo mVrijeme=metdao.dohvatiOdredenoVrijeme(icao,vrijeme, pbp);
                if(mVrijeme!=null){
                    return Response
                        .status(Response.Status.OK)
                        .entity(mVrijeme)
                        .build();
                }else{
                    return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nema vremena za taj aerodrom u ovo vrijeme ili nakon")
                        .build();
                }
                
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Ne postoji aerodrom za trazeni ICAO")
                        .build();
            }
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }

    }

    private String posaljiKomandu(String komanda) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = "localhost";
        System.out.println("Šaljem komandu: " + komanda);
        try (
                Socket uticnica = new Socket(adresa, port); InputStream is = uticnica.getInputStream(); OutputStream os = uticnica.getOutputStream();) {
            System.out.println("Spojen na: " + adresa + ":" + port);
            os.write(komanda.getBytes());
            os.flush();
            uticnica.shutdownOutput();
            StringBuilder text = new StringBuilder();
            while (true) {
                int i = is.read();
                if (i == -1) {
                    break;
                }
                text.append((char) i);
            }
            uticnica.shutdownInput();
            uticnica.close();
            return text.toString();
        } catch (IOException ex) {

        }
        return "ERROR: Ne postoji server na portu: " + port;
    }
    //curl 'http://localhost:8084/ahip20_zadaca_2_1/rest/korisnici' -H 'korisnik:pero' -H 'lozinka:123456'
}
