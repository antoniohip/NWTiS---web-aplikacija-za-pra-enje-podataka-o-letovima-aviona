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

public class MeteoDAO {

    public boolean unesiVrijeme(String ident, float temperatura, int vlaga,
            float tlak, float brzinaVjetra, float smjer_vjetra, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "insert into METEO (ident, spremljeno, temperatura, vlaga, "
                + "tlak, brzina_vjetra, smjer_vjetra) values "
                + "('" + ident + "', CURRENT_TIMESTAMP, '" + temperatura + "', '" + vlaga + "', '"
                + tlak + "', '" + brzinaVjetra + "', '" + smjer_vjetra + "')";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {

                int brojAzuriranja = s.executeUpdate(upit);
                con.close();
                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<Meteo> dohvatiVrijemeZaDan(String icao, long poc, long kraj, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT * FROM METEO where ident = '" + icao + "' and spremljeno BETWEEN FROM_UNIXTIME(" + poc + "/1000) AND FROM_UNIXTIME(" + kraj + "/1000)";
        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Meteo> meteo = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String ident = rs.getString("ident");
                    Timestamp spremljeno = rs.getTimestamp("spremljeno");
                    float temperatura = rs.getFloat("temperatura");
                    int vlaga = rs.getInt("vlaga");
                    float tlak = rs.getFloat("tlak");
                    float brzinaVjetra = rs.getFloat("brzina_vjetra");
                    float smjerVjetra = rs.getFloat("smjer_vjetra");
                    Meteo m = new Meteo(id, ident, spremljeno, temperatura, vlaga, tlak, brzinaVjetra, smjerVjetra);
                    meteo.add(m);
                }
                con.close();
                return meteo;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Meteo dohvatiOdredenoVrijeme(String icao, long poc, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT * FROM METEO where ident = '" + icao + "'"
                + " and spremljeno BETWEEN FROM_UNIXTIME(" + poc + "/1000) "
                + "AND current_timestamp order by spremljeno limit 1";
        try {
            Class.forName(pbp.getDriverDatabase(url));

            Meteo m = null;
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String ident = rs.getString("ident");
                    Timestamp spremljeno = rs.getTimestamp("spremljeno");
                    float temperatura = rs.getFloat("temperatura");
                    int vlaga = rs.getInt("vlaga");
                    float tlak = rs.getFloat("tlak");
                    float brzinaVjetra = rs.getFloat("brzina_vjetra");
                    float smjerVjetra = rs.getFloat("smjer_vjetra");
                    m = new Meteo(id, ident, spremljeno, temperatura, vlaga, tlak, brzinaVjetra, smjerVjetra);

                }
                con.close();
                return m;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
