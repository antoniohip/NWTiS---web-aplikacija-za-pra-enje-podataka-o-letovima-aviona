package org.foi.nwtis.ahip20.ahip20_aplikacija_3.controller;

import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.AerodromiKlijent_1;
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.Korisnik;
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.MojiAerodromiKlijent_1;
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.MojiAerodromiKlijent_2;
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.MojiAerodromiKlijent_3;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;

@Path("upravljanjeAerodromima")
@Controller

public class KorisniciKontroler_5 {

    @Inject
    ServletContext context;
    @Inject
    private Models model;
    @Inject
    HttpServletRequest request;
    private List<Aerodrom> korisnikovi=new ArrayList<>();

    @QueryParam("akcija")
    String akcija;
    @QueryParam("aerodrom")
    String icao;
    @QueryParam("naziv")
    String naziv;
    @QueryParam("drzava")
    String drzava;
    @QueryParam("sviAerodromi")
    String sviAerodromiIcao;
    

 
    @GET
    public String upravljanjeAerodromima() {
        HttpSession sesija = request.getSession();
        String korisnik = sesija.getAttribute("korIme").toString();
        String lozinka = sesija.getAttribute("lozinka").toString();
        System.out.println("SESIJA: " + korisnik + " " + lozinka);
        String komandaAuth = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(komandaAuth);
        if (authOdg.startsWith("OK")) {
            int sjednicaId = dajIdSjednice(authOdg);
            String komandaProvjeriPodrucje = "AUTHOR " + korisnik + " " + sjednicaId + " administracijaAerodroma";
            String authorOdg = posaljiKomandu(komandaProvjeriPodrucje);
            if (authorOdg.startsWith("OK")) {
                return obradiPogled(korisnik, lozinka);
            } else {
                model.put("porukaIzbornik", authorOdg);
                return "izbornik.jsp";
            }

        } else {
            model.put("porukaIzbornik", "Niste autorizirani");
            return "izbornik.jsp";
        }
    }

