/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.podaci;

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
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author NWTiS_2
 */
public class AirplanesDAO {

    /**
     * Metoda služi za provjeru postojanja preuzetog aerodroma za određeni dan
     *
     * @param aerodrom - icao traženog aerodroma
     * @param preuzimanjeOd - datum za koji se provjerava zapis
     * @param pbp
     * @return vraća true ako postoji preuzimanje za traženi datum i icao, inače
     * false
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
                    Date datumPolaska = (Date) new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("FLIGHTDATE"));
                    if (icao.equals(aerodrom) && datumPolaska.equals(new Date(preuzimanjeOd.getTime()))) {
                        return true;
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<AvionLeti> dohvatiSveLetoveZaIcao(String aerodrom, long poc, long kraj, PostavkeBazaPodataka pbp) throws ParseException {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        List<AvionLeti> al = new ArrayList<>();
        String upit = "SELECT * FROM AIRPLANES WHERE estDepartureAirport = '" + aerodrom + "' AND (firstSeen * 1000) between " + poc + " AND " + kraj;

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    
                    String icao24=rs.getString("icao24");
                    int firstSeen=rs.getInt("firstSeen");
                    String estDepartureAirport=rs.getString("estDepartureAirport");
                    int lastSeen=rs.getInt("lastSeen");
                    String estArrivalAirport=rs.getString("estArrivalAirport");
                    String callsign=rs.getString("callsign");
                    int estDepartureAirportHorizDistance=rs.getInt("estDepartureAirportHorizDistance");
                    int estDepartureAirportVertDistance=rs.getInt("estDepartureAirportVertDistance");
                    int estArrivalAirportHorizDistance=rs.getInt("estArrivalAirportHorizDistance");
                    int estArrivalAirportVertDistance=rs.getInt("estArrivalAirportVertDistance");
                    int departureAirportCandidatesCount=rs.getInt("departureAirportCandidatesCount");
                    int arrivalAirportCandidatesCount=rs.getInt("arrivalAirportCandidatesCount");
                    AvionLeti avion=new AvionLeti(icao24, firstSeen, estDepartureAirport, lastSeen, estArrivalAirport, callsign, estDepartureAirportHorizDistance, estDepartureAirportVertDistance, estArrivalAirportHorizDistance, estArrivalAirportVertDistance, departureAirportCandidatesCount, arrivalAirportCandidatesCount);
                    al.add(avion);
                }
                return al;

            } catch (SQLException ex) {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Metoda vraća broj letova za traženi icao
     *
     * @param icao
     * @param pbp
     * @return broj letova za traženi icao
     */
    public int dohvatiLetoveZaIcao(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT count(*) as 'brojLetova' FROM AIRPLANES where estdepartureairport = '" + icao + "' or "
                + "estarrivalairport = '" + icao + "'";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                int brojac = 0;
                while (rs.next()) {
                    brojac = rs.getInt(1);

                }
                return brojac;

            } catch (SQLException ex) {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

   

    /**
     * Metoda služi za dodavanje aviona u bazu podataka
     *
     * @param al objekt avionleti
     * @param pbp
     * @return true ukoliko je uspješno, inače false
     */
    public boolean dodajAvion(AvionLeti al, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO AIRPLANES (ICAO24, FIRSTSEEN, ESTDEPARTUREAIRPORT, LASTSEEN, ESTARRIVALAIRPORT,"
                + " CALLSIGN, ESTDEPARTUREAIRPORTHORIZDISTANCE, ESTDEPARTUREAIRPORTVERTDISTANCE, ESTARRIVALAIRPORTHORIZDISTANCE,"
                + " ESTARRIVALAIRPORTVERTDISTANCE, DEPARTUREAIRPORTCANDIDATESCOUNT, ARRIVALAIRPORTCANDIDATESCOUNT, `stored`) "
                + "VALUES ('" + al.getIcao24() + "', " + al.getFirstSeen() + ", '" + al.getEstDepartureAirport() + "',"
                + " " + al.getLastSeen() + ", '" + al.getEstArrivalAirport() + "', '" + al.getCallsign() + "', "
                + "" + al.getEstDepartureAirportHorizDistance() + ", " + al.getEstDepartureAirportVertDistance() + ", "
                + "" + al.getEstArrivalAirportHorizDistance() + ", " + al.getEstArrivalAirportVertDistance() + ", "
                + "" + al.getDepartureAirportCandidatesCount() + ", " + al.getArrivalAirportCandidatesCount() + ", CURRENT_TIMESTAMP )";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {

                int brojAzuriranja = s.executeUpdate(upit);

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
