/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci;

import java.sql.Connection;
import java.sql.Date;
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
public class OvlastiDAO {

    
    
    
    public Boolean provjeriPostojanostPodrucja(String korisnik, String podrucje, PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        Korisnik k=null;
        String upit = "SELECT * FROM ovlasti where korisnicko_ime = '"+korisnik+"' AND"
                + " podrucje_rada='"+podrucje+"'";
   

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    return true;
                }
                con.close();               
                

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
        
    }
    
     public Boolean postojiPodrucje(String podrucje, PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        Korisnik k=null;
        String upit = "SELECT * FROM ovlasti where "
                + " podrucje_rada='"+podrucje+"'";
  

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    return true;
                }
                
                con.close();  
                return false;
                

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
        
    }
    
  
    
   public boolean dodajPodrucje(String korisnik,String podrucje, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO ovlasti "
                + "VALUES ('"+korisnik+"', '"+podrucje+"', true)";
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {              
                int brojAzuriranja = s.executeUpdate(upit);
                con.close();
                return brojAzuriranja == 1;               

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
   
   
   public boolean imaAktivnoPodrucje(String korisnik,String podrucje, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        boolean status=false;
        Korisnik k=null;
        String upit = "SELECT * FROM ovlasti where korisnicko_ime = '"+korisnik+"' AND"
                + " podrucje_rada='"+podrucje+"'";
      

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    status=rs.getBoolean("status");
                }                
                con.close();               
                if(status) return true;
                return false;

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
   
   public boolean provjeriKorisnikuPodrucje(String korisnik,String podrucje, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        boolean status=false;
        Korisnik k=null;
        String upit = "SELECT * FROM ovlasti where korisnicko_ime = '"+korisnik+"' AND"
                + " podrucje_rada='"+podrucje+"' AND status = true";
  

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    status=true;
                }                
                con.close();               
                if(status) return true;
                return false;

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
   
   
    public int brojPodrucja(String korisnik, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        boolean status=false;
        String upit = "SELECT * FROM ovlasti where korisnicko_ime = '"+korisnik+"' AND"
                + " status = true";
     
        int brojac=0;
        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                     brojac++;
                }
                
                con.close();               
                return brojac;

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
     public String dohvatiAktivnaPodrucja(String korisnik, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        boolean status=false;
        Korisnik k=null;
        String upit = "SELECT * FROM ovlasti where korisnicko_ime = '"+korisnik+"' AND"
                + " status = true";
        String podrucja="";
        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    podrucja+=rs.getString("podrucje_rada")+" ";
                }
                
                con.close();               
                return podrucja;

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

   

        /**
     * Metoda služi za ažuriranje zapisa o korisniku
     * @param k objekt korisnika kojeg treba ažurirati
     * @param lozinka
     * @param pbp
     * @return ture ako je uspješno, inače false
     */
    public boolean aktivirajPodrucje(String korisnik,String podrucje, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "UPDATE ovlasti SET status = true WHERE korisnicko_ime = '"+korisnik+"' "
                + "AND podrucje_rada = '"+podrucje+"'";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {


                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean deaktivirajPodrucje(String korisnik, String podrucje, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "UPDATE ovlasti SET status = false WHERE korisnicko_ime = '"+korisnik+"' "
                + "AND podrucje_rada = '"+podrucje+"'";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {


                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

  
    

}
