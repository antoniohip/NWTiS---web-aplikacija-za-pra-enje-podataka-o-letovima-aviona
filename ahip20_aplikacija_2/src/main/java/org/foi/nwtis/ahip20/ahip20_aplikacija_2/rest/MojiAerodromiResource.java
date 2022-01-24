/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.rest;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.DELETE;
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
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.AirportDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.MyAirportDAO;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.Korisnik;

/**
 *
 * @author NWTiS_2
 */
@Path("mojiAerodromi")
public class MojiAerodromiResource {

    @Inject
    ServletContext context;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrome(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            MyAirportDAO madao = new MyAirportDAO();
            List<Aerodrom> aerodromi = madao.dohvatiSveAerodrome(pbp);
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

    @Path("{icao}/prate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnikeKojiPrateIcao(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            int sjednicaId = dajIdSjednice(authOdg);
            MyAirportDAO adao = new MyAirportDAO();
            String korisniciPopis = adao.dohvatiKorisnikeZaIcao(icao, pbp);
            if (!korisniciPopis.equals("")) {
                List<Korisnik> korisnici = new ArrayList<>();
                String polje[];
                polje = korisniciPopis.split(";");
                for (int i = 0; i < polje.length; i++) {

                    String komanda = "LIST " + korisnik + " " + sjednicaId + " " + polje[i];
                    String odgovorList = posaljiKomandu(komanda);
                    
                    if (odgovorList.startsWith("OK")) {
                        String odrezi = odgovorList.substring(4, odgovorList.length() - 1);
                        String polje1[];
                        polje1 = odrezi.split("\t");
                        String kor = polje1[0];
                        String prezime = polje1[1];
                        String ime = polje1[2];

                        Korisnik k = new Korisnik(kor, prezime, ime);
                        korisnici.add(k);
                    } else {
                        return Response
                                .status(Response.Status.NOT_FOUND)
                                .entity(odgovorList)
                                .build();
                    }
                }

                return Response
                        .status(Response.Status.OK)
                        .entity(korisnici)
                        .build();

            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Ne postoje korisnici koji prate aerodrom za trazeni ICAO")
                        .build();
            }

        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }

    }

    @Path("{korisnik}/prati")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodromeKojePratiKorisnik(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String korIme) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {

            MyAirportDAO madao = new MyAirportDAO();
            List<Aerodrom> aerodromi = madao.dohvatiAerodromeZaKorisnike(korIme, pbp);
            if (aerodromi != null) {
                return Response
                        .status(Response.Status.OK)
                        .entity(aerodromi)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Ne postoje aerodromi koje prati trazeni korisnik")
                        .build();
            }

        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }
    }

    @Path("{korisnik}/prati")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajIcaoKorisniku(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String korIme,
            @QueryParam("icao") String icao) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            AirportDAO adao = new AirportDAO();
            Aerodrom a = adao.dohvatiAerodrom(icao, pbp);
            if (a == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Ne postoji ICAO")
                        .build();
            }

            MyAirportDAO madao = new MyAirportDAO();
            boolean vecPostoji = madao.provjeriPostojiLiZapis(korIme, icao, pbp);
            if (!vecPostoji) {
                if (madao.unesiIcaoKorisniku(korIme, icao, pbp)) {
                    return Response
                            .status(Response.Status.OK)
                            .entity("Korisniku je dodan aerodrom")
                            .build();
                }
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Greška prilikom dodavanja aerodroma korisniku!")
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Korsinik vec prati ovaj aerodrom")
                        .build();
            }

        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(authOdg)
                    .build();
        }

    }

    @Path("{korisnik}/prati/{icao}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public Response izbrisiKorisnikuIcao(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String korIme,
            @PathParam("icao") String icao) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String autentifikacija = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(autentifikacija);
        System.out.println("Odgovor je: " + authOdg);
        if (authOdg.startsWith("OK")) {
            MyAirportDAO madao = new MyAirportDAO();
            boolean postoji = madao.provjeriPostojiLiZapis(korIme, icao, pbp);
            if (postoji) {
                boolean odgovor = madao.izbrisiKorisnikuIcao(korIme, icao, pbp);
                if (odgovor) {
                    return Response
                            .status(Response.Status.OK)
                            .entity("Obrisan ICAO za korisnika")
                            .build();
                } else {
                    return Response
                            .status(Response.Status.OK)
                            .entity("Greska prilikom brisanja")
                            .build();
                }
            }else{
                 return Response
                            .status(Response.Status.OK)
                            .entity("Korisnik ne prati ovaj aerodrom")
                            .build();
            }

        } else {
            return Response
                    .status(Response.Status.OK)
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

    private int dajIdSjednice(String authOdg) {
        String polje[];
        polje = authOdg.split(" ");
        return Integer.parseInt(polje[1]);

    }

}
