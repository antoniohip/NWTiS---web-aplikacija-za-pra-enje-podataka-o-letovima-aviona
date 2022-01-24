package org.foi.nwtis.ahip20.vjezba_03.konfiguracije;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class KonfiguracijaXML extends KonfiguracijaApstraktna{

    public KonfiguracijaXML(String nazivDatoteke) {
        super(nazivDatoteke);
    }
    
    @Override
    public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {
        this.obrisiSvePostavke();
        
         if(nazivDatoteke == null || nazivDatoteke.length() == 0){
             throw new NeispravnaKonfiguracija("Neispravni naziv datoteke: '" + nazivDatoteke+"'");
         }
         File f = new File(nazivDatoteke);
         if(f.exists() && f.isFile()){
             try {
                 this.postavke.loadFromXML(new FileInputStream(f));
             } catch (IOException ex) {
                 throw new NeispravnaKonfiguracija("Datoteka pod nazivom: '" + nazivDatoteke+"' ne postoji ili nije datoteka");
             }
         } 
         else {
             throw new NeispravnaKonfiguracija("Neispravno");
         }
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
         if(datoteka == null || datoteka.length() == 0){
             throw new NeispravnaKonfiguracija("Neispravni naziv datoteke: '" + datoteka+"'");
         }
         File f = new File(datoteka);
         if(!f.exists() || (f.isFile() && f.exists())){
             try {
                 this.postavke.storeToXML(new FileOutputStream(f), "NWTiS 2021. Antonio Hip");
             } catch (IOException ex) {
                 throw new NeispravnaKonfiguracija("Problem kod spremanja datoteke konfiguracije: '" + datoteka+"'");
             }
         } 
         else {
             throw new NeispravnaKonfiguracija("Datoteka pod nazivom: '" + datoteka+"' ne postoji ili nije datoteka");
         }
    }
    
}
