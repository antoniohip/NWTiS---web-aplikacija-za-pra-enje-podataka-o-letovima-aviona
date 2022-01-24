package org.foi.nwtis.ahip20_modul_4_2.zrna;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import org.foi.nwtis.podaci.Aerodrom;

@Named(value = "index")
@ApplicationScoped
public class Index {

    @Inject
    ServletContext context;
     @Inject
    HttpServletRequest request;
    @Getter
    @Setter
    private String lozinka;
    @Getter
    @Setter
    private String korisnik;
    @Getter
    @Setter
    private String porukaPrijave = "";
    @Getter
    @Setter
    private int prikazi = 0;

    private String idSjednice = "";

    private List<Aerodrom> sviAerodromi = null;

    public Index() {
    }

    public String prijava() {
        String komanda = "AUTHEN " + this.korisnik + " " + this.lozinka;
        String odg = posaljiKomandu(komanda);
        if (odg.startsWith("OK")) {
            dohvatiId(odg);
            HttpSession sesija = request.getSession();
            sesija.setAttribute("korisnickoIme", this.korisnik);
            sesija.setAttribute("loz", this.lozinka);
            sesija.setAttribute("sesijaId", this.idSjednice);
            this.prikazi = 1;
            this.porukaPrijave = "";
        } else {
            this.porukaPrijave = "Neuspješna prijava...";
        }
        return "";
    }

    public String odjava() {
        String komanda = "LOGOUT " + this.korisnik + " " + this.idSjednice;
        String odg = posaljiKomandu(komanda);
        this.prikazi = 0;
        this.porukaPrijave = "";
        return "";
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

    private void dohvatiId(String odg) {
        String polje[];
        polje = odg.split(" ");
        this.idSjednice = polje[1];
    }

}
