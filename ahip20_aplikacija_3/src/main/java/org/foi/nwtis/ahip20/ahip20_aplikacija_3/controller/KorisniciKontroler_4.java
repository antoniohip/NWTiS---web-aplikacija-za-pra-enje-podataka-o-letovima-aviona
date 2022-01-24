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
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.KorisniciKlijent_1;
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.Korisnik;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

@Path("dodavanjePodrucja")
@Controller
public class KorisniciKontroler_4 {

    @Inject
    ServletContext context;
    @Inject
    private Models model;
    @Inject
    HttpServletRequest request;

    @QueryParam("akcija")
    String akcija;
    @QueryParam("korisnik")
    String formaKorisnik;
    @QueryParam("podrucje")
    String podrucje;

    /**
     * Metoda služi za provjeru korisnika kod prijave. Korisničko ime i lozinka
     * iz forme šalje se na rest servis te se provjerava postoji li takav
     * korisnik u bazi. Ako je odgovor servisa "OK", prijava je uspješna,
     * korisnik se vraća na početnu stranicu, a ako odgovor nije "OK" otvara se
     * stranica Greška
     *
     * @return
     */
    @GET
    public String provjeriAuthor() {
        HttpSession sesija = request.getSession();
        String korisnik = sesija.getAttribute("korIme").toString();
        String lozinka = sesija.getAttribute("lozinka").toString();
        System.out.println("SESIJA: " + korisnik + " " + lozinka);
        String komandaAuth = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(komandaAuth);
        if (authOdg.startsWith("OK")) {
            int sjednicaId = dajIdSjednice(authOdg);
            String komandaProvjeriPodrucje = "AUTHOR " + korisnik + " " + sjednicaId + " administracija";
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
        KorisniciKlijent_1 kk1 = new KorisniciKlijent_1();
        Response res = kk1.dajKorisnike(Response.class, korisnik, lozinka);
        List<Korisnik> korisnici = new ArrayList<>();

        if (res.getStatus() == 200) {
            korisnici = res.readEntity(new GenericType<List<Korisnik>>() {
            });
            model.put("korisnici", korisnici);
            return "dodavanjePodrucja.jsp";
        } else {
            String odg = res.readEntity(String.class);
            String poruka = "";
            if (odg.contains("podru")) {
                poruka = "Nemate ovlasti za ovaj pogled";
            }
            if (odg.contains("zahtjev")) {
                poruka = "Nemate više zahtjeva";
            }
            model.put("dohvacanjeKorisnikaGreska", poruka);
            return "dodavanjePodrucja.jsp";
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

    @GET
    @Path("zahtjev")
    public String obradiZahtjev() {
        HttpSession s = request.getSession();
        String korisnik = s.getAttribute("korIme").toString();
        String lozinka = s.getAttribute("lozinka").toString();
        int sId=Integer.parseInt(s.getAttribute("id").toString());
        if(akcija.equals("dodaj")){
            String komanda = "GRANT "+korisnik+" "+sId+" "+podrucje+" "+formaKorisnik;
            String grantOdg=posaljiKomandu(komanda);
            String poruka="";
            System.out.println("Odgovor je: "+grantOdg);
            if(grantOdg.startsWith("OK")){
                poruka="Korisniku je uspješno dodano područje";
            }
            else{
                poruka=grantOdg;
            }
            model.put("dohvacanjeKorisnikaGreska",poruka);
        }else{
            String komanda = "REVOKE "+korisnik+" "+sId+" "+podrucje+" "+formaKorisnik;
            String revokeOdg=posaljiKomandu(komanda);
            String poruka="";
            System.out.println("Odgovor: "+revokeOdg);
            if(revokeOdg.startsWith("OK")){
                poruka="Korisniku je uspješno izbrisano područje";
            }
            else{
                poruka=revokeOdg;
            }
            model.put("dohvacanjeKorisnikaGreska",poruka);
        }
        List<Korisnik> korisnici = dohvatiKorisnike(korisnik,lozinka);
        if (korisnici == null){
            model.put("dohvacanjeKorisnikaGreska", "Nema preostalih zahtjeva");
            return "dodavanjePodrucja.jsp";
        }else{
            model.put("korisnici",korisnici);
           return "dodavanjePodrucja.jsp"; 
        }
        
    }

    private List<Korisnik> dohvatiKorisnike(String korisnik, String lozinka) {
        KorisniciKlijent_1 kk1 = new KorisniciKlijent_1();
        
        Response res = kk1.dajKorisnike(Response.class, korisnik, lozinka);
        List<Korisnik> korisnici = new ArrayList<>();
        if (res.getStatus() == 200) {
            korisnici = res.readEntity(new GenericType<List<Korisnik>>() {
            });
            return korisnici;

        }
        return null;
    }

}
