package org.foi.nwtis.ahip20.ahip20_aplikacija_3.controller;

import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

@Path("prijavaKorisnika")
@Controller
public class KorisniciKontroler_3 {

    @Inject
    ServletContext context;
    @Inject
    private Models model;
    @Inject
    HttpServletRequest request;

    @FormParam("korisnik")
    String korisnik;
    @FormParam("lozinka")
    String lozinka;

    /**
     * Metoda služi za provjeru korisnika kod prijave. Korisničko ime i lozinka
     * iz forme šalje se na rest servis te se provjerava postoji li takav
     * korisnik u bazi. Ako je odgovor servisa "OK", prijava je uspješna,
     * korisnik se vraća na početnu stranicu, a ako odgovor nije "OK" otvara se
     * stranica Greška
     *
     * @return
     */
    @POST
    public String prijavaKorisnika() {
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String authOdg = posaljiKomandu(komanda);
        if (authOdg.startsWith("OK")) {
            HttpSession sesija = request.getSession();
            sesija.setAttribute("korIme", korisnik);
            sesija.setAttribute("lozinka", lozinka);
            int sId=dajIdSjednice(authOdg);
            sesija.setAttribute("id", sId);
            System.out.println("Prijava uspjesna!");
            model.put("prijavaPoruka", "Uspješna prijava");
            return "izbornik.jsp";
        } else {
            model.put("prijavaPoruka", "Neuspješna prijava");
            return "korisnikPrijava.jsp";
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
