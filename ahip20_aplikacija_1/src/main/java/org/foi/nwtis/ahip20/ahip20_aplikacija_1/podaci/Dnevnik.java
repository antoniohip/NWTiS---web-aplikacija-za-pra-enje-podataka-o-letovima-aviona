package org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci;

import java.sql.Timestamp;


public class Dnevnik {
    private int id;
    private String korisnik;
    private String zahtjev;
    private String odgovor;
    private Timestamp vrijemeZahtjeva;

    public Dnevnik(int id,String korisnik, String zahtjev, String odgovor, Timestamp vrijemeZahtjeva) {
        this.id=id;
        this.korisnik = korisnik;
        this.zahtjev = zahtjev;
        this.odgovor = odgovor;
        this.vrijemeZahtjeva=vrijemeZahtjeva;
    }

    public Dnevnik() {
    }

    public Dnevnik(String korisnik, String zahtjevDnevnik, String dnevnikOdg, Timestamp vrijemePrimitka) {
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getVrijemeZahtjeva() {
        return vrijemeZahtjeva;
    }

    public void setVrijemeZahtjeva(Timestamp vrijemeZahtjeva) {
        this.vrijemeZahtjeva = vrijemeZahtjeva;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getZahtjev() {
        return zahtjev;
    }

    public void setZahtjev(String zahtjev) {
        this.zahtjev = zahtjev;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }
    
    
    
}