    private String obradiPogled(String korisnik, String lozinka) {

        MojiAerodromiKlijent_1 mak1 = new MojiAerodromiKlijent_1(korisnik);
        Response res = mak1.dajAerodromeKojePratiKorisnik(Response.class, korisnik, lozinka);
        List<Aerodrom> korisnikoviAerodromi=new ArrayList<>();
        if (res.getStatus() == 200) {
            korisnikoviAerodromi = res.readEntity(new GenericType<List<Aerodrom>>() {
            });
            korisnikovi=korisnikoviAerodromi;
            model.put("aerodromi", korisnikoviAerodromi);
        } else {
            model.put("upravljanjeAerodromimaGreska", res.readEntity(String.class));
        }
        AerodromiKlijent_1 ak1 = new AerodromiKlijent_1();
        Response res2 = ak1.dajAerodrome(Response.class, "", "", korisnik, lozinka);
        if (res2.getStatus() == 200) {
            List<Aerodrom> sviAerodromi = res2.readEntity(new GenericType<List<Aerodrom>>() {
            });
            sviAerodromi=izbrisiPostojece(korisnikoviAerodromi, sviAerodromi);
            model.put("sviAerodromi", sviAerodromi);

        } else {
            model.put("upravljanjeAerodromimaGreska", res.readEntity(String.class));
        }

        return "upravljanjeAerodromima.jsp";

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

    @GET
    @Path("zahtjev")
    public String obradiZahtjev() {
        boolean filter = false;
        HttpSession s = request.getSession();
        String korisnik = s.getAttribute("korIme").toString();
        String lozinka = s.getAttribute("lozinka").toString();
        if (akcija.equals("dajKorisnike")) {
            MojiAerodromiKlijent_2 mak2 = new MojiAerodromiKlijent_2(icao);
            Response res = mak2.dajKorisnikeKojiPrateIcao(Response.class, korisnik, lozinka);
            List<Korisnik> korisniciKojiPrate = null;
            if (res.getStatus() == 200) {
                korisniciKojiPrate = res.readEntity(new GenericType<List<Korisnik>>() {
                });
                model.put("korisnici", korisniciKojiPrate);
            } else {
                model.put("upravljanjeAerodromimaGreska", res.readEntity(String.class));
            }
        } else if (akcija.equals("izbrisi")) {
            String naziv1 = "";
            List<Aerodrom> aero = dohvatiAerodrome(korisnik, lozinka);
            for (Aerodrom a : aero) {
                if (a.getIcao().equals(icao)) {
                    naziv1 = a.getNaziv();
                    break;
                }
            }
            MojiAerodromiKlijent_3 mak3 = new MojiAerodromiKlijent_3(korisnik, icao);
            Response res = mak3.izbrisiKorisnikuIcao(Response.class, korisnik, lozinka);
            String odg = res.readEntity(String.class);
            korisnikovi.removeIf(icao -> icao.getIcao().equals(this.icao));
            if (odg.startsWith("Obrisan")) {
                model.put("upravljanjeAerodromimaGreska", "Više ne pratite " + icao + " - " + naziv1);
            }

        } else if(akcija.equals("prati")){
            MojiAerodromiKlijent_1 mak1 = new MojiAerodromiKlijent_1(korisnik);
            Response res = mak1.dodajIcaoKorisniku(Response.class, sviAerodromiIcao, korisnik, lozinka);
            korisnikovi=dohvatiAerodrome(korisnik, lozinka);
            List<Aerodrom> svi = dohvatiSve(korisnik, lozinka);
            svi=izbrisiPostojece(korisnikovi, svi);
            String aerodrom="";
            for(Aerodrom a:svi){
                if(a.getIcao().equals(sviAerodromiIcao)){
                    aerodrom=a.getNaziv();
                    break;
                }
            }
            String odg=res.readEntity(String.class);
            String dodavanje="";
            if(odg.startsWith("Korisniku")){
                dodavanje="Korisniku je dodan "+sviAerodromiIcao+" - "+aerodrom;
                model.put("upravljanjeAerodromimaGreska", dodavanje);
            }
            else{
                model.put("upravljanjeAerodromimaGreska",odg);
            }  
            
        }
        else if (akcija.equals("filter")) {
            filter = true;
            AerodromiKlijent_1 ak1 = new AerodromiKlijent_1();
            Response res3 = ak1.dajAerodrome(Response.class, naziv, drzava, korisnik, lozinka);
            if (res3.getStatus() == 200) {
                List<Aerodrom> filtrirani = res3.readEntity(new GenericType<List<Aerodrom>>() {
                });
                filtrirani=izbrisiPostojece(korisnikovi, filtrirani);
                model.put("sviAerodromi", filtrirani);
            }

        }else{
                                
        }
        if (!filter) {
            List<Aerodrom> sviAerodromi = dohvatiSve(korisnik, lozinka);
            if (sviAerodromi == null) {
                model.put("upravljanjeAerodromimaGreska", "Nema preostalih zahtjeva");

            } else {
                model.put("sviAerodromi", sviAerodromi);
            }
        }
        List<Aerodrom> aerodromi = dohvatiAerodrome(korisnik, lozinka);
        if (aerodromi == null) {
            model.put("upravljanjeAerodromimaGreska", "Nema preostalih zahtjeva");
            return "upravljanjeAerodromima.jsp";
        } else {
            model.put("aerodromi", aerodromi);
            return "upravljanjeAerodromima.jsp";
        }
    }

    private List<Aerodrom> dohvatiAerodrome(String korisnik, String lozinka) {
        MojiAerodromiKlijent_1 mak = new MojiAerodromiKlijent_1(korisnik);

        Response res = mak.dajAerodromeKojePratiKorisnik(Response.class, korisnik, lozinka);
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (res.getStatus() == 200) {
            aerodromi = res.readEntity(new GenericType<List<Aerodrom>>() {
            });
            this.korisnikovi=aerodromi;
            return aerodromi;

        }
        return null;
    }

    private List<Aerodrom> dohvatiSve(String korisnik, String lozinka) {
        AerodromiKlijent_1 ak1 = new AerodromiKlijent_1();
        Response res = ak1.dajAerodrome(Response.class, "", "", korisnik, lozinka);
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (res.getStatus() == 200) {
            aerodromi = res.readEntity(new GenericType<List<Aerodrom>>() {
            });
            aerodromi=izbrisiPostojece(korisnikovi, aerodromi);
            return aerodromi;

        }
        return null;
    }
    
    private List<Aerodrom> izbrisiPostojece(List<Aerodrom> korisnikoviAerodromi, List<Aerodrom> sviAerodromi){
        List<Aerodrom> novi=new ArrayList();
         novi = sviAerodromi.stream().filter(a -> !this.postojiAerodromUKorAerodromima(a, korisnikoviAerodromi)).collect(Collectors.toList());
         return novi;
    }
    
     private boolean postojiAerodromUKorAerodromima(Aerodrom a, List<Aerodrom> korisnikoviAerodromi) {
        for (Aerodrom ak : korisnikoviAerodromi) {
            if (a.getIcao().equals(ak.getIcao())) {
                return true;
            }
        }
        return false;

    }

}
