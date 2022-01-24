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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.Dnevnik;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.DnevnikDAO;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.Korisnik;
import org.foi.nwtis.podaci.Odgovor;

/**
 *
 * @author
 */
@Path("dnevnik")
public class DnevnikResource {

    @Inject
    ServletContext context;

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajZapisDnevnika(String json) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        JsonObject jsonObj = new Gson().fromJson(json, JsonObject.class);
        String korIme = jsonObj.get("korisnik").getAsString();
        String zahtjev = jsonObj.get("zahtjev").getAsString();
        String odgovor = jsonObj.get("odgovor").getAsString();
        Timestamp vrijemeZahtjeva = new Timestamp(jsonObj.get("vrijemeZahtjeva").getAsLong());
        DnevnikDAO ddao = new DnevnikDAO();
        boolean unos = ddao.unesiZapis(korIme, zahtjev, odgovor, vrijemeZahtjeva, pbp);
        String odg = "";
        if (unos) {
            odg = "Uspješno dodan zapis";
        } else {
            odg = "Pogreška prilikom dodavanja zapisa";
        }

        return Response
                .status(Response.Status.OK)
                .entity(odg)
                .build();

    }

    @Path("{korisnik}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajZapise(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("odVrijeme") String odVrijeme,
            @QueryParam("doVrijeme") String doVrijeme,
            @QueryParam("pomak") String pomak,
            @QueryParam("stranica") String stranica) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            if (doVrijeme == null) {
                doVrijeme = "";
            }
            if (odVrijeme == null) {
                odVrijeme = "";
            }
            if (pomak == null) {
                pomak = "";
            }
            if (stranica == null) {
                stranica = "";
            }
            List<Dnevnik> zapisi = null;
            DnevnikDAO ddao = new DnevnikDAO();
            zapisi = ddao.dohvatiZapise(korisnik, odVrijeme, doVrijeme, pomak, stranica, pbp);
            return Response
                    .status(Response.Status.OK)
                    .entity(zapisi)
                    .build();
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }
    }
    
    @Path("{korisnik}/broj")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajBrojZapisa(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("odVrijeme") String odVrijeme,
            @QueryParam("doVrijeme") String doVrijeme,
            @PathParam("korisnik") String korIme) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        if (authOdg.startsWith("OK")) {
            if (doVrijeme == null) {
                doVrijeme = "";
            }
            if (odVrijeme == null) {
                odVrijeme = "";
            }           
            
            DnevnikDAO ddao = new DnevnikDAO();
            int brojZapisa=0;
            brojZapisa = ddao.dohvatiBrojZapisa(korIme, odVrijeme, doVrijeme, pbp);
            return Response
                    .status(Response.Status.OK)
                    .entity(brojZapisa)
                    .build();
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
