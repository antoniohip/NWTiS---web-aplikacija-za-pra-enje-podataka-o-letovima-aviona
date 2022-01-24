package org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci;

import java.sql.Timestamp;



public class Meteo {
    private int id;
    private String ident;
    private Timestamp spremljeno;
    private float temperatura;
    private int vlaga;
    private float tlak;
    private float brzinaVjetra;
    private float smjerVjetra;

    public Meteo(int id, String ident, Timestamp spremljeno, float temperatura, int vlaga, float tlak, float brzinaVjetra, float smjerVjetra) {
        this.id = id;
        this.ident = ident;
        this.spremljeno = spremljeno;
        this.temperatura = temperatura;
        this.vlaga = vlaga;
        this.tlak = tlak;
        this.brzinaVjetra = brzinaVjetra;
        this.smjerVjetra = smjerVjetra;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public Timestamp getSpremljeno() {
        return spremljeno;
    }

    public void setSpremljeno(Timestamp spremljeno) {
        this.spremljeno = spremljeno;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }

    public int getVlaga() {
        return vlaga;
    }

    public void setVlaga(int vlaga) {
        this.vlaga = vlaga;
    }

    public float getTlak() {
        return tlak;
    }

    public void setTlak(float tlak) {
        this.tlak = tlak;
    }

    public float getBrzinaVjetra() {
        return brzinaVjetra;
    }

    public void setBrzinaVjetra(float brzinaVjetra) {
        this.brzinaVjetra = brzinaVjetra;
    }

    public float getSmjerVjetra() {
        return smjerVjetra;
    }

    public void setSmjerVjetra(float smjerVjetra) {
        this.smjerVjetra = smjerVjetra;
    }

   
    
    
}
