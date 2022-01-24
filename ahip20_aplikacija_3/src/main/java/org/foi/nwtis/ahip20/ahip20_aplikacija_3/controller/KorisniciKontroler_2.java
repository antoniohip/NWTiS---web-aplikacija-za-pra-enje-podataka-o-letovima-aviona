package org.foi.nwtis.ahip20.ahip20_aplikacija_3.controller;

import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.KorisniciKlijent_1;
import org.foi.nwtis.ahip20.ahip20_aplikacija_3.podaci.Korisnik;


@Path("registracijaKorisnika")
@Controller

public class KorisniciKontroler_2 {

    @FormParam("korisnik")
    String korisnik;
    @FormParam("lozinka")
    String lozinka;
    @FormParam("ime")
    String ime;
    @FormParam("prezime")
    String prezime;
    
    @Inject
    private Models model;

    /**
     * Metoda služi za registraciju korisnika. Podaci o korisniku dobivaju se iz forme
     * te se kreira objekt korisnik koji se šalje na rest servis. Ukoliko je odgovor servisa
     * "OK", ispisuje se poruka o uspješnom dodavanju korisnika te se preusmjerava na prijavu, a 
     * ako odgovor nije OK ispsiuje se greška te se ponovo učitava stranica registracije... 
     * @return 
     */
    @POST
    public String registracijaKorisnika() {
  
        Korisnik k = new Korisnik(korisnik, lozinka, prezime, ime);
        KorisniciKlijent_1 kk=new KorisniciKlijent_1();
        Response res = kk.dodajKorisnika(k, Response.class);        
        String odg = res.readEntity(String.class);
        System.out.println(odg);
        if(!odg.startsWith("OK")){
            model.put("greska", odg);
            return "korisnikUnos.jsp";
        }        
        else {
            model.put("registracijaUspjeh", "Uspješna registracija. Možete se prijaviti...");
            return "korisnikPrijava.jsp";
        }
 
    }

}
