/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author NWTiS_2
 */
public class MyAirportsLogDAO {

    /**
     * Metoda služi za provjeru postojanja preuzetog aerodroma za određeni dan
     * @param aerodrom - icao traženog aerodroma
     * @param preuzimanjeOd - datum za koji se provjerava zapis
     * @param pbp 
     * @return vraća true ako postoji preuzimanje za traženi datum i icao, inače false
     * @throws ParseException 
     */
    public boolean provjeriLogAerodroma(String aerodrom, Timestamp preuzimanjeOd, PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        java.sql.Date datum = new java.sql.Date(preuzimanjeOd.getTime());
        String upit = "SELECT * FROM MYAIRPORTSLOG WHERE IDENT = '" + aerodrom + "' AND FLIGHTDATE = '" + datum + "'";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String icao = rs.getString("IDENT");
                    Date datumPolaska=(Date)new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("FLIGHTDATE"));
                    if(icao.equals(aerodrom) && datumPolaska.equals(new Date(preuzimanjeOd.getTime()))){
                        return true;
                    }
                }
                

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Metoda služi za preuzimanje jedinstvenih icao oznaka iz tablice myairports
     * @param pbp
     * @return lista koja sadrži jedinstvene icao oznake iz tablice myairports
     */
    public List<String> dohvatiIcaoAerodroma(PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT DISTINCT IDENT FROM MYAIRPORTS";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<String> aerodromi = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String ident = rs.getString("IDENT");
                    aerodromi.add(ident);
                }
                return aerodromi;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

   /**
    * Metoda služi za dodavanje aerodroma u tablicu myairportslog
    * @param icao 
    * @param datum
    * @param pbp
    * @return 
    */
    public boolean dodajAerodrom(String icao, Timestamp datum, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO `MYAIRPORTSLOG` (`ident`, `flightDate`, `stored`) VALUES ('" + icao + "','" + new java.sql.Date(datum.getTime()) + "', CURRENT_TIMESTAMP )";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {
                    
                    
                

                int brojAzuriranja = s.executeUpdate(upit);

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
