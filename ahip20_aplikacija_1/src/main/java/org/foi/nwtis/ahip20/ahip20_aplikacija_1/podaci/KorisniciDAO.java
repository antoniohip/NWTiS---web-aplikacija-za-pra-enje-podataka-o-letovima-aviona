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
public class KorisniciDAO {

     /**
     * Metoda služi za provjeru postojanja preuzetog aerodroma za određeni dan
     * @param aerodrom - icao traženog aerodroma
     * @param preuzimanjeOd - datum za koji se provjerava zapis
     * @param pbp 
     * @return vraća true ako postoji preuzimanje za traženi datum i icao, inače false
     * @throws ParseException 
     */
    public String dajKorisnike(PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT * FROM korisnici";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                String svi="";

                while (rs.next()) {
                    String korime = rs.getString("korisnicko_ime");
                    System.out.println("Korisnicko ime je: " +korime);
                    svi+=korime+" ";
                }
                con.close();
                return svi;
                

            } catch (SQLException ex) {
                Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    
    public Korisnik provjeriPostojanostKorisnika(String korisnik, PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        Korisnik k=null;
        String upit = "SELECT * FROM korisnici where korisnicko_ime = '"+korisnik+"'";
 

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    String korime = rs.getString("korisnicko_ime");
                    String lozinka = rs.getString("lozinka");
                    String prezime = rs.getString("prezime");
                    String ime = rs.getString("ime");
                    k = new Korisnik(korime, lozinka, prezime, ime);
                }
                con.close();
                return k;
                

            } catch (SQLException ex) {
                Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
    
    public Korisnik provjeriKorisnika(String korisnik,String lozinka, PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        Korisnik k=null;
        String upit = "SELECT * FROM korisnici where korisnicko_ime = '"+korisnik+"'"
                + "AND lozinka = '"+lozinka+"'";
 

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    String korime = rs.getString("korisnicko_ime");
                    String loz = rs.getString("lozinka");
                    String prezime = rs.getString("prezime");
                    String ime = rs.getString("ime");
                    k = new Korisnik(korime, loz, prezime, ime);
                }
                con.close();
                return k;
                

            } catch (SQLException ex) {
                Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
    
     public String dajPodatkeKorisnika(String korisnik, PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        Korisnik k=null;
        String upit = "SELECT * FROM korisnici where korisnicko_ime = '"+korisnik+"'";
               
 

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    String korime = rs.getString("korisnicko_ime");
                    String prezime = rs.getString("prezime");
                    String ime = rs.getString("ime");
                    String podatak=korime+"\t"+prezime+"\t"+ime;
                    return podatak;
                }
                con.close();
                return "";
                

            } catch (SQLException ex) {
                Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
     
     
      public String dajSvePodatkeKorisnika(PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String podatak = "";
        String upit = "SELECT * FROM korisnici";
               


        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                

                while (rs.next()) {
                    String korime = rs.getString("korisnicko_ime");
                    String prezime = rs.getString("prezime");
                    String ime = rs.getString("ime");
                    podatak+="\""+korime+"\t"+prezime+"\t"+ime+"\"";
                    podatak+= " ";
         
                }
                con.close();
                return podatak;
                
               
                

            } catch (SQLException ex) {
                Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
    
   public boolean dodajKorisnika(Korisnik k, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO korisnici "
                + "VALUES ('"+k.getKorisnik()+"', '"+k.getLozinka()+"', '"+k.getPrezime()+"', '"+k.getIme()+"')";
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {              
                int brojAzuriranja = s.executeUpdate(upit);
                con.close();
                return brojAzuriranja == 1;               

            } catch (SQLException ex) {
                Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

   

        /**
     * Metoda služi za ažuriranje zapisa o korisniku
     * @param k objekt korisnika kojeg treba ažurirati
     * @param lozinka
     * @param pbp
     * @return ture ako je uspješno, inače false
     */
    public boolean azurirajKorisnika(Korisnik k, String lozinka, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "UPDATE korisnici SET ime = ?, prezime = ?, emailAdresa = ?, lozinka = ?, "
                + "vrijemePromjene = CURRENT_TIMESTAMP WHERE korisnik = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, k.getIme());
                s.setString(2, k.getPrezime());
                s.setString(4, lozinka);
                s.setString(5, k.getKorisnik());

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Metoda služi za dodavanje aviona u bazu podataka
     * @param al objekt avionleti
     * @param pbp
     * @return true ukoliko je uspješno, inače false
     */
    

}
