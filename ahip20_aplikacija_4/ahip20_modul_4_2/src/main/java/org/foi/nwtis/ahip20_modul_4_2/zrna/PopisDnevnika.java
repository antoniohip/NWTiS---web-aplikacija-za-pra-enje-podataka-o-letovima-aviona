package org.foi.nwtis.ahip20_modul_4_2.zrna;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.PopisDnevnikaPL;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.PopisKorisnikaPL;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.Dnevnik;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.Korisnik;

@Named(value = "popisDnevnika")
@ApplicationScoped
public class PopisDnevnika {

    @Inject
    HttpServletRequest request;
    @EJB
    PopisDnevnikaPL popisDnevnikaPL;
    @Inject
    ServletContext context;
    @Getter
    @Setter
    private String nazivAerodroma;
    @Getter
    @Setter
    private String korisnik;
    @Getter
    @Setter
    private String filter;
    @Getter
    @Setter
    private String odabraniAerodrom;
    @Getter
    @Setter
    private List<Korisnik> filtrirani = new ArrayList<>();

    private String podrucje = "";

    private int stranica = 10;
    private int pomak = 0;
    private boolean posljednja = false;

    @Getter
    @Setter
    private String vrijemeOd;
    @Getter
    @Setter
    private String vrijemeDo;

    private List<Dnevnik> dnevnik;

    public PopisDnevnika() {
    }

    public String getPodrucje() {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        this.stranica = Integer.parseInt(pbp.dajPostavku("stranica"));
        HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String id = sesija.getAttribute("sesijaId").toString();
        String komanda = "AUTHOR " + kor + " " + id + " pregledDnevnik";
        String odg = popisDnevnikaPL.posaljiAuthor(komanda, stranica);
        vrijemeOd = "";
        vrijemeDo = "";
        dajDnevnik();
        return odg;
    }

    public List<Dnevnik> getDnevnik() {
//        System.out.println("pogled ide po dnevnik.. "+pomak + stranica);
//        this.dnevnik=popisDnevnikaPL.dajDnevnik(pomak, stranica,false);
//        System.out.println("pogled ima  "+this.dnevnik.size());
        return this.dnevnik;
    }

    public List<Dnevnik> dajDnevnik() {
        this.dnevnik = popisDnevnikaPL.dajDnevnik(vrijemeOd, vrijemeDo, pomak, stranica, posljednja);
        return this.dnevnik;
    }

    public void pocetna() {
        this.pomak = 0;
        this.posljednja = false;
        dajDnevnik();

    }

    public void sljedeca() {
        pomak += stranica;
        this.posljednja = false;
        dajDnevnik();
    }

    public void prethodna() {
        pomak -= stranica;
        if (pomak < 0) {
            pomak = 0;
        }
        this.posljednja = false;
        dajDnevnik();

    }

    public void posljednja() {
        pomak = popisDnevnikaPL.dajPosljednjiPomak(stranica, vrijemeOd, vrijemeDo);
        dajDnevnik();

    }

    public String pretvori(long vrijeme) {
        Date d = new Date(vrijeme);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String strDate = dateFormat.format(d);
        return strDate;
    }

    public void postaviFilter() {
        if (vrijemeDo.equals("") && vrijemeOd.equals("")) {
            pomak = 0;
            dajDnevnik();
        }
        if (!provjeriRegVrijeme(vrijemeDo) || !provjeriRegVrijeme(vrijemeOd)) {
            System.out.println("Ne odgovara datum i vrijeme");
            return;
        }
        
        pomak=0;
        dajDnevnik();

    }

    public boolean provjeriRegVrijeme(String datum) {
        String regExSintaksa;
        regExSintaksa = "(0[1-9]|[12][0-9]|3[01])."
                + "(0[1-9]|1[012]).([0-9]{4}) ([0-1]?[0-9]|2?[0-3]):([0-5]\\d):([0-5]\\d)";
        Pattern p = Pattern.compile(regExSintaksa);
        Matcher m = p.matcher(datum);
        return m.matches();
    }

}
