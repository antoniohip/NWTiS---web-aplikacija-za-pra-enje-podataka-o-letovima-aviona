/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.Korisnik;
import org.foi.nwtis.podaci.Odgovor;

/**
 *
 * @author
 */
@Path("korisnici")
public class KorisniciResource {

    @Inject
    ServletContext context;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnike(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            int idSjednice = dajIdSjednice(authOdg);
            String komanda = "LISTALL " + korisnik + " " + idSjednice;
            String odgovorListAll = posaljiKomandu(komanda);
            System.out.println("Odgovor list all: " + odgovorListAll);
            if (odgovorListAll.startsWith("OK")) {
                List<Korisnik> korisnici = kreirajListu(odgovorListAll);
                return Response
                        .status(Response.Status.OK)
                        .entity(korisnici)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(odgovorListAll)
                        .build();
            }
        }

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(authOdg)
                .build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajKorisnika(String json) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        JsonObject jsonObj = new Gson().fromJson(json, JsonObject.class);
        String korIme = jsonObj.get("korisnik").getAsString();
        String lozinka = jsonObj.get("lozinka").getAsString();
        String prezime = jsonObj.get("prezime").getAsString();
        String ime = jsonObj.get("ime").getAsString();
        String komanda = "ADD " + korIme + " " + lozinka + " \"" + prezime + "\" \"" + ime + "\"";
        String odgovorAdd = posaljiKomandu(komanda);
        
        return Response
                .status(Response.Status.OK)
                .entity(odgovorAdd)
                .build();

    }

    @Path("{korisnik}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnika(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String pKorisnik) {

        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            int idSjednice = dajIdSjednice(authOdg);
            String komanda = "LIST " + korisnik + " " + idSjednice + " " + pKorisnik;
            String odgovorList = posaljiKomandu(komanda);
            System.out.println("Odgovor list: " + odgovorList);
            if (odgovorList.startsWith("OK")) {
                Korisnik k = kreirajKorisnika(odgovorList);
                return Response
                        .status(Response.Status.OK)
                        .entity(k)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(odgovorList)
                        .build();
            }
        }

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(authOdg)
                .build();
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
    //curl 'http://localhost:8084/ahip20_aplikacija_2/rest/korisnici' -H 'korisnik:pero' -H 'lozinka:123456'

    private int dajIdSjednice(String authOdg) {
        String polje[];
        polje = authOdg.split(" ");
        return Integer.parseInt(polje[1]);

    }

    private List<Korisnik> kreirajListu(String odgovorListAll) {

        // OK "pero\tKos\tPero" "mato\tMedved\tMato“
        List<Korisnik> korisnici = new ArrayList<Korisnik>();
        String odrezi = odgovorListAll.substring(3, odgovorListAll.length());
        String polje[];
        Korisnik k = null;
        polje = odrezi.split(" ");
        for (int i = 0; i < polje.length; i++) {
            String bezNavodnika = polje[i].substring(1, polje[i].length() - 1);
            String podaci[];
            podaci = bezNavodnika.split("\t");
            for (int j = 0; j < podaci.length; j++) {
                String korime = podaci[0];
                String prezime = podaci[1];
                String ime = podaci[2];
                k = new Korisnik(korime, prezime, ime);
            }

            korisnici.add(k);
        }
        return korisnici;

    }

    private Korisnik kreirajKorisnika(String odgovorList) {
        String odrezi = odgovorList.substring(4, odgovorList.length() - 1);
        String polje[];
        polje = odrezi.split("\t");
        String kor = polje[0];
        String prezime = polje[1];
        String ime = polje[2];
        Korisnik k = new Korisnik(kor, prezime, ime);
        return k;
    }
}
