package org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

public class DnevnikDAO {
    
     public boolean unesiZapis(String korisnik, String zahtjev, String odgovor, Timestamp vrijeme,
              PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "insert into DNEVNIK (korisnik, zahtjev, odgovor, vrijemeZahtjeva) values "
                + "('"+korisnik+"','"+zahtjev+"', '"+odgovor+"', '"+
                vrijeme+"')";

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
     
    public List<Dnevnik> dohvatiZapise(String korisnik, String vrijemeOd, String vrijemeDo, String pomak, String stranica, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit="";
        if(vrijemeOd.equals("") && pomak.equals("") && vrijemeDo.equals("") && stranica.equals("")) 
            upit="SELECT * FROM DNEVNIK where korisnik = '"+korisnik+"'";
        else if(!vrijemeOd.equals("") && !vrijemeDo.equals("") && pomak.equals("") && stranica.equals(""))
            upit="SELECT * FROM DNEVNIK where korisnik = '"+korisnik+"' AND vrijemeZahtjeva  BETWEEN FROM_UNIXTIME("+vrijemeOd+"/1000) AND FROM_UNIXTIME("+vrijemeDo+"/1000)";
        else if(vrijemeOd.equals("") && vrijemeDo.equals("") && !pomak.equals("") && !stranica.equals(""))
            upit="SELECT * FROM DNEVNIK where korisnik = '"+korisnik+"' order by vrijemeZahtjeva limit "+pomak
                    +" , "+stranica;
        else if(!vrijemeOd.equals("") && !vrijemeDo.equals("") && !pomak.equals("") && !stranica.equals(""))
        upit="SELECT * FROM DNEVNIK where korisnik = '"+korisnik+"' AND vrijemeZahtjeva  "
                + "BETWEEN FROM_UNIXTIME("+vrijemeOd+"/1000) AND FROM_UNIXTIME("+vrijemeDo+"/1000) order by vrijemeZahtjeva limit "+pomak
                    +" , "+stranica;
        
            try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Dnevnik> zapisi = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String zahtjev = rs.getString("zahtjev");
                    String odgovor = rs.getString("odgovor");
                    Timestamp vrijemeZahtjeva = rs.getTimestamp("vrijemeZahtjeva");
                    Dnevnik d = new Dnevnik(id, korisnik, zahtjev, odgovor, vrijemeZahtjeva);
                    zapisi.add(d);
                    
                }
                return zapisi;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
     public int dohvatiBrojZapisa(String korisnik, String vrijemeOd, String vrijemeDo, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit="";
        if(vrijemeOd.equals("") && vrijemeDo.equals("") ) 
            upit="SELECT count(*) as broj FROM DNEVNIK where korisnik = '"+korisnik+"'";
        else
            upit="SELECT count(*) as broj FROM DNEVNIK where korisnik = '"+korisnik+"' AND vrijemeZahtjeva  BETWEEN FROM_UNIXTIME("+vrijemeOd+"/1000) AND FROM_UNIXTIME("+vrijemeDo+"/1000)";
        
            try {
            Class.forName(pbp.getDriverDatabase(url));

            int broj = 0;

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    broj = rs.getInt("broj");
                    
                    
                }
                return broj;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    
    
}
