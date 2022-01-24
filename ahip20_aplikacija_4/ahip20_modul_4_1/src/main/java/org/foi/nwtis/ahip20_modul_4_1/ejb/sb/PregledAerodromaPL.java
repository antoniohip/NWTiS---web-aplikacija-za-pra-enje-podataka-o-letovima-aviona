
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
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.AerodromiKlijent1;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.AerodromiKlijent2;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.AerodromiKlijent3;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.Meteo;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.MojiAerodromiKlijent1;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

@Stateless
public class PregledAerodromaPL {
    
    @Inject
    ServletContext context;
    @Inject
    HttpServletRequest request;
    
    private String greska="";
    
    private List<Aerodrom> aerodromi = new ArrayList<>();
    
    
    public String posaljiAuthor(String komanda){
        String odg = posaljiKomandu(komanda);
        if (odg.startsWith("OK")) {
            dajAerodrome();            
            return "";
        } else {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(PregledAerodromaPL.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "index";
        }
    
    }
    public List<Aerodrom> dajSveAerodrome(){
   
        return this.aerodromi;
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
      
       private void dajAerodrome() {
        HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String loz = sesija.getAttribute("loz").toString();
        List<Aerodrom> aerodromi = new ArrayList<>();
        MojiAerodromiKlijent1 mak1=new MojiAerodromiKlijent1(kor);
        Response res = mak1.dajAerodromeKojePratiKorisnik(Response.class, kor, loz);
        if(res.getStatus()==200){
            aerodromi = res.readEntity(new GenericType<List<Aerodrom>>() {
            });
            greska="";
            this.aerodromi=aerodromi;    
        }else{
            greska = res.readEntity(String.class);
        }
                     
    }  

    public List<AvionLeti> dajLetove(String odabraniAerodrom, String datumLeta) {
       SimpleDateFormat primljeni = new SimpleDateFormat("dd.MM.yyyy");
       SimpleDateFormat formatiran=new SimpleDateFormat("yyyy-MM-dd");
       HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String loz = sesija.getAttribute("loz").toString();
       Date date = null;
        try {
            date = primljeni.parse(datumLeta);
        } catch (ParseException ex) {
            Logger.getLogger(PregledAerodromaPL.class.getName()).log(Level.SEVERE, null, ex);
        }
        String datum = formatiran.format(date);

        AerodromiKlijent1 ak1= new AerodromiKlijent1(odabraniAerodrom, datum);
        Response res = ak1.dajLetoveZaIcao(Response.class, kor, loz);
        List<AvionLeti> letovi = new ArrayList<>();
        if(res.getStatus()==200){
            letovi = res.readEntity(new GenericType<List<AvionLeti>>() {
            });
            greska="";
        }
        else{
            greska=res.readEntity(String.class);
        }
        return letovi;   
    
    }
    public List<Meteo> dajVrijemeDan(String odabraniAerodrom, String datumVrijeme) {
       SimpleDateFormat primljeni = new SimpleDateFormat("dd.MM.yyyy");
       SimpleDateFormat formatiran=new SimpleDateFormat("yyyy-MM-dd");
       HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String loz = sesija.getAttribute("loz").toString();
       Date date = null;
        try {
            date = primljeni.parse(datumVrijeme);
        } catch (ParseException ex) {
            Logger.getLogger(PregledAerodromaPL.class.getName()).log(Level.SEVERE, null, ex);
        }
        String datum = formatiran.format(date);
        AerodromiKlijent2 ak2 = new AerodromiKlijent2(odabraniAerodrom, datum);
        Response res = ak2.dajVrijemeDan(Response.class, kor, loz);
        List<Meteo> vrijeme = new ArrayList<>();
        if(res.getStatus()==200){
            vrijeme = res.readEntity(new GenericType<List<Meteo>>() {
            });   
            greska="";
        }   
        else{
            greska=res.readEntity(String.class);
        }
        return vrijeme;   
    
    }

    public Meteo dajNajblizeVrijeme(String odabraniAerodrom, String datum) {
         long vrijeme=0;
        try {
            vrijeme = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(datum).getTime();
        } catch (ParseException ex) {
            Logger.getLogger(PregledAerodromaPL.class.getName()).log(Level.SEVERE, null, ex);
        }
       String strVrijeme=String.valueOf(vrijeme);
     
       HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String loz = sesija.getAttribute("loz").toString();
        AerodromiKlijent3 ak3 = new AerodromiKlijent3(odabraniAerodrom, strVrijeme);
        Response res = ak3.dajOdredenoVrijeme(Response.class, kor, loz);
        Meteo mVrijeme=null;
        if(res.getStatus()==200){
            mVrijeme=res.readEntity(Meteo.class);
            greska="";
        }
        else{
            greska=res.readEntity(String.class);
        }
        return mVrijeme;   
    }
    public String dajGresku(){
        return this.greska;
    }
    
}
