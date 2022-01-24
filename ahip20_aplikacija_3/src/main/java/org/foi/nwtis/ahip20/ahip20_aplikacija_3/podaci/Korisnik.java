/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author NWTiS_2
 */
public class Korisnik {

    @Getter
    @Setter
    String korisnik;

    @Getter
    @Setter
    String lozinka;
    
    @Getter
    @Setter
    String prezime;

    @Getter
    @Setter
    String ime;

    public Korisnik() {
    }

   

    public Korisnik(String korisnik, String prezime, String ime) {
        this.korisnik = korisnik;
        this.prezime = prezime;
        this.ime = ime;       
    }

    public Korisnik(String korisnik, String lozinka, String prezime, String ime) {
        this.korisnik = korisnik;
        this.lozinka = lozinka;
        this.prezime = prezime;
        this.ime = ime;
    }

}
