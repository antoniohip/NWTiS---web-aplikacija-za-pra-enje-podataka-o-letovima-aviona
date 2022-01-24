package org.foi.nwtis.ahip20_modul_4_2.zrna;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.PopisKorisnikaPL;
import org.foi.nwtis.ahip20_modul_4_1.ejb.sb.podaci.Korisnik;

@Named(value = "popisKorisnika")
@ApplicationScoped
public class PopisKorisnika {

    @Inject
    HttpServletRequest request;
    @EJB
    PopisKorisnikaPL popisKorisnikaPL;
    @Inject
    ServletContext context;
    @Getter
    @Setter
    private String filterKorisnik;
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
    private List<Korisnik> filtrirani=new ArrayList<>();

    private String podrucje = "";

    @Getter
    @Setter
    private List<Korisnik> korisnici = null;

    public PopisKorisnika() {
    }

    public String getPodrucje() {
        HttpSession sesija = request.getSession();
        String kor = sesija.getAttribute("korisnickoIme").toString();
        String id = sesija.getAttribute("sesijaId").toString();
        String komanda = "AUTHOR " + kor + " " + id + " pregledKorisnik";
        String odg = popisKorisnikaPL.posaljiAuthor(komanda);
        dajKorisnike();
        return odg;
    }
    public List<Korisnik> getKorisnici() {        
        return this.korisnici;
    }
    
    public void dajKorisnike(){       
        this.korisnici=popisKorisnikaPL.dajSveKorisnike();
        this.filtrirani=popisKorisnikaPL.dajFiltrirane();
        
    }  

    public List<Korisnik> filtrirajAerodrome() {
        List<Korisnik> filter = new ArrayList<>();
        for(Korisnik k: this.korisnici){
            if(k.getKorisnik().contains(this.filterKorisnik)){
                filter.add(k);
            }             
        }
        this.filtrirani=filter;
        return this.filtrirani;
       
    }  

}
