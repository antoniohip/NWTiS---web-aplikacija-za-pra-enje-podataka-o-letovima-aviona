package org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci;

import java.util.ArrayList;
import java.util.List;

public class SpremistePodataka {

    private static SpremistePodataka instance = null;

    public List<Sjednica> sjednice;

    public SpremistePodataka() {
        sjednice = new ArrayList<>();
    }

    public static SpremistePodataka getInstance() {
        if (instance == null) {
            instance = new SpremistePodataka();
        }
        return instance;
    }

    public List<Sjednica> getSjednice() {
        return sjednice;
    }

    public void setSjednice(List<Sjednica> sjednice) {
        this.sjednice = sjednice;
    }

    public Sjednica dohvatiSjednicuPoKorisniku(String korisnik) {

        for (Sjednica s : sjednice) {
            if (s.getKorisnik().equals(korisnik)) {
                return s;
            }
        }
        return null;
    }

    public void osvjeziSjednicu(int id, long doKadaVrijedi) {
        for (Sjednica s : sjednice) {
            if (s.getId() == id) {
                s.setVrijemeDoKadaVrijedi(doKadaVrijedi);
            }
        }
    }

}
