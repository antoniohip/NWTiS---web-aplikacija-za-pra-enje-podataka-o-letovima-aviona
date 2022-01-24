package org.foi.nwtis.ahip20_modul_4_2.zrna;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.PregledAerodromaPL;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.Meteo;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

@Named(value = "pregledAerodroma")
@ApplicationScoped
public class PregledAerodroma {

    @EJB
    PregledAerodromaPL pregledAerodromaPL;
    @Inject
    HttpServletRequest request;
    @Getter
    @Setter
    private List<Aerodrom> aerodromi;
    @Getter
    @Setter
    private List<Meteo> svaVremena;
    @Getter
    @Setter
    private List<AvionLeti> letovi;
    @Getter
    @Setter
    private Meteo najblizeVrijeme;
    @Getter
    @Setter
    private String datumLeta = "";
    @Getter
    @Setter
    private String datumVrijeme = "";
    @Getter
    @Setter
    private String odabraniAerodrom="";
    @Getter
    @Setter
    private int prikaziLetove = 0;
    @Getter
    @Setter
    private int prikaziVrijeme = 0;
    @Getter
    @Setter
    private int prikaziNajblizeVrijeme = 0;
    private String podrucje = "";

    private String greska = "";

    public String getPodrucje() {
        HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String id = sesija.getAttribute("sesijaId").toString();
        String komanda = "AUTHOR " + kor + " " + id + " pregledAerodroma";
        String odg = pregledAerodromaPL.posaljiAuthor(komanda);
        dajAerodrome();
        return odg;
    }

    public List<Aerodrom> getAerodromi() {
        dajAerodrome();
        return this.aerodromi;
    }

    public String getGreska() {
        return this.greska;
    }

    public void dajAerodrome() {
        this.aerodromi = pregledAerodromaPL.dajSveAerodrome();
        this.greska = pregledAerodromaPL.dajGresku();
    }

    public void dohvatiLetove() {
        if(odabraniAerodrom==null) return;
        if (odabraniAerodrom.equals("") || datumLeta.equals("")) {
            prikaziLetove = 0;
            prikaziVrijeme = 0;
            return;
        }
        if (provjeriReg(datumLeta)) {
            this.letovi = pregledAerodromaPL.dajLetove(odabraniAerodrom, datumLeta);
            this.greska = pregledAerodromaPL.dajGresku();
            prikaziLetove = 1;
            prikaziVrijeme = 0;
            prikaziNajblizeVrijeme = 0;
        }
    }

    public void dohvatiVrijemeDan() {
        if(odabraniAerodrom==null)return;
        if (odabraniAerodrom.equals("") || datumLeta.equals("")) {
            prikaziLetove = 0;
            prikaziVrijeme = 0;
            return;
        }
        if (provjeriReg(datumLeta)) {
            this.svaVremena = pregledAerodromaPL.dajVrijemeDan(odabraniAerodrom, datumLeta);
            this.greska = pregledAerodromaPL.dajGresku();
            prikaziLetove = 0;
            prikaziNajblizeVrijeme = 0;
            prikaziVrijeme = 1;
        } 
    }

    public String pretvori(long vrijeme) {
        Date d = new Date(vrijeme);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String strDate = dateFormat.format(d);
        return strDate;
    }

    public String pretvoriSeen(long vrijeme) {
        Date d = new Date(vrijeme * 1000);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String strDate = dateFormat.format(d);
        return strDate;
    }

    public void dohvatiVrijeme() {
        if(odabraniAerodrom==null) return;
        if (datumVrijeme.equals("")) {
            prikaziNajblizeVrijeme = 0;
            return;
        }
        if (provjeriRegVrijeme(datumVrijeme)) {
            this.najblizeVrijeme = pregledAerodromaPL.dajNajblizeVrijeme(odabraniAerodrom, datumVrijeme);
            this.greska = pregledAerodromaPL.dajGresku();
            prikaziNajblizeVrijeme = 1;
        }
    }

    public boolean provjeriReg(String datum) {
        String regExSintaksa;
        regExSintaksa = "(0[1-9]|[12][0-9]|3[01]).(0[1-9]|1[012]).([0-9]{4})";
        Pattern p = Pattern.compile(regExSintaksa);
        Matcher m = p.matcher(datum);
        return m.matches();
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
