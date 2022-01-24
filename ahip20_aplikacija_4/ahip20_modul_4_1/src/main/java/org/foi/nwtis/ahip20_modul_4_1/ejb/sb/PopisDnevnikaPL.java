/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20_modul_4_1.ejb.sb;

import jakarta.ejb.Stateless;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Math.floor;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.Dnevnik;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.DnevnikKlijent1;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.DnevnikKlijent2;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.KorisniciKlijent1;

@Stateless
public class PopisDnevnikaPL {

    @Inject
    ServletContext context;
    @Inject
    HttpServletRequest request;

    private List<Dnevnik> dnevnik = new ArrayList<>();
    private List<Dnevnik> filtrirani = new ArrayList<>();

    public String posaljiAuthor(String komanda, int stranica) {

        String odg = posaljiKomandu(komanda);
        if (odg.startsWith("OK")) {
            dajDnevnik("", "", 0, stranica, false);

            return "";
        } else {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(PopisKorisnikaPL.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "index";
        }

    }

    public List<Dnevnik> dajDnevnik(String vrijemeOd, String vrijemeDo, int pomak, int stranica, boolean posljednja) {
        HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String loz = sesija.getAttribute("loz").toString();
        String strVrijemeOd="";
        String strVrijemeDo ="";
        if (!vrijemeOd.equals("") && !vrijemeDo.equals("")) {
            long vrijeme1 = 0;
            try {
                vrijeme1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(vrijemeOd).getTime();
            } catch (ParseException ex) {
                Logger.getLogger(PregledAerodromaPL.class.getName()).log(Level.SEVERE, null, ex);
            }
            strVrijemeOd = String.valueOf(vrijeme1);
            long vrijeme2 = 0;
            try {
                vrijeme2 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(vrijemeDo).getTime();
            } catch (ParseException ex) {
                Logger.getLogger(PregledAerodromaPL.class.getName()).log(Level.SEVERE, null, ex);
            }
            strVrijemeDo = String.valueOf(vrijeme2);
        }

        if (pomak < 0) {
            pomak = 0;
        }
        List<Dnevnik> dnevnik = new ArrayList<>();
        String strPomak = String.valueOf(pomak);
        String strStranica = String.valueOf(stranica);
        Response res = null;
        System.out.println("Idem po dnevnik. pomak, str: " + strPomak + " " + strStranica+" "+
                strVrijemeOd+" "+strVrijemeDo);

        DnevnikKlijent1 dk1 = new DnevnikKlijent1(kor);
        res = dk1.dajZapise(Response.class, strVrijemeOd, strVrijemeDo, strPomak, strStranica, kor, loz);
        if (res.getStatus() == 200) {
            dnevnik = res.readEntity(new GenericType<List<Dnevnik>>() {
            });
            this.dnevnik = dnevnik;

        }
        return dnevnik;

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

    public int dajPosljednjiPomak(int stranica, String vrijemeOd, String vrijemeDo) {
        HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String loz = sesija.getAttribute("loz").toString();
         String strVrijemeOd="";
        String strVrijemeDo ="";
        if (!vrijemeOd.equals("") && !vrijemeDo.equals("")) {
            long vrijeme1 = 0;
            try {
                vrijeme1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(vrijemeOd).getTime();
            } catch (ParseException ex) {
                Logger.getLogger(PregledAerodromaPL.class.getName()).log(Level.SEVERE, null, ex);
            }
            strVrijemeOd = String.valueOf(vrijeme1);
            long vrijeme2 = 0;
            try {
                vrijeme2 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(vrijemeDo).getTime();
            } catch (ParseException ex) {
                Logger.getLogger(PregledAerodromaPL.class.getName()).log(Level.SEVERE, null, ex);
            }
            strVrijemeDo = String.valueOf(vrijeme2);
        }

        DnevnikKlijent2 dk2 = new DnevnikKlijent2(kor);
        Response res = dk2.dajBrojZapisa(Response.class, strVrijemeOd, strVrijemeDo, kor, loz);
        int brojZapisa = Integer.parseInt(res.readEntity(String.class));
        double brs = floor(brojZapisa / stranica);
        int brojStr = (int) brs;
        int pomak = brojStr * stranica;
        return pomak;
    }

}
