package org.foi.nwtis.ahip20.ahip20_aplikacija_2.dretve;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import java.util.List;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.MeteoDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci.MyAirportDAO;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.MeteoOriginal;

public class PreuzimanjeVremena extends Thread {

    private PostavkeBazaPodataka pbp;
    private boolean kraj = false;
    private long ciklus;
    @Inject
    ServletContext context;

    public PreuzimanjeVremena(PostavkeBazaPodataka pbp) {
        this.pbp = pbp;
    }
    
    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt();
    }

    @Override
    public void run() {
        System.out.println("Krecem preuzimati vrijeme");
        long pocetak;
        long trajanjePreuzimanja;
        long pauza;
        String owmKey = pbp.dajPostavku("OpenWeatherMap.apikey");
        MyAirportDAO madao = new MyAirportDAO();
        List<Aerodrom> mojiAerodromi = madao.dohvatiSveAerodrome(pbp);
        MeteoDAO medao = new MeteoDAO();
        OWMKlijent owm = new OWMKlijent(owmKey);
        MeteoOriginal met=null;
        while (!kraj) {
            System.out.println("Preuzimam vrijeme...");
            pocetak = System.currentTimeMillis();
            for (Aerodrom a : mojiAerodromi) {
                met = owm.getRealTimeWeatherOriginal(a.getLokacija().getLongitude(), a.getLokacija().getLatitude());
                float temp = met.getMainTemp();
                int vlaga = met.getMainHumidity();
                float tlak = met.getMainPressure();
                float vjetar = met.getWindSpeed();
                float vjetarSmjer = met.getWindDeg();

                boolean unos = medao.unesiVrijeme(a.getIcao(), temp, vlaga, tlak, vjetar, vjetarSmjer, pbp);

            }
            trajanjePreuzimanja = System.currentTimeMillis() - pocetak;
            pauza = ciklus - trajanjePreuzimanja;

            
                System.out.println("Preuzimanje vremena zavrseno...");
                synchronized (this) {
                    
                try {
                    this.wait(pauza);
                } catch (InterruptedException ex) {
                    
                }
                
            } 
        }

    }

  

    @Override
    public synchronized void start() {
        this.ciklus = Long.parseLong(pbp.dajPostavku("preuzimanje.vremena.ciklus"));

        super.start();
    }
}
