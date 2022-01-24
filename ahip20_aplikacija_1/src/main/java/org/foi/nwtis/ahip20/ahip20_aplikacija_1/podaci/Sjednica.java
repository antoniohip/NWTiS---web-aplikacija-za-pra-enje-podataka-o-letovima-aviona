package org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci;

public class Sjednica {

    

    private int id;
    private String korisnik="";
    private long vrijemeKreiranja;
    private long vrijemeDoKadaVrijedi;
    private int brojZahtjeva;
    private boolean aktivna;

    public Sjednica(int id, String korisnik, long vrijemeKreiranja, long vrijemeDoKadaVrijedi, int brojZahtjeva, boolean aktivna) {
        this.id = id;
        this.korisnik = korisnik;
        this.vrijemeKreiranja = vrijemeKreiranja;
        this.vrijemeDoKadaVrijedi = vrijemeDoKadaVrijedi;
        this.brojZahtjeva=brojZahtjeva;
        this.aktivna = aktivna;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public long getVrijemeKreiranja() {
        return vrijemeKreiranja;
    }

    public void setVrijemeKreiranja(long vrijemeKreiranja) {
        this.vrijemeKreiranja = vrijemeKreiranja;
    }

    public long getVrijemeDoKadaVrijedi() {
        return vrijemeDoKadaVrijedi;
    }

    public void setVrijemeDoKadaVrijedi(long vrijemeDoKadaVrijedi) {
        this.vrijemeDoKadaVrijedi = vrijemeDoKadaVrijedi;
    }

    public boolean getStatus() {
        return aktivna;
    }

    public void setStatus(boolean aktivna) {
        this.aktivna = aktivna;
    }

    public int getBrojZahtjeva() {
        return brojZahtjeva;
    }

    public void setBrojZahtjeva(int brojZahtjeva) {
        this.brojZahtjeva = brojZahtjeva;
    }

    public boolean isAktivna() {
        return aktivna;
    }

    public void setAktivna(boolean aktivna) {
        this.aktivna = aktivna;
    }

}
