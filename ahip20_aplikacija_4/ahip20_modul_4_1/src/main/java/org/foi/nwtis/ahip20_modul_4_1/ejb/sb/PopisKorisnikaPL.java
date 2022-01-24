
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.KorisniciKlijent1;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.Korisnik;

@Stateless
public class PopisKorisnikaPL {
    
    @Inject
    ServletContext context;
    @Inject
    HttpServletRequest request;
    
    private List<Korisnik> korisnici = new ArrayList<>();
    private List<Korisnik> filtrirani = new ArrayList<>();
    
    
    public String posaljiAuthor(String komanda){
        String odg = posaljiKomandu(komanda);
        if (odg.startsWith("OK")) {
            dajKorisnike();
            
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
    public List<Korisnik> dajSveKorisnike(){
        return this.korisnici;
    }
     public List<Korisnik> dajFiltrirane(){
        return this.filtrirani;
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
      
       private void dajKorisnike() {
        List<Korisnik> korisnici = new ArrayList<>();
        KorisniciKlijent1 kk1 = new KorisniciKlijent1();
        HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String loz = sesija.getAttribute("loz").toString();
        Response res = null;

        res = kk1.dajKorisnike(Response.class, kor, loz);

        if (res.getStatus() == 200) {
            korisnici = res.readEntity(new GenericType<List<Korisnik>>() {
            });
            this.korisnici = korisnici;
            this.filtrirani=korisnici;

        }
    }
    
    
}
