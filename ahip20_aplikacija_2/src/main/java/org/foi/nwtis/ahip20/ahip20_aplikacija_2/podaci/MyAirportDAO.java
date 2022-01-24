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
public class MyAirportDAO {

    

    /**
     * Metoda služi za dohvaćanje jedinstvenih aerodroma iz tablice myairports
     * @param pbp
     * @return vraća listu aerodroma
     */
    public List<Aerodrom> dohvatiSveAerodrome(PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "";
        upit = "SELECT DISTINCT ident FROM MYAIRPORTS";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Aerodrom> aerodromi = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String icao = rs.getString("ident");
                    String upit2 = "SELECT ident, name, iso_country, coordinates FROM AIRPORTS WHERE ident = '" + icao + "'";
                    try (Connection con1 = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                            Statement s1 = con1.createStatement();
                            ResultSet rs2 = s1.executeQuery(upit2)) {

                        while (rs2.next()) {
                            String ident = icao;
                            String name = rs2.getString("name");
                            String iso_country = rs2.getString("iso_country");
                            String[] el = rs2.getString("coordinates").split(", ");

                            Aerodrom a = new Aerodrom(ident, name, iso_country, new Lokacija(el[0], el[1]));

                            aerodromi.add(a);
                        }

                    }

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
    
    public boolean provjeriPostojiLiZapis(String korisnik, String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "";
        boolean postoji=false;
        upit = "SELECT * FROM MYAIRPORTS WHERE username = '"+korisnik+"' AND ident = '"+icao+"'";
        System.out.println("Upit: "+upit);
        try {
            Class.forName(pbp.getDriverDatabase(url));

            

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    postoji = true;
                    }

                }
               
            } catch (SQLException ex) {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
      return postoji;
        
    }

    /**
     * metoda služi za dohvaćanje jedinstvenih icao-a iz tablice myairports
     * @param pbp
     * @return vraća listu aerodroma s jedinstvenim icao-ima
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
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Metoda služi da u tablici izbriše zapis o traženom korisniku i aerodromu
     * @param korIme korisničko ime za koji se briše aerodrom
     * @param icao aerodrom koji se briše (ICAO)
     * @param pbp 
     * @return true ukoliko je uspješna transkacija
     */
    public boolean izbrisiKorisnikuIcao(String korIme, String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "DELETE from MYAIRPORTS where username = '"+korIme+"' and ident = '"+icao+"'";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

   

    /**
     * metoda služi za dohvaćanje liste korisnika koji prate traženi aerodrom.
     * @param icao 
     * @param pbp
     * @return lista korisnika koji prate traženi aerodrom
     */
    public String dohvatiKorisnikeZaIcao(String icao, PostavkeBazaPodataka pbp){
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "";
        String korisniciPopis="";
        upit = "SELECT username FROM MYAIRPORTS where ident = '" + icao + "'";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    korisniciPopis+=rs.getString("username")+";";                                        
                    }

                }
                return korisniciPopis;

            } catch (SQLException ex) {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;        
    }

    /**
     * metoda služi za dohvaćanje aerodroma koje prati traženi korisnik
     * @param korIme korisničko ime za koje se traže aerodromi koje on prati
     * @param pbp
     * @return lista aerodroma koje prati korisnik
     */
    public List<Aerodrom> dohvatiAerodromeZaKorisnike(String korIme, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "";
        upit = "SELECT ident FROM MYAIRPORTS where username = '" + korIme + "'";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Aerodrom> aerodromi = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String icao = rs.getString("ident");
                    String upit2 = "SELECT ident, name, iso_country, coordinates FROM AIRPORTS WHERE ident = '" + icao + "'";
                    try (Connection con1 = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                            Statement s1 = con1.createStatement();
                            ResultSet rs2 = s1.executeQuery(upit2)) {

                        while (rs2.next()) {
                            String ident = icao;
                            String name = rs2.getString("name");
                            String iso_country = rs2.getString("iso_country");
                            String[] el = rs2.getString("coordinates").split(", ");

                            Aerodrom a = new Aerodrom(ident, name, iso_country, new Lokacija(el[0], el[1]));

                            aerodromi.add(a);
                        }

                    }

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
    
    /**
     * Metoda služi za unos zapisa o korisniku i aerodromu koji prati
     * @param korisnik
     * @param icao
     * @param pbp
     * @return ukoliko je uspješan upis true, inače false
     */
    public boolean unesiIcaoKorisniku(String korisnik, String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "insert into MYAIRPORTS(ident, username, `stored`) values ('"+icao+"', '"+korisnik+"', CURRENT_TIMESTAMP )";

         try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {

               

                int brojAzuriranja = s.executeUpdate(upit);

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    
}
