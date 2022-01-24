/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author NWTiS_2
 */
public class AirportDAO {

    /**
     * Metoda za dohvaćanje aerodroma za traženi icao
     * @param icao
     * @param pbp
     * @return objekt aerodroma ukoliko postoji, inače null
     */
    public Aerodrom dohvatiAerodrom(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ident, name, iso_country, coordinates FROM AIRPORTS WHERE ident = '"+icao+"'";

       
        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String name = rs.getString("name");
                    String iso_country = rs.getString("iso_country");
                    String []el=rs.getString("coordinates").split(", ");
                    
                    Aerodrom a = new Aerodrom(ident, name, iso_country, new Lokacija(el[0],el[1]));
                    return a;

                }
                

             

            } catch (SQLException ex) {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Metoda za dohvaćanje svih aerodroma uz mogućnost sortiranja po nazivu ili državi
     * @param naziv niz znakova za filtriranje po nazivu
     * @param drzava niz znakova za filtriranje po državi
     * @param pbp
     * @return lista aerodroma
     */
    public List<Aerodrom> dohvatiSveAerodrome(String naziv, String drzava, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "";
        if (naziv.equals("") && drzava.equals("")) {
            upit = "SELECT ident, name, iso_country, coordinates FROM AIRPORTS";
        } else if (naziv.length() > 0 && drzava.equals("")) {
            upit = "SELECT ident, name, iso_country, coordinates FROM AIRPORTS WHERE name LIKE '%"+naziv+"%'";
        } else if (naziv.equals("") && drzava.length() > 0) {
            upit = "SELECT ident, name, iso_country, coordinates FROM AIRPORTS WHERE iso_country LIKE '%"+drzava+"%'";
        } else if (naziv.length() > 0 && drzava.length() > 0) {
            upit = "SELECT ident, name, iso_country, coordinates FROM AIRPORTS WHERE iso_country LIKE '%"+drzava+"%' AND name LIKE '%"+naziv+"%'";
        }

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Aerodrom> aerodromi = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String name = rs.getString("name");
                    String iso_country = rs.getString("iso_country");
                    String []el=rs.getString("coordinates").split(", ");                    
                    Aerodrom a = new Aerodrom(ident, name, iso_country, new Lokacija(el[0],el[1]));                   
                    aerodromi.add(a);
                }
                return aerodromi;
            } catch (SQLException ex) {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
  

}
