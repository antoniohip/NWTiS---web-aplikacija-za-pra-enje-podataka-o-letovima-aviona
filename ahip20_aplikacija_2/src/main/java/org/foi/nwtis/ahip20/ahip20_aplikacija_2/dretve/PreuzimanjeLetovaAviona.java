
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.dretve;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.AirplanesDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.AirportDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.MyAirportDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.MyAirportsLogDAO;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

public class PreuzimanjeLetovaAviona extends Thread {

    private PostavkeBazaPodataka pbp;
    private OSKlijent osk;
    private String lozinka;
    private String korIme;
    private String pocetakPreuzimanja;
    private String krajPreuzimanja;
    private int trajanjeCiklusa;
    private int trajanjePauze;
    private boolean preuzimanjeStatus;
    private boolean kraj = false;

    public PreuzimanjeLetovaAviona(PostavkeBazaPodataka pbp) {
        this.pbp = pbp;
    }
        
        
    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt();
    }
    /**
     * Metoda koja služi za dohvaćanje aerodroma. 
     * Nakon kreiranja instanci DAO objekata i sređivanja datuma u željenom formatu
     * u while petlji preuzimaju se podaci o aerodromima. 
     * Na početku se dodaje dan na datum početka preuzimanja kako bi se preuzimanje obavilo
     * za jedan dan. Zatim se provjeravaju uvjeti preuzimanja. Ako je datum kraja unutar
     * raspona konfiguracije i ako je status preuzimanja TRUE, dohvaćaju se aerodromi iz 
     * tablice koje prate korisnici. Ako postoje aerodromi, dodaju se u listu te se za svaki 
     * aerodrom provjerava ima li zapis u tablici myairportslog. Ukoliko nema, preuzimaju 
     * se podaci sa servera te se upisuju u bazu. Zatim se preuzimaju avioni na tom letu
     * i spremaju se u bazu. Nakon toga slijedi pauza zadana u konfiguraciji, te se dodaje 
     * dan. 
     */
    @Override
    public void run() {
        System.out.println("Krenuli preuzimati podatke");
        int brojac=0;
        long vrijemeCekanja=0;
        Timestamp datumKraj=null;
        Timestamp preuzimanjeOd=null;
        Timestamp preuzimanjeDo=null;
        List<String> aerodromi = new ArrayList<>();
        AirportDAO airportDAO=new AirportDAO();
        AirplanesDAO airplanesDAO= new AirplanesDAO();
        
        MyAirportsLogDAO myAirportsLogDAO=new MyAirportsLogDAO();
        MyAirportDAO myAirportDAO=new MyAirportDAO();
        Date datum=null;
        SimpleDateFormat sdf= new SimpleDateFormat("dd.MM.yyyy");
        List<AvionLeti> avioni = new ArrayList<>();
        try{
           datum=(Date) sdf.parse(this.pocetakPreuzimanja); 
            System.out.println("Datum je "+this.pocetakPreuzimanja);
           preuzimanjeOd=new Timestamp(datum.getTime());
           datum=(Date) sdf.parse(this.krajPreuzimanja);  
           datumKraj=new Timestamp(datum.getTime());
        }catch(ParseException ax){
            System.out.println("Greška datuma");            
        }
        while (!kraj){
            System.out.println("Preuzimanje podataka: ");
            try {
                this.preuzimanjeStatus=Boolean.parseBoolean(pbp.dajPostavku("preuzimanje.status"));
                long vrijemePocetka=System.currentTimeMillis();
                preuzimanjeDo=dodajDan(preuzimanjeOd);
                System.out.println("Preuz.od: "+preuzimanjeOd+", preuzimanje do: "+preuzimanjeDo);
                if(!datumKraj.before(preuzimanjeDo) && preuzimanjeStatus==true){
                    aerodromi = myAirportDAO.dohvatiIcaoAerodroma(pbp);
                    
                    if(aerodromi!=null){
                        for(String aerodrom:aerodromi){
                            try {
                                if(!myAirportsLogDAO.provjeriLogAerodroma(aerodrom, preuzimanjeOd, pbp)){
                                   avioni=null;
                                   avioni=this.osk.getDepartures(aerodrom, preuzimanjeOd, preuzimanjeDo);
                                   if(avioni!=null && avioni.size()>0){
                                       if(myAirportsLogDAO.dodajAerodrom(aerodrom, preuzimanjeOd, pbp)){
                                           for(AvionLeti al:avioni){
                                               airplanesDAO.dodajAvion(al, pbp);
         
                                           }
                                           synchronized(this){
                                               this.wait(trajanjePauze);
                                           }
                                       }
                                   }
                                }
                            } catch (ParseException ex) {
                                Logger.getLogger(PreuzimanjeLetovaAviona.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    preuzimanjeOd=dodajDan(preuzimanjeOd);
                    long zavrsetak = System.currentTimeMillis();
                    vrijemeCekanja=(trajanjeCiklusa*1000) - (zavrsetak-vrijemePocetka);
                    synchronized(this){
                        System.out.println("Spavam jedan ciklus!");
                        this.wait(vrijemeCekanja);
                    }
                    
                }else {
                    synchronized(this){
                        System.out.println("Cekam jedan dan! ");
                        this.wait(86400000);
                    }
                }
                              
                
                
            } catch (InterruptedException ex) {
                Logger.getLogger(PreuzimanjeLetovaAviona.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Preuzimanje podataka zavrsilo!");
    }

    @Override
    public synchronized void start() {
        boolean status = Boolean.parseBoolean(pbp.dajPostavku("preuzimanje.status"));
        if(!status){
            System.out.println("Ne preuzimam podatke!");
            return;
        }
        this.pocetakPreuzimanja = pbp.dajPostavku("preuzimanje.pocetak");
        this.krajPreuzimanja = pbp.dajPostavku("preuzimanje.kraj");
        this.trajanjeCiklusa = Integer.parseInt(pbp.dajPostavku("preuzimanje.ciklus"));
        this.korIme = pbp.dajPostavku("OpenSkyNetwork.korisnik");
        this.lozinka = pbp.dajPostavku("OpenSkyNetwork.lozinka");
        this.trajanjePauze=Integer.parseInt(pbp.dajPostavku("preuzimanje.pauza"));
        this.osk = new OSKlijent(this.korIme, this.lozinka);
        this.preuzimanjeStatus=Boolean.parseBoolean(pbp.dajPostavku("preuzimanje.status"));
        super.start();
    }

    /**
     * Metoda služi za dodavanje jednog dana na datum
     * @param datum na koji je potrebno dodati dan
     * @return datum uvećan za jedan dan
     */
    private Timestamp dodajDan(Timestamp preuzimanjeOd) {
         Timestamp t;
         Calendar c = Calendar.getInstance();
         c.setTime(preuzimanjeOd);
         c.add(Calendar.DAY_OF_WEEK, 1);
         t=new Timestamp(c.getTime().getTime());
         return t;
         
    }
    
}
